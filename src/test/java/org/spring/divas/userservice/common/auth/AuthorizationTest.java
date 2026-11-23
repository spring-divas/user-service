package org.spring.divas.userservice.common.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.spring.divas.userservice.common.auth.authorizers.AdminAuthorizer;
import org.spring.divas.userservice.common.auth.authorizers.CustomerAuthorizer;
import org.spring.divas.userservice.common.auth.authorizers.ManagerAuthorizer;
import org.spring.divas.userservice.common.auth.authorizers.UserAuthorizer;
import org.spring.divas.userservice.common.auth.factories.AuthorizerCombinationFactory;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthorizationTest {
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void roleAuthorizersCheckPrefixedAuthoritiesAndExplainFailures() {
        var admin = authentication("42", "ROLE_ADMIN");
        assertThat(new AdminAuthorizer(admin).passes()).isTrue();
        assertThat(new CustomerAuthorizer(admin).passes()).isFalse();
        assertThat(new CustomerAuthorizer(admin).getErrorMsg())
                .containsExactly("User does not have role: CUSTOMER");
        assertThat(new AdminAuthorizer(null).passes()).isFalse();
        assertThat(new AdminAuthorizer(UsernamePasswordAuthenticationToken.unauthenticated(
                "42", "secret")).passes()).isFalse();
    }

    @Test
    void userAuthorizerMatchesIdentityAndManagerAuthorizerHonorsVenueAndLevel() {
        var owner = authentication("42", "ROLE_CUSTOMER");
        assertThat(new UserAuthorizer(owner, 42L).passes()).isTrue();
        assertThat(new UserAuthorizer(owner, 7L).passes()).isFalse();
        assertThat(new UserAuthorizer(null, 42L).passes()).isFalse();

        var manager = authentication("42", "ROLE_JUNIOR 8", "ROLE_SENIOR 9");
        assertThat(new ManagerAuthorizer(manager, 8L, ManagerRole.JUNIOR).passes()).isTrue();
        assertThat(new ManagerAuthorizer(manager, 8L, ManagerRole.SENIOR).passes()).isFalse();
        assertThat(new ManagerAuthorizer(manager, 9L, ManagerRole.JUNIOR).passes()).isTrue();
        assertThat(new ManagerAuthorizer(manager, 10L, ManagerRole.JUNIOR).passes()).isFalse();
        assertThat(new ManagerAuthorizer(null, 8L, ManagerRole.JUNIOR).getErrorMsg())
                .containsExactly("User does not have manager role: JUNIOR for venue 8");
    }

    @Test
    void combinationsAggregateErrorsAndHandleEmptyInputs() {
        var denied = new CustomerAuthorizer(null);
        var allowed = new AdminAuthorizer(authentication("42", "ROLE_ADMIN"));
        assertThat(AuthorizerCombinationFactory.both(denied, allowed).passes()).isFalse();
        assertThat(AuthorizerCombinationFactory.both(denied, allowed).getErrorMsg())
                .containsExactly("User does not have role: CUSTOMER");
        assertThat(AuthorizerCombinationFactory.either(denied, allowed).passes()).isTrue();
        assertThat(AuthorizerCombinationFactory.either(denied, allowed).getErrorMsg()).isEmpty();
        assertThat(AuthorizerCombinationFactory.any(denied, new UserAuthorizer(null, 1L))
                .getErrorMsg()).hasSize(2);
        assertThat(AuthorizerCombinationFactory.all().passes()).isTrue();
        assertThat(AuthorizerCombinationFactory.any().passes()).isFalse();
        assertThat(AuthorizerCombinationFactory.any().getErrorMsg())
                .containsExactly("No authorizers provided");
    }

    @Test
    void requireRejectsDeniedAuthenticatedUserAndAllowsAuthorizedOrAnonymous() {
        SecurityContextHolder.getContext().setAuthentication(authentication("42", "ROLE_USER"));
        CustomerAuthorizer authorizer = new CustomerAuthorizer(null);
        assertThatThrownBy(() -> AuthChecker.require(authorizer))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("User does not have role: CUSTOMER");
        AuthChecker.require(new AdminAuthorizer(authentication("42", "ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "test-key", "anonymousUser",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
        AuthChecker.require(new CustomerAuthorizer(null));
    }

    private static UsernamePasswordAuthenticationToken authentication(
            String name,
            String... roles
    ) {
        return UsernamePasswordAuthenticationToken.authenticated(
                name, "", List.of(roles).stream()
                .map(SimpleGrantedAuthority::new).toList());
    }
}
