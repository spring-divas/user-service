package org.spring.divas.userservice.common.auth;

import org.spring.divas.userservice.common.auth.authorizers.Authorizer;
import org.springframework.security.access.AccessDeniedException;

public class AuthChecker {

    public static void require(Authorizer auth) {
        if (!auth.passes()) {
            throw new AccessDeniedException(String.join("; ", auth.getErrorMsg()));
        }
    }
}
