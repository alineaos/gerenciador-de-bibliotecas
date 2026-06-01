package io.github.alineaos.librarymanager.dto.users;

public record UserLoginResponse(
        String accessToken,
        String tokenType,
        Long expiresIn
) {}
