
package es.caib.notib.logic.intf.dto.adviser;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@Getter
@Setter
public class Acuse implements Serializable {

    @XmlElement(required = true)
    protected byte[] contenido;
    @XmlElement(required = true)
    protected String hash;
    protected String csvResguardo;
}
