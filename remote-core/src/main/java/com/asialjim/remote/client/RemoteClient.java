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
package com.asialjim.remote.client;

import com.asialjim.remote.context.RemoteReqContext;
import com.asialjim.remote.context.RemoteResContext;

/**
 * <pre>
 * Remote 客户端,一般适用于向某个系统发送一段什么内容，并又从对方接收一段什么内容的客户端，通常情况下，应用于网络请求，但更多的可能。
 *
 * "Remote Client" typically refers to a component used to send specific content to a system and receive corresponding content back. It's commonly applied in network requests but has broader potential applications beyond that context.
 * </pre>
 *
 * @author Copyright  © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/9/26, &nbsp;&nbsp; <em>version:3.0.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
public interface RemoteClient {

    void send(RemoteReqContext reqContext, RemoteResContext resContext);
}