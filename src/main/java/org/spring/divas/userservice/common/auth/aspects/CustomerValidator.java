package org.spring.divas.userservice.common.auth.aspects;

import org.spring.divas.userservice.common.auth.annotations.RequiresCustomer;
import org.spring.divas.userservice.common.enums.UserRole;

public class CustomerValidator extends RoleValidator<RequiresCustomer> {
    @Override
    protected UserRole getUserRole() {
        return UserRole.CUSTOMER;
    }
}
