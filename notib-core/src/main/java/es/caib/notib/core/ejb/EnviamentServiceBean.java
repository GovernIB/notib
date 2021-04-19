/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notenviament.ColumnesDto;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.rest.consulta.Resposta;
import es.caib.notib.core.api.service.EnviamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<Long> findIdsAmbFiltre(
			Long entitatId, 
			NotificacioEnviamentFiltreDto filtre) throws NotFoundException, ParseException {
		return delegate.findIdsAmbFiltre(entitatId, filtre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<NotEnviamentTableItemDto> enviamentFindByEntityAndFiltre(
			EntitatDto entitat, 
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdminOrgan, 
			List<String> codisProcedimentsDisponibles,
			List<String> codisOrgansGestorsDisponibles,
			List<Long> codisProcedimentOrgansDisponibles,
			String organGestorCodi,
			String usuariCodi,
			NotificacioEnviamentFiltreDto filtre, 
			PaginacioParamsDto paginacio)
			throws ParseException {
		return delegate.enviamentFindByEntityAndFiltre(
				entitat, 
				isUsuari, 
				isUsuariEntitat, 
				isAdminOrgan,
				codisProcedimentsDisponibles,
				codisOrgansGestorsDisponibles,
				codisProcedimentOrgansDisponibles,
				organGestorCodi,
				usuariCodi,
				filtre, 
				paginacio);
	}


	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<NotificacioEnviamentDatatableDto> enviamentFindAmbNotificacio(Long notificacioId) {
		return delegate.enviamentFindAmbNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public NotificacioEnviamentDto enviamentFindAmbId(Long enviamentId) {
		return delegate.enviamentFindAmbId(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<NotificacioEventDto> eventFindAmbNotificacio(Long notificacioId) {
		return delegate.eventFindAmbNotificacio(notificacioId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
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
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public void columnesUpdate(
			Long entitatId, 
			ColumnesDto columnes) {
		delegate.columnesUpdate(
				entitatId, 
				columnes);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public ColumnesDto getColumnesUsuari(
			Long entitatId, 
			UsuariDto usuari) {
		return delegate.getColumnesUsuari(
				entitatId, 
				usuari);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
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
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public NotificacioEnviamentDtoV2 getOne(Long entitatId) {
		return delegate.getOne(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_CARPETA"})
	public byte[] getDocumentJustificant(Long enviamentId) {
		return delegate.getDocumentJustificant(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public boolean reintentarCallback(Long eventId) {
		return delegate.reintentarCallback(eventId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public void reactivaConsultes(Set<Long> enviaments) {
		delegate.reactivaConsultes(enviaments);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public void reactivaSir(Set<Long> enviaments) {
		delegate.reactivaSir(enviaments);
	}
	
	@Override
	@RolesAllowed({"NOT_CARPETA", "NOT_SUPER"})
	public Resposta findEnviamentsByNif(
			String dniTitular,
			NotificaEnviamentTipusEnumDto tipus,
			Boolean estatFinal,
			String basePath,
			Integer pagina,
			Integer mida) {
		return delegate.findEnviamentsByNif(
				dniTitular, 
				tipus, 
				estatFinal, 
				basePath, 
				pagina, 
				mida);
	}

	@Override
	public void actualitzarEstat(Long enviamentId) {
		delegate.actualitzarEstat(enviamentId);
	}
}
