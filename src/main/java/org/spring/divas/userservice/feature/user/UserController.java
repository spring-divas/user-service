package org.spring.divas.userservice.feature.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.enums.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/customer")
    public UserResponseDto createCustomer(@Valid @RequestBody UserCreateDto dto) {
        return userService.create(dto, UserRole.CUSTOMER);
    }

    @PostMapping("/manager")
    public UserResponseDto createManager(@Valid @RequestBody UserCreateDto dto) {
        return userService.create(dto, UserRole.MANAGER);
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createAdmin(@Valid @RequestBody UserCreateDto dto) {
        return userService.create(dto, UserRole.ADMIN);
    }

    @GetMapping
    public List<UserResponseDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponseDto findById(@PathVariable Long id) {
        return userService.findById(id);
    }
}
