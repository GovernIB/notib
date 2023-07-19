/**
 * 
 */
package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.helper.*;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioCertificacioDto.TipusActInfo;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDataDto;
import es.caib.notib.logic.intf.dto.notificacio.Enviament;
import es.caib.notib.logic.intf.dto.notificacio.NotTableUpdate;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.persist.entity.CallbackEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.NotificacioTableEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import es.caib.notib.persist.objectes.FiltreNotificacio;
import es.caib.notib.persist.repository.CallbackRepository;
import es.caib.notib.persist.repository.ColumnesRepository;
import es.caib.notib.persist.repository.DocumentRepository;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.PersonaRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.persist.repository.ProcedimentRepository;
import es.caib.notib.persist.repository.auditoria.NotificacioAuditRepository;
import es.caib.notib.persist.repository.auditoria.NotificacioEnviamentAuditRepository;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.plugins.arxiu.api.Document;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
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
	private EntitatRepository entitatRepository;
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
	private EmailNotificacioHelper emailNotificacioHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private RegistreNotificaHelper registreNotificaHelper;
	@Autowired
	private OrganigramaHelper organigramaHelper;
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
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private ProcSerHelper procedimentHelper;
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
	private CallbackRepository callbackRepository;

	private static final String DELETE = "NotificacioServiceImpl.delete";
	private static final String UPDATE = "NotificacioServiceImpl.update";
	private static final String ERROR_DIR3 = "Error recuperant les provincies de DIR3CAIB: ";

	protected static Map<String, ProgresActualitzacioCertificacioDto> progresActualitzacioExpirades = new HashMap<>();
	

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
			return conversioTipusHelper.convertir(notificacioEntity, Notificacio.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void delete(Long entitatId, Long notificacioId) throws NotFoundException {

		var timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(entitatId,false,true,true,false);
			log.debug("Esborrant la notificació (notificacioId=" + notificacioId + ")");
			var notificacio = notificacioRepository.findById(notificacioId).orElse(null);
			if (notificacio == null) {
				throw new NotFoundException(notificacioId, NotificacioEntity.class, "No s'ha trobat cap notificació amb l'id especificat");
			}
			var enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacioId);
//			### Esborrar la notificació
			if (enviamentsPendents == null || enviamentsPendents.isEmpty()) {
				throw new ValidationException("Aquesta notificació està enviada i no es pot esborrar");
			}
			// esborram tots els seus events
			notificacioEventRepository.deleteByNotificacio(notificacio);

//				## El titular s'ha d'esborrar de forma individual
			for (var enviament : notificacio.getEnviaments()) {
				var titular = enviament.getTitular();
				if (HibernateHelper.isProxy(titular)) {
					titular = HibernateHelper.deproxy(titular);
				}
				enviamentTableRepository.deleteById(enviament.getId());
				notificacioEventRepository.deleteByEnviament(enviament);
				notificacioEnviamentRepository.delete(enviament);
				notificacioEnviamentRepository.flush();
				auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.DELETE, DELETE);
				personaRepository.delete(titular);
			}
			notificacioTableHelper.eliminarRegistre(notificacio);
			notificacioRepository.delete(notificacio);
			notificacioRepository.flush();
			auditHelper.auditaNotificacio(notificacio, AuditService.TipusOperacio.DELETE, DELETE);
			log.debug("La notificació s'ha esborrat correctament (notificacioId=" + notificacioId + ")");
		} finally {
			metricsHelper.fiMetrica(timer);
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
				// TODO VEURE PERQUE S'UTILITZAVA EL codiPostalNorm
//				if (enviament.getEntregaPostal() != null && Strings.isNullOrEmpty(enviament.getEntregaPostal().getCodiPostal())) {
//						enviament.getEntregaPostal().setCodiPostal(enviament.getEntregaPostal().getCodiPostalNorm());
//
//				}
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

//			### Realitzar el procés de registre i notific@
			if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(pluginHelper.getNotibTipusComunicacioDefecte())) {
				synchronized(SemaforNotificacio.agafar(notificacioEntity.getId())) {
					boolean notificar = registreNotificaHelper.realitzarProcesRegistrar(notificacioEntity);
					if (notificar) {
						notificaHelper.notificacioEnviar(notificacioEntity.getId());
					}
				}
				SemaforNotificacio.alliberar(notificacioEntity.getId());
			}
			notificacioTableHelper.actualitzarRegistre(notificacioEntity);
			auditHelper.auditaNotificacio(notificacioEntity, AuditService.TipusOperacio.UPDATE, UPDATE);
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
			pluginHelper.addOficinaAndLlibreRegistre(notificacio);
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
			if(notificacio == null) {
				return null;
			}
			var enviamentsPendentsNotifica = notificacioEnviamentRepository.findEnviamentsPendentsNotificaByNotificacio(notificacio);
			notificacio.setHasEnviamentsPendents(enviamentsPendentsNotifica != null && !enviamentsPendentsNotifica.isEmpty());
			// Emplena els atributs registreLlibreNom i registreOficinaNom
			pluginHelper.addOficinaAndLlibreRegistre(notificacio);
			var dto = conversioTipusHelper.convertir(notificacio, NotificacioInfoDto.class);
			//CALLBACKS
			var pendents = callbackRepository.findByNotificacioIdAndEstatOrderByDataDesc(notificacio.getId(), CallbackEstatEnumDto.PENDENT);
			dto.setEventsCallbackPendent(notificacio.isTipusUsuariAplicacio() && pendents != null && !pendents.isEmpty());
			var df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			var data = pendents != null && !pendents.isEmpty() && pendents.get(0).getData() != null ? df.format(pendents.get(0).getData()) : null;
			dto.setDataCallbackPendent(data);
			int callbackFiReintents = 0;
			CallbackEntity callback;
			NotificacioEventEntity eventNotMovil;
			for (var env : dto.getEnviaments()) {
				eventNotMovil = notificacioEventRepository.findLastApiCarpetaByEnviamentId(env.getId());
				if (eventNotMovil != null && eventNotMovil.isError()) {
					dto.getNotificacionsMovilErrorDesc().add(eventNotMovil.getErrorDescripcio());
					env.setNotificacioMovilErrorDesc(eventNotMovil.getErrorDescripcio());
				}
				callback = callbackRepository.findByEnviamentIdAndEstat(env.getId(), CallbackEstatEnumDto.ERROR);
				if (callback == null) {
					continue;
				}
				env.setCallbackFiReintents(true);
				env.setCallbackFiReintentsDesc(messageHelper.getMessage("callback.fi.reintents"));
				callbackFiReintents++;

			}
			if (dto.getNotificacionsMovilErrorDesc().size() > 1) {
				List<String> foo = new ArrayList<>();
				foo.add(messageHelper.getMessage("api.carpeta.send.notificacio.movil.error"));
				dto.setNotificacionsMovilErrorDesc(foo);
			}
//			int callbackFiReintents = notificacioEventRepository.countEventCallbackAmbFiReintentsByNotificacioId(notificacio.getId());
			if (callbackFiReintents > 0) {
				dto.setCallbackFiReintents(true);
				dto.setCallbackFiReintentsDesc(messageHelper.getMessage("callback.fi.reintents"));
			}
			// Emplena dades del procediment
			var procedimentEntity = notificacio.getProcediment();
			if (procedimentEntity != null && procedimentEntity.isEntregaCieActivaAlgunNivell()) {
				EntregaCieEntity entregaCieEntity = procedimentEntity.getEntregaCieEfectiva();
				dto.setOperadorPostal(conversioTipusHelper.convertir(entregaCieEntity.getOperadorPostal(), OperadorPostalDataDto.class));
				dto.setCie(conversioTipusHelper.convertir(entregaCieEntity.getCie(), CieDataDto.class));
			}

			var lastErrorEvent = notificacioEventRepository.findEventsAmbFiReintentsByNotificacioId(notificacio.getId());
			if (lastErrorEvent != null && !lastErrorEvent.isEmpty()) {
				String msg = "";
				String tipus = "";
				StringBuilder m = new StringBuilder();
				int env = 1;
				for (var event : lastErrorEvent) {
					msg = messageHelper.getMessage("notificacio.event.fi.reintents");
					tipus = messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + event.getTipus());
					m.append("Env ").append(env).append(": ").append(msg).append(" -> ").append(tipus).append("\n");
					env++;
				}
				dto.setFiReintentsDesc(m.toString());
				dto.setFiReintents(true);
				dto.setNoticaErrorEventTipus(lastErrorEvent.get(0).getTipus());
				// Obtenir error dels events
				dto.setNotificaErrorTipus(getErrorTipus(lastErrorEvent.get(0)));
			}
			dto.setEnviadaDate(getEnviadaDate(notificacio));

			// TODO RECUPERAR INFORMACIÓ DIRECTAMENT DE LES ENTITATS
			var notificacioTableEntity = notificacioTableViewRepository.findById(id).orElse(null);
			if (notificacioTableEntity == null) {
				return dto;
			}
			var e = notificacioEventRepository.findLastErrorEventByNotificacioId(id);
//			dto.notificaError(e != null);
			dto.setNotificaErrorData(notificacioTableEntity.getNotificaErrorData());
			dto.setNotificaErrorDescripcio(notificacioTableEntity.getNotificaErrorDescripcio());
			return dto;
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

	private Date getEnviadaDate(NotificacioEntity notificacio) {

		try {
			if (notificacio.getEnviaments() == null || notificacio.getEnviaments().isEmpty()) {
				return null;
			}

			Date dataEnviament = null;
			for (NotificacioEnviamentEntity env : notificacio.getEnviaments()) {
				if (env.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)
					&& (!notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT) || !notificacio.getEstat().equals(NotificacioEstatEnumDto.ENVIANT))) {

					dataEnviament = env.getRegistreData();
				}

				if (!env.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)
					&& (!notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT) || !notificacio.getEstat().equals(NotificacioEstatEnumDto.REGISTRADA)
						|| !notificacio.getEstat().equals(NotificacioEstatEnumDto.ENVIANT))) {

					dataEnviament = notificacio.getNotificaEnviamentNotificaData();
				}
				if (dataEnviament != null) {
					break;
				}
			}
			return dataEnviament;
		} catch (Exception ex) {
			log.error("Error actualitzant la data d'enviament a la taula del llistat", ex);
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<NotificacioTableItemDto> findAmbFiltrePaginat(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		log.info("Consulta taula de remeses ...");
		var timer = metricsHelper.iniciMetrica();
		try {
			var isUsuari = RolEnumDto.tothom.equals(rol);
			var isUsuariEntitat = RolEnumDto.NOT_ADMIN.equals(rol);
			var isSuperAdmin = RolEnumDto.NOT_SUPER.equals(rol);
			var isAdminOrgan = RolEnumDto.NOT_ADMIN_ORGAN.equals(rol);
			var entitatActual = entityComprovarHelper.comprovarEntitat(entitatId,false, isUsuariEntitat,false);
			Page<NotificacioTableEntity> notificacions = null;
			var pageable = notificacioListHelper.getMappeigPropietats(paginacioParams);
			List<String> codisProcedimentsDisponibles = new ArrayList<>();
			List<String> codisOrgansGestorsDisponibles = new ArrayList<>();
			List<String> codisProcedimentsOrgans = new ArrayList<>();

			if (isUsuari && entitatActual != null) {
				var auth = SecurityContextHolder.getContext().getAuthentication();
				entityComprovarHelper.getPermissionsFromName(PermisEnum.CONSULTA);
				// Procediments accessibles per qualsevol òrgan gestor
				codisProcedimentsDisponibles = procedimentHelper.findCodiProcedimentsWithPermis(auth, entitatActual, PermisEnum.CONSULTA);
				// Òrgans gestors dels que es poden consultar tots els procediments que no requereixen permís directe
				codisOrgansGestorsDisponibles = organGestorHelper.findCodiOrgansGestorsWithPermis(auth, entitatActual, PermisEnum.CONSULTA);
				// Procediments comuns que es poden consultar per a òrgans gestors concrets
				codisProcedimentsOrgans = permisosService.getProcedimentsOrgansAmbPermis(entitatActual.getId(), auth.getName(), PermisEnum.CONSULTA);
			} else if (isAdminOrgan && entitatActual != null) {
				codisProcedimentsDisponibles = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi);
			}

			var esProcedimentsCodisNotibNull = (codisProcedimentsDisponibles == null || codisProcedimentsDisponibles.isEmpty());
			var esOrgansGestorsCodisNotibNull = (codisOrgansGestorsDisponibles == null || codisOrgansGestorsDisponibles.isEmpty());
			var esProcedimentOrgansAmbPermisNull = (codisProcedimentsOrgans == null || codisProcedimentsOrgans.isEmpty());
			var f = notificacioListHelper.getFiltre(filtre);
			var organs = isAdminOrgan ? organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi) : null;
			var entitatsActives = isSuperAdmin ? entitatRepository.findByActiva(true) : null;
			var entitatFiltre = isUsuariEntitat || isUsuari ? entitatId : f.getEntitatId().getField();
			var not = FiltreNotificacio.builder().entitatIdNull(entitatFiltre == null).entitatId(entitatActual.getId())
					.enviamentTipusNull(f.getEnviamentTipus().isNull())
					.enviamentTipus(f.getEnviamentTipus().getField())
					.concepteNull(f.getConcepte().isNull())
					.concepte(f.getConcepte().getField())
					.estatNull(f.getEstat().isNull())
					.estatMask(f.getEstat().isNull() ? 0 : f.getEstat().getField().getMask())
					.dataIniciNull(f.getDataInici().isNull())
					.dataInici(f.getDataInici().getField())
					.dataFiNull(f.getDataFi().isNull())
					.dataFi(f.getDataFi().getField())
					.titularNull(f.getTitular().isNull())
					.titular(f.getTitular().isNull() ? "" : f.getTitular().getField())
					.organCodiNull(f.getOrganGestor().isNull())
					.organCodi(f.getOrganGestor().isNull() ? "" : f.getOrganGestor().getField().getCodi())
					.procedimentNull(f.getProcediment().isNull())
					.procedimentCodi(f.getProcediment().isNull() ? "" : f.getProcediment().getField().getCodi())
					.tipusUsuariNull(f.getTipusUsuari().isNull())
					.tipusUsuari(f.getTipusUsuari().getField())
					.numExpedientNull(f.getNumExpedient().isNull())
					.numExpedient(f.getNumExpedient().getField())
					.creadaPerNull(f.getCreadaPer().isNull())
					.creadaPer(f.getCreadaPer().getField())
					.identificadorNull(f.getIdentificador().isNull())
					.identificador(f.getIdentificador().getField())
					.nomesAmbErrors(f.getNomesAmbErrors().getField())
					.nomesSenseErrors(f.getNomesSenseErrors().getField())
					.referenciaNull(f.getReferencia().isNull())
					.referencia(f.getReferencia().getField())
					.isUsuari(isUsuari)
					.procedimentsCodisNotibNull(esProcedimentsCodisNotibNull)
					.procedimentsCodisNotib(esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles)
					.grupsProcedimentCodisNotib(aplicacioService.findRolsUsuariActual())
					.organsGestorsCodisNotibNull(esOrgansGestorsCodisNotibNull)
					.organsGestorsCodisNotib(esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles)
					.procedimentOrgansIdsNotibNull(esProcedimentOrgansAmbPermisNull)
					.procedimentOrgansIdsNotib(esProcedimentOrgansAmbPermisNull ?  null : codisProcedimentsOrgans)
					.usuariCodi(usuariCodi)
					.isSuperAdmin(isSuperAdmin)
					.entitatsActives(entitatsActives)
					.isAdminOrgan(isAdminOrgan)
					.organs(organs)
					.notMassivaIdNull(filtre.getNotMassivaId() == null)
					.notMassivaId(filtre.getNotMassivaId()).build();
			notificacions = notificacioTableViewRepository.findAmbFiltre(not, pageable);
//			if (filtre == null || filtre.isEmpty()) {
//
//				//Consulta les notificacions sobre les quals té permis l'usuari actual
//				if (isUsuari) {
//					long start = System.nanoTime();

//
//					notificacions = notificacioTableViewRepository.findByProcedimentCodiNotibAndGrupsCodiNotibAndEntitat(
//							esProcedimentsCodisNotibNull,
//							esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
//							aplicacioService.findRolsUsuariActual(),
//							esOrgansGestorsCodisNotibNull,
//							esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
//							esProcedimentOrgansAmbPermisNull,
//							esProcedimentOrgansAmbPermisNull ?  null : codisProcedimentsOrgans,
//							entitatActual,
//							usuariCodi,
//							pageable);
//					var elapsedTime = System.nanoTime() - start;
//					log.info(">>>>>>>>>>>>> Notificacions sense filtre: "  + elapsedTime);
//				//Consulta les notificacions de l'entitat acutal
//				} else if (isUsuariEntitat) {
//					notificacions = notificacioTableViewRepository.findByEntitatActual(entitatActual, pageable);
//				//Consulta totes les notificacions de les entitats actives
//				} else if (isSuperAdmin) {
//					var entitatsActiva = entitatRepository.findByActiva(true);
//					notificacions = notificacioTableViewRepository.findByEntitatActiva(entitatsActiva, pageable);
//				} else if (isAdminOrgan) {
//					var organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi);
//					notificacions = notificacioTableViewRepository.findByProcedimentCodiNotibAndEntitat(esProcedimentsCodisNotibNull,
//							esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles, entitatActual, organs, pageable);
//				}
//			} else {
//				var filtreNetejat = notificacioListHelper.getFiltre(filtre);
//				if (isUsuari) {
//					notificacions = notificacioTableViewRepository.findAmbFiltreAndProcedimentCodiNotibAndGrupsCodiNotib(
//							entitatActual,
//							filtreNetejat.getEntitatId().isNull(),
//							filtreNetejat.getEntitatId().getField(),
//							esProcedimentsCodisNotibNull,
//							esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
//							aplicacioService.findRolsUsuariActual(),
//							esOrgansGestorsCodisNotibNull,
//							esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
//							esProcedimentOrgansAmbPermisNull,
//							esProcedimentOrgansAmbPermisNull ?  null : codisProcedimentsOrgans,
//							filtreNetejat.getEnviamentTipus().isNull(),
//							filtreNetejat.getEnviamentTipus().getField(),
//							filtreNetejat.getConcepte().isNull(),
//							filtreNetejat.getConcepte().isNull() ? "" : filtreNetejat.getConcepte().getField(),
//							filtreNetejat.getEstat().isNull(),
//							filtreNetejat.getEstat().isNull() ? 0 : filtreNetejat.getEstat().getField().getMask(),
////							!filtreNetejat.getEstat().isNull() ?
////									EnviamentEstat.valueOf(filtreNetejat.getEstat().getField().toString()) : null,
//							filtreNetejat.getDataInici().isNull(),
//							filtreNetejat.getDataInici().getField(),
//							filtreNetejat.getDataFi().isNull(),
//							filtreNetejat.getDataFi().getField(),
//							filtreNetejat.getTitular().isNull(),
//							filtreNetejat.getTitular().isNull() ? "" : filtreNetejat.getTitular().getField(),
//							filtreNetejat.getOrganGestor().isNull(),
//							filtreNetejat.getOrganGestor().isNull() ? "" : filtreNetejat.getOrganGestor().getField().getCodi(),
//							filtreNetejat.getProcediment().isNull(),
//							filtreNetejat.getProcediment().isNull() ? "" : filtreNetejat.getProcediment().getField().getCodi(),
//							filtreNetejat.getTipusUsuari().isNull(),
//							filtreNetejat.getTipusUsuari().getField(),
//							filtreNetejat.getNumExpedient().isNull(),
//							filtreNetejat.getNumExpedient().getField(),
//							filtreNetejat.getCreadaPer().isNull(),
//							filtreNetejat.getCreadaPer().getField(),
//							filtreNetejat.getIdentificador().isNull(),
//							filtreNetejat.getIdentificador().getField(),
//							usuariCodi,
//							filtreNetejat.getNomesAmbErrors().getField(),
//							filtreNetejat.getNomesSenseErrors().getField(),
//							filtreNetejat.getReferencia().isNull(),
//							filtreNetejat.getReferencia().getField(),
//							pageable);
//				} else if (isUsuariEntitat || isSuperAdmin) {
//					var entitatFiltre = isUsuariEntitat ? entitatId :filtreNetejat.getEntitatId().getField();
//					notificacions = notificacioTableViewRepository.findAmbFiltre(
//							entitatFiltre == null,
//							entitatFiltre,
//							filtreNetejat.getEnviamentTipus().isNull(),
//							filtreNetejat.getEnviamentTipus().getField(),
//							filtreNetejat.getConcepte().isNull(),
//							filtreNetejat.getConcepte().getField(),
//							filtreNetejat.getEstat().isNull(),
//							filtreNetejat.getEstat().isNull() ? 0 : filtreNetejat.getEstat().getField().getMask(),
////							!filtreNetejat.getEstat().isNull() ?
////									EnviamentEstat.valueOf(filtreNetejat.getEstat().getField().toString()) : null,
//							filtreNetejat.getDataInici().isNull(),
//							filtreNetejat.getDataInici().getField(),
//							filtreNetejat.getDataFi().isNull(),
//							filtreNetejat.getDataFi().getField(),
//							filtreNetejat.getTitular().isNull(),
//							filtreNetejat.getTitular().isNull() ? "" : filtreNetejat.getTitular().getField(),
//							filtreNetejat.getOrganGestor().isNull(),
//							filtreNetejat.getOrganGestor().isNull() ? "" : filtreNetejat.getOrganGestor().getField().getCodi(),
//							filtreNetejat.getProcediment().isNull(),
//							filtreNetejat.getProcediment().isNull() ? "" : filtreNetejat.getProcediment().getField().getCodi(),
//							filtreNetejat.getTipusUsuari().isNull(),
//							filtreNetejat.getTipusUsuari().getField(),
//							filtreNetejat.getNumExpedient().isNull(),
//							filtreNetejat.getNumExpedient().getField(),
//							filtreNetejat.getCreadaPer().isNull(),
//							filtreNetejat.getCreadaPer().getField(),
//							filtreNetejat.getIdentificador().isNull(),
//							filtreNetejat.getIdentificador().getField(),
//							filtreNetejat.getNomesAmbErrors().getField(),
//							filtreNetejat.getNomesSenseErrors().getField(),
//							filtreNetejat.getReferencia().isNull(),
//							filtreNetejat.getReferencia().getField(),
//							pageable);
//
//				} else if (isAdminOrgan) {
//					var organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi);
//					notificacions = notificacioTableViewRepository.findAmbFiltreAndProcedimentCodiNotib(
//							entitatActual,
//							filtreNetejat.getEntitatId().isNull(),
//							filtreNetejat.getEntitatId().getField(),
//							esProcedimentsCodisNotibNull,
//							esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
//							filtreNetejat.getEnviamentTipus().isNull(),
//							filtreNetejat.getEnviamentTipus().getField(),
//							filtreNetejat.getConcepte().isNull(),
//							filtreNetejat.getConcepte().getField(),
//							filtreNetejat.getEstat().isNull(),
//							filtreNetejat.getEstat().isNull() ? 0 : filtreNetejat.getEstat().getField().getMask(),
////							!filtreNetejat.getEstat().isNull() ?
////									EnviamentEstat.valueOf(filtreNetejat.getEstat().getField().toString()) : null,
//							filtreNetejat.getDataInici().isNull(),
//							filtreNetejat.getDataInici().getField(),
//							filtreNetejat.getDataFi().isNull(),
//							filtreNetejat.getDataFi().getField(),
//							filtreNetejat.getTitular().isNull(),
//							filtreNetejat.getTitular().isNull() ? "" : filtreNetejat.getTitular().getField(),
//							filtreNetejat.getOrganGestor().isNull(),
//							filtreNetejat.getOrganGestor().isNull() ? "" : filtreNetejat.getOrganGestor().getField().getCodi(),
//							filtreNetejat.getProcediment().isNull(),
//							filtreNetejat.getProcediment().isNull() ? "" : filtreNetejat.getProcediment().getField().getCodi(),
//							filtreNetejat.getTipusUsuari().isNull(),
//							filtreNetejat.getTipusUsuari().getField(),
//							filtreNetejat.getNumExpedient().isNull(),
//							filtreNetejat.getNumExpedient().getField(),
//							filtreNetejat.getCreadaPer().isNull(),
//							filtreNetejat.getCreadaPer().getField(),
//							filtreNetejat.getIdentificador().isNull(),
//							filtreNetejat.getIdentificador().getField(),
//							organs,
//							filtreNetejat.getNomesSenseErrors().getField(),
//							filtreNetejat.getReferencia().isNull(),
//							filtreNetejat.getReferencia().getField(),
//							pageable);
//				}
//			}
 			return notificacioListHelper.complementaNotificacions(entitatActual, usuariCodi, notificacions);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsAmbFiltre(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre) {

		var isUsuari = RolEnumDto.tothom.equals(rol);
		var isUsuariEntitat = RolEnumDto.NOT_ADMIN.equals(rol);
		var isSuperAdmin = RolEnumDto.NOT_SUPER.equals(rol);
		var isAdminOrgan = RolEnumDto.NOT_ADMIN_ORGAN.equals(rol);
		var entitatActual = entityComprovarHelper.comprovarEntitat(entitatId,false, isUsuariEntitat,false);
		List<String> codisProcedimentsDisponibles = new ArrayList<>();
		List<String> codisOrgansGestorsDisponibles = new ArrayList<>();
		List<String> codisProcedimentsOrgans = new ArrayList<>();

		if (isUsuari && entitatActual != null) {
			var auth = SecurityContextHolder.getContext().getAuthentication();
			entityComprovarHelper.getPermissionsFromName(PermisEnum.CONSULTA);
			// Procediments accessibles per qualsevol òrgan gestor
			codisProcedimentsDisponibles = procedimentHelper.findCodiProcedimentsWithPermis(auth, entitatActual, PermisEnum.CONSULTA);
			// Òrgans gestors dels que es poden consultar tots els procediments que no requereixen permís directe
			codisOrgansGestorsDisponibles = organGestorHelper.findCodiOrgansGestorsWithPermis(auth, entitatActual, PermisEnum.CONSULTA);
			// Procediments comuns que es poden consultar per a òrgans gestors concrets
			codisProcedimentsOrgans = permisosService.getProcedimentsOrgansAmbPermis(entitatActual.getId(), auth.getName(), PermisEnum.CONSULTA);
		} else if (isAdminOrgan && entitatActual != null) {
			codisProcedimentsDisponibles = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi);
		}
		var esProcedimentsCodisNotibNull = (codisProcedimentsDisponibles == null || codisProcedimentsDisponibles.isEmpty());
		var esOrgansGestorsCodisNotibNull = (codisOrgansGestorsDisponibles == null || codisOrgansGestorsDisponibles.isEmpty());
		var esProcedimentOrgansAmbPermisNull = (codisProcedimentsOrgans == null || codisProcedimentsOrgans.isEmpty());
		var entitatsActives = isSuperAdmin ? entitatRepository.findByActiva(true) : null;
		var organs = isAdminOrgan ? organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi) : null;
		var f = notificacioListHelper.getFiltre(filtre);
		var entitatFiltre = isUsuariEntitat || isUsuari ? entitatId : f.getEntitatId().getField();
		return notificacioTableViewRepository.findIdsAmbFiltre(
				entitatFiltre == null,
				entitatFiltre,
				f.getEnviamentTipus().isNull(),
				f.getEnviamentTipus().getField(),
				f.getConcepte().isNull(),
				f.getConcepte().getField(),
				f.getEstat().isNull(),
				f.getEstat().isNull() ? 0 : f.getEstat().getField().getMask(),
				f.getDataInici().isNull(),
				f.getDataInici().getField(),
				f.getDataFi().isNull(),
				f.getDataFi().getField(),
				f.getTitular().isNull(),
				f.getTitular().isNull() ? "" : f.getTitular().getField(),
				f.getOrganGestor().isNull(),
				f.getOrganGestor().isNull() ? "" : f.getOrganGestor().getField().getCodi(),
				f.getProcediment().isNull(),
				f.getProcediment().isNull() ? "" : f.getProcediment().getField().getCodi(),
				f.getTipusUsuari().isNull(),
				f.getTipusUsuari().getField(),
				f.getNumExpedient().isNull(),
				f.getNumExpedient().getField(),
				f.getCreadaPer().isNull(),
				f.getCreadaPer().getField(),
				f.getIdentificador().isNull(),
				f.getIdentificador().getField(),
				f.getNomesAmbErrors().getField(),
				f.getNomesSenseErrors().getField(),
				f.getReferencia().isNull(),
				f.getReferencia().getField(),
				isUsuari,
				esProcedimentsCodisNotibNull,
				esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
				aplicacioService.findRolsUsuariActual(),
				esOrgansGestorsCodisNotibNull,
				esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
				esProcedimentOrgansAmbPermisNull,
				esProcedimentOrgansAmbPermisNull ?  null : codisProcedimentsOrgans,
				usuariCodi,
				isSuperAdmin,
				entitatsActives,
				isAdminOrgan,
				organs,
				filtre.getNotMassivaId() == null,
				filtre.getNotMassivaId());
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
					paginacioHelper.toPaginaDto(page, NotificacioDto.class) : paginacioHelper.getPaginaDtoBuida(NotificacioDto.class);
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
				codiValorPais = pluginHelper.llistarPaisos();
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
				codiValor = pluginHelper.llistarProvincies();
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
	@Transactional(readOnly = true)
	public ArxiuDto getDocumentArxiu(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var nomDocumentDefault = "document";
			var entity = notificacioRepository.findById(notificacioId).orElseThrow();
			var document = entity.getDocument();
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
			// #779: Obtenim la certificació de forma automàtica
			if (enviament.getNotificaCertificacioArxiuId() == null) {
				enviament = notificaHelper.enviamentRefrescarEstat(enviamentId);
			}
			if (enviament.getNotificaCertificacioArxiuId() == null) {
				throw new RuntimeException("No s'ha trobat la certificació de l'enviament amb id: " + enviamentId);
			}
			var output = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(enviament.getNotificaCertificacioArxiuId(), PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, output);
			return new ArxiuDto(calcularNomArxiuCertificacio(enviament), enviament.getNotificaCertificacioMime(), output.toByteArray(), output.size());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public boolean enviar(Long notificacioId) {

		var timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Intentant enviament de la notificació pendent (notificacioId=" + notificacioId + ")");
			var notificacio = notificaHelper.notificacioEnviar(notificacioId);
			return (notificacio != null && NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat()));
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public List<RegistreIdDto> registrarNotificar(Long notificacioId) throws RegistreNotificaException {

		var timer = metricsHelper.iniciMetrica();
		try {
			return notificacioHelper.registrarNotificar(notificacioId);
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
			notificaHelper.enviamentRefrescarEstat(enviament.getId());
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
			NotificacioEntity notificacioEntity = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
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
				resposta = emailNotificacioHelper.prepararEnvioEmailNotificacio(notificacioEntity);
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
				event = enviament.getNotificacioErrorEvent();
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
			// 1. Tots els enviaments a Notifica
			if (enviamentsSenseNifNoEnviats.isEmpty()) {
				notificaHelper.notificacioEnviar(notificacio.getId());
				return;
			}
			// 2. Tots els enviaments per email
			if (notificacio.getEnviamentsNoEnviats().size() <= enviamentsSenseNifNoEnviats.size()) {
				emailNotificacioSenseNifHelper.notificacioEnviarEmail(enviamentsSenseNifNoEnviats, true);
				return;
			}
			/* 3. Una part dels enviaments a Notifica i l'altre via email */
			notificaHelper.notificacioEnviar(notificacio.getId(), true);
			// Fa falta enviar els restants per email
			emailNotificacioSenseNifHelper.notificacioEnviarEmail(enviamentsSenseNifNoEnviats, false);
		} finally {
			metricsHelper.fiMetrica(timer);
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
	public boolean enviamentRefrescarEstatSir(Long enviamentId) {

		var timer = metricsHelper.iniciMetrica();
		var totBe = false;
		try {
			registreHelper.enviamentRefrescarEstatRegistre(enviamentId);
			var enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
			totBe = enviament.getSirConsultaIntent() == 0;
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
					paginacioHelper.toPaginaDto(page, NotificacioDto.class): paginacioHelper.getPaginaDtoBuida(NotificacioDto.class);
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
				notificacioEnviar(notificacioId);
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
			// TODO VEURE PERQUE EL MÈTODE UPDATE DEL REPOSITORY NO FUNCIONA
			var events = notificacioEventRepository.findEventsAmbFiReintentsByNotificacioId(notificacioId);
			for (var e : events) {
				e.setFiReintents(false);
			}
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
			return pluginHelper.detectSignedAttachedUsingValidateSignaturePlugin(contingut, nom, contentType);
		} finally {
			metricsHelper.fiMetrica(timer);
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
				if (enviament.getNotificacioErrorEvent() != null && enviament.getNotificacioErrorEvent().getId() != null) {
					event = notificacioEventRepository.getOne(enviament.getNotificacioErrorEvent().getId());
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
		
		Document documentArxiu = new Document();
		
		try {
			if (esUuid)
				documentArxiu = pluginHelper.arxiuDocumentConsultar(identificador, null, true, true);
			else
				documentArxiu = pluginHelper.arxiuDocumentConsultar(identificador, null, true, false);
		} catch (Exception ex){
			log.debug("S'ha produit un error obtenent els detalls del document con identificador: " + identificador, ex);
			return null;
			
		}
		
		DocumentDto documentDto = new DocumentDto();
		
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