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
package com.asialjim.microapplet.remote.proxy.server.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.SocksMessage;
import io.netty.handler.codec.socksx.v4.DefaultSocks4CommandResponse;
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest;
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;

@ChannelHandler.Sharable
public final class SocksServerConnectHandler extends SimpleChannelInboundHandler<SocksMessage> {
    private final Bootstrap b = new Bootstrap();

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final SocksMessage message) {
        if (message instanceof Socks4CommandRequest) {
            Socks4CommandRequest request = (Socks4CommandRequest) message;
            Promise<Channel> promise = ctx.executor().newPromise();
            promise.addListener((FutureListener<Channel>) future -> {
                final Channel outboundChannel = future.getNow();
                if (future.isSuccess()) {
                    ChannelFuture responseFuture = ctx.channel().writeAndFlush(new DefaultSocks4CommandResponse(Socks4CommandStatus.SUCCESS));

                    responseFuture.addListener((ChannelFutureListener) channelFuture -> {
                        ctx.pipeline().remove(SocksServerConnectHandler.this);
                        outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
                        ctx.pipeline().addLast(new RelayHandler(outboundChannel));
                    });
                } else {
                    ctx.channel().writeAndFlush(new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED));
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
            });

            final Channel inboundChannel = ctx.channel();
            b.group(inboundChannel.eventLoop())
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new DirectClientHandler(promise));

            //noinspection CommentedOutCode
            b.connect(request.dstAddr(), request.dstPort()).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    ctx.channel().writeAndFlush(new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED));
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
               /*
                if (future.isSuccess()) {
                    // Connection established use handler provided results
                } else {
                    // Close the connection if the connection attempt has failed.
                    ctx.channel().writeAndFlush(new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED));
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
                */
            });
        } else if (message instanceof Socks5CommandRequest) {
            Socks5CommandRequest request = (Socks5CommandRequest) message;
            Promise<Channel> promise = ctx.executor().newPromise();
            promise.addListener((FutureListener<Channel>) future -> {
                final Channel outboundChannel = future.getNow();
                if (future.isSuccess()) {
                    ChannelFuture responseFuture = ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, request.dstAddrType(), request.dstAddr(), request.dstPort()));

                    responseFuture.addListener((ChannelFutureListener) channelFuture -> {
                        ctx.pipeline().remove(SocksServerConnectHandler.this);
                        outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
                        ctx.pipeline().addLast(new RelayHandler(outboundChannel));
                    });
                } else {
                    ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, request.dstAddrType()));
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
            });

            final Channel inboundChannel = ctx.channel();
            b.group(inboundChannel.eventLoop())
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new DirectClientHandler(promise));

            //noinspection CommentedOutCode
            b.connect(request.dstAddr(), request.dstPort()).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, request.dstAddrType()));
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
               /*
                if (future.isSuccess()) {
                    // do nothing here
                    // Connection established use handler provided results
                } else {
                    // Close the connection if the connection attempt has failed.
                    ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, request.dstAddrType()));
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
                */
            });
        } else {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        SocksServerUtils.closeOnFlush(ctx.channel());
    }
}