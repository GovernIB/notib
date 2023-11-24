/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.ProgresDescarregaDto;
import es.caib.notib.logic.intf.exception.JustificantException;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de NotificacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class JustificantService extends AbstractService<es.caib.notib.logic.intf.service.JustificantService> implements es.caib.notib.logic.intf.service.JustificantService {

	@Override
	@RolesAllowed("**")
	public FitxerDto generarJustificantEnviament(Long notificacioId, String sequence) throws JustificantException {
		return getDelegateService().generarJustificantEnviament(notificacioId, sequence);
	}

	@RolesAllowed("**")
	public FitxerDto generarJustificantEnviament(Long notificacioId, Long entitatId, String sequence) throws JustificantException {
		return getDelegateService().generarJustificantEnviament(notificacioId, entitatId, sequence);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto generarJustificantComunicacioSIR(Long notificacioId, Long entitatId, String sequence) throws JustificantException {
		return getDelegateService().generarJustificantComunicacioSIR(notificacioId, entitatId, sequence);
	}

	@Override
	@RolesAllowed("**")
	public ProgresDescarregaDto consultaProgresGeneracioJustificant(String sequence) throws JustificantException {
		return getDelegateService().consultaProgresGeneracioJustificant(sequence);
	}

}