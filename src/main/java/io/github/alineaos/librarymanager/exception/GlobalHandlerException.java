package io.github.alineaos.librarymanager.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.github.alineaos.librarymanager.dto.error.DefaultMessageError;
import io.github.alineaos.librarymanager.dto.error.ValidationMessageError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DefaultMessageError> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = "Duplicated entry for one or more unique fields";

        String specificMessage = e.getMostSpecificCause().getMessage().toLowerCase();

        if (specificMessage.contains("cpf")) {
            message = "Duplicated entry for cpf field";
        }

        if (specificMessage.contains("email")) {
            message = "Duplicated entry for e-mail field";
        }

        DefaultMessageError error = new DefaultMessageError(
                HttpStatus.CONFLICT.value(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationMessageError> handleConstraintViolationException(ConstraintViolationException e) {
        String message = "Some fields could not be validated in controller layer.";

        List<ValidationMessageError.FieldError> errors = e.getConstraintViolations().stream()
                .map(violation -> new ValidationMessageError.FieldError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage())
                )
                .toList();

        return buildValidationErrorResponse(message, errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationMessageError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = "Some fields could not be validated in service layer.";

        List<ValidationMessageError.FieldError> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationMessageError.FieldError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        return buildValidationErrorResponse(message, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DefaultMessageError> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String message = "The JSON could not be read. One or more fields are invalids.";

        if (e.getCause() instanceof InvalidFormatException ex){
            String invalidValue = ex.getValue().toString();
            String fieldName = ex.getPath().getFirst().getFieldName();
            message = "The value '%s' is invalid for the field '%s'.".formatted(invalidValue, fieldName);
        }

        DefaultMessageError error = new DefaultMessageError(HttpStatus.BAD_REQUEST.value(), message, LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    private ResponseEntity<ValidationMessageError> buildValidationErrorResponse(String message, List<ValidationMessageError.FieldError> errors) {
        ValidationMessageError error = new ValidationMessageError(HttpStatus.BAD_REQUEST.value(),
                message,
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
