package org.spring.divas.userservice.common.auth.aspects;

import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.auth.annotations.Check;
import org.spring.divas.userservice.common.auth.annotations.RequiresAny;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class RequiresAnyValidator implements CheckValidator<RequiresAny> {

    private final ApplicationContext applicationContext;

    @Override
    public boolean validate(RequiresAny annotation) {
        return Arrays.stream(annotation.value())
                .anyMatch(this::validateCheck);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean validateCheck(Check check) {
        CheckValidator validator = applicationContext.getBean(check.validator());
        return validator.validate(check);
    }
}