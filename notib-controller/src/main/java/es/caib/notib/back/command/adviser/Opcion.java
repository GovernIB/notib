
package es.caib.notib.back.command.adviser;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

@Slf4j
@Getter
@Setter
public class Opcion {

//    @XmlValue
    protected String value;
//    @XmlAttribute(name = "tipo", required = true)
    @NotNull
    protected String tipo;

}
