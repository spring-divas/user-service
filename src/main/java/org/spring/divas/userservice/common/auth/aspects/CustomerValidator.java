package org.spring.divas.userservice.common.auth.aspects;

import org.spring.divas.userservice.common.auth.annotations.IsCustomer;
import org.spring.divas.userservice.common.enums.UserRole;

public class CustomerValidator extends RoleValidator<IsCustomer> {
    @Override
    protected UserRole getUserRole() {
        return UserRole.CUSTOMER;
    }
}
