/**
 * 
 */
package es.caib.notib.core.ejb;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.ColumnesDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentFiltreDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.EnviamentService;

/**
 * Implementació de EnviamentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EnviamentServiceBean implements EnviamentService {

	@Autowired
	EnviamentService delegate;


	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<Long> findIdsAmbFiltre(
			Long entitatId, 
			NotificacioEnviamentFiltreDto filtre) throws NotFoundException, ParseException {
		return delegate.findIdsAmbFiltre(entitatId, filtre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public PaginaDto<NotificacioEnviamentDtoV2> enviamentFindByEntityAndFiltre(
			EntitatDto entitat, 
			boolean isUsuari,
			boolean isUsuariEntitat, 
			List<ProcedimentGrupDto> grupsProcediments,
			List<ProcedimentDto> procediments, 
			NotificacioEnviamentFiltreDto filtre, 
			PaginacioParamsDto paginacio)
			throws ParseException {
		return delegate.enviamentFindByEntityAndFiltre(
				entitat, 
				isUsuari, 
				isUsuariEntitat, 
				grupsProcediments, 
				procediments, 
				filtre, 
				paginacio);
	}


	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<NotificacioEnviamentDto> enviamentFindAmbNotificacio(Long notificacioId) {
		return delegate.enviamentFindAmbNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public NotificacioEnviamentDto enviamentFindAmbId(Long enviamentId) {
		return delegate.enviamentFindAmbId(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public List<NotificacioEventDto> eventFindAmbNotificacio(Long notificacioId) {
		return delegate.eventFindAmbNotificacio(notificacioId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public void columnesCreate(
			UsuariDto usuaris, 
			Long entitatId, 
			ColumnesDto columnes) {
		delegate.columnesCreate(
				usuaris, 
				entitatId, 
				columnes);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public void columnesUpdate(
			Long entitatId, 
			ColumnesDto columnes) {
		delegate.columnesUpdate(
				entitatId, 
				columnes);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public ColumnesDto getColumnesUsuari(
			Long entitatId, 
			UsuariDto usuari) {
		return delegate.getColumnesUsuari(
				entitatId, 
				usuari);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public FitxerDto exportacio(
			Long entitatId, 
			Collection<Long> enviamentIds, 
			String format,
			NotificacioEnviamentFiltreDto filtre)
			throws IOException, NotFoundException, ParseException {
		return delegate.exportacio(
				entitatId, 
				enviamentIds, 
				format, 
				filtre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public NotificacioEnviamentDtoV2 getOne(Long entitatId) {
		return delegate.getOne(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "NOT_USER"})
	public byte[] getDocumentJustificant(Long enviamentId) {
		return delegate.getDocumentJustificant(enviamentId);
	}
}
