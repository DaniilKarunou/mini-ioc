package com.miniioc.core.context;

import com.miniioc.core.annotation.*;
import com.miniioc.core.factory.BeanFactory;
import com.miniioc.core.lifecycle.BeanLifecycleProcessor;
import com.miniioc.core.scanner.ComponentScanner;
import com.miniioc.core.scanner.PackageScanner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {

    private final Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();
    private final BeanLifecycleProcessor lifecycleProcessor = new BeanLifecycleProcessor();
    private final BeanFactory beanFactory =
            new BeanFactory(beanDefinitions, lifecycleProcessor);

    public void registerBean(Class<?> clazz, boolean lazy) {
        ScopeType scope = clazz.isAnnotationPresent(Scope.class)
                ? clazz.getAnnotation(Scope.class).value()
                : ScopeType.SINGLETON;

        beanDefinitions.put(clazz, new BeanDefinition(clazz, scope, lazy));
    }

    public void initializeSingletons() {
        beanDefinitions.values().stream()
                .filter(bd -> bd.getScope() == ScopeType.SINGLETON && !bd.isLazy())
                .forEach(bd -> getBean(bd.getBeanClass()));
    }

    public void scanPackage(String packageName) throws PackageScanException {
        PackageScanner scanner = new PackageScanner();
        ComponentScanner componentScanner = new ComponentScanner();

        List<Class<?>> classes;
        try {
            classes = scanner.findClasses(packageName);
        } catch (ClassNotFoundException e) {
            throw new PackageScanException("Failed to scan package: " + packageName, e);
        }

        for (Class<?> clazz : classes) {
            if (componentScanner.isComponent(clazz)) {
                boolean lazy = componentScanner.isLazy(clazz);
                registerBean(clazz, lazy);
            }
        }
    }

    public Object getBean(Class<?> clazz) {
        return beanFactory.getBean(clazz, this);
    }

    public Object createBean(Class<?> clazz) {
        try {
            var constructor = clazz.getConstructors()[0];
            var paramTypes = constructor.getParameterTypes();
            Object[] paramInstances = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                paramInstances[i] = getBean(paramTypes[i]);
            }

            return constructor.newInstance(paramInstances);
        } catch (Exception e) {
            throw new BeanCreationException("Failed to create bean: " + clazz.getName(), e);
        }
    }


    public Map<Class<?>, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }
}