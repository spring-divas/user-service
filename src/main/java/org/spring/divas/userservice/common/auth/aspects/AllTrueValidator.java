package org.spring.divas.userservice.common.auth.aspects;

import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.auth.annotations.RequiresAllTrue;
import org.spring.divas.userservice.common.auth.annotations.Check;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AllTrueValidator implements CheckValidator<RequiresAllTrue> {

    private final ApplicationContext applicationContext;

    @Override
    public boolean validate(RequiresAllTrue annotation) {
        return Arrays.stream(annotation.value())
                .allMatch(this::validateCheck);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean validateCheck(Check check) {
        CheckValidator validator = applicationContext.getBean(check.validator());
        return validator.validate(check);
    }
}