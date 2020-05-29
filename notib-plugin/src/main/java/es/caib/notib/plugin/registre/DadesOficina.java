package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació sobre l'oficina de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class DadesOficina {

	private String organ;
	private String oficinaCodi;
	private String oficinaNom;
	private String llibreCodi;
	private String llibreNom;
	private String oficinaFisica;

}
