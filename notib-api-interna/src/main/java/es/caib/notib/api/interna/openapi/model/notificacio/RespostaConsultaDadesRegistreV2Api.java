
package es.caib.notib.api.interna.openapi.model.notificacio;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Date;

@Getter
@Schema(name = "RespostaConsultaDadesRegistreV2")
public class RespostaConsultaDadesRegistreV2Api extends RespostaBaseApi {

    @Schema(name = "numRegistre", implementation = Integer.class, example = "1753", description = "Número de registre")
    private int numRegistre;
    @Schema(name = "numeroFormatat", implementation = String.class, example = "GOIBS1753/2023", description = "Número de registre amb format")
    private String numRegistreFormatat;
    @Schema(name = "dataRegistre", implementation = Long.class, example = "1706168093962", description = "Data del registre")
    private Date dataRegistre;
    @Schema(name = "justificant", description = "Contingut del fitxer justificant")
    private byte[] justificant;
    @Schema(name = "oficina", implementation = String.class, example = "Oficina Virtual", description = "Oficina on s'ha realitzat el registre")
    private String oficina;
    @Schema(name = "llibre", implementation = String.class, example = "Govern de les Illes Balears", description = "Llibre on s'ha anotat el registre")
    private String llibre;
    @Schema(name = "enviamentSir", implementation = Boolean.class, example = "false", description = "Indica si s'ha realitzat l'enviament via SIR")
    private boolean enviamentSir;
    @Schema(name = "dataRecepcioSir", implementation = Long.class, example = "1706168093962", description = "En cas d'enviament SIR, data en que s'ha recepcionat el registre")
    private Date dataRecepcioSir;
    @Schema(name = "dataRegistreDestiSir", implementation = Long.class, example = "1706168093962", description = "En cas d'enviament SIR, data en que s'ha registrat en el registre de destí")
    private Date dataRegistreDestiSir;

}