
package es.caib.notib.war.command.adviser;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
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
