package io.github.alineaos.librarymanager.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserBasicResponse(
        @Schema(description = "The user's unique id.", example = "1")
        Long id,

        @Schema(description = "The user's full name.", example = "Maria Silva")
        String fullName,

        @Schema(description = "The user's e-mail.", example = "maria.silva@testes.com")
        String email
){}
