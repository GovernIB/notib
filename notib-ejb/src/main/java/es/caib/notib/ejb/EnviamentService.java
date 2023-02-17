/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import es.caib.notib.logic.intf.dto.ApiConsulta;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentFiltreDto;
import es.caib.notib.logic.intf.dto.NotificacioEventDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.notenviament.ColumnesDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.logic.intf.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.rest.consulta.Resposta;

import javax.annotation.security.PermitAll;
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


	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<Long> findIdsAmbFiltre(Long entitatId, NotificacioEnviamentFiltreDto filtre) throws NotFoundException, ParseException {
		return getDelegateService().findIdsAmbFiltre(entitatId, filtre);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public PaginaDto<NotEnviamentTableItemDto> enviamentFindByEntityAndFiltre(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioEnviamentFiltreDto filtre, PaginacioParamsDto paginacio) throws ParseException {
		return getDelegateService().enviamentFindByEntityAndFiltre(entitatId, rol, organGestorCodi, usuariCodi, filtre, paginacio);
	}


	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<NotificacioEnviamentDatatableDto> enviamentFindAmbNotificacio(Long notificacioId) {
		return getDelegateService().enviamentFindAmbNotificacio(notificacioId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public Set<Long> findIdsByNotificacioIds(Collection<Long> notificacionsIds) {
		return getDelegateService().findIdsByNotificacioIds(notificacionsIds);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public NotificacioEnviamentDto enviamentFindAmbId(Long enviamentId) {
		return getDelegateService().enviamentFindAmbId(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public List<NotificacioEventDto> eventFindAmbNotificacio(Long notificacioId) {
		return getDelegateService().eventFindAmbNotificacio(notificacioId);
	}
	
	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public void columnesCreate(String codiUsuari, Long entitatId, ColumnesDto columnes) {
		getDelegateService().columnesCreate(codiUsuari, entitatId, columnes);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public void columnesUpdate(Long entitatId, ColumnesDto columnes) {
		getDelegateService().columnesUpdate(entitatId, columnes);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public ColumnesDto getColumnesUsuari(Long entitatId, String codiUsuari) {
		return getDelegateService().getColumnesUsuari(entitatId, codiUsuari);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public FitxerDto exportacio(Long entitatId, Collection<Long> enviamentIds, String format) throws IOException, NotFoundException, ParseException {
		return getDelegateService().exportacio(entitatId, enviamentIds, format);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public NotificacioEnviamentDtoV2 getOne(Long entitatId) {
		return getDelegateService().getOne(entitatId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom", "NOT_CARPETA"})
	public byte[] getDocumentJustificant(Long enviamentId) {
		return getDelegateService().getDocumentJustificant(enviamentId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public boolean reintentarCallback(Long eventId) {
		return getDelegateService().reintentarCallback(eventId);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public void reactivaConsultes(Set<Long> enviaments) {
		getDelegateService().reactivaConsultes(enviaments);
	}

	@Override
	@RolesAllowed({"NOT_ADMIN", "NOT_SUPER", "tothom"})
	public void reactivaSir(Set<Long> enviaments) {
		getDelegateService().reactivaSir(enviaments);
	}
	
	@Override
	@RolesAllowed({"NOT_CARPETA", "NOT_SUPER"})
	public Resposta findEnviaments(ApiConsulta consulta) {
		return getDelegateService().findEnviaments(consulta);
	}

    @Override
	@RolesAllowed({"NOT_CARPETA", "NOT_SUPER"})
    public RespostaConsultaV2 findEnviamentsV2(ApiConsulta consulta) {
        return getDelegateService().findEnviamentsV2(consulta);
    }

    @Override
	public void actualitzarEstat(Long enviamentId) {
		getDelegateService().actualitzarEstat(enviamentId);
	}

	@Override
	@PermitAll
	public void activarCallback(Long enviamentId) {
		getDelegateService().activarCallback(enviamentId);
	}

	@Override
	@PermitAll
	public void enviarCallback(Long enviamentId) throws Exception {
		getDelegateService().enviarCallback(enviamentId);
	}
}
