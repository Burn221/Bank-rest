package com.example.bankcards.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import java.util.Base64;


public final class AesGcm {
    private SecretKey key;
    private final int KEY_LENGTH=128;
    private final int T_LEN=128;
    private Cipher encryptionCipher;

    private void init() throws Exception{
        KeyGenerator generator= KeyGenerator.getInstance("AES");
        generator.init(KEY_LENGTH);
        key= generator.generateKey();


    }

    public String encrypt(String pan) throws Exception{
        byte[] panBytes= pan.getBytes();

        Cipher encryptionCipher= Cipher.getInstance("AES/GCM/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes= encryptionCipher.doFinal(panBytes);
        return encode(encryptedBytes);
    }

    public String decrypt(String encryptedMessage) throws Exception{
        byte[] messageInBytes= decode(encryptedMessage);
        Cipher decryptionCipher= Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec= new GCMParameterSpec(T_LEN, encryptionCipher.getIV());
        decryptionCipher.init(Cipher.DECRYPT_MODE,key,spec);
        byte[] decryptedBytes= decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes);


    }

    private String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);

    }

    private byte[] decode(String data){
        return Base64.getDecoder().decode(data);

    }
}
