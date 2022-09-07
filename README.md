# Overview

Playground for Kotlin, Springboot, and Kubernetes

# Setup 

## Homebrew
Install Homebrew

### Minikube
Minikube is a mini version of kubernetes

Install VirtualBox before running these commands.
```shell
brew install kubectl
brew install minikube
```

### Create Application
https://minikube.sigs.k8s.io/docs/start/

# Tests

Tests standup the app, using an embedded mongo instance and a mock server for the redsky api.

Run
```./gradlew test```

# Running Locally
``` brew services start mongodb-community@5.0```

