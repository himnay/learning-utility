package com.learning.totp.web.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A freshly generated TOTP seed and its current code, returned once for enrollment.
 */

@Schema(name = "TotpGenerateResponse", description = "A freshly generated TOTP seed and its current code, returned once for enrollment.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-22T19:22:29.344856+01:00[Europe/Dublin]", comments = "Generator version: 7.16.0")
public class TotpGenerateResponse {

  private String accountName;

  private String secret;

  private String currentCode;

  private String otpAuthUri;

  public TotpGenerateResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TotpGenerateResponse(String accountName, String secret, String currentCode, String otpAuthUri) {
    this.accountName = accountName;
    this.secret = secret;
    this.currentCode = currentCode;
    this.otpAuthUri = otpAuthUri;
  }

  public TotpGenerateResponse accountName(String accountName) {
    this.accountName = accountName;
    return this;
  }

  /**
   * Get accountName
   * @return accountName
   */
  @NotNull 
  @Schema(name = "accountName", example = "alice@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("accountName")
  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public TotpGenerateResponse secret(String secret) {
    this.secret = secret;
    return this;
  }

  /**
   * Base32-encoded seed. Persisted server-side; shown here only at enrollment time.
   * @return secret
   */
  @NotNull 
  @Schema(name = "secret", example = "JBSWY3DPEHPK3PXP", description = "Base32-encoded seed. Persisted server-side; shown here only at enrollment time.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("secret")
  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public TotpGenerateResponse currentCode(String currentCode) {
    this.currentCode = currentCode;
    return this;
  }

  /**
   * The 6-digit TOTP code valid right now for this seed (demo convenience — normally computed client-side by an authenticator app).
   * @return currentCode
   */
  @NotNull 
  @Schema(name = "currentCode", example = "123456", description = "The 6-digit TOTP code valid right now for this seed (demo convenience — normally computed client-side by an authenticator app).", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currentCode")
  public String getCurrentCode() {
    return currentCode;
  }

  public void setCurrentCode(String currentCode) {
    this.currentCode = currentCode;
  }

  public TotpGenerateResponse otpAuthUri(String otpAuthUri) {
    this.otpAuthUri = otpAuthUri;
    return this;
  }

  /**
   * otpauth:// URI suitable for rendering as a QR code in an authenticator app.
   * @return otpAuthUri
   */
  @NotNull 
  @Schema(name = "otpAuthUri", example = "otpauth://totp/learning-utility:alice@example.com?secret=JBSWY3DPEHPK3PXP&issuer=learning-utility", description = "otpauth:// URI suitable for rendering as a QR code in an authenticator app.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("otpAuthUri")
  public String getOtpAuthUri() {
    return otpAuthUri;
  }

  public void setOtpAuthUri(String otpAuthUri) {
    this.otpAuthUri = otpAuthUri;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TotpGenerateResponse totpGenerateResponse = (TotpGenerateResponse) o;
    return Objects.equals(this.accountName, totpGenerateResponse.accountName) &&
        Objects.equals(this.secret, totpGenerateResponse.secret) &&
        Objects.equals(this.currentCode, totpGenerateResponse.currentCode) &&
        Objects.equals(this.otpAuthUri, totpGenerateResponse.otpAuthUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountName, secret, currentCode, otpAuthUri);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TotpGenerateResponse {\n");
    sb.append("    accountName: ").append(toIndentedString(accountName)).append("\n");
    sb.append("    secret: ").append(toIndentedString(secret)).append("\n");
    sb.append("    currentCode: ").append(toIndentedString(currentCode)).append("\n");
    sb.append("    otpAuthUri: ").append(toIndentedString(otpAuthUri)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

