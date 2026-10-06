package org.spring.divas.userservice.common.auth.factories;

import org.spring.divas.userservice.common.auth.authorizers.ManagerAuthorizer;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ManagerAuthorizerFactory {

    public static ManagerAuthorizer isManager(Long venueId, ManagerRole level) {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return new ManagerAuthorizer(auth, venueId, level);
    }
}
