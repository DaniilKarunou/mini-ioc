package com.miniioc.core.lifecycle;

import com.miniioc.core.annotation.PostConstruct;

import java.lang.reflect.Method;

public class BeanLifecycleProcessor {

    public void processPostConstruct(Object bean) {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                invoke(method, bean);
            }
        }
    }

    private void invoke(Method method, Object bean) {
        try {
            method.setAccessible(true);
            method.invoke(bean);
        } catch (Exception e) {
            throw new BeanLifecycleException(
                "Failed to invoke @PostConstruct on " + bean.getClass().getName(), e
            );
        }
    }
}
