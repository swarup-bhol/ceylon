package org.ceylonsmunich.service.config.table;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DisplayNumberFormat {
    boolean thousandSeparators() default false;
    int precision() default Integer.MIN_VALUE;
}
