package org.spring.divas.userservice.common.auth;

import org.spring.divas.userservice.common.auth.authorizers.Authorizer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthChecker {
    private AuthChecker() {
        /* This utility class should not be instantiated */
    }


    public static void require(Authorizer auth) {
        Authentication current = SecurityContextHolder.getContext().getAuthentication();
        if (current instanceof AnonymousAuthenticationToken) {
            return;
        }
        if (!auth.passes()) {
            throw new AccessDeniedException(String.join("; ", auth.getErrorMsg()));
        }
    }
}
