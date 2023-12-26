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

package com.asialjim.remote.annotation;

import java.lang.annotation.*;

/**
 * <pre>
 * Remote 客户端类型属性, 一般用于标记客户端的某种子属性，该属性可用于用户指定优先某种属性执行。
 * "Remote Client Type Property" is generally used to denote a specific sub-property of the client. This property can be used for users to specify a preference for executing certain operations based on this attribute.
 * </pre>
 *
 * @author Copyright  © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/9/26, &nbsp;&nbsp; <em>version:3.0.0</em>, &nbsp;&nbsp; <em>java version:8</em>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteSubProperty {

    String[] value();
}