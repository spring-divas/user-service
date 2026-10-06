package org.spring.divas.userservice.feature.allergy;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/allergy")
@RequiredArgsConstructor
public class UserAllergyController {

    private final UserAllergyService userAllergyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestParam Long userId, @RequestParam Long allergenId) {
        userAllergyService.create(userId, allergenId);
    }

    @GetMapping("/user/{userId}")
    public List<Long> findAllByUserId(@PathVariable Long userId) {
        return userAllergyService.findAllByUserId(userId);
    }

    @PostMapping("/user/{userId}/check")
    public boolean hasAnyAllergy(@PathVariable Long userId, @RequestBody List<Long> allergyIds) {
        return userAllergyService.hasAnyAllergy(userId, allergyIds);
    }

    @DeleteMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllByUserId(@PathVariable Long userId) {
        userAllergyService.deleteAllByUserId(userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam Long userId, @RequestParam Long allergenId) {
        userAllergyService.delete(userId, allergenId);
    }
}