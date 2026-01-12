package com.miniioc.core.factory;

import com.miniioc.core.annotation.injection.ScopeType;
import com.miniioc.core.context.beans.ApplicationContext;
import com.miniioc.core.context.beans.BeanDefinition;
import com.miniioc.core.context.exception.BeanCreationException;
import com.miniioc.core.lifecycle.BeanLifecycleProcessor;
import com.miniioc.core.context.beans.ConstructorResolver;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanFactory {

    private final Map<String, Object> singletonCache = new HashMap<>();
    private final Map<String, BeanDefinition> definitions;
    private final BeanLifecycleProcessor lifecycleProcessor;

    private final ThreadLocal<Set<String>> beansInCreation = ThreadLocal.withInitial(HashSet::new);

    public BeanFactory(Map<String, BeanDefinition> definitions,
                       BeanLifecycleProcessor lifecycleProcessor) {
        this.definitions = definitions;
        this.lifecycleProcessor = lifecycleProcessor;
    }

    public Object getBean(String name, ApplicationContext context) {
        BeanDefinition bd = definitions.get(name);
        if (bd == null) {
            throw new BeanCreationException("No bean definition found for " + name);
        }

        if (bd.getScope() == ScopeType.SINGLETON) {
            Object existing = singletonCache.get(name);
            if (existing != null) return existing;

            singletonCache.put(name, null);
            Object bean = createBeanWithDependencies(bd, context);
            singletonCache.put(name, bean);
            return bean;
        } else {
            return createBeanWithDependencies(bd, context);
        }
    }

    private Object createBeanWithDependencies(BeanDefinition bd, ApplicationContext context) {
        Set<String> inCreation = beansInCreation.get();
        String name = bd.getName();
        if (inCreation.contains(name)) {
            throw new BeanCreationException("Cyclic dependency detected: " + name);
        }

        inCreation.add(name);
        try {
            Constructor<?> constructor = ConstructorResolver.resolve(bd.getBeanClass());
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] paramInstances = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                paramInstances[i] = context.getBean(paramTypes[i], null);
            }

            Object bean = constructor.newInstance(paramInstances);
            lifecycleProcessor.processPostConstruct(bean);
            return bean;
        } catch (Exception e) {
            throw new BeanCreationException("Failed to create bean: " + name, e);
        } finally {
            inCreation.remove(name);
            if (inCreation.isEmpty()) beansInCreation.remove();
        }
    }
}