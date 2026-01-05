package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ExplotFetsKey implements Serializable {
    private static final long serialVersionUID = 2524275859893279117L;

    private Long entitatId;
    private Long procedimentId;
    private String organCodi;
    private String usuariCodi;
    private EnviamentTipus tipus;
    private EnviamentOrigen origen;
}
