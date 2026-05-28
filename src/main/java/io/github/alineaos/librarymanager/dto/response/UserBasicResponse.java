package io.github.alineaos.librarymanager.dto.response;

public record UserBasicResponse(
        Long id,
        String fullName,
        String email
){}
