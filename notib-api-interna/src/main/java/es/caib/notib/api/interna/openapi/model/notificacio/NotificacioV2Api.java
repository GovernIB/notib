
package es.caib.notib.api.interna.openapi.model.notificacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una notificació per al seu enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "NotificacioV2")
public class NotificacioV2Api {

    @Size(max = 9)
    @Schema(name = "emisorDir3Codi", implementation = String.class, example = "A04003003", required = true,
            description = "Codi Dir3 de l’organisme emisor" +
                    "\n(Entitat de Notib)")
    private String emisorDir3Codi;
    @Size(max = 9)
    @Schema(name = "organGestor", implementation = String.class, example = "A04035965",
            description = "Codi DIR3 de l’òrgan gestor que realitza la notificació/comunicació.\n" +
                    " * Obligatori en el cas de procediments comuns.\n" +
                    " * En el cas de notificacions de procediments no comuns, no s'utilitzarà aquest camp, sinó que s’utilitzarà l’òrgan gestor al que pertany el procediment.")
    private String organGestor;
    @Schema(name = "enviamentTipus", implementation = EnviamentTipus.class, defaultValue = "COMUNICACIO", example = "NOTIFICACIO", required = true,
            description = "Enumerat que indica si l’enviament és una comunicació o una notificació")
    private EnviamentTipus enviamentTipus;
    @Size(max = 240)
    @Schema(name = "concepte", implementation = String.class, example = "Concepte de la notificació", required = true,
            description = "Concepte de l’enviament. \n" +
                    " * Si ha d’anar a CIE només s’agafaran els 50 primers caràcters")
    private String concepte;
    @Size(max = 1000)
    @Schema(name = "descripcio", implementation = String.class, example = "Descripció de la notificació", required = false,
            description = "Descripció detallada de l’enviament")
    private String descripcio;
    @Schema(name = "enviamentDataProgramada", implementation = Date.class, example = "2023-05-25", required = false,
            description = "Data en la que l’enviament es posarà a disposició per a la compareixença.\n" +
                    " * En cas de no informar-se es posarà en disposició per a la compareixença de forma inmediata.\n\n" +
                    "El format de la data serà del tipus yyyy-MM-dd")
    private Date enviamentDataProgramada;
    @Schema(name = "retard", implementation = Integer.class, example = "5", required = false,
            description = "Dies que l’enviament estarà a disposició de compareixença en carpeta abans d’entregar-lo per altres mitjans")
    private Integer retard;
    @Schema(name = "caducitat", implementation = Date.class, example = "2023-05-25", required = false,
            description = "Data d’expiració de l’enviament.\n" +
                    " * Aquest camp és excloent amb el de caducitatDiesNaturals. Només s'ha d'indicar un dels dos. En cas d'informar els dos, s'ignorarà el camp caducitatDiesNaturals.\n" +
                    " * Aquest camp és obligatori per notificacions i opcional per a comunicacions.\n\n" +
                    "El format de la data serà del tipus yyyy-MM-dd")
    private Date caducitat;
    @Schema(name = "caducitatDiesNaturals", implementation = Integer.class, example = "10", required = false,
            description = "Dies naturals abans que expiri l'enviament\n" +
                    " * Aquest camp és excloent amb el de caducitat. Només s'ha d'indicar un dels dos. En cas d'informar els dos, aquest s'ignorarà.")
    private Integer caducitatDiesNaturals;
    @Size(max = 64)
    @Schema(name = "usuariCodi", implementation = String.class, example = "u000000", required = true,
            description = "Codi de l’usuari que està realitzant la notificació.\n" +
                    "Aquest usuari s'utilitzarà per a realitzar l'assentament registral de sortida")
    private String usuariCodi;
    @Size(max = 64)
    @Schema(name = "procedimentCodi", implementation = String.class, example = "666666", required = false,
            description = "Identificador del procediment SIA al que pertany la notificació.\n" +
                    "Obligatori en el cas de notificacions")
    private String procedimentCodi;
    @Size(max = 64)
    @Schema(name = "grupCodi", implementation = String.class, example = "NOT_DG_DESTECNOLOGIC", required = false,
            description = "Codi del grup al que s’assigna la notificació.\n " +
                    "* Aquest camp només té sentit quan es vol donar d’alta una notificació que s’ha configurat amb grups a Notifica (veure manual d’usuari)")
    private String grupCodi;
    @Size(max = 80)
    @Schema(name = "numExpedient", implementation = String.class, example = "123/2023", required = false,
            description = "Identificador de l'expedient al qual pertany la notificació")
    private String numExpedient;
    @Schema(name = "enviaments", required = true,
            description = "Llista d'enviaments continguts en la Notificació/Comunicació")
    private List<EnviamentApi> enviaments;
    @Schema(name = "idioma", implementation = Idioma.class, defaultValue = "CA", example = "ES", required = false,
            description = "Enumerat que indica l’idioma de la notificació")
    private Idioma idioma;
    @Schema(name = "document", implementation = DocumentApi.class, required = true,
            description = "Document que s’envia en la notificació.")
    private DocumentApi document;
    @Schema(name = "document2", implementation = DocumentApi.class, required = false,
            description = "Document que s’envia en la comunicació.\n" +
                    " * Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)")
    private DocumentApi document2;
    @Schema(name = "document3", implementation = DocumentApi.class, required = false,
            description = "Document que s’envia en la comunicació.\n" +
                    " * Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)")
    private DocumentApi document3;
    @Schema(name = "document4", implementation = DocumentApi.class, required = false,
            description = "Document que s’envia en la comunicació.\n" +
                    " * Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)")
    private DocumentApi document4;
    @Schema(name = "document5", implementation = DocumentApi.class, required = false,
            description = "Document que s’envia en la comunicació.\n" +
                    " * Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)")
    private DocumentApi document5;

}
