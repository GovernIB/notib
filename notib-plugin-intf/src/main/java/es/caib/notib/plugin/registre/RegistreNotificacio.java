package es.caib.notib.plugin.registre;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Registre de sortida o notificació
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RegistreNotificacio {

	private DadesExpedient dadesExpedient;
	private DadesOficina dadesOficina;
	private DadesInteressat dadesInteressat;
	private DadesRepresentat dadesRepresentat;
	private DadesNotificacio dadesNotificacio;
	private List<DocumentRegistre_llorenc> documents;

}
