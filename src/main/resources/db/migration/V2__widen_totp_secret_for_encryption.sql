-- The column now stores base64(iv || AES-GCM ciphertext+tag), not the plaintext Base32 secret,
-- so it needs more room than the original VARCHAR(64).
ALTER TABLE totp_seed ALTER COLUMN secret TYPE VARCHAR(255);

COMMENT ON COLUMN totp_seed.secret IS 'AES-256-GCM ciphertext of the Base32 TOTP secret, base64-encoded (iv || ciphertext+tag). See TotpSecretCipher.';
