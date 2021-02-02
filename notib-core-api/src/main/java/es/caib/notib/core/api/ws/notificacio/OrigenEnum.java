package es.caib.notib.core.api.ws.notificacio;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum OrigenEnum  implements Serializable {
    CIUTADA (0),
    ADMINISTRACIO (1);

    private final Integer valor;
    private OrigenEnum(Integer valor) {
        this.valor = valor;
    }
    public Integer getValor() {
        return valor;
    }
    private static final Map<Integer, OrigenEnum> lookup;
    static {
        lookup = new HashMap<Integer, OrigenEnum>();
        for (OrigenEnum s: EnumSet.allOf(OrigenEnum.class))
            lookup.put(s.getValor(), s);
    }
    public static OrigenEnum valorAsEnum(Integer valor) {
        if (valor == null)
            return null;
        return lookup.get(valor);
    }
}
