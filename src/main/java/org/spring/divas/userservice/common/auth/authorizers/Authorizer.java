package org.spring.divas.userservice.common.auth.authorizers;

import java.util.List;

public abstract class Authorizer {

    public abstract boolean passes();

    public abstract List<String> getErrorMsg();
}