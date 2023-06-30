package es.caib.notib.logic.objectes;

import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssentamentRegistralParams {

    private NotificacioEntity not;
    private NotificacioEnviamentEntity env;
    private boolean isSirActivat;
    private  boolean isComunicacio;
    private String dir3Codi;
    private IntegracioInfo info;
    private long t0;
    private boolean isComSir;
    private boolean totsAdministracio;
    private RespostaConsultaRegistre arbResposta;
    private boolean error = false;
    private String errorDescripcio = null;
    private boolean errorMaxReintents = false;
    private int errorMaxReintentsProperty;
}
