package org.spring.divas.userservice.common.auth.aspects;

import org.spring.divas.userservice.common.auth.annotations.RequiresAdmin;
import org.spring.divas.userservice.common.enums.UserRole;

public class AdminValidator extends RoleValidator<RequiresAdmin> {
    @Override
    protected UserRole getUserRole() {
        return UserRole.ADMIN;
    }
}
