package org.spring.divas.userservice.feature.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserResponseDto create(UserCreateDto dto) {
        User savedUser = repository.save(mapper.toEntity(dto));
        return mapper.toResponseDto(savedUser);
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
        user.setPassword(dto.getNewPassword());
        return mapper.toResponseDto(repository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.findById(id).orElseThrow();
        repository.deleteById(id);
    }
}
