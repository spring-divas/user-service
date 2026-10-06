package org.spring.divas.userservice.feature.manager;

import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.auth.AuthChecker;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.spring.divas.userservice.common.auth.factories.AdminAuthorizerFactory.isAdmin;
import static org.spring.divas.userservice.common.auth.factories.AuthorizerCombinationFactory.either;
import static org.spring.divas.userservice.common.auth.factories.ManagerAuthorizerFactory.isManager;

@RestController
@RequestMapping("/venue-manager")
@RequiredArgsConstructor
public class VenueManagerController {

    private final VenueManagerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody VenueManagerCreateDto dto) throws AuthException {
        AuthChecker.require(either(isAdmin(), isManager(dto.getVenueId(), ManagerRole.SENIOR)));
        service.create(dto);
    }

    @GetMapping
    public List<VenueManagerResponseDto> findAllByUserAndVenueId(
            @RequestParam Long userId,
            @RequestParam Long venueId) {
        AuthChecker.require(either(isAdmin(), isManager(venueId, ManagerRole.JUNIOR)));
        return service.findAllByUserAndVenueId(userId, venueId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody VenueManagerUpdateDto dto) {
        AuthChecker.require(either(isAdmin(), isManager(dto.getVenueId(), ManagerRole.SENIOR)));
        service.update(dto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam Long userId, @RequestParam Long venueId) {
        AuthChecker.require(either(isAdmin(), isManager(venueId, ManagerRole.SENIOR)));
        service.delete(userId, venueId);
    }
}