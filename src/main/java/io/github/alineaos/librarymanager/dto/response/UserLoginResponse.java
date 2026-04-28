package io.github.alineaos.librarymanager.dto.response;

public record UserLoginResponse(
        String accessToken,
        String tokenType,
        Long expiresIn
) {}
