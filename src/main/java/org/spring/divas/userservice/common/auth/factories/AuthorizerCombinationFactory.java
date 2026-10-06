package org.spring.divas.userservice.common.auth.factories;

import org.spring.divas.userservice.common.auth.authorizers.Authorizer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class AuthorizerCombinationFactory {

    public static Authorizer both(Authorizer a1, Authorizer a2) {
        Objects.requireNonNull(a1);
        Objects.requireNonNull(a2);

        return new Authorizer() {

            @Override
            public boolean passes() {
                return a1.passes() && a2.passes();
            }

            @Override
            public List<String> getErrorMsg() {
                return Stream.of(a1, a2)
                        .filter(a -> !a.passes())
                        .flatMap(a -> a.getErrorMsg().stream())
                        .toList();
            }
        };
    }

    public static Authorizer either(Authorizer a1, Authorizer a2) {
        Objects.requireNonNull(a1);
        Objects.requireNonNull(a2);

        return new Authorizer() {

            @Override
            public boolean passes() {
                return a1.passes() || a2.passes();
            }

            @Override
            public List<String> getErrorMsg() {
                if (passes()) {
                    return List.of();
                }

                return Stream.of(a1, a2)
                        .flatMap(a -> a.getErrorMsg().stream())
                        .toList();
            }
        };
    }

    public static Authorizer all(Authorizer... authorizers) {
        Objects.requireNonNull(authorizers);

        if (authorizers.length == 0) {
            return new Authorizer() {
                @Override
                public boolean passes() {
                    return true;
                }

                @Override
                public List<String> getErrorMsg() {
                    return List.of();
                }
            };
        }

        return Arrays.stream(authorizers)
                .map(Objects::requireNonNull)
                .reduce(AuthorizerCombinationFactory::both)
                .orElseThrow();
    }

    public static Authorizer any(Authorizer... authorizers) {
        Objects.requireNonNull(authorizers);

        if (authorizers.length == 0) {
            return new Authorizer() {
                @Override
                public boolean passes() {
                    return false;
                }

                @Override
                public List<String> getErrorMsg() {
                    return List.of("No authorizers provided");
                }
            };
        }

        return Arrays.stream(authorizers)
                .map(Objects::requireNonNull)
                .reduce(AuthorizerCombinationFactory::either)
                .orElseThrow();
    }
}