package org.spring.divas.userservice.feature.allergy;

import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.annotations.RequiresUserOrAdmin;
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
    @RequiresUserOrAdmin(userId = "#userId")
    public void create(@RequestParam Long userId, @RequestParam Long allergenId) {
        userAllergyService.create(userId, allergenId);
    }

    @GetMapping("/user/{userId}")
    @RequiresUserOrAdmin(userId = "#userId")
    public List<Long> findAllByUserId(@PathVariable Long userId) {
        return userAllergyService.findAllByUserId(userId);
    }

    @PostMapping("/user/{userId}/check")
    @RequiresUserOrAdmin(userId = "#userId")
    public boolean hasAnyAllergy(@PathVariable Long userId, @RequestBody List<Long> allergyIds) {
        return userAllergyService.hasAnyAllergy(userId, allergyIds);
    }

    @DeleteMapping("/user/{userId}")
    @RequiresUserOrAdmin(userId = "#userId")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllByUserId(@PathVariable Long userId) {
        userAllergyService.deleteAllByUserId(userId);
    }

    @DeleteMapping
    @RequiresUserOrAdmin(userId = "#userId")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam Long userId, @RequestParam Long allergenId) {
        userAllergyService.delete(userId, allergenId);
    }
}