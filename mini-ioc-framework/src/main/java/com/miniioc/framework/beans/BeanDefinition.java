package com.miniioc.framework.beans;

import com.miniioc.framework.annotation.beans.ScopeType;

public class BeanDefinition {

    private final String name;
    private final Class<?> beanClass;
    private final ScopeType scope;
    private final boolean lazy;
    private final String qualifier;

    public BeanDefinition(String name, Class<?> beanClass, ScopeType scope, boolean lazy, String qualifier) {
        this.name = name;
        this.beanClass = beanClass;
        this.scope = scope;
        this.lazy = lazy;
        this.qualifier = qualifier;
    }

    public String getName() { return name; }
    public Class<?> getBeanClass() { return beanClass; }
    public ScopeType getScope() { return scope; }
    public boolean isLazy() { return lazy; }
    public String getQualifier() { return qualifier; }
}