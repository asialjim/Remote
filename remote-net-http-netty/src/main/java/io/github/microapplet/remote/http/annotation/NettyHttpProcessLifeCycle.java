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

package io.github.microapplet.remote.http.annotation;

import io.github.microapplet.remote.annotation.Primary;
import io.github.microapplet.remote.annotation.RemoteSubProperty;
import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.context.RemoteReqContext;
import io.github.microapplet.remote.context.RemoteResContext;
import io.github.microapplet.remote.http.client.NettyRemoteHTTPClient;
import io.github.microapplet.remote.net.client.RemoteNetClient;
import io.github.microapplet.remote.net.context.RemoteNetNodeKey;

/**
 * 基于 Netty 的HTTP 网络处理器
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/10/11, &nbsp;&nbsp; <em>version:3.0.0</em>,  &nbsp;&nbsp;  <em>java version:8</em>
 */
@Primary
@RemoteSubProperty("netty")
public class NettyHttpProcessLifeCycle extends HttpMapping.HttpProcessLifeCycle {
    @Override
    public void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
    }

    @Override
    protected void afterBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        NettyRemoteHTTPClient.buildHttpRequest(methodConfig,req);
    }

    @Override
    protected RemoteNetClient newRemoteNetClient(RemoteNetNodeKey nodeKey) {
        return new NettyRemoteHTTPClient(nodeKey);
    }
}