
package es.caib.notib.api.interna.model.adviser;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

@Slf4j
@Getter
@Setter
public class Opcion {

    protected String value;
    @NotNull
    protected String tipo;

}