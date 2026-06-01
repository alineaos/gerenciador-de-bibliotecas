package io.github.alineaos.librarymanager.util.factories.error;

import java.util.ArrayList;
import java.util.List;

public class UserErrorFactory {
    public static final String FULL_NAME_REQUIRED_ERROR = "The field 'fullName' is required.";
    public static final String EMAIL_REQUIRED_ERROR = "The field 'email' is required.";
    public static final String CPF_REQUIRED_ERROR = "The field 'cpf' is required.";
    public static final String BIRTH_DATE_REQUIRED_ERROR = "The field 'birthDate' is required.";
    public static final String PASSWORD_REQUIRED_ERROR = "The field 'password' is required.";

    public static final String CPF_NOT_VALID_ERROR = "The CPF is not valid.";
    public static final String EMAIL_NOT_VALID_ERROR = "The e-mail is not valid.";

    public static final String PASSWORD_MINIMUM_LENGTH_ERROR = "The password must have a minimum of 8 characters.";
    public static final String BIRTH_DATE_NOT_PAST_ERROR = "The birth date must be in the past.";


    public static List<String> allRequiredErrors() {
        return new ArrayList<>(List.of(FULL_NAME_REQUIRED_ERROR, EMAIL_REQUIRED_ERROR, CPF_REQUIRED_ERROR, BIRTH_DATE_REQUIRED_ERROR, PASSWORD_REQUIRED_ERROR));
    }

    public static List<String> allNotValidErrors() {
        return new ArrayList<>(List.of(CPF_NOT_VALID_ERROR, EMAIL_NOT_VALID_ERROR));
    }

    public static List<String> invalidFieldErrors() {
        return new ArrayList<>(List.of(EMAIL_NOT_VALID_ERROR, BIRTH_DATE_NOT_PAST_ERROR, PASSWORD_MINIMUM_LENGTH_ERROR));
    }

    public static List<String> allLoginFieldsErrors(){
        return new ArrayList<>(List.of(EMAIL_REQUIRED_ERROR, EMAIL_NOT_VALID_ERROR, PASSWORD_REQUIRED_ERROR));
    }
}
