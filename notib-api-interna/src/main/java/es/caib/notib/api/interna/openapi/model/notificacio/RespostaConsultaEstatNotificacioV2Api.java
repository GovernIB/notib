
package es.caib.notib.api.interna.openapi.model.notificacio;

import es.caib.notib.client.domini.NotificacioEstatEnum;
import es.caib.notib.client.domini.Procediment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;

/**
 * Informació retornada per la consulta de l'estat d'una notificació.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "RespostaConsultaEstatNotificacioV2")
public class RespostaConsultaEstatNotificacioV2Api extends RespostaBaseApi {

    @Schema(name = "identificador", implementation = String.class, example = "00000000-0000-0000-0000-000000000000", description = "Identificador de la notificació a Notib")
    private String identificador;
    @Schema(name = "estat", implementation = NotificacioEstatEnum.class, example = "FINALITZADA", description = "Enumerat amb la informació de l'estat de l'enviament")
    private NotificacioEstatEnum estat;
    @Schema(name = "tipus", implementation = String.class, example = "NOTIFICACIO", description = "Indica si l’enviament és una comunicació o una notificació")
    private String tipus;
    @Schema(name = "emisorDir3", implementation = String.class, example = "A04003003", description = "Codi Dir3 de l’organisme emisor\n(Entitat de Notib)")
    private String emisorDir3;
    @Schema(name = "procediment", implementation = ProcedimentApi.class, description = "Procediment al que pertany la notificació")
    private Procediment procediment;
    @Schema(name = "organGestorDir3", implementation = String.class, example = "A04035965", description = "Codi DIR3 de l’òrgan gestor que realitza la notificació/comunicació")
    private String organGestorDir3;
    @Schema(name = "concepte", implementation = String.class, example = "Concepte de la notificació", description = "Concepte de la notificació")
    private String concepte;
    @Schema(name = "numExpedient", implementation = String.class, example = "123/2023", description = "Identificador de l'expedient al qual pertany la notificació")
    private String numExpedient;

    @Schema(name = "dataCreada", implementation = Long.class, example = "1706168093962", description = "Data en que s'ha donat d'alta la notificació")
    private Date dataCreada;
    @Schema(name = "dataEnviada", implementation = Long.class, example = "1706168093962", description = "Data en que s'ha enviat la notificació a Notifica (o SIR)")
    private Date dataEnviada;
    @Schema(name = "dataFinalitzada", implementation = Long.class, example = "1706168093962", description = "Data en que s'ha finalitzat la notificació")
    private Date dataFinalitzada;
    @Schema(name = "dataProcessada", implementation = Long.class, example = "1706168093962", description = "Data en que s'ha marcat la notificació com a processada")
    private Date dataProcessada;

}