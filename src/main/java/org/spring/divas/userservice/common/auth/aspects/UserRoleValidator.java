package org.spring.divas.userservice.common.auth.aspects;

import org.spring.divas.userservice.common.auth.annotations.RequiresTheUser;
import org.spring.divas.userservice.common.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRoleValidator implements CheckValidator<RequiresTheUser> {

    @Override
    public boolean validate(RequiresTheUser annotation) {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (auth == null || auth.isAuthenticated()) {
            return false;
        }
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return roles.contains("ROLE_" + UserRole.ADMIN);
    }
}