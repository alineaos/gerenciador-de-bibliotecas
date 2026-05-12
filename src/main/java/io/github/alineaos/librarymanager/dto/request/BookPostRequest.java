package io.github.alineaos.librarymanager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.Year;

public record BookPostRequest(
        @NotBlank(message = "The field 'title' is required.")
        String title,

        @NotBlank(message = "The field 'author' is required.")
        String author,

        @NotBlank(message = "The field 'publisher' is required.")
        String publisher,

        @NotNull(message = "The field 'year' is required.")
        @PastOrPresent(message = "The year can not be in the future.")
        Year year,

        @NotNull(message = "The field 'edition' is required.")
        @Positive(message = "The edition must be greater than or equal to 1.")
        Integer edition,

        @NotBlank(message = "The field 'isbn' is required.")
        @Pattern(regexp = "^(\\d{10}|\\d{13})$", message = "ISBN must be 10 or 13 digits.")
        String isbn
) {}
