/**
 * 
 */
package es.caib.notib.core.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.LocalitatsDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.core.api.dto.NotificacioErrorCallbackFiltreDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PaisosDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProvinciesDto;
import es.caib.notib.core.api.dto.RegistreIdDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.NotificacioService;

/**
 * Implementació de NotificacioService com a EJB que empra una clase
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
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public NotificacioDtoV2 findAmbId(
			Long id,
			boolean isAdministrador) {
		return delegate.findAmbId(
				id,
				isAdministrador);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long entitatId,
			Long notificacioId) {
		return delegate.eventFindAmbNotificacio(
				entitatId,
				notificacioId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER"})	
	public NotificacioEventDto findUltimEventCallbackByNotificacio(
			Long notificacioId) {
		return delegate.findUltimEventCallbackByNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public List<NotificacioEventDto> eventFindAmbEnviament(
			Long entitatId,
			Long notificacioId,
			Long enviamentId) {
		return delegate.eventFindAmbEnviament(
				entitatId,
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
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public boolean enviar(Long notificacioId) {
		return delegate.enviar(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(
			Long entitatId,
			Long enviamentId) {
		return delegate.enviamentRefrescarEstat(
				entitatId,
				enviamentId);
	}

//	@Override
//	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
//	public void notificaEnviamentsRegistrats() {
//		delegate.notificaEnviamentsRegistrats();
//	}
//	
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
//	public void enviamentRefrescarEstatPendents() {
//		delegate.enviamentRefrescarEstatPendents();
//	}
//
//	@Override
//	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
//	public void registrarEnviamentsPendents() {
//		delegate.registrarEnviamentsPendents();
//	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public List<NotificacioDto> create(
			Long entitatId, 
			NotificacioDtoV2 notificacio) {
		return delegate.create(entitatId, notificacio);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public NotificacioDtoV2 update(
			Long entitatId,
			NotificacioDtoV2 notificacio) throws NotFoundException {
		return delegate.update(
				entitatId, 
				notificacio);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<ProcedimentDto> findProcedimentsEntitatAmbPermisConsulta(EntitatDto entitat) {
		return delegate.findProcedimentsEntitatAmbPermisConsulta(entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<ProcedimentDto> findProcedimentsAmbPermisNotificacio(EntitatDto entitat) {
		return delegate.findProcedimentsAmbPermisNotificacio(entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public PaginaDto<NotificacioDto> findAmbFiltrePaginat(
			Long entitatId,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isSuperUsuari,
			boolean isAdministradorOrgan,
			List<String> codisProcedimentsDisponibles,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(
				entitatId,
				isUsuari,
				isUsuariEntitat,
				isSuperUsuari,
				isAdministradorOrgan,
				codisProcedimentsDisponibles,
				filtre,
				paginacioParams);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<ProcedimentDto> findProcedimentsAmbPermisConsultaAndGrupsAndEntitat(
			Map<String, ProcedimentDto> procediments,
			EntitatDto entitat) {
		return delegate.findProcedimentsAmbPermisConsultaAndGrupsAndEntitat(
				procediments,
				entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<ProcedimentDto> findProcedimentsAmbPermisNotificacioAndGrupsAndEntitat(
			Map<String, ProcedimentDto> procediments,
			EntitatDto entitat) {
		return delegate.findProcedimentsAmbPermisNotificacioAndGrupsAndEntitat(
				procediments,
				entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<ProcedimentDto> findProcedimentsAmbPermisNotificacioSenseGrupsAndEntitat(
			List<ProcedimentDto> procediments,
			EntitatDto entitat) {
		return delegate.findProcedimentsAmbPermisNotificacioSenseGrupsAndEntitat(
				procediments,
				entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<ProcedimentDto> findProcedimentsAmbPermisConsultaSenseGrupsAndEntitat(
			List<ProcedimentDto> procediments,
			EntitatDto entitat) {
		return delegate.findProcedimentsAmbPermisConsultaSenseGrupsAndEntitat(
				procediments,
				entitat);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public String marcarComProcessada(
			Long enviamentId,
			String motiu) throws MessagingException {
		return delegate.marcarComProcessada(
				enviamentId,
				motiu);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER", "NOT_APL"})
	public List<RegistreIdDto> registrarNotificar(Long notificacioId) {
		return delegate.registrarNotificar(notificacioId);
	}


	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<ProcedimentDto> findProcedimentsAmbPermisConsulta() {
		return delegate.findProcedimentsAmbPermisConsulta();
	}

	@Override
	@RolesAllowed({"NOT_USER"})
	public List<ProvinciesDto> llistarProvincies() {
		return delegate.llistarProvincies();
	}

	@Override
	@RolesAllowed({"NOT_USER"})
	public List<LocalitatsDto> llistarLocalitats(String codiProvincia) {
		return delegate.llistarLocalitats(codiProvincia);
	}

	@Override
	@RolesAllowed({"NOT_USER"})
	public List<PaisosDto> llistarPaisos() {
		return delegate.llistarPaisos();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getNotificacionsPendentsRegistrar() {
		return delegate.getNotificacionsPendentsRegistrar();
	}

	@Override
	public void notificacioRegistrar(Long notificacioId) {
		delegate.notificacioRegistrar(notificacioId);		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List getNotificacionsPendentsEnviar() {
		return delegate.getNotificacionsPendentsEnviar();
	}

	@Override
	public void notificacioEnviar(Long notificacioId) {
		delegate.notificacioEnviar(notificacioId);		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getNotificacionsPendentsRefrescarEstat() {
		return delegate.getNotificacionsPendentsRefrescarEstat();
	}
	
	@Override
	public void enviamentRefrescarEstat(Long notificacioId) {
		delegate.enviamentRefrescarEstat(notificacioId);		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getNotificacionsPendentsRefrescarEstatRegistre() {
		return delegate.getNotificacionsPendentsRefrescarEstatRegistre();
	}

	@Override
	public void enviamentRefrescarEstatRegistre(Long notificacioId) {
		delegate.enviamentRefrescarEstatRegistre(notificacioId);		
	}

	@Override
	@RolesAllowed({"NOT_SUPER"})
	public PaginaDto<NotificacioDto> findWithCallbackError(
			NotificacioErrorCallbackFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findWithCallbackError(filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public boolean reactivarConsulta(Long notificacioId) {
		return delegate.reactivarConsulta(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public boolean reactivarSir(Long notificacioId) {
		return delegate.reactivarSir(notificacioId);
	}

}
