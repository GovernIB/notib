/**
 * 
 */
package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.ampliarPlazo.AmpliarPlazoOE;
import es.caib.notib.client.domini.ampliarPlazo.RespuestaAmpliarPlazoOE;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.ws.adviser.nexea.NexeaAdviserWs;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.SincronizarEnvio;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.GeneralSecurityException;
import java.util.Date;

/**
 * Helper per a interactuar amb el servei web de Notific@.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificaHelper {

	@Autowired
	private NotificaV0Helper notificaV0Helper; // Mock
	@Autowired
	private NotificaV2Helper notificaV2Helper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private NotificacioEventRepository eventRepository;
	@Autowired
	private NotificacioEnviamentRepository enviamentRepository;

	public static final String CUA_SINCRONIZAR_ENVIO_OE = "qu_sincronizar_envio_oe";
	public static final String JMS_FACTORY_ACK = "jmsFactory";


	public NotificacioEntity notificacioEnviar(Long notificacioId) {
		return getNotificaHelper().notificacioEnviar(notificacioId, false);
	}

	public NotificacioEntity notificacioEnviar(Long notificacioId, boolean ambEnviamentPerEmail) {
		return getNotificaHelper().notificacioEnviar(notificacioId, ambEnviamentPerEmail);
	}

	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId) throws SistemaExternException {
		return getNotificaHelper().enviamentRefrescarEstat(enviamentId);
	}

	public NotificacioEnviamentEntity enviamentRefrescarEstat(Long enviamentId, boolean raiseException) throws Exception {
		return getNotificaHelper().enviamentRefrescarEstat(enviamentId, raiseException);
	}

	@Transactional
	@JmsListener(destination = CUA_SINCRONIZAR_ENVIO_OE, containerFactory = JMS_FACTORY_ACK)
	public void enviamentEntregaPostalNotificada(SincronizarEnvio sincronizarEnvio) throws Exception {

		var resposta = getNotificaHelper().enviamentEntregaPostalNotificada(sincronizarEnvio);
		if (NexeaAdviserWs.SYNC_ENVIO_OE_OK.equals(resposta.getCodigoRespuesta())) {
			return;
		}
		var enviament = enviamentRepository.findByCieId(sincronizarEnvio.getIdentificador());
		var events = eventRepository.findByEnviamentIdAndTipus(enviament.getId(), NotificacioEventTipusEnumDto.NOTIFICA_ENVIO_OE);
		var reintents = !events.isEmpty() ? events.get(0).getIntents() : 0;
		var maxIntents = configHelper.getConfigAsInteger("es.caib.notib.tasca.notifica.sincronizar.envioOE.reintents.maxim");
		maxIntents = maxIntents != null ? maxIntents : 3;
		if (reintents > maxIntents) {
			for (var event : events) {
				event.setFiReintents(true);
			}
			return;
		}
		jmsTemplate.convertAndSend(NotificaHelper.CUA_SINCRONIZAR_ENVIO_OE, sincronizarEnvio,
				m -> {
					m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, SmConstants.delay(reintents));
					return m;
				});
	}

	public RespuestaAmpliarPlazoOE ampliarPlazoOE(AmpliarPlazoOE ampliarPlazo) {

		if (ampliarPlazo.getEnvios() == null) {
			return RespuestaAmpliarPlazoOE.builder().codigoRespuesta("error").descripcionRespuesta("ampliar.plazo.enviaments.not.null").build();
		}
		if (ampliarPlazo.getEnvios().getIdentificador().size() > 100) {
			return RespuestaAmpliarPlazoOE.builder().codigoRespuesta("error").descripcionRespuesta("ampliar.plazo.max.enviaments").build();
		}
		if (Strings.isNullOrEmpty(ampliarPlazo.getMotivo())) {
			return RespuestaAmpliarPlazoOE.builder().codigoRespuesta("error").descripcionRespuesta("ampliar.plazo.motiu.null").build();
		}
		if (ampliarPlazo.getPlazo() <= 0) {
			return RespuestaAmpliarPlazoOE.builder().codigoRespuesta("error").descripcionRespuesta("ampliar.plazo.plazo.major.zero").build();
		}
		var ids = ampliarPlazo.getEnvios().getIdentificador();
		var enviaments = enviamentRepository.findByNotificaIdentificadorIn(ids);
		if (ids.size() != enviaments.size()) {
			return RespuestaAmpliarPlazoOE.builder().codigoRespuesta("error").descripcionRespuesta("ampliar.plazo.identificadors.no.valids").build();
		}
//		boolean organsIguals = enviaments.stream().map(e -> e.getNotificacio().getOrganGestor().getCodi()).distinct().count() == 1;
		var organsIguals = true;
		var entregaPostal = false;
		var estatPendent = true;
		NotificacioEnviamentEntity enviament;;
		for (var i = 0; i < enviaments.size(); i++) {
			enviament = enviaments.get(i);
			entregaPostal = entregaPostal || enviament.getEntregaPostal() != null;
			estatPendent = estatPendent && EnviamentEstat.PENDENT.equals(enviament.getNotificaEstat());
			if (i != 0) {
				organsIguals = organsIguals && enviament.getNotificacio().getOrganGestor().getCodi().equals(enviaments.get(i-1).getNotificacio().getOrganGestor().getCodi());
			}
		}
		if (entregaPostal) {
			return RespuestaAmpliarPlazoOE.builder().codigoRespuesta("error").descripcionRespuesta("ampliar.plazo.enviaments.entrega.postal").build();
		}
//		if (!estatPendent) {
//			return RespuestaAmpliarPlazoOE.builder().codigoRespuesta("error").descripcionRespuesta("Tots els enviaments han de tenir un estat pendent a Notific@").build();
//		}
		if (!organsIguals) {
			return RespuestaAmpliarPlazoOE.builder().codigoRespuesta("error").descripcionRespuesta("").build();
		}

		return getNotificaHelper().ampliarPlazoOE(ampliarPlazo, enviaments);
	}

	public String xifrarId(Long id) throws GeneralSecurityException {
		return getNotificaHelper().xifrarId(id);
	}
	public Long desxifrarId(String identificador) throws GeneralSecurityException {
		return getNotificaHelper().desxifrarId(identificador);
	}

	public boolean isConnexioNotificaDisponible() {
		return getNotificaHelper().isConnexioNotificaDisponible();
	}

	public boolean isAdviserActiu() {
		return getNotificaHelper().isAdviserActiu();
	}

	public void enviamentUpdateDatat(EnviamentEstat notificaEstat, Date notificaEstatData, String notificaEstatDescripcio, String notificaDatatOrigen,
									 String notificaDatatReceptorNif, String notificaDatatReceptorNom, String notificaDatatNumSeguiment,
									 String notificaDatatErrorDescripcio, NotificacioEnviamentEntity enviament) throws Exception {

		getNotificaHelper().enviamentUpdateDatat(notificaEstat, notificaEstatData, notificaEstatDescripcio, notificaDatatOrigen, notificaDatatReceptorNif,
													notificaDatatReceptorNom, notificaDatatNumSeguiment, notificaDatatErrorDescripcio, enviament);
	}

	private AbstractNotificaHelper getNotificaHelper() {

		var versio = getNotificaVersioProperty();
		return "0".equals(versio) ? notificaV0Helper : notificaV2Helper;
	}

	private String getNotificaVersioProperty() {
		return configHelper.getConfig("es.caib.notib.notifica.versio");
	}
}
