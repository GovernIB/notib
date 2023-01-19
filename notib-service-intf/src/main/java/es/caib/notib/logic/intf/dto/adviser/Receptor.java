
package es.caib.notib.logic.intf.dto.adviser;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import java.math.BigInteger;

@Getter
@Setter
public class Receptor {

    @XmlElement(required = true)
    protected String nifReceptor;
    @XmlElement(required = true)
    protected String nombreReceptor;
    @XmlElement(required = true)
    protected BigInteger vinculoReceptor;
    protected String nifRepresentante;
    protected String nombreRepresentante;
    protected String csvRepresetante;

}
