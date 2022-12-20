
package es.caib.notib.core.api.dto.adviser;

import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@Setter
public class Opciones {

    @XmlElement(required = true)
    protected List<Opcion> opcion;

    public List<Opcion> getOpcion() {

        return opcion != null ? this.opcion : new ArrayList<Opcion>();
    }
}
