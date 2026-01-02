package com.miniioc.core.factory;

import com.miniioc.core.annotation.ScopeType;
import com.miniioc.core.context.ApplicationContext;
import com.miniioc.core.context.BeanDefinition;
import com.miniioc.core.lifecycle.BeanLifecycleProcessor;
import java.util.HashMap;
import java.util.Map;

public class BeanFactory {

    private final Map<Class<?>, Object> singletonCache = new HashMap<>();
    private final Map<Class<?>, BeanDefinition> definitions;
    private final BeanLifecycleProcessor lifecycleProcessor;

    public BeanFactory(Map<Class<?>, BeanDefinition> definitions,
                       BeanLifecycleProcessor lifecycleProcessor) {
        this.definitions = definitions;
        this.lifecycleProcessor = lifecycleProcessor;
    }

    public Object getBean(Class<?> clazz, ApplicationContext context) {
        BeanDefinition bd = definitions.get(clazz);

        if (bd.getScope() == ScopeType.SINGLETON) {
            return singletonCache.computeIfAbsent(clazz,
                    c -> initialize(bd, context));
        }
        return initialize(bd, context);
    }

    private Object initialize(BeanDefinition bd, ApplicationContext context) {
        Object bean = context.createBean(bd.getBeanClass());
        lifecycleProcessor.processPostConstruct(bean);
        return bean;
    }
}
