package com.learning.totp.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CodeGenerationFailedExceptionTest {

  @Test
  void keepsItsOwnMessageAndTheCause() {
    Exception cause = new Exception("HMAC failed");

    CodeGenerationFailedException ex = new CodeGenerationFailedException("Failed to generate the current TOTP code", cause);

    assertThat(ex.getMessage()).isEqualTo("Failed to generate the current TOTP code");
    assertThat(ex.getCause()).isSameAs(cause);
  }
}
