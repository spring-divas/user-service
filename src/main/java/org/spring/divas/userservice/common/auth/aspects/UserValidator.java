package org.spring.divas.userservice.common.auth.aspects;

import org.spring.divas.userservice.common.auth.annotations.IsTheUser;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserValidator implements CheckValidator<IsTheUser> {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public boolean validate(IsTheUser annotation) {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (auth == null || auth.isAuthenticated()) {
            return false;
        }
        Expression expression = parser.parseExpression(annotation.userId());
        Object requiredUserId = expression.getValue();
        Long actualUserId = Long.parseLong(auth.getName());
        return actualUserId.equals(requiredUserId);
    }
}