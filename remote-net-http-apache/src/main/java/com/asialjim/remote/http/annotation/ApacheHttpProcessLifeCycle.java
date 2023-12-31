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
package com.asialjim.remote.http.annotation;

import com.asialjim.remote.annotation.RemoteSubProperty;
import com.asialjim.remote.context.RemoteMethodConfig;
import com.asialjim.remote.context.RemoteReqContext;
import com.asialjim.remote.context.RemoteResContext;
import com.asialjim.remote.http.annotation.lifecycle.AbstractHttpHeaderLifeCycle;
import com.asialjim.remote.http.annotation.lifecycle.AbstractHttpMappingLifeCycle;
import com.asialjim.remote.http.annotation.lifecycle.AbstractHttpQueryLifeCycle;
import com.asialjim.remote.http.client.ApacheRemoteHTTPClient;
import com.asialjim.remote.net.annotation.AbstractSslLifeCycle;
import com.asialjim.remote.net.annotation.ServerLifeCycle;
import com.asialjim.remote.net.client.RemoteNetClient;
import com.asialjim.remote.net.context.RemoteNetNodeKey;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 基于 Apache 的 HTTP 处理器
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/10/10, &nbsp;&nbsp; <em>version:3.0.0</em>,  &nbsp;&nbsp;  <em>java version:8</em>
 */
@RemoteSubProperty("apache.http,https")
public final class ApacheHttpProcessLifeCycle extends HttpMapping.HttpProcessLifeCycle {

    @Override
    public void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        String uri = req.get(AbstractHttpMappingLifeCycle.HTTP_REQUEST_URI);
        if (StringUtils.isBlank(uri))
            uri = methodConfig.config(AbstractHttpMappingLifeCycle.HTTP_REQUEST_URI);

        Map<String, String> commonQuery = Optional.ofNullable(req.get(AbstractHttpMappingLifeCycle.COMMON_QUERY)).orElseGet(HashMap::new);
        Map<String, String> configCommonQuery = methodConfig.config(AbstractHttpMappingLifeCycle.COMMON_QUERY);
        if (Optional.ofNullable(configCommonQuery).isPresent())
            commonQuery.putAll(configCommonQuery);
        Map<String, String> queries = Optional.ofNullable(req.get(AbstractHttpQueryLifeCycle.HTTP_QUERY_VALUE)).orElseGet(HashMap::new);
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        if (MapUtils.isNotEmpty(commonQuery)) {
            for (Map.Entry<String, String> entry : commonQuery.entrySet()) {
                nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        if (MapUtils.isNotEmpty(queries)) {
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }

        String query = URLEncodedUtils.format(nameValuePairList, StandardCharsets.UTF_8);
        if (StringUtils.isNotBlank(query))
            uri += "&" + query;
        if (StringUtils.contains(uri, "&") && !StringUtils.contains(uri, "?"))
            uri = uri.replaceFirst("&", "?");

        req.put(AbstractHttpMappingLifeCycle.HTTP_REQUEST_URI, uri);


        RemoteNetNodeKey nodeKey = req.get(ServerLifeCycle.NET_NODE_KEY_GENERIC_KEY);
        SSLContext sslContext = req.get(AbstractSslLifeCycle.SSL_CONTEXT_GENERIC_KEY);
        if (Objects.nonNull(nodeKey) && Objects.nonNull(sslContext))
            nodeKey.setSslContext(sslContext);
        Map<String, String> headerMap = Optional.ofNullable(req.get(AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE)).orElseGet(HashMap::new);
        req.put(AbstractHttpHeaderLifeCycle.HTTP_HEADER_VALUE, headerMap);
    }

    @Override
    protected RemoteNetClient newRemoteNetClient(RemoteNetNodeKey nodeKey) {
        return new ApacheRemoteHTTPClient(nodeKey);
    }
}