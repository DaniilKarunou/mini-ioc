package com.miniioc.framework.util;

import com.miniioc.framework.annotation.stereotype.Component;
import com.miniioc.framework.annotation.beans.Qualifier;
import com.miniioc.framework.context.exception.BeanResolutionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public final class BeanUtils {

    private static final Logger logger = Logger.getLogger(BeanUtils.class.getName());

    private BeanUtils() {}

    public static String resolveQualifier(Class<?> clazz) {
        Qualifier qualifierAnn = clazz.getAnnotation(Qualifier.class);
        if (qualifierAnn != null && !qualifierAnn.value().isEmpty()) {
            return qualifierAnn.value();
        }

        for (Annotation annotation : clazz.getAnnotations()) {
            Class<?> annType = annotation.annotationType();
            Component compAnn = annType.getAnnotation(Component.class);
            if (compAnn != null) {
                try {
                    Method valueMethod = annType.getMethod("value");
                    String value = (String) valueMethod.invoke(annotation);
                    if (!value.isEmpty()) {
                        return value;
                    }
                } catch (NoSuchMethodException ignored) {
                    logger.fine("Annotation " + annType + " has no value() method.");
                } catch (Exception e) {
                    throw new BeanResolutionException("Failed to read qualifier from " + annType, e);
                }
            }
        }
        return null;
    }
}