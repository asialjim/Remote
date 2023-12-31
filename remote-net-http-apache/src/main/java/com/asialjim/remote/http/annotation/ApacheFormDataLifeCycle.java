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
import com.asialjim.remote.http.annotation.body.FormData;
import com.asialjim.remote.http.annotation.lifecycle.UploadAttributeWrapper;
import com.asialjim.remote.http.annotation.lifecycle.UploadByteArrayWrapper;
import com.asialjim.remote.http.client.ApacheRemoteHTTPClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.util.List;

/**
 * 基于Apache 的HTTP 上传文件处理器
 *
 * @author Copyright © <a href="mailto:asialjim@hotmail.com">Asial Jim</a>   Co., LTD
 * @version 3.0.0
 * @since 2023/10/10, &nbsp;&nbsp; <em>version:3.0.0</em>,  &nbsp;&nbsp;  <em>java version:8</em>
 */
@RemoteSubProperty("apache.http,https")
public class ApacheFormDataLifeCycle extends FormData.FormDataLifeCycle {
    @Override
    public void invoke(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
    }

    @Override
    protected void doBefore(Object data, RemoteMethodConfig methodConfig, RemoteReqContext req, RemoteResContext res, Object[] args) {
        List<UploadAttributeWrapper> attributes = req.get(UPLOAD_ATTRIBUTE_LIST);
        List<UploadByteArrayWrapper> contents = req.get(UPLOAD_CONTENT_LIST);
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        if (CollectionUtils.isNotEmpty(attributes)) {
            for (UploadAttributeWrapper attribute : attributes) {
                builder.addTextBody(attribute.getName(), attribute.getValue(), ContentType.parse(attribute.getContentType()));
            }
        }

        if (CollectionUtils.isNotEmpty(contents)) {
            for (UploadByteArrayWrapper content : contents) {
                builder.addBinaryBody(content.getName(), content.getContent(), ContentType.parse(content.getContentType()), content.getFileName());
            }
        }

        HttpEntity entity = builder.build();
        req.put(ApacheRemoteHTTPClient.HTTP_ENTITY_GENERIC_KEY,entity);
    }
}