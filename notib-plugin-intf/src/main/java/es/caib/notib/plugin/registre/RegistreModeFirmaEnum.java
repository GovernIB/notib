package es.caib.notib.plugin.registre;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumeració amb els possibles valors del tipus de document del registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum RegistreModeFirmaEnum {

	SENSE_FIRMA(0),
	AUTOFIRMA_SI(1),
	AUTOFIRMA_NO(2);

	private final Integer valor;
	private RegistreModeFirmaEnum(Integer valor) {
		this.valor = valor;
	}
	public Integer getValor() {
		return valor;
	}
	private static final Map<Integer, RegistreModeFirmaEnum> lookup;

	static {
		lookup = new HashMap<>();
		for (var s: EnumSet.allOf(RegistreModeFirmaEnum.class)) {
			lookup.put(s.getValor(), s);
		}
	}

	public static RegistreModeFirmaEnum valorAsEnum(Integer valor) {
        return valor != null ? lookup.get(valor) : null;
    }

}
