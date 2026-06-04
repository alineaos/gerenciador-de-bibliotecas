package io.github.alineaos.librarymanager.dto.books;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.Year;
import java.util.List;

public record BookUpdateRequest(
        @Schema(description = "The book's title.", example = "Capitães da Areia")
        String title,

        @Schema(description = "The book's author name.", example = "Jorge Amado")
        String author,

        @Schema(description = "The book's publisher.", example = "Companhia das Letras")
        String publisher,

        @PastOrPresent(message = "The year can not be in the future.")
        @Schema(description = "The book's publication year. Must be a year in the past or present.", example = "2008")
        Year year,

        @Positive(message = "The edition must be greater than or equal to 1.")
        @Schema(description = "The book's edition number. Must be a positive number.", example = "1")
        Integer edition,

        @Pattern(regexp = "^(\\d{10}|\\d{13})$", message = "ISBN must be 10 or 13 digits.")
        @Schema(description = "The book's ISBN code. Must be 10 or 13 digits.", example = "9788535911695")
        String isbn,

        @Schema(description = "The genres ids associated with the book.", example = "[1]")
        List<Long> genreIds
){}
