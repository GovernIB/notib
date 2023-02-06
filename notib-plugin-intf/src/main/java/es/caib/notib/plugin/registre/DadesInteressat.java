package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació sobre l'interessat d'una anotació de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class DadesInteressat {

	private Interessat interessat;
	private Interessat representat;

}
