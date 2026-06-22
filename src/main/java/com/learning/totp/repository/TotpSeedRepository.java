package com.learning.totp.repository;

import com.learning.totp.domain.TotpSeed;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Plain {@link JdbcTemplate} persistence for {@code totp_seed}. */
@Repository
@RequiredArgsConstructor
public class TotpSeedRepository {

  private final JdbcTemplate jdbc;

  /** Inserts a new seed, or replaces the existing one for this account (re-enrollment rotates the secret). */
  public void upsert(String accountName, String secret) {
    jdbc.update(
        """
        INSERT INTO totp_seed (account_name, secret) VALUES (?, ?)
        ON CONFLICT (account_name) DO UPDATE SET secret = EXCLUDED.secret, created_at = NOW()
        """,
        accountName,
        secret);
  }

  /** Returns {@code null} when no seed exists for this account (not an exception). */
  public TotpSeed findByAccountName(String accountName) {
    List<TotpSeed> matches =
        jdbc.query(
            "SELECT id, account_name, secret, created_at FROM totp_seed WHERE account_name = ?",
            (rs, rowNum) ->
                new TotpSeed(
                    rs.getLong("id"),
                    rs.getString("account_name"),
                    rs.getString("secret"),
                    toInstant(rs.getTimestamp("created_at"))),
            accountName);
    return matches.isEmpty() ? null : matches.get(0);
  }

  private static Instant toInstant(Timestamp timestamp) {
    return timestamp != null ? timestamp.toInstant() : null;
  }
}
