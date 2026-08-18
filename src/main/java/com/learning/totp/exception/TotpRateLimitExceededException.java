package com.learning.totp.exception;

/** Thrown when an account has too many recent failed verify attempts — mapped to 429. */
public class TotpRateLimitExceededException extends RuntimeException {
  public TotpRateLimitExceededException(String accountName) {
    super("Too many failed verification attempts for account '" + accountName + "' — try again later");
  }
}
