package org.spring.divas.userservice.feature.manager;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venue-manager")
@RequiredArgsConstructor
public class VenueManagerController {

    private final VenueManagerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody VenueManagerCreateDto dto) {
        service.create(dto);
    }

    @GetMapping("/user/{userId}")
    public List<VenueManager> findAllByUserId(@PathVariable Long userId) {
        return service.findAllByUserId(userId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody VenueManagerUpdateDto dto) {
        service.update(dto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam Long userId, @RequestParam Long venueId) {
        service.delete(userId, venueId);
    }
}