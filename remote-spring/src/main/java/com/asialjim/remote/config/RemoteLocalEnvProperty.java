/*
 * Copyright 2014-2023 <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
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
package com.asialjim.remote.config;

import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@ToString
@Configuration
@ConfigurationProperties(prefix = RemoteLocalEnvProperty.PREFIX)
public class RemoteLocalEnvProperty {
    public static final String PREFIX = "remote.local";
    public static final String ENABLE = "enable";
    public static final String CONFIG_CONDITION_NAME = PREFIX + "." + ENABLE;

    private Boolean enable;
    private RemoteLocalEnvironment.RemoteLocalEnv env;
    private RemoteLocalEnvironment.Arch arch;
    private String primaries;

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public void setEnv(RemoteLocalEnvironment.RemoteLocalEnv env) {
        this.env = env;
    }

    public void setArch(RemoteLocalEnvironment.Arch arch) {
        this.arch = arch;
    }

    public void setPrimaries(String primaries) {
        this.primaries = primaries;
    }
}