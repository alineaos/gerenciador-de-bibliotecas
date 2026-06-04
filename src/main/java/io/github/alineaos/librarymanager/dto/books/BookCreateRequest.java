package io.github.alineaos.librarymanager.dto.books;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.Year;
import java.util.List;

public record BookCreateRequest(
        @NotBlank(message = "The field 'title' is required.")
        @Schema(description = "The book's title.", example = "Capitães da Areia")
        String title,

        @NotBlank(message = "The field 'author' is required.")
        @Schema(description = "The book's author name.", example = "Jorge Amado")
        String author,

        @NotBlank(message = "The field 'publisher' is required.")
        @Schema(description = "The book's publisher.", example = "Companhia das Letras")
        String publisher,

        @NotNull(message = "The field 'year' is required.")
        @PastOrPresent(message = "The year can not be in the future.")
        @Schema(description = "The book's publication year. Must be a year in the past or present.", example = "2008")
        Year year,

        @NotNull(message = "The field 'edition' is required.")
        @Positive(message = "The edition must be greater than or equal to 1.")
        @Schema(description = "The book's edition number. Must be a positive number.", example = "1")
        Integer edition,

        @NotBlank(message = "The field 'isbn' is required.")
        @Pattern(regexp = "^(\\d{10}|\\d{13})$", message = "ISBN must be 10 or 13 digits.")
        @Schema(description = "The book's ISBN code. Must be 10 or 13 digits.", example = "9788535911695")
        String isbn,

        @NotEmpty(message = "The field 'genreIds' is required.")
        @Schema(description = "The genres ids associated with the book.", example = "[1]")
        List<Long> genreIds
) {}
