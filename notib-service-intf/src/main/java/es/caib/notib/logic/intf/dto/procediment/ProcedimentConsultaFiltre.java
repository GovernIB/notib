package es.caib.notib.logic.intf.dto.procediment;

import com.google.common.base.Strings;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

        codiNull = Strings.isNullOrEmpty(codi);
        nomNull = Strings.isNullOrEmpty(nom);
        organGestorNull = Strings.isNullOrEmpty(organGestor);
    }
}
