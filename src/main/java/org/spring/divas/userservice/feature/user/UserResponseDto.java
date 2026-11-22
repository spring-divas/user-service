package org.spring.divas.userservice.feature.user;

import org.spring.divas.userservice.common.enums.UserRole;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        UserRole role
) {}
