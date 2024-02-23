
package es.caib.notib.api.interna.openapi.model.notificacio;

import es.caib.notib.client.domini.EnviamentEstat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;

/**
 * Informació sobre el datat d'un enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "Datat")
public class DatatApi {

    @Schema(name = "estat", implementation = EnviamentEstat.class, example = "NOTIFICADA", description = "Estat de la notificació")
    private EnviamentEstat estat;
    @Schema(name = "data", implementation = Long.class, example = "1706168093962", description = "Data en que s'ha creat el datat")
    private Date data;
    @Schema(name = "origen", implementation = String.class, example = "carpeta", description = "Origen del datat")
    private String origen;
    @Schema(name = "receptorNif", implementation = String.class, example = "00000000T", description = "Nif del receptor de la notificació")
    private String receptorNif;
    @Schema(name = "receptorNom", implementation = String.class, example = "Pep Riera", description = "Nom del receptor de la notificació")
    private String receptorNom;
    @Schema(name = "errorDescripcio", implementation = String.class, example = "java.lang.NullPointerException", description = "Descripció de l'error, en cas que es datat tingui algun error")
    private String errorDescripcio;
    @Schema(name = "numSeguiment", implementation = String.class, example = "123456789", description = "Número de seguiment, en cas d'enviament postal\n * Camp actualemnt no utilitzat")
    private String numSeguiment;

}