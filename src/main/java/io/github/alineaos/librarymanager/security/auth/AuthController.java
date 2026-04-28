package io.github.alineaos.librarymanager.security.auth;

import io.github.alineaos.librarymanager.dto.request.UserLoginRequest;
import io.github.alineaos.librarymanager.dto.response.UserLoginResponse;
import io.github.alineaos.librarymanager.security.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    @Value("${jwt.expiration-time}")
    private Long expiresIn;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.password()
        );

        Authentication authentication = authenticationManager.authenticate(authToken);

        String tokenValue = tokenService.generateToken(authentication);

        return ResponseEntity.ok(new UserLoginResponse(tokenValue, "Bearer", expiresIn));
    }
}
