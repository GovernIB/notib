package es.caib.notib.core.api.dto;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del tipus d'un interessat
 * del registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreInteressatDocumentTipusDtoEnum {

	NIF("N"),
	CIF("C"),
	PASSAPORT("P"),
	DOCUMENT_IDENTIFICACIO_EXTRANGERS("E"),
	ALTRES_PERSONES_FISIQUES("X"),
	CODI_ORIGEN("O");

	private final String valor;
	private RegistreInteressatDocumentTipusDtoEnum(String valor) {
		this.valor = valor;
	}
	public String getValor() {
		return valor;
	}
	private static final Map<String, RegistreInteressatDocumentTipusDtoEnum> lookup;
	static {
		lookup = new HashMap<String, RegistreInteressatDocumentTipusDtoEnum>();
		for (RegistreInteressatDocumentTipusDtoEnum s: EnumSet.allOf(RegistreInteressatDocumentTipusDtoEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreInteressatDocumentTipusDtoEnum valorAsEnum(String valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
