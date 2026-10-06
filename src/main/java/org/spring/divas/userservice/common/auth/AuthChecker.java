package org.spring.divas.userservice.common.auth;

import jakarta.security.auth.message.AuthException;
import org.spring.divas.userservice.common.auth.authorizers.Authorizer;

public class AuthChecker {

    public static void require(Authorizer auth) throws AuthException {
        if (!auth.passes()) {
            throw new AuthException(String.join("; ", auth.getErrorMsg()));
        }
    }
}
