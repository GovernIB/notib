
package es.caib.notib.api.interna.openapi.model.notificacio;

import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Size;


/**
 * Informació d'una persona per a un enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "Persona")
public class PersonaApi {

    @Schema(name = "incapacitat", implementation = Boolean.class, example = "false", defaultValue = "false", required = false,
            description = "Indica si el titular es incapacitat per llegir/rebutjar la notificació.\n" +
                    "En aquest cas, seria obligatori informar un destinatari de l'enviament.")
    private boolean incapacitat;
    @Schema(name = "interessatTipus", implementation = InteressatTipus.class, example = "FISICA", required = true,
            description = "Enumerat que indica el tipus de l’interessat que realitza l’enviament.\n" +
                    " * En cas d'indicar un interessat tipus FISICA_SENSE_NIF serà obligatori informar el camp documentTipus")
    private InteressatTipus interessatTipus;
    @Size(max = 30)
    @Schema(name = "nom", implementation = String.class, example = "Joan", required = false,
            description = "Nom del titular.\n" +
                    " * Obligatori en cas de persones físiques\n" +
                    " * Mida màxima de 255 caràcters quan es tracta d'una administració\n" +
                    " * Mida màxima de 30 caràcters per la resta de tipus d'interessats\n" +
                    " * En el cas de persones jurídiques es posarà el nom en el camp raoSocial. En cas de no informar-se el camp raoSocial s'agafarà el valor indicat en aquest camp")
    private String nom;
    @Size(max = 30)
    @Schema(name = "llinatge1", implementation = String.class, example = "Riera", required = false,
            description = "Primer llinatge de l’interessat\n" +
                    " * Obligatori en cas de persones físiques")
    private String llinatge1;
    @Size(max = 30)
    @Schema(name = "llinatge2", implementation = String.class, example = "Servera", required = false,
            description = "Segon llinatge de l’interessat")
    private String llinatge2;
    @Schema(name = "documentTipus", implementation = DocumentTipus.class, example = "PASSAPORT", required = false,
            description = "Enumerat que indica el tipus de document aportat per l’interessat\n" +
                    " * Aquest camp únicament es té en compte quan el tipus d’interessat és FISICA_SENSE_NIF")
    protected DocumentTipus documentTipus;
    @Size(max = 9)
    @Schema(name = "nif", implementation = String.class, example = "00000000T", required = false,
            description = "Número del document de l'interessat\n" +
                    " * Obligatori excepte per interessats tipus FISICA_SENSE_NIF")
    private String nif;
    @Size(max = 16)
    @Schema(name = "telefon", implementation = String.class, example = "678123456", required = false,
            description = "Telèfon de l’interessat\n" +
                    " * Actualment no s’utilitza")
    private String telefon;
    @Size(max = 160)
    @Schema(name = "email", implementation = String.class, example = "persona@correu.es", required = false,
            description = "Correu electrònic de l’interessat. \n" +
                    " * Altament recomanat el seu ús.")
    private String email;
    @Size(max = 80)
    @Schema(name = "raoSocial", implementation = String.class, example = "Amazon ltd.", required = false,
            description = "Raó social de l’interessat\n" +
                    " * S’utilitza únicament en cas de persones jurídiques, i si no s’informa aquest camp, s’utilitzarà el valor del camp nom. És obligatori que un dels dos camps estigui informat")
    private String raoSocial;
    @Size(max = 9)
    @Schema(name = "dir3Codi", implementation = String.class, example = "A04035965", required = true,
            description = "Codi DIR3 de la administració a la que pertany la persona.\n" +
                    " * Obligatori si s’ha d’enviar a una administració.")
    private String dir3Codi;
}
