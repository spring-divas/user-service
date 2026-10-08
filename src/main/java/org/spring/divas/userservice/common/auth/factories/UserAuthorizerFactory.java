package org.spring.divas.userservice.common.auth.factories;

import org.spring.divas.userservice.common.auth.authorizers.UserAuthorizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserAuthorizerFactory {

    public static UserAuthorizer isUser(Long userId) {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return new UserAuthorizer(auth, userId);
    }
}
