package org.spring.divas.userservice.feature.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(
        @NotBlank @Size(min = 8, max = 255) String newPassword
) {}
