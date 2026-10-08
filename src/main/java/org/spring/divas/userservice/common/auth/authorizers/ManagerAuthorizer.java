package org.spring.divas.userservice.common.auth.authorizers;

import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.spring.divas.userservice.feature.auth.VenueManagerRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ManagerAuthorizer extends Authorizer {

    private final Authentication auth;

    private final Long venueId;

    private final ManagerRole level;


    @Override
    public boolean passes() {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        for (String str : roles) {
            VenueManagerRole role = VenueManagerRole.fromString(str);
            if (role == null || !Objects.equals(venueId, role.getVenueId())) {
                continue;
            }
            return switch (role.getLevel()) {
                case SENIOR -> true;
                case JUNIOR -> level == ManagerRole.JUNIOR;
            };
        }
        return false;
    }

    @Override
    public List<String> getErrorMsg() {
        return List.of("User does not have manager role: " + level + " for venue " + venueId);
    }
}
