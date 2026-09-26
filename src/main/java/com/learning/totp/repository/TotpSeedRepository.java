package com.learning.totp.repository;

import com.learning.totp.crypto.TotpSecretCipher;
import com.learning.totp.domain.TotpSeed;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Plain {@link JdbcTemplate} persistence for {@code totp_seed}. The secret is encrypted at rest
 * (AES-GCM via {@link TotpSecretCipher}) — {@code upsert} takes/{@code findByAccountName} returns
 * plaintext, the ciphertext never leaves this class.
 */
@Repository
public class TotpSeedRepository {

  private final JdbcTemplate jdbc;
  private final TotpSecretCipher cipher;

  public TotpSeedRepository(JdbcTemplate jdbc, TotpSecretCipher cipher) {
    this.jdbc = jdbc;
    this.cipher = cipher;
  }

  /** Inserts a new seed, or replaces the existing one for this account (re-enrollment rotates the secret). */
  public void upsert(String accountName, String secret) {
    jdbc.update(
        """
        INSERT INTO totp_seed (account_name, secret) VALUES (?, ?)
        ON CONFLICT (account_name) DO UPDATE SET secret = EXCLUDED.secret, created_at = NOW(), last_used_step = NULL
        """,
        accountName,
        cipher.encrypt(secret));
  }

  /**
   * Records {@code timeStep} as the latest accepted code for {@code accountName}, atomically and
   * only if it is newer than the previous one. {@code false} means that code (or an older one) was
   * already used — a replay.
   */
  public boolean markStepUsed(String accountName, long timeStep) {
    return jdbc.update(
            "UPDATE totp_seed SET last_used_step = ? WHERE account_name = ? AND (last_used_step IS NULL OR last_used_step < ?)",
            timeStep,
            accountName,
            timeStep)
        == 1;
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
                    cipher.decrypt(rs.getString("secret")),
                    toInstant(rs.getTimestamp("created_at"))),
            accountName);
    return matches.isEmpty() ? null : matches.get(0);
  }

  private static Instant toInstant(Timestamp timestamp) {
    return timestamp != null ? timestamp.toInstant() : null;
  }
}
