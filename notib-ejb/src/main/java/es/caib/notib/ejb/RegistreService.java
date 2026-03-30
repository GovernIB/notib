package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.RegistreAnotacioDto;
import es.caib.notib.logic.intf.dto.adviser.sir.RespostaSirAdviser;
import es.caib.notib.logic.intf.dto.adviser.sir.SirAdviser;
import es.caib.notib.logic.intf.statemachine.dto.ConsultaSirDto;
import es.caib.notib.logic.intf.statemachine.events.ConsultaSirRequest;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de RegistreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class RegistreService extends AbstractService<es.caib.notib.logic.intf.service.RegistreService> implements es.caib.notib.logic.intf.service.RegistreService {

	@Override
	@PermitAll
	public RespostaSirAdviser sincronitzarEnviamentSir(SirAdviser adviser) {
		return getDelegateService().sincronitzarEnviamentSir(adviser);
	}

}
