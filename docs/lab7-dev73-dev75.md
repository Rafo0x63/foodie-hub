# Lab 7 - DEV-73 and DEV-75

This document describes the FoodieHub CI/CD setup for the Sprint 7 tasks:

- DEV-73: Trigger Jenkins pipeline automatically from GitHub webhook
- DEV-75: Trigger Render deploy automatically from Jenkins after a successful build

## Current Status

- Render Web Service is live:
  - `https://foodie-hub-gllq.onrender.com`
- Render health endpoint:
  - `https://foodie-hub-gllq.onrender.com/actuator/health`
- Jenkins job type:
  - Multibranch Pipeline
- Jenkins branch used for this lab:
  - `sprint-7`
- Jenkinsfile deploy stages are configured for:
  - `sprint-7`
- Jenkinsfile already uses the Render deploy hook through a Jenkins credential:
  - `render-deploy-hook`

## DEV-73 - GitHub Webhook Trigger

The goal is to start the Jenkins pipeline automatically after a push to GitHub.

### Jenkins Configuration

Use a Multibranch Pipeline job.

Recommended job name:

```text
foodie-hub
```

Repository:

```text
https://github.com/Rafo0x63/foodie-hub.git
```

Build configuration:

```text
Mode: by Jenkinsfile
Script Path: Jenkinsfile
```

The Jenkinsfile contains a GitHub push trigger and a polling fallback:

```groovy
triggers {
    githubPush()
    pollSCM('H/10 * * * *')
}
```

`githubPush()` is the actual webhook trigger. `pollSCM` is only a fallback if the webhook is temporarily unavailable.

### Jenkins Plugins

The Jenkins instance must have these plugins:

```text
Git
GitHub
GitHub Branch Source
Pipeline
Credentials Binding
AnsiColor
HTML Publisher
SonarQube Scanner
Workspace Cleanup
```

### Ngrok

If Jenkins runs locally on port `8081`, expose it with:

```powershell
ngrok http 8081
```

Ngrok will provide a public HTTPS URL such as:

```text
https://example-id.ngrok-free.app
```

### GitHub Webhook

In GitHub:

```text
Repository -> Settings -> Webhooks -> Add webhook
```

Use:

```text
Payload URL: https://<ngrok-url>/github-webhook/
Content type: application/json
Events: Just the push event
Active: checked
```

The Jenkins endpoint for GitHub webhooks is:

```text
/github-webhook/
```

### DEV-73 Proof

To prove that DEV-73 works:

1. Push a small commit to `sprint-7`.
2. Open GitHub webhook recent deliveries.
3. Confirm the delivery status is `200`.
4. Open Jenkins.
5. Confirm a build started automatically from the pushed commit.

Suggested screenshots:

- GitHub webhook configuration
- GitHub recent delivery with status `200`
- Jenkins build started after the push
- Jenkins build page showing the pushed commit hash

## DEV-75 - Automatic Render Deploy From Jenkins

The goal is to let Jenkins trigger a Render deployment after the pipeline succeeds.

### Render Configuration

The Render service is a Docker Web Service.

Service URL:

```text
https://foodie-hub-gllq.onrender.com
```

Health endpoint:

```text
https://foodie-hub-gllq.onrender.com/actuator/health
```

The project also includes `render.yaml`, which documents the intended Render setup:

```yaml
runtime: docker
branch: sprint-7
healthCheckPath: /actuator/health
dockerfilePath: ./Dockerfile
```

### Render Environment Variables

The service must have these environment variables:

```text
SPRING_PROFILES_ACTIVE=prod
JAVA_OPTS=-Xmx384m -XX:+UseSerialGC -Dserver.tomcat.threads.max=20
SPRING_DATASOURCE_URL=<jdbc postgres url>
SPRING_DATASOURCE_USERNAME=<postgres username>
SPRING_DATASOURCE_PASSWORD=<postgres password>
```

For the current Render setup, Flyway migrations are not used because the existing migration scripts are not aligned with the current JPA model.

Recommended Render overrides:

```text
SPRING_FLYWAY_ENABLED=false
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

For initial database creation on a fresh Render Postgres database, `create` can be used temporarily:

```text
SPRING_JPA_HIBERNATE_DDL_AUTO=create
```

After the first successful startup, switch it back to:

```text
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Jenkins Credential

Create a Jenkins credential for the Render deploy hook:

```text
Kind: Secret text
ID: render-deploy-hook
Secret: <Render Deploy Hook URL>
Description: Render deploy hook for FoodieHub
```

The deploy hook URL is copied from:

```text
Render Web Service -> Settings -> Deploy Hook
```

Do not commit the deploy hook URL to Git.

### Jenkinsfile Deploy Stage

The Jenkinsfile uses:

```groovy
RENDER_HOOK = credentials('render-deploy-hook')
STAGING_URL = 'https://foodie-hub-gllq.onrender.com'
```

Deploy stage:

```groovy
stage('Deploy to Render') {
    when { branch 'sprint-7' }
    steps {
        sh 'curl -fsSL -X POST "$RENDER_HOOK"'
        echo "Deploy pokrenut. Render ce preuzeti novi image."
    }
}
```

After deploy, Jenkins waits for Render and checks:

```text
https://foodie-hub-gllq.onrender.com/actuator/health
```

### DEV-75 Proof

To prove that DEV-75 works:

1. Jenkins build reaches the `Deploy to Render` stage.
2. The deploy stage calls the Render deploy hook successfully.
3. Render dashboard shows a new deploy event.
4. The service is live.
5. The health endpoint responds.

Suggested screenshots:

- Jenkins credential list showing `render-deploy-hook` ID
- Jenkins pipeline with `Deploy to Render`
- Jenkins console log for the deploy stage
- Render deploy history showing a new deployment
- Browser showing the public Render URL
- Browser or terminal showing `/actuator/health`

## Required Jenkins Credentials

The current Jenkinsfile expects these credentials:

```text
sonar-token
render-deploy-hook
ghcr-pat
```

Credential details:

```text
sonar-token
Kind: Secret text
Purpose: SonarQube analysis
```

```text
render-deploy-hook
Kind: Secret text
Purpose: Render deploy trigger
```

```text
ghcr-pat
Kind: Username with password
Purpose: GitHub Container Registry login
```

For `ghcr-pat`, the username must match the GitHub account that generated the PAT.

## Known Issue

At the time of this setup, the Jenkins pipeline can fail during Maven tests because tests use a database configuration that is not fully CI-container friendly.

In Docker Compose, MySQL is available as:

```text
db:3306
```

But local test configuration may use:

```text
localhost:3306
```

That issue blocks later stages, including deploy, until the test database configuration is aligned with Jenkins/Docker networking. This is separate from the Render deploy hook setup itself.

## Submission Checklist

DEV-73:

- GitHub webhook exists.
- Payload URL ends with `/github-webhook/`.
- Webhook uses push events.
- Webhook recent delivery returns `200`.
- Jenkins build starts after a GitHub push.

DEV-75:

- Render service is live.
- Render health endpoint works.
- Jenkins credential `render-deploy-hook` exists.
- Jenkinsfile contains the Render deploy stage.
- Jenkins deploy stage calls the Render deploy hook.
- Render dashboard shows a deployment triggered after Jenkins build.

