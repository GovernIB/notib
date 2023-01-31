/**
 * 
 */
package es.caib.notib.logic.service;

import com.codahale.metrics.Timer;
import es.caib.notib.client.domini.Enviament;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.Persona;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioCertificacioDto.TipusActInfo;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDataDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDatabaseDto;
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
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.persist.entity.*;
import es.caib.notib.persist.entity.auditoria.NotificacioAudit;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import es.caib.notib.logic.helper.*;
import es.caib.notib.persist.repository.*;
import es.caib.notib.persist.repository.auditoria.NotificacioAuditRepository;
import es.caib.notib.persist.repository.auditoria.NotificacioEnviamentAuditRepository;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.plugins.arxiu.api.Document;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private AuditNotificacioHelper auditNotificacioHelper;
	@Autowired
	private AuditEnviamentHelper auditEnviamentHelper;
//	@Autowired
//	private AplicacioService aplicacioService;
	@Lazy
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
	private ProcessosInicialsRepository processosInicialsRepository;

	public static Map<String, ProgresActualitzacioCertificacioDto> progresActualitzacioExpirades = new HashMap<>();

	@Transactional(rollbackFor=Exception.class)
	@Override
	public NotificacioDatabaseDto create(Long entitatId, NotificacioDatabaseDto notificacio) throws RegistreNotificaException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			NotificacioHelper.NotificacioData notData = notificacioHelper.buildNotificacioData(entitat, notificacio, false);
			// Dades generals de la notificació
			NotificacioEntity notificacioEntity = notificacioHelper.saveNotificacio(notData);
			notificacioHelper.altaEnviamentsWeb(entitat, notificacioEntity, notificacio.getEnviaments());
			return conversioTipusHelper.convertir(notificacioEntity, NotificacioDatabaseDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void delete(Long entitatId, Long notificacioId) throws NotFoundException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(entitatId,false,true,true,false);
			
			log.debug("Esborrant la notificació (notificacioId=" + notificacioId + ")");
			NotificacioEntity notificacio = notificacioRepository.findById(notificacioId)
					.orElseThrow(() -> new NotFoundException(notificacioId, NotificacioEntity.class, "No s'ha trobat cap notificació amb l'id especificat"));
//			if (notificacio == null) {
//				throw new NotFoundException(notificacioId, NotificacioEntity.class, "No s'ha trobat cap notificació amb l'id especificat");
//			}
			List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacioId);
//			### Esborrar la notificació
			if (enviamentsPendents == null || enviamentsPendents.isEmpty()) {
				throw new ValidationException("Aquesta notificació està enviada i no es pot esborrar");
			}
			// esborram tots els seus events
			notificacioEventRepository.deleteByNotificacio(notificacio);

//				## El titular s'ha d'esborrar de forma individual
			for (NotificacioEnviamentEntity enviament : notificacio.getEnviaments()) {
				PersonaEntity titular = enviament.getTitular();
				if (HibernateHelper.isProxy(titular)) {
					titular = HibernateHelper.deproxy(titular);
				}
				auditEnviamentHelper.deleteEnviament(enviament);
				personaRepository.delete(titular);
			}

			auditNotificacioHelper.deleteNotificacio(notificacio);
			log.debug("La notificació s'ha esborrat correctament (notificacioId=" + notificacioId + ")");
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public NotificacioDatabaseDto update(Long entitatId, NotificacioDatabaseDto notificacio, boolean isAdministradorEntitat) throws NotFoundException, RegistreNotificaException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, true, false);
			List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacio.getId());
			if (enviamentsPendents == null || enviamentsPendents.isEmpty()) {
				throw new ValidationException("Aquesta notificació està enviada i no es pot modificar");
			}
			NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacio.getId()).orElseThrow();
			NotificacioHelper.NotificacioData notData = notificacioHelper.buildNotificacioData(entitat, notificacio, false); //!isAdministradorEntitat);
			// Actualitzar notificació existent
			auditNotificacioHelper.updateNotificacio(notificacioEntity, notData);
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
			List<Enviament> enviaments = new ArrayList<Enviament>();
			List<Long> enviamentsIds = new ArrayList<Long>();
			List<Long> destinatarisIds = new ArrayList<Long>();
			List<NotificacioEnviamentEntity> nousEnviaments = new ArrayList<NotificacioEnviamentEntity>();
			for(NotEnviamentDatabaseDto enviament: notificacio.getEnviaments()) {
				if (enviament.getEntregaPostal() != null) {
					if (enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty()) {
						enviament.getEntregaPostal().setCodiPostal(enviament.getEntregaPostal().getCodiPostalNorm());
					}
				}
				if (enviament.getTitular() != null) {
					enviaments.add(conversioTipusHelper.convertir(enviament, Enviament.class));
				}
				if (enviament.getId() != null) { //En cas d'enviaments nous
					enviamentsIds.add(enviament.getId());
				}
			}
			// Creació o edició enviament existent
			for (Enviament enviament: enviaments) {
				ServeiTipusEnumDto serveiTipus = null;
				if (enviament.getServeiTipus() != null) {
					switch (enviament.getServeiTipus()) {
					case NORMAL:
						serveiTipus = ServeiTipusEnumDto.NORMAL;
						break;
					case URGENT:
						serveiTipus = ServeiTipusEnumDto.URGENT;
						break;
					}
				}
				PersonaEntity titular = enviament.getTitular().getId() != null ? personaHelper.update(enviament.getTitular(),  enviament.getTitular().isIncapacitat())
										: personaHelper.create(enviament.getTitular(), enviament.getTitular().isIncapacitat());
				List<PersonaEntity> nousDestinataris = new ArrayList<PersonaEntity>();
//					### Crear o editar destinataris enviament existent
				if (enviament.getDestinataris() != null) {
					for(Persona destinatari: enviament.getDestinataris()) {
						if ((destinatari.getNif() != null && !destinatari.getNif().isEmpty()) || (destinatari.getDir3Codi() != null && !destinatari.getDir3Codi().isEmpty())) {
							if (destinatari.getId() != null) {
								destinatarisIds.add(destinatari.getId());
								personaHelper.update(destinatari, false);
							} else {
								PersonaEntity destinatariEntity = personaHelper.create(destinatari, false);
								nousDestinataris.add(destinatariEntity);
								destinatarisIds.add(destinatariEntity.getId());
							}
						}
					}
				}

//					### Actualitzar les dades d'un enviament existent o crear un de nou
				if (enviament.getId() != null) {
					NotificacioEnviamentEntity enviamentEntity = auditEnviamentHelper.updateEnviament(entitat, notificacioEntity, enviament, serveiTipus, titular);
					enviamentEntity.getDestinataris().addAll(nousDestinataris);
				} else {
					NotificacioEnviamentEntity nouEnviament = auditEnviamentHelper.desaEnviament(entitat, notificacioEntity, enviament, serveiTipus, titular, nousDestinataris);
					nousEnviaments.add(nouEnviament);
					enviamentsIds.add(nouEnviament.getId());
				}
			}
			notificacioEntity.getEnviaments().addAll(nousEnviaments);
//			### Enviaments esborrats
			Set<NotificacioEnviamentEntity> enviamentsDisponibles = new HashSet<NotificacioEnviamentEntity>(notificacioEntity.getEnviaments());
			for (NotificacioEnviamentEntity enviament: enviamentsDisponibles) {
				if (HibernateHelper.isProxy(enviament)) { //en cas d'haver modificat l'enviament
					enviament = HibernateHelper.deproxy(enviament);
				}
				if (! enviamentsIds.contains(enviament.getId())) {
					notificacioEntity.getEnviaments().remove(enviament);
					auditEnviamentHelper.deleteEnviament(enviament);
				}
//				### Destinataris esborrats
				List<PersonaEntity> destinatarisDisponibles = new ArrayList<PersonaEntity>(enviament.getDestinataris());
				for (PersonaEntity destinatari : destinatarisDisponibles) {
					if (HibernateHelper.isProxy(destinatari)) { //en cas d'haver modificat l'interessat
						destinatari = HibernateHelper.deproxy(destinatari);
					}
					if (! destinatarisIds.contains(destinatari.getId())) {
						enviament.getDestinataris().remove(destinatari);
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
			return conversioTipusHelper.convertir(notificacioRepository.getOne(notificacio.getId()), NotificacioDatabaseDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public NotificacioDtoV2 findAmbId(Long id, boolean isAdministrador) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de la notificacio amb id (id=" + id + ")");
			NotificacioEntity notificacio = notificacioRepository.findById(id).orElse(null);
			if(notificacio == null) {
				return null;
			}
			entityComprovarHelper.comprovarPermisos(null, false, false, false);
			List<NotificacioEnviamentEntity> enviamentsPendentsNotifica = notificacioEnviamentRepository.findEnviamentsPendentsNotificaByNotificacio(notificacio);
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

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta de la notificacio amb id (id=" + id + ")");
			NotificacioEntity notificacio = notificacioRepository.findById(id).orElse(null);
			if (notificacio == null) {
				return null;
			}
			List<NotificacioEnviamentEntity> enviamentsPendentsNotifica = notificacioEnviamentRepository.findEnviamentsPendentsNotificaByNotificacio(notificacio);
			notificacio.setHasEnviamentsPendents(enviamentsPendentsNotifica != null && !enviamentsPendentsNotifica.isEmpty());

			// Emplena els atributs registreLlibreNom i registreOficinaNom
			pluginHelper.addOficinaAndLlibreRegistre(notificacio);
			NotificacioInfoDto dto = conversioTipusHelper.convertir(notificacio, NotificacioInfoDto.class);
			List<Long> pendents = notificacioEventRepository.findEventsAmbCallbackPendentByNotificacioId(notificacio.getId());
			dto.setEventsCallbackPendent(notificacio.isTipusUsuariAplicacio() && pendents != null && !pendents.isEmpty());

			// Emplena dades del procediment
			ProcSerEntity procedimentEntity = notificacio.getProcediment();
			if (procedimentEntity != null && procedimentEntity.isEntregaCieActivaAlgunNivell()) {
				EntregaCieEntity entregaCieEntity = procedimentEntity.getEntregaCieEfectiva();
				dto.setOperadorPostal(conversioTipusHelper.convertir(entregaCieEntity.getOperadorPostal(), OperadorPostalDataDto.class));
				dto.setCie(conversioTipusHelper.convertir(entregaCieEntity.getCie(), CieDataDto.class));
			}

			NotificacioTableEntity notificacioTableEntity = notificacioTableViewRepository.findById(id).orElseThrow();
			dto.setNotificaErrorData(notificacioTableEntity.getNotificaErrorData());
			dto.setNotificaErrorDescripcio(notificacioTableEntity.getNotificaErrorDescripcio());
			NotificacioEventEntity lastErrorEvent = notificacioEventRepository.findLastErrorEventByNotificacioId(notificacio.getId());
			dto.setNoticaErrorEventTipus(lastErrorEvent != null ? lastErrorEvent.getTipus() : null);
			dto.setNotificaErrorTipus(lastErrorEvent != null ? lastErrorEvent.getErrorTipus() : null);
			dto.setEnviadaDate(getEnviadaDate(notificacio));

			return dto;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private Date getEnviadaDate(NotificacioEntity notificacio) {

		try {
			if (notificacio.getEnviaments() == null || notificacio.getEnviaments().isEmpty()) {
				return null;
			}

			Date dataEnviament = null;
			Iterator<NotificacioEnviamentEntity> it = notificacio.getEnviaments().iterator();
			while (it.hasNext()) {
				NotificacioEnviamentEntity env = it.next();
				if (env.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)
						&& (!notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT)
						|| !notificacio.getEstat().equals(NotificacioEstatEnumDto.ENVIANT))) {
					dataEnviament = env.getRegistreData();
				}

				if (!env.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)
						&& (!notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT)
						|| !notificacio.getEstat().equals(NotificacioEstatEnumDto.REGISTRADA)
						|| !notificacio.getEstat().equals(NotificacioEstatEnumDto.ENVIANT))) {
					dataEnviament = notificacio.getNotificaEnviamentNotificaData();
				}
				if (dataEnviament != null)
					break;
			}
			return dataEnviament;

		} catch (Exception ex) {
			log.error("Error actualitzant la data d'enviament a la taula del llistat", ex);
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<NotificacioTableItemDto> findAmbFiltrePaginat(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre,
																   PaginacioParamsDto paginacioParams) {

		log.info("Consulta taula de remeses ...");
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			var isUsuari = RolEnumDto.tothom.equals(rol);
			var isUsuariEntitat = RolEnumDto.NOT_ADMIN.equals(rol);
			var isSuperAdmin = RolEnumDto.NOT_SUPER.equals(rol);
			var isAdminOrgan = RolEnumDto.NOT_ADMIN_ORGAN.equals(rol);
			EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitatId,false, isUsuariEntitat,false);
			Page<NotificacioTableEntity> notificacions = null;
			Pageable pageable = notificacioListHelper.getMappeigPropietats(paginacioParams);
			List<String> codisProcedimentsDisponibles = new ArrayList<>();
			List<String> codisOrgansGestorsDisponibles = new ArrayList<>();
			List<String> codisProcedimentsOrgans = new ArrayList<>();
			if (isUsuari && entitatActual != null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				Permission[] permisos = entityComprovarHelper.getPermissionsFromName(PermisEnum.CONSULTA);
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

			if (filtre == null || filtre.isEmpty()) {
				//Consulta les notificacions sobre les quals té permis l'usuari actual
				if (isUsuari) {
					long start = System.nanoTime();
					notificacions = notificacioTableViewRepository.findByProcedimentCodiNotibAndGrupsCodiNotibAndEntitat(
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
							cacheHelper.findRolsUsuariAmbCodi(SecurityContextHolder.getContext().getAuthentication().getName()),
//							aplicacioService.findRolsUsuariActual(),
							esOrgansGestorsCodisNotibNull,
							esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
							esProcedimentOrgansAmbPermisNull,
							esProcedimentOrgansAmbPermisNull ?  null : codisProcedimentsOrgans,
							entitatActual,
							usuariCodi,
							pageable);
					long elapsedTime = System.nanoTime() - start;
					log.info(">>>>>>>>>>>>> Notificacions sense filtre: "  + elapsedTime);
				//Consulta les notificacions de l'entitat acutal
				} else if (isUsuariEntitat) {
					notificacions = notificacioTableViewRepository.findByEntitatActual(entitatActual, pageable);
				//Consulta totes les notificacions de les entitats actives
				} else if (isSuperAdmin) {
					var entitatsActiva = entitatRepository.findByActiva(true);
					notificacions = notificacioTableViewRepository.findByEntitatActiva(entitatsActiva, pageable);
				} else if (isAdminOrgan) {
					var organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi);
					notificacions = notificacioTableViewRepository.findByProcedimentCodiNotibAndEntitat(
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
							entitatActual,
							organs,
							pageable);
				}
			} else {
				var filtreNetejat = notificacioListHelper.getFiltre(filtre);
				if (isUsuari) {
					long start = System.nanoTime();
					notificacions = notificacioTableViewRepository.findAmbFiltreAndProcedimentCodiNotibAndGrupsCodiNotib(
							entitatActual,
							filtreNetejat.getEntitatId().isNull(),
							filtreNetejat.getEntitatId().getField(),
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
							cacheHelper.findRolsUsuariAmbCodi(SecurityContextHolder.getContext().getAuthentication().getName()),
//							aplicacioService.findRolsUsuariActual(),
							esOrgansGestorsCodisNotibNull,
							esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
							esProcedimentOrgansAmbPermisNull,
							esProcedimentOrgansAmbPermisNull ?  null : codisProcedimentsOrgans,
							filtreNetejat.getEnviamentTipus().isNull(),
							filtreNetejat.getEnviamentTipus().getField(),
							filtreNetejat.getConcepte().isNull(),
							filtreNetejat.getConcepte().isNull() ? "" : filtreNetejat.getConcepte().getField(),
							filtreNetejat.getEstat().isNull(),
							filtreNetejat.getEstat().isNull() ? 0 : filtreNetejat.getEstat().getField().getMask(),
//							!filtreNetejat.getEstat().isNull() ?
//									EnviamentEstat.valueOf(filtreNetejat.getEstat().getField().toString()) : null,
							filtreNetejat.getDataInici().isNull(),
							filtreNetejat.getDataInici().getField(),
							filtreNetejat.getDataFi().isNull(),
							filtreNetejat.getDataFi().getField(),
							filtreNetejat.getTitular().isNull(),
							filtreNetejat.getTitular().isNull() ? "" : filtreNetejat.getTitular().getField(),
							filtreNetejat.getOrganGestor().isNull(),
							filtreNetejat.getOrganGestor().isNull() ? "" : filtreNetejat.getOrganGestor().getField().getCodi(),
							filtreNetejat.getProcediment().isNull(),
							filtreNetejat.getProcediment().isNull() ? "" : filtreNetejat.getProcediment().getField().getCodi(),
							filtreNetejat.getTipusUsuari().isNull(),
							filtreNetejat.getTipusUsuari().getField(),
							filtreNetejat.getNumExpedient().isNull(),
							filtreNetejat.getNumExpedient().getField(),
							filtreNetejat.getCreadaPer().isNull(),
							filtreNetejat.getCreadaPer().getField(),
							filtreNetejat.getIdentificador().isNull(),
							filtreNetejat.getIdentificador().getField(),
							usuariCodi,
							filtreNetejat.getNomesAmbErrors().getField(),
							filtreNetejat.getNomesSenseErrors().getField(),
							filtreNetejat.getHasZeronotificaEnviamentIntent().isNull(),
							filtreNetejat.getHasZeronotificaEnviamentIntent().getField(),
							filtreNetejat.getReferencia().isNull(),
							filtreNetejat.getReferencia().getField(),
							pageable);
					long elapsedTime = System.nanoTime() - start;
					log.info(">>>>>>>>>>>>> Notificacions amb filtre: "  + elapsedTime);
				} else if (isUsuariEntitat || isSuperAdmin) {
					var entitatFiltre = isUsuariEntitat ? entitatId :filtreNetejat.getEntitatId().getField();
					notificacions = notificacioTableViewRepository.findAmbFiltre(
							entitatFiltre == null,
							entitatFiltre,
							filtreNetejat.getEnviamentTipus().isNull(),
							filtreNetejat.getEnviamentTipus().getField(),
							filtreNetejat.getConcepte().isNull(),
							filtreNetejat.getConcepte().getField(),
							filtreNetejat.getEstat().isNull(),
							filtreNetejat.getEstat().isNull() ? 0 : filtreNetejat.getEstat().getField().getMask(),
//							!filtreNetejat.getEstat().isNull() ?
//									EnviamentEstat.valueOf(filtreNetejat.getEstat().getField().toString()) : null,
							filtreNetejat.getDataInici().isNull(),
							filtreNetejat.getDataInici().getField(),
							filtreNetejat.getDataFi().isNull(),
							filtreNetejat.getDataFi().getField(),
							filtreNetejat.getTitular().isNull(),
							filtreNetejat.getTitular().isNull() ? "" : filtreNetejat.getTitular().getField(),
							filtreNetejat.getOrganGestor().isNull(),
							filtreNetejat.getOrganGestor().isNull() ? "" : filtreNetejat.getOrganGestor().getField().getCodi(),
							filtreNetejat.getProcediment().isNull(),
							filtreNetejat.getProcediment().isNull() ? "" : filtreNetejat.getProcediment().getField().getCodi(),
							filtreNetejat.getTipusUsuari().isNull(),
							filtreNetejat.getTipusUsuari().getField(),
							filtreNetejat.getNumExpedient().isNull(),
							filtreNetejat.getNumExpedient().getField(),
							filtreNetejat.getCreadaPer().isNull(),
							filtreNetejat.getCreadaPer().getField(),
							filtreNetejat.getIdentificador().isNull(),
							filtreNetejat.getIdentificador().getField(),
							filtreNetejat.getNomesAmbErrors().getField(),
							filtreNetejat.getNomesSenseErrors().getField(),
							filtreNetejat.getHasZeronotificaEnviamentIntent().isNull(),
							filtreNetejat.getHasZeronotificaEnviamentIntent().getField(),
							filtreNetejat.getReferencia().isNull(),
							filtreNetejat.getReferencia().getField(),
							pageable);

				} else if (isAdminOrgan) {
					var organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi);
					notificacions = notificacioTableViewRepository.findAmbFiltreAndProcedimentCodiNotib(
							entitatActual,
							filtreNetejat.getEntitatId().isNull(),
							filtreNetejat.getEntitatId().getField(),
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
							filtreNetejat.getEnviamentTipus().isNull(),
							filtreNetejat.getEnviamentTipus().getField(),
							filtreNetejat.getConcepte().isNull(),
							filtreNetejat.getConcepte().getField(),
							filtreNetejat.getEstat().isNull(),
							filtreNetejat.getEstat().isNull() ? 0 : filtreNetejat.getEstat().getField().getMask(),
//							!filtreNetejat.getEstat().isNull() ?
//									EnviamentEstat.valueOf(filtreNetejat.getEstat().getField().toString()) : null,
							filtreNetejat.getDataInici().isNull(),
							filtreNetejat.getDataInici().getField(),
							filtreNetejat.getDataFi().isNull(),
							filtreNetejat.getDataFi().getField(),
							filtreNetejat.getTitular().isNull(),
							filtreNetejat.getTitular().isNull() ? "" : filtreNetejat.getTitular().getField(),
							filtreNetejat.getOrganGestor().isNull(),
							filtreNetejat.getOrganGestor().isNull() ? "" : filtreNetejat.getOrganGestor().getField().getCodi(),
							filtreNetejat.getProcediment().isNull(),
							filtreNetejat.getProcediment().isNull() ? "" : filtreNetejat.getProcediment().getField().getCodi(),
							filtreNetejat.getTipusUsuari().isNull(),
							filtreNetejat.getTipusUsuari().getField(),
							filtreNetejat.getNumExpedient().isNull(),
							filtreNetejat.getNumExpedient().getField(),
							filtreNetejat.getCreadaPer().isNull(),
							filtreNetejat.getCreadaPer().getField(),
							filtreNetejat.getIdentificador().isNull(),
							filtreNetejat.getIdentificador().getField(),
							organs,
							filtreNetejat.getNomesSenseErrors().getField(),
							filtreNetejat.getHasZeronotificaEnviamentIntent().isNull(),
							filtreNetejat.getHasZeronotificaEnviamentIntent().getField(),
							filtreNetejat.getReferencia().isNull(),
							filtreNetejat.getReferencia().getField(),
							pageable);
				}
			}

			var pag = notificacioListHelper.complementaNotificacions(entitatActual, usuariCodi, notificacions);
//			List<NotificacioTableItemDto> nots = pag.getContingut();
//			for (int foo = 0; foo < nots.size(); foo++) {
//				NotificacioTableItemDto not = nots.get(foo);
//				NotificacioEntity e = notificacioRepository.findById(not.getId());
//				List<NotificacioEnviamentEntity> envs = enviamentRepository.findByNotificacio(e);
//				Date cerData = envs != null && !envs.isEmpty() && envs.get(0) != null ? envs.get(0).getNotificaCertificacioData() : null;
//				Long id = e != null && e.getDocument() != null ? e.getDocument().getId() : null;
//				not.setDocumentId(id);
//				not.setEnvCerData(cerData);
//				not.setOrganEstat(e != null && e.getOrganGestor() != null ? e.getOrganGestor().getEstat() : null);
//			}
			return pag;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsAmbFiltre(Long entitatId, RolEnumDto rol, String organGestorCodi, String usuariCodi, NotificacioFiltreDto filtre) {

		PaginacioParamsDto paginacioParamsDto = new PaginacioParamsDto();
		paginacioParamsDto.setPaginaNum(0);
		paginacioParamsDto.setPaginaTamany(notificacioEnviamentRepository.findAll().size());
		PaginaDto<NotificacioTableItemDto> pagina = findAmbFiltrePaginat(entitatId, rol, organGestorCodi, usuariCodi, filtre, paginacioParamsDto);
		List<Long> idsNotificacions = new ArrayList<>();
		for (NotificacioTableItemDto notificacio: pagina.getContingut()) {
			idsNotificacions.add(notificacio.getId());
		}
		return idsNotificacions;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<NotificacioDto> findWithCallbackError(NotificacioErrorCallbackFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		Page<NotificacioEntity> page = null;
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (filtre == null || filtre.isEmpty()) {
				page = notificacioRepository.findNotificacioLastEventAmbError(paginacioHelper.toSpringDataPageable(paginacioParams));
			} else {
				Date dataInici = filtre.getDataInici();
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
	//					filtre.getEntitatId() == null,
	//					filtre.getEntitatId(),
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
				
			if (page != null && page.getContent() != null && page.getContent().size() > 0) {
				return paginacioHelper.toPaginaDto(page, NotificacioDto.class);
			}
			return paginacioHelper.getPaginaDtoBuida(NotificacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CodiValorDto> llistarNivellsAdministracions() {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValor> codiValor = new ArrayList<CodiValor>();
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

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValor> codiValor = new ArrayList<CodiValor>();
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

		Timer.Context timer = metricsHelper.iniciMetrica();
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

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValor> codiValor = new ArrayList<CodiValor>();
			try {
				codiValor = pluginHelper.llistarProvincies();
			} catch (Exception ex) {
				log.error("Error recuperant les provincies de DIR3CAIB: " + ex);
			}
			return conversioTipusHelper.convertirList(codiValor, ProvinciesDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ProvinciesDto> llistarProvincies(String codiCA) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValor> codiValor = new ArrayList<CodiValor>();
			try {
				codiValor = cacheHelper.llistarProvincies(codiCA);
			} catch (Exception ex) {
				log.error("Error recuperant les provincies de DIR3CAIB: " + ex);
			}
			return conversioTipusHelper.convertirList(codiValor, ProvinciesDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<LocalitatsDto> llistarLocalitats(String codiProvincia) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValor> codiValor = new ArrayList<CodiValor>();
			try {
				codiValor = cacheHelper.llistarLocalitats(codiProvincia);
			} catch (Exception ex) {
				log.error("Error recuperant les provincies de DIR3CAIB: " + ex);
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

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return pluginHelper.cercaUnitats(codi, denominacio, nivellAdministracio, comunitatAutonoma, ambOficines, esUnitatArrel, provincia, municipi);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> unitatsPerCodi(String codi) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return pluginHelper.unitatsPerCodi(codi);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> unitatsPerDenominacio(String denominacio) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return pluginHelper.unitatsPerDenominacio(denominacio);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbNotificacio(Long entitatId, Long notificacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
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

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta dels històrics de la notificació (notificacioId=" + notificacioId + ")");
			List<NotificacioAudit> historic = notificacioAuditRepository.findByNotificacioIdOrderByCreatedDateAsc(notificacioId);
			return conversioTipusHelper.convertirList(historic, NotificacioAuditDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public NotificacioEventDto findUltimEventCallbackByNotificacio(Long notificacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			NotificacioEventEntity event = notificacioEventRepository.findUltimEventByNotificacioId(notificacioId);
			return event != null ? conversioTipusHelper.convertir(event, NotificacioEventDto.class) : null;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public NotificacioEventDto findUltimEventRegistreByNotificacio(Long notificacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			NotificacioEventEntity event = notificacioEventRepository.findUltimEventRegistreByNotificacioId(notificacioId);
			return event != null ? conversioTipusHelper.convertir(event, NotificacioEventDto.class) : null;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbEnviament(Long entitatId, Long notificacioId, Long enviamentId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta dels events associats a un destinatari (notificacioId=" + notificacioId + ", enviamentId=" + enviamentId + ")");
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
			entityComprovarHelper.comprovarPermisos(enviament.getNotificacio().getId(), true, true, true);
			return conversioTipusHelper.convertirList(notificacioEventRepository.findByNotificacioIdOrEnviamentIdOrderByDataAsc(notificacioId, enviamentId), NotificacioEventDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEnviamentAuditDto> historicFindAmbEnviament(Long entitatId, Long notificacioId, Long enviamentId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Consulta dels events associats a un destinatari (notificacioId=" + notificacioId + ", enviamentId=" + enviamentId + ")");
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
			entityComprovarHelper.comprovarPermisos(enviament.getNotificacio().getId(), true, true, true);
			return conversioTipusHelper.convertirList(notificacioEnviamentAuditRepository.findByEnviamentIdOrderByCreatedDateAsc(enviamentId), NotificacioEnviamentAuditDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getDocumentArxiu(Long notificacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String nomDocumentDefault = "document";
			NotificacioEntity entity = notificacioRepository.findById(notificacioId).orElseThrow();
			DocumentEntity document = entity.getDocument();
			return documentHelper.documentToArxiuDto(nomDocumentDefault, document);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getDocumentArxiu(Long notificacioId, Long documentId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String nomDocumentDefault = "document";
			DocumentEntity document = documentRepository.findById(documentId).orElse(null);
//			DocumentEntity document = documentRepository.findByNotificacioIdAndId(notificacioId, documentId);
			return documentHelper.documentToArxiuDto(nomDocumentDefault, document);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto enviamentGetCertificacioArxiu(Long enviamentId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(enviament.getNotificaCertificacioArxiuId(), PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, output);
			return new ArxiuDto(calcularNomArxiuCertificacio(enviament), enviament.getNotificaCertificacioMime(), output.toByteArray(), output.size());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public boolean enviar(Long notificacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Intentant enviament de la notificació pendent (notificacioId=" + notificacioId + ")");
			NotificacioEntity notificacio = notificaHelper.notificacioEnviar(notificacioId);
			return (notificacio != null && NotificacioEstatEnumDto.ENVIADA.equals(notificacio.getEstat()));
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public List<RegistreIdDto> registrarNotificar(Long notificacioId) throws RegistreNotificaException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			return notificacioHelper.registrarNotificar(notificacioId);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(Long entitatId, Long enviamentId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Refrescant l'estat de la notificació de Notific@ (enviamentId=" + enviamentId + ")");
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId).orElseThrow();
//			enviament.setNotificacio(notificacioRepository.findById(enviament.getNotificacio().getId()));
			notificaHelper.enviamentRefrescarEstat(enviament.getId());
			NotificacioEnviamenEstatDto estatDto = conversioTipusHelper.convertir(enviament, NotificacioEnviamenEstatDto.class);
			estatCalcularCampsAddicionals(enviament, estatDto);
			return estatDto;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public String marcarComProcessada(Long notificacioId, String motiu, boolean isAdministrador) throws Exception {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Refrescant l'estat de la notificació a PROCESSAT (notificacioId=" + notificacioId + ")");
			String resposta = null;
			NotificacioEntity notificacioEntity = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			if (!NotificacioEstatEnumDto.FINALITZADA.equals(notificacioEntity.getEstat())) {
				throw new Exception("La notificació no es pot marcar com a processada, no esta en estat finalitzada.");
			}
			var permisProcessar = permisosService.hasNotificacioPermis(notificacioId, notificacioEntity.getEntitat().getId(), notificacioEntity.getUsuariCodi(), PermisEnum.PROCESSAR);
			if (!isAdministrador && !permisProcessar) {
				throw new Exception("La notificació no es pot marcar com a processada, l'usuari no té els permisos requerits.");
			}
			notificacioEntity = auditNotificacioHelper.updateNotificacioProcessada(notificacioEntity, motiu);
			UsuariEntity usuari = usuariHelper.getUsuariAutenticat();
			if(usuari != null && notificacioEntity.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB) {
//				if(!usuari.isRebreEmailsNotificacioCreats() || usuari.getCodi() == notificacioEntity.getCreatedBy().getCodi()) {
					resposta = emailNotificacioHelper.prepararEnvioEmailNotificacio(notificacioEntity);
//				}
			}
			notificacioRepository.saveAndFlush(notificacioEntity);
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	/**
	 * Comprova si l'usuari actual té un determinat permís sobre una notificació
	 *
	 * @param notificacio Notificació a comprovar
	 * @return boleà indicant si l'usuari té el permís
	 */
	private boolean hasPermisProcessar(NotificacioEntity notificacio) {

		boolean hasPermis = false;
		ProcSerEntity procedimentNotificacio = notificacio.getProcediment();
		if (procedimentNotificacio != null) {
			hasPermis = entityComprovarHelper.hasPermisProcediment(
					notificacio.getProcediment().getId(),
					PermisEnum.PROCESSAR);
		}
		// Si no té permís otorgat a nivell de procediment, en els casos en que l'òrgan gestor no vé fix pel procediment l'hem de comprovar
		if (!hasPermis && (procedimentNotificacio == null || procedimentNotificacio.isComu())) {
			hasPermis = entityComprovarHelper.hasPermisOrganGestor(
					notificacio.getOrganGestor(),
					PermisEnum.PROCESSAR);
		}
		// En cas de procediments comuns també es pot tenir permís per a la tupla procediment-organ
		if (!hasPermis && procedimentNotificacio != null && procedimentNotificacio.isComu()) {
			ProcSerOrganEntity procedimentOrganEntity = procedimentOrganRepository.findByProcSerIdAndOrganGestorId(procedimentNotificacio.getId(), notificacio.getOrganGestor().getId());
			hasPermis = entityComprovarHelper.hasPermisProcedimentOrgan(procedimentOrganEntity, PermisEnum.PROCESSAR);
		}
		return hasPermis;
	}

	@Transactional
	@Override
	public boolean reactivarConsulta(Long notificacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Reactivant consultes d'estat de la notificació (notificacioId=" + notificacioId + ")");
			NotificacioEntity notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
	//			List<NotificacioEnviamentEntity> enviamentsEntity = notificacioEnviamentRepository.findByNotificacio(notificacio);
			for(NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
				auditEnviamentHelper.resetConsultaNotifica(enviament);
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

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Reactivant consultes d'estat de SIR (notificacioId=" + notificacioId + ")");
			NotificacioEntity notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			for(NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
				auditEnviamentHelper.resetConsultaSir(enviament);
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
			var p = PageRequest.of(0, maxPendents);
			return notificacioRepository.findByNotificaEstatPendent(pluginHelper.getRegistreReintentsMaxProperty(), p);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List getNotificacionsPendentsEnviar() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var maxPendents = getNotificaEnviamentsProcessarMaxProperty();
			var p = PageRequest.of(0, maxPendents);
			return notificacioRepository.findByNotificaEstatRegistradaAmbReintentsDisponibles(pluginHelper.getNotificaReintentsMaxProperty(), p);
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
			// 3. Una part dels enviaments a Notifica i l'altre via email
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
			var p = PageRequest.of(0, maxPendents);
			return notificacioEnviamentRepository.findByNotificaRefresc(pluginHelper.getConsultaReintentsMaxProperty(), p);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void enviamentRefrescarEstat(Long enviamentId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
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
			var p = PageRequest.of(0, maxPendents);
			return notificacioEnviamentRepository.findByRegistreRefresc(pluginHelper.getConsultaSirReintentsMaxProperty(), p);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public void enviamentRefrescarEstatRegistre(Long enviamentId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			registreHelper.enviamentRefrescarEstatRegistre(enviamentId);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<NotificacioDto> findNotificacionsAmbErrorRegistre(Long entitatId, NotificacioRegistreErrorFiltreDto filtre, PaginacioParamsDto paginacioParams) {

		Page<NotificacioEntity> page = null;
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (filtre == null || filtre.isEmpty()) {
				Pageable params = paginacioHelper.toSpringDataPageable(paginacioParams);
				page = notificacioRepository.findByNotificaEstatPendentSenseReintentsDisponibles(entitatId, pluginHelper.getRegistreReintentsMaxProperty(), params);
			} else {
				Date dataInici = filtre.getDataInici();
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
			if (page != null && page.getContent() != null && page.getContent().size() > 0) {
				return paginacioHelper.toPaginaDto(page, NotificacioDto.class);
			}
			return paginacioHelper.getPaginaDtoBuida(NotificacioDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	public List<Long> findNotificacionsIdAmbErrorRegistre(Long entitatId, NotificacioRegistreErrorFiltreDto filtre) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		List<Long> ids = null;
		try {
			if (filtre == null || filtre.isEmpty()) {
				ids = notificacioRepository.findIdsByNotificaEstatPendentSenseReintentsDisponibles(entitatId, pluginHelper.getRegistreReintentsMaxProperty());
			} else {
				Date dataInici = filtre.getDataInici();
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
				ids = notificacioRepository.findIdsByNotificaEstatPendentSenseReintentsDisponiblesAmbFiltre(
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
			}
			return ids;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void reactivarRegistre(Long notificacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			log.debug("Reactivant registre de la notificació (notificacioId=" + notificacioId + ")");
			NotificacioEntity notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			auditNotificacioHelper.updateNotificacioRefreshRegistreNotificacio(notificacio);
		} catch (Exception e) {
			log.debug("Error reactivant consultes d'estat de la notificació (notificacioId=" + notificacioId + ")", e);
		} finally {
			metricsHelper.fiMetrica(timer);
		}		
	}

	@Transactional
	@Override
	public boolean reenviarNotificacioAmbErrors(Long notificacioId) {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			NotificacioEntity notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			if (NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(notificacio.getEstat())) {
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

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			NotificacioEntity notificacio = entityComprovarHelper.comprovarNotificacio(null, notificacioId);
			if (NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS.equals(notificacio.getEstat())) {
				auditNotificacioHelper.updateNotificacioReintentaFinalitzadaAmbErrors(notificacio);
				return true;
			}
		} catch (Exception e) {
			log.debug("Error reactivant notificació amb errors (notificacioId=" + notificacioId + ")", e);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
		return false;
	}

	@Override
	public SignatureInfoDto checkIfSignedAttached(byte[] contingut, String nom, String contentType) {
//		if (configHelper.getAsBoolean("es.caib.notib.firma.detectar.attached.validate.signature", true)) {
		return pluginHelper.detectSignedAttachedUsingValidateSignaturePlugin(contingut, nom, contentType);
//		} else {
//			return pluginHelper.detectSignedAttachedUsingPdfReader(
//					contingut,
//					contentType);
//		}
	}

	@Override
	public void refrescarEnviamentsExpirats() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			
			log.debug("S'ha iniciat els procés d'actualització dels enviaments expirats");
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String username = auth == null ? "schedulled" : auth.getName();
			ProgresActualitzacioCertificacioDto progres = progresActualitzacioExpirades.get(username);
			if (progres != null) {
				progres.addInfo(TipusActInfo.ERROR, "Existeix un altre procés en progrés...");
				return;
			}
			progres = new ProgresActualitzacioCertificacioDto();
			progresActualitzacioExpirades.put(username, progres);
			enviamentHelper.refrescarEnviamentsExpirats(progres);
			progres.setProgres(100);
			progres.setFinished(true);
//			progresActualitzacioExpirades.remove(username);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public ProgresActualitzacioCertificacioDto actualitzacioEnviamentsEstat() {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ProgresActualitzacioCertificacioDto progres = progresActualitzacioExpirades.get(auth.getName());
//			if (progres != null && progres.getProgres() != null &&  progres.getProgres() >= 100) {
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
			var p = PageRequest.of(0, maxPendents);
			return notificacioEnviamentRepository.findByDEHAndEstatFinal(pluginHelper.getConsultaReintentsDEHMaxProperty(), p);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	@Override
	public List<Long> getNotificacionsCIEPendentsRefrescarCert() {

		var timer = metricsHelper.iniciMetrica();
		try {
			var maxPendents = getEnviamentCIEActualitzacioCertProcessarMaxProperty();
			var p = PageRequest.of(0, maxPendents);
			return notificacioEnviamentRepository.findByCIEAndEstatFinal(pluginHelper.getConsultaReintentsCIEMaxProperty(), p);
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
	
	private void estatCalcularCampsAddicionals(NotificacioEnviamentEntity enviament, NotificacioEnviamenEstatDto estatDto) {

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
		estatDto.setNotificaCertificacioArxiuNom(calcularNomArxiuCertificacio(enviament));
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
			documentArxiu =  esUuid ? pluginHelper.arxiuDocumentConsultar(identificador, null, true, true)
							: pluginHelper.arxiuDocumentConsultar(identificador, null, true, false);
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