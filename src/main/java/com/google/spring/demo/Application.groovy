/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.spring.demo

import groovy.util.logging.Slf4j;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayBuilderDeserializer;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.google.spring.demo")
class Application {
    static void main(String[] args) {
        def executionEnv = new ExecutionEnvironment();
        def app = new SpringApplication(Application.class);
        def profiles = executionEnv.determineProfiles();
        println profiles
        if (profiles != null) {
            app.additionalProfiles = profiles;
        }

        app.run(args);
    }
}

@Slf4j
class ExecutionEnvironment {
    def determineProfiles() {
        def gaeEnvironment = System.getenv("GAE_PARTITION");
        def dockerEnvironment = System.getenv("REDIS_NAME");
        def kubernetesEnvironment = System.getenv("REDIS_MASTER_SERVICE_HOST");

        if (gaeEnvironment != null) {
            if ("dev" == gaeEnvironment) {
                log.info "Detected Google App Engine development environment - no session replication"
            }
            if ("dev" != gaeEnvironment) {
                log.info "Detected Google App Engine production environment - enable session replication"
                return ["GAE", "replication"];
            }
        } else if (dockerEnvironment != null) {
            log.info "Detected Docker environment with Redis - enable session replication"
            return ["docker", "replication"];
        } else if (kubernetesEnvironment != null) {
            log.info "Detected Kubernetes environment with Redis - enable session replication"
            return ["kubernetes", "replication"]
        } else {
            log.info "Didn't detect any special environments - no replication"
        }
    }
}

@Profile("kubernetes")
@Configuration
class KubernetesConfig {
    @Value(value="#{systemEnvironment['REDIS_MASTER_SERVICE_HOST']}")
    String redisHost;

    @Value("#{systemEnvironment['REDIS_MASTER_SERVICE_PORT']}")
    Integer redisPort;

    @Bean
    JedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory(hostName: redisHost, port: redisPort);
    }
}

@Profile("docker")
@Configuration
class DockerConfig {
    @Value(value="#{systemEnvironment['REDIS_PORT_6379_TCP_ADDR']}")
    String redisHost;

    @Value("#{systemEnvironment['REDIS_PORT_6379_TCP_PORT']}")
    Integer redisPort;

    @Bean
    JedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory(hostName: redisHost, port: redisPort);
    }
}

@Profile("GAE")
@Configuration
class GAEConfig {
    @Value('${spring.redis.host}')
    String redisHost;

    @Value('${spring.redis.port}')
    Integer redisPort;

    @Bean
    JedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory(hostName: redisHost, port: redisPort);
    }
}

@Configuration
@Profile("replication")
@EnableRedisHttpSession
@Slf4j
class SessionReplicationConfig {
    @Bean
    @Order(value = 0)
    def sessionRepositoryFilterRegistration(
            SessionRepositoryFilter springSessionRepositoryFilter) {
        log.info "Enabling Redis Http Session"
        def bean = new FilterRegistrationBean(
                filter: new DelegatingFilterProxy(springSessionRepositoryFilter),
                urlPatterns: ["/*"] as List);
        return bean;
    }
}