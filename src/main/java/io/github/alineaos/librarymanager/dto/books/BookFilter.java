package io.github.alineaos.librarymanager.dto.books;

import org.springframework.web.bind.annotation.BindParam;

import java.time.Year;

public record BookFilter (
        String title,
        String author,
        String publisher,
        Year year,
        Integer edition,
        String isbn,
        @BindParam("genre") String genreName
){}
