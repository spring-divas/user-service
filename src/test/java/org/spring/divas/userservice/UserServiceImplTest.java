package org.spring.divas.userservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spring.divas.userservice.common.enums.UserRole;
import org.spring.divas.userservice.feature.user.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encoded_pass")
                .role(UserRole.CUSTOMER)
                .build();

        responseDto = new UserResponseDto(
                1L, "John", "Doe", "john.doe@example.com", UserRole.CUSTOMER
        );
    }

    @Test
    void create_shouldSaveUserAndReturnDto() {
        UserCreateDto createDto = new UserCreateDto("John", "Doe", "john.doe@example.com", "password123");
        when(mapper.toEntity(createDto)).thenReturn(user);
        when(repository.save(user)).thenReturn(user);
        when(mapper.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.create(createDto, UserRole.CUSTOMER);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("john.doe@example.com");
        verify(repository).save(user);
    }

    @Test
    void findById_shouldReturnUserWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.firstName()).isEqualTo("John");
    }

    @Test
    void findById_shouldThrowExceptionWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void findAll_shouldReturnListOfUsers() {
        when(repository.findAll()).thenReturn(List.of(user));
        when(mapper.toResponseDto(user)).thenReturn(responseDto);

        List<UserResponseDto> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().email()).isEqualTo("john.doe@example.com");
    }

    @Test
    void update_shouldUpdateAndReturnUser() {
        UserUpdateDto updateDto = new UserUpdateDto("Jane", "Doe", "jane.doe@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);
        when(mapper.toResponseDto(user)).thenReturn(new UserResponseDto(1L, "Jane", "Doe", "jane.doe@example.com", UserRole.CUSTOMER));

        UserResponseDto result = userService.update(1L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Jane");
        verify(mapper).updateEntity(updateDto, user);
        verify(repository).save(user);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        UserUpdateDto updateDto = new UserUpdateDto("Jane", "Doe", "jane.doe@example.com");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, updateDto))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updatePassword_shouldEncodeAndSaveNewPassword() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("newSecret123");
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(encoder.encode("newSecret123")).thenReturn("new_encoded_pass");
        when(repository.save(user)).thenReturn(user);
        when(mapper.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.updatePassword(1L, changePasswordDto);

        assertThat(result).isNotNull();
        assertThat(user.getPassword()).isEqualTo("new_encoded_pass");
        verify(repository).save(user);
    }

    @Test
    void updatePassword_shouldThrowWhenNotFound() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("newSecret123");
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePassword(99L, changePasswordDto))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void delete_shouldDeleteWhenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
