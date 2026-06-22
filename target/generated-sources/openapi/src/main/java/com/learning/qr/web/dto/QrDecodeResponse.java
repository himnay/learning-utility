package com.learning.qr.web.dto;

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
 * Result of decoding an uploaded QR code image.
 */

@Schema(name = "QrDecodeResponse", description = "Result of decoding an uploaded QR code image.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-22T19:22:29.194749+01:00[Europe/Dublin]", comments = "Generator version: 7.16.0")
public class QrDecodeResponse {

  private String text;

  private String format;

  public QrDecodeResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public QrDecodeResponse(String text, String format) {
    this.text = text;
    this.format = format;
  }

  public QrDecodeResponse text(String text) {
    this.text = text;
    return this;
  }

  /**
   * The raw text/data encoded in the QR code.
   * @return text
   */
  @NotNull 
  @Schema(name = "text", example = "https://example.com", description = "The raw text/data encoded in the QR code.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public QrDecodeResponse format(String format) {
    this.format = format;
    return this;
  }

  /**
   * Barcode format ZXing detected (always QR_CODE for this endpoint).
   * @return format
   */
  @NotNull 
  @Schema(name = "format", example = "QR_CODE", description = "Barcode format ZXing detected (always QR_CODE for this endpoint).", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("format")
  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QrDecodeResponse qrDecodeResponse = (QrDecodeResponse) o;
    return Objects.equals(this.text, qrDecodeResponse.text) &&
        Objects.equals(this.format, qrDecodeResponse.format);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, format);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class QrDecodeResponse {\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    format: ").append(toIndentedString(format)).append("\n");
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

