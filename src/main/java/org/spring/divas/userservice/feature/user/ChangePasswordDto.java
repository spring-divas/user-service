package org.spring.divas.userservice.feature.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {

    @NotBlank
    @Size(min = 8, max = 255)
    private String newPassword;
}
