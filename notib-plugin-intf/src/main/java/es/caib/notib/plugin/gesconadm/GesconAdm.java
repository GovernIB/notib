package es.caib.notib.plugin.gesconadm;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GesconAdm {

    private String codiSIA;
    private String nom;
    private String unitatAdministrativacodi;
    private boolean comu;
    private Date dataActualitzacio;
}
