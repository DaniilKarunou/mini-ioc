package com.miniioc.core.context;

import com.miniioc.core.annotation.*;
import com.miniioc.core.lifecycle.BeanLifecycleProcessor;
import com.miniioc.core.scanner.ComponentScanner;
import com.miniioc.core.scanner.PackageScanner;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationContext {

    private static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());

    private final Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();
    private final BeanLifecycleProcessor lifecycleProcessor = new BeanLifecycleProcessor();
    private final Map<ScopeType, BeanScopeHandler> scopeHandlers = new EnumMap<>(ScopeType.class);

    public ApplicationContext() {
        scopeHandlers.put(ScopeType.SINGLETON, new SingletonScopeHandler(lifecycleProcessor));
        scopeHandlers.put(ScopeType.PROTOTYPE, new PrototypeScopeHandler(lifecycleProcessor));
    }

    public void registerBean(Class<?> clazz, boolean lazy) {
        BeanScopeHandler handler;

        if (clazz.isAnnotationPresent(Scope.class)) {
            ScopeType scope = clazz.getAnnotation(Scope.class).value();
            handler = scopeHandlers.getOrDefault(scope, scopeHandlers.get(ScopeType.SINGLETON));
        } else {
            handler = scopeHandlers.get(ScopeType.SINGLETON);
        }

        beanDefinitions.put(clazz, new BeanDefinition(clazz, handler, lazy));

        if (logger.isLoggable(Level.INFO)) {
            logger.info(String.format("Registered bean: %s (lazy=%s, handler=%s)",
                    clazz.getName(), lazy, handler.getClass().getSimpleName()));
        }
    }

    public void initializeSingletons() throws Exception {
        for (BeanDefinition bd : beanDefinitions.values()) {
            if (!bd.isLazy() && bd.getInstance() == null) {
                bd.getInstance(this);
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
                registerBean(clazz, lazy);
            }
        }
    }

    public Object getBean(Class<?> clazz) throws Exception {
        BeanDefinition bd = beanDefinitions.get(clazz);
        if (bd == null) throw new BeanNotFoundException(clazz);
        return bd.getInstance(this);
    }

    Object createBean(Class<?> clazz) throws BeanCreationException {
        try {
            var constructor = clazz.getConstructors()[0];
            var paramTypes = constructor.getParameterTypes();
            Object[] paramInstances = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                paramInstances[i] = getBean(paramTypes[i]);
            }

            Object instance = constructor.newInstance(paramInstances);
            lifecycleProcessor.processPostConstruct(instance);
            return instance;
        } catch (Exception e) {
            throw new BeanCreationException("Failed to create bean: " + clazz.getName(), e);
        }
    }

    public Map<Class<?>, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }
}