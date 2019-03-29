package es.caib.notib.core.api.dto;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del tipus de document del registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreDocumentacioFisicaEnumDto {

	ACOMPANYA_DOCUMENTACIO_FISICA_REQUERIDA("01"),
	ACOMPANYA_DOCUMENTACIO_FISICA_COMPLEMENTARIA("02"),
	NO_ACOMPANYA_DOCUMENTACIO("03");

	private final String valor;
	private RegistreDocumentacioFisicaEnumDto(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreDocumentacioFisicaEnumDto> lookup;
	static {
		lookup = new HashMap<String, RegistreDocumentacioFisicaEnumDto>();
		for (RegistreDocumentacioFisicaEnumDto s: EnumSet.allOf(RegistreDocumentacioFisicaEnumDto.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreDocumentacioFisicaEnumDto valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
