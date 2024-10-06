package com.lifestockserver.lifestock.security;

import com.lifestockserver.lifestock.user.domain.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
    UserRole[] roles() default {};
}
