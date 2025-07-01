package es.caib.notib.logic.intf.dto.accioMassiva;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccioMassivaDto /*extends AuditoriaDto*/ {

    private Long id;
    protected String createdByCodi;
    protected Date createdDate;
    private AccioMassivaTipus tipus;
    private Date dataInici;
    private Date dataFi;
    private Boolean error;
    private String errorDescripcio;
    private String excepcioStacktrace;
}
