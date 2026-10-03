package org.spring.divas.userservice.common.annotations;

import org.spring.divas.userservice.common.enums.ManagerRole;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(
        "hasRole('ADMIN') || " +
                "(hasRole('MANAGER') && " +
                "@managerAuthorization.isAllowed(authentication, {venueId}, {level}))"
)
public @interface RequiresManagerOrAdmin {

    String venueId();

    ManagerRole level();
}