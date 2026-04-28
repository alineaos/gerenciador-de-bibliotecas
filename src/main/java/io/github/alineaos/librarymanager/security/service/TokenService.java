package io.github.alineaos.librarymanager.security.service;

import io.github.alineaos.librarymanager.security.domain.UserAuthenticated;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder encoder;
    @Value("${jwt.expiration-time}")
    private long expiresIn;

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        String scopes = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("library-manager-api")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .subject(authentication.getName())
                .claim("scope", scopes);

        if (authentication.getPrincipal() instanceof UserAuthenticated user){
            claims.claim("userId", user.getId());
        }

        return encoder.encode(JwtEncoderParameters.from(claims.build())).getTokenValue();
    }
}
