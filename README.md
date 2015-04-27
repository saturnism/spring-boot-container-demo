<!--
  Copyright 2015 Google Inc. All Rights Reserved.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

Spring Boot Docker Example
==========================
This is an example Spring Boot "Hello World" micro-service application that can be containerized and subsequently executed in Docker, Kubernetes, and Google App Engine!

This is not an official Google product.

Building
========
Regular Maven build:

    mvn package
    
Build Docker container:

    mvn docker:build

Running
=======
Java
----
Pretty straight forward:

    mvn exec:java

Docker
------
Without Redis for session:

    docker run -ti spring-boot-demo

With Redis for session:

    docker run -d --name redis -p 6379:6379 redis
    docker run -ti --rm --link redis:redis -p 8080:8080 spring-boot-demo

Docker Compose
--------------
Docker Compose uses a descriptor to describe the Docker container and linking configurations.
Rather than manually executing commands like the previous section, you can start the demo with Redis by running:

    cd src/test/docker-compose
    docker-compose up
    
The configuration is stored in `docker-compose.yml`

Kubernetes
----------
The following instructions assume that you already have Kubernetes cluster up and running.  If not, check out [Google Container Engine](https://cloud.google.com/container-engine/) to get started quickly.

Change directory to where all the Kubernetes configuration files are:

    cd src/test/kubernetes

Deploy Redis master pod:

    kubectl create -f redis-master-pod.yaml
    
Create a Redis master service:

    kubectl create -f redis-master-service.yaml
    
Deploy Spring Boot Demo replication controller:

    kubectl create -f spring-boot-demo-controller.yaml
    
Create a service:

    kubectl create -f spring-boot-demo-service.yaml
    
If you are on Google Container Engine, or Kubernetes deployment that supports external services, you can create an external service:

    kubectl create -f spring-boot-demo-external-service.yaml

Google App Engine
-----------------
The instruction assumes that you have [Google Cloud SDK](https://cloud.google.com/sdk/) already installed and that you are familiar with [Google App Engine Managed VMs](https://cloud.google.com/appengine/docs/managed-vms/).

To run locally:

    mvn gcloud:run
    
To deploy into Google App Engine:

    mvn gcloud:deploy -Dgcloud.project=YOUR_PROJECT_ID
    
