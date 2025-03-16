/*
 *  Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */
 package com.asialjim.microapplet.remote.http.annotation.lifecycle;

import com.asialjim.microapplet.remote.context.*;
import com.asialjim.microapplet.remote.lifecycle.callback.Before;
import com.asialjim.microapplet.remote.net.jackson.AbstractJacksonUtil;

public abstract class AbstractJsonBodyLifeCycle  extends BaseObjectMapperRequestBodyLifeCycle implements Before {
     public static final GenericKey<RemoteMethodParameter> JSON_BODY_KEY = GenericKey.keyOf("JSON_REQUEST_BODY_KEY");

     @Override
     public int order() {
         return Integer.MAX_VALUE - 100;
     }

     @Override
     public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
         super.parseObjectMapperBody(methodConfig, req, res, args, JSON_BODY_KEY, AbstractJacksonUtil.JSON_MAPPER, "application/json");
     }
 }