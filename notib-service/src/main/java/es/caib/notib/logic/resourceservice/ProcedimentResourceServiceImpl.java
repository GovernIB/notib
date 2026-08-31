package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.resourceservice.ProcedimentResourceService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ServeiService;
import es.caib.notib.logic.procSer.ComuOnChangeLogicProcessor;
import es.caib.notib.persist.resourceentity.EntregaCieResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import es.caib.notib.persist.resourcerepository.EntregaCieResourceRepository;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorCieResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorPostalResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentOrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementació del servei de gestió de procediments.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class ProcedimentResourceServiceImpl extends BaseAdminEntitatResourceServiceImpl<ProcedimentResource, ProcedimentResourceEntity> implements ProcedimentResourceService {

	private final AclHelper aclHelper;
	private final PagadorPostalResourceRepository pagadorPostalResourceRepository;
	private final PagadorCieResourceRepository pagadorCieResourceRepository;
	private final EntregaCieResourceRepository entregaCieResourceRepository;
	private final ProcedimentResourceRepository procedimentResourceRepository;
	private final OrganGestorResourceRepository organGestorResourceRepository;
	private final ProcedimentOrganGestorResourceRepository procedimentOrganGestorResourceRepository;
	private final PaginacioHelper paginacioHelper;
	private final ProcedimentService procedimentService;
	private final ServeiService serveiService;

	public ProcedimentResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		NotibPermissionHelper notibPermissionHelper,
		AclHelper aclHelper,
		PagadorPostalResourceRepository pagadorPostalResourceRepository,
		PagadorCieResourceRepository pagadorCieResourceRepository,
		EntregaCieResourceRepository entregaCieResourceRepository,
		ProcedimentResourceRepository procedimentResourceRepository,
		OrganGestorResourceRepository organGestorResourceRepository,
		ProcedimentOrganGestorResourceRepository procedimentOrganGestorResourceRepository, PaginacioHelper paginacioHelper,
		ProcedimentService procedimentService, ServeiService serveiService) {

		super(userSessionHelper, authenticationHelper, notibPermissionHelper);
		this.aclHelper = aclHelper;
		this.pagadorPostalResourceRepository = pagadorPostalResourceRepository;
		this.pagadorCieResourceRepository = pagadorCieResourceRepository;
		this.entregaCieResourceRepository = entregaCieResourceRepository;
		this.procedimentResourceRepository = procedimentResourceRepository;
		this.organGestorResourceRepository = organGestorResourceRepository;
		this.procedimentOrganGestorResourceRepository = procedimentOrganGestorResourceRepository;
		this.paginacioHelper = paginacioHelper;
		this.procedimentService = procedimentService;
		this.serveiService = serveiService;
	}

	@PostConstruct
	public void init() {
		register(ProcedimentResource.Fields.comu, new ComuOnChangeLogicProcessor(organGestorResourceRepository, userSessionHelper));
	}

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		var superFilter = super.additionalSpringFilter(currentSpringFilter, namedQueries);
		var isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
		if (isRoleAdminOrgan && notibPermissionHelper.currentOrganGestorPermissionAllowed(BasePermission.ADMINISTRATION)) {
			var permisComuns = notibPermissionHelper.currentOrganGestorPermissionAllowed(ExtendedPermission.PERM3);
			superFilter += " and (organGestor.id:" + userSessionHelper.getCurrentOrganGestorId()
							+ (permisComuns ? " or comu:true)" : "");
		}
		return superFilter;
	}

	@Override
	protected void completeResource(ProcedimentResource resource) {

//		if (!resource.isComu()) {
//			return;
//		}
//		var entitat = userSessionHelper.getCurrentEntitat();
//
//		resource.setOrganGestor(null);
	}

	@Override
	protected Page<ProcedimentResourceEntity> entityRepositoryFindEntities(String quickFilter, String filter, String[] namedQueries, Pageable pageable) {

		var userRoles = authenticationHelper.getCurrentUserRoles();
		if (userRoles == null || userRoles.length != 1) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}
		var entitat = userSessionHelper.getCurrentEntitatId();
//		filter += " and entitat.id:" + entitat;
		Sort processedSort = toProcessedSort(pageable.getSort());
		List<ProcedimentResourceEntity> procediments;
		var rol = userRoles[0];
		if (BaseConfig.ROLE_ADMIN.equals(rol) || BaseConfig.ROLE_ADMIN_LECTURA.equals(rol)) {
			Specification<ProcedimentResourceEntity> specification = toFindProcessedSpecification(quickFilter, filter, namedQueries);
			procediments = procedimentResourceRepository.findAll(specification, processedSort);
		} else if (BaseConfig.ROLE_ORGAN.equals(rol)) {
			Specification<ProcedimentResourceEntity> specification = toFindProcessedSpecification(quickFilter, filter, namedQueries);
			procediments = procedimentResourceRepository.findAll(specification, processedSort);
		} else {
			var isProcediment = filter.contains("tipus:'PROCEDIMENT'");
			var isServei = filter.contains("tipus:'SERVEI'");
			var codisValor = isProcediment ? procedimentService.getProcedimentsOrgan(entitat, null, null, RolEnumDto.valueOf(rol), PermisEnum.CONSULTA)
								: isServei ? serveiService.getServeisOrgan(entitat, null, null, RolEnumDto.valueOf(rol), PermisEnum.CONSULTA) : null;
			if (codisValor == null) {
				return new PageImpl<>(new ArrayList<>(), pageable, 0);
			}
			procediments = procedimentResourceRepository.findAllById(codisValor.stream().map(CodiValorOrganGestorComuDto::getId).collect(Collectors.toList()));
		}
		if (pageable.isUnpaged()) {
			return new PageImpl<>(procediments, pageable, procediments.size());
		}
		Pageable processedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), processedSort);
		return paginacioHelper.toPage(procediments, processedPageable);
	}

	@Override
	protected void afterConversion(ProcedimentResourceEntity entity, ProcedimentResource resource) {

		var count = aclHelper.count(AclHelper.PROCEDIMENT_CLASS, resource.getId(), null);
		if (resource.isComu()) {
			var procSerIds = procedimentOrganGestorResourceRepository.findProcOrganIdByProcediment(resource.getId());
			for (var procSerId : procSerIds) {
				count += aclHelper.count(AclHelper.PROCEDIMENT_ORGAN_CLASS, procSerId, null);
			}
		}
		resource.setAclEntryCount(count);
		if (entity.getEntregaCie() == null) {
			return;
		}
		var pagadorCie = entity.getEntregaCie().getPagadorCie();
		resource.setEntregaCiePagadorCie(ResourceReference.toResourceReference(pagadorCie.getId(), pagadorCie.getNom()));
		var pagadorPostal = entity.getEntregaCie().getPagadorPostal();
		resource.setEntregaCiePagadorPostal(ResourceReference.toResourceReference(pagadorPostal.getId(), pagadorPostal.getNomContracteNum()));
	}

	@Override
	protected void beforeUpdateSave(ProcedimentResourceEntity entity, ProcedimentResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {

		if (!resource.isEntregaCieActiva()) {
			entity.setEntregaCie(null);
			return;
		}
		if (resource.getEntregaCiePagadorPostal() == null || resource.getEntregaCiePagadorCie() == null) {
			return;
		}
		var entregaCie = entity.getEntregaCie();
		var pagadorPostal = pagadorPostalResourceRepository.findById(resource.getEntregaCiePagadorPostal().getId());
		var pagadorCie = pagadorCieResourceRepository.findById(resource.getEntregaCiePagadorCie().getId());
		if (pagadorPostal.isEmpty() || pagadorCie.isEmpty()) {
			return;
		}
		if (entregaCie == null) {
			entregaCie = EntregaCieResourceEntity.builder().build();
			entity.setEntregaCie(entregaCie);
		}
		entregaCie.setPagadorPostal(pagadorPostal.get());
		entregaCie.setPagadorCie(pagadorCie.get());
		entregaCieResourceRepository.save(entregaCie);
	}

	@Override
	protected void beforeDelete(ProcedimentResourceEntity entity, Map<String, AnswerRequiredException.AnswerValue> answers) {

		var procSerOrgans = procedimentOrganGestorResourceRepository.findProcOrganIdByProcediment(entity.getId());
		procedimentOrganGestorResourceRepository.deleteAllById(procSerOrgans);
	}

	@Override
	public boolean validarCodiNoRepetit(Long id, String codi) {

		var procediments =  id != null ? procedimentResourceRepository.findByIdNotLikeAndCodi(id, codi) : procedimentResourceRepository.findByCodi(codi);
		return procediments.isEmpty();
	}

	@Override
	public boolean validarNomNoRepetit(Long id, String nom) {

		var procediments =  id != null ? procedimentResourceRepository.findByIdNotLikeAndNom(id, nom) : procedimentResourceRepository.findByNom(nom);
		return procediments.isEmpty();
	}
}
