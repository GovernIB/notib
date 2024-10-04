package es.caib.notib.plugin.cie;

import es.caib.notib.client.domini.CieEstat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InfoCie {

    private String identificador;
    private String codiResposta;
    private String descripcioResposta;
    private CieEstat codiEstat;
    private Date data;
}
