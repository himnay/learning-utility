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
 * TotpGenerateRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-22T19:22:29.344856+01:00[Europe/Dublin]", comments = "Generator version: 7.16.0")
public class TotpGenerateRequest {

  private String accountName;

  public TotpGenerateRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TotpGenerateRequest(String accountName) {
    this.accountName = accountName;
  }

  public TotpGenerateRequest accountName(String accountName) {
    this.accountName = accountName;
    return this;
  }

  /**
   * Identifier the seed is stored under (e.g. a username or email).
   * @return accountName
   */
  @NotNull @Size(min = 1, max = 100) 
  @Schema(name = "accountName", example = "alice@example.com", description = "Identifier the seed is stored under (e.g. a username or email).", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("accountName")
  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TotpGenerateRequest totpGenerateRequest = (TotpGenerateRequest) o;
    return Objects.equals(this.accountName, totpGenerateRequest.accountName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TotpGenerateRequest {\n");
    sb.append("    accountName: ").append(toIndentedString(accountName)).append("\n");
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

