/**
 * 
 */
package es.caib.notib.core.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto;
import es.caib.notib.core.api.dto.NotificaRespostaEstatDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
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
	@RolesAllowed({"NOT_APL"})
	public NotificacioDto alta(
			String entitatDir3Codi,
			NotificacioDto notificacio) {
		return delegate.alta(entitatDir3Codi, notificacio);
	}

	@Override
	@RolesAllowed({"NOT_APL"})
	public NotificacioDto consulta(
			String referencia) {
		return delegate.consulta(referencia);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public NotificacioDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public PaginaDto<NotificacioDto> findByFiltrePaginat(
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findByFiltrePaginat(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_REP"})
	public PaginaDto<NotificacioDto> findByEntitatIFiltrePaginat(
			Long entitatId,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findByEntitatIFiltrePaginat(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public List<NotificacioDestinatariDto> destinatariFindByNotificacio(
			Long notificacioId) {
		return delegate.destinatariFindByNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public NotificacioDestinatariDto destinatariFindById(
			Long destinatariId) {
		return delegate.destinatariFindById(destinatariId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public NotificacioDestinatariDto destinatariFindByReferencia(
			String referencia) {
		return delegate.destinatariFindByReferencia(referencia);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public List<NotificacioEventDto> eventFindByNotificacio(
			Long notificacioId) {
		return delegate.eventFindByNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public List<NotificacioEventDto> eventFindByNotificacioIDestinatari(
			Long notificacioId,
			Long destinatariId) {
		return delegate.eventFindByNotificacioIDestinatari(
				notificacioId,
				destinatariId);
	}
	
	@Override
	public FitxerDto findCertificacio(
			String referencia) {
		return delegate.findCertificacio(referencia);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_REP"})
	public FitxerDto findFitxer(
			Long notificacioId) {
		return delegate.findFitxer(notificacioId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void updateDestinatariEstat(
			String referencia,
			NotificacioDestinatariEstatEnumDto notificaEstat,
			Date notificaEstatData,
			String notificaEstatReceptorNom,
			String notificaEstatReceptorNif,
			String notificaEstatOrigen,
			String notificaEstatNumSeguiment,
			NotificacioDestinatariEstatEnumDto seuEstat) {
		delegate.updateDestinatariEstat( 
				referencia,
				notificaEstat,
				notificaEstatData,
				notificaEstatReceptorNom,
				notificaEstatReceptorNif,
				notificaEstatOrigen,
				notificaEstatNumSeguiment,
				seuEstat);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN"})
	public void updateCertificacio(
			String referencia,
			NotificaCertificacioTipusEnumDto notificaCertificacioTipus,
			NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus,
			String notificaCertificacioArxiuId,
			String notificaCertificacioNumSeguiment,
			Date notificaCertificacioDataActualitzacio) {
		delegate.updateCertificacio(
				referencia,
				notificaCertificacioTipus,
				notificaCertificacioArxiuTipus,
				notificaCertificacioArxiuId,
				notificaCertificacioNumSeguiment,
				notificaCertificacioDataActualitzacio);
	}

	public void enviar(
			Long notificacioId) {
		delegate.enviar(notificacioId);
	}

	@Override
	public NotificacioDto consultarInformacio(
			String referencia) {
		return delegate.consultarInformacio(referencia);
	}

	@Override
	public NotificaRespostaEstatDto consultarEstat(
			String referencia) {
		return delegate.consultarEstat(referencia);
	}

	@Override
	public NotificaRespostaDatatDto consultarDatat(
			String referencia) {
		return delegate.consultarDatat(referencia);
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
