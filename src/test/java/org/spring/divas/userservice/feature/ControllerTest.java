package org.spring.divas.userservice.feature;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.spring.divas.userservice.common.enums.UserRole;
import org.spring.divas.userservice.feature.allergy.UserAllergyController;
import org.spring.divas.userservice.feature.allergy.UserAllergyService;
import org.spring.divas.userservice.feature.manager.VenueManagerController;
import org.spring.divas.userservice.feature.manager.VenueManagerCreateDto;
import org.spring.divas.userservice.feature.manager.VenueManagerResponseDto;
import org.spring.divas.userservice.feature.manager.VenueManagerService;
import org.spring.divas.userservice.feature.manager.VenueManagerUpdateDto;
import org.spring.divas.userservice.feature.user.UserController;
import org.spring.divas.userservice.feature.user.UserCreateDto;
import org.spring.divas.userservice.feature.user.UserResponseDto;
import org.spring.divas.userservice.feature.user.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControllerTest {
    @BeforeEach
    void authenticateAsAdmin() {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("1", "",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void userControllerDelegatesCreationAndReads() {
        UserService service = mock(UserService.class);
        UserController controller = new UserController(service);
        UserCreateDto create = new UserCreateDto("Ava", "Jones", "ava@example.com", "password123");
        UserResponseDto response = new UserResponseDto(3L, "Ava", "Jones", "ava@example.com",
                UserRole.CUSTOMER);
        when(service.create(create, UserRole.CUSTOMER)).thenReturn(response);
        when(service.create(create, UserRole.MANAGER)).thenReturn(response);
        when(service.create(create, UserRole.ADMIN)).thenReturn(response);
        when(service.findAll()).thenReturn(List.of(response));
        when(service.findById(3L)).thenReturn(response);

        assertThat(controller.createCustomer(create)).isSameAs(response);
        assertThat(controller.createManager(create)).isSameAs(response);
        assertThat(controller.createAdmin(create)).isSameAs(response);
        assertThat(controller.findAll()).containsExactly(response);
        assertThat(controller.findById(3L)).isSameAs(response);
        verify(service).create(create, UserRole.CUSTOMER);
        verify(service).create(create, UserRole.MANAGER);
        verify(service).create(create, UserRole.ADMIN);
    }

    @Test
    void allergyControllerDelegatesEveryOperation() {
        UserAllergyService service = mock(UserAllergyService.class);
        UserAllergyController controller = new UserAllergyController(service);
        when(service.findAllByUserId(3L)).thenReturn(List.of(8L, 9L));
        when(service.hasAnyAllergy(3L, List.of(8L))).thenReturn(true);

        controller.create(3L, 8L);
        assertThat(controller.findAllByUserId(3L)).containsExactly(8L, 9L);
        assertThat(controller.hasAnyAllergy(3L, List.of(8L))).isTrue();
        controller.deleteAllByUserId(3L);
        controller.delete(3L, 8L);

        verify(service).create(3L, 8L);
        verify(service).deleteAllByUserId(3L);
        verify(service).delete(3L, 8L);
    }

    @Test
    void venueManagerControllerDelegatesEveryOperation() {
        VenueManagerResponseDto response = new VenueManagerResponseDto(3L, 8L, ManagerRole.SENIOR);
        VenueManagerService service = mock(VenueManagerService.class);
        when(service.findAllByUserAndVenueId(3L, 8L)).thenReturn(List.of(response));

        VenueManagerCreateDto create = new VenueManagerCreateDto(3L, 8L, ManagerRole.SENIOR);
        VenueManagerUpdateDto update = new VenueManagerUpdateDto(3L, 8L, ManagerRole.JUNIOR);
        VenueManagerController controller = new VenueManagerController(service);
        controller.create(create);
        assertThat(controller.findAllByUserAndVenueId(3L, 8L)).containsExactly(response);
        controller.update(update);
        controller.delete(3L, 8L);

        verify(service).create(create);
        verify(service).update(update);
        verify(service).delete(3L, 8L);
    }
}
