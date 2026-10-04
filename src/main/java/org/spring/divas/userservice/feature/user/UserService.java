package org.spring.divas.userservice.feature.user;

import org.spring.divas.userservice.common.enums.UserRole;

import java.util.List;

public interface UserService {

    UserResponseDto create(UserCreateDto dto, UserRole role);

    UserResponseDto findById(Long id);

    List<UserResponseDto> findAll();

    UserResponseDto update(Long id, UserUpdateDto dto);

    UserResponseDto updatePassword(Long id, ChangePasswordDto dto);

    void delete(Long id);
}
