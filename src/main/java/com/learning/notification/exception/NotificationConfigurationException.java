package com.learning.notification.exception;

/** Thrown when APNs is disabled/unconfigured at call time, or its credentials fail to load at startup. */
public class NotificationConfigurationException extends RuntimeException {

  public NotificationConfigurationException(String message) {
    super(message);
  }

  public NotificationConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
