/*
 * Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
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
package com.asialjim.microapplet.remote.http.annotation.body;

import com.asialjim.microapplet.remote.annotation.RemoteLifeCycle;
import com.asialjim.microapplet.remote.context.RemoteMethodConfig;
import com.asialjim.microapplet.remote.context.RemoteMethodParameter;
import com.asialjim.microapplet.remote.http.annotation.lifecycle.AbstractOctetStreamBodyLifeCycle;

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Documented
@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RemoteLifeCycle(OctetStreamBody.OctetStreamBodyLifeCycle.class)
public @interface OctetStreamBody {
    abstract class OctetStreamBodyLifeCycle extends AbstractOctetStreamBodyLifeCycle implements RemoteLifeCycle.LifeCycleHandler<OctetStreamBody>{

        @Override
        public void doInit(RemoteMethodConfig methodConfig, RemoteMethodParameter methodParameter, OctetStreamBody annotation) {
            Map<String, Boolean> location = Optional.ofNullable(methodConfig.config(OCTET_STREAM_PARAMETER_LOCATION)).orElseGet(HashMap::new);
            if (Objects.isNull(methodParameter)){
                location.put(AbstractOctetStreamBodyLifeCycle.METHOD_LOCATION,Boolean.TRUE);
            } else {
                methodConfig.config(AbstractOctetStreamBodyLifeCycle.OCTET_STREAM_CONFIG, methodParameter);
                location.put(AbstractOctetStreamBodyLifeCycle.PARAMETER_LOCATION,Boolean.TRUE);
            }
        }
    }
}