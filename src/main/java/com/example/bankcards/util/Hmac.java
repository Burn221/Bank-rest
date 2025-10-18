package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/** Класс, отвечающий за создание Hmac хэша для карты */
public class  Hmac {

    public static final String DEFAULT_DEV_HMAC_KEY= "H/V7RFuEh8JEVvoHUCB5GB7Cw/uDzy2ICxg1AfEApQA=";


    /** Создает Hmac ключ */

    public static SecretKeySpec hmacKey(){
        byte[] key = Base64.getDecoder().decode(DEFAULT_DEV_HMAC_KEY);
        if (key.length < 32) throw new IllegalArgumentException("HMAC key must be >= 32 bytes");
        return new SecretKeySpec(key, "HmacSHA256");

    }

    /** Создает Hmac хэш */
    public static byte[] hmacSha256(String panNormalized, SecretKeySpec key)  {


        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            return mac.doFinal(panNormalized.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | IllegalStateException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

}
