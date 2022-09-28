/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import es.caib.notib.ejb.AbstractService;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.notenviament.ColumnesDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.logic.intf.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.rest.consulta.Resposta;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
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
public class EnviamentService extends AbstractService<es.caib.notib.logic.intf.service.EnviamentService> implements es.caib.notib.logic.intf.service.EnviamentService {

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
	public void enviarCallback(Long enviamentId) throws Exception {
		delegate.enviarCallback(enviamentId);
	}
}
