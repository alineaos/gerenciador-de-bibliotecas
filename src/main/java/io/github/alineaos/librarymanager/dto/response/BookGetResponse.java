package io.github.alineaos.librarymanager.dto.response;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

public record BookGetResponse(
        Long id,
        String title,
        String author,
        String publisher,
        Year year,
        Integer edition,
        String isbn,
        List<GenreBasicResponse> genres,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
