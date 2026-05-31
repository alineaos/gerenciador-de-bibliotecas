package io.github.alineaos.librarymanager.util;

import java.util.ArrayList;
import java.util.List;

public class LoanErrorFactory {
    public static String userIdRequiredError = "The field 'userId' is required.";
    public static String bookIdRequiredError = "The field 'bookId' is required.";

    public static String userIdNotPositiveError = "The userId must be a positive number.";
    public static String bookIdNotPositiveError = "The bookId must be a positive number.";

    public static String borrowedAtNotPastDateError = "The field 'borrowedAt' must be today or a date in the past.";


    public static List<String> allRequiredErrors(){
        return new ArrayList<>(List.of(userIdRequiredError, bookIdRequiredError));
    }

    public static List<String> allNotValidErrors(){
        return new ArrayList<>(List.of(userIdNotPositiveError, bookIdNotPositiveError, borrowedAtNotPastDateError));
    }
}
