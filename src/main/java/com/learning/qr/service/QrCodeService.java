package com.learning.qr.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.learning.qr.exception.QrDecodeException;
import com.learning.qr.web.dto.QrDecodeResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Decodes a QR code out of an uploaded image using ZXing. */
@Slf4j
@Service
public class QrCodeService {

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
