package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RegistreTipusDocumentDtoEnum implements Serializable {

	FORMULARI("01"),
	DOCUMENT_ADJUNT_FORMULARI("02"),
	FITXER_TECNIC_INTERN("03");

	private final String valor;
	private RegistreTipusDocumentDtoEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreTipusDocumentDtoEnum> lookup;
	static {
		lookup = new HashMap<String, RegistreTipusDocumentDtoEnum>();
		for (RegistreTipusDocumentDtoEnum s: EnumSet.allOf(RegistreTipusDocumentDtoEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreTipusDocumentDtoEnum valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

	private static final long serialVersionUID = -6620978420169044462L;
}
