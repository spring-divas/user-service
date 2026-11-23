package org.spring.divas.userservice.common.auth.factories;

import org.spring.divas.userservice.common.auth.authorizers.CustomerAuthorizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomerAuthorizerFactory {

    private CustomerAuthorizerFactory() {
        /* This utility class should not be instantiated */
    }


    public static CustomerAuthorizer isCustomer() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return new CustomerAuthorizer(auth);
    }
}
