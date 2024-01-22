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

package io.github.microapplet.remote.net.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * 专线环境网络超时
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/10/18, &nbsp;&nbsp; <em>version:3.0.0</em>,  &nbsp;&nbsp;  <em>java version:8</em>
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DedicatedTimeoutException extends RuntimeException{

    private final String supplier;
    private final String namespace;
    private final String environment;
    private final Throwable throwable;

    public static DedicatedTimeoutException create(String supplier, String namespace, String environment,Throwable throwable){
        if (!StringUtils.equalsAnyIgnoreCase("DEDICATED", environment))
            return null;

        return new DedicatedTimeoutException(supplier,namespace,environment,throwable);
    }

    @Override
    public String getMessage() {
        return "API 供应商：[" + supplier + "], 命名空间：[" + namespace + "],网络环境：["  + environment + "]专线网络不可用 " + Optional.ofNullable(throwable).map(Throwable::getMessage).orElse(StringUtils.EMPTY);
    }
}
