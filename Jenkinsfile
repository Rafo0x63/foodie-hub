// =============================================================
//  Jenkinsfile — kompletan CI/CD pipeline za Quotes API
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
    agent any

    tools {
        // Konfigurirano u Manage Jenkins → Tools
        jdk   'jdk-21'
        maven 'maven-3.9'
    }

    environment {
        APP_NAME        = 'quotes-api'
        DOCKER_REGISTRY = 'ghcr.io/imilos1@tvz.hr'   // <-- promijenite
        SONAR_HOST_URL  = 'http://host.docker.internal:9000'
        SONAR_TOKEN     = credentials('sonar-token')
        GHCR_PAT        = credentials('ghcr-pat')        // GitHub PAT s write:packages
        RENDER_HOOK     = credentials('render-deploy-hook')
        STAGING_URL     = 'https://quotes-api-staging.onrender.com'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '15'))
        timestamps()
        ansiColor('xterm')
    }

    triggers {
        // Backup ako webhook padne — pollaj svakih 10 minuta
        pollSCM('H/10 * * * *')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
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
                sh 'mvn -B -ntp clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn -B -ntp test'
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
                sh 'mvn -B -ntp verify -DskipUnitTests'
            }
            post {
                always {
                    junit testResults: 'target/failsafe-reports/*.xml',
                          allowEmptyResults: true
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
                        mvn -B -ntp sonar:sonar \
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
                sh 'mvn -B -ntp package -DskipTests'
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
                }
            }
            steps {
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
            when { branch 'main' }
            steps {
                sh 'curl -fsSL -X POST "$RENDER_HOOK"'
                echo "Deploy pokrenut. Render će preuzeti novi image."
            }
        }

        stage('Wait for Deploy') {
            when { branch 'main' }
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

        stage('E2E Smoke Tests (Selenium)') {
            when { branch 'main' }
            steps {
                sh """
                    mvn -B -ntp -Pselenium verify \
                        -DskipTests=false \
                        -DskipITs=true \
                        -Dapp.url=${STAGING_URL} \
                        -Dselenium.headless=true
                """
            }
            post {
                always {
                    junit testResults: 'target/failsafe-reports/*.xml',
                          allowEmptyResults: true
                    archiveArtifacts artifacts: 'target/screenshots/**',
                                     allowEmptyArchive: true
                }
            }
        }
    }

    post {
        success {
            echo "Build #${BUILD_NUMBER} (${GIT_SHA}) USPJEŠAN."
        }
        failure {
            echo "Build #${BUILD_NUMBER} (${GIT_SHA}) PAO. Provjeri: ${BUILD_URL}"
        }
        always {
            cleanWs(deleteDirs: true, notFailBuild: true)
        }
    }
}
