package com.odontogama.auth.domain.exception;

public enum UserErrorCode {

    INVALID_NAME("USER_001", "name is invalid"),
    INVALID_EMAIL("USER_002", "email is invalid"),
    USER_NOT_FOUND("USER_003", "user not found"),
    INVALID_PASSWORD("USER_004", "password is invalid"),
    EMAIL_ALREADY_EXISTS("USER_005", "email already exists");

    private final String code;
    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
