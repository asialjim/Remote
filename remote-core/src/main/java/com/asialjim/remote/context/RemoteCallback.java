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
package com.asialjim.remote.context;

/**
 * <pre>
 * Remote 回调函数。
 * 在Remote 中，如果{@link  com.asialjim.remote.lifecycle.CallBack} 及其子接口/类是基于注解{@link  com.asialjim.remote.annotation.RemoteLifeCycle} 和注解生命周期处理器 {@link com.asialjim.remote.annotation.RemoteLifeCycle.LifeCycleHandler} 的一等公民
 * 那么，{@link RemoteCallback} 就是基于Java 方法参数的工作的一等公民。
 * 用于可根据自身需要，在Java 接口方法中声明此接口，而后将代码拆分封装到各种 {@link RemoteCallback} 的类中，并以Lambda 的方式传递给方法，最终有Remote-Core触发执行。
 *
 *
 * Remote Callback
 *
 * Within Remote, if {@link com.asialjim.remote.lifecycle.CallBack} and its subinterfaces/classes are first-class citizens based on the annotation {@link com.asialjim.remote.annotation.RemoteLifeCycle} and the annotation lifecycle handler {@link com.asialjim.remote.annotation.RemoteLifeCycle.LifeCycleHandler},
 * Then, {@link RemoteCallback} functions as a first-class citizen based on Java method parameters.
 * It's used to declare this interface within Java interface methods according to one's needs. Code can then be split and encapsulated into various classes of {@link RemoteCallback}, passed to methods via Lambda, and ultimately triggered for execution by Remote-Core
 * </pre>
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 1.0
 * @since 2023/12/26, &nbsp;&nbsp; <em>version:1.0</em>,  &nbsp;&nbsp;  <em>java version:8</em>
 */
@FunctionalInterface
public interface RemoteCallback {

    void fun();
}