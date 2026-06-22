package com.learning.common.web;

import com.learning.common.web.dto.ApiError;
import com.learning.qr.exception.QrDecodeException;
import com.learning.totp.exception.TotpAccountNotFoundException;
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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .findFirst()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .orElse("Validation failed");
    return ResponseEntity.badRequest().body(error(HttpStatus.BAD_REQUEST, message));
  }

  @ExceptionHandler(QrDecodeException.class)
  public ResponseEntity<ApiError> handleQrDecode(QrDecodeException ex) {
    return ResponseEntity.badRequest().body(error(HttpStatus.BAD_REQUEST, ex.getMessage()));
  }

  @ExceptionHandler(TotpAccountNotFoundException.class)
  public ResponseEntity<ApiError> handleTotpAccountNotFound(TotpAccountNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(HttpStatus.NOT_FOUND, ex.getMessage()));
  }

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
