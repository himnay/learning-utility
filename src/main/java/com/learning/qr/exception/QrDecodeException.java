package com.learning.qr.exception;

/** Thrown when an uploaded image can't be decoded as a QR code (unreadable image, no code found). */
public class QrDecodeException extends RuntimeException {

  public QrDecodeException(String message) {
    super(message);
  }

  public QrDecodeException(String message, Throwable cause) {
    super(message, cause);
  }
}
