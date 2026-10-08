package org.spring.divas.userservice.common.auth.authorizers;

import org.spring.divas.userservice.common.enums.UserRole;
import org.springframework.security.core.Authentication;

public class AdminAuthorizer extends RoleAuthorizer {

    public AdminAuthorizer(Authentication auth) {
        super(auth);
    }

    @Override
    protected UserRole getUserRole() {
        return UserRole.ADMIN;
    }
}
