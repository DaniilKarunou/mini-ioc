package com.miniioc.core.context.beans;

import com.miniioc.core.annotation.injection.Scope;
import com.miniioc.core.annotation.injection.ScopeType;
import com.miniioc.core.context.exception.BeanCreationException;
import com.miniioc.core.context.exception.PackageScanException;
import com.miniioc.core.factory.BeanFactory;
import com.miniioc.core.lifecycle.BeanLifecycleProcessor;
import com.miniioc.core.scanner.ComponentScanner;
import com.miniioc.core.scanner.PackageScanner;
import com.miniioc.util.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static java.beans.Introspector.decapitalize;

public class ApplicationContext {

    private static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());

    private final Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
    private final BeanLifecycleProcessor lifecycleProcessor = new BeanLifecycleProcessor();
    private final BeanFactory beanFactory = new BeanFactory(beanDefinitions, lifecycleProcessor);

    public void registerBean(Class<?> clazz, boolean lazy, String qualifier) {
        ScopeType scope = clazz.isAnnotationPresent(Scope.class)
                ? clazz.getAnnotation(Scope.class).value()
                : ScopeType.SINGLETON;

        String name = qualifier != null ? qualifier : decapitalize(clazz.getSimpleName());

        BeanDefinition bd = new BeanDefinition(name, clazz, scope, lazy, qualifier);
        beanDefinitions.put(name, bd);

        logger.info(() -> "Registering bean: " + clazz.getName() + ", name=" + name +
                ", scope=" + scope + ", lazy=" + lazy + ", qualifier=" + qualifier);
    }

    public void registerBean(Class<?> clazz, boolean lazy) {
        registerBean(clazz, lazy, null);
    }

    public void initializeSingletons() {
        List<BeanDefinition> singletons = beanDefinitions.values().stream()
                .filter(bd -> bd.getScope() == ScopeType.SINGLETON && !bd.isLazy())
                .toList();

        for (BeanDefinition bd : singletons) {
            try {
                beanFactory.getBean(bd.getName(), this);
            } catch (Exception e) {
                throw new BeanCreationException("Failed to initialize singleton: " + bd.getBeanClass(), e);
            }
        }
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
                String qualifier = BeanUtils.resolveQualifier(clazz);
                registerBean(clazz, lazy, qualifier);
            }
        }
    }

    public Object getBean(String name) {
        BeanDefinition bd = beanDefinitions.get(name);
        if (bd == null) {
            throw new BeanCreationException("No bean found with name " + name);
        }
        return beanFactory.getBean(name, this);
    }

    public Object getBean(Class<?> type) {
        return getBean(type, null);
    }

    public Object getBean(Class<?> type, String qualifier) {
        return beanDefinitions.values().stream()
                .filter(bd -> type.isAssignableFrom(bd.getBeanClass()))
                .filter(bd -> qualifier == null || qualifier.equals(bd.getQualifier()))
                .findFirst()
                .map(bd -> beanFactory.getBean(bd.getName(), this))
                .orElseThrow(() -> new BeanCreationException(
                        "No bean found for type " + type + " with qualifier " + qualifier
                ));
    }

    public Map<String, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }
}