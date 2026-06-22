package com.learning.totp.exception;

/** Thrown when rendering the otpauth:// enrollment URI as a QR code image fails. */
public class TotpQrGenerationException extends RuntimeException {

  public TotpQrGenerationException(String message, Throwable cause) {
    super(message, cause);
  }
}
