package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RegistreValidezDocumentDtoEnum implements Serializable {

	COPIA("01"),
    COPIA_COMPULSADA("02"),
    COPIA_ORIGINAL("03"),
    ORIGINAL("04");

	private final String valor;
	private RegistreValidezDocumentDtoEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreValidezDocumentDtoEnum> lookup;
	static {
		lookup = new HashMap<String, RegistreValidezDocumentDtoEnum>();
		for (RegistreValidezDocumentDtoEnum s: EnumSet.allOf(RegistreValidezDocumentDtoEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreValidezDocumentDtoEnum valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

	private static final long serialVersionUID = -6620978420169044462L;
}
