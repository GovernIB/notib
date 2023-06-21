
package es.caib.notib.api.interna.openapi.model.notificacio;

import es.caib.notib.client.domini.NotificaServeiTipusEnumDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;


/**
 * Informació d'un enviament d'una notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "Enviament")
public class EnviamentApi {

    @Schema(name = "titular", implementation = PersonaApi.class, required = true, description = "Titular a qui s'adreça la notificació")
    private PersonaApi titular;

    @Schema(name = "destinataris", description = "Conjunt de destinataris als que s'enviarà la notificació.\n" +
            " * Depenent de l'entitat es permet informar únicament un destinatari per enviament a múltiples destinataris (Ex. la CAIB únicamnet admet un destinatari.\n" +
            " * En cas d'informar múltiples destinataris, només que un d'ells accepti la notificació, aquesta es dóna per notificada.")
    private List<PersonaApi> destinataris;

    @Schema(name = "entregaPostalActiva", implementation = Boolean.class, example = "false",
            description = "Indica si s'ha de realitzar entrega postal\n" +
                    "(Consultar prèviament si està disponible l'entrega postal a l'òrgan gestor emissor)")
    private boolean entregaPostalActiva;

    @Schema(name = "entregaPostal", implementation = EntregaPostalApi.class,
            description = "Objecte amb les dades necessàries per a realitzar una entrega postal\n" +
                    " * Obligatori únicament en cas d'indicar que es realitza entrega postal" +
                    "(Consultar prèviament si està disponible l'entrega postal a l'òrgan gestor emissor)")
    private EntregaPostalApi entregaPostal;

    @Schema(name = "entregaDehActiva", implementation = Boolean.class, example = "false", defaultValue = "false",
            description = "Indica si s'ha de realitzar l'enviament a la DEH\n" +
                    "__Actualment en desús amb l'entrada en funcionament de la DEHú__")
    private boolean entregaDehActiva;

    @Schema(name = "entregaDeh", implementation = EntregaDehApi.class,
            description = "Identificador del procediment SIA al que pertany la notificació" +
                    " * Obligatori únicament en cas d'indicar que es realitza entrega DEH\n" +
                    "__Actualment en desús amb l'entrada en funcionament de la DEHú__")
    private EntregaDehApi entregaDeh;

    @Schema(name = "serveiTipus", implementation = NotificaServeiTipusEnumDto.class, example = "NORMAL",
            description = "Enumerat que indica la urgència que té l’enviament.")
    private NotificaServeiTipusEnumDto serveiTipus;

}