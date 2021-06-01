/**
 * 
 */
package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.ProgresActualitzacioCertificacioDto.TipusActInfo;
import es.caib.notib.core.api.dto.ProgresDescarregaDto.TipusInfo;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.core.api.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.core.api.exception.JustificantException;
import es.caib.notib.core.api.exception.MaxLinesExceededException;
import es.caib.notib.core.api.exception.NoDocumentException;
import es.caib.notib.core.api.exception.NoMetadadesException;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.exception.WriteCsvException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.api.ws.notificacio.OrigenEnum;
import es.caib.notib.core.api.ws.notificacio.Persona;
import es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum;
import es.caib.notib.core.api.ws.notificacio.ValidesaEnum;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.entity.auditoria.NotificacioAudit;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.DocumentRepository;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.NotificacioTableViewRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.PersonaRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.repository.auditoria.NotificacioAuditRepository;
import es.caib.notib.core.repository.auditoria.NotificacioEnviamentAuditRepository;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import javax.annotation.Resource;
import javax.mail.internet.InternetAddress;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.NoSuchFileException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private EmailHelper emailHelper;
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
	private JustificantHelper justificantHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private NotificacioHelper notificacioHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;
	@Autowired
	private OrganGestorCachable organGestorCachable;

	public static Map<String, ProgresDescarregaDto> progresDescarrega = new HashMap<String, ProgresDescarregaDto>();
	public static Map<String, ProgresActualitzacioCertificacioDto> progresActulitzacioExpirades = new HashMap<String, ProgresActualitzacioCertificacioDto>();
	
	
	@Transactional(rollbackFor=Exception.class)
	@Override
	public NotificacioDatabaseDto create(
			Long entitatId,
			NotificacioDatabaseDto notificacio,
			Map<String, Long> documentsProcessatsMassiu) throws RegistreNotificaException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);

			NotificacioHelper.NotificacioData notData = notificacioHelper.buildNotificacioData(entitat, notificacio, false, documentsProcessatsMassiu);
			// Dades generals de la notificació
			NotificacioEntity notificacioEntity = notificacioHelper.saveNotificacio(notData);
	
			List<Enviament> enviaments = new ArrayList<Enviament>();
			for(NotificacioEnviamentDtoV2 enviament: notificacio.getEnviaments()) {
				if (enviament.getEntregaPostal() != null && (enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty()))
					enviament.getEntregaPostal().setCodiPostal(enviament.getEntregaPostal().getCodiPostalNorm());
				enviaments.add(conversioTipusHelper.convertir(enviament, Enviament.class));
			}
			List<NotificacioEnviamentEntity> enviamentsCreats = new ArrayList<NotificacioEnviamentEntity>();
			for (Enviament enviament: enviaments) {
				if (enviament.getTitular() != null) {
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
					NotificaDomiciliNumeracioTipusEnumDto numeracioTipus = null;
					NotificaDomiciliConcretTipusEnumDto tipusConcret = null;
					if (enviament.isEntregaPostalActiva() && enviament.getEntregaPostal() != null) {
						if (enviament.getEntregaPostal().getTipus() != null) {
							switch (enviament.getEntregaPostal().getTipus()) {
							case APARTAT_CORREUS:
								tipusConcret = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS;
								break;
							case ESTRANGER:
								tipusConcret = NotificaDomiciliConcretTipusEnumDto.ESTRANGER;
								break;
							case NACIONAL:
								tipusConcret = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
								break;
							case SENSE_NORMALITZAR:
								tipusConcret = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;
								break;
							}
						}
						if (enviament.getEntregaPostal().getNumeroCasa() != null) {
							numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
						} else if (enviament.getEntregaPostal().getApartatCorreus() != null) {
							numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS;
						} else if (enviament.getEntregaPostal().getPuntKm() != null) {
							numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC;
						} else {
							numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO;
						}
					}
					PersonaEntity titular = personaRepository.saveAndFlush(PersonaEntity.getBuilderV2(
							enviament.getTitular().getInteressatTipus(),
							enviament.getTitular().getEmail(), 
							enviament.getTitular().getLlinatge1(), 
							enviament.getTitular().getLlinatge2(), 
							enviament.getTitular().getNif(), 
							enviament.getTitular().getNom(), 
							enviament.getTitular().getTelefon(),
							enviament.getTitular().getRaoSocial(),
							enviament.getTitular().getDir3Codi()
							).incapacitat(enviament.getTitular().isIncapacitat()).build());
					
					List<PersonaEntity> destinataris = new ArrayList<PersonaEntity>();
					if (enviament.getDestinataris() != null) {
						for(Persona persona: enviament.getDestinataris()) {
								if ((persona.getNif() != null && !persona.getNif().isEmpty()) || 
										(persona.getDir3Codi() != null && !persona.getDir3Codi().isEmpty())) {
									PersonaEntity destinatari = personaRepository.saveAndFlush(PersonaEntity.getBuilderV2(
										persona.getInteressatTipus(),
										persona.getEmail(), 
										persona.getLlinatge1(), 
										persona.getLlinatge2(), 
										persona.getNif(), 
										persona.getNom(), 
										persona.getTelefon(),
										persona.getRaoSocial(),
										persona.getDir3Codi()).incapacitat(false).build());
								destinataris.add(destinatari);
							}
						}
					}
					EntregaPostalViaTipusEnum viaTipus = null;
					
					if (enviament.getEntregaPostal() != null) {
						viaTipus = enviament.getEntregaPostal().getViaTipus();
					}
					// Rellenar dades enviament titular
					enviamentsCreats.add(auditEnviamentHelper.desaEnviament(
							entitat,
							notificacioEntity,
							enviament,
							serveiTipus,
							numeracioTipus,
							tipusConcret,
							titular,
							destinataris,
							viaTipus));
				}
			}
			notificacioEntity.getEnviaments().addAll(enviamentsCreats);

			// Comprovar on s'ha d'enviar ara
			if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(pluginHelper.getNotibTipusComunicacioDefecte())) {
				synchronized(CreacioSemaforDto.getCreacioSemafor()) {
					boolean notificar = registreNotificaHelper.realitzarProcesRegistrar(
							notificacioEntity);
					if (notificar) 
						notificaHelper.notificacioEnviar(notificacioEntity.getId());
				}
			}

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
			NotificacioHelper.NotificacioData notData = notificacioHelper.buildNotificacioData(entitat, notificacio, !isAdministradorEntitat, null);

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
			for(NotificacioEnviamentDtoV2 enviament: notificacio.getEnviaments()) {
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
				NotificaDomiciliNumeracioTipusEnumDto numeracioTipus = null;
				NotificaDomiciliConcretTipusEnumDto tipusConcret = null;
				if (enviament.isEntregaPostalActiva() && enviament.getEntregaPostal() != null) {
					if (enviament.getEntregaPostal().getTipus() != null) {
						switch (enviament.getEntregaPostal().getTipus()) {
						case APARTAT_CORREUS:
							tipusConcret = NotificaDomiciliConcretTipusEnumDto.APARTAT_CORREUS;
							break;
						case ESTRANGER:
							tipusConcret = NotificaDomiciliConcretTipusEnumDto.ESTRANGER;
							break;
						case NACIONAL:
							tipusConcret = NotificaDomiciliConcretTipusEnumDto.NACIONAL;
							break;
						case SENSE_NORMALITZAR:
							tipusConcret = NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR;
							break;
						}
					}
					if (enviament.getEntregaPostal().getNumeroCasa() != null) {
						numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
					} else if (enviament.getEntregaPostal().getApartatCorreus() != null) {
						numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.APARTAT_CORREUS;
					} else if (enviament.getEntregaPostal().getPuntKm() != null) {
						numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.PUNT_KILOMETRIC;
					} else {
						numeracioTipus = NotificaDomiciliNumeracioTipusEnumDto.SENSE_NUMERO;
					}
				}
				PersonaEntity titular = null;
				if (enviament.getTitular().getId() != null) {
					titular = personaRepository.findOne(enviament.getTitular().getId());
					titular.update(
							enviament.getTitular().getInteressatTipus(),
							enviament.getTitular().getEmail(),
							enviament.getTitular().getLlinatge1(),
							enviament.getTitular().getLlinatge2(),
							enviament.getTitular().getNif(),
							enviament.getTitular().getNom(),
							enviament.getTitular().getTelefon(),
							enviament.getTitular().getRaoSocial(),
							enviament.getTitular().getDir3Codi(),
							enviament.getTitular().isIncapacitat());
				} else {
					titular = personaRepository.saveAndFlush(PersonaEntity.getBuilderV2(
							enviament.getTitular().getInteressatTipus(),
							enviament.getTitular().getEmail(),
							enviament.getTitular().getLlinatge1(),
							enviament.getTitular().getLlinatge2(),
							enviament.getTitular().getNif(),
							enviament.getTitular().getNom(),
							enviament.getTitular().getTelefon(),
							enviament.getTitular().getRaoSocial(),
							enviament.getTitular().getDir3Codi()
							).incapacitat(enviament.getTitular().isIncapacitat()).build());
				}
				List<PersonaEntity> nousDestinataris = new ArrayList<PersonaEntity>();
//					### Crear o editar destinataris enviament existent
				if (enviament.getDestinataris() != null) {
					for(Persona destinatari: enviament.getDestinataris()) {
							if ((destinatari.getNif() != null && !destinatari.getNif().isEmpty()) ||
									(destinatari.getDir3Codi() != null && !destinatari.getDir3Codi().isEmpty())) {
								if (destinatari.getId() != null) {
									destinatarisIds.add(destinatari.getId());
									PersonaEntity destinatariEntity = personaRepository.findOne(destinatari.getId());
									destinatariEntity.update(
											destinatari.getInteressatTipus(),
											destinatari.getEmail(),
											destinatari.getLlinatge1(),
											destinatari.getLlinatge2(),
											destinatari.getNif(),
											destinatari.getNom(),
											destinatari.getTelefon(),
											destinatari.getRaoSocial(),
											destinatari.getDir3Codi(),
											false);
								} else {
									PersonaEntity destinatariEntity = personaRepository.saveAndFlush(PersonaEntity.getBuilderV2(
											destinatari.getInteressatTipus(),
											destinatari.getEmail(),
											destinatari.getLlinatge1(),
											destinatari.getLlinatge2(),
											destinatari.getNif(),
											destinatari.getNom(),
											destinatari.getTelefon(),
											destinatari.getRaoSocial(),
											destinatari.getDir3Codi()).incapacitat(false).build());
									nousDestinataris.add(destinatariEntity);
									destinatarisIds.add(destinatariEntity.getId());
								}
						}
					}
				}
				EntregaPostalViaTipusEnum viaTipus = null;

				if (enviament.getEntregaPostal() != null) {
					viaTipus = enviament.getEntregaPostal().getViaTipus();
				}
//					### Actualitzar les dades d'un enviament existent o crear un de nou
				if (enviament.getId() != null) {
					NotificacioEnviamentEntity enviamentEntity = auditEnviamentHelper.updateEnviament(
							entitat,
							notificacioEntity,
							enviament,
							serveiTipus,
							numeracioTipus,
							tipusConcret,
							titular,
							viaTipus);
					enviamentEntity.getDestinataris().addAll(nousDestinataris);
				} else {
					NotificacioEnviamentEntity nouEnviament = auditEnviamentHelper.desaEnviament(
							entitat,
							notificacioEntity,
							enviament,
							serveiTipus,
							numeracioTipus,
							tipusConcret,
							titular,
							nousDestinataris,
							viaTipus);
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
			
			entityComprovarHelper.comprovarPermisos(
					null,
					false,
					false,
					false);
			
			if(notificacio != null) {
				if (notificacio.getProcediment() != null && notificacio.getEstat() != NotificacioEstatEnumDto.PROCESSADA) {
					notificacio.setPermisProcessar(
							entityComprovarHelper.hasPermisProcediment(
									notificacio.getProcediment().getId(),
									PermisEnum.PROCESSAR));
				}
				
				List<NotificacioEnviamentEntity> enviamentsPendentsNotifica = notificacioEnviamentRepository.findEnviamentsPendentsNotificaByNotificacio(notificacio);
				if (enviamentsPendentsNotifica != null && !enviamentsPendentsNotifica.isEmpty()) {
					notificacio.setHasEnviamentsPendents(true);
				}
				
				pluginHelper.addOficinaAndLlibreRegistre(notificacio);
			}
			
			return conversioTipusHelper.convertir(
					notificacio,
					NotificacioDtoV2.class);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<NotificacioTableItemDto> findAmbFiltrePaginat(
			Long entitatId,
			RolEnumDto rol,
			List<String> procedimentsCodisNotib,
			List<String> codisOrgansGestorsDisponibles,
			List<Long> codisProcedimentOrgansDisponibles,
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
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("procediment.organGestor", new String[] {"pro.organGestor.codi"});
			mapeigPropietatsOrdenacio.put("organGestorDesc", new String[] {"organCodi"});
			mapeigPropietatsOrdenacio.put("procediment.nom", new String[] {"procedimentNom"});
			mapeigPropietatsOrdenacio.put("procedimentDesc", new String[] {"procedimentCodi"});
			mapeigPropietatsOrdenacio.put("createdByComplet", new String[] {"createdBy"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);

			boolean esProcedimentsCodisNotibNull = (procedimentsCodisNotib == null || procedimentsCodisNotib.isEmpty());
			boolean esOrgansGestorsCodisNotibNull = (codisOrgansGestorsDisponibles == null || codisOrgansGestorsDisponibles.isEmpty());
			boolean esProcedimentsOrgansCodisNotibNull = (codisProcedimentOrgansDisponibles == null || codisProcedimentOrgansDisponibles.isEmpty());

			if (filtre == null || filtre.isEmpty()) {
				//Consulta les notificacions sobre les quals té permis l'usuari actual
				if (isUsuari) {
					notificacions = notificacioTableViewRepository.findByProcedimentCodiNotibAndGrupsCodiNotibAndEntitat(
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : procedimentsCodisNotib,
							aplicacioService.findRolsUsuariActual(),
							esOrgansGestorsCodisNotibNull,
							esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
							esProcedimentsOrgansCodisNotibNull,
							esProcedimentsOrgansCodisNotibNull ? null : codisProcedimentOrgansDisponibles,
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
							esProcedimentsCodisNotibNull ? null : procedimentsCodisNotib,
							entitatActual,
							organs,
							pageable);
				}
			} else {
				Date dataInici = toIniciDia(filtre.getDataInici());
				Date dataFi = toFiDia(filtre.getDataFi());
				OrganGestorEntity organGestor = null;
				if (filtre.getOrganGestor() != null && !filtre.getOrganGestor().isEmpty()) {
					organGestor = organGestorRepository.findOne(Long.parseLong(filtre.getOrganGestor()));
				}
				ProcedimentEntity procediment = null;
				if (filtre.getProcedimentId() != null) {
					procediment = procedimentRepository.findById(filtre.getProcedimentId());
				}
				NotificacioEstatEnumDto estat = filtre.getEstat();
				Boolean hasZeronotificaEnviamentIntent = null;
				boolean isEstatNull = estat == null;
				boolean nomesSenseErrors = false;
				boolean nomesAmbErrors = filtre.isNomesAmbErrors();
				if (!isEstatNull && estat.equals(NotificacioEstatEnumDto.ENVIANT)) {
					estat = NotificacioEstatEnumDto.PENDENT;
					hasZeronotificaEnviamentIntent = true;
					nomesSenseErrors = true;

				} else if (!isEstatNull && estat.equals(NotificacioEstatEnumDto.PENDENT)) {
					hasZeronotificaEnviamentIntent = false;
//					nomesAmbErrors = true;
				}

				if (isUsuari) {
					notificacions = notificacioTableViewRepository.findAmbFiltreAndProcedimentCodiNotibAndGrupsCodiNotib(
							entitatActual,
							filtre.getEntitatId() == null,
							filtre.getEntitatId(),
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : procedimentsCodisNotib,
							aplicacioService.findRolsUsuariActual(),
							esOrgansGestorsCodisNotibNull,
							esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
							esProcedimentsOrgansCodisNotibNull,
							esProcedimentsOrgansCodisNotibNull ? null : codisProcedimentOrgansDisponibles,
							filtre.getEnviamentTipus() == null,
							filtre.getEnviamentTipus(),
							filtre.getConcepte() == null,
							filtre.getConcepte() == null ? "" : filtre.getConcepte(),
							isEstatNull,
							estat,
							estat != null ? NotificacioEnviamentEstatEnumDto.valueOf(estat.toString()) : null,
							dataInici == null,
							dataInici,
							dataFi == null,
							dataFi,
							filtre.getTitular() == null || filtre.getTitular().isEmpty(),
							filtre.getTitular() == null ? "" : filtre.getTitular(),
							organGestor == null,
							organGestor == null ? "" : organGestor.getCodi(),
							procediment == null,
							procediment == null ? "" : procediment.getCodi(),
							filtre.getTipusUsuari() == null,
							filtre.getTipusUsuari(),
							filtre.getNumExpedient() == null || filtre.getNumExpedient().isEmpty(),
							filtre.getNumExpedient(),
							filtre.getCreadaPer() == null || filtre.getCreadaPer().isEmpty(),
							filtre.getCreadaPer(),
							filtre.getIdentificador() == null || filtre.getIdentificador().isEmpty(),
							filtre.getIdentificador(),
							usuariCodi,
							nomesAmbErrors,
							nomesSenseErrors,
							hasZeronotificaEnviamentIntent == null,
							hasZeronotificaEnviamentIntent,
							pageable);
				} else if (isUsuariEntitat) {
					notificacions = notificacioTableViewRepository.findAmbFiltre(
							entitatId == null,
							entitatId,
							filtre.getEnviamentTipus() == null,
							filtre.getEnviamentTipus(),
							filtre.getConcepte() == null,
							filtre.getConcepte(),
							isEstatNull,
							estat,
							estat != null ? NotificacioEnviamentEstatEnumDto.valueOf(estat.toString()) : null,
							dataInici == null,
							dataInici,
							dataFi == null,
							dataFi,
							filtre.getTitular() == null || filtre.getTitular().isEmpty(),
							filtre.getTitular(),
							organGestor == null,
							organGestor == null ? "" : organGestor.getCodi(),
							procediment == null,
							procediment == null ? "" : procediment.getCodi(),
							filtre.getTipusUsuari() == null,
							filtre.getTipusUsuari(),
							filtre.getNumExpedient() == null || filtre.getNumExpedient().isEmpty(),
							filtre.getNumExpedient(),
							filtre.getCreadaPer() == null || filtre.getCreadaPer().isEmpty(),
							filtre.getCreadaPer(),
							filtre.getIdentificador() == null || filtre.getIdentificador().isEmpty(),
							filtre.getIdentificador(),
							nomesAmbErrors,
							nomesSenseErrors,
							hasZeronotificaEnviamentIntent == null,
							hasZeronotificaEnviamentIntent,
							pageable);
				} else if (isSuperAdmin) {
					notificacions = notificacioTableViewRepository.findAmbFiltre(
							filtre.getEntitatId() == null,
							filtre.getEntitatId(),
							filtre.getEnviamentTipus() == null,
							filtre.getEnviamentTipus(),
							filtre.getConcepte() == null || filtre.getConcepte().isEmpty(),
							filtre.getConcepte(),
							isEstatNull,
							estat,
							estat != null ? NotificacioEnviamentEstatEnumDto.valueOf(estat.toString()) : null,
							dataInici == null,
							dataInici,
							dataFi == null,
							dataFi,
							filtre.getTitular() == null || filtre.getTitular().isEmpty(),
							filtre.getTitular(),
							organGestor == null,
							organGestor == null ? "" : organGestor.getCodi(),
							procediment == null,
							procediment == null ? "" : procediment.getCodi(),
							filtre.getTipusUsuari() == null,
							filtre.getTipusUsuari(),
							filtre.getNumExpedient() == null || filtre.getNumExpedient().isEmpty(),
							filtre.getNumExpedient(),
							filtre.getCreadaPer() == null || filtre.getCreadaPer().isEmpty(),
							filtre.getCreadaPer(),
							filtre.getIdentificador() == null || filtre.getIdentificador().isEmpty(),
							filtre.getIdentificador(),
							nomesAmbErrors,
							nomesSenseErrors,
							hasZeronotificaEnviamentIntent == null,
							hasZeronotificaEnviamentIntent,
							pageable);
				} else if (isAdminOrgan) {
					List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi);
					notificacions = notificacioTableViewRepository.findAmbFiltreAndProcedimentCodiNotib(
							entitatActual,
							filtre.getEntitatId() == null,
							filtre.getEntitatId(),
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : procedimentsCodisNotib,
							filtre.getEnviamentTipus() == null,
							filtre.getEnviamentTipus(),
							filtre.getConcepte() == null,
							filtre.getConcepte() == null ? "" : filtre.getConcepte(),
							isEstatNull,
							estat,
							estat != null ? NotificacioEnviamentEstatEnumDto.valueOf(estat.toString()) : null,
							dataInici == null,
							dataInici,
							dataFi == null,
							dataFi,
							filtre.getTitular() == null || filtre.getTitular().isEmpty(),
							filtre.getTitular() == null ? "" : filtre.getTitular(),
							organGestor == null,
							organGestor == null ? "" : organGestor.getCodi(),
							procediment == null,
							procediment == null ? "" : procediment.getCodi(),
							filtre.getTipusUsuari() == null,
							filtre.getTipusUsuari(),
							filtre.getNumExpedient() == null || filtre.getNumExpedient().isEmpty(),
							filtre.getNumExpedient(),
							filtre.getCreadaPer() == null || filtre.getCreadaPer().isEmpty(),
							filtre.getCreadaPer(),
							filtre.getIdentificador() == null || filtre.getIdentificador().isEmpty(),
							filtre.getIdentificador(),
							organs,
							nomesSenseErrors,
							hasZeronotificaEnviamentIntent == null,
							hasZeronotificaEnviamentIntent,
							pageable);
				}
			}

			return complementaNotificacions(entitatActual, usuariCodi, notificacions);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private Date toIniciDia(Date data) {
		if (data != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			data = cal.getTime();
		}
		return data;
	}
	
	private Date toFiDia(Date data) {
		if (data != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(data);
			cal.set(Calendar.HOUR, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			data = cal.getTime();
		}
		return data;
	}
	
	private PaginaDto<NotificacioTableItemDto> complementaNotificacions(
			EntitatEntity entitatEntity,
			String usuariCodi,
			Page<NotificacioTableEntity> notificacions) {

		if (notificacions == null) {
			return paginacioHelper.getPaginaDtoBuida(NotificacioTableItemDto.class);
		}

		List<String> codisProcedimentsProcessables = new ArrayList<String>();
		List<ProcedimentDto> procedimentsProcessables = procedimentService.findProcedimentsWithPermis(entitatEntity.getId(),
				usuariCodi, PermisEnum.PROCESSAR);
		if (procedimentsProcessables != null)
			for (ProcedimentDto procediment : procedimentsProcessables) {
				codisProcedimentsProcessables.add(procediment.getCodi());
			}
		List<ProcedimentOrganDto> procedimentOrgansProcessables = procedimentService.findProcedimentsOrganWithPermis(entitatEntity.getId(), usuariCodi, PermisEnum.PROCESSAR);
		if (procedimentOrgansProcessables != null) {
			for (ProcedimentOrganDto procedimentOrgan : procedimentOrgansProcessables) {
				codisProcedimentsProcessables.add(procedimentOrgan.getProcediment().getCodi());
			}
		}

		for (NotificacioTableEntity notificacio : notificacions) {
			if (notificacio.getProcedimentCodi() != null && notificacio.getEstat() != NotificacioEstatEnumDto.PROCESSADA) {
				notificacio.setPermisProcessar(codisProcedimentsProcessables.contains(notificacio.getProcedimentCodi()));
			}

			List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacio.getId());
			if (enviamentsPendents != null && !enviamentsPendents.isEmpty()) {
				notificacio.setHasEnviamentsPendentsRegistre(true);
			}
		}

		return paginacioHelper.toPaginaDto(
				notificacions,
				NotificacioTableItemDto.class);
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
			logger.info("Intentant registrar la notificació pendent (notificacioId=" + notificacioId + ")");
			List<RegistreIdDto> registresIdDto = new ArrayList<RegistreIdDto>();
			NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioId);
			logger.info(" [REG] Inici registre notificació [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");

			long startTime = System.nanoTime();
			double elapsedTime;
			synchronized(CreacioSemaforDto.getCreacioSemafor()) {
				logger.info("Comprovant estat actual notificació (id: " + notificacioEntity.getId() + ")...");
				NotificacioEstatEnumDto estatActual = notificacioEntity.getEstat();
				logger.info("Estat notificació [Id:" + notificacioEntity.getId() + ", Estat: "+ estatActual + "]");
				
				if (estatActual.equals(NotificacioEstatEnumDto.PENDENT)) {
					long startTime2 = System.nanoTime();
					boolean notificar = registreNotificaHelper.realitzarProcesRegistrar(notificacioEntity);
					elapsedTime = (System.nanoTime() - startTime2) / 10e6;
					logger.info(" [TIMER-REG] Realitzar procés registrar [Id: " + notificacioEntity.getId() + "]: " + elapsedTime + " ms");
					if (notificar){
						startTime2 = System.nanoTime();
						notificaHelper.notificacioEnviar(notificacioEntity.getId());
						elapsedTime = (System.nanoTime() - startTime2) / 10e6;
						logger.info(" [TIMER-REG] Notificació enviar [Id: " + notificacioEntity.getId() + "]: " + elapsedTime + " ms");
					}
				}
			}
			elapsedTime = (System.nanoTime() - startTime) / 10e6;
			logger.info(" [TIMER-REG] Temps global registrar notificar amb esperes concurrents [Id: " + notificacioEntity.getId() + "]: " + elapsedTime + " ms");
			logger.info(" [REG] Fi registre notificació [Id: " + notificacioEntity.getId() + ", Estat: " + notificacioEntity.getEstat() + "]");
			return registresIdDto;
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
			String motiu) throws Exception {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Refrescant l'estat de la notificació a PROCESSAT (" +
					"notificacioId=" + notificacioId + ")");		
			String resposta = null;
			NotificacioEntity notificacioEntity = entityComprovarHelper.comprovarNotificacio(
					null,
					notificacioId); 
			notificacioEntity = auditNotificacioHelper.updateNotificacioProcessada(notificacioEntity, motiu);
			UsuariEntity usuari = usuariHelper.getUsuariAutenticat();
			if(usuari != null && notificacioEntity.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB) {
				if(!usuari.isRebreEmailsNotificacioCreats() || usuari.getCodi() == notificacioEntity.getCreatedBy().getCodi()) {
					resposta = emailHelper.prepararEnvioEmailNotificacio(notificacioEntity);
				}
			}
			
			notificacioRepository.saveAndFlush(notificacioEntity);
			
			return resposta;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
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
	
	@Transactional
	@Override
	public void notificacioRegistrar(Long notificacioId) throws RegistreNotificaException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			registrarNotificar(notificacioId);
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
			IntegracioInfo info = new IntegracioInfo(
					IntegracioHelper.INTCODI_NOTIFICA,
					"Actualització d'enviaments expirats sense certificació",
					IntegracioAccioTipusEnumDto.PROCESSAR,
					new AccioParam("Usuari encarregat: ", username));
			
			ProgresActualitzacioCertificacioDto progres = progresActulitzacioExpirades.get(username);
			if (progres != null && progres.getProgres() != 0) {
				progres.addInfo(TipusActInfo.ERROR, "Existeix un altre procés en progrés...");
			} else {
				progres = new ProgresActualitzacioCertificacioDto();
				progresActulitzacioExpirades.put(username, progres);
				List<Long> enviamentsIds = notificacioEnviamentRepository.findIdExpiradesAndNotificaCertificacioDataNull();
				if (enviamentsIds == null || enviamentsIds.isEmpty()) {
					progres.setProgres(100);
					String msgInfoEnviamentsEmpty = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.empty");
					progres.addInfo(TipusActInfo.WARNING, msgInfoEnviamentsEmpty);
					info.getParams().add(new AccioParam("Msg. Títol:", msgInfoEnviamentsEmpty));
				} else {
					String msgInfoInici = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.inici");
					progres.setNumEnviamentsExpirats(enviamentsIds.size());
					progres.addInfo(TipusActInfo.TITOL, msgInfoInici);
					info.getParams().add(new AccioParam("Msg. Títol:", msgInfoInici));
					for (Long enviamentId : enviamentsIds) {
						progres.incrementProcedimentsActualitzats();
						try {
							notificacioHelper.enviamentRefrescarEstat(
									enviamentId, 
									progres, 
									info);	
						} catch (Exception ex) {
							progres.addInfo(TipusActInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant.ko", new Object[] {enviamentId}));
							logger.error("No s'ha pogut refrescar l'estat de l'enviament (enviamentId=" + enviamentId + ")", ex);
						}
					}
				}
				integracioHelper.addAccioOk(info);
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public ProgresActualitzacioCertificacioDto actualitzacioEnviamentsEstat() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ProgresActualitzacioCertificacioDto progres = progresActulitzacioExpirades.get(auth.getName());
			if (progres != null && progres.getProgres() != null &&  progres.getProgres() >= 100) {
				progresActulitzacioExpirades.remove(auth.getName());
			}
			return progres;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	
	@Transactional
	@Override
	public FitxerDto recuperarJustificant(
			Long notificacioId,
			Long entitatId,
			String sequence) throws JustificantException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
			List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacio.getId());
			
			if (enviamentsPendents != null && !enviamentsPendents.isEmpty()) 
				throw new ValidationException("No es pot generar el justificant d'una notificació amb enviaments pendents.");
			
			entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false,
					true, 
					true, 
					false);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ProgresDescarregaDto progres = progresDescarrega.get(auth.getName() + "_" + sequence);
			
			if (progres != null && progres.getProgres() != 0) {
				logger.error("Ja existeix un altre procés iniciat"); 
				progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant"));
				return null;
			} else {
				//## Únic procés per usuari per evitar sobrecàrrega
				progres = new ProgresDescarregaDto();
				progresDescarrega.put(auth.getName() + "_" + sequence, progres);
				
				//## GENERAR JUSTIFICANT
				logger.debug("Recuperant el justificant de la notificacio (notificacioId=" + notificacioId + ")");
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.generant"));
				byte[] contingut = justificantHelper.generarJustificant(
						conversioTipusHelper.convertir(
								notificacio, 
								NotificacioDtoV2.class),
						progres);
				FitxerDto justificantOriginal = new FitxerDto();
				justificantOriginal.setNom("justificant_notificació_" + notificacio.getId() + ".pdf");
				justificantOriginal.setContentType("application/pdf");
				justificantOriginal.setContingut(contingut);
				
				//## FIRMA EN SERVIDOR
				progres.setProgres(80);
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.aplicant.firma"));
				byte[] contingutFirmat = null;
				try {
					contingutFirmat = pluginHelper.firmaServidorFirmar(
							notificacio, 
							justificantOriginal, 
							TipusFirma.PADES, 
							"justificant enviament Notib", 
							"ca");
					progres.setProgres(100);
				} catch (Exception ex) {
					progres.setProgres(100);
					String errorDescripcio = messageHelper.getMessage("es.caib.notib.justificant.proces.aplicant.firma.error");
					progres.addInfo(TipusInfo.ERROR, errorDescripcio);
					logger.error(errorDescripcio, ex);
					progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.finalitzat"));
					return justificantOriginal;
				}
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("es.caib.notib.justificant.proces.finalitzat.firma"));
				FitxerDto justificantFirmat = new FitxerDto();
				justificantFirmat.setContentType("application/pdf");
				justificantFirmat.setContingut(contingutFirmat);
				justificantFirmat.setNom("justificant_notificació_" + notificacio.getId() + "_firmat.pdf");
				justificantFirmat.setTamany(contingutFirmat.length);
				return justificantFirmat;
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Override
	public ProgresDescarregaDto justificantEstat(String sequence) throws JustificantException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ProgresDescarregaDto progres = progresDescarrega.get(auth.getName() + "_" + sequence);
			if (progres != null && progres.getProgres() != null &&  progres.getProgres() >= 100) {
				progresDescarrega.remove(auth.getName());
			}
			return progres;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	private int getRegistreEnviamentsProcessarMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.tasca.registre.enviaments.processar.max",
				10);
	}
	private int getNotificaEnviamentsProcessarMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.tasca.notifica.enviaments.processar.max",
				10);
	}
	private int getEnviamentActualitzacioEstatProcessarMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.tasca.enviament.actualitzacio.estat.processar.max",
				10);
	}
	private int getEnviamentActualitzacioEstatRegistreProcessarMaxProperty() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.tasca.enviament.actualitzacio.estat.registre.processar.max",
				10);
	}
	
		
	
	
	private void estatCalcularCampsAddicionals(
			NotificacioEnviamentEntity enviament,
			NotificacioEnviamenEstatDto estatDto) {
		if (enviament.isNotificaError()) {
			NotificacioEventEntity event = enviament.getNotificacioErrorEvent();
			if (event != null) {
				estatDto.setNotificaErrorData(event.getData());
				estatDto.setNotificaErrorDescripcio(event.getErrorDescripcio());
			}
		}
		estatDto.setNotificaCertificacioArxiuNom(
				calcularNomArxiuCertificacio(enviament));
	}

	private String calcularNomArxiuCertificacio(
			NotificacioEnviamentEntity enviament) {
		return "certificacio_" + enviament.getNotificaIdentificador() + ".pdf";
	}
	
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String guardarArxiuTemporal(String contigut) {
		String documentGesdocId = null;
		try {
			if(contigut != null) {
				documentGesdocId = pluginHelper.gestioDocumentalCreate(
						PluginHelper.GESDOC_AGRUPACIO_TEMPORALS,
						Base64.decodeBase64(contigut));
			}
		} catch (Exception ex) {
			logger.error(
					"Error al guardar l'arxiu temporal " + ex);
		} 
		return documentGesdocId;
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public byte[] obtenirArxiuTemporal(String arxiuGestdocId) {
		return consultaArxiuGestioDocumental(arxiuGestdocId, PluginHelper.GESDOC_AGRUPACIO_TEMPORALS);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public byte[] obtenirArxiuNotificacio(String arxiuGestdocId) {
		return consultaArxiuGestioDocumental(arxiuGestdocId, PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS);
	}

	private byte[] consultaArxiuGestioDocumental(String arxiuGestdocId, String agrupacio) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			if(arxiuGestdocId != null) {
				pluginHelper.gestioDocumentalGet(
						arxiuGestdocId,
						agrupacio,
						output);
			}
		} catch (Exception ex) {
			logger.error("Error al recuperar l'arxiu de l'agrupació: " + agrupacio);
			throw ex;
		}
		return output.toByteArray();
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
	
	private int getMidaMinIdCsv() {
		return PropertiesHelper.getProperties().getAsInt(
				"es.caib.notib.document.consulta.id.csv.mida.min", 16);
	}
	
	@Transactional
	@Override
	public byte[] getModelDadesCarregaMassiuCSV() throws NoSuchFileException, IOException{
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			InputStream input;
			if (registreNotificaHelper.isSendDocumentsActive()) {
				input = this.getClass().getClassLoader().getResourceAsStream("es/caib/notib/core/plantillas/modelo_datos_carga_masiva_metadades.csv");
			} else {
				input = this.getClass().getClassLoader().getResourceAsStream("es/caib/notib/core/plantillas/modelo_datos_carga_masiva.csv");
			}
			return IOUtils.toByteArray(input);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Transactional(rollbackFor=Exception.class)
	@Override
	public void createMassiu(
			EntitatDto entitat, 
			String usuariCodi, 
			NotificacioMassiuDto notificacioMassiu) throws RegistreNotificaException {
 
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<String[]> linies = readCSV(notificacioMassiu.getFicheroCsvBytes());
			
			if (linies.size() > 999) {
				logger.debug("El fitxer CSV conté més de les 999 línies permeses.");
				throw new MaxLinesExceededException(
						"S'ha superat el màxim nombre de línies permès (999) per al CSV de càrrega massiva.");
			}
			List<String> fileNames = readZipFileNames(notificacioMassiu.getFicheroZipBytes());
			Map<String, Long> documentsProcessatsMassiu = new HashMap<String, Long>(); // key: csv/uuid/arxiuFisicoNom - value: documentEntity.getId()
			
			// TODO: ver dónde colocar los ficheros generados
			ICsvListWriter listWriterErrors = writeCsvHeader("C:\\Users\\Esther\u0020\u0020Puente\\dades\\notib-fs\\informeErrors.csv");
			ICsvListWriter listWriterInforme = writeCsvHeader("C:\\Users\\Esther\u0020\u0020Puente\\dades\\notib-fs\\informe.csv");
			
			for (String[] linia : linies) {
				NotificacioDatabaseDto notificacio = csvToNotificaDatabaseDto(
						linia, 
						notificacioMassiu.getCaducitat(), 
						entitat, 
						usuariCodi, 
						fileNames,
						notificacioMassiu.getFicheroZipBytes(),
						documentsProcessatsMassiu);
				
				if (notificacio.getDocument().getContingutBase64() != null && !notificacio.getDocument().getContingutBase64().isEmpty()) {//arxiu
					if (!documentsProcessatsMassiu.containsKey(notificacio.getDocument().getArxiuNom())) {
						documentsProcessatsMassiu.put(notificacio.getDocument().getArxiuNom(), null);
					}
				} else if (notificacio.getDocument().getUuid() != null) {
					if (!documentsProcessatsMassiu.containsKey(notificacio.getDocument().getUuid())) {
						documentsProcessatsMassiu.put(notificacio.getDocument().getUuid(), null);
					}
				} else if (notificacio.getDocument().getCsv() != null) {
					if (!documentsProcessatsMassiu.containsKey(notificacio.getDocument().getCsv())) {
						documentsProcessatsMassiu.put(notificacio.getDocument().getCsv(), null);
					}
				}
				
				List<String> errors = validarNotificacioMassiu(
						notificacio, entitat, linia,
						documentsProcessatsMassiu);
				try {
					ProcedimentDto procediment = procedimentService.findByCodi(entitat.getId(), notificacio.getProcediment().getCodi());
					notificacio.setProcediment(procediment);
				} catch (NotFoundException e) {
					errors.add("[1330] No s'ha trobat cap procediment amb el codi indicat.");
				}
				
				if (errors == null || errors.size() == 0) {
					try {
						create(entitat.getId(), notificacio, documentsProcessatsMassiu);
					} catch (NoDocumentException ex) {
						errors.add("[1064] No s'ha pogut obtenir el document de l'arxiu.");
					} catch (NoMetadadesException ex) {
						errors.add("[1066] Error en les metadades del document. No s'han obtingut de la consulta a l'arxiu ni de el fitxer CSV de càrrega.");
					}
				}
				
				if (errors != null && errors.size() > 0) {
					writeCsvLinia(listWriterErrors,linia, errors);
					writeCsvLinia(listWriterInforme,linia, errors);
				} else {
					List<String> ok = new ArrayList<String>();
					ok.add("OK");
					writeCsvLinia(listWriterInforme,linia, ok);
				}
				
			}
			writeCsvClose(listWriterErrors);
			writeCsvClose(listWriterInforme);
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private NotificacioDatabaseDto csvToNotificaDatabaseDto(String[] linia, Date caducitat, EntitatDto entitat, 
			String usuariCodi, List<String> fileNames, byte[] ficheroZipBytes, 
			Map<String, Long> documentsProcessatsMassiu) {
		NotificacioDatabaseDto notificacio = new NotificacioDatabaseDto();
		NotificacioEnviamentDtoV2 enviament = new NotificacioEnviamentDtoV2();
		List<NotificacioEnviamentDtoV2> enviaments = new ArrayList<NotificacioEnviamentDtoV2>();
		DocumentDto document = new DocumentDto();
		
		notificacio.setCaducitat(caducitat);
		notificacio.setOrganGestorCodi(linia[0]); 
		notificacio.setEmisorDir3Codi(entitat.getDir3Codi());
		notificacio.setConcepte(linia[1]);
		notificacio.setDescripcio(null); 
		notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.toEnum(linia[2].toUpperCase()));
		notificacio.setGrup(null); 
		notificacio.setIdioma(null); 
		notificacio.setNumExpedient(null); 
		notificacio.setUsuariCodi(usuariCodi); 
		notificacio.setRetard(Integer.valueOf(linia[15]));
		
		// Procediment
		ProcedimentDto procediment = new ProcedimentDto();
		procediment.setCodi(linia[16]);
		notificacio.setProcediment(procediment);
		
		// Fecha envío programado
		try {//viene de CSV y es opcional pero NO sabemos formato
			if (linia[17] != null && !linia[17].isEmpty()) {
				notificacio.setEnviamentDataProgramada(new SimpleDateFormat("dd/MM/yyyy").parse(linia[17]));
			} else { 
				notificacio.setEnviamentDataProgramada(null);
			}
		} catch (ParseException e) {
			notificacio.setEnviamentDataProgramada(null);
		}
				
		// Document
		if (fileNames.contains(linia[4])) { // Archivo físico
			document.setArxiuNom(linia[4]);
			byte [] arxiuBytes;
			if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
					(documentsProcessatsMassiu.containsKey(document.getArxiuNom()) && 
					documentsProcessatsMassiu.get(document.getArxiuNom()) == null)) {
				arxiuBytes = readZipFile (ficheroZipBytes, linia[4]);
				document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
				document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
				document.setGenerarCsv(false);
				document.setMediaType(URLConnection.guessContentTypeFromName(linia[4]));
				document.setMida(Long.valueOf(arxiuBytes.length));
				if (registreNotificaHelper.isSendDocumentsActive()) {
					leerMetadadesDelCsv(document, linia);
				}
			}
		} else {
			String uuidPattern = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$";
			Pattern pUuid = Pattern.compile(uuidPattern);
			Matcher mUuid = pUuid.matcher(linia[4]);
			if (mUuid.matches()) {
				// Uuid
				document.setUuid(linia[4]);
				document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
				document.setGenerarCsv(false);
				if (registreNotificaHelper.isSendDocumentsActive()) {
					leerMetadadesDelCsv(document, linia);
				}
			} else {
				// Csv
				document.setCsv(linia[4]);
				document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
				document.setGenerarCsv(false);
				if (registreNotificaHelper.isSendDocumentsActive()) {
					leerMetadadesDelCsv(document, linia);
				}
			}
		}
		notificacio.setDocument(document);
		
		// Enviaments
		enviament.setNotificaReferencia((linia[3] != null && !linia[3].isEmpty()) ? linia[3] : null); //si no se envía, Notific@ genera una
		enviament.setEntregaDehActiva(false); // De momento dejamos false
		EntregaPostalDto entregaPostal = new EntregaPostalDto();
		if (linia[12] != null && !linia[12].isEmpty() && // Si vienen Línea 1 y Código Postal
				linia[14] != null && !linia[14].isEmpty()) { 
			enviament.setEntregaPostalActiva(true);
			entregaPostal.setActiva(true); //??
			entregaPostal.setLinea1(linia[12]);
			entregaPostal.setLinea2(linia[13]);
			entregaPostal.setCodiPostal(linia[14]);	
			entregaPostal.setTipus(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR);
		} else {
			enviament.setEntregaPostalActiva(false);
			entregaPostal.setActiva(false); //??
		}
		enviament.setEntregaPostal(entregaPostal);
		
		enviament.setServeiTipus((linia[6] != null && !linia[6].isEmpty()) ? 
				ServeiTipusEnumDto.valueOf(linia[6].trim().toUpperCase()) : ServeiTipusEnumDto.NORMAL);
				
		PersonaDto titular = new PersonaDto();
		titular.setNom(linia[7]);
		titular.setLlinatge1(linia[8]); // vienen ap1 y ap2 juntos
		titular.setLlinatge2(null);
		titular.setNif(linia[9]);
		titular.setEmail(linia[10]);
		titular.setDir3Codi(linia[11]);
		titular.setIncapacitat(false);
		// TODO:  Igual lo hemos planteado mal. Si es un nif, podria ser el Nif de la administración. 
		//Entiendo que el "Código destino" = linia[11] solo se informará en caso de ser una administración
		//Si es persona física o jurídica no tiene sentido
		//Entonces podriamos utilizar este campo para saber si es una administración
		if (NifHelper.isValidCif(linia[9])) {
			titular.setInteressatTipus(InteressatTipusEnumDto.JURIDICA);
		} else if (NifHelper.isValidNifNie(linia[9])) {
			titular.setInteressatTipus(InteressatTipusEnumDto.FISICA);
		} else { 
			List<OrganGestorDto> lista = unitatsPerCodi(linia[9]); 
			if (lista != null && lista.size() > 0) {
				titular.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
			}
		}
		enviament.setTitular(titular);
		enviaments.add(enviament);
		notificacio.setEnviaments(enviaments);
		
		
		return notificacio;
	}

	private void leerMetadadesDelCsv(DocumentDto document, String[] linia) {
		document.setOrigen((linia[18] != null && !linia[18].isEmpty()) ? 
				OrigenEnum.valueOf(linia[18].trim().toUpperCase()): null);
		document.setValidesa((linia[19] != null && !linia[19].isEmpty()) ? 
				ValidesaEnum.valueOf(linia[19].trim().toUpperCase()) : null);
		document.setTipoDocumental((linia[20] != null && !linia[20].isEmpty()) ? 
				TipusDocumentalEnum.valueOf(linia[20].trim().toUpperCase()) : null);
		document.setModoFirma((linia[21] != null && !linia[21].isEmpty()) ? 
				Boolean.valueOf(linia[21]) : Boolean.FALSE);
	}

	private List<String[]> readCSV(byte[] fitxer) {
		List<String[]> linies = new ArrayList<String[]>();
		ICsvListReader listReader = null;
		try {
			Reader reader = new InputStreamReader(new ByteArrayInputStream(fitxer));
			listReader = new CsvListReader(reader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
			List<String> linia;
			int index = 0;
			while( (linia = listReader.read()) != null ) {
				if (index > 0) {
					linies.add(linia.toArray(new String[]{}));
				}
				index++;
			}
			if( listReader != null )
				listReader.close();
		} catch (Exception e) {
			logger.debug("S'ha produït un error a l'llegir el fitxer CSV.", e);
			return null;
		}
		return linies;
	}
	
	private List<String> readZipFileNames (byte [] fitxer) {
		List<String> names = new ArrayList<String>();
		try {
			ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(fitxer));
			ZipEntry entrada;
			while (null != (entrada=zip.getNextEntry()) ){
			   names.add(entrada.getName());
			   zip.closeEntry();
			}
			zip.close();		
		} catch (Exception e) {
			logger.debug("S'ha produït un error a l'llegir el fitxer ZIP per obtenir els noms dels fitxers.", e);
			return null;
		}
		return names;
	}

	private byte [] readZipFile (byte [] fitxer, String fileName) {
		ByteArrayOutputStream baos;
		byte arxiuBytes[] = null;
		try {
			ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(fitxer));
			ZipEntry entrada;
			while (null != (entrada=zip.getNextEntry()) ){
				if (fileName.equalsIgnoreCase(entrada.getName())) {
					baos = new ByteArrayOutputStream();
					int leido;
					byte [] buffer = new byte[1024];
					while ( 0 < (leido=zip.read(buffer))){
						baos.write(buffer,0,leido);
						
					}
					arxiuBytes = baos.toByteArray();
					baos.close();
					zip.closeEntry();
				}
			}
			zip.close();		
		} catch (Exception e) {
			logger.debug("S'ha produït un error a l'llegir el fitxer ZIP per extreure un fitxer.", e);
			return null;
		}
		return arxiuBytes;
	}

	private List<String> validarNotificacioMassiu(
			NotificacioDatabaseDto notificacio, EntitatDto entitat, String[] linia, 
			Map<String, Long> documentsProcessatsMassiu) {
		List<String> errors = new ArrayList<String>();
		boolean comunicacioSenseAdministracio = false;
		
		Map<String, OrganismeDto> organigramaByEntitat = null;
		
		String emisorDir3Codi = notificacio.getEmisorDir3Codi(); //entitat.getDir3Codi() entidad actual

		organigramaByEntitat = organGestorCachable.findOrganigramaByEntitat(emisorDir3Codi);	

		// Emisor
		if (emisorDir3Codi == null || emisorDir3Codi.isEmpty()) {
			errors.add("[1000] El camp 'emisorDir3Codi' no pot ser null.");
		} 
		if (emisorDir3Codi.length() > 9) {
			errors.add("[1001] El camp 'emisorDir3Codi' no pot tenir una longitud superior a 9 caràcters.");
		}
		// Entitat
		if (entitat == null) {
			errors.add("[1010] No s'ha trobat cap entitat configurada a Notib amb el codi Dir3 " + emisorDir3Codi + ". (emisorDir3Codi)");
		}
		if (!entitat.isActiva()) {
			errors.add("[1011] L'entitat especificada està desactivada per a l'enviament de notificacions");
		}
		// Procediment
		if (notificacio.getProcediment() != null && notificacio.getProcediment().getCodi() != null && notificacio.getProcediment().getCodi().length() > 9) {
			errors.add("[1021] El camp 'procedimentCodi' no pot tenir una longitud superior a 9 caràcters.");
		}
		// Concepte
		if (notificacio.getConcepte() == null || notificacio.getConcepte().isEmpty()) {
			errors.add("[1030] El concepte de la notificació no pot ser null.");
		}
		if (notificacio.getConcepte().length() > 240) {
			errors.add("[1031] El concepte de la notificació no pot tenir una longitud superior a 240 caràcters.");
		}
		if (!validFormat(notificacio.getConcepte()).isEmpty()) {
			errors.add("[1032] El format del camp concepte no és correcte. Inclou els caràcters ("+ listToString(validFormat(notificacio.getConcepte())) +") que no són correctes");
		}		
		// Tipus d'enviament
		if (notificacio.getEnviamentTipus() == null) {
			errors.add("[1050] El tipus d'enviament de la notificació no pot ser null.");
		}
		// Document
		if (notificacio.getDocument() == null) {
			errors.add("[1060] El camp 'document' no pot ser null.");
		}
		DocumentDto document = notificacio.getDocument();

		if (document.getArxiuNom() != null && document.getArxiuNom().length() > 200) {
			errors.add("[1072] El camp 'arxiuNom' no pot pot tenir una longitud superior a 200 caràcters.");
		}
		
		if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
				(documentsProcessatsMassiu.containsKey(document.getArxiuNom()) && 
				documentsProcessatsMassiu.get(document.getArxiuNom()) == null)) {			
			if (	(document.getContingutBase64() == null || document.getContingutBase64().isEmpty()) &&
					(document.getCsv() == null || document.getCsv().isEmpty()) &&
					(document.getUrl() == null || document.getUrl().isEmpty()) &&
					(document.getUuid() == null || document.getUuid().isEmpty())) {
				errors.add("[1062] És necessari incloure un document (contingutBase64, CSV, UUID o URL) a la notificació.");
			}
		}
		// Usuari
		if (notificacio.getUsuariCodi() == null || notificacio.getUsuariCodi().isEmpty()) {
			errors.add("[1070] El camp 'usuariCodi' no pot ser null (Requisit per fer el registre de sortida).");
		} 
		if (notificacio.getUsuariCodi().length() > 64) {
			errors.add("[1071] El camp 'usuariCodi' no pot pot tenir una longitud superior a 64 caràcters.");
		}

		// Enviaments
		if (notificacio.getEnviaments() == null || notificacio.getEnviaments().isEmpty()) {
			errors.add("[1100] El camp 'enviaments' no pot ser null.");
		}
		for(NotificacioEnviamentDtoV2 enviament : notificacio.getEnviaments()) {
			//Si és comunicació a administració i altres mitjans (persona física/jurídica) --> Excepció
			if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO) {
				if ((enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.FISICA) || 
						(enviament.getTitular().getInteressatTipus() == InteressatTipusEnumDto.JURIDICA))  {
					comunicacioSenseAdministracio = true;
				}
			}
			boolean senseNif = true;
			
			// Servei tipus
			if(enviament.getServeiTipus() == null) {
				errors.add("[1101] El camp 'serveiTipus' d'un enviament no pot ser null.");
			}
			
			// Titular
			if(enviament.getTitular() == null) {
				errors.add("[1110] El titular d'un enviament no pot ser null.");
			}
			// - Tipus
			if(enviament.getTitular().getInteressatTipus() == null) {
				errors.add("[1111] El camp 'interessat_tipus' del titular d'un enviament no pot ser null.");
			}
			// - Nom
			if(enviament.getTitular().getNom() != null && enviament.getTitular().getNom().length() > 255) {
				errors.add("[1112] El camp 'nom' del titular no pot ser tenir una longitud superior a 255 caràcters.");
			}
			// - Llinatge 1
			if (enviament.getTitular().getLlinatge1() != null && enviament.getTitular().getLlinatge1().length() > 40) {
				errors.add("[1113] El camp 'llinatge1' del titular no pot ser major que 40 caràcters.");
			}
			// - Llinatge 2
			if (enviament.getTitular().getLlinatge2() != null && enviament.getTitular().getLlinatge2().length() > 40) {
				errors.add("[1114] El camp 'llinatge2' del titular no pot ser major que 40 caràcters.");
			}
			// - Nif
			if(enviament.getTitular().getNif() != null && enviament.getTitular().getNif().length() > 9) {
				errors.add("[1115] El camp 'nif' del titular d'un enviament no pot tenir una longitud superior a 9 caràcters.");
			}
			if (enviament.getTitular().getNif() != null && !enviament.getTitular().getNif().isEmpty()) {
				if (NifHelper.isvalid(enviament.getTitular().getNif())) {
					senseNif = false;
				} else {
					errors.add("[1116] El 'nif' del titular no és vàlid.");
				}
				switch (enviament.getTitular().getInteressatTipus()) {
					case FISICA:
						if (!NifHelper.isValidNifNie(enviament.getTitular().getNif())) {
							errors.add("[1123] El camp 'nif' del titular no és un tipus de document vàlid per a persona física. Només s'admet NIF/NIE.");
						}
						break;
					case JURIDICA:
						if (!NifHelper.isValidCif(enviament.getTitular().getNif())) {
							errors.add("[1123] El camp 'nif' del titular no és un tipus de document vàlid per a persona jurídica. Només s'admet CIF.");
						}
						break;
					case ADMINISTRACIO:
						break;
				}
			}
			// - Email
			if (enviament.getTitular().getEmail() != null && enviament.getTitular().getEmail().length() > 160) {
				errors.add("[1117] El camp 'email' del titular no pot ser major que 160 caràcters.");
			}
			if (enviament.getTitular().getEmail() != null && !isEmailValid(enviament.getTitular().getEmail())) {
				errors.add("[1118] El format del camp 'email' del titular no és correcte");
			}
			// - Telèfon
			if (enviament.getTitular().getTelefon() != null && enviament.getTitular().getTelefon().length() > 16) {
				errors.add("[1119] El camp 'telefon' del titular no pot ser major que 16 caràcters.");
			}
			// - Raó social
			if (enviament.getTitular().getRaoSocial() != null && enviament.getTitular().getRaoSocial().length() > 80) {
				errors.add("[1120] El camp 'raoSocial' del titular no pot ser major que 80 caràcters.");
			}
			// - Codi Dir3
			if (enviament.getTitular().getDir3Codi() != null && enviament.getTitular().getDir3Codi().length() > 9) {
				errors.add("[1121] El camp 'dir3Codi' del titular no pot ser major que 9 caràcters.");
			}
			// - Incapacitat
			if (enviament.getTitular().isIncapacitat() && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())) {
				errors.add("[1122] En cas de titular amb incapacitat es obligatori indicar un destinatari.");
			}
			//   - Persona física
			if(enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.FISICA)) {
				if(enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty()) {
					errors.add("[1130] El camp 'nom' de la persona física titular no pot ser null.");
				}
				if (enviament.getTitular().getLlinatge1() == null || enviament.getTitular().getLlinatge1().isEmpty()) {
					errors.add("[1131] El camp 'llinatge1' de la persona física titular d'un enviament no pot ser null en el cas de persones físiques.");
				}
				if(enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
					errors.add("[1132] El camp 'nif' de la persona física titular d'un enviament no pot ser null.");
				}
			//   - Persona jurídica
			} else if(enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.JURIDICA)) {
				if((enviament.getTitular().getRaoSocial() == null || enviament.getTitular().getRaoSocial().isEmpty()) && 
						(enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty())) {
					errors.add("[1140] El camp 'raoSocial/nom' de la persona jurídica titular d'un enviament no pot ser null.");
				}
				if(enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
					errors.add("[1141] El camp 'nif' de la persona jurídica titular d'un enviament no pot ser null.");
				}
			//   - Administració
			} else if(enviament.getTitular().getInteressatTipus().equals(InteressatTipusEnumDto.ADMINISTRACIO)) {
				if(enviament.getTitular().getNom() == null || enviament.getTitular().getNom().isEmpty()) {
					errors.add("[1150] El camp 'nom' de l'administració titular d'un enviament no pot ser null.");
				}
				if(enviament.getTitular().getDir3Codi() == null) {
					errors.add("[1151] El camp 'dir3codi' de l'administració titular d'un enviament no pot ser null.");
				}
				OrganGestorDto organDir3 = cacheHelper.unitatPerCodi(enviament.getTitular().getDir3Codi());
				if (organDir3 == null) {
					errors.add("[1152] El camp 'dir3codi' (" + enviament.getTitular().getDir3Codi() + ") no es correspon a un codi Dir3 vàlid.");
				}
				if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO) {
					if (organDir3.getSir() == null || !organDir3.getSir()) {
						errors.add("[1153] El camp 'dir3codi' (" + enviament.getTitular().getDir3Codi() + ") no disposa d'oficina SIR. És obligatori per a comunicacions.");
					}
					if (organigramaByEntitat.containsKey(enviament.getTitular().getDir3Codi())) {
						errors.add("[1154] El camp 'dir3codi' (" + enviament.getTitular().getDir3Codi() + ") fa referència a una administració de la pròpia entitat. No es pot utilitzar Notib per enviar comunicacions dins la pròpia entitat.");
					}
				}
				if (enviament.getTitular().getNif() == null || enviament.getTitular().getNif().isEmpty()) {
					enviament.getTitular().setNif(organDir3.getCif());
				}
			}
			
			// Destinataris.
			// De momento se trata cada línea como 1 notificación con 1 envío y 1 titular

			if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.NOTIFICACIO && senseNif) {
				errors.add("[1220] En una notificació, com a mínim un dels interessats ha de tenir el Nif informat.");
			}
			
			// Entrega postal
			if(enviament.isEntregaPostalActiva()){
				if(enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty()) {
					errors.add("[1231] El camp 'codiPostal' no pot ser null (indicar 00000 en cas de no disposar del codi postal).");
				}
				if(enviament.getEntregaPostal().getCodiPostal() != null && enviament.getEntregaPostal().getCodiPostal().length() > 10) {
					errors.add("[1242] El camp 'codiPostal' no pot contenir més de 10 caràcters).");
				}
				if (enviament.getEntregaPostal().getLinea1() != null && enviament.getEntregaPostal().getLinea1().length() > 50) {
					errors.add("[1248] El camp 'linea1' de l'entrega postal no pot contenir més de 50 caràcters.");
				}
				if (enviament.getEntregaPostal().getLinea2() != null && enviament.getEntregaPostal().getLinea2().length() > 50) {
					errors.add("[1249] El camp 'linea2' de l'entrega postal no pot contenir més de 50 caràcters.");
				}
				if (enviament.getEntregaPostal().getLinea1() == null || enviament.getEntregaPostal().getLinea1().isEmpty()) {
					errors.add("[1290] El camp 'linea1' no pot ser null.");
				}
				if (enviament.getEntregaPostal().getLinea2() == null || enviament.getEntregaPostal().getLinea2().isEmpty()) {
					errors.add("[1291] El camp 'linea2' no pot ser null.");
				}
			}

			// Entrega DEH de momento siempre es false
		}
		
		// Procediment
		if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.NOTIFICACIO ) {
			if (notificacio.getProcediment() == null || notificacio.getProcediment().getCodi() == null) {
				errors.add("[1020] El camp 'procedimentCodi' no pot ser null.");
			}
		} else if ((notificacio.getProcediment() == null || notificacio.getProcediment().getCodi() == null) 
				&& notificacio.getOrganGestorCodi() == null){
			errors.add("[1022] El camp 'organ gestor' no pot ser null en una comunicació amb l'administració on no s'especifica un procediment.");
		}
		if (notificacio.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO && comunicacioSenseAdministracio) {
			if (notificacio.getProcediment() == null || notificacio.getProcediment().getCodi() == null) {
				errors.add("[1020] El camp 'procedimentCodi' no pot ser null.");
			}
		}
		if (!organigramaByEntitat.containsKey(notificacio.getOrganGestorCodi())) {
			errors.add("[1023] El camp 'organ gestor' no es correspon a cap Òrgan Gestor de l'entitat especificada.");
		}
		//TODO: está fallando en REST y aquí esta validación. Es correcto???
		// Respuesta: Por ahora no pongas esta validación. Lo consultaré con la DGTIC. Pero el problema no es la validación. Son los datos utilizados.
		// Otra cosa es que la validación NO se tiene que hacer si notificacio.getOrganGestor() == null en el caso de REST.
		// Entiendo que para CSV siempre tienen que enviarla.		
		// En el CSV me indican órgano y procedimiento. Si el procedimiento no es común y es de otro órgano => error
//		if (notificacio.getProcediment() != null && !notificacio.getProcediment().isComu()) {
//		 if (notificacio.getOrganGestorCodi() != notificacio.getProcediment().getOrganGestor()) {
//				errors.add("[1024] El camp 'organ gestor' no es correspon a l'òrgan gestor de l'procediment.");
//			}
//		}

		// Documents
		if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
				(documentsProcessatsMassiu.containsKey(document.getArxiuNom()) && 
				documentsProcessatsMassiu.get(document.getArxiuNom()) == null)) {
			
			if (document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) {
				if (!isFormatValid(document.getContingutBase64())) {
					errors.add("[1063] El format del document no és vàlid. Les notificacions i comunicacions a ciutadà només admeten els formats PDF i ZIP.");
				}
				if (document.getMida() > getMaxSizeFile()) {
					errors.add("[1065] La longitud del document supera el màxim definit (" + getMaxSizeFile() / (1024*1024) + "Mb).");
				}
			}

			// Metadades
			if ((document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) 
					&& registreNotificaHelper.isSendDocumentsActive()) {
				if (document.getOrigen() == null) {
					errors.add("[1066] Error en les metadades del document. No està informat l'ORIGEN del document");
				}
				if (document.getValidesa() == null) {
					errors.add("[1066] Error en les metadades del document. No està informat la VALIDESA(Estat elaboració) del document");
				}
				if (document.getTipoDocumental() == null) {
					errors.add("[1066] Error en les metadades del document. No està informat el TIPUS DOCUMENTAL del document");
				}
				if (document.getArxiuNom().toUpperCase().endsWith("PDF") && document.getModoFirma() == null) {
					errors.add("[1066] Error en les metadades del document. No està informat el MODE de FIRMA del document tipus PDF");
				}
			}
		}
			
		return errors;
	}
	
	private ICsvListWriter writeCsvHeader(String fileName) {		
		
		ICsvListWriter listWriter = null;
		try {
				listWriter = new CsvListWriter(new FileWriter(fileName),
						CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);

				final String[] header;
				if (registreNotificaHelper.isSendDocumentsActive()) {
					header = new String[] { "Codigo Unidad Remisora", "Concepto", "Tipo de Envio", "Referencia Emisor", "Nombre Fichero", "Normalizado", "Prioridad Servicio", "Nombre",	"APELLIDOS", "CIF/NIF",	"Email", "Codigo destino", "Línea 1", "Línea 2", "Codigo Postal", "Retardo Postal", "Código Procedimiento", "Fecha Envio Programado", "Origen", "Estado Elaboración", "Tipo Documental", "PDF Firmado", "Errores" };
				} else {
					header = new String[] { "Codigo Unidad Remisora", "Concepto", "Tipo de Envio", "Referencia Emisor", "Nombre Fichero", "Normalizado", "Prioridad Servicio", "Nombre",	"APELLIDOS", "CIF/NIF",	"Email", "Codigo destino", "Línea 1", "Línea 2", "Codigo Postal", "Retardo Postal", "Código Procedimiento", "Fecha Envio Programado", "Errores" };
				}
				
				listWriter.writeHeader(header);	
				return listWriter;
		} catch (IOException e) {
			logger.error("S'ha produït un error a l'escriure la capçalera de l'fitxer CSV.", e);
			throw new WriteCsvException("No s'ha pogut escriure la capçalera de l'fitxer CSV.");
		}
	}
	
	private void writeCsvLinia(ICsvListWriter listWriter, String[] linia, List<String> errors) {		
		
		List<String> liniaAmbErrors = new ArrayList<String>();
		for (int i = 0; i < linia.length; i++) {
			liniaAmbErrors.add(linia[i]);
		}
		
		StringBuffer sbErrors = new StringBuffer();
		for(int i = 0; i < errors.size(); i++) {
			sbErrors.append(errors.get(i));
		}
		
		String errorsStr = sbErrors.toString();
		liniaAmbErrors.add(errorsStr);
		
		try {
			listWriter.write(liniaAmbErrors);		
		} catch (IOException e) {
			logger.error("S'ha produït un error a l'escriure la línia en el fitxer CSV.", e);
			throw new WriteCsvException("No s'ha pogut escriure la línia en el fitxer CSV.");
		}
	}
	
	private void writeCsvClose(ICsvListWriter listWriter) {		
		try {
			if( listWriter != null ) {
				listWriter.close();
			}			
		} catch (IOException e) {
			logger.error("S'ha produït un error a l'tancar el fitxer CSV.", e);
			throw new WriteCsvException("No s'ha pogut tancar el fitxer CSV.");
		}
	}


	private ArrayList<Character> validFormat(String value) {
		String CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;·";
		ArrayList<Character> charsNoValids = new ArrayList<Character>();
		char[] chars = value.replace("\n", "").replace("\r", "").toCharArray();
		
		boolean esCaracterValid = true;
		for (int i = 0; i < chars.length; i++) {
			esCaracterValid = !(CONTROL_CARACTERS.indexOf(chars[i]) < 0);
			if (!esCaracterValid) {
				charsNoValids.add(chars[i]);
			}
	    }
		return charsNoValids;
	}
	
	private StringBuilder listToString(ArrayList<?> list) {
	    StringBuilder str = new StringBuilder();
	    for (int i = 0; i < list.size(); i++) {
	    	str.append(list.get(i));
	    }
	    return str;
	}
	
	private boolean isEmailValid(String email) {
		boolean valid = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (Exception e) {
			valid = false; //no vàlid
		}
		return valid;
	}
	
	private boolean isFormatValid(String docBase64) {
		boolean valid = true;
		String[] formatsValids = {"JVBERi0","UEsDBBQAAAAIA"}; //PDF / ZIP
		
		if (!(docBase64.startsWith(formatsValids[0]) || docBase64.startsWith(formatsValids[1])))
			valid = false;
		
		return valid;
	}
	
	private static Long getMaxSizeFile() {
		String property = "es.caib.notib.notificacio.document.size";
		logger.debug("Consulta del valor de la property (property=" + property + ")");
		return Long.valueOf(PropertiesHelper.getProperties().getProperty(property, "10485760"));
	}
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceImpl.class);

}