
package es.caib.notib.client.domini;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumerat que indica l'origen del document a notificar.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum OrigenEnum  implements Serializable {
    CIUTADA (0),
    ADMINISTRACIO (1);

    private final Integer valor;

    OrigenEnum(Integer valor) {
        this.valor = valor;
    }

    public Integer getValor() {
        return valor;
    }

    private static final Map<Integer, OrigenEnum> lookup;
    static {
        lookup = new HashMap<>();
        for (OrigenEnum s: EnumSet.allOf(OrigenEnum.class))
            lookup.put(s.getValor(), s);
    }
    public static OrigenEnum valorAsEnum(Integer valor) {
        if (valor == null)
            return null;
        return lookup.get(valor);
    }
}
