/**
 * 
 */
package es.caib.notib.core.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.core.util.Base64;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.LocalitatsDto;
import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliViaTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PaisosDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.ProvinciesDto;
import es.caib.notib.core.api.dto.RegistreIdDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.api.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Enviament;
import es.caib.notib.core.api.ws.notificacio.Persona;
import es.caib.notib.core.entity.DocumentEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.CreacioSemaforDto;
import es.caib.notib.core.helper.EmailHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.RegistreHelper;
import es.caib.notib.core.helper.RegistreNotificaHelper;
import es.caib.notib.core.repository.DocumentRepository;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.GrupProcedimentRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.PersonaRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;

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
	private ProcedimentService procedimentService;
	@Autowired
	private PersonaRepository personaRepository;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private RegistreNotificaHelper registreNotificaHelper;
	@Autowired
	private GrupRepository grupRepository;
	@Autowired
	private GrupProcedimentRepository grupProcedimentRepository;
	@Autowired
	private RegistreHelper registreHelper;
	
	@Transactional(rollbackFor=Exception.class)
	@Override
	public List<NotificacioDto> create(
			Long entitatId, 
			NotificacioDtoV2 notificacio) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		GrupEntity grupNotificacio = null;
		String documentGesdocId = null;
		ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
					null,
				 	notificacio.getProcediment().getId(),
				 	false,
				 	false,
				 	true,
				 	false);
		if (notificacio.getGrup() != null && notificacio.getGrup().getId() != null) {
			grupNotificacio = grupRepository.findOne(notificacio.getGrup().getId());
		}
		if(notificacio.getDocument().getContingutBase64() != null) {
			documentGesdocId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					new ByteArrayInputStream(
							Base64.decode(notificacio.getDocument().getContingutBase64())));
		} else if (notificacio.getDocument().getUuid() != null) {
			DocumentDto document = new DocumentDto();
			String arxiuUuid = notificacio.getDocument().getUuid();
			if (pluginHelper.isArxiuPluginDisponible()) {
				Document documentArxiu = pluginHelper.arxiuDocumentConsultar(
						arxiuUuid, 
						null);
				document.setArxiuNom(documentArxiu.getNom());
				document.setNormalitzat(notificacio.getDocument().isNormalitzat());
				document.setGenerarCsv(notificacio.getDocument().isGenerarCsv());
				document.setUuid(arxiuUuid);
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
				document.setCsv(arxiuCsv);
				notificacio.setDocument(document);
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
					notificacio.getDocument().getCsv()).build());
		}
		// Dades generals de la notificació
		NotificacioEntity.BuilderV2 notificacioBuilder = NotificacioEntity.
				getBuilderV2(
						entitat,
						notificacio.getEmisorDir3Codi(),
						pluginHelper.getNotibTipusComunicacioDefecte(),
						notificacio.getEnviamentTipus(), 
						notificacio.getConcepte(),
						notificacio.getDescripcio(),
						notificacio.getEnviamentDataProgramada(),
						notificacio.getRetard(),
						notificacio.getCaducitat(),
						notificacio.getUsuariCodi(),
						procediment.getCodi(),
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
				if (enviament.getEntregaPostal() != null && !enviament.getEntregaPostal().getViaNom().isEmpty()) {
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
								notificacio, 
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
			List<NotificacioEnviamentDtoV2> enviamentsDto = conversioTipusHelper.convertirList(
					notificacio.getEnviaments(), 
					NotificacioEnviamentDtoV2.class);
			synchronized(CreacioSemaforDto.getCreacioSemafor()) {
				registreNotificaHelper.realitzarProcesRegistrarNotificar(
						notificacioEntity,
						enviamentsDto);	
			}
		}

		List<NotificacioEntity> notificacions = notificacioRepository.findByEntitatId(entitatId);

		return conversioTipusHelper.convertirList(
			notificacions,
			NotificacioDto.class);
	}
	
	@Override
	public NotificacioDtoV2 update(
			Long entitatId,
			NotificacioDtoV2 procediment) throws NotFoundException {
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public NotificacioDtoV2 findAmbId(
			Long id,
			boolean isAdministrador) {
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
						procedimentService.hasPermisProcessarProcediment(
								notificacio.getProcediment().getCodi(),
								notificacio.getProcediment().getId(),
								isAdministrador));
				}	
		}
		
		return conversioTipusHelper.convertir(
				notificacio,
				NotificacioDtoV2.class);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<NotificacioDto> findAmbFiltrePaginat(
			Long entitatId, 
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdministrador,
			List<ProcedimentGrupDto> grupsProcediments,
			Map<String, ProcedimentDto> procediments,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		
		entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				isUsuariEntitat,
				false);		
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitatId);
		List<EntitatEntity> entitatsActiva = entitatRepository.findByActiva(true);
		PaginaDto<NotificacioDto> resultatPagina = null;
		Page<NotificacioEntity> notificacions = null;
		List<String> procedimentsCodisNotib = new ArrayList<String>();
		List<String> grupsProcedimentsCodis = null;
		List<ProcedimentDto> procedimentsPermisConsultaAndAgrupable = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsPermisConsulta = new ArrayList<ProcedimentDto>();
		
		if (isUsuari) {
			if (grupsProcediments.isEmpty()) {
				//Obté tots els procediments amb permís de consutla d'una entitat
				procedimentsPermisConsultaAndAgrupable = entityComprovarHelper.findPermisProcedimentsUsuariActualAndEntitat(
					new Permission[] {
							ExtendedPermission.READ},
					entitatActual);
			} else if (!procediments.isEmpty()) {
				//Obté els procediments amb grup i permís de consulta
				procedimentsPermisConsultaAndAgrupable = entityComprovarHelper.findByGrupAndPermisProcedimentsUsuariActualAndEntitat(
						procediments, 
						entitatActual,
						new Permission[] {
								ExtendedPermission.READ}
						);
				//Procediments amb permís de consulta no agurpables
				List<ProcedimentDto> procedimentsNoAgrupables = new ArrayList<ProcedimentDto>();
				for (Map.Entry<String, ProcedimentDto> procediment : procediments.entrySet()) {
					if (!procediment.getValue().isAgrupar()) {
						procedimentsNoAgrupables.add(procediment.getValue());
					}
				}
//				for(ProcedimentDto procediment: procediments) {
//					if (!procediment.isAgrupar()) {
//						procedimentsNoAgrupables.add(procediment);
//					}
//				}
				procedimentsPermisConsulta = entityComprovarHelper.findByPermisProcedimentsUsuariActual(
						procedimentsNoAgrupables, 	
						entitatActual,
						new Permission[] {
								ExtendedPermission.READ}
						);
				
			}
			if (!procedimentsPermisConsultaAndAgrupable.isEmpty()) {
					for (ProcedimentDto procedimentDto : procedimentsPermisConsultaAndAgrupable) {
						procedimentsCodisNotib.add(procedimentDto.getCodi());
					}
			}
			if (!procedimentsPermisConsulta.isEmpty()) {
				for (ProcedimentDto procedimentDto : procedimentsPermisConsulta) {
					procedimentsCodisNotib.add(procedimentDto.getCodi());
				}
			}
		}
		
		if (filtre == null) {
			//Consulta les notificacions sobre les quals té permis l'usuari actual
			if (isUsuari) {
				//Notificacions sense grups i amb grups
				grupsProcedimentsCodis = new ArrayList<String>();
				for (String codiProcediment : procedimentsCodisNotib) {
					ProcedimentEntity procedimentAgrupable = procedimentRepository.findByCodiAndEntitat(
							codiProcediment,
							entitatActual);

					List<GrupProcedimentEntity> grups = grupProcedimentRepository.findByProcediment(procedimentAgrupable);
					for (GrupProcedimentEntity grup : grups) {
						grupsProcedimentsCodis.add(grup.getGrup().getCodi());
					}
				}
				
				//Quan no hi ha cap procediment amb grups
				if (!procedimentsCodisNotib.isEmpty() && grupsProcedimentsCodis.isEmpty()) {
					notificacions = notificacioRepository.findByProcedimentCodiNotibAndEntitat(
							procedimentsCodisNotib,
							entitatActual,
							paginacioHelper.toSpringDataPageable(paginacioParams));
				} else if (!procedimentsCodisNotib.isEmpty()) {
					notificacions = notificacioRepository.findByProcedimentCodiNotibAndGrupsCodiNotibAndEntitat(
							procedimentsCodisNotib, 
							grupsProcedimentsCodis, 
							entitatActual,
							paginacioHelper.toSpringDataPageable(paginacioParams));
				}
				
			//Consulta els notificacions de l'entitat acutal
			} else if (isUsuariEntitat) {
				notificacions = notificacioRepository.findByEntitatActual(
						entitatActual,
						paginacioHelper.toSpringDataPageable(paginacioParams));
			//Consulta totes les notificacions de les entitats actives
			} else if (isAdministrador) {
				notificacions = notificacioRepository.findByEntitatActiva(
						entitatsActiva,
						paginacioHelper.toSpringDataPageable(paginacioParams));
			}
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
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams);
			if (isUsuari) {
				//Notificacions amb grups
				grupsProcedimentsCodis = new ArrayList<String>();
				for (String codiProcediment : procedimentsCodisNotib) {
					ProcedimentEntity procedimentAgrupable = procedimentRepository.findByCodiAndEntitat(
							codiProcediment,
							entitatActual);

					List<GrupProcedimentEntity> grups = grupProcedimentRepository.findByProcediment(procedimentAgrupable);
					for (GrupProcedimentEntity grup : grups) {
						grupsProcedimentsCodis.add(grup.getGrup().getCodi());
					}
				}
				//Quan no hi ha cap procediment amb grups
				if (!procedimentsCodisNotib.isEmpty() && grupsProcedimentsCodis.isEmpty()) {
					notificacions = notificacioRepository.findAmbFiltreAndProcedimentCodiNotib(
							filtre.getEntitatId() == null,
							filtre.getEntitatId(),
							procedimentsCodisNotib,
							filtre.getEnviamentTipus() == null,
							filtre.getEnviamentTipus(),
							filtre.getConcepte() == null,
							filtre.getConcepte() == null ? "" : filtre.getConcepte(), 
							filtre.getEstat() == null,
							filtre.getEstat(), 
							dataInici == null,
							dataInici,
							dataFi == null,
							dataFi,
							filtre.getTitular() == null || filtre.getTitular().isEmpty(),
							filtre.getTitular() == null ? "" : filtre.getTitular(),
							entitatActual, 
							procediment == null,
							procediment,
							filtre.getTipusUsuari() == null,
							filtre.getTipusUsuari(),
							pageable);
				} else if (!procedimentsCodisNotib.isEmpty()) {
					notificacions = notificacioRepository.findAmbFiltreAndProcedimentCodiNotibAndGrupsCodiNotib(
							filtre.getEntitatId() == null,
							filtre.getEntitatId(),
							procedimentsCodisNotib, 
							grupsProcedimentsCodis, 
							filtre.getEnviamentTipus() == null,
							filtre.getEnviamentTipus(),
							filtre.getConcepte() == null,
							filtre.getConcepte() == null ? "" : filtre.getConcepte(), 
							filtre.getEstat() == null,
							filtre.getEstat(), 
							dataInici == null,
							dataInici,
							dataFi == null,
							dataFi,
							filtre.getTitular() == null || filtre.getTitular().isEmpty(),
							filtre.getTitular() == null ? "" : filtre.getTitular(),
							entitatActual, 
							procediment == null,
							procediment,
							filtre.getTipusUsuari() == null,
							filtre.getTipusUsuari(),
							pageable);
				}
				
				
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
						dataInici == null,
						dataInici,
						dataFi == null,
						dataFi,
						filtre.getTitular() == null || filtre.getTitular().isEmpty(),
						filtre.getTitular(),
						procediment == null,
						procediment,
						filtre.getTipusUsuari() == null,
						filtre.getTipusUsuari(),
						pageable);
			} else if (isAdministrador) {
				notificacions = notificacioRepository.findAmbFiltre(
						filtre.getEntitatId() == null,
						filtre.getEntitatId(),
						filtre.getEnviamentTipus() == null,
						filtre.getEnviamentTipus(),
						filtre.getConcepte() == null,
						filtre.getConcepte(),
						filtre.getEstat() == null,
						filtre.getEstat(),
						dataInici == null,
						dataInici,
						dataFi == null,
						dataFi,
						filtre.getTitular() == null || filtre.getTitular().isEmpty(),
						filtre.getTitular(),
						procediment == null,
						procediment,
						filtre.getTipusUsuari() == null,
						filtre.getTipusUsuari(),
						pageable);
			}
		}
		if (notificacions == null) {
			resultatPagina = paginacioHelper.getPaginaDtoBuida(NotificacioDto.class);
		} else {
			if(notificacions != null) {
				for (NotificacioEntity notificacio : notificacions) {
					if (notificacio.getProcediment() != null && notificacio.getEstat() != NotificacioEstatEnumDto.PROCESSADA) {
						notificacio.setPermisProcessar(
								procedimentService.hasPermisProcessarProcediment(
										notificacio.getProcediment().getCodi(),
										notificacio.getProcediment().getId(),
										isAdministrador));
						}
				}	
			}
			resultatPagina = paginacioHelper.toPaginaDto(
				notificacions,
				NotificacioDto.class);
		
		}
		return resultatPagina;
	}
	
	@Override
	public List<ProcedimentDto> findProcedimentsAmbPermisConsultaAndGrupsAndEntitat(
			Map<String, ProcedimentDto> procediments,
			EntitatDto entitat) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());
		
		return entityComprovarHelper.findByGrupAndPermisProcedimentsUsuariActualAndEntitat(
				procediments,
				entitatActual,
				new Permission[] {
						ExtendedPermission.READ}
				);	
	}
	
	@Override
	public List<ProcedimentDto> findProcedimentsEntitatAmbPermisConsulta(
			EntitatDto entitat) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());
		
		return entityComprovarHelper.findPermisProcedimentsUsuariActualAndEntitat(
				new Permission[] {
						ExtendedPermission.READ},
				entitatActual
				);	
	}
	
	@Override
	public List<ProcedimentDto> findProcedimentsAmbPermisConsulta() {		
		return entityComprovarHelper.findPermisProcediments(
				new Permission[] {
						ExtendedPermission.READ});	
	}

	@Override
	public List<ProcedimentDto> findProcedimentsAmbPermisNotificacio(
			EntitatDto entitat) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());

		return entityComprovarHelper.findPermisProcedimentsUsuariActualAndEntitat(
				new Permission[] {
						ExtendedPermission.NOTIFICACIO},
				entitatActual
				);	
	}
	
	@Override
	public List<ProcedimentDto> findProcedimentsAmbPermisNotificacioAndGrupsAndEntitat(
			Map<String, ProcedimentDto> procediments,
//			List<ProcedimentDto> procediments,
			EntitatDto entitat) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());

		return entityComprovarHelper.findByGrupAndPermisProcedimentsUsuariActualAndEntitat(
				procediments,
				entitatActual,
				new Permission[] {
						ExtendedPermission.NOTIFICACIO}
				);	
	}
	
	@Override
	public List<ProcedimentDto> findProcedimentsAmbPermisNotificacioSenseGrupsAndEntitat(
			List<ProcedimentDto> procediments,
			EntitatDto entitat) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());

		return entityComprovarHelper.findByPermisProcedimentsUsuariActual(
				procediments,
				entitatActual,
				new Permission[] {
						ExtendedPermission.NOTIFICACIO}
				);	
	}
	
	@Override
	public List<ProcedimentDto> findProcedimentsAmbPermisConsultaSenseGrupsAndEntitat(
			List<ProcedimentDto> procediments,
			EntitatDto entitat) {
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitat.getId());

		return entityComprovarHelper.findByPermisProcedimentsUsuariActual(
				procediments,
				entitatActual,
				new Permission[] {
						ExtendedPermission.READ}
				);	
	}
	
	@Override
	public List<ProvinciesDto> llistarProvincies() {
		List<CodiValor> codiValor = new ArrayList<CodiValor>();
		try {
			codiValor = pluginHelper.llistarProvincies();
		} catch (Exception ex) {
			logger.error(
					"Error recuperant les provincies de DIR3CAIB: " + ex);
		}
		return conversioTipusHelper.convertirList(codiValor, ProvinciesDto.class);
	}

	@Override
	public List<LocalitatsDto> llistarLocalitats(String codiProvincia) {
		List<CodiValor> codiValor = new ArrayList<CodiValor>();
		try {
			codiValor = pluginHelper.llistarLocalitats(codiProvincia);
		} catch (Exception ex) {
			logger.error(
					"Error recuperant les provincies de DIR3CAIB: " + ex);
		}
		return conversioTipusHelper.convertirList(codiValor, LocalitatsDto.class);
	}
	
	@Override
	public List<PaisosDto> llistarPaisos() {
		List<CodiValorPais> codiValorPais = new ArrayList<CodiValorPais>();
		try {
			codiValorPais = pluginHelper.llistarPaisos();
		} catch (Exception ex) {
			logger.error(
					"Error recuperant els paisos de DIR3CAIB: " + ex);
		}
		return conversioTipusHelper.convertirList(codiValorPais, PaisosDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long entitatId, 
			Long notificacioId) {
		logger.debug("Consulta dels events de la notificació (" +
				"notificacioId=" + notificacioId + ")");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		return conversioTipusHelper.convertirList(
				notificacioEventRepository.findByNotificacioIdOrderByDataAsc(notificacioId),
				NotificacioEventDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbEnviament(
			Long entitatId, 
			Long notificacioId,
			Long enviamentId) {
		logger.debug("Consulta dels events associats a un destinatari (" +
				"notificacioId=" + notificacioId + ", " + 
				"enviamentId=" + enviamentId + ")");
		NotificacioEnviamentEntity destinatari = notificacioEnviamentRepository.findOne(enviamentId);
		entityComprovarHelper.comprovarPermisos(
				destinatari.getNotificacioId(),
				true,
				true,
				false);
		return conversioTipusHelper.convertirList(
				notificacioEventRepository.findByNotificacioIdOrEnviamentIdOrderByDataAsc(
						notificacioId,
						enviamentId),
				NotificacioEventDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getDocumentArxiu(
			Long notificacioId) {
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
	}
	@Override
	@Transactional(readOnly = true)
	public ArxiuDto enviamentGetCertificacioArxiu(
			Long enviamentId) {
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
	}

	@Override
	public boolean enviar(Long notificacioId) {
		logger.debug("Intentant enviament de la notificació pendent (" +
				"notificacioId=" + notificacioId + ")");
		return notificaHelper.notificacioEnviar(notificacioId);
	}
	
	@Transactional
	@Override
	public List<RegistreIdDto> registrarNotificar(Long notificacioId) {
		logger.debug("Intentant registrar la notificació pendent (" +
				"notificacioId=" + notificacioId + ")");
		List<RegistreIdDto> registresIdDto = new ArrayList<RegistreIdDto>();
		NotificacioEntity notificacioEntity = notificacioRepository.findById(notificacioId);
		List<NotificacioEnviamentEntity> enviamentsEntity = notificacioEnviamentRepository.findByNotificacio(notificacioEntity);
		
		List<NotificacioEnviamentDtoV2> enviaments = conversioTipusHelper.convertirList(
				enviamentsEntity, 
				NotificacioEnviamentDtoV2.class);
		
		synchronized(CreacioSemaforDto.getCreacioSemafor()) {
			registreNotificaHelper.realitzarProcesRegistrarNotificar(
					notificacioEntity,
					enviaments);
		}
		
		return registresIdDto;
	}

	@Override
	@Transactional
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(
			Long entitatId, 
			Long enviamentId) {
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
	}
	
	@Transactional
	@Override
	public String marcarComProcessada(
			Long notificacioId,
			String motiu) throws MessagingException {
		logger.debug("Refrescant l'estat de la notificació a PROCESSAT (" +
				"notificacioId=" + notificacioId + ")");		
		String resposta;
		NotificacioEntity notificacioEntity = entityComprovarHelper.comprovarNotificacio(
				null,
				notificacioId);
		notificacioEntity.updateEstat(NotificacioEstatEnumDto.PROCESSADA);
		notificacioEntity.updateEstatDate(new Date());
		notificacioEntity.updateMotiu(motiu);
		resposta = emailHelper.prepararEnvioEmailNotificacio(notificacioEntity);
		notificacioRepository.saveAndFlush(notificacioEntity);
		
		return resposta;
	}
	
//	// 1. Enviament de notificacions pendents al registre y notific@
//	////////////////////////////////////////////////////////////////
//	@Override
//	@Scheduled(
//			fixedRateString = "${config:es.caib.notib.tasca.registre.enviaments.periode}", 
//			initialDelayString = "${config:es.caib.notib.tasca.registre.enviaments.retard.inicial}")
//	public void registrarEnviamentsPendents() {
//		logger.debug("Cercant notificacions pendents de registrar");
//		int maxPendents = getRegistreEnviamentsProcessarMaxProperty();
//		List<NotificacioEntity> pendents = notificacioRepository.findByNotificaEstatPendent(new PageRequest(0, maxPendents));
//		if (!pendents.isEmpty()) {
//			logger.debug("Realitzant registre per a " + pendents.size()
//					+ " notificacions pendents (màxim=" + maxPendents + ")");
//			for (NotificacioEntity pendent : pendents) {
//				logger.debug(">>> Realitzant registre de la notificació amb identificador: "
//						+ pendent.getId());
//				registrarNotificar(pendent.getId());
//			}
//		} else {
//			logger.debug("No hi ha notificacions pendents de registrar");
//		}
//	}
//	
//	// 2. Enviament de notificacions registrades a Notific@
//	///////////////////////////////////////////////////////
//	@Override
//	@Scheduled(
//			fixedRateString = "${config:es.caib.notib.tasca.notifica.enviaments.periode}",
//			initialDelayString = "${config:es.caib.notib.tasca.notifica.enviaments.retard.inicial}")
//	public void notificaEnviamentsRegistrats() {
//		if (isTasquesActivesProperty() && isNotificaEnviamentsActiu() && notificaHelper.isConnexioNotificaDisponible()) {
//			logger.debug("Cercant notificacions registrades pendents d'enviar a Notifica");
//			int maxPendents = getNotificaEnviamentsProcessarMaxProperty();
//			List<NotificacioEntity> pendents = notificacioRepository.findByNotificaEstatRegistrada(
//					pluginHelper.getNotificaReintentsMaxProperty(), 
//					new PageRequest(0, maxPendents));
//			if (!pendents.isEmpty()) {
//				logger.debug("Realitzant enviaments a Notifica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
//				for (NotificacioEntity pendent: pendents) {
//					logger.debug(">>> Realitzant enviament a Notifica de la notificació amb identificador: " + pendent.getId());
//					notificacioEnviar(pendent.getId());
//				}
//			} else {
//				logger.debug("No hi ha notificacions pendents d'enviar a Notific@");
//			}
//		} else {
//			logger.debug("L'enviament de notificacions a Notific@ està deshabilitada");
//		}
//	}
//	// 2. Actualització de l'estat dels enviaments amb l'estat de Notific@
//	// PENDENT ELIMINAR DESPRÉS DE PROVAR ADVISER
//	//////////////////////////////////////////////////////////////////
//	@Override
//	@Scheduled(
//			fixedRateString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.periode}",
//			initialDelayString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.retard.inicial}")
//	public void enviamentRefrescarEstatPendents() {
//		if (isTasquesActivesProperty() && isEnviamentActualitzacioEstatActiu() && notificaHelper.isConnexioNotificaDisponible()) {
//			logger.debug("Cercant enviaments pendents de refrescar l'estat de Notifica");
//			int maxPendents = getEnviamentActualitzacioEstatProcessarMaxProperty();
//			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findByNotificaRefresc(
//					new PageRequest(0, maxPendents));
//			if (!pendents.isEmpty()) {
//				logger.debug("Realitzant refresc de l'estat de Notifica per a " + pendents.size() + " enviaments (màxim=" + maxPendents + ")");
//				for (NotificacioEnviamentEntity pendent: pendents) {
//					logger.debug(">>> Consultat l'estat a Notific@ de la notificació amb identificador " + pendent.getId() + ", i actualitzant les dades a Notib.");
//					enviamentRefrescarEstat(pendent.getId());
//				}
//			} else {
//				logger.debug("No hi ha enviaments pendents de refrescar l'estat de Notifica");
//			}
//		} else {
//			logger.debug("L'actualització de l'estat dels enviaments amb l'estat de Notific@ està deshabilitada");
//		}
//	}
	
	@Transactional
	@Override
	public void notificacioRegistrar(Long notificacioId) {
		registrarNotificar(notificacioId);
	}
	
	@Transactional
	@Override
	public void notificacioEnviar(Long notificacioId) {
		notificaHelper.notificacioEnviar(notificacioId);
	}
	
	@Transactional
	@Override
	public void enviamentRefrescarEstat(Long notificacioId) {
		notificaHelper.enviamentRefrescarEstat(notificacioId);
	}
	
	@Transactional
	@Override
	public void enviamentRefrescarEstatRegistre(Long notificacioId) {
		registreHelper.enviamentRefrescarEstatRegistre(notificacioId);
	}
	
	private void estatCalcularCampsAddicionals(
			NotificacioEnviamentEntity enviament,
			NotificacioEnviamenEstatDto estatDto) {
		if (enviament.isNotificaError()) {
			NotificacioEventEntity event = enviament.getNotificaErrorEvent();
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
