package org.spring.divas.userservice.feature.manager;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.spring.divas.userservice.common.enums.ManagerRole;

@Data
public class VenueManagerCreateDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long venueId;

    @NotNull
    private ManagerRole managerLevel;
}