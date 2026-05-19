package io.github.alineaos.librarymanager.dto.request;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.Year;

public record BookPatchRequest (
        String title,

        String author,

        String publisher,

        @PastOrPresent(message = "The year can not be in the future.")
        Year year,

        @Positive(message = "The edition must be greater than or equal to 1.")
        Integer edition,

        @Pattern(regexp = "^(\\d{10}|\\d{13})$", message = "ISBN must be 10 or 13 digits.")
        String isbn
){}
