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

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
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
	public NotificacioDto findAmbId(Long id) {
		return delegate.findAmbId(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public PaginaDto<NotificacioDto> findAmbFiltrePaginat(
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_REP"})
	public PaginaDto<NotificacioDto> findAmbEntitatIFiltrePaginat(
			Long entitatId,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbEntitatIFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public List<NotificacioEnviamentDto> enviamentFindAmbNotificacio(
			Long notificacioId) {
		return delegate.enviamentFindAmbNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public NotificacioEnviamentDto enviamentFindAmbId(
			Long enviamentId) {
		return delegate.enviamentFindAmbId(enviamentId);
	}

	@Override
	@RolesAllowed("NOT_APL")
	public NotificacioEnviamentDto enviamentFindAmbReferencia(
			String referencia) {
		return delegate.enviamentFindAmbReferencia(referencia);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long notificacioId) {
		return delegate.eventFindAmbNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public List<NotificacioEventDto> eventFindAmbNotificacioIEnviament(
			Long notificacioId,
			Long enviamentId) {
		return delegate.eventFindAmbNotificacioIEnviament(
				notificacioId,
				enviamentId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public ArxiuDto getDocumentArxiu(
			Long notificacioId) {
		return delegate.getDocumentArxiu(notificacioId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public ArxiuDto enviamentGetCertificacioArxiu(
			Long enviamentId) {
		return delegate.enviamentGetCertificacioArxiu(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public boolean enviar(
			Long notificacioId) {
		return delegate.enviar(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(
			Long enviamentId) {
		return delegate.enviamentRefrescarEstat(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public boolean enviamentComunicacioSeu(
			Long enviamentId) {
		return delegate.enviamentComunicacioSeu(enviamentId);
	}

	@Override
	public void notificaEnviamentsPendents() {
		delegate.notificaEnviamentsPendents();
	}

	@Override
	public void seuEnviamentsPendents() {
		delegate.seuEnviamentsPendents();
	}

	@Override
	public void seuNotificacionsPendents() {
		delegate.seuNotificacionsPendents();
	}

	@Override
	public void seuNotificaComunicarEstatPendents() {
		delegate.seuNotificaComunicarEstatPendents();
	}

}
