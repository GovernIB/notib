/**
 * 
 */
package es.caib.notib.core.ejb;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.service.AuditService;

/**
 * Implementació de AuditaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AuditServiceBean implements AuditService {

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
