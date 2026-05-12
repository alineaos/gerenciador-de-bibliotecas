package io.github.alineaos.librarymanager.dto.response;

import java.time.LocalDateTime;
import java.time.Year;

public record BookGetResponse(
        Long id,
        String title,
        String author,
        String publisher,
        Year year,
        Integer edition,
        String isbn,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
