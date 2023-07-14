package es.caib.notib.plugin.unitat;

import es.caib.dir3caib.ws.api.unidad.UnidadTF;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UnitatOrganitzativa extends UnidadTF implements Serializable {

    private String denominacionCooficial;
    private Integer version;
}
