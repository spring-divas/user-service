package org.spring.divas.userservice.common.auth.authorizers;

import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@RequiredArgsConstructor
abstract class RoleAuthorizer extends Authorizer {

    private final Authentication auth;

    @Override
    public boolean passes() {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return roles.contains("ROLE_" + getUserRole());
    }

    @Override
    public List<String> getErrorMsg() {
        return List.of("User does not have role: " + getUserRole());
    }

    protected abstract UserRole getUserRole();
}