package org.spring.divas.userservice.feature.allergy;

import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.auth.AuthChecker;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.spring.divas.userservice.common.auth.factories.AdminAuthorizerFactory.isAdmin;
import static org.spring.divas.userservice.common.auth.factories.AuthorizerCombinationFactory.both;
import static org.spring.divas.userservice.common.auth.factories.AuthorizerCombinationFactory.either;
import static org.spring.divas.userservice.common.auth.factories.CustomerAuthorizerFactory.isCustomer;
import static org.spring.divas.userservice.common.auth.factories.UserAuthorizerFactory.isUser;

@RestController
@RequestMapping("/allergy")
@RequiredArgsConstructor
public class UserAllergyController {

    private final UserAllergyService userAllergyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestParam Long userId, @RequestParam Long allergenId) throws AuthException {
        AuthChecker.require(either(isAdmin(), both(isCustomer(), isUser(userId))));
        userAllergyService.create(userId, allergenId);
    }

    @GetMapping("/user/{userId}")
    public List<Long> findAllByUserId(@PathVariable Long userId) {
        AuthChecker.require(either(isAdmin(), both(isCustomer(), isUser(userId))));
        return userAllergyService.findAllByUserId(userId);
    }

    @PostMapping("/user/{userId}/check")
    public boolean hasAnyAllergy(@PathVariable Long userId, @RequestBody List<Long> allergyIds) {
        AuthChecker.require(either(isAdmin(), both(isCustomer(), isUser(userId))));
        return userAllergyService.hasAnyAllergy(userId, allergyIds);
    }

    @DeleteMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllByUserId(@PathVariable Long userId) {
        AuthChecker.require(either(isAdmin(), both(isCustomer(), isUser(userId))));
        userAllergyService.deleteAllByUserId(userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam Long userId, @RequestParam Long allergenId) {
        AuthChecker.require(either(isAdmin(), both(isCustomer(), isUser(userId))));
        userAllergyService.delete(userId, allergenId);
    }
}