package org.spring.divas.userservice.feature.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spring.divas.userservice.common.enums.UserRole;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository repository;
    @Mock private UserMapper mapper;
    @Mock private org.springframework.security.crypto.password.PasswordEncoder encoder;
    @InjectMocks private UserServiceImpl service;

    @Test
    void createAssignsRoleAndReturnsSavedUser() {
        UserCreateDto request =
                new UserCreateDto("Test", "User", "user@example.com", "password123");
        User user = new User();
        UserResponseDto response = userResponse();
        when(mapper.toEntity(request)).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(mapper.toResponseDto(user)).thenReturn(response);

        assertThat(service.create(request, UserRole.CUSTOMER)).isSameAs(response);

        assertThat(user.getRole()).isEqualTo(UserRole.CUSTOMER);
        verify(repository).save(user);
    }

    @Test
    void findByIdMapsTheFoundUser() {
        User user = new User();
        UserResponseDto response = userResponse();
        when(repository.findById(7L)).thenReturn(Optional.of(user));
        when(mapper.toResponseDto(user)).thenReturn(response);

        assertThat(service.findById(7L)).isSameAs(response);
    }

    @Test
    void findByIdThrowsWhenUserDoesNotExist() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(7L))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }

    @Test
    void findAllMapsEveryUser() {
        User first = new User();
        User second = new User();
        UserResponseDto firstResponse = userResponse();
        UserResponseDto secondResponse = userResponse();
        when(repository.findAll()).thenReturn(List.of(first, second));
        when(mapper.toResponseDto(first)).thenReturn(firstResponse);
        when(mapper.toResponseDto(second)).thenReturn(secondResponse);

        assertThat(service.findAll()).containsExactly(firstResponse, secondResponse);
    }

    @Test
    void updateMutatesAndSavesExistingUser() {
        User user = new User();
        UserUpdateDto request = new UserUpdateDto("Test", "User", "user@example.com");
        UserResponseDto response = userResponse();
        when(repository.findById(7L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);
        when(mapper.toResponseDto(user)).thenReturn(response);

        assertThat(service.update(7L, request)).isSameAs(response);

        verify(mapper).updateEntity(request, user);
        verify(repository).save(user);
    }

    @Test
    void updatePasswordEncodesTheNewPassword() {
        User user = new User();
        final ChangePasswordDto request = new ChangePasswordDto("new-password");
        when(repository.findById(7L)).thenReturn(Optional.of(user));
        when(encoder.encode("new-password")).thenReturn("encoded-password");
        when(repository.save(user)).thenReturn(user);
        when(mapper.toResponseDto(user)).thenReturn(userResponse());

        service.updatePassword(7L, request);

        assertThat(user.getPassword()).isEqualTo("encoded-password");
        verify(repository).save(user);
    }

    @Test
    void deleteRequiresAnExistingUser() {
        when(repository.findById(7L)).thenReturn(Optional.of(new User()));

        service.delete(7L);

        verify(repository).deleteById(7L);
    }

    private static UserResponseDto userResponse() {
        return new UserResponseDto(7L, "Test", "User", "user@example.com", UserRole.CUSTOMER);
    }
}
