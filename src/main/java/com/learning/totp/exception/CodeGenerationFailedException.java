package com.learning.totp.exception;

public class CodeGenerationFailedException extends RuntimeException {

    private String message;

    public CodeGenerationFailedException(String message, Exception exception) {
        super(exception);
        this.message = message;
    }

}
