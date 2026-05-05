package io.github.alineaos.librarymanager.util;

import java.util.ArrayList;
import java.util.List;

public class UserErrorFactory {
    public static String fullNameRequiredError = "The field 'fullName' is required.";
    public static String emailRequiredError = "The field 'email' is required.";
    public static String cpfRequiredError = "The field 'cpf' is required.";
    public static String birthDateRequiredError = "The field 'birthDate' is required.";
    public static String passwordRequiredError = "The field 'password' is required.";

    public static String cpfNotValidError = "The CPF is not valid.";
    public static String emailNotValidError = "The e-mail is not valid.";

    public static String birthDateNotPastError = "The birth date must be in the past.";


    public static List<String> allRequiredErrors() {
        return new ArrayList<>(List.of(fullNameRequiredError, emailRequiredError, cpfRequiredError, birthDateRequiredError, passwordRequiredError));
    }

    public static List<String> allNotValidErrors() {
        return new ArrayList<>(List.of(cpfNotValidError, emailNotValidError));
    }

    public static List<String> emailNotValidAndDateNotPastErrors() {
        return new ArrayList<>(List.of(emailNotValidError, birthDateNotPastError));
    }
}
