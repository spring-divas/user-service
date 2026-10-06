package org.spring.divas.userservice.common.auth.aspects;

import org.spring.divas.userservice.common.auth.annotations.IsAdmin;
import org.spring.divas.userservice.common.enums.UserRole;

public class AdminValidator extends RoleValidator<IsAdmin> {
    @Override
    protected UserRole getUserRole() {
        return UserRole.ADMIN;
    }
}
