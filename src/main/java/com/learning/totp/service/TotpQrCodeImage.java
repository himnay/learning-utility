package com.learning.totp.service;

/** A rendered enrollment QR code — raw image bytes plus the MIME type to serve them as. */
public record TotpQrCodeImage(byte[] bytes, String mimeType) {}
