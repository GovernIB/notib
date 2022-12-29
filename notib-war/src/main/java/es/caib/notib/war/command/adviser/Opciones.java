
package es.caib.notib.war.command.adviser;

import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@Setter
public class Opciones {

//    @XmlElement(required = true)
    @NotNull
    @Valid
    protected List<Opcion> opcion;

    public List<Opcion> getOpcion() {

        return opcion != null ? this.opcion : new ArrayList<Opcion>();
    }
}
