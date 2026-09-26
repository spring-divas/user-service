package org.spring.divas.userservice.feature.manager;

import org.springframework.stereotype.Component;

@Component
public class VenueManagerMapper {

    public VenueManager toEntity(VenueManagerCreateDto dto) {
        return VenueManager.builder()
                .userId(dto.getUserId())
                .venueId(dto.getVenueId())
                .managerLevel(dto.getManagerLevel())
                .build();
    }
}
