package org.spring.divas.userservice.feature.manager;

import jakarta.validation.constraints.NotNull;
import org.spring.divas.userservice.common.enums.ManagerRole;

public record VenueManagerUpdateDto(
        @NotNull Long userId,
        @NotNull Long venueId,
        @NotNull ManagerRole managerLevel
) {}
