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
package io.github.microapplet.remote.lifecycle.callback;


import io.github.microapplet.remote.context.RemoteMethodConfig;
import io.github.microapplet.remote.context.RemoteReqContext;
import io.github.microapplet.remote.context.RemoteResContext;
import io.github.microapplet.remote.lifecycle.CallBack;

/**
 * <p> 回调接口，当流程处理成功时，回调此接口
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2022/11/29 &nbsp;&nbsp; 1.0 &nbsp;&nbsp; JDK 8
 */
@FunctionalInterface
public interface OnSuccess extends CallBack {

    /**
     * 当回调方法  SuccessWhen#success(Data, RemoteReqContext, RemoteResContext, Object[]) 执行成功时，回调此接口
     * @param data  相应结果
     * @param req   Remote 请求上下文
     * @param res   Remote 相应上下文
     */
    void onSuccess(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args);
}
