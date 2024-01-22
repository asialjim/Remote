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
package io.github.microapplet.remote.spring;

import io.github.microapplet.remote.annotation.RemoteLifeCycle;
import io.github.microapplet.remote.context.RemoteLifeCycleHandlerFactory;
import io.github.microapplet.remote.loader.RemoteClassLoader;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RemoteBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    private static final Logger log = LoggerFactory.getLogger(RemoteBeanDefinitionRegistrar.class);
    private static final Set<String> CLASSES = new HashSet<>();
    private static final ResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();
    private static final Environment ENVIRONMENT = new StandardEnvironment();
    private static boolean init = false;

    private void init(BeanDefinitionRegistry applicationContext) {
        if (init)
            return;
        if (!(applicationContext instanceof DefaultListableBeanFactory beanFactory))
            return;

        Environment environment = beanFactory.getBean(Environment.class);
        String primaries = environment.getProperty("remote.local.primaries");
        RemoteLifeCycleHandlerFactory.primary(primaries);
        RemoteClassLoader.classLoader(beanFactory.getBeanClassLoader());
        RemoteClassLoader.INSTANCE.init();
        init = true;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, @SuppressWarnings("NullableProblems") BeanDefinitionRegistry registry) {
        init(registry);
        String configClassName = metadata.getClassName();

        final Set<String> packages = new HashSet<>();

        try {
            selectBrokerScanClass(Class.forName(configClassName), packages);
        } catch (ClassNotFoundException e) {
            // do nothing here
        }

        for (String aPackage : packages) {
            try {
                String resourcePattern = "**/*.class";
                String packageSearchPath = "classpath*:" + ClassUtils.convertClassNameToResourcePath(ENVIRONMENT.resolveRequiredPlaceholders(aPackage)) + "/" + resourcePattern;
                Resource[] resources = RESOLVER.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    MetadataReader metadataReader = METADATA_READER_FACTORY.getMetadataReader(resource);
                    AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                    //noinspection ConstantValue
                    if (Objects.isNull(annotationMetadata))
                        continue;
                    if (annotationMetadata.isAnnotation())
                        continue;
                    if (!annotationMetadata.isInterface())
                        continue;
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    String className = classMetadata.getClassName();
                    if (StringUtils.isBlank(className))
                        continue;

                    try {
                        Class<?> aClass = Class.forName(className);
                        boolean candidateClass = candidateClass(aClass);
                        if (!candidateClass)
                            continue;

                        if (CLASSES.contains(className))
                            continue;
                        CLASSES.add(className);
                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RemoteFactoryBean.class);
                        builder.addPropertyValue("remoteInterface", className);
                        builder.setLazyInit(true);
                        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
                        registry.registerBeanDefinition(className, beanDefinition);
                    } catch (ClassNotFoundException e) {
                        log.error("Cannot Found Class: {}, Exception: {}", className, e.getMessage(), e);
                    }
                }
            } catch (IOException e) {
                log.warn("Resolve package: {} to Find Remote Client Classes Exception Happen: {}", aPackage, e.getMessage(), e);
            }
        }

    }


    private void selectBrokerScanClass(Class<?> sourceClass, Set<String> packages) {
        Class<?> superclass = sourceClass.getSuperclass();
        if (!Object.class.equals(superclass))
            selectBrokerScanClass(superclass, packages);

        Annotation[] annotations = sourceClass.getAnnotations();
        for (Annotation annotation : annotations) {
            selectBrokerScanClass(annotation, packages);
        }
    }

    private void selectBrokerScanClass(Annotation annotation, Set<String> packages) {
        if (Objects.isNull(annotation))
            return;

        if (annotation instanceof RemoteScan) {
            String[] value = ((RemoteScan) annotation).value();
            packages.addAll(Arrays.asList(value));
            return;
        }

        if (annotation.annotationType().getName().startsWith("java.lang.annotation"))
            return;

        Annotation[] annotations = annotation.annotationType().getAnnotations();
        if (ArrayUtils.isNotEmpty(annotations)) {
            for (Annotation annotation1 : annotations) {
                selectBrokerScanClass(annotation1, packages);
            }
        }
    }

    private boolean candidateClass(Class<?> sourceClass) {
        Annotation[] annotations = sourceClass.getAnnotations();
        for (Annotation annotation : annotations) {
            boolean b = candidateAnnotation(annotation);
            if (b)
                return true;
        }

        Class<?> superclass = sourceClass.getSuperclass();
        if (Objects.nonNull(superclass))
            return candidateClass(superclass);

        return false;
    }

    private boolean candidateAnnotation(Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        if (annotationType.getName().startsWith("java.lang.annotation"))
            return false;

        if (annotationType.isAnnotationPresent(RemoteLifeCycle.class))
            return true;

        Annotation[] annotations = annotationType.getAnnotations();
        if (ArrayUtils.isNotEmpty(annotations)) {
            for (Annotation annotation1 : annotations) {
                boolean b = candidateAnnotation(annotation1);
                if (b)
                    return true;
            }
        }

        return false;
    }
}
