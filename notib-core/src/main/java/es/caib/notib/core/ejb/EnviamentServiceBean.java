/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notenviament.ColumnesDto;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.core.api.dto.notenviament.NotificacioEnviamentDatatableDto;
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
	public List<Long> findIdsAmbFiltre(Long entitatId, RolEnumDto rol, String usuariCodi, String organGestorCodi, NotificacioEnviamentFiltreDto filtre) throws NotFoundException, ParseException {
		return delegate.findIdsAmbFiltre(entitatId, rol, usuariCodi, organGestorCodi, filtre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<NotEnviamentTableItemDto> enviamentFindByEntityAndFiltre(
			Long entitatId,
			RolEnumDto rol,
			String organGestorCodi,
			String usuariCodi,
			NotificacioEnviamentFiltreDto filtre,
			PaginacioParamsDto paginacio) throws ParseException {
		return delegate.enviamentFindByEntityAndFiltre(
				entitatId,
				rol,
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
	public Set<Long> findIdsByNotificacioIds(Collection<Long> notificacionsIds) {
		return delegate.findIdsByNotificacioIds(notificacionsIds);
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
	public void columnesCreate(String codiUsuari, Long entitatId, ColumnesDto columnes) {
		delegate.columnesCreate(codiUsuari, entitatId, columnes);
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
	public ColumnesDto getColumnesUsuari(Long entitatId, String codiUsuari) {
		return delegate.getColumnesUsuari(entitatId, codiUsuari);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public FitxerDto exportacio(
			Long entitatId, 
			Collection<Long> enviamentIds, 
			String format)
			throws IOException, NotFoundException, ParseException {
		return delegate.exportacio(
				entitatId, 
				enviamentIds, 
				format);
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
	public Resposta findEnviaments(ApiConsulta consulta) {
		return delegate.findEnviaments(consulta);
	}

    @Override
	@RolesAllowed({"NOT_CARPETA", "NOT_SUPER"})
    public RespostaConsultaV2 findEnviamentsV2(ApiConsulta consulta) {
        return delegate.findEnviamentsV2(consulta);
    }

    @Override
	public void actualitzarEstat(Long enviamentId) {
		delegate.actualitzarEstat(enviamentId);
	}

	@Override
	public void activarCallback(Long enviamentId) {
		delegate.activarCallback(enviamentId);
	}

	@Override
	public List<Long> enviarCallback(Set<Long> enviaments) throws Exception {
		return delegate.enviarCallback(enviaments);
	}
}
