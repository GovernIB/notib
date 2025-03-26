/**
 * 
 */
package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.client.domini.ampliarPlazo.AmpliarPlazoOE;
import es.caib.notib.client.domini.ampliarPlazo.Envios;
import es.caib.notib.client.domini.ampliarPlazo.RespuestaAmpliarPlazoOE;
import es.caib.notib.logic.email.EmailConstants;
import es.caib.notib.logic.helper.*;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioCertificacioDto.TipusActInfo;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDataDto;
import es.caib.notib.logic.intf.dto.notificacio.Enviament;
import es.caib.notib.logic.intf.dto.notificacio.NotTableUpdate;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.EnviamentSmEstatException;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.intf.statemachine.EnviamentSmEstat;
import es.caib.notib.logic.intf.util.MimeUtils;
import es.caib.notib.logic.intf.util.PdfUtils;
import es.caib.notib.logic.mapper.NotificacioMapper;
import es.caib.notib.logic.mapper.NotificacioTableMapper;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.plugin.cie.CiePluginHelper;
import es.caib.notib.logic.plugin.cie.CiePluginJms;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.logic.utils.DatesUtils;
import es.caib.notib.persist.entity.CallbackEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import es.caib.notib.persist.repository.CallbackRepository;
import es.caib.notib.persist.repository.ColumnesRepository;
import es.caib.notib.persist.repository.DocumentRepository;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.PagadorCieRepository;
import es.caib.notib.persist.repository.PersonaRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.persist.repository.ProcedimentRepository;
import es.caib.notib.persist.repository.auditoria.NotificacioAuditRepository;
import es.caib.notib.persist.repository.auditoria.NotificacioEnviamentAuditRepository;
import es.caib.notib.plugin.cie.TipusImpressio;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.plugins.arxiu.api.Document;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementació del servei de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class NotificacioServiceImpl implements NotificacioService {

	@Autowired
	private PermisosService permisosService;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository enviamentRepository;
	@Autowired
	private PagadorCieRepository cieRepository;
	@Autowired
	private NotificacioAuditRepository notificacioAuditRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private NotificacioEnviamentAuditRepository notificacioEnviamentAuditRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private NotificacioTableViewRepository notificacioTableViewRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private PersonaRepository personaRepository;
	@Autowired
	private ColumnesRepository columnesRepository;
	@Autowired
	private PersonaHelper personaHelper;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private RegistreHelper registreHelper;
	@Autowired
	private AplicacioService aplicacioService;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private MetricsHelper metricsHelper;
	@Autowired
	private NotificacioHelper notificacioHelper;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;
	@Autowired
	private EnviamentHelper enviamentHelper;
	@Autowired
	private EnviamentTableHelper enviamentTableHelper;
	@Autowired
	private NotificacioListHelper notificacioListHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private ProcSerOrganRepository procedimentOrganRepository;
	@Autowired
	private EnviamentTableRepository enviamentTableRepository;
	@Autowired
	private DocumentHelper documentHelper;
	@Autowired
	private EmailNotificacioSenseNifHelper emailNotificacioSenseNifHelper;
	@Autowired
	private AuditHelper auditHelper;
    @Autowired
    private CiePluginHelper ciePluginHelper;
	@Autowired
	private CallbackRepository callbackRepository;

	@Autowired
	private EnviamentSmService enviamentSmService;

	@Autowired
	private NotificacioMapper notificacioMapper;
	@Autowired
	private NotificacioTableMapper notificacioTableMapper;
	@Autowired
	protected JmsTemplate jmsTemplate;
	@Autowired
	protected CiePluginJms ciePluginJms;

	private static final String DELETE = "NotificacioServiceImpl.delete";
	private static final String UPDATE = "NotificacioServiceImpl.update";
	private static final String ERROR_DIR3 = "Error recuperant les provincies de DIR3CAIB: ";

	protected static Map<String, ProgresActualitzacioCertificacioDto> progresActualitzacioExpirades = new HashMap<>();
    @Autowired
    private NotificacioEventHelper notificacioEventHelper;


	@Transactional(rollbackFor=Exception.class)
	@Override
	public Notificacio create(Long entitatId, Notificacio notificacio) throws RegistreNotificaException {

		var timer = metricsHelper.iniciMetrica();
		try {
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			var notData = notificacioHelper.buildNotificacioData(entitat, notificacio, false);
			// Dades generals de la notificació
			var notificacioEntity = notificacioHelper.saveNotificacio(notData);
			notificacioHelper.altaEnviamentsWeb(entitat, notificacioEntity, notificacio.getEnviaments());
			auditHelper.auditaNotificacio(notificacioEntity, AuditService.TipusOperacio.CREATE, "NotificacioServiceImpl.create");
			notificacioEntity.getEnviaments().forEach(e -> enviamentSmService.acquireStateMachine(e.getNotificaReferencia()));
			return conversioTipusHelper.convertir(notificacioEntity, Notificacio.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

//	@Transactional
//	@Override
//	public void delete(Long entitatId, Long notificacioId) throws NotFoundException {
//
//		var timer = metricsHelper.iniciMetrica();
//		try {
//			entityComprovarHelper.comprovarEntitat(entitatId,false,true,true,false);
//			log.debug("Esborrant la notificació (notificacioId=" + notificacioId + ")");
//			var notificacio = notificacioRepository.findById(notificacioId).orElse(null);
//			if (notificacio == null) {
//				throw new NotFoundException(notificacioId, NotificacioEntity.class, "No s'ha trobat cap notificació amb l'id especificat");
//			}
//			var enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacioId);
////			### Esborrar la notificació
//			if (enviamentsPendents == null || enviamentsPendents.isEmpty()) {
//				throw new ValidationException("Aquesta notificació està enviada i no es pot esborrar");
//			}
//			// esborram tots els seus events
//			notificacioEventRepository.deleteByNotificacio(notificacio);
//
////				## El titular s'ha d'esborrar de forma individual
//			for (var enviament : notificacio.getEnviaments()) {
//				var titular = enviament.getTitular();
//				if (HibernateHelper.isProxy(titular)) {
//					titular = HibernateHelper.deproxy(titular);
//				}
//				enviamentTableRepository.deleteById(enviament.getId());
//				notificacioEventRepository.deleteByEnviament(enviament);
//				notificacioEnviamentRepository.delete(enviament);
//				notificacioEnviamentRepository.flush();
//				auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.DELETE, DELETE);
//				personaRepository.delete(titular);
//				enviamentSmService.remove(enviament.getNotificaReferencia());
//			}
//			notificacioTableHelper.eliminarRegistre(notificacio);
//			notificacioRepository.delete(notificacio);
//			notificacioRepository.flush();
//			auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.DELETE, DELETE);
//			log.debug("La notificació s'ha esborrat correctament (notificacioId=" + notificacioId + ")");
//		} finally {
//			metricsHelper.fiMetrica(timer);
//		}
//	}

	@Transactional
	@Override
	public void delete(Long entitatId, Long notificacioId) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(entitatId,false,true,true,false);
			log.debug("Esborrant la notificació (notificacioId=" + notificacioId + ")");
			var notificacioEntity = notificacioRepository.findById(notificacioId).orElse(null);
			var notificacioTableEntity = notificacioTableViewRepository.findById(notificacioId).orElse(null);

			if (notificacioEntity == null || notificacioTableEntity == null) {
				throw new NotFoundException(notificacioId, NotificacioEntity.class, "No s'ha trobat cap notificació amb l'id especificat");
			}
			var enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacioId);

			if (notificacioTableEntity.getEnviamentTipus().equals(EnviamentTipus.SIR) && (enviamentsPendents == null || enviamentsPendents.isEmpty())) {
					throw new ValidationException("Aquesta notificació està enviada a SIR i no es pot esborrar");
			}

			if ((enviamentsPendents == null || enviamentsPendents.isEmpty()) && !(notificacioTableEntity.getEstat().equals(NotificacioEstatEnumDto.REGISTRADA))) {
				throw new ValidationException("Aquesta notificació està enviada i no està registrada, per tant, no es pot esborrar");
			}

			notificacioEntity.setDeleted(true);
			notificacioTableEntity.setDeleted(true);
			notificacioRepository.save(notificacioEntity);
			notificacioTableViewRepository.save(notificacioTableEntity);

			notificacioRepository.flush();
			notificacioTableViewRepository.flush();

			log.debug("La notificació s'ha esborrat correctament (notificacioId=" + notificacioId + ")");
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void restore(Long entitatId, Long notificacioId) throws Exception {

		var timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(entitatId,false,true,true,false);
			NotibLogger.getInstance().info("Recuperant la notificació (notificacioId=" + notificacioId + ")", log, LoggingTipus.TAULA_REMESES);
			var notificacioEntity = notificacioRepository.findById(notificacioId).orElse(null);
			var notificacioTableEntity = notificacioTableViewRepository.findById(notificacioId).orElse(null);

			if (notificacioEntity == null || notificacioTableEntity == null) {
				throw new NotFoundException(notificacioId, NotificacioEntity.class, "No s'ha trobat cap notificació amb l'id especificat");
			}


			notificacioEntity.setDeleted(false);
			notificacioTableEntity.setDeleted(false);
			notificacioRepository.saveAndFlush(notificacioEntity);
			notificacioTableViewRepository.saveAndFlush(notificacioTableEntity);

			restuararEnviamentsStateMachine(notificacioEntity.getEnviaments());

			NotibLogger.getInstance().info("La notificació s'ha recuperat correctament (notificacioId=" + notificacioId + ")", log, LoggingTipus.TAULA_REMESES);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private void restuararEnviamentsStateMachine(Set<NotificacioEnviamentEntity> enviaments) throws Exception {

		EnviamentSmEstat estat;
		for (var env : enviaments) {
			estat = enviamentSmService.getEstatEnviament(env.getUuid());
			switch (estat) {
				case NOU:
					enviamentSmService.registreEnviament(env.getUuid(), false);
					break;
				case REGISTRE_PENDENT:
					enviamentSmService.registreEnviament(env.getUuid(), true);
					break;
				case REGISTRE_ERROR:
					enviamentSmService.registreRetry(env.getUuid());
					break;
				case NOTIFICA_PENDENT:
					enviamentSmService.notificaEnviament(env.getUuid(), true);
					break;
				case NOTIFICA_ERROR:
					enviamentSmService.notificaRetry(env.getUuid());
					break;
				default:
					throw new Exception("Enviament " + env.getUuid() + " te un estat que no es pot recuperar " + estat);
			}
		}
	}

	@Transactional
	@Override
	public Notificacio update(Long entitatId, Notificacio notificacio, boolean isAdministradorEntitat) throws NotFoundException, RegistreNotificaException {

		var timer = metricsHelper.iniciMetrica();
		try {
			var entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, true, false);
			var enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacio.getId());
			if (enviamentsPendents == null || enviamentsPendents.isEmpty()) {
				throw new ValidationException("Aquesta notificació està enviada i no es pot modificar");
			}
			var notificacioEntity = notificacioRepository.findById(notificacio.getId()).orElseThrow();
			var notData = notificacioHelper.buildNotificacioData(entitat, notificacio, false);
			// Actualitzar notificació existent
			notificacioEntity.update(
					notData.getEntitat(),
					notData.getNotificacio().getEmisorDir3Codi(),
					notData.getOrganGestor(),
					pluginHelper.getNotibTipusComunicacioDefecte(),
					notData.getNotificacio().getEnviamentTipus(),
					notData.getNotificacio().getConcepte(),
					notData.getNotificacio().getDescripcio(),
					notData.getNotificacio().getEnviamentDataProgramada(),
					notData.getNotificacio().getRetard(),
					notData.getNotificacio().getCaducitat(),
					notData.getNotificacio().getUsuariCodi(),
					notData.getProcSer() != null ? notData.getProcSer().getCodi() : null,
					notData.getProcSer(),
					notData.getGrupNotificacio() != null ? notData.getGrupNotificacio().getCodi() : null,
					notData.getNotificacio().getNumExpedient(),
					TipusUsuariEnumDto.INTERFICIE_WEB,
					notData.getDocumentEntity(),
					notData.getDocument2Entity(),
					notData.getDocument3Entity(),
					notData.getDocument4Entity(),
					notData.getDocument5Entity(),
					notData.getProcedimentOrgan(),
					notData.getNotificacio().getIdioma());

			// Esbo
			if (notificacioEntity.getDocument2() != null && notificacio.getDocument2() == null) {
				documentRepository.delete(notData.getDocument2Entity());
			}
			if (notificacioEntity.getDocument3() != null && notificacio.getDocument3() == null) {
				documentRepository.delete(notData.getDocument3Entity());
			}
			if (notificacioEntity.getDocument4() != null && notificacio.getDocument4() == null) {
				documentRepository.delete(notData.getDocument4Entity());
			}
			if (notificacioEntity.getDocument5() != null && notificacio.getDocument5() == null) {
				documentRepository.delete(notData.getDocument5Entity());
			}

			List<Enviament> enviaments = new ArrayList<>();
			List<Long> enviamentsIds = new ArrayList<>();
			List<Long> destinatarisIds = new ArrayList<>();
			List<NotificacioEnviamentEntity> nousEnviaments = new ArrayList<>();
			for(var enviament: notificacio.getEnviaments()) {
				if (enviament.getTitular() != null) {
					enviaments.add(conversioTipusHelper.convertir(enviament, Enviament.class));
				}
				if (enviament.getId() != null) {//En cas d'enviaments nous
					enviamentsIds.add(enviament.getId());
				}
			}

			// Creació o edició enviament existent
			Enviament enviament;
			Enviament enviamentDto;
			ServeiTipus serveiTipus;
			PersonaEntity titular;
			List<PersonaEntity> nousDestinataris;
			for (var i = 0; i < enviaments.size(); i++) {
				enviament = enviaments.get(i);
				enviamentDto = notificacio.getEnviaments().get(i);
				serveiTipus = null;
				if (enviament.getServeiTipus() != null) {
					switch (enviament.getServeiTipus()) {
					case NORMAL:
						serveiTipus = ServeiTipus.NORMAL;
						break;
					case URGENT:
						serveiTipus = ServeiTipus.URGENT;
						break;
					}
				}

				titular = enviamentDto.getTitular().getId() != null ? personaHelper.update(enviamentDto.getTitular(),  enviament.getTitular().isIncapacitat())
						: personaHelper.create(enviament.getTitular(), enviament.getTitular().isIncapacitat());
				nousDestinataris = new ArrayList<>();
//					### Crear o editar destinataris enviament existent
				if (enviament.getDestinataris() != null) {
					for(var destinatari: enviamentDto.getDestinataris()) {
						if (Strings.isNullOrEmpty(destinatari.getNif()) && Strings.isNullOrEmpty(destinatari.getDir3Codi())) {
							continue;
						}
						if (destinatari.getId() != null) {
							destinatarisIds.add(destinatari.getId());
							personaHelper.update(destinatari, false);
							continue;
						}
						var destinatariEntity = personaHelper.create(destinatari, false);
						nousDestinataris.add(destinatariEntity);
						destinatarisIds.add(destinatariEntity.getId());
					}
				}

//					### Actualitzar les dades d'un enviament existent o crear un de nou
				if (enviamentDto.getId() != null) {
					var enviamentEntity = notificacioEnviamentRepository.findById(enviamentDto.getId()).orElseThrow();
					enviamentEntity.update(enviament, entitat.isAmbEntregaDeh(), serveiTipus, notificacioEntity, titular);
					enviamentEntity.getDestinataris().addAll(nousDestinataris);
					enviamentTableHelper.actualitzarRegistre(enviamentEntity);
					auditHelper.auditaEnviament(enviamentEntity, AuditService.TipusOperacio.UPDATE, UPDATE);
					continue;
				}
				var env = NotificacioEnviamentEntity.getBuilderV2(enviament, entitat.isAmbEntregaDeh(), serveiTipus, notificacioEntity, titular, nousDestinataris, UUID.randomUUID().toString()).build();
				var nouEnviament = notificacioEnviamentRepository.saveAndFlush(env);
				enviamentSmService.altaEnviament(nouEnviament.getNotificaReferencia());
				nousEnviaments.add(nouEnviament);
				enviamentsIds.add(nouEnviament.getId());
				enviamentTableHelper.crearRegistre(nouEnviament);
				auditHelper.auditaEnviament(nouEnviament, AuditService.TipusOperacio.CREATE, UPDATE);
			}
			notificacioEntity.getEnviaments().addAll(nousEnviaments);
//			### Enviaments esborrats
			Set<NotificacioEnviamentEntity> enviamentsDisponibles = new HashSet<>(notificacioEntity.getEnviaments());
			for (var env : enviamentsDisponibles) {
				if (HibernateHelper.isProxy(env)) { //en cas d'haver modificat l'enviament
					env = HibernateHelper.deproxy(env);
				}
				if (!enviamentsIds.contains(env.getId())) {
					notificacioEntity.getEnviaments().remove(env);
					enviamentTableRepository.deleteById(env.getId());
					notificacioEventRepository.deleteByEnviament(env);
					notificacioEnviamentRepository.delete(env);
					notificacioEnviamentRepository.flush();
					auditHelper.auditaEnviament(env, AuditService.TipusOperacio.DELETE, DELETE);
					enviamentSmService.remove(env.getNotificaReferencia());
				}

//				### Destinataris esborrats
				List<PersonaEntity> destinatarisDisponibles = new ArrayList<>(env.getDestinataris());
				for (var destinatari : destinatarisDisponibles) {
					if (HibernateHelper.isProxy(destinatari)) { //en cas d'haver modificat l'interessat
						destinatari = HibernateHelper.deproxy(destinatari);
					}
					if (! destinatarisIds.contains(destinatari.getId())) {
						env.getDestinataris().remove(destinatari);
						personaRepository.delete(destinatari);
					}
				}
			}

			notificacioTableHelper.actualitzarRegistre(notificacioEntity);
			auditHelper.auditaNotificacio(notificacioEntity, AuditService.TipusOperacio.UPDATE, UPDATE);

			// SM
			notificacioEntity.getEnviaments().forEach(e -> enviamentSmService.altaEnviament(e.getNotificaReferencia()));
			return conversioTipusHelper.convertir(notificacioRepository.getOne(notificacio.getId()), Notificacio.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public NotificacioDtoV2 findAmbId(Long id, boolean isAdministrador) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de la notificacio amb id (id=" + id + ")");
			var notificacio = notificacioRepository.findById(id).orElse(null);
			if(notificacio == null) {
				return null;
			}
			entityComprovarHelper.comprovarPermisos(null, false, false, false);
			var enviamentsPendentsNotifica = notificacioEnviamentRepository.findEnviamentsPendentsNotificaByNotificacio(notificacio);
			notificacio.setHasEnviamentsPendents(enviamentsPendentsNotifica != null && !enviamentsPendentsNotifica.isEmpty());
//			pluginHelper.addOficinaAndLlibreRegistre(notificacio);
			return conversioTipusHelper.convertir(notificacio, NotificacioDtoV2.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public NotificacioInfoDto findNotificacioInfo(Long id, boolean isAdministrador) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de la notificacio amb id (id=" + id + ")");
			var notificacio = notificacioRepository.findById(id).orElse(null);
			if (notificacio == null) {
				return null;
			}
			var enviamentsPendentsNotifica = notificacioEnviamentRepository.findEnviamentsPendentsNotificaByNotificacio(notificacio);
			notificacio.setHasEnviamentsPendents(enviamentsPendentsNotifica != null && !enviamentsPendentsNotifica.isEmpty());
			// Emplena els atributs registreLlibreNom i registreOficinaNom
//			pluginHelper.addOficinaAndLlibreRegistre(notificacio);
			var dto = conversioTipusHelper.convertir(notificacio, NotificacioInfoDto.class);
			var llindarDies = configHelper.getConfigAsInteger("es.caib.notib.llindar.dies.enviament.remeses");
			dto.setNotificacioAntiga(DatesUtils.isNowAfterDate(notificacio.getCreatedDate().get(), llindarDies));
			//CALLBACKS
			var pendents = callbackRepository.findByNotificacioIdAndEstatOrderByDataDesc(notificacio.getId(), CallbackEstatEnumDto.PENDENT);
			dto.setEventsCallbackPendent(notificacio.isTipusUsuariAplicacio() && pendents != null && !pendents.isEmpty());
			var df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			var data = pendents != null && !pendents.isEmpty() && pendents.get(0).getData() != null ? df.format(pendents.get(0).getData()) : null;
			dto.setDataCallbackPendent(data);
			int callbackFiReintents = 0;
			CallbackEntity callback;
			List<NotificacioEventEntity> eventNotMovil;
			List<NotificacioEventEntity> lastErrorEvent = new ArrayList<>();
			List<NotificacioEnviamentEntity> enviamentsEntity = new ArrayList<>(notificacio.getEnviaments());
			NotificacioEventEntity eventError;
			var numEnviament = 0;
			var entregaPostal = false;
			NotificacioEnviamentEntity enviament;
			for (var env : dto.getEnviaments()) {

				if (!entregaPostal && env.getEntregaPostal() != null) {
					entregaPostal = true;
				}
				enviament = enviamentsEntity.get(numEnviament);
				boolean plazoAmpliado = dto.isPlazoAmpliado();
				dto.setPlazoAmpliado(plazoAmpliado || enviament.isPlazoAmpliado());
				eventError = enviament.getUltimEvent();
				if (eventError != null && eventError.isError()) {
					lastErrorEvent.add(eventError);
				}
				eventNotMovil = notificacioEventRepository.findLastApiCarpetaByEnviamentId(env.getId());
				if (eventNotMovil != null && !eventNotMovil.isEmpty() && eventNotMovil.get(0).isError()) {
					dto.getNotificacionsMovilErrorDesc().add(eventNotMovil.get(0).getErrorDescripcio());
					env.setNotificacioMovilErrorDesc(eventNotMovil.get(0).getErrorDescripcio());
				}
				if (env.isSirFiPooling()) {
					env.setFiReintents(true);
					env.setFiReintentsDesc(messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + NotificacioEventTipusEnumDto.SIR_FI_POOLING));
				}
				callback = callbackRepository.findByEnviamentIdAndEstat(env.getId(), CallbackEstatEnumDto.ERROR);
				if (callback == null) {
					continue;
				}
				dto.setErrorLastCallback(callback.isError());
				env.setCallbackFiReintents(true);
				env.setCallbackFiReintentsDesc(messageHelper.getMessage("callback.fi.reintents"));
				callbackFiReintents++;
				numEnviament++;
			}
			if (dto.getNotificacionsMovilErrorDesc().size() > 1) {
				List<String> desc = new ArrayList<>();
				desc.add(messageHelper.getMessage("api.carpeta.send.notificacio.movil.error"));
				dto.setNotificacionsMovilErrorDesc(desc);
			}
			if (callbackFiReintents > 0) {
				dto.setCallbackFiReintents(true);
				dto.setCallbackFiReintentsDesc(messageHelper.getMessage("callback.fi.reintents"));
			}
			// Emplena dades del procediment
			var procedimentEntity = notificacio.getProcediment();
			var procComuOrganCie = procedimentEntity != null && procedimentEntity.isComu() && notificacio.getOrganGestor().getEntregaCie() != null;
			if (entregaPostal && (procedimentEntity != null && procedimentEntity.isEntregaCieActivaAlgunNivell() || procComuOrganCie)) {
				var entregaCieEntity = procedimentEntity.getEntregaCieEfectiva();
				entregaCieEntity = entregaCieEntity == null ? notificacio.getOrganGestor().getEntregaCie() : entregaCieEntity;
				dto.setOperadorPostal(conversioTipusHelper.convertir(entregaCieEntity.getOperadorPostal(), OperadorPostalDataDto.class));
				dto.setCie(conversioTipusHelper.convertir(entregaCieEntity.getCie(), CieDataDto.class));
			}
			if (!lastErrorEvent.isEmpty()) {
				String msg = "";
				String tipus = "";
				StringBuilder m = new StringBuilder();
				int env = 1;
				var fiReintents = false;
				for (var event : lastErrorEvent) {

					msg = messageHelper.getMessage("notificacio.event.fi.reintents");
					var et = NotificacioEventTipusEnumDto.SIR_CONSULTA.equals(event.getTipus()) && event.getEnviament().isSirFiPooling() ? NotificacioEventTipusEnumDto.SIR_FI_POOLING : event.getTipus();
					tipus = messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + et);
					m.append("Env ").append(env).append(": ").append(msg).append(" -> ").append(tipus).append("\n");
					env++;
					fiReintents = fiReintents || event.getFiReintents();
					if (entregaPostal && NotificacioEventTipusEnumDto.CIE_ENVIAMENT.equals(event.getTipus())) {
						dto.setErrorEntregaPostal(true);
					}
				}
				dto.setFiReintentsDesc(m.toString());
				dto.setFiReintents(fiReintents);

				dto.setNotificaErrorDescripcio(lastErrorEvent.size() > 1 ? messageHelper.getMessage("error.notificacio.enviaments") : lastErrorEvent.get(0).getErrorDescripcio());

				// TODO S'HA DE POSAR PER TOTS ELS EVENTS
				dto.setNotificaErrorData(lastErrorEvent.get(0).getData());
				dto.setNoticaErrorEventTipus(lastErrorEvent.get(0).getTipus());
				// Obtenir error dels events
				dto.setNotificaErrorTipus(getErrorTipus(lastErrorEvent.get(0)));
			}

			if (entregaPostal) {
				var eventsCie = notificacioEventRepository.findByNotificacioAndTipusAndError(notificacio, NotificacioEventTipusEnumDto.CIE_ENVIAMENT, false);
				dto.setCancelarEntregaPostal(eventsCie != null && !eventsCie.isEmpty());
			}
			dto.setEnviadaDate(notificacioTableHelper.getEnviadaDate(notificacio));
//			var notificacioTableEntity = notificacioTableViewRepository.findById(id).orElse(null);
//			if (notificacioTableEntity == null) {
//				return dto;
//			}
			return dto;
		} catch (Exception ex) {
			log.error("[findNotificacioError] Error al consultar la informacio per la notificacio " + id, ex);
			throw ex;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private NotificacioErrorTipusEnumDto getErrorTipus(NotificacioEventEntity lastErrorEvent) {

		if (lastErrorEvent == null || !NotificacioEstatEnumDto.ENVIADA.equals(lastErrorEvent.getNotificacio().getEstat())) {
			return null;
		}
		if (NotificacioEventTipusEnumDto.SIR_CONSULTA.equals(lastErrorEvent.getTipus()) && Boolean.TRUE.equals(lastErrorEvent.getFiReintents())) {
			return NotificacioErrorTipusEnumDto.ERROR_REINTENTS_SIR;
		}
		if (NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA.equals(lastErrorEvent.getTipus()) && Boolean.TRUE.equals(lastErrorEvent.getFiReintents())) {
			return NotificacioErrorTipusEnumDto.ERROR_REINTENTS_CONSULTA;
		}
		return null;
	}

//	@Transactional(readOnly = true)
	@Transactional
	@Override
	public PaginaDto<NotificacioTableItemDto> findAmbFiltrePaginat(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		NotibLogger.getInstance().info("Consulta taula de remeses ...", log, LoggingTipus.TAULA_REMESES);
		var timer = metricsHelper.iniciMetrica();
		try {
			var pageable = notificacioListHelper.getMappeigPropietats(paginacioParams);
			var rols = aplicacioService.findRolsUsuariActual();
			filtre.setOrganGestor(organGestorCodi);
			var f = notificacioListHelper.getFiltre(filtre, entitatId, rol, usuariCodi, rols);
			var notificacions = notificacioTableViewRepository.findAmbFiltre(f, pageable);
			if (notificacions.getTotalPages() < paginacioParams.getPaginaNum()) {
				paginacioParams.setPaginaNum(0);
				pageable = notificacioListHelper.getMappeigPropietats(paginacioParams);
				notificacions = notificacioTableViewRepository.findAmbFiltre(f, pageable);
			}
			var dtos = notificacioTableMapper.toNotificacionsTableItemDto(
					notificacions.getContent(),
					notificacioListHelper.getCodisProcedimentsAndOrgansAmpPermisProcessar(entitatId, usuariCodi),
					cacheHelper.findOrganigramaNodeByEntitat(f.getEntitat().getDir3Codi()));
			return paginacioHelper.toPaginaDto(dtos, notificacions);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsAmbFiltre(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre) {

		try {
			var rols = aplicacioService.findRolsUsuariActual();
			filtre.setOrganGestor(organGestorCodi);
			var f = notificacioListHelper.getFiltre(filtre, entitatId, rol, usuariCodi, rols);
			return notificacioTableViewRepository.findIdsAmbFiltre(f);
		} finally {
			log.error("Error obtinguent els ids amb filtre de les remeses")
;		}
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<NotificacioDto> findWithCallbackError(NotificacioErrorCallbackFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		var timer = metricsHelper.iniciMetrica();
		try {
			Page<NotificacioEntity> page = null;
			if (filtre == null || filtre.isEmpty()) {
				page = notificacioRepository.findNotificacioLastEventAmbError(
						paginacioHelper.toSpringDataPageable(paginacioParams));
			} else {
				var dataInici = filtre.getDataInici();
				if (dataInici != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataInici);
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataInici = cal.getTime();
				}
				Date dataFi = filtre.getDataFi();
				if (dataFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataFi);
					cal.set(Calendar.HOUR, 23);
					cal.set(Calendar.MINUTE, 59);
					cal.set(Calendar.SECOND, 59);
					cal.set(Calendar.MILLISECOND, 999);
					dataFi = cal.getTime();
				}
				ProcedimentEntity procediment = null;
				if (filtre.getProcedimentId() != null) {
					procediment = procedimentRepository.findById(filtre.getProcedimentId()).orElse(null);
				}
				page = notificacioRepository.findNotificacioLastEventAmbErrorAmbFiltre(
						procediment == null,
						procediment,
						dataInici == null,
						dataInici,
						dataFi == null,
						dataFi,
						filtre.getConcepte() == null || filtre.getConcepte().trim().isEmpty(),
						filtre.getConcepte() == null ? "" : filtre.getConcepte(),
						filtre.getEstat() == null,
						filtre.getEstat(),
						filtre.getEstat() == null ? null : EnviamentEstat.valueOf(filtre.getEstat().toString()),
						filtre.getUsuari() == null || filtre.getUsuari().trim().isEmpty(),
						filtre.getUsuari() == null ? "" : filtre.getUsuari(),
						paginacioHelper.toSpringDataPageable(paginacioParams));
			}
			return page != null && page.getContent() != null && !page.isEmpty() ?
					paginacioHelper.toPaginaDto(page, NotificacioDto.class, notificacioMapper::toCallbackErrorDto) : paginacioHelper.getPaginaDtoBuida(NotificacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CodiValorDto> llistarNivellsAdministracions() {

		var timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValor> codiValor = new ArrayList<>();
			try {
				codiValor = cacheHelper.llistarNivellsAdministracions();
			} catch (Exception ex) {
				log.error("Error recuperant els nivells d'administració de DIR3CAIB: " + ex);
			}
			return conversioTipusHelper.convertirList(codiValor, CodiValorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CodiValorDto> llistarComunitatsAutonomes() {

		var timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValor> codiValor = new ArrayList<>();
			try {
				codiValor = cacheHelper.llistarComunitatsAutonomes();
			} catch (Exception ex) {
				log.error("Error recuperant les comunitats autònomes de DIR3CAIB: " + ex);
			}
			return conversioTipusHelper.convertirList(codiValor, CodiValorDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PaisosDto> llistarPaisos() {

		var timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValorPais> codiValorPais = new ArrayList<>();
			try {
				codiValorPais = cacheHelper.llistarPaisos();
			} catch (Exception ex) {
				log.error("Error recuperant els paisos de DIR3CAIB: " + ex);
			}
			return conversioTipusHelper.convertirList(codiValorPais, PaisosDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProvinciesDto> llistarProvincies() {

		var timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValor> codiValor = new ArrayList<>();
			try {
				codiValor = cacheHelper.llistarProvincies();
			} catch (Exception ex) {
				log.error(ERROR_DIR3 + ex);
			}
			return conversioTipusHelper.convertirList(codiValor, ProvinciesDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProvinciesDto> llistarProvincies(String codiCA) {

		var timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValor> codiValor = new ArrayList<>();
			try {
				codiValor = cacheHelper.llistarProvincies(codiCA);
			} catch (Exception ex) {
				log.error(ERROR_DIR3 + ex);
			}
			return conversioTipusHelper.convertirList(codiValor, ProvinciesDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<LocalitatsDto> llistarLocalitats(String codiProvincia) {

		var timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValor> codiValor = new ArrayList<>();
			try {
				codiValor = cacheHelper.llistarLocalitats(codiProvincia);
			} catch (Exception ex) {
				log.error(ERROR_DIR3 + ex);
			}
			return conversioTipusHelper.convertirList(codiValor, LocalitatsDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> cercaUnitats(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma, Boolean ambOficines,
											 Boolean esUnitatArrel, Long provincia, String municipi) {

		var timer = metricsHelper.iniciMetrica();
		try {
			return pluginHelper.cercaUnitats(codi, denominacio, nivellAdministracio, comunitatAutonoma, ambOficines, esUnitatArrel, provincia, municipi);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> unitatsPerCodi(String codi) {

		var timer = metricsHelper.iniciMetrica();
		try {
			return pluginHelper.unitatsPerCodi(codi);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> unitatsPerDenominacio(String denominacio) {

		var timer = metricsHelper.iniciMetrica();
		try {
			return pluginHelper.unitatsPerDenominacio(denominacio);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbNotificacio(Long entitatId, Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta dels events de la notificació (notificacioId=" + notificacioId + ")");
			return conversioTipusHelper.convertirList(notificacioEventRepository.findByNotificacioIdOrderByDataAsc(notificacioId), NotificacioEventDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioAuditDto> historicFindAmbNotificacio(Long entitatId, Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta dels històrics de la notificació (notificacioId=" + notificacioId + ")");
			var historic = notificacioAuditRepository.findByNotificacioIdOrderByCreatedDateAsc(notificacioId);
			return conversioTipusHelper.convertirList(historic, NotificacioAuditDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public NotificacioEventDto findUltimEventCallbackByNotificacio(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var event = notificacioEventRepository.findUltimEventByNotificacioId(notificacioId);
			if (event == null) {
				return null;
			}
			return conversioTipusHelper.convertir(event, NotificacioEventDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public NotificacioEventDto findUltimEventRegistreByNotificacio(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var event = notificacioEventRepository.findUltimEventRegistreByNotificacioId(notificacioId);
			return event != null ? conversioTipusHelper.convertir(event, NotificacioEventDto.class) : null;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbEnviament(Long entitatId, Long notificacioId, Long enviamentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta dels events associats a un destinatari (notificacioId=" + notificacioId + ", enviamentId=" + enviamentId + ")");
			var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
			entityComprovarHelper.comprovarPermisos(enviament.getNotificacio().getId(), true, true, true);
			return conversioTipusHelper.convertirList(notificacioEventRepository.findByEnviamentIdOrderByDataAsc(enviamentId), NotificacioEventDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEnviamentAuditDto> historicFindAmbEnviament(Long entitatId, Long notificacioId, Long enviamentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta dels events associats a un destinatari (notificacioId=" + notificacioId + ", enviamentId=" + enviamentId + ")");
			var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
			entityComprovarHelper.comprovarPermisos(enviament.getNotificacio().getId(), true, true, true);
			return conversioTipusHelper.convertirList(notificacioEnviamentAuditRepository.findByEnviamentIdOrderByCreatedDateAsc(enviamentId), NotificacioEnviamentAuditDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public byte[] getDiagramaMaquinaEstats() throws IOException {

		var timer = metricsHelper.iniciMetrica();
		try {
			var input = this.getClass().getClassLoader().getResourceAsStream("es/caib/notib/logic/statemachine/diagramaStateMachine.png");
			assert input != null;
			return IOUtils.toByteArray(input);
		} catch (IOException ex) {
            throw new IOException("Arxiu no trobat", ex);
        } finally {
			metricsHelper.fiMetrica(timer);
		}
	}


	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getDocumentArxiu(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var nomDocumentDefault = "document";
			var notificacio = notificacioRepository.findById(notificacioId).orElseThrow();
			try {
				ConfigHelper.setEntitatCodi(notificacio.getEntitat().getCodi());
			} catch (Exception ex) {
				log.error("No s'ha pogut definir l'entitat actual.", ex);
			}
			var document = notificacio.getDocument();
			return documentHelper.documentToArxiuDto(nomDocumentDefault, document);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getDocumentArxiu(Long notificacioId, Long documentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var nomDocumentDefault = "document";
			try {
				var notificacio = notificacioRepository.findById(notificacioId).orElseThrow();
				ConfigHelper.setEntitatCodi(notificacio.getEntitat().getCodi());
			} catch (Exception ex) {
				log.error("No s'ha pogut definir l'entitat actual.", ex);
			}
			var document = documentRepository.findById(documentId).orElse(null);
			return documentHelper.documentToArxiuDto(nomDocumentDefault, document);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto enviamentGetCertificacioArxiu(Long enviamentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
			try {
				ConfigHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
			} catch (Exception ex) {
				log.error("No s'ha pogut definir l'entitat actual.", ex);
			}
			// #779: Obtenim la certificació de forma automàtica
			if (enviament.getNotificaCertificacioArxiuId() == null) {
				enviament = notificaHelper.enviamentRefrescarEstat(enviamentId);
			}
			if (enviament.getNotificaCertificacioArxiuId() == null) {
				throw new RuntimeException("No s'ha trobat la certificació de l'enviament amb id: " + enviamentId);
			}
			var output = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(enviament.getNotificaCertificacioArxiuId(), PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, output, false);
			return new ArxiuDto(calcularNomArxiuCertificacio(enviament), enviament.getNotificaCertificacioMime(), output.toByteArray(), output.size());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public RespostaAccio<String> enviarNotificacioARegistre(Long notificacioId, boolean retry) throws RegistreNotificaException {

		var timer = metricsHelper.iniciMetrica();
		var resposta = new RespostaAccio<String>();
		try {
			var notificacioEntity = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			var llindarDies = configHelper.getConfigAsInteger("es.caib.notib.llindar.dies.enviament.remeses");
			if (DatesUtils.isNowAfterDate(notificacioEntity.getCreatedDate().get(), llindarDies)) {
				log.info("La notificacio amb id " + notificacioId + " no sera enviada ja que es massa antiga. S'ha de tornar a crear");
				resposta.getNoExecutables().add(notificacioId + "");
				return resposta;
			}
			notificacioEntity.getEnviaments().forEach(e -> {
				EnviamentSmEstat estatEnviament = enviamentSmService.getEstatEnviament(e.getUuid());
				try {
					switch (estatEnviament) {
						case REGISTRE_ERROR:
							enviamentSmService.registreRetry(e.getUuid());
							resposta.getExecutades().add(e.getUuid());
							break;
						case REGISTRE_PENDENT:
						case NOU:
							enviamentSmService.registreEnviament(e.getUuid(), retry);
							resposta.getExecutades().add(e.getUuid());
							break;
						default:
							resposta.getNoExecutables().add(e.getUuid());
					}
				} catch (Exception ex) {
					resposta.getErrors().add(e.getUuid());
				}
			});
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public RespostaAccio<String> resetNotificacioARegistre(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		var resposta = new RespostaAccio<String>();
		try {
			var notificacioEntity = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			notificacioEntity.getEnviaments().forEach(e -> {
				var estatEnviament = enviamentSmService.getEstatEnviament(e.getUuid());
				try {
					if (!EnviamentSmEstat.REGISTRE_ERROR.equals(estatEnviament)) {
						return;
					}
					enviamentSmService.registreReset(e.getUuid(), 0);
					resposta.getExecutades().add(e.getUuid());
				} catch (Exception ex) {
					resposta.getErrors().add(e.getUuid());
				}
			});
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void refrescarEstatEnviamentASir(Long enviamentId, boolean retry) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Intentant refrescar estat de l'enviament al registre (enviamentId=" + enviamentId + ")");
			var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
			EnviamentSmEstat estatEnviament = enviamentSmService.getEstatEnviament(enviament.getUuid());
			switch (estatEnviament) {
				case SIR_ERROR:
					enviamentSmService.notificaRetry(enviament.getUuid());
					break;
				case SIR_PENDENT:
					enviamentSmService.notificaEnviament(enviament.getUuid(), retry);
					break;
				default:
					throw new EnviamentSmEstatException("Estat incorrecte per a refrescar l'estat SIR", estatEnviament);
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public boolean enviarNotificacioANotifica(Long notificacioId, boolean retry) {

		var timer = metricsHelper.iniciMetrica();
		var resposta = new RespostaAccio<String>();
		try {
			log.debug("Intentant enviament de la notificació pendent (notificacioId=" + notificacioId + ")");
			var notificacioEntity = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			var llindarDies = configHelper.getConfigAsInteger("es.caib.notib.llindar.dies.enviament.remeses");
			if (DatesUtils.isNowAfterDate(notificacioEntity.getCreatedDate().get(), llindarDies)) {
				log.info("La notificacio amb id " + notificacioId + " no sera enviada ja que es massa antiga. S'ha de tornar a crear");
				return false;
			}
			notificacioEntity.getEnviaments().forEach(e -> {
				var estatEnviament = enviamentSmService.getEstatEnviament(e.getUuid());
				try {
					switch (estatEnviament) {
						case NOTIFICA_RETRY:
						case NOTIFICA_ERROR:
							enviamentSmService.notificaRetry(e.getUuid());
							resposta.getExecutades().add(e.getUuid());
							break;
						case NOTIFICA_PENDENT:
							enviamentSmService.notificaEnviament(e.getUuid(), retry);
							resposta.getExecutades().add(e.getUuid());
							break;
						case SIR_PENDENT:
						case SIR_ERROR:
							enviamentSmService.sirRetry(e.getUuid());
						default:
							resposta.getNoExecutables().add(e.getUuid());
					}
				} catch (Exception ex) {
					resposta.getErrors().add(e.getUuid());
				}
			});
			return !resposta.getExecutades().isEmpty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public boolean resetConsultaEstat(Set<Long> ids) {

		var timer = metricsHelper.iniciMetrica();
		var resposta = new RespostaAccio<String>();
		try {
			NotibLogger.getInstance().info("[MASSIVA] Reset enviamentS " + ids, log, LoggingTipus.MASSIVA);
			var isSir = false;
			NotificacioEnviamentEntity enviament;
			var isAdviser = configHelper.getConfigAsBoolean("es.caib.notib.adviser.actiu");
			for (var id : ids) {
				enviament = enviamentRepository.findById(id).orElseThrow();
				var estatEnviament = enviamentSmService.getEstatEnviament(enviament.getUuid());
				try {
					isSir = EnviamentTipus.SIR.equals(enviament.getNotificacio().getEnviamentTipus());
					if (isSir && (EnviamentSmEstat.SIR_ERROR.equals(estatEnviament) || enviament.isSirFiPooling())) {
						enviament.refreshSirConsulta();
						enviamentSmService.sirReset(enviament.getUuid());
					}
					// mirar si te adivser o no  si en té fer la consulta al momnent
					if (!isAdviser && EnviamentSmEstat.NOTIFICA_SENT.equals(estatEnviament)) {
						enviament.refreshNotificaConsulta();
						enviamentSmService.consultaReset(enviament.getUuid());
					}
					if (isAdviser && (EnviamentSmEstat.CONSULTA_ERROR.equals(estatEnviament) || EnviamentSmEstat.NOTIFICA_SENT.equals(estatEnviament))) {
						notificaHelper.enviamentRefrescarEstat(enviament.getId());
					}

					resposta.getExecutades().add(enviament.getUuid());
				} catch (Exception ex) {
					resposta.getErrors().add(enviament.getUuid());
				}
			}
			return !resposta.getExecutades().isEmpty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(Long entitatId, Long enviamentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Refrescant l'estat de la notificació de Notific@ (enviamentId=" + enviamentId + ")");
			var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
			if (!enviament.isPendentRefrescarEstatNotifica()) {
				return null;
			}
			// SM
			if (enviament.isNotificaEstatFinal()) {
				enviamentSmService.consultaForward(enviament.getUuid());
			}
			enviament = notificaHelper.enviamentRefrescarEstat(enviament.getId());
			var estatDto = conversioTipusHelper.convertir(enviament, NotificacioEnviamenEstatDto.class);
			estatCalcularCampsAddicionals(enviament, estatDto);
			return estatDto;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public String marcarComProcessada(Long notificacioId, String motiu, boolean isAdministrador) throws Exception {

		log.info("PRC >> notificacioId=" + notificacioId + ", motiu=" + motiu + ", isAdmin=" + isAdministrador + ")");
		var timer = metricsHelper.iniciMetrica();
		String resposta = null;
		try {
			var notificacioEntity = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			ConfigHelper.setEntitatCodi(notificacioEntity.getEntitat().getCodi());
			log.info("PRC >> Notificacio trobada");
			if (!NotificacioEstatEnumDto.FINALITZADA.equals(notificacioEntity.getEstat())) {
				log.info("PRC >> Notificacio no finalitzada");
				throw new Exception("La notificació no es pot marcar com a processada, no esta en estat finalitzada.");
			}
			if (!isAdministrador && !permisosService.hasNotificacioPermis(notificacioId, notificacioEntity.getEntitat().getId(), notificacioEntity.getUsuariCodi(), PermisEnum.PROCESSAR)) {
				log.info("PRC >> Sense permisos");
				throw new Exception("La notificació no es pot marcar com a processada, l'usuari no té els permisos requerits.");
			}

			notificacioEntity.updateEstat(NotificacioEstatEnumDto.PROCESSADA);
			notificacioEntity.updateEstatProcessatDate(new Date());
			notificacioEntity.updateMotiu(motiu);
			notificacioTableHelper.actualitzar(NotTableUpdate.builder().id(notificacioEntity.getId()).estat(NotificacioEstatEnumDto.PROCESSADA).estatProcessatDate(new Date()).build());
			log.info("PRC >> Notificacio table actualitzada");

			var usuari = usuariHelper.getUsuariAutenticat();
			if (usuari != null && notificacioEntity.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB) {
				for (var enviament : notificacioEntity.getEnviaments()) {
					try {
						jmsTemplate.convertAndSend(EmailConstants.CUA_EMAIL_NOTIFICACIO, enviament.getId());
					} catch (JmsException ex) {
						log.error("Hi ha hagut un error al intentar enviar el correu electrònic de l'enviament " + enviament.getId() + " de la notificació amb id: ." + notificacioId, ex);
						resposta = "No s'ha pogut avisar per correu electrònic: " + ex.getMessage();
					}
				}
			}
			log.info("PRC >> Email enviat si s'escau");
			notificacioRepository.saveAndFlush(notificacioEntity);
			auditHelper.auditaNotificacio(notificacioEntity, AuditService.TipusOperacio.UPDATE, "NotificacioService.marcarComProcessada");
			log.info("PRC >> auditat");
		} catch (Exception ex) {
			log.error("Excepció al marcarComProcessada (notificacioId=" + notificacioId + ", motiu=" + motiu + ", isAdmin=" + isAdministrador + ")", ex);
			throw ex;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
		return resposta;
	}

	/**
	 * Comprova si l'usuari actual té un determinat permís sobre una notificació
	 *
	 * @param notificacio Notificació a comprovar
	 * @return boleà indicant si l'usuari té el permís
	 */
	private boolean hasPermisProcessar(NotificacioEntity notificacio) {

		boolean hasPermis = false;
		var procedimentNotificacio = notificacio.getProcediment();
		if (procedimentNotificacio != null) {
			hasPermis = entityComprovarHelper.hasPermisProcediment(notificacio.getProcediment().getId(), PermisEnum.PROCESSAR);
		}
		// Si no té permís otorgat a nivell de procediment, en els casos en que l'òrgan gestor no vé fix pel procediment l'hem de comprovar
		if (!hasPermis && (procedimentNotificacio == null || procedimentNotificacio.isComu())) {
			hasPermis = entityComprovarHelper.hasPermisOrganGestor(notificacio.getOrganGestor(), PermisEnum.PROCESSAR);
		}
		// En cas de procediments comuns també es pot tenir permís per a la tupla procediment-organ
		if (!hasPermis && procedimentNotificacio != null && procedimentNotificacio.isComu()) {
			var procedimentOrganEntity = procedimentOrganRepository.findByProcSerIdAndOrganGestorId(procedimentNotificacio.getId(), notificacio.getOrganGestor().getId());
			hasPermis = entityComprovarHelper.hasPermisProcedimentOrgan(procedimentOrganEntity, PermisEnum.PROCESSAR);
		}
		return hasPermis;
	}

	@Transactional
	@Override
	public boolean reactivarConsulta(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Reactivant consultes d'estat de la notificació (notificacioId=" + notificacioId + ")");
			var notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			for(var enviament: notificacio.getEnviaments()) {
				enviament.refreshNotificaConsulta();
				enviamentSmService.consultaReset(enviament.getUuid());
			}
			notificacioTableHelper.actualitzarRegistre(notificacio);
			notificacioRepository.saveAndFlush(notificacio);
			return true;
		} catch (Exception e) {
			log.debug("Error reactivant consultes d'estat de la notificació (notificacioId=" + notificacioId + ")", e);
			return false;	
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public boolean reactivarSir(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Reactivant consultes d'estat de SIR (notificacioId=" + notificacioId + ")");
			var notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			NotificacioEventEntity event;
			for(var enviament: notificacio.getEnviaments()) {
				enviament.refreshSirConsulta();
				event = enviament.getUltimEvent();
				if (event != null) {
					event.setFiReintents(false);
				}
				auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "NotificacioServiceImpl.reactivarSir");
			}
			notificacioTableHelper.actualitzarRegistre(notificacio);
			notificacioRepository.saveAndFlush(notificacio);
			return true;
		} catch (Exception e) {
			log.debug("Error reactivant consultes a SIR de la notificació (notificacioId=" + notificacioId + ")", e);
			return false;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	// SCHEDULLED METHODS
	////////////////////////////////////////////////////////////////
	
	@Transactional(readOnly = true)
	@Override
	public List<Long> getNotificacionsPendentsRegistrar() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var maxPendents = getRegistreEnviamentsProcessarMaxProperty();
			return notificacioRepository.findByNotificaEstatPendent(pluginHelper.getRegistreReintentsMaxProperty(), PageRequest.of(0, maxPendents));
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> getNotificacionsPendentsEnviar() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var maxPendents = getNotificaEnviamentsProcessarMaxProperty();
			var maxReintents = pluginHelper.getNotificaReintentsMaxProperty();
			return notificacioRepository.findByNotificaEstatRegistradaAmbReintentsDisponibles(maxReintents, PageRequest.of(0, maxPendents));
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void notificacioEnviar(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			var enviamentsSenseNifNoEnviats = notificacio.getEnviamentsPerEmailNoEnviats();
			// 3 possibles casuístiques
			if (enviamentsSenseNifNoEnviats.isEmpty()) {
				// 1. Tots els enviaments a Notifica
				notificaHelper.notificacioEnviar(notificacio.getId());
			} else if (notificacio.getEnviamentsNoEnviats().size() <= enviamentsSenseNifNoEnviats.size()) {
			// 2. Tots els enviaments per email
				emailNotificacioSenseNifHelper.notificacioEnviarEmail(enviamentsSenseNifNoEnviats, true);
				return;
			} else {
				// 3. Una part dels enviaments a Notifica i l'altre via email
				notificaHelper.notificacioEnviar(notificacio.getId(), true);
				// Fa falta enviar els restants per email
				emailNotificacioSenseNifHelper.notificacioEnviarEmail(enviamentsSenseNifNoEnviats, false);
			}
			// 4. Enviar la entrega postal
			if (isEntregaCieActiva(notificacio) && NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat())) {
				enviarEntregaPostal(notificacio.getReferencia(), false);
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private boolean isEntregaCieActiva(NotificacioEntity notificacio) {

		var enviaments = notificacio.getEnviaments();
		EntregaCieEntity cieProcediment, cieOrgan;
		for (var e : enviaments) {
			if (e.getEntregaPostal() == null) {
				continue;
			}
			cieProcediment = notificacio.getProcediment().getEntregaCieEfectiva();
			if (cieProcediment != null && cieProcediment.getCie().isCieExtern()) {
				return true;
			}
			cieOrgan = notificacio.getOrganGestor().getEntregaCie();
			if (notificacio.getProcediment().isComu() && cieOrgan != null && cieOrgan.getCie().isCieExtern()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean enviarEntregaPostal(String uuid, boolean retry) {

		try {
			if (ciePluginJms.enviarMissatge(uuid, retry)) {
				return true;
			}
			var notificacio = notificacioRepository.findByReferencia(uuid);
			notificacioEventHelper.addCieEventEnviar(notificacio, true, "Error inesperat al enviar la entrega postal", false);
			return false;
		} catch (Exception ex) {
			log.error("Error al enviar la entrega postal", ex);
			return false;
		}
	}

	@Override
	public boolean cancelarEntregaPostal(Long enviamentId) {

		try {
			var env = enviamentRepository.findById(enviamentId).orElseThrow();
			return ciePluginJms.cancelarEnviament(env.getUuid());
		} catch (Exception ex) {
			log.error("Error cancelant l'entrega postal", ex);
			return false;
		}
	}

	@Override
	public boolean consultarEstatEntregaPostal(Long enviamentId) {

		try {
			ciePluginHelper.consultarEstatEntregaPostal(enviamentId);
			return true;
		} catch (Exception ex) {
			log.error("Error consultant l'estat de l'entrega postal", ex);
			return false;
		}
	}


	@Transactional(readOnly = true)
	@Override
	public List<Long> getNotificacionsPendentsRefrescarEstat() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var maxPendents = getEnviamentActualitzacioEstatProcessarMaxProperty();
			return notificacioEnviamentRepository.findByNotificaRefresc(pluginHelper.getConsultaReintentsMaxProperty(), PageRequest.of(0, maxPendents));
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void enviamentRefrescarEstat(Long enviamentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			notificaHelper.enviamentRefrescarEstat(enviamentId);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<Long> getNotificacionsPendentsRefrescarEstatRegistre() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var maxPendents = getEnviamentActualitzacioEstatRegistreProcessarMaxProperty();
			return notificacioEnviamentRepository.findByRegistreRefresc(pluginHelper.getConsultaSirReintentsMaxProperty(), PageRequest.of(0, maxPendents));
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void enviamentRefrescarEstatRegistre(Long enviamentId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			registreHelper.enviamentRefrescarEstatRegistre(enviamentId);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public Boolean enviamentRefrescarEstatSir(Long enviamentId) {

		var timer = metricsHelper.iniciMetrica();
		var totBe = false;
		try {
			var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
			if (!enviament.isPendentRefrescarEstatRegistre()) {
				return null;
			}
			enviament = registreHelper.enviamentRefrescarEstatRegistre(enviamentId);
			totBe = enviament.getSirConsultaIntent() == 0;
			if (totBe) {
				enviamentSmService.sirSuccess(enviament.getUuid());
			} else {
				enviamentSmService.sirFailed(enviament.getUuid());
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
		return totBe;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<NotificacioDto> findNotificacionsAmbErrorRegistre(Long entitatId, NotificacioRegistreErrorFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		Page<NotificacioEntity> page = null;
		var timer = metricsHelper.iniciMetrica();
		try {
			if (filtre == null || filtre.isEmpty()) {
				var pageable = paginacioHelper.toSpringDataPageable(paginacioParams);
				page = notificacioRepository.findByNotificaEstatPendentSenseReintentsDisponibles(entitatId, pluginHelper.getRegistreReintentsMaxProperty(), pageable);
			} else {
				var dataInici = filtre.getDataInici();
				if (dataInici != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataInici);
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					dataInici = cal.getTime();
				}
				var dataFi = filtre.getDataFi();
				if (dataFi != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataFi);
					cal.set(Calendar.HOUR, 23);
					cal.set(Calendar.MINUTE, 59);
					cal.set(Calendar.SECOND, 59);
					cal.set(Calendar.MILLISECOND, 999);
					dataFi = cal.getTime();
				}
				ProcedimentEntity procediment = null;
				if (filtre.getProcedimentId() != null) {
					procediment = procedimentRepository.findById(filtre.getProcedimentId()).orElse(null);
				}
				page = notificacioRepository.findByNotificaEstatPendentSenseReintentsDisponiblesAmbFiltre(
						entitatId,
						procediment == null,
						procediment,
						dataInici == null,
						dataInici,
						dataFi == null,
						dataFi,
						filtre.getConcepte() == null || filtre.getConcepte().trim().isEmpty(),
						filtre.getConcepte() == null ? "" : filtre.getConcepte(),
						filtre.getUsuari() == null || filtre.getUsuari().trim().isEmpty(),
						filtre.getUsuari() == null ? "" : filtre.getUsuari(),
						pluginHelper.getRegistreReintentsMaxProperty(),
						paginacioHelper.toSpringDataPageable(paginacioParams));
			}
				
			return page != null && page.getContent() != null && !page.getContent().isEmpty() ?
					paginacioHelper.toPaginaDto(page, NotificacioDto.class, notificacioMapper::toErrorRegistreDto): paginacioHelper.getPaginaDtoBuida(NotificacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	public List<Long> findNotificacionsIdAmbErrorRegistre(Long entitatId, NotificacioRegistreErrorFiltreDto filtre) {

		var timer = metricsHelper.iniciMetrica();
		try {
			if (filtre == null || filtre.isEmpty()) {
				return notificacioRepository.findIdsByNotificaEstatPendentSenseReintentsDisponibles(entitatId, pluginHelper.getRegistreReintentsMaxProperty());
			}
			var dataInici = filtre.getDataInici();
			if (dataInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataInici);
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataInici = cal.getTime();
			}
			var dataFi = filtre.getDataFi();
			if (dataFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataFi);
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataFi = cal.getTime();
			}
			ProcedimentEntity procediment = null;
			if (filtre.getProcedimentId() != null) {
				procediment = procedimentRepository.findById(filtre.getProcedimentId()).orElse(null);
			}
			return notificacioRepository.findIdsByNotificaEstatPendentSenseReintentsDisponiblesAmbFiltre(
					entitatId,
					procediment == null,
					procediment,
					dataInici == null,
					dataInici,
					dataFi == null,
					dataFi,
					filtre.getConcepte() == null || filtre.getConcepte().trim().isEmpty(),
					filtre.getConcepte() == null ? "" : filtre.getConcepte(),
					filtre.getUsuari() == null || filtre.getUsuari().trim().isEmpty(),
					filtre.getUsuari() == null ? "" : filtre.getUsuari(),
					pluginHelper.getRegistreReintentsMaxProperty());

		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void reactivarRegistre(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Reactivant registre de la notificació (notificacioId=" + notificacioId + ")");
			var notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			notificacio.refreshRegistre();
			notificacioRepository.saveAndFlush(notificacio);
			notificacioTableHelper.actualitzarRegistre(notificacio);
			var events = notificacioEventRepository.findByNotificacioAndTipusAndErrorOrderByDataDescIdDesc(notificacio, NotificacioEventTipusEnumDto.REGISTRE_ENVIAMENT, true);
			for (var event : events) {
				event.setFiReintents(false);
			}
			auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.UPDATE, "NotificacioServiceImpl.reactivarRegistre");
		} catch (Exception e) {
			log.debug("Error reactivant consultes d'estat de la notificació (notificacioId=" + notificacioId + ")", e);
		} finally {
			metricsHelper.fiMetrica(timer);
		}		
	}

	@Transactional
	@Override
	public void reenviarNotificaionsMovil(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {

			log.debug("Reenviar notificació movil pels enviaments de la notificació " + notificacioId );
			var notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			for (var e : notificacio.getEnviaments()) {
				pluginHelper.enviarNotificacioMobil(e);
			}
		} catch (Exception e) {
			log.debug("Error reenviant la notifciació mòvil pels enviaments de la notificació " + notificacioId, e);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public boolean reenviarNotificacioAmbErrors(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			if ((NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(notificacio.getEstat())
				|| NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())) && !notificacio.isJustificantCreat()) {

				notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS);
				enviarNotificacioANotifica(notificacioId, true);
				return true;
			}
		} catch (Exception e) {
			log.debug("Error reenviant notificació amb errors (notificacioId=" + notificacioId + ")", e);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
		return false;
	}

	@Transactional
	@Override
	public RespostaAccio<String> reactivarNotificacioAmbErrors(Set<Long> enviaments) {

		var timer = metricsHelper.iniciMetrica();
		var resposta = new RespostaAccio<String>();
		try {
			Set<NotificacioEntity> notificacionsPostals = new HashSet<>();
			NotibLogger.getInstance().info("[MASSIVA] Reactivar enviaments amb error a registre, notifica o entrega apostal amb fi reintents" + enviaments, log, LoggingTipus.MASSIVA);
			NotificacioEntity notificacio = null;
			NotificacioEnviamentEntity enviament;
			var llindarDies = configHelper.getConfigAsInteger("es.caib.notib.llindar.dies.enviament.remeses");
			AtomicInteger enviamentCounter = new AtomicInteger(1);
			var globalDelay = configHelper.getConfigAsLong("es.caib.notib.massives.state.machine.inici.delay", SmConstants.MASSIU_DELAY);
			for (var id : enviaments) {
				enviament = enviamentRepository.findById(id).orElse(null);
				if (enviament == null) {
					log.error("MASSIVA] No existeix cap enviament amb id " + id);
					resposta.getErrors().add(id + "");
					continue;
				}
				notificacio = enviament.getNotificacio();
				var saltar = DatesUtils.isNowAfterDate(notificacio.getCreatedDate().get(), llindarDies);
				if (saltar) {
					resposta.getNoExecutables().add(enviament.getUuid());
					continue;
				}
				try {
					var estatEnviament = enviamentSmService.getEstatEnviament(enviament.getUuid());
					var delay = enviamentCounter.getAndIncrement() * globalDelay;
					var e = enviament;
					if (EnviamentSmEstat.REGISTRE_ERROR.equals(estatEnviament) || EnviamentSmEstat.REGISTRE_PENDENT.equals(estatEnviament)) {
						notificacio.refreshRegistre();
						new Thread(() -> enviamentSmService.registreReset(e.getUuid(), delay)).start();
//						enviamentSmService.registreReset(enviament.getUuid());
						resposta.getExecutades().add(enviament.getUuid());
						continue;
					}
					if (EnviamentSmEstat.NOTIFICA_ERROR.equals(estatEnviament) || EnviamentSmEstat.NOTIFICA_PENDENT.equals(estatEnviament)) {
						notificacio.resetIntentsNotificacio();
						new Thread(() -> enviamentSmService.notificaReset(e.getUuid(), delay)).start();
//						enviamentSmService.notificaReset(enviament.getUuid(), delay);
						resposta.getExecutades().add(enviament.getUuid());
						continue;
					}
					if (enviament.getEntregaPostal() != null) {
						var ultimEvent = enviament.getUltimEvent();
						if (ultimEvent != null && NotificacioEventTipusEnumDto.CIE_ENVIAMENT.equals(ultimEvent.getTipus()) && ultimEvent.isError()) {
							notificacionsPostals.add(notificacio);
							resposta.getExecutades().add(enviament.getUuid());
						}
					}
				} catch (Exception ex) {
					resposta.getErrors().add(enviament.getUuid());
				}
			}
			var ok = true;
			for (var not : notificacionsPostals) {
				ok = ciePluginJms.enviarMissatge(not.getReferencia(), false);
				if (!ok) {
					for (var env : notificacio.getEnviaments()) {
						resposta.getErrors().add(env.getUuid());
					}
				}
			}
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public boolean reactivarNotificacioAmbErrors(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			if (!NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(notificacio.getEstat())
					&& !NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS.equals(notificacio.getEstat())) {
				return false;
			}
			notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS);
			notificacio.resetIntentsNotificacio();
			var enviaments = notificacio.getEnviaments();
			for (var env : enviaments) {
				if (EnviamentSmEstat.NOTIFICA_ERROR.equals(enviamentSmService.getEstat(env.getUuid()))) {
					enviamentSmService.consultaReset(env.getUuid());
				}
				if (env.getUltimEvent() != null && env.getUltimEvent().getFiReintents()) {
					env.getUltimEvent().setFiReintents(false);
				}
			}
			// TODO VEURE PERQUE EL MÈTODE UPDATE DEL REPOSITORY NO FUNCIONA
//			var events = notificacioEventRepository.findEventsAmbFiReintentsByNotificacioId(notificacioId);
//			for (var e : events) {
//				e.setFiReintents(false);
//			}
			// D'ALGUNA FORMA NO ESTÀ QUADRAN ELS REINTENTS DE LA NOTIFICACIO AMB LA DELS EVENTS
			var not = NotTableUpdate.builder().id(notificacioId).estat(NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS).build();
			notificacioTableHelper.actualitzar(not);
			auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.UPDATE, "NotificacioServiceImpl.reactivarNotificacioAmbErrors");
			return true;
		} catch (Exception e) {
			log.debug("Error reactivant notificació amb errors (notificacioId=" + notificacioId + ")", e);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
		return false;
	}

    @Override
    public SignatureInfoDto checkIfSignedAttached(byte[] contingut, String nom, String contentType) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var info = pluginHelper.detectSignedAttachedUsingValidateSignaturePlugin(contingut, nom, contentType);
			return info;
		} catch (Exception ex) {
			log.error("Error detectant la signatura", ex);
			throw ex;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
    }

	@Override
	public DocCieValid validateDocCIE(byte[] bytes) throws IOException {

		List<String> errors = new ArrayList<>();

//		var cieOrgan = cieRepository.findByEntitatAndOrganGestor(entitat, organGestor);
//		if (!cieOrgan.isCieExtern()) {
//			return DocCieValid.builder().errorsCie(errors).build();
//		}
		var prefix = new Object[]{""};
		if (!MimeUtils.isPDF(Base64.encodeBase64String(bytes))) {
			errors.add(messageHelper.getMessage("error.validacio.10755", prefix));
		}
		if (bytes.length > 5242880) {
			errors.add(messageHelper.getMessage("error.validacio.107554", prefix));
		}

		var pdf = new PdfUtils(bytes);
		var versio = "7"; // 1.7
		if (pdf.versionGreaterThan(versio)) {
			errors.add(messageHelper.getMessage("error.validacio.107551", new Object[]{"1.7"}));
		}
		if (!pdf.isDinA4()) {
			errors.add(messageHelper.getMessage("error.validacio.107552", prefix));
		}
		if (!pdf.maxPages(TipusImpressio.SIMPLEX.name())) {
			errors.add(messageHelper.getMessage("error.validacio.107553", prefix));
		}
		if (pdf.isEditBlocked()) {
			errors.add(messageHelper.getMessage("error.validacio.107555", prefix));
		}
		if (pdf.hasNoneEmbeddedFonts() && !pdf.hasBaseFonts()) {
			errors.add(messageHelper.getMessage("error.validacio.107556", prefix));
		}
		if (!Strings.isNullOrEmpty(pdf.getJavaScript())) {
			errors.add(messageHelper.getMessage("error.validacio.107557", prefix));
		}
		if (pdf.hasExternalLinks()) {
			errors.add(messageHelper.getMessage("error.validacio.107558", prefix));
		}
		if (pdf.hasTransparency()) {
			errors.add(messageHelper.getMessage("error.validacio.107559", prefix));
		}
		if (pdf.hasAttachedFiles()) {
			errors.add(messageHelper.getMessage("error.validacio.107560", prefix));
		}
		if (pdf.hasMultimedia()) {
			errors.add(messageHelper.getMessage("error.validacio.107561", prefix));
		}
		if (pdf.hasNonPrintableAnnotations()) {
			errors.add(messageHelper.getMessage("error.validacio.107562", prefix));
		}
//		if (pdf.hasForms()) {
//			errors.add(messageHelper.getMessage("error.validacio.107563", prefix));
//		}
		if (pdf.hasNoneEmbeddedImages()) {
			errors.add(messageHelper.getMessage("error.validacio.107564", prefix));
		}
		if (pdf.isPrintingAllowed()) {
			errors.add(messageHelper.getMessage("error.validacio.107565", prefix));
		}
		if (pdf.isModifyAllowed()) {
			errors.add(messageHelper.getMessage("error.validacio.107566", prefix));
		}
		if (!pdf.isMaxRightMarginOk()) {
			errors.add(messageHelper.getMessage("error.validacio.107567", prefix));
		}
		var msg = !errors.isEmpty() ? messageHelper.getMessage("errors.validacio.cie") : "";

		return DocCieValid.builder().errorsCie(errors).errorCieMsg(msg).build();
	}

	@Override
	@Transactional
	public void updateEstatList(Long notificacioId) {

		var item = notificacioTableViewRepository.findById(notificacioId).orElse(null);
		if (item == null) {
			return;
		}
		try {
			var not = item.getNotificacio();
			notificacioTableHelper.actualitzarRegistre(not);
			for (var env : not.getEnviaments()) {
				enviamentTableHelper.actualitzarRegistre(env);
			}
		} catch (Exception ex) {
			log.error("Error actualitzant les taules auxiliars de la notificacio i l'enviament", ex);
		}
		item.setPerActualitzar(true);
		notificacioTableViewRepository.save(item);
	}

	@Override
	public RespuestaAmpliarPlazoOE ampliacionPlazoOE(AmpliacionPlazoDto dto) {

		var timer = metricsHelper.iniciMetrica();
		try {
			List<String> identificadors = new ArrayList<>();
			var isNotificacio = dto.getNotificacioId() != null;
			var isEnviament = dto.getEnviamentId() != null;
			var isNotificacioMassiu = dto.getNotificacionsId() != null;
			var isEnviamentMassiu = dto.getEnviamentsId() != null;
			if (isNotificacio) {
				var notificacio = notificacioRepository.findById(dto.getNotificacioId()).orElseThrow();
				for (var enviament : notificacio.getEnviaments()) {
					if (enviament.getEntregaPostal() == null && !Strings.isNullOrEmpty(enviament.getNotificaIdentificador())) {
						identificadors.add(enviament.getNotificaReferencia());
					}
				}
			}
			if (isEnviament) {
				var enviament = enviamentRepository.findById(dto.getEnviamentId()).orElseThrow();
				if (enviament.getEntregaPostal() == null && !Strings.isNullOrEmpty(enviament.getNotificaIdentificador())) {
					identificadors.add(enviament.getNotificaReferencia());
				}
			}
			if (isNotificacioMassiu) {
				var notificacions = notificacioRepository.findByIdIn(dto.getNotificacionsId());
				for (var not : notificacions) {
					for (var enviament : not.getEnviaments()) {
						if (enviament.getEntregaPostal() == null && !Strings.isNullOrEmpty(enviament.getNotificaIdentificador())) {
							identificadors.add(enviament.getNotificaReferencia());
						}
					}
				}
			}
			if (isEnviamentMassiu) {
				var enviaments = enviamentRepository.findByIdIn(dto.getEnviamentsId());
				for (var enviament : enviaments) {
					if (enviament.getEntregaPostal() == null && !Strings.isNullOrEmpty(enviament.getNotificaIdentificador())) {
						identificadors.add(enviament.getNotificaReferencia());
					}
				}
			}
			var envios = new Envios();
			envios.setIdentificador(identificadors);
			var ampliarPlazoOE = new AmpliarPlazoOE(envios, dto.getDies(), dto.getMotiu());
			return notificaHelper.ampliarPlazoOE(ampliarPlazoOE);
		} catch (Exception ex) {
			var msg = "Error inesperat al ampliarPlazoOE ";
			log.error(msg, ex);
			var resposta = new RespuestaAmpliarPlazoOE();
			resposta.setDescripcionRespuesta(msg + ex.getMessage());
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public Date getCaducitat(Long notificacioId) {

		try {
			return notificacioRepository.findById(notificacioId).orElseThrow().getCaducitat();
		} catch (Exception ex) {
			log.error("Error al obtenir la caducitat de la notificacio amb id " + notificacioId, ex);
			return null;
		}
	}

	@Override
	public void refrescarEnviamentsExpirats() {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("S'ha iniciat els procés d'actualització dels enviaments expirats");
			var auth = SecurityContextHolder.getContext().getAuthentication();
			var username = auth == null ? "schedulled" : auth.getName();
			var progres = progresActualitzacioExpirades.get(username);
			if (progres != null) {
				progres.addInfo(TipusActInfo.ERROR, "Existeix un altre procés en progrés...");
				return;
			}
			progres = new ProgresActualitzacioCertificacioDto();
			progresActualitzacioExpirades.put(username, progres);
			enviamentHelper.refrescarEnviamentsExpirats(progres);
			progres.setProgres(100);
			progres.setFinished(true);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public ProgresActualitzacioCertificacioDto actualitzacioEnviamentsEstat() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var auth = SecurityContextHolder.getContext().getAuthentication();
			var progres = progresActualitzacioExpirades.get(auth.getName());
			if (progres != null && progres.isFinished()) {
				progresActualitzacioExpirades.remove(auth.getName());
			}
			return progres;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	@Override
	public List<Long> getNotificacionsDEHPendentsRefrescarCert() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var maxPendents = getEnviamentDEHActualitzacioCertProcessarMaxProperty();
			return notificacioEnviamentRepository.findByDEHAndEstatFinal(pluginHelper.getConsultaReintentsDEHMaxProperty(), PageRequest.of(0, maxPendents));
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> getNotificacionsCIEPendentsRefrescarCert() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var maxPendents = getEnviamentCIEActualitzacioCertProcessarMaxProperty();
			return notificacioEnviamentRepository.findByCIEAndEstatFinal(pluginHelper.getConsultaReintentsCIEMaxProperty(), PageRequest.of(0, maxPendents));
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public int getMaxAccionesMassives() {

		var max = configHelper.getConfigAsInteger("es.caib.notib.maxim.accions.massives");
		return max != null ? max : 250;
	}

	private int getRegistreEnviamentsProcessarMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.registre.enviaments.processar.max");
	}
	private int getNotificaEnviamentsProcessarMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.notifica.enviaments.processar.max");
	}
	private int getEnviamentActualitzacioEstatProcessarMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.processar.max");
	}
	private int getEnviamentDEHActualitzacioCertProcessarMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.deh.actualitzacio.certificacio.processar.max");
	}
	private int getEnviamentCIEActualitzacioCertProcessarMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.cie.actualitzacio.certificacio.processar.max");
	}
	private int getEnviamentActualitzacioEstatRegistreProcessarMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.processar.max");
	}

	private int getMidaMinIdCsv() {
		return configHelper.getConfigAsInteger("es.caib.notib.document.consulta.id.csv.mida.min");
	}
	

	@Transactional
	public void estatCalcularCampsAddicionals(
			NotificacioEnviamentEntity enviament,
			NotificacioEnviamenEstatDto estatDto) {
		if (enviament.isNotificaError()) {
			try {
				NotificacioEventEntity event = null;
				if (enviament.getUltimEvent() != null && enviament.getUltimEvent().getId() != null) {
					event = notificacioEventRepository.getOne(enviament.getUltimEvent().getId());
				}
				if (event != null) {
					estatDto.setNotificaErrorData(event.getData());
					estatDto.setNotificaErrorDescripcio(event.getErrorDescripcio());
				}
			} catch (Exception ex) {
				log.error("Error obtenit l'event d'error de l'enviament " + enviament.getId(), ex);
			}
		}
		estatDto.setNotificaCertificacioArxiuNom(
				calcularNomArxiuCertificacio(enviament));
	}

	private String calcularNomArxiuCertificacio(NotificacioEnviamentEntity enviament) {
		return "certificacio_" + enviament.getNotificaIdentificador() + ".pdf";
	}

	public boolean validarIdCsv (String idCsv) {
		return idCsv.length() >= getMidaMinIdCsv() ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public boolean validarFormatCsv(String csv) {
		return csv.matches("^([0-9a-f]{64})$") || csv.matches("^([0-9a-zA-Z_]+)$");
	}

	@Override
	@Transactional(timeout = 1800)
	public void actualitzarReferencies() {

		try {
			List<Long> ids = notificacioRepository.findIdsSenseReferencia();
			int size = ids != null ? ids.size() : 0;

			// Obtenim el xifrador
			Cipher cipher = Cipher.getInstance("RC4");
			SecretKeySpec rc4Key = new SecretKeySpec(configHelper.getConfig("es.caib.notib.notifica.clau.xifrat.ids").getBytes(),"RC4");
			cipher.init(Cipher.ENCRYPT_MODE, rc4Key);

			log.info("Actualitzant not_notificacions");
			for (int foo = 0; foo < size; foo++) {
				Long notId = ids.get(foo);
				String referencia = new String(Base64.encodeBase64(cipher.doFinal(longToBytes(notId.longValue()))));
				notificacioRepository.updateReferencia(notId, referencia);
			}

			log.info("Actualitzant not_notificacio_env");
			ids = enviamentRepository.findIdsSenseReferencia();
			size = ids != null ? ids.size() : 0;
			for (int foo = 0; foo < size; foo++) {
				Long id = ids.get(foo);
				String referencia = new String(Base64.encodeBase64(cipher.doFinal(longToBytes(id.longValue()))));
				enviamentRepository.updateReferencia(id, referencia);
			}
			//Taules auxiliar de notificacions
			notificacioTableViewRepository.updateReferenciesNules();
			notificacioAuditRepository.updateReferenciesNules();
			//Taules auxiliar d'enviament
			notificacioEnviamentAuditRepository.updateReferenciesNules();
			enviamentTableRepository.updateReferenciesNules();
			columnesRepository.refNotUpdateNulls();
		} catch (Exception ex) {
			log.error("Error actualitzant les referencies", ex);
		}
	}

	private byte[] longToBytes(long l) {
		byte[] result = new byte[Long.SIZE / Byte.SIZE];
		for (int i = 7; i >= 0; i--) {
			result[i] = (byte)(l & 0xFF);
			l >>= 8;
		}
		return result;
	}

	public DocumentDto consultaDocumentIMetadades(String identificador, Boolean esUuid) {
		
		Document documentArxiu = null;
		try {
			documentArxiu = pluginHelper.arxiuDocumentConsultar(identificador, null, true, esUuid);
		} catch (Exception ex){
			log.debug("S'ha produit un error obtenent els detalls del document con identificador: " + identificador, ex);
			return null;
			
		}
		var documentDto = new DocumentDto();
		if (documentArxiu != null) {
			documentDto.setCsv(identificador);
			if (documentArxiu.getMetadades() != null) {
				documentDto.setOrigen(OrigenEnum.valorAsEnum(documentArxiu.getMetadades().getOrigen().ordinal()));
				documentDto.setValidesa(ValidesaEnum.valorAsEnum(pluginHelper.estatElaboracioToValidesa(documentArxiu.getMetadades().getEstatElaboracio())));
				documentDto.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(documentArxiu.getMetadades().getTipusDocumental().toString()));
				documentDto.setModoFirma(pluginHelper.getModeFirma(documentArxiu, documentArxiu.getContingut().getArxiuNom()) == 1 ? Boolean.TRUE : Boolean.FALSE);
			}
		}
		return documentDto;
	}
}