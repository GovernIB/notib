/**
 * 
 */
package es.caib.notib.ejb;

import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementació de AuditaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AuditService extends AbstractService<es.caib.notib.logic.intf.service.AuditService> implements es.caib.notib.logic.intf.service.AuditService {

	@Autowired
	AuditService delegate;

	@Override
	public void audita(
			Object objecteAuditar, 
			TipusOperacio tipusOperacio, 
			TipusEntitat tipusEntitat,
			TipusObjecte tipusObjecte, 
			String joinPoint) {
		delegate.audita(
				objecteAuditar, 
				tipusOperacio, 
				tipusEntitat, 
				tipusObjecte, 
				joinPoint);
	}
	
}
