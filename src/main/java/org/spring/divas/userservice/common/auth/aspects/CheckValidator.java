package org.spring.divas.userservice.common.auth.aspects;

import java.lang.annotation.Annotation;

public interface CheckValidator<A extends Annotation> {

    boolean validate(A annotation);
}