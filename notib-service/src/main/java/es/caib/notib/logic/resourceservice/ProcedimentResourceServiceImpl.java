package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.resourceservice.ProcedimentResourceService;
import es.caib.notib.persist.resourceentity.EntregaCieResourceEntity;
import es.caib.notib.persist.resourceentity.PagadorCieResourceEntity;
import es.caib.notib.persist.resourceentity.PagadorPostalResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import es.caib.notib.persist.resourcerepository.EntregaCieResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorCieResourceRepository;
import es.caib.notib.persist.resourcerepository.PagadorPostalResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

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

	public ProcedimentResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		NotibPermissionHelper notibPermissionHelper,
		AclHelper aclHelper,
		PagadorPostalResourceRepository pagadorPostalResourceRepository,
		PagadorCieResourceRepository pagadorCieResourceRepository,
		EntregaCieResourceRepository entregaCieResourceRepository) {

		super(userSessionHelper, authenticationHelper, notibPermissionHelper);
		this.aclHelper = aclHelper;
		this.pagadorPostalResourceRepository = pagadorPostalResourceRepository;
		this.pagadorCieResourceRepository = pagadorCieResourceRepository;
		this.entregaCieResourceRepository = entregaCieResourceRepository;
	}

	@PostConstruct
	public void init() {
		register(ProcedimentResource.Fields.comu, new ProcedimentResourceServiceImpl.ComuOnChangeLogicProcessor());
	}

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {

		var superFilter = super.additionalSpringFilter(currentSpringFilter, namedQueries);
		var isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
		if (isRoleAdminOrgan && notibPermissionHelper.currentOrganGestorPermissionAllowed(BasePermission.ADMINISTRATION)) {
			var currentOrganGestorId = userSessionHelper.getCurrentOrganGestorId();
			superFilter += " and organGestor.id: " + currentOrganGestorId;
		}
		return superFilter;
	}

	@Override
	protected void completeResource(ProcedimentResource resource) {

		if (resource.isComu()) {
			resource.setOrganGestor(null);
		}
	}

	@Override
	protected void afterConversion(ProcedimentResourceEntity entity, ProcedimentResource resource) {

		resource.setAclEntryCount(aclHelper.count(AclHelper.PROCEDIMENT_CLASS, entity.getId(), null));
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

	/*
	 * Lògica onChange pel camp comu. Segons el valor d'aquest camp canvien els camps visibles / habilitats.
	 */
	public static class ComuOnChangeLogicProcessor implements OnChangeLogicProcessor<ProcedimentResource> {

		@Override
		public void onChange(Serializable id, ProcedimentResource previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, ProcedimentResource target) {

			if ((boolean)fieldValue) {
				target.setFieldEntregaCieHidden(true);
				target.setFieldOrganGestorDisabled(true);
				target.setOrganGestor(ResourceReference.toResourceReference(0L, "A04003003, Govern de les Illes Balears"));
				return;
			}
			target.setFieldEntregaCieHidden(false);
			target.setFieldOrganGestorDisabled(false);
			target.setOrganGestor(null);
		}
	}

}
