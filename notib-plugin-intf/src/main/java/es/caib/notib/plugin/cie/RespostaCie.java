package es.caib.notib.plugin.cie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RespostaCie {

    private List<IdentificadorCie> identificador = new ArrayList<>();
    private String codiError;
    private String descripcioError;
}
