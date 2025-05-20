/**
 * 
 */
package es.caib.notib.logic.aspect;

import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.intf.statemachine.events.EnviamentRegistreRequest;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.cie.EntregaPostalEntity;
import es.caib.notib.persist.entity.explotacio.ExplotEnvInfoEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.explotacio.ExplotEnvInfoRepository;
import es.caib.notib.plugin.cie.RespostaCie;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Advice AspectJ que intercepta les excepcions llençades des dels
 * services.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Aspect
@Order(100)
@Component
public class EstadistiquesAdvice {

	@Autowired
	private ExplotEnvInfoRepository explotEnvInfoRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;


	// REGISTRE
	@AfterReturning(
			pointcut = "execution(public boolean es.caib.notib.logic.service.RegistreServiceImpl.enviarRegistre(..)) && args(enviamentRegistreRequest)",
			returning = "success",
			argNames = "enviamentRegistreRequest,success"
	)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void estRegistre(EnviamentRegistreRequest enviamentRegistreRequest, boolean success) {

		try {
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findByUuid(enviamentRegistreRequest.getEnviamentUuid()).orElse(null);
			if (enviament == null) {
				log.error("[EstRegistre] Enviament amb UUID: " + enviamentRegistreRequest.getEnviamentUuid() + " no trobat.");
				return;
			}

			ExplotEnvInfoEntity explotEnvInfoEntity = explotEnvInfoRepository.findByEnviamentId(enviament.getId())
					.orElseGet(() -> createExplotEnvInfo(enviament));

			int intents = explotEnvInfoEntity.getIntentsRegEnviament() + 1;
			explotEnvInfoEntity.setIntentsRegEnviament(intents);
			if (success) {
				LocalDateTime dataRegistre = enviament.getRegistreData().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				long tempsPendent = ChronoUnit.MILLIS.between(explotEnvInfoEntity.getDataCreacio(), dataRegistre);
				explotEnvInfoEntity.setDataRegistrada(dataRegistre);
				explotEnvInfoEntity.setTempsPendent(tempsPendent);
			} else if (explotEnvInfoEntity.getDataRegEnviamentError() == null) {
				explotEnvInfoEntity.setDataRegEnviamentError(LocalDateTime.now());
			}
			explotEnvInfoRepository.save(explotEnvInfoEntity);
		} catch (Exception e) {
			String uuid = enviamentRegistreRequest != null ? enviamentRegistreRequest.getEnviamentUuid() : null;
			log.error("[estRegistre] Error generant informació estadística de enviament Uuid: " + uuid, e);
		}
	}


	// SIR
	@AfterReturning(
			pointcut = "execution(public es.caib.notib.persist.entity.NotificacioEnviamentEntity es.caib.notib.logic.helper.RegistreHelper.enviamentRefrescarEstatRegistre(..)) && args(enviamentId)",
			returning = "enviament",
			argNames = "enviamentId,enviament"
	)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void estSir(Long enviamentId, NotificacioEnviamentEntity enviament) {

		try {
			if (enviament == null) {
				log.error("[estSir] Enviament amb id: " + enviamentId + " no trobat.");
				return;
			}

			ExplotEnvInfoEntity explotEnvInfoEntity = explotEnvInfoRepository.findByEnviamentId(enviament.getId())
					.orElseGet(() -> createExplotEnvInfo(enviament));

			int intents = explotEnvInfoEntity.getIntentsSirConsulta() + 1;
			explotEnvInfoEntity.setIntentsSirConsulta(intents);
			if (NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT.equals(enviament.getRegistreEstat())) {
				LocalDateTime dataSir = enviament.getSirRecepcioData() != null
						? enviament.getSirRecepcioData().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
						: enviament.getSirRegDestiData().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				long tempsRegistrada = ChronoUnit.MILLIS.between(explotEnvInfoEntity.getDataRegistrada(), dataSir);
				explotEnvInfoEntity.setDataRegAcceptada(dataSir);
				explotEnvInfoEntity.setTempsRegistrada(tempsRegistrada);
			} else if (NotificacioRegistreEstatEnumDto.REBUTJAT.equals(enviament.getRegistreEstat())) {
				LocalDateTime dataSir = enviament.getSirRecepcioData() != null
						? enviament.getSirRecepcioData().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
						: enviament.getSirRegDestiData().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				long tempsRegistrada = ChronoUnit.MILLIS.between(explotEnvInfoEntity.getDataRegistrada(), dataSir);
				explotEnvInfoEntity.setDataRegRebutjada(dataSir);
				explotEnvInfoEntity.setTempsRegistrada(tempsRegistrada);
			}
			explotEnvInfoRepository.save(explotEnvInfoEntity);
		} catch (Exception e) {
			log.error("[estSir] Error generant informació estadística de enviamentId: " + enviamentId, e);
		}
	}

	// NOTIFICA
	// Enviament
	@AfterReturning(
			pointcut = "execution(public es.caib.notib.persist.entity.NotificacioEntity es.caib.notib.logic.helper.NotificaHelper.notificacioEnviar(..))",
			returning = "notificacio"
	)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void estNotificacio(NotificacioEntity notificacio) {
		if (notificacio == null || notificacio.getEnviaments() == null) {
			log.error("[estNotificacio] Notificacio no trobada.");
			return;
		}

		notificacio.getEnviaments().forEach(enviament -> {
			try {
				ExplotEnvInfoEntity explotEnvInfoEntity = explotEnvInfoRepository.findByEnviamentId(enviament.getId())
						.orElseGet(() -> createExplotEnvInfo(enviament));

				int intents = explotEnvInfoEntity.getIntentsNotEnviament() + 1;
				explotEnvInfoEntity.setIntentsNotEnviament(intents);
				if (EnviamentEstat.NOTIB_ENVIADA.equals(enviament.getNotificaEstat())) {
					LocalDateTime dataNotifica = enviament.getNotificaEstatData().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					long tempsRegistrada = ChronoUnit.MILLIS.between(explotEnvInfoEntity.getDataRegistrada(), dataNotifica);
					explotEnvInfoEntity.setDataNotEnviada(dataNotifica);
					explotEnvInfoEntity.setTempsRegistrada(tempsRegistrada);
				} else if (EnviamentEstat.REGISTRADA.equals(enviament.getNotificaEstat()) && explotEnvInfoEntity.getDataNotEnviamentError() == null) {
					explotEnvInfoEntity.setDataNotEnviamentError(LocalDateTime.now());
				}
				explotEnvInfoRepository.save(explotEnvInfoEntity);
			} catch (Exception e) {
				log.error("[estNotificacio] Error generant informació estadística de enviamentId: " + enviament.getId(), e);
			}
		});
	}

	// Recepció
	@AfterReturning(
			pointcut = "execution(public es.caib.notib.persist.entity.NotificacioEnviamentEntity es.caib.notib.logic.helper.AbstractNotificaHelper.enviamentUpdateDatat(..))",
			returning = "enviament"
	)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void estNotificacioRecepcio(NotificacioEnviamentEntity enviament) {
		if (enviament == null) {
			log.error("[estNotificacioRecepcio] Enviament no trobat.");
			return;
		}

		try {
			ExplotEnvInfoEntity explotEnvInfoEntity = explotEnvInfoRepository.findByEnviamentId(enviament.getId())
					.orElseGet(() -> createExplotEnvInfo(enviament));

			if (enviament.isNotificaEstatFinal()) {
				LocalDateTime dataEstatFinal = enviament.getNotificaEstatData().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				long tempsNotEnviada = ChronoUnit.MILLIS.between(explotEnvInfoEntity.getDataNotEnviada(), dataEstatFinal);
				long tempsTotal = ChronoUnit.MILLIS.between(explotEnvInfoEntity.getDataCreacio(), dataEstatFinal);

				if (EnviamentEstat.NOTIFICADA.equals(enviament.getNotificaEstat()) || EnviamentEstat.LLEGIDA.equals(enviament.getNotificaEstat())) {
					explotEnvInfoEntity.setDataNotNotificada(dataEstatFinal);
				} else if (EnviamentEstat.REBUTJADA.equals(enviament.getNotificaEstat())) {
					explotEnvInfoEntity.setDataNotRebutjada(dataEstatFinal);
				} else if (EnviamentEstat.EXPIRADA.equals(enviament.getNotificaEstat())) {
					explotEnvInfoEntity.setDataNotExpirada(dataEstatFinal);
				} else {
					explotEnvInfoEntity.setDataNotError(dataEstatFinal);
				}

				EntregaPostalEntity entregaPostal = enviament.getEntregaPostal();
				if (explotEnvInfoEntity.getTempsTotal() == null &&
						(EnviamentEstat.NOTIFICADA.equals(enviament.getNotificaEstat())
								|| EnviamentEstat.LLEGIDA.equals(enviament.getNotificaEstat())
								|| enviament.getEntregaPostal() == null
								|| isCieEstatFinal(entregaPostal.getCieEstat()))) {
					explotEnvInfoEntity.setTempsTotal(tempsTotal);
				}
				explotEnvInfoEntity.setTempsNotEnviada(tempsNotEnviada);
				explotEnvInfoRepository.save(explotEnvInfoEntity);
			}
		} catch (Exception e) {
			log.error("[estNotificacioRecepcio] Error generant informació estadística de enviamentId: " + enviament.getId(), e);
		}
	}

	// ENVIAMENT CIE
	// Enviament
	@AfterReturning(
			pointcut = "execution(public es.caib.notib.plugin.cie.RespostaCie es.caib.notib.logic.plugin.cie.CiePluginHelper.enviar(..)) && args(notificacioReferencia)",
			returning = "resposta",
			argNames = "notificacioReferencia,resposta"
	)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void estNotificacioCie(String notificacioReferencia, RespostaCie resposta) {
		NotificacioEntity notificacio = notificacioRepository.findByReferencia(notificacioReferencia);
		if (notificacio == null) {
			log.error("[estNotificacioCie] Notificacio amb Uuid: " + notificacioReferencia + " no trobat.");
			return;
		}

		notificacio.getEnviaments().forEach(enviament -> {
			try {
				ExplotEnvInfoEntity explotEnvInfoEntity = explotEnvInfoRepository.findByEnviamentId(enviament.getId())
						.orElseGet(() -> createExplotEnvInfo(enviament));

				int intents = explotEnvInfoEntity.getIntentsCieEnviament() + 1;
				explotEnvInfoEntity.setIntentsCieEnviament(intents);
				if ("000".equals(resposta.getCodiResposta())
						&& enviament.getEntregaPostal() != null && CieEstat.ENVIADO_CI.equals(enviament.getEntregaPostal().getCieEstat())) {
					LocalDateTime dataCie = LocalDateTime.now();
					explotEnvInfoEntity.setDataCieEnviada(dataCie);
				} else if (explotEnvInfoEntity.getDataNotEnviamentError() == null) {
					explotEnvInfoEntity.setDataCieEnviamentError(LocalDateTime.now());
				}
				explotEnvInfoRepository.save(explotEnvInfoEntity);
			} catch (Exception e) {
				log.error("[estNotificacioCie] Error generant informació estadística de enviamentId: " + enviament.getId(), e);
			}
		});
	}

	// Recepció
	@AfterReturning(
			pointcut = "execution(public es.caib.notib.persist.entity.NotificacioEnviamentEntity es.caib.notib.logic.service.CieAdviserServiceImpl.updateDatat(..))",
			returning = "enviament"
	)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void estNotificacioCieRecepcio(NotificacioEnviamentEntity enviament) {
		if (enviament == null) {
			log.error("[estNotificacioCieRecepcio] Enviament no trobat.");
			return;
		}
		if (enviament.getEntregaPostal() == null) {
			log.error("[estNotificacioCieRecepcio] Entrega postal no trobada per enviamentId: " + enviament.getId());
			return;
		}

		try {
			ExplotEnvInfoEntity explotEnvInfoEntity = explotEnvInfoRepository.findByEnviamentId(enviament.getId())
					.orElseGet(() -> createExplotEnvInfo(enviament));

			EntregaPostalEntity entregaPostal = enviament.getEntregaPostal();
			CieEstat cieEstat = entregaPostal.getCieEstat();
			boolean cieEstatFinal = isCieEstatFinal(cieEstat);

			if (cieEstatFinal) {
				LocalDateTime dataEstatFinal = entregaPostal.getCieEstatData().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				long tempsCieEnviada = ChronoUnit.MILLIS.between(explotEnvInfoEntity.getDataCieEnviada(), dataEstatFinal);
				long tempsTotal = ChronoUnit.MILLIS.between(explotEnvInfoEntity.getDataCreacio(), dataEstatFinal);

				if (CieEstat.NOTIFICADA.equals(entregaPostal.getCieEstat())) {
					explotEnvInfoEntity.setDataCieNotificada(dataEstatFinal);
				} else if (CieEstat.REHUSADA.equals(entregaPostal.getCieEstat())) {
					explotEnvInfoEntity.setDataCieRebutjada(dataEstatFinal);
				} else if (CieEstat.CANCELADO.equals(entregaPostal.getCieEstat())) {
					explotEnvInfoEntity.setDataCieCancelada(dataEstatFinal);
				} else {
					explotEnvInfoEntity.setDataCieError(dataEstatFinal);
				}

				if (explotEnvInfoEntity.getTempsTotal() == null &&
						(enviament.isNotificaEstatFinal() || CieEstat.NOTIFICADA.equals(entregaPostal.getCieEstat()))) {
					explotEnvInfoEntity.setTempsTotal(tempsTotal);
				}
				explotEnvInfoEntity.setTempsCieEnviada(tempsCieEnviada);
				explotEnvInfoRepository.save(explotEnvInfoEntity);
			}
		} catch (Exception e) {
			log.error("[estNotificacioCieRecepcio] Error generant informació estadística de enviamentId: " + enviament.getId(), e);
		}
	}

	// ENVIAMENT Email
	@AfterReturning(
			pointcut = "execution(public es.caib.notib.persist.entity.NotificacioEntity es.caib.notib.logic.helper.EmailNotificacioSenseNifHelper.notificacioEnviarEmail(..)) && args(enviamentsSenseNif, totsEmail)",
			returning = "notificacio",
			argNames = "enviamentsSenseNif,totsEmail,notificacio"
	)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void estNotificacioEmail(List<NotificacioEnviamentEntity> enviamentsSenseNif, boolean totsEmail, NotificacioEntity notificacio) {
		if (enviamentsSenseNif == null) {
			log.error("[estNotificacioEmail] sense enviaments");
			return;
		}

		enviamentsSenseNif.forEach(enviament -> {
			try {
				ExplotEnvInfoEntity explotEnvInfoEntity = explotEnvInfoRepository.findByEnviamentId(enviament.getId())
						.orElseGet(() -> createExplotEnvInfo(enviament));

				int intents = explotEnvInfoEntity.getIntentsEmailEnviament() + 1;
				explotEnvInfoEntity.setIntentsEmailEnviament(intents);
				if (EnviamentEstat.FINALITZADA.equals(enviament.getNotificaEstat()) || EnviamentEstat.PROCESSADA.equals(enviament.getNotificaEstat())) {
					LocalDateTime dataEmail = enviament.getNotificaEstatData().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
					long tempsRegistrada = ChronoUnit.MILLIS.between(explotEnvInfoEntity.getDataRegistrada(), dataEmail);
					explotEnvInfoEntity.setDataEmailEnviada(dataEmail);
					explotEnvInfoEntity.setTempsRegistrada(tempsRegistrada);
				} else if (explotEnvInfoEntity.getDataNotEnviamentError() == null) {
					explotEnvInfoEntity.setDataEmailEnviamentError(LocalDateTime.now());
				}
				explotEnvInfoRepository.save(explotEnvInfoEntity);
			} catch (Exception e) {
				log.error("[estNotificacioEmail] Error generant informació estadística de enviamentId: " + enviament.getId(), e);
			}
		});
	}


	private static ExplotEnvInfoEntity createExplotEnvInfo(NotificacioEnviamentEntity enviament) {
		Long tempsPendent = null;
		Long tempsRegistre = null;
		LocalDateTime dataRegistre = null;
		LocalDateTime dataEnviament = null;
		LocalDateTime dataCieEnviament = null;
		int intentsRegistre = 0;
		int intentsNotEnviament = 0;
		int intentsCieEnviament = 0;
		if (enviament.getRegistreData() != null) {
			intentsRegistre = 1;
			dataRegistre = enviament.getRegistreData().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			tempsPendent = ChronoUnit.MILLIS.between(enviament.getCreatedDate().get(), dataRegistre);

			if (enviament.getNotificaEstatData() != null) {
				intentsNotEnviament = Math.max(enviament.getNotificaIntentNum(), 1);
				dataEnviament = enviament.getNotificaEstatData().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				tempsRegistre = ChronoUnit.MILLIS.between(dataRegistre, dataEnviament);

				if (enviament.getEntregaPostal() != null) {
					intentsCieEnviament = 1;
					if (CieEstat.ENVIADO_CI.equals(enviament.getEntregaPostal().getCieEstat())) {
						dataCieEnviament = dataEnviament;
					}
				}
			}
		}
		return ExplotEnvInfoEntity.builder()
				.enviament(enviament)
				.dataCreacio(enviament.getCreatedDate().get())
				.tempsPendent(tempsPendent)
				.intentsRegEnviament(intentsRegistre)
				.dataRegistrada(dataRegistre)
				.tempsRegistrada(tempsRegistre)
				.dataNotEnviada(dataEnviament)
				.intentsNotEnviament(intentsNotEnviament)
				.dataCieEnviada(dataCieEnviament)
				.intentsCieEnviament(intentsCieEnviament)
				.build();
	}

	private static boolean isCieEstatFinal(CieEstat cieEstat) {
		if (cieEstat == null) {
			return false;
		}
		boolean cieEstatFinal = CieEstat.NOTIFICADA.equals(cieEstat)
				|| CieEstat.CANCELADO.equals(cieEstat)
				|| CieEstat.EXTRAVIADA.equals(cieEstat)
				|| CieEstat.SIN_INFORMACION.equals(cieEstat)
				|| CieEstat.REHUSADA.equals(cieEstat)
				|| CieEstat.ERROR.equals(cieEstat)
				|| CieEstat.DEVUELTO.equals(cieEstat);
		return cieEstatFinal;
	}
}
