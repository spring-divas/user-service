package org.spring.divas.userservice.common.auth.authorizers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;

import java.util.List;

@RequiredArgsConstructor
public class UserAuthorizer extends Authorizer {

    private final Authentication auth;

    private final Long userId;

    @Override
    public boolean passes() {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        Long actualUserId = Long.parseLong(auth.getName());
        return actualUserId.equals(userId);
    }

    @Override
    public List<String> getErrorMsg() {
        return List.of("Authentication does not match the required user account");
    }
}
