package com.zygen.linebot.client;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.NonNull;
import lombok.ToString;

@ToString
public class LineSignatureValidator {
    private static final String HASH_ALGORITHM = "HmacSHA256";
    private final SecretKeySpec secretKeySpec;

    public LineSignatureValidator(byte[] channelSecret) {
        this.secretKeySpec = new SecretKeySpec(channelSecret, HASH_ALGORITHM);
    }

    public boolean validateSignature(@NonNull byte[] content, @NonNull String headerSignature) {
        final byte[] signature = generateSignature(content);
        final byte[] decodeHeaderSignature = Base64.getDecoder().decode(headerSignature);
        return MessageDigest.isEqual(decodeHeaderSignature, signature);
    }

    public byte[] generateSignature(@NonNull byte[] content) {
        try {
            Mac mac = Mac.getInstance(HASH_ALGORITHM);
            mac.init(secretKeySpec);
            return mac.doFinal(content);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            // "HmacSHA256" is always supported in Java 8 platform.
            //   (see https://docs.oracle.com/javase/8/docs/api/javax/crypto/Mac.html)
            // All valid-SecretKeySpec-instance are not InvalidKey.
            //   (because the key for HmacSHA256 can be of any length. see RFC2104)
            throw new IllegalStateException(e);
        }
    }

}

