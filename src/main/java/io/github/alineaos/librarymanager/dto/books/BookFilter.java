package io.github.alineaos.librarymanager.dto.books;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.BindParam;

import java.time.Year;

public record BookFilter (
        @Schema(description = "Filter books by their title or part of it.", example = "Cap")
        String title,

        @Schema(description = "Filter books by their author name or part of it.", example = "Jorg")
        String author,

        @Schema(description = "Filter books by their publisher or part of it.", example = "Compa")
        String publisher,

        @Schema(description = "Filter books by their publication year.", example = "2008")
        Year year,

        @Schema(description = "Filter books by their edition year.", example = "1")
        Integer edition,

        @Schema(description = "Filter books by their ISBN code or part of it.", example = "9788535")
        String isbn,

        @Schema(description = "Filter books by their associated genre name or part of it.", example = "Romanc")
        @BindParam("genre") String genreName
){}
