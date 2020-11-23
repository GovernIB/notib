/**
 * 
 */
package es.caib.notib.core.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.mail.MessagingException;

import org.apache.commons.codec.binary.Base64;
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

import com.codahale.metrics.Timer;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.LocalitatsDto;
import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioErrorCallbackFiltreDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.NotificacioRegistreErrorFiltreDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PaisosDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.ProgresDescarregaDto;
import es.caib.notib.core.api.dto.ProgresDescarregaDto.TipusInfo;
import es.caib.notib.core.api.dto.ProvinciesDto;
import es.caib.notib.core.api.dto.RegistreIdDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.exception.JustificantException;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.api.ws.notificacio.Persona;
import es.caib.notib.core.entity.DocumentEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.CreacioSemaforDto;
import es.caib.notib.core.helper.EmailHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.HibernateHelper;
import es.caib.notib.core.helper.JustificantHelper;
import es.caib.notib.core.helper.MessageHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.OrganigramaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.helper.RegistreHelper;
import es.caib.notib.core.helper.RegistreNotificaHelper;
import es.caib.notib.core.helper.UsuariHelper;
import es.caib.notib.core.repository.DocumentRepository;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.PersonaRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;

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
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
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
	private GrupRepository grupRepository;
	@Autowired
	private RegistreHelper registreHelper;
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
	
	public static Map<String, ProgresDescarregaDto> progresDescarrega = new HashMap<String, ProgresDescarregaDto>();
	
	@Transactional(rollbackFor=Exception.class)
	@Override
	public List<NotificacioDto> create(
			Long entitatId, 
			NotificacioDtoV2 notificacio) throws RegistreNotificaException {

		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
			GrupEntity grupNotificacio = null;
			OrganGestorEntity organGestor = null;
			String documentGesdocId = null;
			ProcedimentEntity procediment = null;
			if (notificacio.getProcediment() != null && notificacio.getProcediment().getId() != null) {
				procediment = entityComprovarHelper.comprovarProcediment(
							entitat,
						 	notificacio.getProcediment().getId(),
						 	false,
						 	false,
						 	true,
						 	false);
				if (!procediment.isComu() || (procediment.isComu() && notificacio.getOrganGestor() == null)) {
					organGestor = procediment.getOrganGestor();
				}
			} 
			if (organGestor == null && notificacio.getOrganGestor() != null ) {
//				organGestor = entityComprovarHelper.comprovarOrganGestor(entitat, notificacio.getOrganGestor());
				organGestor = organGestorRepository.findByCodi(notificacio.getOrganGestor());
				if (organGestor == null) {
					Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(entitat.getDir3Codi());
					if (!organigramaEntitat.containsKey(notificacio.getOrganGestor())) {
						throw new NotFoundException(
								notificacio.getOrganGestor(), 
								OrganGestorEntity.class,
								"L'òrgan gestor especificat no es correspon a cap Òrgan Gestor de l'entitat especificada");
					}
					LlibreDto llibreOrgan = pluginHelper.llistarLlibreOrganisme(
							entitat.getCodi(),
							notificacio.getOrganGestor());
					
					organGestor = OrganGestorEntity.getBuilder(
							notificacio.getOrganGestor(),
							organigramaEntitat.get(notificacio.getOrganGestor()).getNom(),
							entitat,
							llibreOrgan.getCodi(),
							llibreOrgan.getNomLlarg()).build();
					organGestorRepository.save(organGestor);
				}
			}
			if (notificacio.getGrup() != null && notificacio.getGrup().getId() != null) {
				grupNotificacio = grupRepository.findOne(notificacio.getGrup().getId());
			}
			if(notificacio.getDocument().getContingutBase64() != null) {
				documentGesdocId = pluginHelper.gestioDocumentalCreate(
						PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
						Base64.decodeBase64(notificacio.getDocument().getContingutBase64()));
			} else if (notificacio.getDocument().getUuid() != null) {
				DocumentDto document = new DocumentDto();
				String arxiuUuid = notificacio.getDocument().getUuid();
				if (pluginHelper.isArxiuPluginDisponible()) {
					Document documentArxiu = pluginHelper.arxiuDocumentConsultar(arxiuUuid, null);
					document.setArxiuNom(documentArxiu.getNom());
					document.setNormalitzat(notificacio.getDocument().isNormalitzat());
					document.setGenerarCsv(notificacio.getDocument().isGenerarCsv());
					document.setUuid(arxiuUuid);
					document.setMediaType(documentArxiu.getContingut().getTipusMime());
					document.setMida(documentArxiu.getContingut().getTamany());
					notificacio.setDocument(document);
				}
			} else if (notificacio.getDocument().getCsv() != null) {
				DocumentDto document = new DocumentDto();
				String arxiuCsv = notificacio.getDocument().getCsv();
				if (pluginHelper.isArxiuPluginDisponible()) {
					DocumentContingut documentArxiu = pluginHelper.arxiuGetImprimible(arxiuCsv, false);
					document.setArxiuNom(documentArxiu.getArxiuNom());
					document.setNormalitzat(notificacio.getDocument().isNormalitzat());
					document.setGenerarCsv(notificacio.getDocument().isGenerarCsv());
					document.setMediaType(documentArxiu.getTipusMime());
					document.setMida(documentArxiu.getTamany());
					document.setCsv(arxiuCsv);
					notificacio.setDocument(document);
				}
			}
			DocumentEntity documentEntity = null;
			
			// Guardar document 
			if(notificacio.getDocument().getCsv() != null || 
			   notificacio.getDocument().getUuid() != null || 
			   notificacio.getDocument().getContingutBase64() != null || 
			   notificacio.getDocument().getArxiuGestdocId() != null) {
	
				documentEntity = documentRepository.save(DocumentEntity.getBuilderV2(
						notificacio.getDocument().getArxiuGestdocId(), 
						documentGesdocId, 
						notificacio.getDocument().getArxiuNom(), 
						notificacio.getDocument().getUrl(),  
						notificacio.getDocument().isNormalitzat(),  
						notificacio.getDocument().getUuid(),
						notificacio.getDocument().getCsv(),
						notificacio.getDocument().getMediaType(),
						notificacio.getDocument().getMida()).build());
			}
			// Dades generals de la notificació
			NotificacioEntity.BuilderV2 notificacioBuilder = NotificacioEntity.
					getBuilderV2(
							entitat,
							notificacio.getEmisorDir3Codi(),
							organGestor,
							pluginHelper.getNotibTipusComunicacioDefecte(),
							notificacio.getEnviamentTipus(), 
							notificacio.getConcepte(),
							notificacio.getDescripcio(),
							notificacio.getEnviamentDataProgramada(),
							notificacio.getRetard(),
							notificacio.getCaducitat(),
							notificacio.getUsuariCodi(),
							procediment != null ? procediment.getCodi() : null,
							procediment,
							grupNotificacio != null ? grupNotificacio.getCodi() : null,
							notificacio.getNumExpedient(),
							TipusUsuariEnumDto.INTERFICIE_WEB
							).document(documentEntity);
	
			NotificacioEntity notificacioEntity = notificacioBuilder.build();
			notificacioEntity = notificacioRepository.saveAndFlush(notificacioEntity);
	
			List<Enviament> enviaments = new ArrayList<Enviament>();
			List<NotificacioEnviamentEntity> enviamentsEntity = new ArrayList<NotificacioEnviamentEntity>();
			for(NotificacioEnviamentDtoV2 enviament: notificacio.getEnviaments()) {
				if (enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty())
					enviament.getEntregaPostal().setCodiPostal(enviament.getEntregaPostal().getCodiPostalNorm());
				enviaments.add(conversioTipusHelper.convertir(enviament, Enviament.class));
			}
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
					enviamentsEntity.add(notificacioEnviamentRepository.saveAndFlush(NotificacioEnviamentEntity.
							getBuilderV2(
									enviament,
									entitat.isAmbEntregaDeh(),
									numeracioTipus, 
									tipusConcret, 
									serveiTipus, 
									notificacioEntity, 
									titular, 
									destinataris).domiciliViaTipus(toEnviamentViaTipusEnum(viaTipus)).build()));
				}
			}
			notificacioEntity.getEnviaments().addAll(enviamentsEntity);
			notificacioEntity = notificacioRepository.saveAndFlush(notificacioEntity);
			// Comprovar on s'ha d'enviar ara
			if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(pluginHelper.getNotibTipusComunicacioDefecte())) {
	//			List<NotificacioEnviamentDtoV2> enviamentsDto = conversioTipusHelper.convertirList(
	//					notificacio.getEnviaments(), 
	//					NotificacioEnviamentDtoV2.class);
				synchronized(CreacioSemaforDto.getCreacioSemafor()) {
					registreNotificaHelper.realitzarProcesRegistrarNotificar(
							notificacioEntity,
							notificacio.getEnviaments());
	//						enviamentsDto);	
				}
			}
	
			List<NotificacioEntity> notificacions = notificacioRepository.findByEntitatId(entitatId);
	
			return conversioTipusHelper.convertirList(
				notificacions,
				NotificacioDto.class);
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

//			### Esborrar els enviaments i els events
			notificacioEnviamentRepository.delete(notificacio.getEnviaments());
			notificacioEventRepository.delete(notificacio.getEvents());
			
//			### Esborrar la notificació
			notificacioRepository.delete(notificacio);
			
//			### Esborrar el titular i els destinataris de cada enviament
			for (NotificacioEnviamentEntity enviament : notificacio.getEnviaments()) {
				personaRepository.delete(enviament.getTitular());
				personaRepository.delete(enviament.getDestinataris());
			}
			logger.debug("La notificació s'ha esborrat correctament (notificacioId=" + notificacioId + ")");
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public List<NotificacioDto> update(
			Long entitatId,
			NotificacioDtoV2 notificacio) throws NotFoundException, RegistreNotificaException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false, 
					false, 
					true,
					false);
			GrupEntity grupNotificacio = null;
			OrganGestorEntity organGestor = null;
			String documentGesdocId = null;
			ProcedimentEntity procediment = null;
//			### Recuperar procediment notificació
			if (notificacio.getProcediment() != null && notificacio.getProcediment().getId() != null) {
				procediment = entityComprovarHelper.comprovarProcediment(
							entitat,
						 	notificacio.getProcediment().getId(),
						 	false,
						 	false,
						 	true,
						 	false);
				if (!procediment.isComu() || (procediment.isComu() && notificacio.getOrganGestor() == null)) {
					organGestor = procediment.getOrganGestor();
				}
			} 
//			### Recuperar òrgan gestor notificació
			if (organGestor == null && notificacio.getOrganGestor() != null ) {
				organGestor = organGestorRepository.findByCodi(notificacio.getOrganGestor());
				if (organGestor == null) {
					Map<String, OrganismeDto> organigramaEntitat = cacheHelper.findOrganigramaByEntitat(entitat.getDir3Codi());
					if (!organigramaEntitat.containsKey(notificacio.getOrganGestor())) {
						throw new NotFoundException(
								notificacio.getOrganGestor(), 
								OrganGestorEntity.class,
								"L'òrgan gestor especificat no es correspon a cap Òrgan Gestor de l'entitat especificada");
					}
					LlibreDto llibreOrgan = pluginHelper.llistarLlibreOrganisme(
							entitat.getCodi(),
							notificacio.getOrganGestor());
					
//					### Crear òrgan gestor si no existeix, si existeix no fer res
					organGestor = OrganGestorEntity.getBuilder(
							notificacio.getOrganGestor(),
							organigramaEntitat.get(notificacio.getOrganGestor()).getNom(),
							entitat,
							llibreOrgan.getCodi(),
							llibreOrgan.getNomLlarg()).build();
					organGestorRepository.save(organGestor);
				}
			}
//			### Recupera grup notificació a partir del codi
			if (notificacio.getGrup() != null && notificacio.getGrup().getId() != null) {
				grupNotificacio = grupRepository.findOne(notificacio.getGrup().getId());
			}
//			### Crear document si és nou
			if(notificacio.getDocument().getContingutBase64() != null) {
				documentGesdocId = pluginHelper.gestioDocumentalCreate(
						PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
						Base64.decodeBase64(notificacio.getDocument().getContingutBase64()));
			} else if (notificacio.getDocument().getUuid() != null) {
				DocumentDto document = new DocumentDto();
				String arxiuUuid = notificacio.getDocument().getUuid();
				if (pluginHelper.isArxiuPluginDisponible()) {
					Document documentArxiu = pluginHelper.arxiuDocumentConsultar(arxiuUuid, null);
					document.setArxiuNom(documentArxiu.getNom());
					document.setNormalitzat(notificacio.getDocument().isNormalitzat());
					document.setGenerarCsv(notificacio.getDocument().isGenerarCsv());
					document.setUuid(arxiuUuid);
					document.setMediaType(documentArxiu.getContingut().getTipusMime());
					document.setMida(documentArxiu.getContingut().getTamany());
					notificacio.setDocument(document);
				}
			} else if (notificacio.getDocument().getCsv() != null) {
				DocumentDto document = new DocumentDto();
				String arxiuCsv = notificacio.getDocument().getCsv();
				if (pluginHelper.isArxiuPluginDisponible()) {
					DocumentContingut documentArxiu = pluginHelper.arxiuGetImprimible(arxiuCsv, false);
					document.setArxiuNom(documentArxiu.getArxiuNom());
					document.setNormalitzat(notificacio.getDocument().isNormalitzat());
					document.setGenerarCsv(notificacio.getDocument().isGenerarCsv());
					document.setMediaType(documentArxiu.getTipusMime());
					document.setMida(documentArxiu.getTamany());
					document.setCsv(arxiuCsv);
					notificacio.setDocument(document);
				}
			}
			DocumentEntity documentEntity = null;
//			### Crear o actualitzar un document existent
			if(notificacio.getDocument().getCsv() != null || 
			   notificacio.getDocument().getUuid() != null || 
			   notificacio.getDocument().getContingutBase64() != null || 
			   notificacio.getDocument().getArxiuGestdocId() != null) {
	
				if (notificacio.getDocument().getId() != null) {
					documentEntity = documentRepository.findOne(Long.valueOf(notificacio.getDocument().getId()));
					documentEntity.update(
							documentGesdocId != null ? documentGesdocId : notificacio.getDocument().getArxiuGestdocId(), 
							notificacio.getDocument().getArxiuNom(), 
							notificacio.getDocument().getUrl(),  
							notificacio.getDocument().isNormalitzat(),  
							notificacio.getDocument().getUuid(),
							notificacio.getDocument().getCsv(),
							notificacio.getDocument().getMediaType(),
							notificacio.getDocument().getMida());
				} else {
					documentEntity = documentRepository.save(DocumentEntity.getBuilderV2(
							notificacio.getDocument().getArxiuGestdocId(), 
							documentGesdocId != null ? documentGesdocId : notificacio.getDocument().getArxiuGestdocId(), 
							notificacio.getDocument().getArxiuNom(), 
							notificacio.getDocument().getUrl(),  
							notificacio.getDocument().isNormalitzat(),  
							notificacio.getDocument().getUuid(),
							notificacio.getDocument().getCsv(),
							notificacio.getDocument().getMediaType(),
							notificacio.getDocument().getMida()).build());
				}
			}
//			### Actualitzar notificació existent
			NotificacioEntity notificacioEntity = notificacioRepository.findOne(notificacio.getId());
			notificacioEntity.update(
					entitat,
					notificacio.getEmisorDir3Codi(),
					organGestor,
					pluginHelper.getNotibTipusComunicacioDefecte(),
					notificacio.getEnviamentTipus(), 
					notificacio.getConcepte(),
					notificacio.getDescripcio(),
					notificacio.getEnviamentDataProgramada(),
					notificacio.getRetard(),
					notificacio.getCaducitat(),
					notificacio.getUsuariCodi(),
					procediment != null ? procediment.getCodi() : null,
					procediment,
					grupNotificacio != null ? grupNotificacio.getCodi() : null,
					notificacio.getNumExpedient(),
					TipusUsuariEnumDto.INTERFICIE_WEB,
					documentEntity);
			
			List<Enviament> enviaments = new ArrayList<Enviament>();
			List<Long> enviamentsIds = new ArrayList<Long>();
			List<Long> destinatarisIds = new ArrayList<Long>();
			List<NotificacioEnviamentEntity> nousEnviaments = new ArrayList<NotificacioEnviamentEntity>();
			for(NotificacioEnviamentDtoV2 enviament: notificacio.getEnviaments()) {
				if (enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty()) {
					enviament.getEntregaPostal().setCodiPostal(enviament.getEntregaPostal().getCodiPostalNorm());
				}
				enviaments.add(conversioTipusHelper.convertir(enviament, Enviament.class));
				if (enviament.getId() != null) //En cas d'enviaments nous
					enviamentsIds.add(enviament.getId());
			}
//			### Creació o edició enviament existent
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
//					### Crear o editar titular enviament existent
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
					NotificacioEnviamentEntity enviamentEntity = notificacioEnviamentRepository.findOne(enviament.getId());
					enviamentEntity.update(
							enviament,
							entitat.isAmbEntregaDeh(),
							numeracioTipus, 
							tipusConcret, 
							serveiTipus, 
							notificacioEntity, 
							titular, 
							toEnviamentViaTipusEnum(viaTipus));
					enviamentEntity.getDestinataris().addAll(nousDestinataris);
					} else {
						NotificacioEnviamentEntity nouEnviament = notificacioEnviamentRepository.saveAndFlush(NotificacioEnviamentEntity.
								getBuilderV2(
										enviament,
										entitat.isAmbEntregaDeh(),
										numeracioTipus, 
										tipusConcret, 
										serveiTipus, 
										notificacioEntity, 
										titular, 
										nousDestinataris).domiciliViaTipus(toEnviamentViaTipusEnum(viaTipus)).build());
						nousEnviaments.add(nouEnviament);
						enviamentsIds.add(nouEnviament.getId());
					}
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
					notificacioEventRepository.deleteByEnviament(enviament);
					notificacioEnviamentRepository.delete(enviament);
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
					registreNotificaHelper.realitzarProcesRegistrarNotificar(
							notificacioEntity,
							notificacio.getEnviaments());
				}
			}
	
			List<NotificacioEntity> notificacions = notificacioRepository.findByEntitatId(entitatId);
			return conversioTipusHelper.convertirList(
				notificacions,
				NotificacioDto.class);
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
				logger.info("Consultant events notificació...");
				List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioIdOrderByDataAsc(notificacio.getId());
				
				if (events != null && events.size() > 0) {
					NotificacioEventEntity lastEvent = events.get(events.size() - 1);
					
					if(lastEvent.isError() && 
								(lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.CALLBACK_CLIENT) ||
								lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT) ||
								lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO) ||
								lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE) || 
								lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT) || 
								lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT) || 
								lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR) || 
								lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR))) {
						logger.info("El darrer event de la notificació " + notificacio.getId()  + " conté un error de tipus: " + lastEvent.getTipus().name());
						notificacio.setErrorLastEvent(true);
					}

				}
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
	public PaginaDto<NotificacioDto> findAmbFiltrePaginat(
			Long entitatId, 
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdministrador,
			boolean isAdministradorOrgan,
			List<String> procedimentsCodisNotib,
			List<String> codisProcedimentsProcessables,
			List<String> codisOrgansGestorsDisponibles,
			String organGestorCodi,
			String usuariCodi,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false, 
					isUsuariEntitat,
					false);
			Page<NotificacioEntity> notificacions = null;
			Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
			mapeigPropietatsOrdenacio.put("procediment.organGestor", new String[] {"pro.organGestor.codi"});
			mapeigPropietatsOrdenacio.put("organGestorDesc", new String[] {"organGestor.codi"});
			mapeigPropietatsOrdenacio.put("procediment.nom", new String[] {"pro.nom"});
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
			
			boolean esProcedimentsCodisNotibNull = (procedimentsCodisNotib == null || procedimentsCodisNotib.isEmpty());
			boolean esOrgansGestorsCodisNotibNull = (codisOrgansGestorsDisponibles == null || codisOrgansGestorsDisponibles.isEmpty());
			
			if (filtre == null) {
				//Consulta les notificacions sobre les quals té permis l'usuari actual
				if (isUsuari) {
					notificacions = notificacioRepository.findByProcedimentCodiNotibAndGrupsCodiNotibAndEntitat(
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : procedimentsCodisNotib, 
							aplicacioService.findRolsUsuariActual(), 
							esOrgansGestorsCodisNotibNull,
							esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
							entitatActual,
							usuariCodi,
							pageable);
				//Consulta els notificacions de l'entitat acutal
				} else if (isUsuariEntitat) {
					notificacions = notificacioRepository.findByEntitatActual(
							entitatActual,
							pageable);
				//Consulta totes les notificacions de les entitats actives
				} else if (isAdministrador) {
					List<EntitatEntity> entitatsActiva = entitatRepository.findByActiva(true);
					notificacions = notificacioRepository.findByEntitatActiva(
							entitatsActiva,
							pageable);
				} else if (isAdministradorOrgan) {
					List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi);
					if (!procedimentsCodisNotib.isEmpty()) {
						notificacions = notificacioRepository.findByProcedimentCodiNotibAndEntitat(
								procedimentsCodisNotib, 
								entitatActual,
								organs,
								pageable);
					}
				}
			} else {
				Date dataInici = toIniciDia(filtre.getDataInici());
				Date dataFi = toFiDia(filtre.getDataFi());
				OrganGestorEntity organGestor = null;
				if (filtre.getOrganGestor() != null) {
					organGestor = organGestorRepository.findByCodi(filtre.getOrganGestor());
				}
				ProcedimentEntity procediment = null;
				if (filtre.getProcedimentId() != null) {
					procediment = procedimentRepository.findById(filtre.getProcedimentId());
				}
				if (isUsuari) {
					notificacions = notificacioRepository.findAmbFiltreAndProcedimentCodiNotibAndGrupsCodiNotib(
							filtre.getEntitatId() == null,
							filtre.getEntitatId(),
							esProcedimentsCodisNotibNull,
							esProcedimentsCodisNotibNull ? null : procedimentsCodisNotib, 
							aplicacioService.findRolsUsuariActual(),
							esOrgansGestorsCodisNotibNull,
							esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles,
							filtre.getEnviamentTipus() == null,
							filtre.getEnviamentTipus(),
							filtre.getConcepte() == null,
							filtre.getConcepte() == null ? "" : filtre.getConcepte(), 
							filtre.getEstat() == null,
							filtre.getEstat(), 
							NotificacioEnviamentEstatEnumDto.valueOf(filtre.getEstat().toString()),
							dataInici == null,
							dataInici,
							dataFi == null,
							dataFi,
							filtre.getTitular() == null || filtre.getTitular().isEmpty(),
							filtre.getTitular() == null ? "" : filtre.getTitular(),
							entitatActual,
							organGestor == null,
							organGestor,
							procediment == null,
							procediment,
							filtre.getTipusUsuari() == null,
							filtre.getTipusUsuari(),
							filtre.getNumExpedient() == null || filtre.getNumExpedient().isEmpty(),
							filtre.getNumExpedient(),
							filtre.getCreadaPer() == null || filtre.getCreadaPer().isEmpty(),
							filtre.getCreadaPer(),
							filtre.getIdentificador() == null || filtre.getIdentificador().isEmpty(),
							filtre.getIdentificador(),
							usuariCodi,
							filtre.isNomesAmbErrors(),
							pageable);
				} else if (isUsuariEntitat) {
					notificacions = notificacioRepository.findAmbFiltre(
							entitatId == null, 
							entitatId, 
							filtre.getEnviamentTipus() == null,
							filtre.getEnviamentTipus(),
							filtre.getConcepte() == null,
							filtre.getConcepte(),
							filtre.getEstat() == null,
							filtre.getEstat(),
							NotificacioEnviamentEstatEnumDto.valueOf(filtre.getEstat().toString()),
							dataInici == null,
							dataInici,
							dataFi == null,
							dataFi,
							filtre.getTitular() == null || filtre.getTitular().isEmpty(),
							filtre.getTitular(),
							organGestor == null,
							organGestor,
							procediment == null,
							procediment,
							filtre.getTipusUsuari() == null,
							filtre.getTipusUsuari(),
							filtre.getNumExpedient() == null || filtre.getNumExpedient().isEmpty(),
							filtre.getNumExpedient(),
							filtre.getCreadaPer() == null || filtre.getCreadaPer().isEmpty(),
							filtre.getCreadaPer(),
							filtre.getIdentificador() == null || filtre.getIdentificador().isEmpty(),
							filtre.getIdentificador(),
							filtre.isNomesAmbErrors(),
							pageable);
				} else if (isAdministrador) {
					notificacions = notificacioRepository.findAmbFiltre(
							filtre.getEntitatId() == null,
							filtre.getEntitatId(),
							filtre.getEnviamentTipus() == null,
							filtre.getEnviamentTipus(),
							filtre.getConcepte() == null || filtre.getConcepte().isEmpty(),
							filtre.getConcepte(),
							filtre.getEstat() == null,
							filtre.getEstat(),
							NotificacioEnviamentEstatEnumDto.valueOf(filtre.getEstat().toString()),
							dataInici == null,
							dataInici,
							dataFi == null,
							dataFi,
							filtre.getTitular() == null || filtre.getTitular().isEmpty(),
							filtre.getTitular(),
							organGestor == null,
							organGestor,
							procediment == null,
							procediment,
							filtre.getTipusUsuari() == null,
							filtre.getTipusUsuari(),
							filtre.getNumExpedient() == null || filtre.getNumExpedient().isEmpty(),
							filtre.getNumExpedient(),
							filtre.getCreadaPer() == null || filtre.getCreadaPer().isEmpty(),
							filtre.getCreadaPer(),
							filtre.getIdentificador() == null || filtre.getIdentificador().isEmpty(),
							filtre.getIdentificador(),
							filtre.isNomesAmbErrors(),
							pageable);
				} else if (isAdministradorOrgan) {
					List<String> organs = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestorCodi);
//					if (!procedimentsCodisNotib.isEmpty()) {
						notificacions = notificacioRepository.findAmbFiltreAndProcedimentCodiNotib(
								filtre.getEntitatId() == null,
								filtre.getEntitatId(),
								esProcedimentsCodisNotibNull,
								esProcedimentsCodisNotibNull ? null : procedimentsCodisNotib, 
								filtre.getEnviamentTipus() == null,
								filtre.getEnviamentTipus(),
								filtre.getConcepte() == null,
								filtre.getConcepte() == null ? "" : filtre.getConcepte(), 
								filtre.getEstat() == null,
								filtre.getEstat(), 
								NotificacioEnviamentEstatEnumDto.valueOf(filtre.getEstat().toString()),
								dataInici == null,
								dataInici,
								dataFi == null,
								dataFi,
								filtre.getTitular() == null || filtre.getTitular().isEmpty(),
								filtre.getTitular() == null ? "" : filtre.getTitular(),
								entitatActual,
								organGestor == null,
								organGestor,
								procediment == null,
								procediment,
								filtre.getTipusUsuari() == null,
								filtre.getTipusUsuari(),
								filtre.getNumExpedient() == null || filtre.getNumExpedient().isEmpty(),
								filtre.getNumExpedient(),
								filtre.getCreadaPer() == null || filtre.getCreadaPer().isEmpty(),
								filtre.getCreadaPer(),
								filtre.getIdentificador() == null || filtre.getIdentificador().isEmpty(),
								filtre.getIdentificador(),
								organs,
								pageable);
//					}
				}
			}
			return complementaNotificacions(notificacions, codisProcedimentsProcessables);
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
	
	private PaginaDto<NotificacioDto> complementaNotificacions(
			Page<NotificacioEntity> notificacions,
			List<String> codisProcedimentsProcessables) {
		PaginaDto<NotificacioDto> resultatPagina = null;
		if (notificacions == null) {
			resultatPagina = paginacioHelper.getPaginaDtoBuida(NotificacioDto.class);
		} else {
			if(notificacions != null) {
				
				for (NotificacioEntity notificacio : notificacions) {
					if (notificacio.getProcediment() != null && notificacio.getEstat() != NotificacioEstatEnumDto.PROCESSADA) {
						notificacio.setPermisProcessar(
								codisProcedimentsProcessables.contains(notificacio.getProcediment().getCodi()));
//								entityComprovarHelper.hasPermisProcediment(
//										notificacio.getProcediment().getId(),
//										PermisEnum.PROCESSAR));
						}
					if (notificacio.getTipusUsuari() != null && notificacio.getTipusUsuari().equals(TipusUsuariEnumDto.APLICACIO) && notificacio.getId() != null) {
						logger.info("Consultant events notificació...");
						List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioIdOrderByDataAsc(notificacio.getId());
						
						if (events != null && events.size() > 0) {
							NotificacioEventEntity lastEvent = events.get(events.size() - 1);
							
							if(lastEvent.isError() && 
										(lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.CALLBACK_CLIENT) ||
										lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT) ||
										lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO) ||
										lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE) || 
										lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT) || 
										lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT) || 
										lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR) || 
										lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR))) {
								logger.info("El darrer event de la notificació " + notificacio.getId()  + " conté un error de tipus: " + lastEvent.getTipus().name());
								notificacio.setErrorLastEvent(true);
							}
						}
					}
					
					List<NotificacioEnviamentEntity> enviamentsPendentsNotifica = notificacioEnviamentRepository.findEnviamentsPendentsNotificaByNotificacio(notificacio);
					if (enviamentsPendentsNotifica != null && ! enviamentsPendentsNotifica.isEmpty()) {
						notificacio.setHasEnviamentsPendents(true);
					}
					
					List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacio(notificacio);
					if (enviamentsPendents != null && ! enviamentsPendents.isEmpty()) {
						notificacio.setHasEnviamentsPendentsRegistre(true);
					}

//					List<NotificacioEnviamentEntity> notificacioEnviaments = notificacioEnviamentRepository.findByNotificacioIdOrderByNotificaEstatDataAndOrderByNotificaEstatDataActualitzacioDesc(notificacio.getId());
//					if(notificacioEnviaments != null && notificacioEnviaments.size() != 0) {
//						notificacio.setNotificaEstat(notificacioEnviaments.get(0).getNotificaEstat());
//					}
				

				}	
			}
			resultatPagina = paginacioHelper.toPaginaDto(
				notificacions,
				NotificacioDto.class);
		
		}
		return resultatPagina;
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
						NotificacioEnviamentEstatEnumDto.valueOf(filtre.getEstat().toString()),
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
	public List<LocalitatsDto> llistarLocalitats(String codiProvincia) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<CodiValor> codiValor = new ArrayList<CodiValor>();
			try {
				codiValor = pluginHelper.llistarLocalitats(codiProvincia);
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
			NotificacioEnviamentEntity destinatari = notificacioEnviamentRepository.findOne(enviamentId);
			entityComprovarHelper.comprovarPermisos(
					destinatari.getNotificacioId(),
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
	public ArxiuDto getDocumentArxiu(
			Long notificacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			String nomDocumetnDefault = "document";
			NotificacioEntity entity = notificacioRepository.findById(notificacioId);
			if(entity.getDocument() != null && entity.getDocument().getArxiuGestdocId() != null) {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				pluginHelper.gestioDocumentalGet(
						entity.getDocument().getArxiuGestdocId(),
						PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
						output);
				return new ArxiuDto(
						entity.getDocument().getArxiuNom() != null ? entity.getDocument().getArxiuNom() : nomDocumetnDefault,
						null,
						output.toByteArray(),
						output.size());	
			}else if(entity.getDocument().getUuid() != null){
				DocumentContingut dc = pluginHelper.arxiuGetImprimible(entity.getDocument().getUuid(), true);
				return new ArxiuDto(
						entity.getDocument().getArxiuNom() != null ? entity.getDocument().getArxiuNom() : nomDocumetnDefault,
						dc.getTipusMime(),
						dc.getContingut(),
						dc.getTamany());
			}else if(entity.getDocument().getCsv() != null){
				DocumentContingut dc = pluginHelper.arxiuGetImprimible(entity.getDocument().getCsv(), false);
				return new ArxiuDto(
						entity.getDocument().getArxiuNom() != null ? entity.getDocument().getArxiuNom() : nomDocumetnDefault,
						dc.getTipusMime(),
						dc.getContingut(),
						dc.getTamany());	
			}else if(entity.getDocument().getUrl() != null){
				try {
					byte[] contingut = downloadUsingStream(entity.getDocument().getUrl(), "document");
					return new ArxiuDto(
							entity.getDocument().getArxiuNom() != null ? entity.getDocument().getArxiuNom() : nomDocumetnDefault,
							"PDF",
							contingut,
							contingut.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		} finally {
			metricsHelper.fiMetrica(timer);
		}
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
			return notificaHelper.notificacioEnviar(notificacioId);
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
			List<NotificacioEnviamentEntity> enviamentsEntity = notificacioEnviamentRepository.findByNotificacio(notificacioEntity);
			
			List<NotificacioEnviamentDtoV2> enviaments = conversioTipusHelper.convertirList(
					enviamentsEntity, 
					NotificacioEnviamentDtoV2.class);
			
			synchronized(CreacioSemaforDto.getCreacioSemafor()) {
				logger.info("Comprovant estat actual notificació (id: " + notificacioEntity.getId() + ")...");
				NotificacioEstatEnumDto estatActual = notificacioRepository.getEstatNotificacio(notificacioEntity.getId());
				logger.info("Estat notificació [Id:" + notificacioEntity.getId() + ", Estat: "+ estatActual + "]");
				
				if (estatActual.equals(NotificacioEstatEnumDto.PENDENT)) {
					registreNotificaHelper.realitzarProcesRegistrarNotificar(
							notificacioEntity,
							enviaments);
				}
			}
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
			enviament.setNotificacio(notificacioRepository.findById(enviament.getNotificacioId()));
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
			String motiu) throws MessagingException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			logger.debug("Refrescant l'estat de la notificació a PROCESSAT (" +
					"notificacioId=" + notificacioId + ")");		
			String resposta = null;
			NotificacioEntity notificacioEntity = entityComprovarHelper.comprovarNotificacio(
					null,
					notificacioId);
			notificacioEntity.updateEstat(NotificacioEstatEnumDto.PROCESSADA);
			notificacioEntity.updateEstatDate(new Date());
			notificacioEntity.updateMotiu(motiu);
			UsuariEntity usuari = usuariHelper.getUsuariAutenticat();
			if(notificacioEntity.getTipusUsuari() == TipusUsuariEnumDto.INTERFICIE_WEB) {
				
				if(usuari.isRebreEmailsNotificacioCreats()) {
					if(usuari.getCodi() == notificacioEntity.getCreatedBy().getCodi()) {
						resposta = emailHelper.prepararEnvioEmailNotificacio(notificacioEntity);
					}else {
						resposta = null;
					}	
				}else {
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
				enviament.refreshNotificaConsulta();
			}
			notificacio.cleanNotificaError();
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
				enviament.refreshSirConsulta();
			}
			notificacio.cleanNotificaError();
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
	public void enviamentRefrescarEstat(Long notificacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			notificaHelper.enviamentRefrescarEstat(notificacioId);
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
	public void enviamentRefrescarEstatRegistre(Long notificacioId) {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			registreHelper.enviamentRefrescarEstatRegistre(
					notificacioId);
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
			notificacio.refreshRegistre();
			notificacio.cleanNotificaError();
			notificacioRepository.saveAndFlush(notificacio);
		} catch (Exception e) {
			logger.debug("Error reactivant consultes d'estat de la notificació (notificacioId=" + notificacioId + ")", e);
		} finally {
			metricsHelper.fiMetrica(timer);
		}		
	}
	
	@Override
	@Transactional
	public void enviamentsRefrescarEstat() {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			List<NotificacioEnviamentEntity> enviaments = notificacioEnviamentRepository.findExpiradesAndNotificaCertificacioDataNull();
			
			for (NotificacioEnviamentEntity enviament : enviaments) {
				logger.debug("Refrescant l'estat de la notificació de Notific@ (enviamentId=" + enviament.getId() + ")");
				try {
					notificaHelper.enviamentRefrescarEstat(enviament.getId());
				
				} catch (Exception e) {
					logger.error("No s'ha pogut refrescar l'estat de l'enviament (enviamentId=" + enviament.getId() + ")");
				}
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}
	
	@Transactional
	@Override
	public FitxerDto recuperarJustificant(
			Long notificacioId,
			Long entitatId) throws JustificantException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
			List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacio(notificacio);
			
			if (enviamentsPendents != null && !enviamentsPendents.isEmpty()) 
				throw new ValidationException("No es pot generar el justificant d'una notificació amb enviaments pendents.");
			
			entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false,
					true, 
					true, 
					false);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ProgresDescarregaDto progres = progresDescarrega.get(auth.getName());
			
			if (progres != null && progres.getProgres() != 0) {
				logger.error("Ja existeix un altre procés iniciat"); 
				progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("es.caib.notib.justificant.proces.iniciant"));
				return null;
			} else {
				//## Únic procés per usuari per evitar sobrecàrrega
				progres = new ProgresDescarregaDto();
				progresDescarrega.put(auth.getName(), progres);
				
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
	public ProgresDescarregaDto justificantEstat() throws JustificantException {
		Timer.Context timer = metricsHelper.iniciMetrica();
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			ProgresDescarregaDto progres = progresDescarrega.get(auth.getName());
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

//	private boolean isNotificaEnviamentsActiu() {
//		String actives = propertiesHelper.getProperty("es.caib.notib.tasca.notifica.enviaments.actiu");
//		if (actives != null) {
//			return new Boolean(actives).booleanValue();
//		} else {
//			return true;
//		}
//	}
//	private int getNotificaEnviamentsProcessarMaxProperty() {
//		return propertiesHelper.getAsInt(
//				"es.caib.notib.tasca.notifica.enviaments.processar.max",
//				10);
//	}
//	
//	private int getRegistreEnviamentsProcessarMaxProperty() {
//		return propertiesHelper.getAsInt(
//				"es.caib.notib.tasca.registre.enviaments.processar.max",
//				10);
//	}
//
//	private boolean isEnviamentActualitzacioEstatActiu() {
//		String actives = propertiesHelper.getProperty("es.caib.notib.tasca.enviament.actualitzacio.estat.actiu");
//		if (actives != null) {
//			return new Boolean(actives).booleanValue();
//		} else {
//			return true;
//		}
//	}
//	private int getEnviamentActualitzacioEstatProcessarMaxProperty() {
//		return propertiesHelper.getAsInt(
//				"es.caib.notib.tasca.enviament.actualitzacio.estat.processar.max",
//				10);
//	}
//
//	private boolean isTasquesActivesProperty() {
//		String actives = propertiesHelper.getProperty("es.caib.notib.tasques.actives");
//		if (actives != null) {
//			return new Boolean(actives).booleanValue();
//		} else {
//			return true;
//		}
//	}
	
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
	
	private NotificaDomiciliViaTipusEnumDto toEnviamentViaTipusEnum(
			EntregaPostalViaTipusEnum viaTipus) {
		if (viaTipus == null) {
			return null;
		}
		return NotificaDomiciliViaTipusEnumDto.valueOf(viaTipus.name());
	}
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceImpl.class);

}
