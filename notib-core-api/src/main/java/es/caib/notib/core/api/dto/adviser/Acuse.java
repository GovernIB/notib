
package es.caib.notib.core.api.dto.adviser;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
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
