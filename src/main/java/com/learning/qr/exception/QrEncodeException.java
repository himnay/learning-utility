package com.learning.qr.exception;

/** Thrown when text can't be rendered as a QR code (e.g. too long for the requested size). */
public class QrEncodeException extends RuntimeException {

  public QrEncodeException(String message) {
    super(message);
  }

  public QrEncodeException(String message, Throwable cause) {
    super(message, cause);
  }
}
