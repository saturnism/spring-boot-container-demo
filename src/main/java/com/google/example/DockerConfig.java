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
package com.google.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Profile("docker")
@Configuration
public class DockerConfig {
    @Value(value="#{systemEnvironment['REDIS_PORT_6379_TCP_ADDR']}")
    private String redisHost;
    @Value("#{systemEnvironment['REDIS_PORT_6379_TCP_PORT']}")
    private Integer redisPort;

    @Bean
    public JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        System.out.println("Redis Host: " + redisHost + ", Redis Port: " + redisPort);
        factory.setHostName(redisHost);
        factory.setPort(redisPort);

        return factory;
    }
}
