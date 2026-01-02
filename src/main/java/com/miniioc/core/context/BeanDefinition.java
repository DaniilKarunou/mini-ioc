package com.miniioc.core.context;

public class BeanDefinition {

    private final Class<?> beanClass;
    private final boolean lazy;
    private final BeanScopeHandler scopeHandler;
    private Object instance;

    public BeanDefinition(Class<?> beanClass, BeanScopeHandler scopeHandler, boolean lazy) {
        this.beanClass = beanClass;
        this.scopeHandler = scopeHandler;
        this.lazy = lazy;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public boolean isLazy() {
        return lazy;
    }

    public Object getInstance(ApplicationContext context) throws Exception {
        return scopeHandler.getInstance(this, context);
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Object getInstance() {
        return instance;
    }
}