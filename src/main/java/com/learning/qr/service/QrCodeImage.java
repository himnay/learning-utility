package com.learning.qr.service;

/** A rendered QR code — raw image bytes plus the MIME type to serve them as. */
public record QrCodeImage(byte[] bytes, String mimeType) {}
