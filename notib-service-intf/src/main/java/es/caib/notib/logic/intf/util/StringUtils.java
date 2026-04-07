package es.caib.notib.logic.intf.util;

public class StringUtils {

	/**
	 * <p>Comprova si un string té un valor valid.</p>
	 *
	 * <pre>
	 * Utils.hasValue(null)      = false
	 * Utils.hasValue("")        = false
	 * Utils.hasValue(" ")       = false
	 * Utils.hasValue("bob")     = true
	 * Utils.hasValue("  bob  ") = true
	 * </pre>
	 *
	 * @param str  the String to check, may be null
	 * @return <code>true</code> if the String is not empty and not null
	 */
	public static boolean hasValue(String str) {
		return str != null && !"".equals(str.trim());
	}
}
