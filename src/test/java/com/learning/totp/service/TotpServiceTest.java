package com.learning.totp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.learning.totp.domain.TotpSeed;
import com.learning.totp.exception.TotpAccountNotFoundException;
import com.learning.totp.repository.TotpSeedRepository;
import com.learning.totp.web.dto.TotpGenerateResponse;
import com.learning.totp.web.dto.TotpVerifyResponse;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TotpServiceTest {

  private final TotpSeedRepository repository = mock(TotpSeedRepository.class);
  private final TotpService service = new TotpService(repository);

  @Test
  @DisplayName("generate() saves the new secret and returns a valid current code + otpauth URI")
  void generateSavesSeedAndReturnsCurrentCode() {
    TotpGenerateResponse response = service.generate("alice@example.com");

    verify(repository).upsert(eq("alice@example.com"), any());
    assertThat(response.getAccountName()).isEqualTo("alice@example.com");
    assertThat(response.getSecret()).isNotBlank();
    assertThat(response.getCurrentCode()).matches("\\d{6}");
    assertThat(response.getOtpAuthUri())
        .startsWith("otpauth://totp/")
        .contains("secret=" + response.getSecret())
        .contains("issuer=learning-utility");
  }

  @Test
  @DisplayName("verify() returns valid=true for the code the same generate() call just produced")
  void verifyAcceptsTheCodeGeneratedForTheSameSecret() {
    TotpGenerateResponse generated = service.generate("bob@example.com");
    when(repository.findByAccountName("bob@example.com"))
        .thenReturn(new TotpSeed(1L, "bob@example.com", generated.getSecret(), Instant.now()));

    TotpVerifyResponse result = service.verify("bob@example.com", generated.getCurrentCode());

    assertThat(result.getValid()).isTrue();
  }

  @Test
  @DisplayName("verify() returns valid=false for a wrong code")
  void verifyRejectsAWrongCode() {
    when(repository.findByAccountName("carol@example.com"))
        .thenReturn(new TotpSeed(1L, "carol@example.com", "JBSWY3DPEHPK3PXP", Instant.now()));

    TotpVerifyResponse result = service.verify("carol@example.com", "000000");

    assertThat(result.getValid()).isFalse();
  }

  @Test
  @DisplayName("verify() throws TotpAccountNotFoundException when no seed is saved for the account")
  void verifyThrowsWhenAccountUnknown() {
    when(repository.findByAccountName("unknown@example.com")).thenReturn(null);

    assertThatThrownBy(() -> service.verify("unknown@example.com", "123456"))
        .isInstanceOf(TotpAccountNotFoundException.class);
  }
}
