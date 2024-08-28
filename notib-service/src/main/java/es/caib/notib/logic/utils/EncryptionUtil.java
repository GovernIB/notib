package es.caib.notib.logic.utils;

import es.caib.notib.logic.objectes.StringEncriptat;
import lombok.Getter;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {

    private static final String PASSWORD = "aaaa";
    private static final int SALT_LENGTH = 32;

    private final TextEncryptor encryptor;

    @Getter
    private final String salt;

    public EncryptionUtil() {

        salt = generateSalt();
        encryptor = Encryptors.text(PASSWORD, salt);
    }

    public EncryptionUtil(String password, String salt) {

        this.salt = salt;
        encryptor = Encryptors.text(password, salt);
    }

    private String generateSalt() {

        var s = new byte[SALT_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(s);
        return Base64.getEncoder().encodeToString(s);
    }

    public StringEncriptat encrypt(String text) {
        return StringEncriptat.builder().string(encryptor.encrypt(text)).salt(salt).build();
    }

    public String decrypt(String encryptedText) {
        return encryptor.decrypt(encryptedText);
    }


    public static void main(String[] args) {

        var originalText = "42f88694-39b1-4a2d-8292-c845aedc1312";
        var encryptionUtil = new EncryptionUtil();
        var encryptedText = encryptionUtil.encrypt(originalText);
        var decryptedText = encryptionUtil.decrypt(encryptedText.getString());

        System.out.println("Original Text: " + originalText);
        System.out.println("Encrypted Text: " + encryptedText);
        System.out.println("Decrypted Text: " + decryptedText);
    }

}
