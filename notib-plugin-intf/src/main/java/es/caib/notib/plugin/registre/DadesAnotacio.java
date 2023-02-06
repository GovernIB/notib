package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació sobre l'anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DadesAnotacio {

	private String codiUsuari;
	private String idiomaCodi;
	private String tipusAssumpte;
	private String codiAssumpte;
	private String extracte;
	private String unitatAdministrativa;
	private String registreNumero;
	private String registreAny;
	private Long docfisica;
	private String numExpedient;
	private String observacions;
	private String refExterna;

}
