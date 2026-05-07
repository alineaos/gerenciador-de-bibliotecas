package io.github.alineaos.librarymanager.dto.response;

import java.time.LocalDateTime;

public record GenreGetResponse(
        Long id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
