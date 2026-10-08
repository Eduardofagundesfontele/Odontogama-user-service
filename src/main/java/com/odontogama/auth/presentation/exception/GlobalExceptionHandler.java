package com.odontogama.auth.presentation.exception;

import com.odontogama.auth.domain.exception.UserErrorCode;
import com.odontogama.auth.domain.exception.UserException;
import com.odontogama.auth.presentation.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException exception, HttpServletRequest request){

        HttpStatus status = resolveStatus(exception.getErrorCode());

        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                status.value(),
                exception.getErrorCode().getCode(),
                exception.getErrorCode().getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>  handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ){

        List<ErrorResponse.FieldError> errors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map((FieldError error) -> new ErrorResponse.FieldError(error.getField(),error.getDefaultMessage()))
                .toList();

        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Validation failed",
                request.getRequestURI(),
                errors
                );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ){

        log.error(
                "Unexpected error while processing request: {}",
                request.getRequestURI(),
                exception
        );

        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

    }

    private HttpStatus resolveStatus(UserErrorCode errorCode) {

        return switch (errorCode) {

            case USER_NOT_FOUND ->
                    HttpStatus.NOT_FOUND;

            case EMAIL_ALREADY_EXISTS ->
                    HttpStatus.CONFLICT;

            case INVALID_NAME,
                 INVALID_EMAIL,
                 INVALID_PASSWORD ->
                    HttpStatus.BAD_REQUEST;
        };
    }
}
