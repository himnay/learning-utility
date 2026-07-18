package com.learning.qr.web;

import com.learning.qr.exception.QrDecodeException;
import com.learning.qr.exception.QrEncodeException;
import com.learning.qr.service.QrCodeImage;
import com.learning.qr.service.QrCodeService;
import com.learning.qr.web.dto.QrDecodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** {@code /qr} — generate a QR code from text, or upload an image to decode the one it contains. */
@RestController
@RequestMapping("/qr")
@Tag(name = "QR Code")
@RequiredArgsConstructor
public class QrCodeController {

  private static final int MIN_SIZE = 100;
  private static final int MAX_SIZE = 1000;

  private final QrCodeService qrCodeService;

  /** Generates. */
  @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
  @Operation(
      operationId = "generateQrCode",
      summary = "Generate a QR code PNG for the given text",
      description = "Encodes the given text as a QR code and returns it as a PNG image.")
  public ResponseEntity<byte[]> generate(
      @RequestParam("text") String text, @RequestParam(name = "size", defaultValue = "250") int size) {
    if (text.isEmpty()) {
      throw new QrEncodeException("Text to encode must not be empty");
    }
    if (size < MIN_SIZE || size > MAX_SIZE) {
      throw new QrEncodeException("size must be between " + MIN_SIZE + " and " + MAX_SIZE);
    }
    QrCodeImage image = qrCodeService.generate(text, size);
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.mimeType())).body(image.bytes());
  }

  /** Returns the scan. */
  @PostMapping(value = "/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      operationId = "scanQrCode",
      summary = "Decode a QR code from an uploaded image",
      description = "Accepts an image (PNG/JPEG) containing a single QR code and returns the text it encodes.")
  public QrDecodeResponse scan(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      throw new QrDecodeException("Uploaded file is empty");
    }
    try {
      return qrCodeService.decode(file.getInputStream());
    } catch (IOException e) {
      throw new QrDecodeException("Failed to read the uploaded file: " + e.getMessage(), e);
    }
  }
}
