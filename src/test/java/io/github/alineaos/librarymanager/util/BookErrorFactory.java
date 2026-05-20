package io.github.alineaos.librarymanager.util;

import java.util.ArrayList;
import java.util.List;

public class BookErrorFactory {
    public static String titleRequiredError = "The field 'title' is required.";
    public static String authorRequiredError = "The field 'author' is required.";
    public static String yearRequiredError = "The field 'year' is required.";
    public static String editionRequiredError = "The field 'edition' is required.";
    public static String isbnRequiredError = "The field 'isbn' is required.";

    public static String isbnNotValidError = "ISBN must be 10 or 13 digits.";
    public static String editionNotPositiveError = "The edition must be greater than or equal to 1.";
    public static String yearNotFutureError= "The year can not be in the future.";



    public static List<String> allRequiredErrors() {
        return new ArrayList<>(List.of(titleRequiredError, authorRequiredError, yearRequiredError, editionRequiredError, isbnRequiredError));
    }

    public static List<String> allInvalidFieldsErrors() {
        return new ArrayList<>(List.of(editionNotPositiveError, isbnNotValidError, yearNotFutureError));
    }
}
