# Overview

Playground for Kotlin, Springboot, and Kubernetes

# Setup 

## Encrypted Database
This package demonstrates using JpaRepository, Entity, and AttributeConverter to encrypt and decrypt a column field when it is saved and retrieved from the database.

### Key Rotation via Master Key + Data Key
The approach of using a master key and encrypted data key is used, where the master key can be periodically rotated, and new encrypted data keys can be used.

https://vaibhav-sonavane.medium.com/rotate-keys-without-re-encrypting-data-ac6cb323d7cd

## Homebrew
Install Homebrew

### Minikube
Minikube is a mini version of kubernetes

Install VirtualBox before running these commands.
```shell
brew install kubectl
brew install minikube
```

#### Create Application
https://minikube.sigs.k8s.io/docs/start/

# Tests


Run
```./gradlew test```

# Running Locally

