package com.miniioc.core.annotation.injection;

import java.lang.annotation.*;

@Target({ ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    boolean required() default true;
}
