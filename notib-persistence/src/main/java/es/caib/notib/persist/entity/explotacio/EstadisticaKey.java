package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import es.caib.notib.client.domini.EnviamentTipus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticaKey {
    Long entitatId;
    Long procedimentId;
    String organCodi;
    EnviamentTipus tipus;
    EnviamentOrigen origen;
}
