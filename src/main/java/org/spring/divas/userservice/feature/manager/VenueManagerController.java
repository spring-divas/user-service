package org.spring.divas.userservice.feature.manager;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.annotations.RequiresManagerOrAdmin;
import org.spring.divas.userservice.common.annotations.RequiresUserManagerOrAdmin;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venue-manager")
@RequiredArgsConstructor
public class VenueManagerController {

    private final VenueManagerService service;

    @PostMapping
    @RequiresManagerOrAdmin(venueId = "#dto.venueId", level = ManagerRole.SENIOR)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody VenueManagerCreateDto dto) {
        service.create(dto);
    }

    @GetMapping
    @RequiresUserManagerOrAdmin(
            venueId = "#venueId",
            userId = "#userId",
            level = ManagerRole.SENIOR
    )
    public List<VenueManagerResponseDto> findAllByUserAndVenueId(
            @RequestParam Long userId,
            @RequestParam Long venueId) {
        return service.findAllByUserAndVenueId(userId, venueId);
    }

    @PutMapping
    @RequiresManagerOrAdmin(venueId = "#dto.venueId", level = ManagerRole.SENIOR)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody VenueManagerUpdateDto dto) {
        service.update(dto);
    }

    @DeleteMapping
    @RequiresManagerOrAdmin(venueId = "#venueId", level = ManagerRole.SENIOR)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam Long userId, @RequestParam Long venueId) {
        service.delete(userId, venueId);
    }
}