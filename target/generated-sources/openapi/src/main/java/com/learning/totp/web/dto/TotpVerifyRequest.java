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
 * TotpVerifyRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-22T19:22:29.344856+01:00[Europe/Dublin]", comments = "Generator version: 7.16.0")
public class TotpVerifyRequest {

  private String accountName;

  private String code;

  public TotpVerifyRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TotpVerifyRequest(String accountName, String code) {
    this.accountName = accountName;
    this.code = code;
  }

  public TotpVerifyRequest accountName(String accountName) {
    this.accountName = accountName;
    return this;
  }

  /**
   * Get accountName
   * @return accountName
   */
  @NotNull @Size(min = 1, max = 100) 
  @Schema(name = "accountName", example = "alice@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("accountName")
  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  public TotpVerifyRequest code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  @NotNull @Pattern(regexp = "^[0-9]{6}$") @Size(min = 6, max = 6) 
  @Schema(name = "code", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TotpVerifyRequest totpVerifyRequest = (TotpVerifyRequest) o;
    return Objects.equals(this.accountName, totpVerifyRequest.accountName) &&
        Objects.equals(this.code, totpVerifyRequest.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountName, code);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TotpVerifyRequest {\n");
    sb.append("    accountName: ").append(toIndentedString(accountName)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
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

