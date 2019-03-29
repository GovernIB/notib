package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RegistreModeFirmaDtoEnum implements Serializable{

	SENSE_FIRMA(0),
	AUTOFIRMA_SI(1),
	AUTOFIRMA_NO(2);

	private final Integer valor;
	private RegistreModeFirmaDtoEnum(Integer valor) {
		this.valor = valor;
	}
	public Integer getValor() {
		return valor;
	}
	private static final Map<Integer, RegistreModeFirmaDtoEnum> lookup;
	static {
		lookup = new HashMap<Integer, RegistreModeFirmaDtoEnum>();
		for (RegistreModeFirmaDtoEnum s: EnumSet.allOf(RegistreModeFirmaDtoEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreModeFirmaDtoEnum valorAsEnum(Integer valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }
	private static final long serialVersionUID = -1833672007622435069L;
}
