
package es.caib.notib.api.interna.openapi.model.notificacio;

import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Size;

/**
 * Informació del document que s'envia amb la notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "DocumentV2")
public class DocumentApi {

    @Size(max = 200)
    @Schema(name = "arxiuNom", implementation = String.class, example = "document.pdf", required = true, description = "Nom de l’arxiu")
    private String arxiuNom;

    @Schema(name = "contingutBase64", implementation = String.class, example = "JVBERi0xLjYKJcOkw7zDtsOfCjIgMCBv==",
            description = "Contingut del document en Base64.\n * Obligatori si no està informat l’enllaç extern, l’uuid o el csv. * Mida màxima 10Mb")
    private String contingutBase64;

    @Size(max = 256)
    @Schema(name = "uuid", implementation = String.class, example = "00000000-0000-0000-0000-000000000000",
            description = "Codi uuid que es pot utilitzar per tal d’obtenir el document imprimible del sistema d’arxiu\n" +
                    " * Obligatori si no està informat l’enllaç extern, el contingut en Base64 o el csv.")
    private String uuid;

    @Size(max = 256)
    @Schema(name = "csv", implementation = String.class, example = "aa10b27d8e221cf923715f01e01f515fa7cfec1322b6b49644a27a00461defea",
            description = "Codi csv que es pot utilitzar per tal d’obtenir el document imprimible del sistema d’arxiu\n" +
                    " * Obligatori si no està informat l’enllaç extern, el contingut en Base64 o l'uuid.")

    private String csv;
    @Size(max = 256)
    @Schema(name = "url", implementation = String.class, example = "http://server_path/document/123456.pdf",
            description = "Enllaç extern on es troba el document de l’enviament.\n" +
                    " * Obligatori si no està informat l’uuid, el contingut en Base64 o el csv. * Sistema actualment NO soportat.")
    private String url;

    @Schema(name = "normalitzat", implementation = Boolean.class, example = "false", defaultValue = "false",
            description = "Indica si el document està normalitzat per a la impressió al CIE. \n * Només aplica per enviaments postals.")
    private boolean normalitzat;

    @Schema(name = "origen", implementation = OrigenEnum.class, example = "ADMINISTRACIO", defaultValue = "ADMINISTRACIO",
            description = "Enumerat que indica l’origen del document.\n" +
                    " * No s’utilitza en el cas de documents passats com a csv o uuid")
    private OrigenEnum origen;

    @Schema(name = "tipoDocumental", implementation = TipusDocumentalEnum.class, example = "INFORME", defaultValue = "NOTIFICACIO",
            description = "Enumerat que indica el tipus de document.\n * No s’utilitza en el cas de documents passats com a csv o uuid")
    private TipusDocumentalEnum tipoDocumental;

    @Schema(name = "validesa", implementation = ValidesaEnum.class, example = "ORIGINAL", defaultValue = "ORIGINAL",
            description = "Enumerat que indica la validesa del document * No s’utilitza en el cas de documents passats com a csv o uuid")
    private ValidesaEnum validesa;

    @Schema(name = "modoFirma", implementation = Boolean.class, example = "false",
            description = "Indica, en cas de document pdf, si aquest està firmat electrònicament.\n * No s’utilitza en el cas de documents passats com a csv o uuid.")
    private Boolean modoFirma;
}
