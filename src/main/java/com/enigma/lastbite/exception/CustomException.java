package com.enigma.lastbite.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
    
    private final HttpStatus httpStatus;
    private final String message;
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        this.httpStatus = errorCode.getHttpStatus();
        this.message = errorCode.getMessage();
        this.errorCode = errorCode;
    }

    public CustomException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.errorCode = null;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ErrorCode getErrorCode() {
        return errorCode; // TAMBAHKAN METHOD INI
    }

}
