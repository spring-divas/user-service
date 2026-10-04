package org.spring.divas.userservice.feature.user;

import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final UserMapper mapper;

    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public UserResponseDto create(UserCreateDto dto, UserRole role) {
        User user = mapper.toEntity(dto);
        user.setRole(role);
        return mapper.toResponseDto(repository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        return mapper.toResponseDto(repository.findById(id).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto dto) {
        User user = repository.findById(id).orElseThrow();
        mapper.updateEntity(dto, user);
        return mapper.toResponseDto(repository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDto updatePassword(Long id, ChangePasswordDto dto) {
        User user = repository.findById(id).orElseThrow();
        user.setPassword(encoder.encode(dto.getNewPassword()));
        return mapper.toResponseDto(repository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.findById(id).orElseThrow();
        repository.deleteById(id);
    }
}
