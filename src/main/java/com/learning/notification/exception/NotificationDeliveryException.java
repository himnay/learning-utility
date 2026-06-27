package com.learning.notification.exception;

/** Thrown when submitting a push notification to APNs fails (network error, timeout, etc). */
public class NotificationDeliveryException extends RuntimeException {

  public NotificationDeliveryException(String message, Throwable cause) {
    super(message, cause);
  }
}
