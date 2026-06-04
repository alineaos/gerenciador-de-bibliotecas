package io.github.alineaos.librarymanager.security.auth;

import io.github.alineaos.librarymanager.dto.errors.DefaultMessageError;
import io.github.alineaos.librarymanager.dto.errors.ValidationMessageError;
import io.github.alineaos.librarymanager.dto.users.UserLoginRequest;
import io.github.alineaos.librarymanager.dto.users.UserLoginResponse;
import io.github.alineaos.librarymanager.security.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/auth")
@SecurityRequirement(name = "")
@Tag(name = "Authentication", description = "Login and authentication related endpoints.")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    @Value("${jwt.expiration-time}")
    private Long expiresIn;

    @PostMapping("/login")
    @Operation(summary = "Authenticate a user", description = "Authenticates a user in the system and generates a JWT Token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully. Returns the access token.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserLoginResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized. Invalid credentials.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DefaultMessageError.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid fields.",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(oneOf = {DefaultMessageError.class, ValidationMessageError.class}))
            )
    })
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest request) {
        log.info("Authentication: Processing login request");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        );

        Authentication authentication = authenticationManager.authenticate(authToken);

        String tokenValue = tokenService.generateToken(authentication);

        log.debug("Authentication: User authenticated successfully. JWT Token generated.");

        return ResponseEntity.ok(new UserLoginResponse(tokenValue, "Bearer", expiresIn));
    }
}
