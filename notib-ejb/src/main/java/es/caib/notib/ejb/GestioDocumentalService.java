/**
 * 
 */
package es.caib.notib.ejb;

import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de NotificacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class GestioDocumentalService extends AbstractService<es.caib.notib.logic.intf.service.GestioDocumentalService> implements es.caib.notib.logic.intf.service.GestioDocumentalService {

	@Override
	@RolesAllowed("**")
	public String guardarArxiuTemporal(String nom){
		return getDelegateService().guardarArxiuTemporal(nom);
	}

	@Override
	@RolesAllowed("**")
	public byte[] obtenirArxiuTemporal(String arxiuGestdocId, boolean isZip) {
		return getDelegateService().obtenirArxiuTemporal(arxiuGestdocId, isZip);
	}
	@Override
	@RolesAllowed("**")
	public byte[] obtenirArxiuNotificacio(String arxiuGestdocId) {
		return getDelegateService().obtenirArxiuNotificacio(arxiuGestdocId);
	}

}