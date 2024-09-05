package es.caib.notib.logic.utils;

import es.caib.notib.logic.objectes.StringEncriptat;
import es.caib.notib.persist.repository.config.ConfigRepository;
import lombok.Getter;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;


public class EncryptionUtil {

    private static final int SALT_LENGTH = 32;

    private final TextEncryptor encryptor;
    @Getter
    private final String salt;


    public EncryptionUtil() {

        encryptor = null;
        salt = null;
    }

    public EncryptionUtil(String password) {

        salt = stringToHex(generateSalt());
        encryptor = Encryptors.text(password, salt);
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
        return StringEncriptat.builder().string(encryptor.encrypt(text).toString()).salt(salt).build();
    }

    public String decrypt(String encryptedText) {
        return encryptor.decrypt(encryptedText).toString();
    }

    public String stringToHex(String string) {

        var bytes = string.getBytes(StandardCharsets.UTF_8);
        var bigInt = new BigInteger(bytes);
        return bigInt.toString(16);
    }

    public static void main(String[] args) {

        var originalText = "9e7f7223-b7f3-408f-b71b-c2063438c622";
        byte[] bytes = originalText.getBytes(StandardCharsets.UTF_8); // you didn't say what charset you wanted
        BigInteger bigInt = new BigInteger(bytes);
        String hexString = bigInt.toString(16);
        System.out.println("Encrypted Text: " + hexString);

        var encryptionUtil = new EncryptionUtil();
        var encryptedText = encryptionUtil.encrypt(originalText);
        var decryptedText = encryptionUtil.decrypt(encryptedText.getString());

        System.out.println("Original Text: " + originalText);

        System.out.println("Decrypted Text: " + decryptedText);


    }

}
