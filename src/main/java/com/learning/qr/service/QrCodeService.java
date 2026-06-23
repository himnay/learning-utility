package com.learning.qr.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.learning.qr.exception.QrDecodeException;
import com.learning.qr.exception.QrEncodeException;
import com.learning.qr.web.dto.QrDecodeResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Decodes and generates QR codes using ZXing. */
@Slf4j
@Service
public class QrCodeService {

  private static final String IMAGE_FORMAT = "PNG";
  private static final String IMAGE_MIME_TYPE = "image/png";

  private static final Map<DecodeHintType, Object> HINTS = new EnumMap<>(DecodeHintType.class);

  static {
    HINTS.put(DecodeHintType.POSSIBLE_FORMATS, List.of(BarcodeFormat.QR_CODE));
  }

  public QrDecodeResponse decode(InputStream imageStream) {
    BufferedImage image = readImage(imageStream);
    LuminanceSource source = new BufferedImageLuminanceSource(image);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

    try {
      Result result = new MultiFormatReader().decode(bitmap, HINTS);
      log.info("QR_DECODE | decoded | format={}", result.getBarcodeFormat());
      return new QrDecodeResponse().text(result.getText()).format(result.getBarcodeFormat().name());
    } catch (NotFoundException e) {
      throw new QrDecodeException("No QR code could be found in the uploaded image");
    }
  }

  public QrCodeImage generate(String text, int size) {
    try {
      BitMatrix matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(matrix, IMAGE_FORMAT, out);
      log.info("QR_ENCODE | generated | size={}", size);
      return new QrCodeImage(out.toByteArray(), IMAGE_MIME_TYPE);
    } catch (WriterException | IOException e) {
      throw new QrEncodeException("Failed to generate the QR code: " + e.getMessage(), e);
    }
  }

  private BufferedImage readImage(InputStream imageStream) {
    try {
      BufferedImage image = ImageIO.read(imageStream);
      if (image == null) {
        throw new QrDecodeException("Uploaded file is not a readable image");
      }
      return image;
    } catch (IOException e) {
      throw new QrDecodeException("Failed to read the uploaded image: " + e.getMessage(), e);
    }
  }
}
