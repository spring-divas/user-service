package org.spring.divas.userservice.common.auth.aspects;

import org.spring.divas.userservice.common.auth.annotations.IsManager;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.spring.divas.userservice.feature.auth.VenueManagerRole;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ManagerValidator implements CheckValidator<IsManager> {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public boolean validate(IsManager annotation) {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        Expression expression = parser.parseExpression(annotation.venueId());
        Object venueId = expression.getValue();
        ManagerRole requiredLevel = annotation.requiredLevel();
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        for (String str : roles) {
            VenueManagerRole role = VenueManagerRole.fromString(str);
            if (role == null || !Objects.equals(venueId, role.getVenueId())) {
                continue;
            }
            return switch (role.getLevel()) {
                case SENIOR -> true;
                case JUNIOR -> requiredLevel == ManagerRole.JUNIOR;
            };
        }
        return false;

    }
}