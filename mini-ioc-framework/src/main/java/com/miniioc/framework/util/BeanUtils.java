package com.miniioc.framework.util;

import com.miniioc.framework.annotation.stereotype.Component;
import com.miniioc.framework.annotation.beans.Qualifier;
import com.miniioc.framework.context.exception.BeanResolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class BeanUtils {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

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
                    logger.debug("Annotation {} has no value() method.", annType);
                } catch (Exception e) {
                    throw new BeanResolutionException("Failed to read qualifier from " + annType, e);
                }
            }
        }
        return null;
    }
}