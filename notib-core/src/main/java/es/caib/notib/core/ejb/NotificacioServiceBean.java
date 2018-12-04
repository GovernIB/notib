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
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public NotificacioDto findAmbId(Long id) {
		return delegate.findAmbId(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public PaginaDto<NotificacioDto> findAmbFiltrePaginat(
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<NotificacioEnviamentDto> enviamentFindAmbNotificacio(
			Long notificacioId) {
		return delegate.enviamentFindAmbNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public NotificacioEnviamentDto enviamentFindAmbId(
			Long enviamentId) {
		return delegate.enviamentFindAmbId(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long notificacioId) {
		return delegate.eventFindAmbNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<NotificacioEventDto> eventFindAmbEnviament(
			Long notificacioId,
			Long enviamentId) {
		return delegate.eventFindAmbEnviament(
				notificacioId,
				enviamentId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public ArxiuDto getDocumentArxiu(
			Long notificacioId) {
		return delegate.getDocumentArxiu(notificacioId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
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
	@RolesAllowed({"NOT_ADMIN"})
	public boolean enviamentCertificacioSeu(
			Long enviamentId,
			ArxiuDto certificacioArxiu) {
		return delegate.enviamentCertificacioSeu(
				enviamentId,
				certificacioArxiu);
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
	public void seuConsultaEstatNotificacions() {
		delegate.seuConsultaEstatNotificacions();
	}

	@Override
	public void notificaInformaCanviEstatSeu() {
		delegate.notificaInformaCanviEstatSeu();
	}
	
	@Override
	public void enviamentRefrescarEstatPendents() {
		delegate.enviamentRefrescarEstatPendents();
	}

	

}
