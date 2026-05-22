// =============================================================
//  Jenkinsfile - kompletan CI/CD pipeline za FoodieHub
//  Stage-ovi:
//    1. Checkout
//    2. Compile
//    3. Unit Test
//    4. Integration Test
//    5. JaCoCo report + Coverage Gate (80%)
//    6. SonarQube analiza + Quality Gate
//    7. Package
//    8. Docker build + push (samo na main)
//    9. Deploy na Render (samo na main)
//   10. Selenium E2E (smoke test deploya)
// =============================================================

pipeline {
    agent {
        docker {
            image 'maven:3.9-eclipse-temurin-21'
            // Mount host Docker socket so `docker build/push` works from inside the agent container.
            // --user root je potreban da bismo (po potrebi) instalirali docker CLI u Docker stage-u.
            args '-v /var/run/docker.sock:/var/run/docker.sock --user root'
            reuseNode true
        }
    }

    environment {
        APP_NAME        = 'foodie-hub'
        DOCKER_REGISTRY = 'ghcr.io/rafo0x63'
        SONAR_HOST_URL  = 'http://host.docker.internal:9000'
        SONAR_TOKEN     = credentials('sonar-token')
        GHCR_PAT        = credentials('ghcr-pat')        // GitHub PAT s write:packages
        RENDER_HOOK     = credentials('render-deploy-hook')
        // TODO: replace with real FoodieHub deploy URL before running deploy smoke tests
        STAGING_URL     = 'https://foodie-hub-gllq.onrender.com'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '15'))
        timestamps()
    }

    triggers {
        githubPush()
        // Backup ako webhook padne — pollaj svakih 10 minuta
        pollSCM('H/10 * * * *')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                sh 'chmod +x mvnw'
                script {
                    env.GIT_SHA = sh(returnStdout: true,
                            script: 'git rev-parse --short HEAD').trim()
                    env.IMAGE_TAG = "${DOCKER_REGISTRY}/${APP_NAME}:${GIT_SHA}"
                    echo "Build: ${APP_NAME} @ ${GIT_SHA}"
                }
            }
        }

        stage('Compile') {
            steps {
                sh './mvnw -B -ntp clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                sh './mvnw -B -ntp test'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml',
                          allowEmptyResults: false
                }
            }
        }

        stage('Integration Tests') {
            steps {
                sh './mvnw -B -ntp verify -DskipUnitTests'
            }
            post {
                always {
                    junit testResults: 'target/failsafe-reports/*.xml',
                          allowEmptyResults: true
                }
            }
        }

        stage('OWASP Dependency Check') {
            steps {
                sh './mvnw -B -ntp org.owasp:dependency-check-maven:check'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/dependency-check-report.*',
                                     allowEmptyArchive: true
                }
            }
        }

        stage('Coverage Report + Gate') {
            steps {
                sh 'ls -la target/site/jacoco-merged/ || true'
                publishHTML(target: [
                    reportDir : 'target/site/jacoco-merged',
                    reportFiles: 'index.html',
                    reportName : 'JaCoCo Coverage',
                    keepAll    : true,
                    alwaysLinkToLastBuild: true,
                    allowMissing: false
                ])
                // JaCoCo 'check' goal je već u Maven verify fazi — ako padne <80%, build pada
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube-local') {
                    sh """
                        ./mvnw -B -ntp sonar:sonar \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.token=${SONAR_TOKEN}
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                sh './mvnw -B -ntp package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true,
                                 onlyIfSuccessful: true
            }
        }

        stage('Docker Build & Push') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    branch 'sprint-7'
                }
            }
            steps {
                // Maven base image nema docker CLI — instaliraj ga jednokratno.
                sh '''
                    if ! command -v docker >/dev/null 2>&1; then
                        apt-get update -qq
                        apt-get install -y -qq --no-install-recommends docker.io
                    fi
                '''
                sh """
                    echo \$GHCR_PAT_PSW | docker login ghcr.io -u \$GHCR_PAT_USR --password-stdin
                    docker build -t ${IMAGE_TAG} -t ${DOCKER_REGISTRY}/${APP_NAME}:latest .
                    docker push ${IMAGE_TAG}
                    docker push ${DOCKER_REGISTRY}/${APP_NAME}:latest
                """
            }
            post {
                always {
                    sh 'docker logout ghcr.io || true'
                }
            }
        }

        stage('Deploy to Render') {
            when { branch 'sprint-7' }
            steps {
                sh 'curl -fsSL -X POST "$RENDER_HOOK"'
                echo "Deploy pokrenut. Render će preuzeti novi image."
            }
        }

        stage('Wait for Deploy') {
            when { branch 'sprint-7' }
            steps {
                script {
                    echo "Čekanje 90s da se Render redeploy završi..."
                    sleep time: 90, unit: 'SECONDS'
                    // Pingaj health endpoint dok ne odgovori
                    retry(10) {
                        sh "curl -fsSL ${STAGING_URL}/actuator/health"
                        sleep 6
                    }
                }
            }
        }

        stage('Selenium E2E Smoke against deploy') {
            when { branch 'sprint-7' }
            steps {
                sh """
                    ./mvnw -B -ntp test \
                        -Dtest=DeploySmokeE2ETest \
                        -Dapp.url=${STAGING_URL} \
                        -Dselenium.headless=true
                """
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml',
                          allowEmptyResults: true
                }
            }
        }
    }

    post {
        success {
            echo "Build #${BUILD_NUMBER} (${env.GIT_SHA ?: 'pre-checkout'}) USPJEŠAN."
        }
        failure {
            echo "Build #${BUILD_NUMBER} (${env.GIT_SHA ?: 'pre-checkout'}) PAO. Provjeri: ${BUILD_URL}"
        }
        always {
            // cleanWs treba (a) ws-cleanup plugin i (b) `node` context. Ako pipeline padne
            // prije nego agent uopće starta (npr. missing credential), nemamo ni jedno ni drugo.
            script {
                try {
                    node('built-in') {
                        cleanWs(deleteDirs: true, notFailBuild: true)
                    }
                } catch (ignored) {
                    echo "cleanWs preskočen (plugin ili node context nedostupan)."
                }
            }
        }
    }
}
