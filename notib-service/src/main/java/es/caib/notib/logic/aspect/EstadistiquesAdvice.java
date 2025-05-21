/**
 * 
 */
package es.caib.notib.logic.aspect;

import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
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

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

	private final ConcurrentHashMap<Long, Lock> locks = new ConcurrentHashMap<>();

	private Lock getLockForId(Long id) {
		return locks.computeIfAbsent(id, key -> new ReentrantLock());
	}
	private void releaseLockForId(Long id) {
		locks.remove(id); // Alliberem la referència quan ja no es necessita
	}

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

			ExplotEnvInfoEntity explotEnvInfoEntity = getExplotEnvInfo(enviament, EstatActualEnv.NOU);

			int intents = explotEnvInfoEntity.getIntentsRegEnviament() + 1;
			explotEnvInfoEntity.setIntentsRegEnviament(intents);
			if (success) {
				Date dataRegistre = enviament.getRegistreData();
				long tempsPendent = getMillisBetween(explotEnvInfoEntity.getDataCreacio(), dataRegistre);
				explotEnvInfoEntity.setDataRegistrada(dataRegistre);
				explotEnvInfoEntity.setTempsPendent(tempsPendent);
			} else {
				Date dataIntent = new Date();
				long tempsPendent = getMillisBetween(explotEnvInfoEntity.getDataCreacio(), dataIntent);
				explotEnvInfoEntity.setDataRegEnviamentError(dataIntent);
				explotEnvInfoEntity.setTempsPendent(tempsPendent);
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

			ExplotEnvInfoEntity explotEnvInfoEntity = getExplotEnvInfo(enviament, EstatActualEnv.REGISTRAT);

			int intents = explotEnvInfoEntity.getIntentsSirConsulta() + 1;
			explotEnvInfoEntity.setIntentsSirConsulta(intents);

			if (NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT.equals(enviament.getRegistreEstat()) ||
					NotificacioRegistreEstatEnumDto.REBUTJAT.equals(enviament.getRegistreEstat())) {
				Date dataSir = enviament.getSirRecepcioData() != null ? enviament.getSirRecepcioData() : enviament.getSirRegDestiData();
				long tempsRegistrada = getMillisBetween(explotEnvInfoEntity.getDataRegistrada(), dataSir);
				long tempsTotal = getMillisBetween(explotEnvInfoEntity.getDataCreacio(), dataSir);

				explotEnvInfoEntity.setTempsRegistrada(tempsRegistrada);
				explotEnvInfoEntity.setTempsTotal(tempsTotal);

				if (NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT.equals(enviament.getRegistreEstat())) {
					explotEnvInfoEntity.setDataRegAcceptada(dataSir);
				} else if (NotificacioRegistreEstatEnumDto.REBUTJAT.equals(enviament.getRegistreEstat())) {
					explotEnvInfoEntity.setDataRegRebutjada(dataSir);
				}
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
				ExplotEnvInfoEntity explotEnvInfoEntity = getExplotEnvInfo(enviament, EstatActualEnv.REGISTRAT);

				int intents = explotEnvInfoEntity.getIntentsNotEnviament() + 1;
				explotEnvInfoEntity.setIntentsNotEnviament(intents);
				if (EnviamentEstat.NOTIB_ENVIADA.equals(enviament.getNotificaEstat())) {
					Date dataNotifica = enviament.getNotificaEstatData();
					long tempsRegistrada = getMillisBetween(explotEnvInfoEntity.getDataRegistrada(), dataNotifica);
					explotEnvInfoEntity.setDataNotEnviada(dataNotifica);
					explotEnvInfoEntity.setTempsRegistrada(tempsRegistrada);
				} else if (EnviamentEstat.REGISTRADA.equals(enviament.getNotificaEstat())) {
					explotEnvInfoEntity.setDataNotEnviamentError(new Date());
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
			ExplotEnvInfoEntity explotEnvInfoEntity = getExplotEnvInfo(enviament, EstatActualEnv.ENVIAT_NOT);

			if (enviament.isNotificaEstatFinal()) {
				Date dataEstatFinal = enviament.getNotificaEstatData();
				long tempsNotEnviada = getMillisBetween(explotEnvInfoEntity.getDataNotEnviada(), dataEstatFinal);
				long tempsTotal = getMillisBetween(explotEnvInfoEntity.getDataCreacio(), dataEstatFinal);

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
				ExplotEnvInfoEntity explotEnvInfoEntity = getExplotEnvInfo(enviament, EstatActualEnv.ENVIAT_NOT);

				int intents = explotEnvInfoEntity.getIntentsCieEnviament() + 1;
				explotEnvInfoEntity.setIntentsCieEnviament(intents);
				if ("000".equals(resposta.getCodiResposta())
						&& enviament.getEntregaPostal() != null && CieEstat.ENVIADO_CI.equals(enviament.getEntregaPostal().getCieEstat())) {
					Date dataCie = new Date();
					explotEnvInfoEntity.setDataCieEnviada(dataCie);
				} else {
					explotEnvInfoEntity.setDataCieEnviamentError(new Date());
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
			ExplotEnvInfoEntity explotEnvInfoEntity = getExplotEnvInfo(enviament, EstatActualEnv.ENVIAT_CIE);

			EntregaPostalEntity entregaPostal = enviament.getEntregaPostal();
			CieEstat cieEstat = entregaPostal.getCieEstat();
			boolean cieEstatFinal = isCieEstatFinal(cieEstat);

			if (cieEstatFinal) {
				Date dataEstatFinal = entregaPostal.getCieEstatData();
				long tempsCieEnviada = getMillisBetween(explotEnvInfoEntity.getDataCieEnviada(), dataEstatFinal);
				long tempsTotal = getMillisBetween(explotEnvInfoEntity.getDataCreacio(), dataEstatFinal);

				if (CieEstat.NOTIFICADA.equals(entregaPostal.getCieEstat())) {
					explotEnvInfoEntity.setDataCieNotificada(dataEstatFinal);
				} else if (CieEstat.REHUSADA.equals(entregaPostal.getCieEstat())) {
					explotEnvInfoEntity.setDataCieRebutjada(dataEstatFinal);
				} else if (CieEstat.CANCELADO.equals(entregaPostal.getCieEstat())) {
					explotEnvInfoEntity.setDataCieCancelada(dataEstatFinal);
				} else {
					explotEnvInfoEntity.setDataCieError(dataEstatFinal);
				}

				if (!EnviamentEstat.NOTIFICADA.equals(enviament.getNotificaEstat())
						&& !EnviamentEstat.LLEGIDA.equals(enviament.getNotificaEstat())
						&& CieEstat.NOTIFICADA.equals(entregaPostal.getCieEstat())) {
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
				ExplotEnvInfoEntity explotEnvInfoEntity = getExplotEnvInfo(enviament, EstatActualEnv.REGISTRAT);

				int intents = explotEnvInfoEntity.getIntentsEmailEnviament() + 1;
				explotEnvInfoEntity.setIntentsEmailEnviament(intents);
				if (EnviamentEstat.FINALITZADA.equals(enviament.getNotificaEstat()) || EnviamentEstat.PROCESSADA.equals(enviament.getNotificaEstat())) {
					Date dataEmail = enviament.getNotificaEstatData();
					long tempsRegistrada = getMillisBetween(explotEnvInfoEntity.getDataRegistrada(), dataEmail);
					explotEnvInfoEntity.setDataEmailEnviada(dataEmail);
					explotEnvInfoEntity.setTempsRegistrada(tempsRegistrada);
				} else {
					explotEnvInfoEntity.setDataEmailEnviamentError(new Date());
				}
				explotEnvInfoRepository.save(explotEnvInfoEntity);
			} catch (Exception e) {
				log.error("[estNotificacioEmail] Error generant informació estadística de enviamentId: " + enviament.getId(), e);
			}
		});
	}

	private ExplotEnvInfoEntity getExplotEnvInfo(NotificacioEnviamentEntity enviament, EstatActualEnv estat) {
		Long id = enviament.getId();
		Lock lock = getLockForId(id);

		lock.lock(); // Bloquegem pel valor específic d'enviament
		try {
			return explotEnvInfoRepository.findByEnviamentId(enviament.getId())
				.orElseGet(() -> createExplotEnvInfo(enviament, estat));
		} finally {
			lock.unlock();
			releaseLockForId(id);
		}
	}

	private static ExplotEnvInfoEntity createExplotEnvInfo(NotificacioEnviamentEntity enviament, EstatActualEnv estat) {

		Long tempsPendent = null;
		Long tempsRegistre = null;
		Date dataRegistre = null;
		Date dataEnviament = null;
		Date dataCieEnviament = null;
		int intentsRegistre = 0;
		int intentsNotEnviament = 0;
		int intentsCieEnviament = 0;

		Date dataCreadio = enviament.getCreatedDate().map(d -> Date.from(d.atZone(ZoneId.systemDefault()).toInstant())).orElse(new Date());

		// Controlam l'estat actual degut a que el procés de notificació es realitza mitjançant events,
		// i es podria donar el cas que s'accedeixi abans a alguna acció com registrar o notificar abans d'haver guardat les dades estadístiques
		if (!EstatActualEnv.NOU.equals(estat) && enviament.getRegistreData() != null) {
			intentsRegistre = 1;
			dataRegistre = enviament.getRegistreData();
			tempsPendent = getMillisBetween(dataCreadio, dataRegistre);

			if ((EstatActualEnv.ENVIAT_NOT.equals(estat) || EstatActualEnv.ENVIAT_CIE.equals(estat)) && enviament.getNotificaEstatData() != null) {
				intentsNotEnviament = Math.max(enviament.getNotificaIntentNum(), 1);
				dataEnviament = enviament.getNotificaEstatData();
				tempsRegistre = getMillisBetween(dataRegistre, dataEnviament);

				if (EstatActualEnv.ENVIAT_CIE.equals(estat) && enviament.getEntregaPostal() != null) {
					intentsCieEnviament = 1;
					if (CieEstat.ENVIADO_CI.equals(enviament.getEntregaPostal().getCieEstat())) {
						dataCieEnviament = dataEnviament;
					}
				}
			}
		}
		Long entitatId = enviament.getNotificacio().getEntitat().getId();
		Long procedimentId = enviament.getNotificacio().getProcediment() != null ? enviament.getNotificacio().getProcediment().getId() : null;
		String organGestorCodi = enviament.getNotificacio().getOrganGestor() != null ? enviament.getNotificacio().getOrganGestor().getCodi() : null;
		String usuariCodi = enviament.getCreatedBy().map(createdBy -> createdBy.getCodi()).orElse("DESCONEGUT");
		EnviamentTipus enviamentTipus = enviament.getNotificacio().getEnviamentTipus();
		EnviamentOrigen origen = enviament.getNotificacio().getOrigen();
		return ExplotEnvInfoEntity.builder()
				.enviament(enviament)
				.entitatId(entitatId)
				.procedimentId(procedimentId)
				.organGestorCodi(organGestorCodi)
				.usuariCodi(usuariCodi)
				.enviamentTipus(enviamentTipus)
				.origen(origen)
				.dataCreacio(dataCreadio)
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

	private static long getMillisBetween(Date dataInicial, Date dataFinal) {
		if (dataInicial == null || dataFinal == null) {
			return 0;
		}
		return dataFinal.getTime() - dataInicial.getTime();
	}

	private enum EstatActualEnv {
		NOU,
		REGISTRAT,
		ENVIAT_NOT,
		ENVIAT_CIE
	}
}
