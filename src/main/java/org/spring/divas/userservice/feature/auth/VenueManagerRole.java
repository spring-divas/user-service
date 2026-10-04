package org.spring.divas.userservice.feature.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.spring.divas.userservice.feature.manager.VenueManager;

@Getter
@RequiredArgsConstructor
public class VenueManagerRole {

    private final ManagerRole level;

    private final Long venueId;

    public static String toString(VenueManager manager) {
        return manager.getManagerLevel() + " " + manager.getVenueId();
    }

    public static VenueManagerRole fromString(String str) {
        try {
            str = str.substring("ROLE_".length());
            String[] parts = str.split(" ");
            if (parts.length != 2) {
                return null;
            }
            ManagerRole parsedLevel = ManagerRole.valueOf(parts[0]);
            Long parsedVenueId = Long.parseLong(parts[1]);
            return new VenueManagerRole(parsedLevel, parsedVenueId);
        } catch (Exception e) {
            return null;
        }
    }
}
