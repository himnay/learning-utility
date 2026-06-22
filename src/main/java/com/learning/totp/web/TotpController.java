package com.learning.totp.web;

import com.learning.totp.service.TotpService;
import com.learning.totp.web.dto.TotpGenerateRequest;
import com.learning.totp.web.dto.TotpGenerateResponse;
import com.learning.totp.web.dto.TotpVerifyRequest;
import com.learning.totp.web.dto.TotpVerifyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** {@code /totp} — generate and verify time-based one-time codes (RFC 6238). */
@RestController
@RequestMapping("/totp")
@RequiredArgsConstructor
@Tag(name = "TOTP")
public class TotpController {

  private final TotpService totpService;

  @PostMapping("/generate")
  @Operation(
      operationId = "generateTotpSeed",
      summary = "Generate and persist a new TOTP seed",
      description =
          "Creates a new Base32 secret for the given account, saves it in Postgres, and returns it "
              + "alongside the code that's valid right now and an otpauth:// enrollment URI.")
  public TotpGenerateResponse generate(@Valid @RequestBody TotpGenerateRequest request) {
    return totpService.generate(request.getAccountName());
  }

  @PostMapping("/verify")
  @Operation(
      operationId = "verifyTotpCode",
      summary = "Verify a 6-digit code against the saved seed",
      description = "Looks up the account's persisted seed and checks whether the supplied code is valid right now.")
  public TotpVerifyResponse verify(@Valid @RequestBody TotpVerifyRequest request) {
    return totpService.verify(request.getAccountName(), request.getCode());
  }
}
