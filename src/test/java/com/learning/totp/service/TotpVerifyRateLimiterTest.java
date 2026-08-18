package com.learning.totp.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.learning.totp.exception.TotpRateLimitExceededException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class TotpVerifyRateLimiterTest {

  @Test
  void allowsAttemptsBelowTheFailureThreshold() {
    TotpVerifyRateLimiter limiter = new TotpVerifyRateLimiter();

    for (int i = 0; i < TotpVerifyRateLimiter.MAX_FAILURES - 1; i++) {
      limiter.recordFailure("alice@example.com");
    }

    assertThatCode(() -> limiter.checkAllowed("alice@example.com")).doesNotThrowAnyException();
  }

  @Test
  void blocksOnceTheFailureThresholdIsReached() {
    TotpVerifyRateLimiter limiter = new TotpVerifyRateLimiter();

    for (int i = 0; i < TotpVerifyRateLimiter.MAX_FAILURES; i++) {
      limiter.recordFailure("bob@example.com");
    }

    assertThatThrownBy(() -> limiter.checkAllowed("bob@example.com"))
        .isInstanceOf(TotpRateLimitExceededException.class);
  }

  @Test
  void aSuccessClearsTheFailureHistory() {
    TotpVerifyRateLimiter limiter = new TotpVerifyRateLimiter();

    for (int i = 0; i < TotpVerifyRateLimiter.MAX_FAILURES; i++) {
      limiter.recordFailure("carol@example.com");
    }
    limiter.recordSuccess("carol@example.com");

    assertThatCode(() -> limiter.checkAllowed("carol@example.com")).doesNotThrowAnyException();
  }

  @Test
  void failuresOutsideTheWindowDoNotCount() {
    Instant base = Instant.parse("2026-01-01T00:00:00Z");
    MutableClock clock = new MutableClock(base);
    TotpVerifyRateLimiter limiter = new TotpVerifyRateLimiter(clock);

    for (int i = 0; i < TotpVerifyRateLimiter.MAX_FAILURES; i++) {
      limiter.recordFailure("dave@example.com");
    }
    clock.advance(TotpVerifyRateLimiter.WINDOW_MINUTES + 1);

    assertThatCode(() -> limiter.checkAllowed("dave@example.com")).doesNotThrowAnyException();
  }

  /** A {@link Clock} whose instant can be advanced mid-test. */
  private static final class MutableClock extends Clock {
    private Instant now;

    MutableClock(Instant now) {
      this.now = now;
    }

    void advance(long minutes) {
      now = now.plusSeconds(minutes * 60);
    }

    @Override
    public ZoneOffset getZone() {
      return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(java.time.ZoneId zone) {
      return this;
    }

    @Override
    public Instant instant() {
      return now;
    }
  }
}
