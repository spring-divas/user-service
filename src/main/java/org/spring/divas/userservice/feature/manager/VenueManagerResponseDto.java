package org.spring.divas.userservice.feature.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.spring.divas.userservice.common.enums.ManagerRole;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueManagerResponseDto {

    private Long userId;

    private Long venueId;

    private ManagerRole managerLevel;
}