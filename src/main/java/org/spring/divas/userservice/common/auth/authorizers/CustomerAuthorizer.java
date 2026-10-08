package org.spring.divas.userservice.common.auth.authorizers;

import org.spring.divas.userservice.common.enums.UserRole;
import org.springframework.security.core.Authentication;

public class CustomerAuthorizer extends RoleAuthorizer {

    public CustomerAuthorizer(Authentication auth) {
        super(auth);
    }

    @Override
    protected UserRole getUserRole() {
        return UserRole.CUSTOMER;
    }
}
