/**
 * 
 */
package es.caib.notib.ejb;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;

/**
 * Implementació de AuditaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AuditService extends AbstractService<es.caib.notib.logic.intf.service.AuditService> implements es.caib.notib.logic.intf.service.AuditService {

	@Override
	@PermitAll
	public void audita(
			Object objecteAuditar, 
			TipusOperacio tipusOperacio, 
			TipusEntitat tipusEntitat,
			TipusObjecte tipusObjecte, 
			String joinPoint) {
		getDelegateService().audita(
				objecteAuditar, 
				tipusOperacio, 
				tipusEntitat, 
				tipusObjecte, 
				joinPoint);
	}
	
}
