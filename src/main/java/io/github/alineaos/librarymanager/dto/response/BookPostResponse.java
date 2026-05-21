package io.github.alineaos.librarymanager.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record BookPostResponse(
        Long id,
        String title,
        String author,
        String isbn,
        List<GenreBasicResponse> genres,
        LocalDateTime createdAt
) {}


