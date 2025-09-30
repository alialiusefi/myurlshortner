# Url Shortner App Pet Project (myurlshortner)

This repo consists of multiple applications:
- [Url Shortner App Pet Project (myurlshortner)](#url-shortner-app-pet-project-myurlshortner)
  - [BE](#be)
  - [Consumer](#consumer)
  - [FE](#fe)
  - [Postgres](#postgres)
  - [Kafka](#kafka)
  - [Schema Registry](#schema-registry)
  - [Api Gateway](#api-gateway)
- [Environments](#environments)
- [Setup](#setup)


## BE

This app handles user redirect requests and the management of short links.

- BuildTool: Gradle
- Language: Java
- Framework: Quarkus

## Consumer

This app handles user events that BE produces. 

- BuildTool: Gradle
- Language: Kotlin
- Framework: Spring

## FE

This is the FE of the application.

- Build tool: NPM
- Language: Typescript
- Framework: Next.JS

## Postgres

This is the database that is shared between backend applications.
- Instance: CloudNativePG
- Manifest: kubectl apply --server-side -f \
  https://raw.githubusercontent.com/cloudnative-pg/cloudnative-pg/release-1.27/releases/cnpg-1.27.0.yaml
## Kafka

This is the event platform used to handle events in current system. 

## Schema Registry

This is the schema registry that store the schemas of the events used in the project

## Api Gateway

This is the gateway / reverse proxy used for our implementation.
- Instance: [NGINX](https://gateway-api.sigs.k8s.io/implementations/#nginx-gateway-fabric)
- Manifest: 
  - kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.3.0/standard-install.yaml
	- kubectl kustomize "https://github.com/nginx/nginx-gateway-fabric/config/crd/gateway-api/standard?ref=v2.1.2" | kubectl apply -f -
	- kubectl apply -f https://raw.githubusercontent.com/nginx/nginx-gateway-fabric/v2.1.2/deploy/default/deploy.yaml


# Environments
- local
  - This is for local development.
  - Dependencies are in-memory or mocked.
- dev
  - This is for docker compose deployment.
  - Also can be used for local development.
  - Dependencies run as docker containers.
- prod
  - This is for kubernetes deployment.
  - Dependencies run as kubernetes objects, to mimic production as much as possible.

# Setup

Makefile is available to run and setup the environment needed.
