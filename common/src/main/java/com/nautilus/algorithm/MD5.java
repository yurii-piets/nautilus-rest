package com.nautilus.algorithm;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new NullPointerException();
        }
        return crypt(rawPassword.toString());
    }

    public String encode(String rawPassword) {
        return crypt(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }

    private String crypt(String in) {
        if (in == null) {
            return null;
        }

        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(in.getBytes());
            byte[] dBytes = md5.digest();
            StringBuilder builder = new StringBuilder(dBytes.length << 1);
            for (byte dByte : dBytes) {
                builder.append(Character.forDigit((dByte & 0xf0) >> 4, 16));
                builder.append(Character.forDigit(dByte & 0x0f, 16));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) { }
        return null;
    }
}
