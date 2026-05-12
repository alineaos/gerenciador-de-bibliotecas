package io.github.alineaos.librarymanager.dto.request;

import java.time.Year;

public record BookPatchRequest (
        String title,
        String author,
        String publisher,
        Year year,
        Integer edition,
        String isbn
){}
