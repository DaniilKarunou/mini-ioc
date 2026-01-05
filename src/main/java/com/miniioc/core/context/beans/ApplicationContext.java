package com.miniioc.core.context.beans;

import com.miniioc.core.annotation.injection.Scope;
import com.miniioc.core.annotation.injection.ScopeType;
import com.miniioc.core.context.exception.BeanCreationException;
import com.miniioc.core.context.exception.PackageScanException;
import com.miniioc.core.factory.BeanFactory;
import com.miniioc.core.lifecycle.BeanLifecycleProcessor;
import com.miniioc.core.scanner.ComponentScanner;
import com.miniioc.core.scanner.PackageScanner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationContext {

    private static final Logger logger = Logger.getLogger(ApplicationContext.class.getName());

    private final Map<Class<?>, BeanDefinition> beanDefinitions = new HashMap<>();
    private final BeanLifecycleProcessor lifecycleProcessor = new BeanLifecycleProcessor();
    private final BeanFactory beanFactory = new BeanFactory(beanDefinitions, lifecycleProcessor);

    public void registerBean(Class<?> clazz, boolean lazy) {
        ScopeType scope = clazz.isAnnotationPresent(Scope.class)
                ? clazz.getAnnotation(Scope.class).value()
                : ScopeType.SINGLETON;

        logger.info(() -> "Registering bean: " + clazz.getName() + ", scope=" + scope + ", lazy=" + lazy);
        beanDefinitions.put(clazz, new BeanDefinition(clazz, scope, lazy));
    }

    public void initializeSingletons() {
        List<BeanDefinition> singletons = beanDefinitions.values().stream()
                .filter(bd -> bd.getScope() == ScopeType.SINGLETON && !bd.isLazy())
                .toList();

        for (BeanDefinition bd : singletons) {
            try {
                beanFactory.getBean(bd.getBeanClass(), this);
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

            logger.info(() -> "Creating bean: " + clazz.getName() + " with dependencies:");
            for (int i = 0; i < paramTypes.length; i++) {
                logger.log(Level.INFO, " - dependency: {0}", paramTypes[i].getName());
                paramInstances[i] = getBean(paramTypes[i]);
            }

            Object bean = constructor.newInstance(paramInstances);
            logger.info(() -> "Bean created: " + clazz.getName());
            return bean;
        } catch (Exception e) {
            throw new BeanCreationException("Failed to create bean: " + clazz.getName(), e);
        }
    }

    public Map<Class<?>, BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }
}