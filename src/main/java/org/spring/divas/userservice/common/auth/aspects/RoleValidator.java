package org.spring.divas.userservice.common.auth.aspects;

import org.spring.divas.userservice.common.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.annotation.Annotation;
import java.util.List;

public abstract class RoleValidator<A extends Annotation> implements CheckValidator<A> {

    @Override
    public boolean validate(A annotation) {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return roles.contains("ROLE_" + getUserRole());
    }

    protected abstract UserRole getUserRole();
}