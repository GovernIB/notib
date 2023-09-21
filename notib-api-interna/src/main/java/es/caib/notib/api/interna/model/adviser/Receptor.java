
package es.caib.notib.api.interna.model.adviser;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Getter
@Setter
public class Receptor {

    @NotNull
    protected String nifReceptor;
    @NotNull
    protected String nombreReceptor;
    @NotNull
    protected BigInteger vinculoReceptor;
    protected String nifRepresentante;
    protected String nombreRepresentante;
    protected String csvRepresetante;

}
