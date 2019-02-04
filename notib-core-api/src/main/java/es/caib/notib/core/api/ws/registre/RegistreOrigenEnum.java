package es.caib.notib.core.api.ws.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del tipus de document del registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreOrigenEnum {

	CIUTADA(0),
	ADMINISTRACIO(1);

	private final Integer valor;
	private RegistreOrigenEnum(Integer valor) {
		this.valor = valor;
	}
	public Integer getValor() {
		return valor;
	}
	private static final Map<Integer, RegistreOrigenEnum> lookup;
	static {
		lookup = new HashMap<Integer, RegistreOrigenEnum>();
		for (RegistreOrigenEnum s: EnumSet.allOf(RegistreOrigenEnum.class))
			lookup.put(s.getValor(), s);
	}
	public static RegistreOrigenEnum valorAsEnum(Integer valor) {
		if (valor == null)
			return null;
        return lookup.get(valor); 
    }

}
