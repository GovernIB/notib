
package es.caib.notib.client.domini;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ValidesaEnum implements Serializable {
    COPIA ("01"),
    COPIA_AUTENTICA ("03"),
    ORIGINAL ("04");

    private final String valor;

    ValidesaEnum(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    private static final Map<String, ValidesaEnum> lookup;
    static {
        lookup = new HashMap<>();
        for (ValidesaEnum s: EnumSet.allOf(ValidesaEnum.class))
            lookup.put(s.getValor(), s);
    }
    public static ValidesaEnum valorAsEnum(String valor) {
        return valor != null ? lookup.get(valor) : null;
    }
}
