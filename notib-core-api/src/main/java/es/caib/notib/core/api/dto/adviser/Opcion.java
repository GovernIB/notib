
package es.caib.notib.core.api.dto.adviser;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@Slf4j
public class Opcion {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "tipo", required = true)
    protected String tipo;

}
