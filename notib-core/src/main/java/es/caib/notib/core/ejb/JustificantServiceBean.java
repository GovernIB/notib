/**
 * 
 */
package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.ProgresDescarregaDto;
import es.caib.notib.core.api.exception.JustificantException;
import es.caib.notib.core.api.service.JustificantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * Implementació de NotificacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class JustificantServiceBean implements JustificantService {

	@Autowired
	JustificantService delegate;

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