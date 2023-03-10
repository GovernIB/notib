
package es.caib.notib.war.command.adviser;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Getter
@Setter
public class Receptor {

//    @XmlElement(required = true)
    @NotNull
    protected String nifReceptor;
//    @XmlElement(required = true)
    @NotNull
    protected String nombreReceptor;
//    @XmlElement(required = true)
    protected BigInteger vinculoReceptor;
    protected String nifRepresentante;
    protected String nombreRepresentante;
    protected String csvRepresetante;

}
