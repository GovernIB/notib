package es.caib.notib.logic.intf.dto.missatges;

import es.caib.comanda.model.v1.avis.AvisTipus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class MissatgeComanda {

    private Long id;
    private AvisTipus tipus;
}
