package eu.baltrad.dex.auth.util;

import eu.baltrad.dex.util.MessageDigestUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoder that matches the legacy MD5 hashing used by BaltradDex
 * (zero-padded lowercase hex, no salt, UTF-8 input).
 */
public class Md5PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return MessageDigestUtil.createHash("MD5", rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
