package org.ceylonsmunich.service.config.table;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DisplayAsColumn {
    String name();
    String type();
    String enumName() default "";
    boolean visible() default true;
    String editable() default "always";
    boolean sortable() default true;
    boolean filterable() default true;
}
