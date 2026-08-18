package com.learning.common.web;

import com.learning.common.web.dto.ApiError;
import com.learning.notification.exception.NotificationConfigurationException;
import com.learning.notification.exception.NotificationDeliveryException;
import com.learning.qr.exception.QrDecodeException;
import com.learning.qr.exception.QrEncodeException;
import com.learning.totp.exception.TotpAccountNotFoundException;
import com.learning.totp.exception.TotpQrGenerationException;
import com.learning.totp.exception.TotpRateLimitExceededException;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps every exception that escapes a controller to a consistent {@link ApiError} JSON body. */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Handles validation. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .orElse("Validation failed");
    return ResponseEntity.badRequest().body(error(HttpStatus.BAD_REQUEST, message));
  }

  /** Handles qr decode. */
  @ExceptionHandler(QrDecodeException.class)
  public ResponseEntity<ApiError> handleQrDecode(QrDecodeException ex) {
    return ResponseEntity.badRequest().body(error(HttpStatus.BAD_REQUEST, ex.getMessage()));
  }

  /** Handles qr encode. */
  @ExceptionHandler(QrEncodeException.class)
  public ResponseEntity<ApiError> handleQrEncode(QrEncodeException ex) {
    return ResponseEntity.badRequest().body(error(HttpStatus.BAD_REQUEST, ex.getMessage()));
  }

  /** Handles totp account not found. */
  @ExceptionHandler(TotpAccountNotFoundException.class)
  public ResponseEntity<ApiError> handleTotpAccountNotFound(TotpAccountNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(HttpStatus.NOT_FOUND, ex.getMessage()));
  }

  /** Handles totp rate limit. */
  @ExceptionHandler(TotpRateLimitExceededException.class)
  public ResponseEntity<ApiError> handleTotpRateLimit(TotpRateLimitExceededException ex) {
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(error(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage()));
  }

  /** Handles totp qr generation. */
  @ExceptionHandler(TotpQrGenerationException.class)
  public ResponseEntity<ApiError> handleTotpQrGeneration(TotpQrGenerationException ex) {
    log.error("LEARNING_UTILITY | QR generation failed | {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
  }

  /** Handles notification configuration. */
  @ExceptionHandler(NotificationConfigurationException.class)
  public ResponseEntity<ApiError> handleNotificationConfiguration(NotificationConfigurationException ex) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage()));
  }

  /** Handles notification delivery. */
  @ExceptionHandler(NotificationDeliveryException.class)
  public ResponseEntity<ApiError> handleNotificationDelivery(NotificationDeliveryException ex) {
    log.error("LEARNING_UTILITY | APNs delivery failed | {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error(HttpStatus.BAD_GATEWAY, ex.getMessage()));
  }

  /** Handles generic. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex) {
    log.error("LEARNING_UTILITY | unhandled error | {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
  }

  private static ApiError error(HttpStatus status, String message) {
    return new ApiError(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message);
  }
}
