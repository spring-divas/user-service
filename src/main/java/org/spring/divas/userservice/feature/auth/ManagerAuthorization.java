package org.spring.divas.userservice.feature.auth;

import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component("managerAuthorization")
public class ManagerAuthorization {

    public boolean isAllowed(Authentication auth, String id, ManagerRole level) {
        Long venueId = Long.parseLong(id);
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        for (String str : roles) {
            VenueManagerRole role = VenueManagerRole.fromString(str);
            if (role == null || !venueId.equals(role.getVenueId())) {
                continue;
            }
            return switch (role.getLevel()) {
                case SENIOR -> true;
                case JUNIOR -> level == ManagerRole.JUNIOR;
            };
        }
        return false;
    }
}