package es.caib.notib.logic.intf.dto.procediment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Strings;

@Builder
@Getter
@Setter
public class ProcedimentConsultaFiltre {

    private String codi;
    private boolean codiNull;
    private String nom;
    private boolean nomNull;
    private String organGestor;
    private boolean organGestorNull;
    private Boolean actiu;
    private Boolean comu;
    private Boolean manual;
    private Boolean entregaCieActiva;
    private Boolean requireDirectPermission;
    private Integer mida;
    private Integer pagina;

    public void setNulls() {

        codiNull = StringUtils.isBlank(codi);
        nomNull = StringUtils.isBlank(nom);
        organGestorNull = StringUtils.isBlank(organGestor);
    }
}
