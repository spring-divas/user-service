package org.spring.divas.userservice.feature.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserResponseDto create(UserCreateDto dto) {
        User savedUser = repository.save(mapper.toEntity(dto));
        return mapper.toResponseDto(savedUser);
    }

    @Override
    public UserResponseDto findById(Long id) {
        return mapper.toResponseDto(repository.findById(id).orElseThrow());
    }

    @Override
    public List<UserResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto update(Long id, UserUpdateDto dto) {
        User user = repository.findById(id).orElseThrow();
        mapper.updateEntity(dto, user);
        return mapper.toResponseDto(repository.save(user));
    }

    @Override
    public UserResponseDto updatePassword(Long id, ChangePasswordDto dto) {
        User user = repository.findById(id).orElseThrow();
        user.setPassword(dto.getNewPassword());
        return mapper.toResponseDto(repository.save(user));
    }

    @Override
    public void delete(Long id) {
        repository.findById(id).orElseThrow();
        repository.deleteById(id);
    }
}
