package com.learning.totp.service;

import com.learning.totp.domain.TotpSeed;
import com.learning.totp.exception.CodeGenerationFailedException;
import com.learning.totp.exception.TotpAccountNotFoundException;
import com.learning.totp.exception.TotpQrGenerationException;
import com.learning.totp.repository.TotpSeedRepository;
import com.learning.totp.web.dto.TotpGenerateResponse;
import com.learning.totp.web.dto.TotpVerifyResponse;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.CodeGenerationException;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Generates and verifies time-based one-time codes (RFC 6238) using dev.samstevens.totp. Seeds
 * are persisted in Postgres ({@link TotpSeedRepository}) keyed by account name.
 */
@Slf4j
@Service
public class TotpService {

  private static final String ISSUER = "learning-utility";
  private static final int DIGITS = 6;
  private static final int PERIOD_SECONDS = 30;

  private final TotpSeedRepository seedRepository;
  private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
  private final TimeProvider timeProvider = new SystemTimeProvider();
  private final CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1, DIGITS);
  private final CodeVerifier codeVerifier = buildCodeVerifier();
  private final QrGenerator qrGenerator = new ZxingPngQrGenerator();

  public TotpService(TotpSeedRepository seedRepository) {
    this.seedRepository = seedRepository;
  }

  private CodeVerifier buildCodeVerifier() {
    DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    verifier.setTimePeriod(PERIOD_SECONDS);
    verifier.setAllowedTimePeriodDiscrepancy(1);
    return verifier;
  }

  public TotpGenerateResponse generate(String accountName) {
    String secret = secretGenerator.generate();
    seedRepository.upsert(accountName, secret);
    log.info("TOTP | seed generated and saved | accountName={}", accountName);

    String currentCode = currentCode(secret);
    QrData qrData = buildQrData(accountName, secret);

    return new TotpGenerateResponse()
        .accountName(accountName)
        .secret(secret)
        .currentCode(currentCode)
        .otpAuthUri(qrData.getUri());
  }

  public TotpVerifyResponse verify(String accountName, String code) {
    TotpSeed seed = findSeedOrThrow(accountName);
    boolean valid = codeVerifier.isValidCode(seed.secret(), code);
    log.info("TOTP | verify | accountName={} | valid={}", accountName, valid);
    return new TotpVerifyResponse().valid(valid);
  }

  /** Renders the saved seed's enrollment URI as a scannable QR code PNG. */
  public TotpQrCodeImage generateQrCodeImage(String accountName) {
    TotpSeed seed = findSeedOrThrow(accountName);
    QrData qrData = buildQrData(accountName, seed.secret());
    try {
      byte[] image = qrGenerator.generate(qrData);
      log.info("TOTP | QR enrollment image rendered | accountName={}", accountName);
      return new TotpQrCodeImage(image, qrGenerator.getImageMimeType());
    } catch (QrGenerationException e) {
      throw new TotpQrGenerationException("Failed to render the enrollment QR code", e);
    }
  }

  private TotpSeed findSeedOrThrow(String accountName) {
    TotpSeed seed = seedRepository.findByAccountName(accountName);
    if (seed == null) {
      throw new TotpAccountNotFoundException(accountName);
    }
    return seed;
  }

  private String currentCode(String secret) {
    try {
      long counter = timeProvider.getTime() / PERIOD_SECONDS;
      return codeGenerator.generate(secret, counter);
    } catch (CodeGenerationException e) {
      throw new CodeGenerationFailedException("Failed to generate the current TOTP code", e);
    }
  }

  private QrData buildQrData(String accountName, String secret) {
    return new QrData.Builder()
        .label(accountName)
        .secret(secret)
        .issuer(ISSUER)
        .algorithm(HashingAlgorithm.SHA1)
        .digits(DIGITS)
        .period(PERIOD_SECONDS)
        .build();
  }
}
