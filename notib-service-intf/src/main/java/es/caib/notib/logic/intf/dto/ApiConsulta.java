package es.caib.notib.logic.intf.dto;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
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
    private EnviamentTipus tipus;
    private Boolean estatFinal;
    private String basePath;
    private Integer pagina;
    private Integer mida;
    private Date dataInicial;
    private Date dataFinal;
    private Idioma idioma;
    private Boolean visibleCarpeta;

    public void setDniTitular(String dniTitular) {
        this.dniTitular = dniTitular != null ? dniTitular.toUpperCase() : null;
    }
    public Idioma getIdioma() {
        return idioma != null ? idioma : Idioma.CA;
    }
}
