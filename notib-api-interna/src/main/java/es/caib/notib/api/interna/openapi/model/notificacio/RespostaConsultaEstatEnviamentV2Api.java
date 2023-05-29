
package es.caib.notib.api.interna.openapi.model.notificacio;

import es.caib.notib.client.domini.EnviamentEstat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;
import java.util.List;

/**
 * Informació retornada per la consulta de l'estat d'un enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "RespostaConsultaEstatEnviamentV2")
public class RespostaConsultaEstatEnviamentV2Api extends RespostaBaseApi {

    @Schema(name = "identificador", implementation = String.class, example = "00000000-0000-0000-0000-000000000000",
            description = "Identificador de la notificació a Notib")
    private String identificador;
    @Schema(name = "referencia", implementation = String.class, example = "00000000-0000-0000-0000-000000000000",
            description = "Identificador de l'enviament a Notib")
    private String referencia;
    @Schema(name = "notificaIndentificador", implementation = String.class, example = "QNzVj5Z9BvbLyftgqDhP",
            description = "Identificador de l'enviament a Notific@")
    private String notificaIndentificador;
    @Schema(name = "estat", implementation = EnviamentEstat.class, example = "NOTIFICADA",
            description = "Enumerat amb la informació de l'estat de l'enviament")
    private EnviamentEstat estat;
    @Schema(name = "estatData", implementation = Date.class, example = "2023-05-29T07:32:03.526+0000",
            description = "Data en que s'ha assignat l'estat actual de l'enviament")
    private Date estatData;
    @Schema(name = "estatDescripcio", implementation = String.class, example = "notificada",
            description = "Descripció de l'estat de l'enviament")
    private String estatDescripcio;
    @Schema(name = "enviamentSir", implementation = Boolean.class, example = "false",
            description = "Indica si l'enviament s'ha realitzat a través de SIR")
    private boolean enviamentSir;
    @Schema(name = "dehObligat", implementation = Boolean.class, example = "false",
            description = "Indica si el destinatari està obligat a rebre les notificacions al DEH")
    private boolean dehObligat;
    @Schema(name = "dehNif", implementation = String.class, example = "00000000T",
            description = "Nif a utilitzar per l'enviamnt a DEH")
    private String dehNif;
    @Schema(name = "entragaPostalActiva", implementation = Boolean.class, example = "false",
            description = "Indica si l'enviament està configurat per enviament postal")
    private boolean entragaPostalActiva;
    @Schema(name = "adressaPostal", implementation = String.class, example = "Carrer Aragó 26bis, 4t - 3, Palma 07003 - Illes Balears",
            description = "Adreça on es realitzarà l'enviament postal")
    private String adressaPostal;
    @Schema(name = "interessat", implementation = PersonaApi.class,
            description = "Interessat al que va adreçat l'enviament")
    private PersonaApi interessat;
    @Schema(name = "representants",
            description = "Representants a qui enviar l'enviament")
    private List<PersonaApi> representants;
    @Schema(name = "registre", implementation = RegistreApi.class,
            description = "Informació de l'apunt de registre de sortida realitzat per l'enviament")
    private RegistreApi registre;
    @Schema(name = "sir", implementation = SirApi.class,
            description = "Informació per a l'enviament SIR")
    private SirApi sir;
    @Schema(name = "datat", implementation = DatatApi.class,
            description = "Informació del datat realitzat a Notific@")
    private DatatApi datat;
    @Schema(name = "certificacio", implementation = CertificacioApi.class,
            description = "Informació de la certificació realitzada a Notific@")
    private CertificacioApi certificacio;

}