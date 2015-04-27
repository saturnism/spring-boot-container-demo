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
package com.google.spring.demo.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Scope
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder

@RestController
@Scope("session")
class HelloWorldController implements Serializable {
    @Value('${app.version}')
    String version;
    Integer count = 0;

    @RequestMapping(value="/helloworld", method=RequestMethod.GET, produces="application/json")
    def helloworld() {
        HelloWorldResponse response = new HelloWorldResponse(
                hostname: hostname(),
                sessionId: RequestContextHolder.currentRequestAttributes().getSessionId(),
                count: ++count,
                version: version
                );

        return response;
    }

    @RequestMapping("/version")
    def version() {
        return version
    }

    def hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (all) {
            return "unknown";
        }
    }
}

class HelloWorldResponse {
    String hostname;
    String sessionId;
    int count;
    String version;
}

