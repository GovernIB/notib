package es.caib.notib.logic.intf.resourceservice;

import es.caib.notib.logic.intf.base.service.MutableResourceService;
import es.caib.notib.logic.intf.model.UsuariResource;

/**
 * Definició del servei de gestió d'usuaris de l'aplicació.
 *
 * @author Límit Tecnologies
 */
public interface UsuariResourceService extends MutableResourceService<UsuariResource, String> {

	/**
	 * Refresca la informació de l'usuari autenticat.
	 */
	void refresh();

}
