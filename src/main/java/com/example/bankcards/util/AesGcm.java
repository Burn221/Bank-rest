package com.example.bankcards.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;


public final class AesGcm {
    private AesGcm() {}

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LEN = 12;
    private static final int TAG_LEN_BITS = 128;

    private static final String DEFAULT_DEV_KEY_B64 = "bWFrZS1tZS1yZXBsYWNlLXRoaXMtMzItYnl0ZS1rZXk=";

    private static volatile SecretKeySpec KEY;
    private static final SecureRandom RAND = new SecureRandom();

    private static SecretKeySpec key() {
        SecretKeySpec k = KEY;
        if (k != null) return k;

        synchronized (AesGcm.class) {
            if (KEY != null) return KEY;
            String b64 = System.getenv("CARD_ENC_KEY");
            if (b64 == null || b64.isBlank()) b64 = DEFAULT_DEV_KEY_B64;
            byte[] raw = Base64.getDecoder().decode(b64);
            int n = raw.length;
            if (n != 16 && n != 24 && n != 32) {
                throw new IllegalStateException("AES key must be 16/24/32 bytes (got " + n + ")");
            }
            KEY = new SecretKeySpec(raw, "AES");
            return KEY;
        }
    }


    public static String encryptToBase64(String plaintext) {
        try {
            byte[] iv = new byte[IV_LEN];
            RAND.nextBytes(iv);
            Cipher c = Cipher.getInstance(TRANSFORMATION);
            c.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(TAG_LEN_BITS, iv));
            byte[] ct = c.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] out = ByteBuffer.allocate(iv.length + ct.length).put(iv).put(ct).array();
            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new RuntimeException("Encrypt failed", e);
        }
    }


    public static String decryptFromBase64(String blobB64) {
        try {
            byte[] all = Base64.getDecoder().decode(blobB64);
            if (all.length <= IV_LEN) throw new RuntimeException("Ciphertext too short");
            byte[] iv = new byte[IV_LEN];
            byte[] ct = new byte[all.length - IV_LEN];
            System.arraycopy(all, 0, iv, 0, IV_LEN);
            System.arraycopy(all, IV_LEN, ct, 0, ct.length);

            Cipher c = Cipher.getInstance(TRANSFORMATION);
            c.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(TAG_LEN_BITS, iv));
            byte[] pt = c.doFinal(ct);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decrypt failed", e);
        }
    }
}
