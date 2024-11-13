
package es.caib.notib.client.domini.ampliarPlazo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmpliacionesPlazo {

    protected List<AmpliacionPlazo> ampliacionPlazo = new ArrayList<>();
}
