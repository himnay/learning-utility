package com.learning.totp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.learning.totp.crypto.TotpSecretCipher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(
    classes = {
      DataSourceAutoConfiguration.class,
      JdbcTemplateAutoConfiguration.class,
      FlywayAutoConfiguration.class,
      TotpSecretCipher.class,
      TotpSeedRepository.class
    },
    properties = "totp.secret-encryption-key=tVaztoUpCpUI5bxM28kEJ0blIoHZ7BHNn9vW4qS3YWw=")
class TotpSeedRepositoryTest {

  @Container @ServiceConnection static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @Autowired private TotpSeedRepository repository;
  @Autowired private JdbcTemplate jdbc;

  @Test
  @DisplayName("upsert() then findByAccountName() round-trips a seed")
  void upsertThenFind() {
    repository.upsert("dave@example.com", "JBSWY3DPEHPK3PXP");

    var seed = repository.findByAccountName("dave@example.com");

    assertThat(seed).isNotNull();
    assertThat(seed.accountName()).isEqualTo("dave@example.com");
    assertThat(seed.secret()).isEqualTo("JBSWY3DPEHPK3PXP");
    assertThat(seed.createdAt()).isNotNull();
  }

  @Test
  @DisplayName("upsert() called again for the same account rotates the secret instead of duplicating the row")
  void upsertRotatesSecretOnConflict() {
    repository.upsert("erin@example.com", "OLDSECRET00000000");
    repository.upsert("erin@example.com", "NEWSECRET00000000");

    var seed = repository.findByAccountName("erin@example.com");

    assertThat(seed.secret()).isEqualTo("NEWSECRET00000000");
  }

  @Test
  @DisplayName("findByAccountName() returns null (not an exception) when nothing is saved")
  void findReturnsNullWhenMissing() {
    assertThat(repository.findByAccountName("nobody@example.com")).isNull();
  }

  @Test
  @DisplayName("secret is encrypted at rest — the raw DB column never contains the plaintext")
  void secretIsEncryptedAtRest() {
    String plaintext = "JBSWY3DPEHPK3PXP";
    repository.upsert("frank@example.com", plaintext);

    String rawColumnValue =
        jdbc.queryForObject(
            "SELECT secret FROM totp_seed WHERE account_name = ?", String.class, "frank@example.com");

    assertThat(rawColumnValue).isNotEqualTo(plaintext);
    assertThat(repository.findByAccountName("frank@example.com").secret()).isEqualTo(plaintext);
  }
}
