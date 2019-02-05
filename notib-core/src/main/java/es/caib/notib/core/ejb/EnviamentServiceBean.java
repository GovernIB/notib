/**
 * 
 */
package es.caib.notib.core.ejb;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.notib.core.api.dto.ColumnesDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentFiltreDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.EnviamentService;

/**
 * Implementació de EntitatService com a EJB que empra una clase
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
	public List<Long> findIdsAmbFiltre(
			Long entitatId, 
			NotificacioEnviamentFiltreDto filtre) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginaDto<NotificacioEnviamentDtoV2> enviamentFindByUserAndFiltre(
			NotificacioEnviamentFiltreDto filtre,
			PaginacioParamsDto paginacio) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<NotificacioEnviamentDto> enviamentFindAmbNotificacio(Long notificacioId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NotificacioEnviamentDto enviamentFindAmbId(Long enviamentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<NotificacioEventDto> eventFindAmbNotificacio(Long notificacioId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void columnesCreate(
			UsuariDto usuaris, 
			Long entitatId, 
			ColumnesDto columnes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void columnesUpdate(
			Long entitatId, 
			ColumnesDto columnes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ColumnesDto getColumnesUsuari(
			Long entitatId, 
			UsuariDto usuari) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FitxerDto exportacio(
			Long entitatId, 
			Collection<Long> enviamentIds, 
			String format,
			NotificacioEnviamentFiltreDto filtre)
			throws IOException, NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	
	

}
