package io.github.alineaos.librarymanager.dto;

import java.time.Year;

public record BookFilter (
        String title,
        String author,
        String publisher,
        Year year,
        Integer edition,
        String isbn
){}
