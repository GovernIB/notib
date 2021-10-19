package es.caib.notib.plugin.gesconadm;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Procediments.
 *
 */
@Getter @Setter
public class GcaServei {
	
	private String codiSIA;
    private String nom;
    private String unitatAdministrativacodi;
    private boolean comu;
    private Date dataActualitzacio;

}
