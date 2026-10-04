package org.spring.divas.userservice.feature.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.annotations.RequiresAdmin;
import org.spring.divas.userservice.common.annotations.RequiresUserOrAdmin;
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
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createCustomer(@Valid @RequestBody UserCreateDto dto) {
        return userService.create(dto, UserRole.CUSTOMER);
    }

    @PostMapping("/manager")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createManager(@Valid @RequestBody UserCreateDto dto) {
        return userService.create(dto, UserRole.MANAGER);
    }

    @PostMapping("/admin")
    @RequiresAdmin
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createAdmin(@Valid @RequestBody UserCreateDto dto) {
        return userService.create(dto, UserRole.ADMIN);
    }

    @GetMapping
    @RequiresAdmin
    public List<UserResponseDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @RequiresUserOrAdmin(userId = "#id")
    public UserResponseDto findById(@PathVariable Long id) {
        return userService.findById(id);
    }
}
