package com.miniioc.core.context;

import com.miniioc.core.lifecycle.BeanLifecycleProcessor;

public class PrototypeScopeHandler implements BeanScopeHandler {
    private final BeanLifecycleProcessor lifecycleProcessor;

    public PrototypeScopeHandler(BeanLifecycleProcessor lifecycleProcessor) {
        this.lifecycleProcessor = lifecycleProcessor;
    }

    @Override
    public Object getInstance(BeanDefinition bd, ApplicationContext context) throws Exception {
        Object instance = context.createBean(bd.getBeanClass());
        lifecycleProcessor.processPostConstruct(instance);
        return instance;
    }
}