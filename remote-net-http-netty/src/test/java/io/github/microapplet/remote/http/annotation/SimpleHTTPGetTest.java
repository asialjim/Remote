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

import io.github.microapplet.remote.net.annotation.Server;
import io.github.microapplet.remote.net.repository.ApiServerEnvironmentHolder;
import io.github.microapplet.remote.proxy.RemoteProxy;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 简单 http 客户端测试
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/10/10, &nbsp;&nbsp; <em>version:3.0.0</em>,  &nbsp;&nbsp;  <em>java version:8</em>
 */
public class SimpleHTTPGetTest {

    HttpGetInterface getInterface;

    @Before
    public void before() {
        ApiServerEnvironmentHolder.globalLogLevel("info");
        getInterface = RemoteProxy.create(HttpGetInterface.class);
    }

    @Test
    public void get(){
        try {
            System.out.println();
            WeChatAccessTokenRes res = getInterface.accessToken("a", "b");
            System.out.println(res);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    @Test public void forTest(){
        for (int i = 0; i < 5; i++) {
            get();
        }
    }

    @Test public void multiTheadTest() throws InterruptedException, ExecutionException {
        ExecutorService service = Executors.newFixedThreadPool(5);
        List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            tasks.add(() -> String.valueOf( getInterface.accessToken("a", "b")));
        }

        List<Future<String>> futures = service.invokeAll(tasks);
        for (Future<String> future : futures) {
            String s = future.get();
            System.out.println(s);
        }
    }

    @Server(schema = "http", host = "api.weixin.qq.com", port = 80/*, proxyHost = "localhost", proxyPort = 13001*/)
    interface HttpGetInterface {

        @HttpMapping(method = HttpMethod.GET, uri = "/cgi-bin/token", queries = @HttpQuery(name = "grant_type", value = "client_credential"))
        WeChatAccessTokenRes accessToken(@HttpQuery(name = "appid") String appid, @HttpQuery(name = "secret") String secret);
    }

    @Data
    public static class WeChatAccessTokenRes implements Serializable {
        private static final long serialVersionUID = -7521105708674266943L;
        private Integer errcode;
        private String errmsg;
        private String access_token;
        private Integer expires_in;
    }
}