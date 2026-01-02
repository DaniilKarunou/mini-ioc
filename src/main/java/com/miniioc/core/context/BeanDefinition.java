package com.miniioc.core.context;

import com.miniioc.core.annotation.ScopeType;

public class BeanDefinition {

    private final Class<?> beanClass;
    private final ScopeType scope;
    private final boolean lazy;

    public BeanDefinition(Class<?> beanClass, ScopeType scope, boolean lazy) {
        this.beanClass = beanClass;
        this.scope = scope;
        this.lazy = lazy;
    }

    public Class<?> getBeanClass() { return beanClass; }
    public ScopeType getScope() { return scope; }
    public boolean isLazy() { return lazy; }
}