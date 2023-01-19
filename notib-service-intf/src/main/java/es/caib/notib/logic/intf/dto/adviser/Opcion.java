
package es.caib.notib.logic.intf.dto.adviser;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@Slf4j
@Getter
@Setter
public class Opcion {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "tipo", required = true)
    protected String tipo;

}
