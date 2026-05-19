package io.github.alineaos.librarymanager.util;

import java.util.ArrayList;
import java.util.List;

public class BookErrorFactory {
    public static String titleRequiredError = "The field 'title' is required.";
    public static String authorRequiredError = "The field 'author' is required.";
    public static String yearRequiredError = "The field 'year' is required.";
    public static String isbnRequiredError = "The field 'isbn' is required.";

    public static String editionNotValidError = "The edition must be greater than or equal to 1.";
    public static String isbnNotValidError = "ISBN must be 10 or 13 digits.";

    public static String yearNotFutureError= "The year can not be in the future.";



    public static List<String> allRequiredErrors() {
        return new ArrayList<>(List.of(titleRequiredError, authorRequiredError, yearRequiredError, isbnRequiredError));
    }

    public static List<String> allNotValidErrors() {
        return new ArrayList<>(List.of(editionNotValidError, isbnNotValidError));
    }

    public static List<String> notValidAndYearNotFutureErrors() {
        return new ArrayList<>(List.of(editionNotValidError, isbnNotValidError, yearNotFutureError));
    }
}
