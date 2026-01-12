package com.miniioc.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public class AnnotationExtractor {

    private AnnotationExtractor(){}

    public static String[] getStrings(Annotation ann, String name) {
        try {
            return (String[]) ann.annotationType().getMethod(name).invoke(ann);
        } catch (NoSuchMethodException e) {
            return new String[0];
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(
                    "Cannot read annotation attribute '" + name +
                            "' from @" + ann.annotationType().getSimpleName(),
                    e
            );
        }
    }
}