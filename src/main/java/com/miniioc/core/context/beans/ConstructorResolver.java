package com.miniioc.core.context.beans;

import com.miniioc.core.annotation.injection.Autowired;
import com.miniioc.core.context.exception.BeanCreationException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public final class ConstructorResolver {

    private ConstructorResolver() {}

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> resolve(Class<T> beanClass) {
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();

        if (constructors.length == 0) {
            throw new BeanCreationException(
                    "No constructors found for " + beanClass.getName());
        }

        List<Constructor<T>> autowired = Arrays.stream(constructors)
                .filter(c -> c.isAnnotationPresent(Autowired.class))
                .map(c -> (Constructor<T>) c)
                .toList();

        if (autowired.size() > 1) {
            throw new BeanCreationException(
                    "Multiple @Autowired constructors found in " + beanClass.getName());
        }

        if (autowired.size() == 1) {
            return autowired.getFirst();
        }

        List<Constructor<T>> eligible = Arrays.stream(constructors)
                .filter(c -> Modifier.isPublic(c.getModifiers()) || Modifier.isProtected(c.getModifiers()))
                .map(c -> (Constructor<T>) c)
                .toList();

        if (eligible.size() == 1) {
            return eligible.getFirst();
        }

        for (Constructor<?> ctor : constructors) {
            if (ctor.getParameterCount() == 0) {
                return (Constructor<T>) ctor;
            }
        }

        throw new BeanCreationException(
                "No suitable constructor found for " + beanClass.getName() +
                        ". Add @Autowired or a no-args constructor."
        );
    }
}