package io.github.alineaos.librarymanager.dto.response;

import java.time.LocalDateTime;

public record GenrePostResponse(
        Long id,
        LocalDateTime createdAt
) {}
