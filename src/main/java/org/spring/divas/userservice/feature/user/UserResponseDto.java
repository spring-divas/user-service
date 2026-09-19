package org.spring.divas.userservice.feature.user;

import lombok.Data;
import org.spring.divas.userservice.common.enums.UserRole;

@Data
public class UserResponseDto {

    private Long id;

    private String name;

    private String email;

    private UserRole role;
}