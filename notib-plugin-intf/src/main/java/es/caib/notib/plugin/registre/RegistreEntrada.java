package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Registre d'entrada
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RegistreEntrada {

	private DadesOficina dadesOficina;
	private DadesInteressat dadesInteressat;
	private DadesRepresentat dadesRepresentat;
	private DadesAnotacio dadesAssumpte;
	private List<DocumentRegistre_llorenc> documents;

}
