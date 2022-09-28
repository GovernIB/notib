/**
 * 
 */
package es.caib.notib.logic.test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Base64;

/**
 * Prova de generació de ids per a les notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GeneradorIds {

	private static final String CLAU = "P0rt4FI8";

	public static void main(String[] args) {
		try {
			new GeneradorIds().xifrarDesxifrarId(new Long(1234567890L));
			new GeneradorIds().xifrarDesxifrarId(Long.MAX_VALUE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void xifrarDesxifrarId(Long id) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		System.out.println(">>> original: " + id.toString() + " (" + id.toString().length() + ")");
		String xifrat = xifrarIdPerNotifica(id);
		System.out.println(">>> xifrat: " + xifrat + " (" + id.toString().length() + "->" + xifrat.length() + ")");
		Long desxifrat = desxifrarIdPerNotifica(xifrat);
		System.out.println(">>> desxifrat: " + desxifrat + " (" + xifrat.length() + "->" + desxifrat.toString().length() + ")");
		System.out.println();
	}

	private String xifrarIdPerNotifica(Long id) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		byte[] bytes = longToBytes(id.longValue());
		Cipher cipher = Cipher.getInstance("RC4");
		SecretKeySpec rc4Key = new SecretKeySpec(CLAU.getBytes(),"RC4");
		cipher.init(Cipher.ENCRYPT_MODE, rc4Key);
		byte[] xifrat = cipher.doFinal(bytes);
		return new String(Base64.encode(xifrat));
	}
	private Long desxifrarIdPerNotifica(String idXifrat) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
		Cipher cipher = Cipher.getInstance("RC4");
		SecretKeySpec rc4Key = new SecretKeySpec(CLAU.getBytes(),"RC4");
		cipher.init(Cipher.DECRYPT_MODE, rc4Key);
		byte[] desxifrat = cipher.doFinal(Base64.decode(idXifrat.getBytes()));
		return new Long(bytesToLong(desxifrat));
	}

	public byte[] longToBytes(long l) {
		byte[] result = new byte[Long.SIZE / Byte.SIZE];
	    for (int i = 7; i >= 0; i--) {
	        result[i] = (byte)(l & 0xFF);
	        l >>= 8;
	    }
	    return result;
	}
	public long bytesToLong(byte[] b) {
		long result = 0;
	    for (int i = 0; i < 8; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}

}
