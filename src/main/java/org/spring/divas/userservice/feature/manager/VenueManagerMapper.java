package org.spring.divas.userservice.feature.manager;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VenueManagerMapper {

    VenueManager toEntity(VenueManagerCreateDto request);

    VenueManagerResponseDto toResponseDto(VenueManager user);
}
