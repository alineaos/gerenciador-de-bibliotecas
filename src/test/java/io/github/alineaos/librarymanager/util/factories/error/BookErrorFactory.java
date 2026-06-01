package io.github.alineaos.librarymanager.util.factories.error;

import java.util.ArrayList;
import java.util.List;

public class BookErrorFactory {
    public static final String TITLE_REQUIRED_ERROR = "The field 'title' is required.";
    public static final String AUTHOR_REQUIRED_ERROR = "The field 'author' is required.";
    public static final String YEAR_REQUIRED_ERROR = "The field 'year' is required.";
    public static final String EDITION_REQUIRED_ERROR = "The field 'edition' is required.";
    public static final String ISBN_REQUIRED_ERROR = "The field 'isbn' is required.";
    public static final String GENRE_IDS_REQUIRED_ERROR = "The field 'genreIds' is required.";

    public static final String ISBN_NOT_VALID_ERROR = "ISBN must be 10 or 13 digits.";
    public static final String EDITION_NOT_POSITIVE_ERROR = "The edition must be greater than or equal to 1.";
    public static final String YEAR_NOT_FUTURE_ERROR = "The year can not be in the future.";



    public static List<String> allRequiredErrors() {
        return new ArrayList<>(List.of(TITLE_REQUIRED_ERROR, AUTHOR_REQUIRED_ERROR, YEAR_REQUIRED_ERROR,
                EDITION_REQUIRED_ERROR, ISBN_REQUIRED_ERROR, GENRE_IDS_REQUIRED_ERROR));
    }

    public static List<String> allInvalidFieldsErrors() {
        return new ArrayList<>(List.of(EDITION_NOT_POSITIVE_ERROR, ISBN_NOT_VALID_ERROR, YEAR_NOT_FUTURE_ERROR));
    }
}
