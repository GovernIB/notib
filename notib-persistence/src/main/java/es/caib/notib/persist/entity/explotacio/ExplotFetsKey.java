package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ExplotFetsKey {
    private Long entitatId;
    private Long procedimentId;
    private String organCodi;
    private String usuariCodi;
    private EnviamentTipus tipus;
    private EnviamentOrigen origen;
}
