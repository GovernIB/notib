/**
 * 
 */
package es.caib.notib.back.validation;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * Valida que el nombre de document d'identitat sigui vàlid. Els tipus de
 * document suportats son: NIF, DNI, NIE i CIF.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class DocumentIdentitatValidator implements ConstraintValidator<DocumentIdentitat, String> {
	
	@Override
	public void initialize(final DocumentIdentitat constraintAnnotation) {
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		try {
			return !Strings.isNullOrEmpty(value) ? validacioNif(value) : true;
		} catch (final Exception ex) {
			log.error("Error en la validació del NIF", ex);
			return false;
		}
	}

	// Validació del DNI
	private static final Pattern dniPattern = Pattern.compile("[0-9]{8}[A-Z]");

	private static boolean validacioDni(String dni) {

		if (!dniPattern.matcher(dni).matches()) {
			return false;
		}
		var nums = dni.substring(0, 8);
		var lletra = dni.substring(8);
		return lletra.equals(lletraNif(new Integer(nums).intValue()));
	}

	// Validació del NIE
	private static final Pattern niePattern = Pattern.compile("[XYZ][0-9]{7}[A-Z]");

	private static boolean validacioNie(String nie) {

		if (!niePattern.matcher(nie).matches()) {
			return false;
		}
		var nums = (char)(nie.charAt(0) - 40) + nie.substring(1, 8);
		var lletra = nie.substring(8);
		return lletra.equals(lletraNif(new Integer(nums).intValue()));
	}

	// Validació del NIF
	// Només s'admeten nombres com a caràcter de control
	private static final String CONTROL_SOLO_NUMEROS = "ABEH";
	// Només s'admeten lletres com a caràcter de control
	private static final String CONTROL_SOLO_LETRAS = "KPQS";
	// Conversió de dígit a lletra de control
	private static final String CONTROL_NUMERO_A_LETRA = "JABCDEFGHI";
	private static final Pattern nifPattern = Pattern.compile("[[A-H][J-N][P-S]UVW][0-9]{7}[0-9A-J]");
	private static boolean validacioNif(String nif) {

		if (dniPattern.matcher(nif).matches()) {
			return validacioDni(nif);
		}
		if (niePattern.matcher(nif).matches()) {
			return validacioNie(nif);
		}
		if (!nifPattern.matcher(nif).matches()) {
			return false;
		}
		var parA = 0;
		for (var i = 2; i < 8; i += 2) {
			final var digito = Character.digit(nif.charAt(i), 10);
			if (digito < 0) {
				return false;
			}
			parA += digito;
		}
		var nonB = 0;
		for (var i = 1; i < 9; i += 2) {
			final var digito = Character.digit(nif.charAt(i), 10);
			if (digito < 0) {
				return false;
			}
			var nn = 2 * digito;
			if (nn > 9) {
				nn = 1 + (nn - 10);
			}
			nonB += nn;
		}
		final var parcialC = parA + nonB;
		final var digitoE = parcialC % 10;
		final var digitoD = (digitoE > 0) ? (10 - digitoE) : 0;
		final var letraIni = nif.charAt(0);
		final var caracterFin = nif.charAt(8);
		return	// El caràcter de control es vàlid com a lletra?
				(CONTROL_SOLO_NUMEROS.indexOf(letraIni) < 0 && CONTROL_NUMERO_A_LETRA.charAt(digitoD) == caracterFin) ||
				// El caràcter de control es vàlid com a dígit?
				(CONTROL_SOLO_LETRAS.indexOf(letraIni) < 0 && digitoD == Character.digit(caracterFin, 10));
	}

	public static final String NIF_STRING_ASOCIATION = "TRWAGMYFPDXBNJZSQVHLCKE";
	public static String lletraNif(int dni) {
		return "" + NIF_STRING_ASOCIATION.charAt(dni % 23);
	}

}
