package com.miniioc.core.context;

import com.miniioc.core.lifecycle.BeanLifecycleProcessor;

public class SingletonScopeHandler implements BeanScopeHandler {
    private final BeanLifecycleProcessor lifecycleProcessor;

    public SingletonScopeHandler(BeanLifecycleProcessor lifecycleProcessor) {
        this.lifecycleProcessor = lifecycleProcessor;
    }

    @Override
    public Object getInstance(BeanDefinition bd, ApplicationContext context) throws Exception {
        if (bd.getInstance() == null) {
            Object instance = context.createBean(bd.getBeanClass());
            lifecycleProcessor.processPostConstruct(instance);
            bd.setInstance(instance);
        }
        return bd.getInstance();
    }
}