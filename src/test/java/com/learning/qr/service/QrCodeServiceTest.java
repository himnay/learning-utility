package com.learning.qr.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.learning.qr.exception.QrDecodeException;
import com.learning.qr.web.dto.QrDecodeResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QrCodeServiceTest {

  private final QrCodeService service = new QrCodeService();

  @Test
  @DisplayName("decode() reads back the exact text encoded into a generated QR image")
  void decodeReadsBackEncodedText() throws WriterException, IOException {
    InputStream qrImage = generateQrImage("https://example.com/learning-utility");

    QrDecodeResponse response = service.decode(qrImage);

    assertThat(response.getText()).isEqualTo("https://example.com/learning-utility");
    assertThat(response.getFormat()).isEqualTo("QR_CODE");
  }

  @Test
  @DisplayName("decode() throws QrDecodeException when the image contains no QR code")
  void decodeThrowsWhenNoQrCodeFound() throws IOException {
    InputStream blankImage = generateBlankImage();

    assertThatThrownBy(() -> service.decode(blankImage))
        .isInstanceOf(QrDecodeException.class)
        .hasMessageContaining("No QR code");
  }

  @Test
  @DisplayName("decode() throws QrDecodeException when the input isn't a readable image at all")
  void decodeThrowsWhenNotAnImage() {
    InputStream garbage = new ByteArrayInputStream("not an image".getBytes());

    assertThatThrownBy(() -> service.decode(garbage)).isInstanceOf(QrDecodeException.class);
  }

  private static InputStream generateQrImage(String text) throws WriterException, IOException {
    BitMatrix matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 250, 250);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(matrix, "PNG", out);
    return new ByteArrayInputStream(out.toByteArray());
  }

  private static InputStream generateBlankImage() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ImageIO.write(new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_RGB), "PNG", out);
    return new ByteArrayInputStream(out.toByteArray());
  }
}
