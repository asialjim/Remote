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
package com.asialjim.microapplet.remote.http.annotation.lifecycle;

import com.asialjim.microapplet.remote.context.*;
import com.asialjim.microapplet.remote.lifecycle.callback.Invoke;
import com.asialjim.microapplet.remote.net.constant.RemoteConstant;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public abstract class BaseObjectMapperRequestBodyLifeCycle implements Invoke {
    public static final GenericKey<String> STRING_BODY_KEY = GenericKey.keyOf("STRING_BODY_KEY");

    protected void parseObjectMapperBody(RemoteMethodConfig methodConfig,
                                         RemoteReqContext req,
                                         RemoteResContext res,
                                         Object[] args,
                                         GenericKey<RemoteMethodParameter> parameterGenericKey,
                                         ObjectMapper mapper,
                                         String contentType) {

        Map<String, String> headers = Optional.ofNullable(req.get(AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE)).orElse(new HashMap<>());
        String charsetName = Optional.ofNullable(req.get(RemoteConstant.CHARSET)).orElse(StandardCharsets.UTF_8.name());
        headers.put("Content-Type", contentType + "; charset=" + charsetName);
        req.put(AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE,headers);

        try {
            // REQUEST_BODY_KEY
            RemoteMethodParameter parameter = methodConfig.config(parameterGenericKey);
            Object bodyObj = args[parameter.getIndex()];
            String reqBodyStr = bodyObj instanceof String ? (String) bodyObj : mapper.writeValueAsString(bodyObj);
            req.put(STRING_BODY_KEY,reqBodyStr);
            doBefore(res.getData(), methodConfig, req, res, args);
        } catch (Throwable t){
            res.setCause(t);
        }
    }

    @Override
    public void invoke(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
    }

    protected abstract void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args);
}