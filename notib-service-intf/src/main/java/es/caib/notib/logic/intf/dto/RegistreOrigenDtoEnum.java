package es.caib.notib.logic.intf.dto;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RegistreOrigenDtoEnum implements Serializable{
	
	CIUTADA(0),
	ADMINISTRACIO(1);

	private final Integer valor;
	private RegistreOrigenDtoEnum(Integer valor) {
		this.valor = valor;
	}
	public Integer getValor() {
		return valor;
	}
	private static final Map<Integer, RegistreOrigenDtoEnum> lookup;
	static {
		lookup = new HashMap<Integer, RegistreOrigenDtoEnum>();
		for (RegistreOrigenDtoEnum s: EnumSet.allOf(RegistreOrigenDtoEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreOrigenDtoEnum valorAsEnum(Integer valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }
	
	private static final long serialVersionUID = 5699295136689332973L;

}
