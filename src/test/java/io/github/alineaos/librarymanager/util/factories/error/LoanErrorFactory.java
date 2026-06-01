package io.github.alineaos.librarymanager.util.factories.error;

import java.util.ArrayList;
import java.util.List;

public class LoanErrorFactory {
    public static final String USER_ID_REQUIRED_ERROR = "The field 'userId' is required.";
    public static final String BOOK_ID_REQUIRED_ERROR = "The field 'bookId' is required.";

    public static final String USER_ID_NOT_POSITIVE_ERROR = "The userId must be a positive number.";
    public static final String BOOK_ID_NOT_POSITIVE_ERROR = "The bookId must be a positive number.";

    public static final String BORROWED_AT_NOT_PAST_DATE_ERROR = "The field 'borrowedAt' must be today or a date in the past.";


    public static List<String> allRequiredErrors(){
        return new ArrayList<>(List.of(USER_ID_REQUIRED_ERROR, BOOK_ID_REQUIRED_ERROR));
    }

    public static List<String> allNotValidErrors(){
        return new ArrayList<>(List.of(USER_ID_NOT_POSITIVE_ERROR, BOOK_ID_NOT_POSITIVE_ERROR, BORROWED_AT_NOT_PAST_DATE_ERROR));
    }
}
