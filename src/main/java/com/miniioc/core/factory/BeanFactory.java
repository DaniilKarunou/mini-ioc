package com.miniioc.core.factory;

import com.miniioc.core.annotation.injection.ScopeType;
import com.miniioc.core.context.beans.ApplicationContext;
import com.miniioc.core.context.beans.BeanDefinition;
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
        if (bd == null) {
            throw new RuntimeException("No bean definition found for " + clazz.getName());
        }

        if (bd.getScope() == ScopeType.SINGLETON) {
            Object existing = singletonCache.get(clazz);
            if (existing != null) {
                return existing;
            }

            singletonCache.put(clazz, null);
            Object bean = initialize(bd, context);
            singletonCache.put(clazz, bean);

            return bean;
        } else {
            return initialize(bd, context);
        }
    }

    private Object initialize(BeanDefinition bd, ApplicationContext context) {
        Object bean = context.createBean(bd.getBeanClass());
        lifecycleProcessor.processPostConstruct(bean);
        return bean;
    }
}
