package com.abclinic.server.annotation.authorized;

import com.abclinic.server.model.entity.user.User;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * @author tmduc
 * @package com.abclinic.server.annotation
 * @created 1/11/2020 2:51 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Restricted {
    Class<? extends User>[] included() default {};
    Class<? extends User>[] excluded() default {};
}