/**
 * 
 */
package es.caib.notib.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.NotificacioDestinatariDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.service.NotificacioService;

/**
 * Implementació de EntitatService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class NotificacioServiceBean implements NotificacioService {

	@Autowired
	NotificacioService delegate;
	
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public NotificacioDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public PaginaDto<NotificacioDto> findFilteredByEntitatAndUsuari(
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		
		return delegate.findFilteredByEntitatAndUsuari(filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_REP"})
	public PaginaDto<NotificacioDto> findByEntitat(
			Long entitatId,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		
		return delegate.findByEntitat(entitatId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public PaginaDto<NotificacioDestinatariDto> findDestinatarisByNotificacioId(
			Long notificacioId,
			PaginacioParamsDto paginacioParams) {
		
		return delegate.findDestinatarisByNotificacioId(notificacioId, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public List<NotificacioDestinatariDto> findDestinatarisByNotificacioId(Long notificacioId) {
		
		return delegate.findDestinatarisByNotificacioId(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public List<NotificacioEventDto> findEventsByNotificacioId(Long notificacioId) {
		
		return delegate.findEventsByNotificacioId(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public List<NotificacioEventDto> findEventsByDestinatariId(Long destinatariId) {
		
		return delegate.findEventsByDestinatariId(destinatariId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public NotificacioDestinatariDto findDestinatariById(Long destinatariId) {
		
		return delegate.findDestinatariById(destinatariId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public NotificacioDestinatariDto findDestinatariByReferencia(String referencia) {
		
		return delegate.findDestinatariByReferencia(referencia);
	}

	@Override
	public void seuEnviamentsPendents() {
		delegate.seuEnviamentsPendents();
	}

	@Override
	public void seuJustificantsPendents() {
		delegate.seuJustificantsPendents();
	}

	@Override
	public void seuNotificaComunicarEstatPendents() {
		delegate.seuNotificaComunicarEstatPendents();
	}

	
	

}
