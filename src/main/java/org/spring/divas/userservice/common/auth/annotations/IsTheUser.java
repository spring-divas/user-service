package org.spring.divas.userservice.common.auth.annotations;

import org.spring.divas.userservice.common.auth.aspects.UserValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Check(validator = UserValidator.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IsTheUser {

    String userId();
}