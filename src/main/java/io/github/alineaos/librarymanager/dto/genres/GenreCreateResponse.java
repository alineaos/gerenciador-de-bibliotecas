package io.github.alineaos.librarymanager.dto.genres;

import java.time.LocalDateTime;
public record GenreCreateResponse(
        Long id,
        String name,
        LocalDateTime createdAt
) {}
