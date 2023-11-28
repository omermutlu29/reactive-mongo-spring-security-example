package com.chance.auth.util;

import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PBKDF2Encoder implements PasswordEncoder {

    @Value("${springbootwebfluxjjwt.password.encoder.secret}")
    private String secret;

    @Value("${springbootwebfluxjjwt.password.encoder.iteration}")
    private Integer iteration;

    @Value("${springbootwebfluxjjwt.password.encoder.keylength}")
    private Integer keylength;

    @Override
    @SneakyThrows
    public String encode(CharSequence charSequence) {
        byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
                .generateSecret(
                        new PBEKeySpec(charSequence.toString().toCharArray(), secret.getBytes(), iteration, keylength))
                .getEncoded();
        return Base64.getEncoder().encodeToString(result);
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return encode(charSequence).equals(s);
    }
}
