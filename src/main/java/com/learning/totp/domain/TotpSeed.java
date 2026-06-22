package com.learning.totp.domain;

import java.time.Instant;

/** A persisted TOTP seed — the row stored in Postgres for a given account. */
public record TotpSeed(Long id, String accountName, String secret, Instant createdAt) {}
