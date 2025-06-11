package mk.ukim.finki.wp.liga.config;

import java.security.SecureRandom;
import java.util.Base64;

public class GenerateSecret {
    public static void main(String[] args) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24]; // 24 bytes = 32 characters in Base64
        random.nextBytes(bytes);
        String secret = Base64.getEncoder().encodeToString(bytes);
        System.out.println(secret);
    }
}