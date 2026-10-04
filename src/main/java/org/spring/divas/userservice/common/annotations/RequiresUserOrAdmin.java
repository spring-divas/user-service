package org.spring.divas.userservice.common.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(
        "hasRole('ADMIN') || "
                + "authentication.name == T(java.lang.String).valueOf({userId})"
)
public @interface RequiresUserOrAdmin {

    String userId();
}