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
 package com.asialjim.remote.http.annotation.lifecycle;

import com.asialjim.remote.context.GenericKey;
import com.asialjim.remote.context.RemoteMethodConfig;
import com.asialjim.remote.context.RemoteReqContext;
import com.asialjim.remote.context.RemoteResContext;
import com.asialjim.remote.lifecycle.callback.After;
import com.asialjim.remote.lifecycle.callback.Before;
import com.asialjim.remote.lifecycle.callback.Invoke;
import com.asialjim.remote.lifecycle.callback.OnError;
import com.asialjim.remote.net.constant.RemoteConstant;
import com.asialjim.remote.net.jackson.AbstractJacksonUtil;
import com.asialjim.remote.net.mime.MimeMenu;
import com.asialjim.remote.net.response.RemoteNetResponseParserHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimeType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


 /**
  * HTTP 基础处理器
  *
  * @author Copyright &copy; <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
  * @version 4.0
  * @since 2023/7/10, &nbsp;&nbsp; <em>version:4.0</em>, &nbsp;&nbsp; <em>java version:8</em>
  */
 public abstract class AbstractHttpMappingLifeCycle implements Before, Invoke, After, OnError {
     private static final Logger log = LoggerFactory.getLogger(AbstractHttpMappingLifeCycle.class);
     public static final GenericKey<String> HTTP_REQUEST_URI = GenericKey.keyOf("HTTP_REQUEST_URI");
     public static final GenericKey<String> HTTP_METHOD_KEY = GenericKey.keyOf("HTTP_REQUEST_METHOD");
     public static final GenericKey<Map<String, String>> COMMON_QUERY = GenericKey.keyOf("COMMON_HTTP_QUERY");
     public static final GenericKey<Map<String, String>> COMMON_HEADER = GenericKey.keyOf("COMMON_HTTP_HEADER");
     private static final Map<String, String> EMPTY_HEADER = new HashMap<>();

     @Override
     public final int order() {
         return Integer.MIN_VALUE + 100;
     }

     @Override
     public void before(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
         req.put(HTTP_METHOD_KEY, methodConfig.config(HTTP_METHOD_KEY));
         if (StringUtils.isBlank(req.get(HTTP_REQUEST_URI)))
            req.put(HTTP_REQUEST_URI, methodConfig.config(HTTP_REQUEST_URI));
         req.put(COMMON_HEADER, methodConfig.config(COMMON_HEADER));
         Integer port = req.get(RemoteConstant.PORT);
         if (Objects.isNull(port) || port == 0) {
             if (StringUtils.equalsIgnoreCase("http", req.get(RemoteConstant.SCHEMA)))
                 port = 80;
             if (StringUtils.equalsIgnoreCase("https", req.get(RemoteConstant.SCHEMA)))
                 port = 443;
         }

         req.put(RemoteConstant.PORT, port);

         String proxyHost = req.get(RemoteConstant.PROXY_HOST);
         Integer proxyPort = req.get(RemoteConstant.PROXY_PORT);
         log.info("\r\n\tRemote NET Req Host >>> Client:{} >>> {}://{}:{}/ , Proxy[Host:{}, Port:{}]",
                 methodConfig.getRemoteName(),
                 req.get(RemoteConstant.SCHEMA),
                 req.get(RemoteConstant.HOST),
                 req.get(RemoteConstant.PORT),
                 StringUtils.isNotBlank(proxyHost) ? proxyHost : "NONE",
                 StringUtils.isNotBlank(String.valueOf(proxyPort)) ? proxyPort : "NONE");
     }

     @Override
     public void invoke(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
         // do nothing here
     }

     @Override
     public void after(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
         doAfter(data, methodConfig, req, res, args);
         log.info("\r\n\tRemote NET Res Line <<< Client:{} <<< Status: {}, ProtocolVersion: {}", methodConfig.getRemoteName(), res.getStatus(), res.getProtocol());
         log.info("\r\n\tRemote NET Res Head <<< Client:{} <<< {}", methodConfig.getRemoteName(), AbstractJacksonUtil.writeValueAsString(res.getHeaders(), AbstractJacksonUtil.JSON_MAPPER));

         Class<?> returnClass = methodConfig.getReturnClass();
         if (Void.class.isAssignableFrom(returnClass)) {
             res.callback();
             return;
         }

         Map<String, String> headers = responseHeader(res);
         String lengthStr = headers.entrySet().stream().filter(item -> StringUtils.equalsIgnoreCase(item.getKey(), "Content-Length")).map(Map.Entry::getValue).findAny().orElse("0");

         if (Integer.parseInt(lengthStr) == 0) {
             try {
                 res.setData(returnClass.newInstance());
             } catch (InstantiationException | IllegalAccessException e) {
                 log.info("\r\n\tRemote NET Res Build <<< Client:{} Error <<< {}", methodConfig.getRemoteName(), e.getMessage(), e);
             }
             res.callback();
             return;
         }


         String contentType = headers.entrySet().stream().filter(item -> StringUtils.equalsIgnoreCase(item.getKey(), "Content-Type")).map(Map.Entry::getValue).findAny().orElse(StringUtils.EMPTY);

         if (log.isDebugEnabled())
             log.debug("Response Content Type: {}", contentType);
         MimeType mimeType = Optional.ofNullable(res.property(MimeMenu.MIME_TYPE_GENERIC_KEY)).orElseGet(() -> MimeMenu.createConstant(contentType));
         RemoteNetResponseParserHolder.parse(mimeType, methodConfig, res);
         res.callback();
     }


     protected Map<String, String> responseHeader(RemoteResContext res) {
         Object headers = res.getHeaders();
         if (headers instanceof Map)
             //noinspection unchecked
             return (Map<String, String>) headers;
         return EMPTY_HEADER;
     }

     public void doAfter(@SuppressWarnings("unused") Object data,
                         @SuppressWarnings("unused") RemoteMethodConfig methodConfig,
                         @SuppressWarnings("unused") RemoteReqContext req,
                         @SuppressWarnings("unused") RemoteResContext res,
                         @SuppressWarnings("unused") Object[] args) {

         // do nothing here
     }

     @Override
     public boolean onError(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Throwable ex, Object[] args) {
         return false;
     }
 }