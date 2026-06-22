package com.learning.qr.web;

import com.learning.qr.exception.QrDecodeException;
import com.learning.qr.service.QrCodeService;
import com.learning.qr.web.dto.QrDecodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** {@code /qr} — upload an image, get back the QR code it contains decoded. */
@RestController
@RequestMapping("/qr")
@RequiredArgsConstructor
@Tag(name = "QR Code")
public class QrCodeController {

  private final QrCodeService qrCodeService;

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
