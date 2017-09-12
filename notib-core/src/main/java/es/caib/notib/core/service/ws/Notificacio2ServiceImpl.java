/**
 * 
 */
package es.caib.notib.core.service.ws;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.notib.core.api.dto.NotificacioDestinatariDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.ws.notificacio2.Notificacio;
import es.caib.notib.core.api.ws.notificacio2.Notificacio2Service;
import es.caib.notib.core.api.ws.notificacio2.Notificacio2ServiceException;
import es.caib.notib.core.api.ws.notificacio2.NotificacioEnviament;
import es.caib.notib.core.helper.ConversioTipusHelper;


/**
 * Implementació del servei per a l'enviament i consulta de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@WebService(
		name = "Notificacio2",
		serviceName = "Notificacio2Service",
		portName = "Notificacio2ServicePort",
		targetNamespace = "http://www.caib.es/notib/ws/notificacio2",
		endpointInterface = "es.caib.notib.core.api.service.ws.Notificacio2Service")
public class Notificacio2ServiceImpl implements Notificacio2Service {

	@Autowired
	private NotificacioService notificacioService;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	@Override
	public List<String> alta(
			Notificacio notificacio) throws Notificacio2ServiceException {
		NotificacioDto dto = conversioTipusHelper.convertir(
				notificacio,
				NotificacioDto.class);
		dto.setDestinataris(
				conversioTipusHelper.convertirList(
						notificacio.getEnviaments(),
						NotificacioDestinatariDto.class));
		NotificacioDto altaResposta = notificacioService.alta(
				notificacio.getEntitatDir3Codi(),
				dto);
		List<String> referencies = new ArrayList<String>();
		for (NotificacioDestinatariDto destinatari: altaResposta.getDestinataris()) {
			referencies.add(destinatari.getReferencia());
		}
		return referencies;
	}

	@Override
	public Notificacio consulta(
			String referencia) throws Notificacio2ServiceException {
		NotificacioDto dto = notificacioService.consulta(referencia);
		Notificacio notificacio = conversioTipusHelper.convertir(
				dto,
				Notificacio.class);
		notificacio.setEnviaments(
				conversioTipusHelper.convertirList(
						dto.getDestinataris(),
						NotificacioEnviament.class));
		return notificacio;
	}

}
