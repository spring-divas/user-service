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
        "hasRole('ADMIN') || "
                + "authentication.name == T(java.lang.String).valueOf({userId}) || "
                + "(hasRole('MANAGER') && "
                + "@managerAuthorization.isAllowed(authentication, {venueId}, {level}))"
)
public @interface RequiresUserManagerOrAdmin {

    String userId();

    String venueId();

    ManagerRole level();
}