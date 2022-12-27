
package es.caib.notib.war.command.adviser;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

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
