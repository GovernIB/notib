package es.caib.notib.plugin.gesconadm;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Procediments.
 *
 */
@Getter @Setter
public class GcaProcediment {
	
	private String codiSIA;
    private String nom;
    private String unitatAdministrativacodi;
    private boolean comu;
    private Date dataActualitzacio;
//    private GdaUnitatAdministrativa unidadAdministrativa;
//    private GdaUnitatAdministrativa unitatAdministrativaPare;

}
