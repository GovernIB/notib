
package es.caib.notib.back.command.adviser;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class Acuse implements Serializable {

//    @XmlElement(required = true)
    @NotNull
    protected byte[] contenido;
//    @XmlElement(required = true)
    @NotNull
    protected String hash;
    protected String csvResguardo;
}
