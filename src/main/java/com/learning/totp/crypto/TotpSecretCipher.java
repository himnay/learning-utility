package com.learning.totp.crypto;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AES-256-GCM encrypt/decrypt for the TOTP secret at rest. Storage format is base64({@code iv ||
 * ciphertext+tag}) — the IV travels with the ciphertext since GCM requires a fresh IV per
 * encryption and there's nowhere else to put it in the {@code totp_seed.secret} column.
 */
@Component
public class TotpSecretCipher {

  private static final String TRANSFORMATION = "AES/GCM/NoPadding";
  private static final int GCM_TAG_LENGTH_BITS = 128;
  private static final int IV_LENGTH_BYTES = 12;

  private final SecretKeySpec key;
  private final SecureRandom random = new SecureRandom();

  public TotpSecretCipher(@Value("${totp.secret-encryption-key}") String base64Key) {
    byte[] keyBytes = Base64.getDecoder().decode(base64Key);
    if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
      throw new IllegalStateException(
          "totp.secret-encryption-key must decode to a 128/192/256-bit AES key, got "
              + keyBytes.length * 8
              + " bits");
    }
    this.key = new SecretKeySpec(keyBytes, "AES");
  }

  public String encrypt(String plaintext) {
    try {
      byte[] iv = new byte[IV_LENGTH_BYTES];
      random.nextBytes(iv);

      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
      byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

      byte[] combined = new byte[iv.length + ciphertext.length];
      System.arraycopy(iv, 0, combined, 0, iv.length);
      System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
      return Base64.getEncoder().encodeToString(combined);
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException("Failed to encrypt TOTP secret", e);
    }
  }

  public String decrypt(String stored) {
    try {
      byte[] combined = Base64.getDecoder().decode(stored);
      byte[] iv = new byte[IV_LENGTH_BYTES];
      byte[] ciphertext = new byte[combined.length - IV_LENGTH_BYTES];
      System.arraycopy(combined, 0, iv, 0, iv.length);
      System.arraycopy(combined, iv.length, ciphertext, 0, ciphertext.length);

      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
      return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
    } catch (GeneralSecurityException e) {
      throw new IllegalStateException("Failed to decrypt TOTP secret", e);
    }
  }
}
