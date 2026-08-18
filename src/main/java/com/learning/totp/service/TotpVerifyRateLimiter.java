package com.learning.totp.service;

import com.learning.totp.exception.TotpRateLimitExceededException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * In-memory sliding-window lockout for {@code /totp/verify} — a 6-digit code is a 1e6 search
 * space, so verification must not be uncapped. Per-account: after {@value #MAX_FAILURES} failed
 * attempts inside {@value #WINDOW_MINUTES} minutes, further attempts are rejected until the
 * oldest failure ages out of the window.
 *
 * <p>Per-process only — fine for this demo's single instance, not for a multi-instance
 * deployment (would need a shared store, e.g. Redis).
 */
@Component
public class TotpVerifyRateLimiter {

  static final int MAX_FAILURES = 5;
  static final long WINDOW_MINUTES = 5;

  private final Clock clock;
  private final ConcurrentHashMap<String, Deque<Instant>> failuresByAccount = new ConcurrentHashMap<>();

  public TotpVerifyRateLimiter() {
    this(Clock.systemUTC());
  }

  TotpVerifyRateLimiter(Clock clock) {
    this.clock = clock;
  }

  /** Throws if this account has hit the failure limit within the current window. */
  public void checkAllowed(String accountName) {
    Deque<Instant> failures = failuresByAccount.get(accountName);
    if (failures == null) {
      return;
    }
    synchronized (failures) {
      purgeExpired(failures);
      if (failures.size() >= MAX_FAILURES) {
        throw new TotpRateLimitExceededException(accountName);
      }
    }
  }

  /** Records a failed verify attempt against the account's window. */
  public void recordFailure(String accountName) {
    Deque<Instant> failures = failuresByAccount.computeIfAbsent(accountName, key -> new ArrayDeque<>());
    synchronized (failures) {
      purgeExpired(failures);
      failures.addLast(clock.instant());
    }
  }

  /** A successful verify clears the account's failure history. */
  public void recordSuccess(String accountName) {
    failuresByAccount.remove(accountName);
  }

  private void purgeExpired(Deque<Instant> failures) {
    Instant cutoff = clock.instant().minus(Duration.ofMinutes(WINDOW_MINUTES));
    while (!failures.isEmpty() && failures.peekFirst().isBefore(cutoff)) {
      failures.pollFirst();
    }
  }
}
