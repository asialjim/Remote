/*
 * Copyright 2014-2024 <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
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
package io.github.microapplet.remote.http.pool;

import io.github.microapplet.remote.net.context.RemoteNetNodeKey;
import io.github.microapplet.remote.net.netty.context.RemoteNettyChannelContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Objects;

/**
 * Remote Http 客户端连接池
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/3/13, &nbsp;&nbsp; <em>version:1.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
public class RemoteHttpClientPoolOnNetty {
    private static volatile int maxConnectionPerRout = 200;
    private static volatile int acquireTimeoutMillis;

    private final Bootstrap strap = new Bootstrap();

    private static RemoteHttpClientPoolOnNetty INSTANCE;
    private ChannelPoolMap<RemoteNetNodeKey, SimpleChannelPool> poolMap;

    public static SimpleChannelPool simpleChannelPool(RemoteNetNodeKey key) {
        return getInstance().poolMap.get(key);
    }

    private static RemoteHttpClientPoolOnNetty getInstance() {
       if (Objects.nonNull(INSTANCE))
            return INSTANCE;
        synchronized (RemoteHttpClientPoolOnNetty.class) {
            if (Objects.nonNull(INSTANCE))
                return INSTANCE;

            INSTANCE = new RemoteHttpClientPoolOnNetty();
            return INSTANCE.build();
        }
    }

    @SuppressWarnings("unused")
    public static void acquireTimeoutMillis(int acquireTimeoutMillis) {
        RemoteHttpClientPoolOnNetty.acquireTimeoutMillis = acquireTimeoutMillis;
    }

    @SuppressWarnings("unused")
    public static void maxConnectionPerRout(int maxConnectionPerRout) {
        RemoteHttpClientPoolOnNetty.maxConnectionPerRout = maxConnectionPerRout;
    }

    private RemoteHttpClientPoolOnNetty build() {
        this.strap.group(RemoteNettyChannelContext.group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
        this.poolMap = new RemoteHttpClientPoolMap(strap, maxConnectionPerRout, acquireTimeoutMillis);
        return this;
    }
}