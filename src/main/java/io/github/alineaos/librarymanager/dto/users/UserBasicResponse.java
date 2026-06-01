package io.github.alineaos.librarymanager.dto.users;

public record UserBasicResponse(
        Long id,
        String fullName,
        String email
){}
