package es.caib.notib.plugin.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del tipus de document del registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreTipusDocumentEnum {

	FORMULARI("01"),
	DOCUMENT_ADJUNT_FORMULARI("02"),
	FITXER_TECNIC_INTERN("03");

	private final String valor;
	private RegistreTipusDocumentEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreTipusDocumentEnum> lookup;

	static {
		lookup = new HashMap<>();
		for (var s: EnumSet.allOf(RegistreTipusDocumentEnum.class))
			lookup.put(s.getValor(), s);
	}

	public static RegistreTipusDocumentEnum valorAsEnum(String valor) {
        return valor != null ? lookup.get(valor) : null;
    }

}
