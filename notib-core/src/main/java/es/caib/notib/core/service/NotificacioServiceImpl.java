/**
 * 
 */
package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.ProgresActualitzacioCertificacioDto.TipusActInfo;
import es.caib.notib.core.api.dto.cie.CieDataDto;
import es.caib.notib.core.api.dto.cie.OperadorPostalDataDto;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.core.api.dto.notificacio.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.ws.notificacio.*;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.entity.auditoria.NotificacioAudit;
import es.caib.notib.core.entity.cie.EntregaCieEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.*;
import es.caib.notib.core.repository.auditoria.NotificacioAuditRepository;
import es.caib.notib.core.repository.auditoria.NotificacioEnviamentAuditRepository;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Implementació del servei de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class NotificacioServiceImpl implements NotificacioService {
	
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
	private NotificacioListHelper notificacioListHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private ProcSerHelper procedimentHelper;
	@Autowired
	private ProcSerOrganRepository procedimentOrganRepository;

	public static Map<String, ProgresActualitzacioCertificacioDto> progresActualitzacioExpirades = new HashMap<>();

	@Transactional(rollbackFor=Exception.class)
	@Override
	public NotificacioDatabaseDto create(
			Long entitatId,
			NotificacioDatabaseDto notificacio) throws RegistreNotificaException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);

			NotificacioHelper.NotificacioData notData = notificacioHelper.buildNotificacioData(entitat, notificacio,
					false);
			// Dades generals de la notificació
			NotificacioEntity notificacioEntity = notificacioHelper.saveNotificacio(notData);


			notificacioHelper.altaEnviamentsWeb(entitat, notificacioEntity, notificacio.getEnviaments());

			return conversioTipusHelper.convertir(
				notificacioEntity,
				NotificacioDatabaseDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void delete(
			Long entitatId, 
			Long notificacioId) throws NotFoundException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false, 
					true, 
					true, 
					false);
			
			logger.debug("Esborrant la notificació (notificacioId=" + notificacioId + ")");
			NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
			if (notificacio == null)
				throw new NotFoundException(
						notificacioId, 
						NotificacioEntity.class,
						"No s'ha trobat cap notificació amb l'id especificat");
			
			List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacioId);
//			### Esborrar la notificació
			if (enviamentsPendents != null && ! enviamentsPendents.isEmpty()) {
				// esborram tots els seus events
				notificacioEventRepository.deleteByNotificacio(notificacio);

//				## El titular s'ha d'esborrar de forma individual
				for (NotificacioEnviamentEntity enviament : notificacio.getEnviaments()) {
					PersonaEntity titular = enviament.getTitular();
					if (HibernateHelper.isProxy(titular))
						titular = HibernateHelper.deproxy(titular);
					auditEnviamentHelper.deleteEnviament(enviament);
					personaRepository.delete(titular);
				}

				auditNotificacioHelper.deleteNotificacio(notificacio);
				logger.debug("La notificació s'ha esborrat correctament (notificacioId=" + notificacioId + ")");
			} else {
				throw new ValidationException("Aquesta notificació està enviada i no es pot esborrar");
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public NotificacioDatabaseDto update(
			Long entitatId,
			NotificacioDatabaseDto notificacio,
			boolean isAdministradorEntitat) throws NotFoundException, RegistreNotificaException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false, 
					true, 
					true,
					false);
			List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacio.getId());
			if (enviamentsPendents == null || enviamentsPendents.isEmpty()) {
				throw new ValidationException("Aquesta notificació està enviada i no es pot modificar");
			}

			NotificacioEntity notificacioEntity = notificacioRepository.findOne(notificacio.getId());
			NotificacioHelper.NotificacioData notData = notificacioHelper.buildNotificacioData(entitat, notificacio, false); //!isAdministradorEntitat);

			// Actualitzar notificació existent
			auditNotificacioHelper.updateNotificacio(notificacioEntity, notData);
				
			// Esbo
			if (notificacioEntity.getDocument2() != null && notificacio.getDocument2() == null)
				documentRepository.delete(notData.getDocument2Entity());
			if (notificacioEntity.getDocument3() != null && notificacio.getDocument3() == null)
				documentRepository.delete(notData.getDocument3Entity());
			if (notificacioEntity.getDocument4() != null && notificacio.getDocument4() == null)
				documentRepository.delete(notData.getDocument4Entity());
			if (notificacioEntity.getDocument5() != null && notificacio.getDocument5() == null)
				documentRepository.delete(notData.getDocument5Entity());

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
				if (enviament.getId() != null) //En cas d'enviaments nous
					enviamentsIds.add(enviament.getId());
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

				PersonaEntity titular = null;
				if (enviament.getTitular().getId() != null) {
					titular = personaHelper.update(enviament.getTitular(),  enviament.getTitular().isIncapacitat());
				} else {
					titular = personaHelper.create(enviament.getTitular(), enviament.getTitular().isIncapacitat());
				}
				List<PersonaEntity> nousDestinataris = new ArrayList<PersonaEntity>();
//					### Crear o editar destinataris enviament existent
				if (enviament.getDestinataris() != null) {
					for(Persona destinatari: enviament.getDestinataris()) {
							if ((destinatari.getNif() != null && !destinatari.getNif().isEmpty()) ||
									(destinatari.getDir3Codi() != null && !destinatari.getDir3Codi().isEmpty())) {
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
					NotificacioEnviamentEntity enviamentEntity = auditEnviamentHelper.updateEnviament(
							entitat,
							notificacioEntity,
							enviament,
							serveiTipus,
//							numeracioTipus,
//							tipusConcret,
							titular);
					enviamentEntity.getDestinataris().addAll(nousDestinataris);
				} else {
					NotificacioEnviamentEntity nouEnviament = auditEnviamentHelper.desaEnviament(
							entitat,
							notificacioEntity,
							enviament,
							serveiTipus,
							titular,
							nousDestinataris);
					nousEnviaments.add(nouEnviament);
					enviamentsIds.add(nouEnviament.getId());
				}
			}
			notificacioEntity.getEnviaments().addAll(nousEnviaments);
//			### Enviaments esborrats
			Set<NotificacioEnviamentEntity> enviamentsDisponibles = new HashSet<NotificacioEnviamentEntity>(notificacioEntity.getEnviaments());
			for (NotificacioEnviamentEntity enviament: enviamentsDisponibles) {
				if (HibernateHelper.isProxy(enviament)) //en cas d'haver modificat l'enviament
					enviament = HibernateHelper.deproxy(enviament);

				if (! enviamentsIds.contains(enviament.getId())) {
					notificacioEntity.getEnviaments().remove(enviament);
					auditEnviamentHelper.deleteEnviament(enviament);
				}

//				### Destinataris esborrats
				List<PersonaEntity> destinatarisDisponibles = new ArrayList<PersonaEntity>(enviament.getDestinataris());
				for (PersonaEntity destinatari : destinatarisDisponibles) {
					if (HibernateHelper.isProxy(destinatari)) //en cas d'haver modificat l'interessat
						destinatari = HibernateHelper.deproxy(destinatari);

					if (! destinatarisIds.contains(destinatari.getId())) {
						enviament.getDestinataris().remove(destinatari);
						personaRepository.delete(destinatari);
					}
				}
			}

//			### Realitzar el procés de registre i notific@
			if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(pluginHelper.getNotibTipusComunicacioDefecte())) {
				synchronized(CreacioSemaforDto.getCreacioSemafor()) {
					boolean notificar = registreNotificaHelper.realitzarProcesRegistrar(
							notificacioEntity);
					if (notificar)
						notificaHelper.notificacioEnviar(notificacioEntity.getId());
				}
			}

			return conversioTipusHelper.convertir(
					notificacioRepository.getOne(notificacio.getId()),
					NotificacioDatabaseDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public NotificacioDtoV2 findAmbId(
			Long id,
			boolean isAdministrador) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de la notificacio amb id (id=" + id + ")");
			NotificacioEntity notificacio = notificacioRepository.findById(id);
			if(notificacio == null) {
				return null;
			}
			entityComprovarHelper.comprovarPermisos(
					null,
					false,
					false,
					false);

			List<NotificacioEnviamentEntity> enviamentsPendentsNotifica = notificacioEnviamentRepository.findEnviamentsPendentsNotificaByNotificacio(notificacio);
			notificacio.setHasEnviamentsPendents(enviamentsPendentsNotifica != null && !enviamentsPendentsNotifica.isEmpty());

			pluginHelper.addOficinaAndLlibreRegistre(notificacio);

			return conversioTipusHelper.convertir(
					notificacio,
					NotificacioDtoV2.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}


	@Transactional(readOnly = true)
	@Override
	public NotificacioInfoDto findNotificacioInfo(Long id, boolean isAdministrador) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta de la notificacio amb id (id=" + id + ")");
			NotificacioEntity notificacio = notificacioRepository.findOne(id);
			if(notificacio == null) {
				return null;
			}

			List<NotificacioEnviamentEntity> enviamentsPendentsNotifica = notificacioEnviamentRepository.findEnviamentsPendentsNotificaByNotificacio(notificacio);
			notificacio.setHasEnviamentsPendents(enviamentsPendentsNotifica != null && !enviamentsPendentsNotifica.isEmpty());

			// Emplena els atributs registreLlibreNom i registreOficinaNom
			pluginHelper.addOficinaAndLlibreRegistre(notificacio);

			NotificacioInfoDto dto = conversioTipusHelper.convertir(
					notificacio,
					NotificacioInfoDto.class);

			// Emplena dades del procediment
			ProcSerEntity procedimentEntity = notificacio.getProcediment();
			if (procedimentEntity != null && procedimentEntity.isEntregaCieActivaAlgunNivell()) {
				EntregaCieEntity entregaCieEntity = procedimentEntity.getEntregaCieEfectiva();
				dto.setOperadorPostal(conversioTipusHelper.convertir(entregaCieEntity.getOperadorPostal(),
						OperadorPostalDataDto.class));
				dto.setCie(conversioTipusHelper.convertir(entregaCieEntity.getCie(),
						CieDataDto.class));
			}

			NotificacioTableEntity notificacioTableEntity = notificacioTableViewRepository.findOne(id);
			dto.setNotificaErrorData(notificacioTableEntity.getNotificaErrorData());
			dto.setNotificaErrorDescripcio(notificacioTableEntity.getNotificaErrorDescripcio());

			NotificacioEventEntity lastErrorEvent = notificacioEventRepository.findLastErrorEventByNotificacioId(notificacio.getId());
			dto.setNoticaErrorEventTipus(lastErrorEvent != null ? lastErrorEvent.getTipus() : null);
			dto.setNotificaErrorTipus(lastErrorEvent != null ? lastErrorEvent.getErrorTipus() : null);
			return dto;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<NotificacioTableItemDto> findAmbFiltrePaginat(
			Long entitatId,
			RolEnumDto rol,
			String organGestorCodi,
			String usuariCodi,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.info("Consulta taula de remeses ...");
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			boolean isUsuari = RolEnumDto.tothom.equals(rol);
			boolean isUsuariEntitat = RolEnumDto.NOT_ADMIN.equals(rol);
			boolean isSuperAdmin = RolEnumDto.NOT_SUPER.equals(rol);
			boolean isAdminOrgan = RolEnumDto.NOT_ADMIN_ORGAN.equals(rol);
			EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false, 
					isUsuariEntitat,
					false);

			Page<NotificacioTableEntity> notificacions = null;
			Pageable pageable = notificacioListHelper.getMappeigPropietats(paginacioParams);

			List<String> codisProcedimentsDisponibles = new ArrayList<>();
			List<String> codisOrgansGestorsDisponibles = new ArrayList<>();
			List<String> codisProcedimentsOrgans = new ArrayList<>();

			if (isUsuari && entitatActual != null) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				Permission[] permisos = entityComprovarHelper.getPermissionsFromName(PermisEnum.CONSULTA);
				// Procediments accessibles per qualsevol òrgan gestor
				codisProcedimentsDisponibles = procedimentHelper.findCodiProcedimentsWithPermis(auth, entitatActual, permisos);

				// Òrgans gestors dels que es poden consultar tots els procediments que no requereixen permís directe
				codisOrgansGestorsDisponibles = organGestorHelper.findCodiOrgansGestorsWithPermis(auth, entitatActual, permisos);

				// Procediments comuns que es poden consultar per a òrgans gestors concrets
				codisProcedimentsOrgans = procedimentHelper.findCodiProcedimentsOrganWithPermis(
						auth,
						entitatActual,
						permisos);

			} else if (isAdminOrgan && entitatActual != null) {
				codisProcedimentsDisponibles = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(
						entitatActual.getDir3Codi(),
						organGestorCodi);
			}

			boolean esProcedimentsCodisNotibNull = (codisProcedimentsDisponibles == null || codisProcedimentsDisponibles.isEmpty());
			boolean esOrgansGestorsCodisNotibNull = (codisOrgansGestorsDisponibles == null || codisOrgansGestorsDisponibles.isEmpty());
			boolean esProcedimentOrgansAmbPermisNull = (codisProcedimentsOrgans == null || codisProcedimentsOrgans.isEmpty());

			if (filtre == null || filtre.isEmpty()) {
				//Consulta les notificacions sobre les quals té permis l'usuari actual
				if (isUsuari) {
					notificacions = notificacioTableViewRepository.findByProcedimentCodiNotibAndGrupsCodiNotibAndEntitat(
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
							aplicacioService.findRolsUsuariActual(),
							esOrgansGestorsCodisNotibNull,
							esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
							esProcedimentOrgansAmbPermisNull,
							esProcedimentOrgansAmbPermisNull ?  null : codisProcedimentsOrgans,
							entitatActual,
							usuariCodi,
							pageable);
				//Consulta les notificacions de l'entitat acutal
				} else if (isUsuariEntitat) {
					notificacions = notificacioTableViewRepository.findByEntitatActual(
							entitatActual,
							pageable);
				//Consulta totes les notificacions de les entitats actives
				} else if (isSuperAdmin) {
					List<EntitatEntity> entitatsActiva = entitatRepository.findByActiva(true);
					notificacions = notificacioTableViewRepository.findByEntitatActiva(
							entitatsActiva,
							pageable);
				} else if (isAdminOrgan) {
					List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi);
					notificacions = notificacioTableViewRepository.findByProcedimentCodiNotibAndEntitat(
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
							entitatActual,
							organs,
							pageable);
				}
			} else {
				NotificacioListHelper.NotificacioFiltre filtreNetejat = notificacioListHelper.getFiltre(filtre);

				if (isUsuari) {
					notificacions = notificacioTableViewRepository.findAmbFiltreAndProcedimentCodiNotibAndGrupsCodiNotib(
							entitatActual,
							filtreNetejat.getEntitatId().isNull(),
							filtreNetejat.getEntitatId().getField(),
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles,
							aplicacioService.findRolsUsuariActual(),
							esOrgansGestorsCodisNotibNull,
							esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
							esProcedimentOrgansAmbPermisNull,
							esProcedimentOrgansAmbPermisNull ?  null : codisProcedimentsOrgans,
							filtreNetejat.getEnviamentTipus().isNull(),
							filtreNetejat.getEnviamentTipus().getField(),
							filtreNetejat.getConcepte().isNull(),
							filtreNetejat.getConcepte().isNull() ? "" : filtreNetejat.getConcepte().getField(),
							filtreNetejat.getEstat().isNull(),
							filtreNetejat.getEstat().getField(),
							!filtreNetejat.getEstat().isNull() ?
									NotificacioEnviamentEstatEnumDto.valueOf(filtreNetejat.getEstat().getField().toString()) : null,
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
							pageable);

				} else if (isUsuariEntitat || isSuperAdmin) {
					Long entitatFiltre;
					if (isUsuariEntitat){
						entitatFiltre = entitatId;
					} else {
						entitatFiltre = filtreNetejat.getEntitatId().getField();
					}

					notificacions = notificacioTableViewRepository.findAmbFiltre(
							entitatFiltre == null,
							entitatFiltre,
							filtreNetejat.getEnviamentTipus().isNull(),
							filtreNetejat.getEnviamentTipus().getField(),
							filtreNetejat.getConcepte().isNull(),
							filtreNetejat.getConcepte().getField(),
							filtreNetejat.getEstat().isNull(),
							filtreNetejat.getEstat().getField(),
							!filtreNetejat.getEstat().isNull() ?
									NotificacioEnviamentEstatEnumDto.valueOf(filtreNetejat.getEstat().getField().toString()) : null,
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
							pageable);

				} else if (isAdminOrgan) {
					List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi);
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
							filtreNetejat.getEstat().getField(),
							!filtreNetejat.getEstat().isNull() ?
									NotificacioEnviamentEstatEnumDto.valueOf(filtreNetejat.getEstat().getField().toString()) : null,
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
							pageable);
				}
			}

			return notificacioListHelper.complementaNotificacions(entitatActual, usuariCodi, notificacions);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Long> findIdsAmbFiltre(Long entitatId,
									   RolEnumDto rol,
									   String organGestorCodi,
									   String usuariCodi,
									   NotificacioFiltreDto filtre) {
		PaginacioParamsDto paginacioParamsDto = new PaginacioParamsDto();
		paginacioParamsDto.setPaginaNum(0);
		paginacioParamsDto.setPaginaTamany(notificacioEnviamentRepository.findAll().size());
		PaginaDto<NotificacioTableItemDto> pagina = findAmbFiltrePaginat(entitatId, rol, organGestorCodi,
				usuariCodi,
				filtre,
				paginacioParamsDto);
		List<Long> idsNotificacions = new ArrayList<>();
		for (NotificacioTableItemDto notificacio: pagina.getContingut()) {
			idsNotificacions.add(notificacio.getId());
		}
		return idsNotificacions;
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<NotificacioDto> findWithCallbackError(
			NotificacioErrorCallbackFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		Page<NotificacioEntity> page = null;
		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (filtre == null || filtre.isEmpty()) {
				page = notificacioRepository.findNotificacioLastEventAmbError(
						paginacioHelper.toSpringDataPageable(paginacioParams));
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
					procediment = procedimentRepository.findById(filtre.getProcedimentId());
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
						filtre.getEstat() == null ? null : NotificacioEnviamentEstatEnumDto.valueOf(filtre.getEstat().toString()),
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
				logger.error(
						"Error recuperant els nivells d'administració de DIR3CAIB: " + ex);
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
				logger.error(
						"Error recuperant les comunitats autònomes de DIR3CAIB: " + ex);
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
			List<CodiValorPais> codiValorPais = new ArrayList<CodiValorPais>();
			try {
				codiValorPais = pluginHelper.llistarPaisos();
			} catch (Exception ex) {
				logger.error(
						"Error recuperant els paisos de DIR3CAIB: " + ex);
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
				logger.error(
						"Error recuperant les provincies de DIR3CAIB: " + ex);
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
				logger.error(
						"Error recuperant les provincies de DIR3CAIB: " + ex);
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
				logger.error(
						"Error recuperant les provincies de DIR3CAIB: " + ex);
			}
			return conversioTipusHelper.convertirList(codiValor, LocalitatsDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	
	@Override
	@Transactional(readOnly = true)
	public List<OrganGestorDto> cercaUnitats(
			String codi, 
			String denominacio,
			Long nivellAdministracio, 
			Long comunitatAutonoma, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long provincia, 
			String municipi) {
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
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long entitatId, 
			Long notificacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels events de la notificació (" +
					"notificacioId=" + notificacioId + ")");
			return conversioTipusHelper.convertirList(
					notificacioEventRepository.findByNotificacioIdOrderByDataAsc(notificacioId),
					NotificacioEventDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioAuditDto> historicFindAmbNotificacio(
			Long entitatId,
			Long notificacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels històrics de la notificació (" +
					"notificacioId=" + notificacioId + ")");
			List<NotificacioAudit> historic = notificacioAuditRepository.findByNotificacioIdOrderByCreatedDateAsc(notificacioId);
			return conversioTipusHelper.convertirList(
					historic,
					NotificacioAuditDto.class);
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
			if (event == null)
				return null;
			return conversioTipusHelper.convertir(
					event, 
					NotificacioEventDto.class);
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
			if (event == null)
				return null;
			return conversioTipusHelper.convertir(
					event, 
					NotificacioEventDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbEnviament(
			Long entitatId, 
			Long notificacioId,
			Long enviamentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels events associats a un destinatari (" +
					"notificacioId=" + notificacioId + ", " + 
					"enviamentId=" + enviamentId + ")");
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
			entityComprovarHelper.comprovarPermisos(
					enviament.getNotificacio().getId(),
					true,
					true,
					true);
			return conversioTipusHelper.convertirList(
					notificacioEventRepository.findByNotificacioIdOrEnviamentIdOrderByDataAsc(
							notificacioId,
							enviamentId),
					NotificacioEventDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEnviamentAuditDto> historicFindAmbEnviament(
			Long entitatId,
			Long notificacioId,
			Long enviamentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Consulta dels events associats a un destinatari (" +
					"notificacioId=" + notificacioId + ", " +
					"enviamentId=" + enviamentId + ")");
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
			entityComprovarHelper.comprovarPermisos(
					enviament.getNotificacio().getId(),
					true,
					true,
					true);
			return conversioTipusHelper.convertirList(
					notificacioEnviamentAuditRepository.findByEnviamentIdOrderByCreatedDateAsc(
							enviamentId),
					NotificacioEnviamentAuditDto.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getDocumentArxiu(
			Long notificacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String nomDocumetnDefault = "document";
			NotificacioEntity entity = notificacioRepository.findById(notificacioId);
			DocumentEntity document = entity.getDocument();
			return documentToArxiuDto(nomDocumetnDefault, document);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getDocumentArxiu(
			Long notificacioId,
			Long documentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String nomDocumentDefault = "document";
			DocumentEntity document = documentRepository.findOne(documentId);
//			DocumentEntity document = documentRepository.findByNotificacioIdAndId(notificacioId, documentId);
			return documentToArxiuDto(nomDocumentDefault, document);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private ArxiuDto documentToArxiuDto(String nomDocumetnDefault, DocumentEntity document) {
		if (document == null)
			return null;
		if(document.getArxiuGestdocId() != null) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(
					document.getArxiuGestdocId(),
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					output);
			return new ArxiuDto(
					document.getArxiuNom() != null ? document.getArxiuNom() : nomDocumetnDefault,
					null,
					output.toByteArray(),
					output.size());
		}else if(document.getUuid() != null){
			DocumentContingut dc = pluginHelper.arxiuGetImprimible(document.getUuid(), true);
			return new ArxiuDto(
					document.getArxiuNom() != null ? document.getArxiuNom() : nomDocumetnDefault,
					dc.getTipusMime(),
					dc.getContingut(),
					dc.getTamany());
		}else if(document.getCsv() != null){
			DocumentContingut dc = pluginHelper.arxiuGetImprimible(document.getCsv(), false);
			return new ArxiuDto(
					document.getArxiuNom() != null ? document.getArxiuNom() : nomDocumetnDefault,
					dc.getTipusMime(),
					dc.getContingut(),
					dc.getTamany());
		}else if(document.getUrl() != null){
			try {
				byte[] contingut = downloadUsingStream(document.getUrl(), "document");
				return new ArxiuDto(
						document.getArxiuNom() != null ? document.getArxiuNom() : nomDocumetnDefault,
						"PDF",
						contingut,
						contingut.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto enviamentGetCertificacioArxiu(
			Long enviamentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			NotificacioEnviamentEntity enviament =
					notificacioEnviamentRepository.findOne(enviamentId);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(
					enviament.getNotificaCertificacioArxiuId(),
					PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
					output);
			return new ArxiuDto(
					calcularNomArxiuCertificacio(enviament),
					enviament.getNotificaCertificacioMime(),
					output.toByteArray(),
					output.size());
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public boolean enviar(Long notificacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Intentant enviament de la notificació pendent (" +
					"notificacioId=" + notificacioId + ")");
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
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(
			Long entitatId, 
			Long enviamentId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Refrescant l'estat de la notificació de Notific@ (enviamentId=" + enviamentId + ")");
			NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId);
//			enviament.setNotificacio(notificacioRepository.findById(enviament.getNotificacio().getId()));
			notificaHelper.enviamentRefrescarEstat(enviament.getId());
			NotificacioEnviamenEstatDto estatDto = conversioTipusHelper.convertir(
					enviament,
					NotificacioEnviamenEstatDto.class);
			estatCalcularCampsAddicionals(
					enviament,
					estatDto);
			return estatDto;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public String marcarComProcessada(
			Long notificacioId,
			String motiu,
			boolean isAdministrador) throws Exception {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Refrescant l'estat de la notificació a PROCESSAT (" +
					"notificacioId=" + notificacioId + ")");		
			String resposta = null;
			NotificacioEntity notificacioEntity = entityComprovarHelper.comprovarNotificacio(
					null,
					notificacioId);
			if (!NotificacioEstatEnumDto.FINALITZADA.equals(notificacioEntity.getEstat())) {
				throw new Exception("La notificació no es pot marcar com a processada, no esta en estat finalitzada.");
			}
			if (!isAdministrador && !hasPermisNotificacio(notificacioEntity)) {
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
	private boolean hasPermisNotificacio(NotificacioEntity notificacio) {
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
			ProcSerOrganEntity procedimentOrganEntity = procedimentOrganRepository.findByProcSerIdAndOrganGestorId(
					procedimentNotificacio.getId(),
					notificacio.getOrganGestor().getId());
			hasPermis = entityComprovarHelper.hasPermisProcedimentOrgan(
					procedimentOrganEntity,
					PermisEnum.PROCESSAR);
		}

		return hasPermis;
	}
	@Transactional
	@Override
	public boolean reactivarConsulta(Long notificacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Reactivant consultes d'estat de la notificació (notificacioId=" + notificacioId + ")");
			NotificacioEntity notificacio = entityComprovarHelper.comprovarNotificacio(
					null,
					notificacioId);
	//			List<NotificacioEnviamentEntity> enviamentsEntity = notificacioEnviamentRepository.findByNotificacio(notificacio);
			for(NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
				auditEnviamentHelper.resetConsultaNotifica(enviament);
			}
			notificacioTableHelper.actualitzarRegistre(notificacio);
			notificacioRepository.saveAndFlush(notificacio);
			
			return true;
		} catch (Exception e) {
			logger.debug("Error reactivant consultes d'estat de la notificació (notificacioId=" + notificacioId + ")", e);
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
			logger.debug("Reactivant consultes d'estat de SIR (notificacioId=" + notificacioId + ")");
			NotificacioEntity notificacio = entityComprovarHelper.comprovarNotificacio(
					null,
					notificacioId);
			for(NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
				auditEnviamentHelper.resetConsultaSir(enviament);
			}
			notificacioTableHelper.actualitzarRegistre(notificacio);
			notificacioRepository.saveAndFlush(notificacio);
			return true;
		} catch (Exception e) {
			logger.debug("Error reactivant consultes a SIR de la notificació (notificacioId=" + notificacioId + ")", e);
			return false;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	// SCHEDULLED METHODS
	////////////////////////////////////////////////////////////////
	
	@SuppressWarnings("rawtypes")
	@Transactional
	@Override
	public List getNotificacionsPendentsRegistrar() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			int maxPendents = getRegistreEnviamentsProcessarMaxProperty();
			List<NotificacioEntity> pendents = notificacioRepository.findByNotificaEstatPendent(
					pluginHelper.getRegistreReintentsMaxProperty(),
					new PageRequest(0, maxPendents));
			return pendents;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	@Override
	public List getNotificacionsPendentsEnviar() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			int maxPendents = getNotificaEnviamentsProcessarMaxProperty();
			List<NotificacioEntity> pendents = notificacioRepository.findByNotificaEstatRegistradaAmbReintentsDisponibles(
					pluginHelper.getNotificaReintentsMaxProperty(), 
					new PageRequest(0, maxPendents));
			return pendents;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional
	@Override
	public void notificacioEnviar(Long notificacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			notificaHelper.notificacioEnviar(notificacioId);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Transactional
	@Override
	public List getNotificacionsPendentsRefrescarEstat() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			int maxPendents = getEnviamentActualitzacioEstatProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findByNotificaRefresc(
					pluginHelper.getConsultaReintentsMaxProperty(),
					new PageRequest(0, maxPendents));
			return pendents;
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
	
	@SuppressWarnings("rawtypes")
	@Transactional
	@Override
	public List getNotificacionsPendentsRefrescarEstatRegistre() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			int maxPendents = getEnviamentActualitzacioEstatRegistreProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findByRegistreRefresc(
					pluginHelper.getConsultaSirReintentsMaxProperty(),
					new PageRequest(0, maxPendents));
			return pendents;
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
	public PaginaDto<NotificacioDto> findNotificacionsAmbErrorRegistre(
			Long entitatId,
			NotificacioRegistreErrorFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		Page<NotificacioEntity> page = null;
		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			if (filtre == null || filtre.isEmpty()) {
				page = notificacioRepository.findByNotificaEstatPendentSenseReintentsDisponibles(
						entitatId,
						pluginHelper.getRegistreReintentsMaxProperty(),
						paginacioHelper.toSpringDataPageable(paginacioParams));
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
					procediment = procedimentRepository.findById(filtre.getProcedimentId());
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
	public List<Long> findNotificacionsIdAmbErrorRegistre(
			Long entitatId, 
			NotificacioRegistreErrorFiltreDto filtre) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		List<Long> ids = null;
		try {
			if (filtre == null || filtre.isEmpty()) {
				ids = notificacioRepository.findIdsByNotificaEstatPendentSenseReintentsDisponibles(
						entitatId,
						pluginHelper.getRegistreReintentsMaxProperty());
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
					procediment = procedimentRepository.findById(filtre.getProcedimentId());
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
			logger.debug("Reactivant registre de la notificació (notificacioId=" + notificacioId + ")");
			NotificacioEntity notificacio = entityComprovarHelper.comprovarNotificacio(
					null,
					notificacioId);
			auditNotificacioHelper.updateNotificacioRefreshRegistreNotificacio(notificacio);
		} catch (Exception e) {
			logger.debug("Error reactivant consultes d'estat de la notificació (notificacioId=" + notificacioId + ")", e);
		} finally {
			metricsHelper.fiMetrica(timer);
		}		
	}

	@Override
	public void refrescarEnviamentsExpirats() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			
			logger.debug("S'ha iniciat els procés d'actualització dels enviaments expirats");
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
	public List getNotificacionsDEHPendentsRefrescarCert() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			int maxPendents = getEnviamentDEHActualitzacioCertProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findByDEHAndEstatFinal(
					pluginHelper.getConsultaReintentsDEHMaxProperty(),
					new PageRequest(0, maxPendents));
			return pendents;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	@Override
	public List getNotificacionsCIEPendentsRefrescarCert() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			int maxPendents = getEnviamentCIEActualitzacioCertProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findByCIEAndEstatFinal(
					pluginHelper.getConsultaReintentsCIEMaxProperty(),
					new PageRequest(0, maxPendents));
			return pendents;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}


	private int getRegistreEnviamentsProcessarMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.registre.enviaments.processar.max");
	}
	private int getNotificaEnviamentsProcessarMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.notifica.enviaments.processar.max");
	}
	private int getEnviamentActualitzacioEstatProcessarMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.enviament.actualitzacio.estat.processar.max");
	}
	private int getEnviamentDEHActualitzacioCertProcessarMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.enviament.deh.actualitzacio.certificacio.processar.max");
	}
	private int getEnviamentCIEActualitzacioCertProcessarMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.enviament.cie.actualitzacio.certificacio.processar.max");
	}
	private int getEnviamentActualitzacioEstatRegistreProcessarMaxProperty() {
		return configHelper.getAsInt("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.processar.max");
	}

	private int getMidaMinIdCsv() {
		return configHelper.getAsInt("es.caib.notib.document.consulta.id.csv.mida.min");
	}
	
	
	private void estatCalcularCampsAddicionals(
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
				logger.error("Error obtenit l'event d'error de l'enviament " + enviament.getId(), ex);
			}
		}
		estatDto.setNotificaCertificacioArxiuNom(
				calcularNomArxiuCertificacio(enviament));
	}

	private String calcularNomArxiuCertificacio(
			NotificacioEnviamentEntity enviament) {
		return "certificacio_" + enviament.getNotificaIdentificador() + ".pdf";
	}

	private byte[] downloadUsingStream(String urlStr, String file) throws IOException{
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
        return buffer;
    }
	
	public boolean validarIdCsv (String idCsv) {
		return idCsv.length() >= getMidaMinIdCsv() ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public boolean validarFormatCsv(String csv) {
		return csv.matches("^([0-9a-f]{64})$") || csv.matches("^([0-9a-zA-Z_]+)$");
	}

	public DocumentDto consultaDocumentIMetadades(String identificador, Boolean esUuid) {
		
		Document documentArxiu = new Document();
		
		try {
			if (esUuid)
				documentArxiu = pluginHelper.arxiuDocumentConsultar(identificador, null, true, true);
			else
				documentArxiu = pluginHelper.arxiuDocumentConsultar(identificador, null, true, false);
		} catch (Exception ex){
			logger.debug("S'ha produit un error obtenent els detalls del document con identificador: " + identificador, ex);
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
	


	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceImpl.class);

}