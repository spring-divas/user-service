package org.spring.divas.userservice.feature.manager;

import java.util.List;

public interface VenueManagerService {

    void create(VenueManagerCreateDto dto);

    List<VenueManagerResponseDto> findAllByUserAndVenueId(Long userId, Long venueId);

    void update(VenueManagerUpdateDto dto);

    void delete(Long userId, Long venueId);
}