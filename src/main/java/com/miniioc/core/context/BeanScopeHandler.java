package com.miniioc.core.context;

public interface BeanScopeHandler {
    Object getInstance(BeanDefinition bd, ApplicationContext context) throws Exception;
}