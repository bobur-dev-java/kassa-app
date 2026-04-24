package com.company.kassa.exceptions;

public class ApiException extends RuntimeException {
    private static final long serialVersionUID = -6619604892898952265L;

    private final String errorCode;
    private final String message;

    public ApiException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
