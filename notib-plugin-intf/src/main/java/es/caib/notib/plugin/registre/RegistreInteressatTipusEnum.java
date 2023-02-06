package es.caib.notib.plugin.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del tipus d'un interessat
 * del registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreInteressatTipusEnum {

	PERSONA_FIS("2"),
	PERSONA_JUR("3"),
	ADMINISTRACIO("1");

	private final String valor;
	private RegistreInteressatTipusEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreInteressatTipusEnum> lookup;

	static {
		lookup = new HashMap<>();
		for (var s: EnumSet.allOf(RegistreInteressatTipusEnum.class)) {
			lookup.put(s.getValor(), s);
		}
	}
	public static RegistreInteressatTipusEnum valorAsEnum(String valor) {
        return valor != null ? lookup.get(valor) : null;
    }

}
