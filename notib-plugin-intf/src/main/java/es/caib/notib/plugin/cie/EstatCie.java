package es.caib.notib.plugin.cie;

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
public class EstatCie {

    private String identificador;
    private String codiFase;
    private String descripcioFase;
    private String codiEstat;
    private String descripcioEstat;
}
