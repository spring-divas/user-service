package org.spring.divas.userservice.feature.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank
    String email;

    @NotBlank
    String password;
}