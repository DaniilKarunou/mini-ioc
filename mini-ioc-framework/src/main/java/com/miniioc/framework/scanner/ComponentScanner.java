package com.miniioc.framework.scanner;

import com.miniioc.framework.annotation.stereotype.Component;
import com.miniioc.framework.annotation.beans.Lazy;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class ComponentScanner {

    public boolean isComponent(Class<?> clazz) {
        for (Annotation annotation : clazz.getAnnotations()) {
            if (isComponentAnnotation(annotation.annotationType(), new HashSet<>())) {
                return true;
            }
        }
        return false;
    }

    private boolean isComponentAnnotation(Class<?> annotationType, Set<Class<?>> visited) {
        if (!visited.add(annotationType)) {
            return false;
        }

        if (annotationType == Component.class) {
            return true;
        }

        if (annotationType.getPackageName().startsWith("java.lang.annotation")) {
            return false;
        }

        for (Annotation meta : annotationType.getAnnotations()) {
            if (isComponentAnnotation(meta.annotationType(), visited)) {
                return true;
            }
        }

        return false;
    }

    public boolean isLazy(Class<?> clazz) {
        Lazy lazy = clazz.getAnnotation(Lazy.class);
        return lazy != null && lazy.value();
    }
}