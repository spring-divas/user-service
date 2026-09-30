package org.spring.divas.userservice.feature.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final RSAPublicKey publicKey;

    @PostMapping("/token")
    public String token(@Valid @RequestBody AuthRequest request) {
        return authService.authenticate(request);
    }

    @GetMapping("/public-key")
    public String publicKey() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}