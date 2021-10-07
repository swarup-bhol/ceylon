package org.ceylonsmunich.service.config.table;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DisplayCurrency {
    String id() default "";
    boolean derived() default false;
}
