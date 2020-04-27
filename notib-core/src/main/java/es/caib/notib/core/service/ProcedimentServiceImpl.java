package es.caib.notib.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.CodiAssumpteDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentFiltreDto;
import es.caib.notib.core.api.dto.ProcedimentFormDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.TipusAssumpteDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentFormEntity;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PermisosHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.ProcedimentHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.GrupProcedimentRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.ProcedimentFormRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.plugin.registre.AutoritzacioRegiWeb3Enum;
import es.caib.notib.plugin.registre.CodiAssumpte;
import es.caib.notib.plugin.registre.Llibre;
import es.caib.notib.plugin.registre.Oficina;
import es.caib.notib.plugin.registre.Organisme;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.TipusAssumpte;

/**
 * Implementació del servei de gestió de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ProcedimentServiceImpl implements ProcedimentService{

	@Resource
	private ProcedimentRepository procedimentRepository;
	@Resource
	private ProcedimentFormRepository procedimentFormRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private ProcedimentHelper procedimentHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private GrupService grupService;
	@Resource
	private GrupRepository grupRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private GrupProcedimentRepository grupProcedimentRepository;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private CacheHelper cacheHelper;
	
	@Override
	public ProcedimentDto create(
			Long entitatId,
			ProcedimentDto procediment) {
		logger.debug("Creant un nou procediment ("
				+ "procediment=" + procediment + ")");
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		PagadorPostalEntity pagadorPostal = null;
		PagadorCieEntity pagadorCie = null;
		if (procediment.getPagadorpostal() != null) {
			pagadorPostal = entityComprovarHelper.comprovarPagadorPostal(
					procediment.getPagadorpostal().getId());
		}
		if (procediment.getPagadorcie() != null) {
			pagadorCie = entityComprovarHelper.comprovarPagadorCie(
					procediment.getPagadorcie().getId());
		}
		
		ProcedimentEntity procedimentEntity = procedimentRepository.save(
				ProcedimentEntity.getBuilder(
						procediment.getCodi(),
						procediment.getNom(),
						procediment.getRetard(),
						procediment.getCaducitat(),
						entitat,
						pagadorPostal,
						pagadorCie,
						procediment.isAgrupar(),
						procediment.getLlibre(),
						procediment.getLlibreNom(),
						procediment.getOficina(),
						procediment.getOficinaNom(),
						procediment.getOrganGestor(),
						procediment.getOrganGestorNom(),
						procediment.getTipusAssumpte(),
						procediment.getTipusAssumpteNom(),
						procediment.getCodiAssumpte(),
						procediment.getCodiAssumpteNom()).build());
		
		return conversioTipusHelper.convertir(
				procedimentEntity, 
				ProcedimentDto.class);
	}

	@Override
	public ProcedimentDto update(
			Long entitatId,
			ProcedimentDto procediment,
			boolean isAdmin) throws NotFoundException {
		logger.debug("Actualitzant procediment ("
				+ "procediment=" + procediment + ")");
		
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		ProcedimentEntity procedimentEntity = null;
		PagadorPostalEntity pagadorPostal = null;
		PagadorCieEntity pagadorCie = null;
		if(!isAdmin) {
			procedimentEntity = entityComprovarHelper.comprovarProcediment(
					entitat,
					procediment.getId());
		} else {
			procedimentEntity = procedimentRepository.findOne(procediment.getId());
		}
		
		if (procediment.getPagadorpostal() != null) {
			pagadorPostal = entityComprovarHelper.comprovarPagadorPostal(
					procediment.getPagadorpostal().getId());
		}
		if (procediment.getPagadorcie() != null) {
			pagadorCie = entityComprovarHelper.comprovarPagadorCie(
					procediment.getPagadorcie().getId());
		}
		
		List<GrupProcedimentEntity> grupsProcediment = grupProcedimentRepository.findByProcediment(procedimentEntity);
		
		if (!procediment.isAgrupar()) {
			grupProcedimentRepository.delete(grupsProcediment);
		}
		
		procedimentEntity.update(
					procediment.getCodi(),
					procediment.getNom(),
					entitat,
					pagadorPostal,
					pagadorCie,
					procediment.getRetard(),
					procediment.getCaducitat(),
					procediment.isAgrupar(),
					procediment.getLlibre(),
					procediment.getLlibreNom(),
					procediment.getOficina(),
					procediment.getOficinaNom(),
					procediment.getOrganGestor(),
					procediment.getOrganGestorNom(),
					procediment.getTipusAssumpte(),
					procediment.getTipusAssumpteNom(),
					procediment.getCodiAssumpte(),
					procediment.getCodiAssumpteNom());
		
		procedimentRepository.save(procedimentEntity);
			
		return conversioTipusHelper.convertir(
				procedimentEntity, 
				ProcedimentDto.class);
	}

	@Override
	public ProcedimentDto delete(
			Long entitatId,
			Long id) throws NotFoundException {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		
		ProcedimentEntity procedimentEntity = entityComprovarHelper.comprovarProcediment(
				entitat, 
				id);
	
		//Eliminar procediment
		procedimentRepository.delete(procedimentEntity);
		
		return conversioTipusHelper.convertir(
				procedimentEntity, 
				ProcedimentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ProcedimentDto findById(
			Long entitatId,
			boolean isAdministrador,
			Long id) {
		logger.debug("Consulta del procediment ("
				+ "entitatId=" + entitatId + ", "
				+ "id=" + id + ")");
		EntitatEntity entitat = null;
			
		if (entitatId != null && !isAdministrador)
			entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false, 
					false, 
					false);

		ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
				entitat, 
				id, 
				false, 
				false, 
				false,
				false);
		ProcedimentDto resposta = conversioTipusHelper.convertir(
				procediment,
				ProcedimentDto.class);
		
		if (resposta != null) {
			procedimentHelper.omplirPermisosPerMetaNode(resposta, false);
		}
		
		return resposta;
	}
	
	@Transactional(readOnly = true)
	@Override
	public ProcedimentDto findByCodi(
			Long entitatId,
			String codiProcediment) throws NotFoundException {
		logger.debug("Consulta del procediment ("
				+ "entitatId=" + entitatId + ", "
				+ "codi=" + codiProcediment + ")");
		EntitatEntity entitat = null;
			
		if (entitatId != null)
			entitat = entityComprovarHelper.comprovarEntitat(
					entitatId, 
					false, 
					false, 
					false);
		
		ProcedimentEntity procediment = procedimentRepository.findByCodiAndEntitat(codiProcediment, entitat);
		
		return conversioTipusHelper.convertir(
				procediment, 
				ProcedimentDto.class);
	}


	@Override
	public List<ProcedimentDto> findByEntitat(Long entitatId) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		
		List<ProcedimentEntity> procediment = procedimentRepository.findByEntitat(entitat);
		
		return conversioTipusHelper.convertirList(
				procediment,
				ProcedimentDto.class);
		
	}
	
	@Override
	@Transactional
	public PaginaDto<ProcedimentFormDto> findAmbFiltrePaginat(
			Long entitatId,
			boolean isUsuari,
			boolean isUsuariEntitat,
			boolean isAdministrador,
			ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(entitatId);
		List<EntitatEntity> entitatsActiva = entitatRepository.findByActiva(true);
		List<Long> entitatsActivaId = new ArrayList<Long>();
		
		for (EntitatEntity entitatActiva : entitatsActiva) {
			entitatsActivaId.add(entitatActiva.getId());
		}
		Page<ProcedimentFormEntity> procediments = null;
		PaginaDto<ProcedimentFormDto> procedimentsPage = null;
		
		if (filtre == null) {
			
			if (isUsuariEntitat) {
				procediments = procedimentFormRepository.findAmbEntitatActual(
						entitatActual.getId(),
						paginacioHelper.toSpringDataPageable(paginacioParams));
				procedimentsPage =  paginacioHelper.toPaginaDto(
						procediments,
						ProcedimentFormDto.class);
			} else if (isAdministrador) {
				procediments = procedimentFormRepository.findAmbEntitatActiva(
						entitatsActivaId,
						paginacioHelper.toSpringDataPageable(paginacioParams));
				procedimentsPage =  paginacioHelper.toPaginaDto(
						procediments,
						ProcedimentFormDto.class);
			}
		} else {
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams);
			
			if (isUsuariEntitat) {
				procediments = procedimentFormRepository.findAmbEntitatAndFiltre(
						entitatActual.getId(),
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() == null ? "" : filtre.getCodi(),
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() == null ? "" : filtre.getNom(),
						pageable);
				
				procedimentsPage = paginacioHelper.toPaginaDto(
						procediments, 
						ProcedimentFormDto.class);
				
			} else if (isAdministrador) {
				procediments = procedimentFormRepository.findAmbFiltre(
						filtre.getCodi() == null || filtre.getCodi().isEmpty(), 
						filtre.getCodi() == null ? "" : filtre.getCodi(),
						filtre.getNom() == null || filtre.getNom().isEmpty(),
						filtre.getNom() == null ? "" : filtre.getNom(),
						pageable);
				
				procedimentsPage =  paginacioHelper.toPaginaDto(
						procediments,
						ProcedimentFormDto.class);
			}
		}
		for (ProcedimentFormDto procediment: procedimentsPage.getContingut()) {
			List<PermisDto> permisos = permisosHelper.findPermisos(
					procediment.getId(),
					ProcedimentEntity.class);
			List<GrupDto> grups = grupService.findGrupsByProcediment(procediment.getId());
			procediment.setGrups(grups);
			procediment.setPermisos(permisos);
		}
		return procedimentsPage;
	}
	
	@Override
	public List<ProcedimentDto> findAll() {
		logger.debug("Consulta de tots els procediments");
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		return conversioTipusHelper.convertirList(
					procedimentRepository.findAll(),
					ProcedimentDto.class);
	}

	@Override
	public List<ProcedimentGrupDto> findAllGrups() {
		logger.debug("Consulta de tots els procediments");
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		
		List<GrupProcedimentEntity> grupsProcediments = grupProcedimentRepository.findAll();
		return conversioTipusHelper.convertirList(
					grupsProcediments,
					ProcedimentGrupDto.class);
	}
	
	@Override
	public List<ProcedimentDto> findProcedimentsSenseGrups(EntitatDto entitatActual) {
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		List<GrupProcedimentEntity> grupsProcediments = grupProcedimentRepository.findAll();
		List<ProcedimentEntity> procediments = new ArrayList<ProcedimentEntity>();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatActual.getId());
		
		for (GrupProcedimentEntity grupProcedimentEntity : grupsProcediments) {
			procediments.add(procedimentRepository.findOne(grupProcedimentEntity.getProcediment().getId()));
		}
		List<ProcedimentEntity> procedimentsSenseGrups = new ArrayList<ProcedimentEntity>();
		if(procediments.size() > 0) {
			procedimentsSenseGrups = procedimentRepository.findProcedimentsSenseGrups(procediments);	
		}
		if (procediments.isEmpty() && procedimentsSenseGrups.isEmpty()) {
			procedimentsSenseGrups = procedimentRepository.findByEntitat(entitat);
		}
		return conversioTipusHelper.convertirList(
				procedimentsSenseGrups,
				ProcedimentDto.class);
	}
	
	@Transactional
	@Override
	public List<PermisDto> permisFind(
			Long entitatId,
			boolean isAdministrador,
			Long id) {
		logger.debug("Consulta dels permisos del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id +  ")"); 
		EntitatEntity entitat = null;
		
		if (entitatId != null && !isAdministrador)
			entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					true,
					false);
		
		entityComprovarHelper.comprovarProcediment(
				entitat, 
				id, 
				false, 
				false, 
				false, 
				false);
		
		return permisosHelper.findPermisos(
				id,
				ProcedimentEntity.class);
	}
	
	@Transactional
	@Override
	public void permisUpdate(
			Long entitatId,
			Long id,
			PermisDto permis,
			boolean isAdministrador) {
		logger.debug("Modificació del permis del procediment ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permis=" + permis + ")");
		
		if (entitatId != null && !isAdministrador)
			entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					true,
					false);
		entityComprovarHelper.comprovarProcediment(
				null,
				id,
				false,
				false,
				false,
				false);
		permisosHelper.updatePermis(
				id,
				ProcedimentEntity.class,
				permis);
	}
	
	@Transactional
	@Override
	public void grupCreate(
			Long entitatId, 
			Long id, 
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		logger.debug("Modificació del grup del procediment ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permis=" + procedimentGrup + ")");
		ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
				null,
				id,
				false,
				false,
				false,
				false);
		GrupEntity grup = entityComprovarHelper.comprovarGrup(procedimentGrup.getGrup().getId());
		
		GrupProcedimentEntity grupProcedimentEntity = GrupProcedimentEntity.getBuilder(
				procediment, 
				grup).build();
		
		grupProcedimentRepository.saveAndFlush(grupProcedimentEntity);
	}
	
	@Transactional
	@Override
	public void grupUpdate(
			Long entitatId, 
			Long id, 
			ProcedimentGrupDto procedimentGrup) throws NotFoundException {
		logger.debug("Modificació del grup del procediment ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permis=" + procedimentGrup + ")");
		ProcedimentEntity procediment = entityComprovarHelper.comprovarProcediment(
				null,
				id,
				false,
				false,
				false,
				false);
		GrupEntity grup = entityComprovarHelper.comprovarGrup(procedimentGrup.getGrup().getId());
		
		GrupProcedimentEntity grupProcedimentEntity = entityComprovarHelper.comprovarGrupProcediment(
				procedimentGrup.getId());
		
		grupProcedimentEntity.update(procediment, grup);
		
		grupProcedimentRepository.saveAndFlush(grupProcedimentEntity);
	}

	@Transactional
	@Override
	public void grupDelete(
			Long entitatId, 
			Long procedimentGrupId) throws NotFoundException {
		logger.debug("Modificació del grup del procediment ("
				+ "entitatId=" + entitatId +  ", "
				+ "procedimentGrupID=" + procedimentGrupId + ")");
		
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		
		GrupProcedimentEntity grupProcedimentEntity = grupProcedimentRepository.findOne(procedimentGrupId);
		
		grupProcedimentRepository.delete(grupProcedimentEntity);
	}

	
	@Override
	public void permisDelete(
			Long entitatId,
			Long id,
			Long permisId,
			boolean isAdministrador) {
		logger.debug("Eliminació del permis del meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "id=" + id + ", "
				+ "permisId=" + permisId + ")");
		EntitatEntity entitat = null;
		
		if (entitatId != null && !isAdministrador)
			entitat = entityComprovarHelper.comprovarEntitat(
					entitatId,
					false,
					true,
					false);
		
		entityComprovarHelper.comprovarProcediment(
				entitat,
				id,
				false,
				false,
				false,
				false);
		permisosHelper.deletePermis(
				id,
				ProcedimentEntity.class,
				permisId);
	}
	
	@Override
	public boolean hasPermisConsultaProcediment(EntitatDto entitat) {		
		List<ProcedimentDto> resposta = entityComprovarHelper.findPermisProcedimentsUsuariActualAndEntitat(
				new Permission[] {
						ExtendedPermission.READ},
				entitat.getId()
				);
		
		return (resposta.isEmpty()) ? false : true;
	}
	
	@Override
	public boolean hasPermisGestioProcediment(
			Long procedimentId) {
		List<ProcedimentEntity> procediments = new ArrayList<ProcedimentEntity>();
		ProcedimentEntity procediment = procedimentRepository.findById(procedimentId);
		
		procediments.add(procediment);
		
		List<ProcedimentDto> resposta = entityComprovarHelper.findPermisProcediments(
				procediments,
				new Permission[] {
						ExtendedPermission.ADMINISTRATION}
				);
		
		return (resposta.isEmpty()) ? false : true;
	}
	
	@Override
	public boolean hasPermisProcessarProcediment(
			String procedimentCodi,
			Long procedimentId,
			boolean isAdministrador) {
		List<ProcedimentEntity> procediments = new ArrayList<ProcedimentEntity>();
		ProcedimentEntity procediment;
		
		if (!isAdministrador) {
				procediment = procedimentRepository.findById(procedimentId);
		} else {
			procediment = procedimentRepository.findOne(procedimentId);
		}
		if (procediment != null)
			procediments.add(procediment);
		
		List<ProcedimentDto> resposta = entityComprovarHelper.findPermisProcediments(
				procediments,
				new Permission[] {
						ExtendedPermission.PROCESSAR}
				);
		
		return (resposta.isEmpty()) ? false : true;
	}
	
	@Override
	public boolean hasPermisNotificacioProcediment(EntitatDto entitat) {		
		List<ProcedimentDto> resposta = entityComprovarHelper.findPermisProcedimentsUsuariActualAndEntitat(
				new Permission[] {
					ExtendedPermission.NOTIFICACIO},
				entitat.getId()
				);
		
		return (resposta.isEmpty()) ? false : true;
	}
	
	@Override
	public boolean hasGrupPermisConsultaProcediment(
			Map<String, ProcedimentDto> procediments,
			EntitatDto entitat) {		
		List<ProcedimentDto> resposta = entityComprovarHelper.findByGrupAndPermisProcedimentsUsuariActualAndEntitat(
				procediments,
				entitat.getId(),
				new Permission[] {
						ExtendedPermission.READ}
				);
		
		return (resposta.isEmpty()) ? false : true;
	}
	
	@Override
	public boolean hasGrupPermisNotificacioProcediment(
			Map<String, ProcedimentDto> procediments,
			EntitatDto entitat) {		
		List<ProcedimentDto> resposta = entityComprovarHelper.findByGrupAndPermisProcedimentsUsuariActualAndEntitat(
				procediments,
				entitat.getId(),
				new Permission[] {
						ExtendedPermission.NOTIFICACIO}
				);
		
		return (resposta.isEmpty()) ? false : true;
	}

	@Override
	public List<TipusAssumpteDto> findTipusAssumpte(EntitatDto entitat) {
		List<TipusAssumpteDto> tipusAssumpte = new ArrayList<TipusAssumpteDto>();
	
		try {
			List<TipusAssumpte> tipusAssumpteRegistre = pluginHelper.llistarTipusAssumpte(entitat.getDir3Codi());
			
			if (tipusAssumpteRegistre != null)
				for (TipusAssumpte assumpteRegistre : tipusAssumpteRegistre) {
					TipusAssumpteDto assumpte = new TipusAssumpteDto();
					assumpte.setCodi(assumpteRegistre.getCodi());
					assumpte.setNom(assumpteRegistre.getNom());
					
					tipusAssumpte.add(assumpte);
				}
		} catch (RegistrePluginException e) {
			String errorMessage = "No s'han pogut recuperar els codis d'assumpte de l'entitat: " + entitat.getDir3Codi();
			logger.error(
					errorMessage, 
					e.getMessage());
		}
		return tipusAssumpte;
	}

	@Override
	public List<CodiAssumpteDto> findCodisAssumpte(
			EntitatDto entitat,
			String codiTipusAssumpte) {
		List<CodiAssumpteDto> codiAssumpte = new ArrayList<CodiAssumpteDto>();
		
		try {
			List<CodiAssumpte> tipusAssumpteRegistre = pluginHelper.llistarCodisAssumpte(
					entitat.getDir3Codi(),
					codiTipusAssumpte);
			
			if (tipusAssumpteRegistre != null)
				for (CodiAssumpte assumpteRegistre : tipusAssumpteRegistre) {
					CodiAssumpteDto assumpte = new CodiAssumpteDto();
					assumpte.setCodi(assumpteRegistre.getCodi());
					assumpte.setNom(assumpteRegistre.getNom());
					assumpte.setTipusAssumpte(assumpteRegistre.getTipusAssumpte());
					
					codiAssumpte.add(assumpte);
				}
		} catch (RegistrePluginException e) {
			String errorMessage = "No s'han pogut recuperar els codis d'assumpte del tipus d'assumpte: " + codiTipusAssumpte;
			logger.error(
					errorMessage, 
					e.getMessage());
		}
		return codiAssumpte;
	}
	

	@Override
	public List<OrganismeDto> findOrganismes(EntitatDto entitat) {
		List<OrganismeDto> organismes = new ArrayList<OrganismeDto>();
		
		try {
			List<Organisme> organismesRegistre = pluginHelper.llistarOrganismes(entitat.getDir3Codi());
			if (organismes != null) {
				for (Organisme organismeRegistre : organismesRegistre) {
					OrganismeDto organisme = new OrganismeDto();
					organisme.setCodi(organismeRegistre.getCodi());
					organisme.setNom(organismeRegistre.getNom());
					organismes.add(organisme);
				}
			}
		} catch (Exception e) {
			String errorMessage = "No s'han pogut recuperar els organismes de l'entitat: " + entitat.getDir3Codi();
			logger.error(
					errorMessage, 
					e.getMessage());
		}
		return organismes;
	}

	@Override
	public List<OficinaDto> findOficines(EntitatDto entitat) {
		List<OficinaDto> oficines = new ArrayList<OficinaDto>();
		
		try {
			List<Oficina> oficinesRegistre = pluginHelper.llistarOficines(
					entitat.getDir3Codi(), 
					AutoritzacioRegiWeb3Enum.REGISTRE_SORTIDA);
			for (Oficina oficinaRegistre : oficinesRegistre) {
				OficinaDto oficina = new OficinaDto();
				oficina.setCodi(oficinaRegistre.getCodi());
				oficina.setNom(oficinaRegistre.getNom());
				oficines.add(oficina);
			}
		} catch (Exception e) {
			String errorMessage = "No s'han pogut recuperar les oficines de l'entitat: " + entitat.getDir3Codi();
			logger.error(
					errorMessage, 
					e.getMessage());
		}
		return oficines;
	}

	@Override
	public List<LlibreDto> findLlibres(
			EntitatDto entitat, 
			String oficina) {
		List<LlibreDto> llibres = new ArrayList<LlibreDto>();
		
		try {
			List<Llibre> llibresRegistre = pluginHelper.llistarLlibres(
					entitat.getDir3Codi(), 
					oficina, 
					AutoritzacioRegiWeb3Enum.REGISTRE_SORTIDA);
			for (Llibre llibreRegistre : llibresRegistre) {
				LlibreDto llibre = new LlibreDto();
				llibre.setCodi(llibreRegistre.getCodi());
				llibre.setNomCurt(llibreRegistre.getNomCurt());
				llibre.setNomLlarg(llibreRegistre.getNomLlarg());
				llibre.setOrganismeCodi(llibreRegistre.getOrganisme());
				llibres.add(llibre);
			}
 		} catch (Exception e) {
 			String errorMessage = "No s'han pogut recuperar els llibres de l'entitat: " + entitat.getDir3Codi();
			logger.error(
					errorMessage, 
					e.getMessage());
		}
		return llibres;
	}
	
	@Transactional(readOnly = true)
	@Override
	public void refrescarCache(EntitatDto entitat) {
		logger.debug("Preparant per buidar la informació en cache dels procediments...");
		
		cacheHelper.evictFindByGrupAndPermisProcedimentsUsuariActualAndEntitat(entitat.getId());
		cacheHelper.evictFindByPermisProcedimentsUsuariActual(entitat.getId());
		cacheHelper.evictFindPermisProcedimentsUsuariActualAndEntitat(entitat.getId());
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);
}
