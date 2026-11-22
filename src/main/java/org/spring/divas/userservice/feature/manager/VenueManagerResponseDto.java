package org.spring.divas.userservice.feature.manager;

import org.spring.divas.userservice.common.enums.ManagerRole;

public record VenueManagerResponseDto(
        Long userId,
        Long venueId,
        ManagerRole managerLevel
) {}
