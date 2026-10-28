package org.spring.divas.userservice.feature.user;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.divas.userservice.common.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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
    @Observed(name = "user.get", contextualName = "get-user")
    public UserResponseDto findById(Long id) {
        log.info("Getting user by id={}", id);
        User user = repository.findById(id).orElseThrow();
        log.info("User found with id={}", id);
        return mapper.toResponseDto(user);
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
