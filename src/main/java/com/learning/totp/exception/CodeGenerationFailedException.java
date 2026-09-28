package com.learning.totp.exception;

/** Wraps a checked {@code CodeGenerationException} from the TOTP library's {@code CodeGenerator}. */
public class CodeGenerationFailedException extends RuntimeException {

    public CodeGenerationFailedException(String message, Exception cause) {
        super(message, cause);
    }
}
