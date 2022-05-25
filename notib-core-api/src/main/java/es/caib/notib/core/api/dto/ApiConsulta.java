package es.caib.notib.core.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Builder
public class ApiConsulta {

    private String dniTitular;
    private NotificaEnviamentTipusEnumDto tipus;
    private Boolean estatFinal;
    private String basePath;
    private Integer pagina;
    private Integer mida;
    private Date dataInicial;
    private Date dataFinal;

    public void setDniTitular(String dniTitular) {
        this.dniTitular = dniTitular != null ? dniTitular.toUpperCase() : null;
    }
}
