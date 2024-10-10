
package es.caib.notib.api.externa.adviser;

import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Setter
public class Opciones {

    @NotNull
    @Valid
    protected List<Opcion> opcion;

    public List<Opcion> getOpcion() {

        return opcion != null ? this.opcion : new ArrayList<>();
    }
}
