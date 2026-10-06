package org.spring.divas.userservice.common.auth.factories;

import org.spring.divas.userservice.common.auth.authorizers.AdminAuthorizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AdminAuthorizerFactory {

    public static AdminAuthorizer isAdmin() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return new AdminAuthorizer(auth);
    }
}
