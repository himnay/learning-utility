package com.learning.totp.exception;

/** Thrown when verifying a code for an account that has no seed saved yet. */
public class TotpAccountNotFoundException extends RuntimeException {

  public TotpAccountNotFoundException(String accountName) {
    super("No TOTP seed found for account '" + accountName + "' — call /totp/generate first");
  }
}
