/**
 * 
 */
package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.ProgresDescarregaDto;
import es.caib.notib.logic.intf.exception.JustificantException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

/**
 * Implementació de NotificacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class JustificantService extends AbstractService<es.caib.notib.logic.intf.service.JustificantService> implements es.caib.notib.logic.intf.service.JustificantService {

	@Autowired
	JustificantService delegate;

	@Override
	public FitxerDto generarJustificantEnviament(Long notificacioId, String sequence) throws JustificantException {
		return delegate.generarJustificantEnviament(notificacioId, sequence);
	}

	@RolesAllowed({"tothom"})
	public FitxerDto generarJustificantEnviament(Long notificacioId, Long entitatId, String sequence) throws JustificantException {
		return delegate.generarJustificantEnviament(notificacioId, entitatId, sequence);
	}

	@Override
	@RolesAllowed({"tothom"})
	public FitxerDto generarJustificantComunicacioSIR(Long notificacioId, Long entitatId, String sequence) throws JustificantException {
		return delegate.generarJustificantComunicacioSIR(notificacioId, entitatId, sequence);
	}

	@Override
	@RolesAllowed({"tothom"})
	public ProgresDescarregaDto consultaProgresGeneracioJustificant(String sequence) throws JustificantException {
		return delegate.consultaProgresGeneracioJustificant(sequence);
	}

}