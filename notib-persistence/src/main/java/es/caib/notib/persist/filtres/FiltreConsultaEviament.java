package es.caib.notib.persist.filtres;

import es.caib.notib.client.domini.EnviamentTipus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.util.Date;

@Builder
@Getter
@Setter
@Slf4j
public class FiltreConsultaEviament {

    private String dniTitular;
    private boolean esDataInicialNull;
    private Date dataInicial;
    private boolean esDataFinalNull;
    private Date dataFinal;
    private EnviamentTipus tipus;
    private boolean tipusNull;
    private boolean esEstatFinalNull;
    private Boolean estatFinal;
    private boolean esVisibleCarpetaNull;
    private Boolean visibleCarpeta;
    private Pageable pageable;
}
