package org.spring.divas.userservice.common.auth.annotations;

import org.spring.divas.userservice.common.auth.aspects.ManagerValidator;
import org.spring.divas.userservice.common.enums.ManagerRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Check(validator = ManagerValidator.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresManager {

    ManagerRole requiredLevel();

    String venueId();
}