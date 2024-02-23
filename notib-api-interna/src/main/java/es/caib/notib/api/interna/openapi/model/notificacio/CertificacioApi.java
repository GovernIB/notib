
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;


/**
 * Informació sobre la certificació d'una notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "Certificacio")
public class CertificacioApi {

    @Schema(name = "data", implementation = Long.class, example = "1706168093962", description = "Data de creació de la certificació de la notificació")
    private Date data;
    @Schema(name = "origen", implementation = String.class, example = "carpeta", description = "Origen de la certificació")
    private String origen;
    @Schema(name = "contingutBase64", implementation = String.class, example = "JVBERi0xLjYKJcOkw7zDtsOfCjIgMCBv==", description = "Contingut de la certificació en Base64.")
    private String contingutBase64;
    @Schema(name = "tamany", implementation = Integer.class, example = "4265384", description = "Mida de la certificació")
    private int tamany;
    @Schema(name = "hash", implementation = String.class, example = "00000000", description = "Hash de la certificació")
    private String hash;
    @Schema(name = "metadades", implementation = String.class, example = "00000000", description = "Metadades de la certificació")
    private String metadades;
    @Schema(name = "csv", implementation = String.class, example = "aa10b27d8e221cf923715f01e01f515fa7cfec1322b6b49644a27a00461defea", description = "Codi CSV de la certificació")
    private String csv;
    @Schema(name = "tipusMime", implementation = String.class, example = "application/pdf", description = "Tipus MIME de la certificació")
    private String tipusMime;

}
