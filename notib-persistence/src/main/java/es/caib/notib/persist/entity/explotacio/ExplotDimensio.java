package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ExplotDimensio {

    private final Long entitatId;
    private final Long procedimentId;
    private final String organCodi;
    private final String usuariCodi;
    private final EnviamentTipus tipus;
    private final EnviamentOrigen origen;

}
