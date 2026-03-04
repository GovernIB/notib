package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.ProcedimentResource;
import es.caib.notib.logic.intf.resourceservice.ProcedimentResourceService;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;

/**
 * Implementació del servei de gestió de procediments.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class ProcedimentResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<ProcedimentResource, ProcedimentResourceEntity>
	implements ProcedimentResourceService {

	private final AclHelper aclHelper;

	public ProcedimentResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		NotibPermissionHelper notibPermissionHelper,
		AclHelper aclHelper) {
		super(userSessionHelper, authenticationHelper, notibPermissionHelper);
		this.aclHelper = aclHelper;
	}

	@PostConstruct
	public void init() {
		register(ProcedimentResource.Fields.comu, new ProcedimentResourceServiceImpl.ComuOnChangeLogicProcessor());
	}

	@Override
	protected void afterConversion(ProcedimentResourceEntity entity, ProcedimentResource resource) {
		resource.setAclEntryCount(
			aclHelper.count(AclHelper.PROCEDIMENT_CLASS, entity.getId(), null));
	}

	/*
	 * Lògica onChange pel camp comu. Segons el valor d'aquest camp canvien els camps visibles / habilitats.
	 */
	private static class ComuOnChangeLogicProcessor implements OnChangeLogicProcessor<ProcedimentResource> {
		@Override
		public void onChange(
			Serializable id,
			ProcedimentResource previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			String[] previousFieldNames,
			ProcedimentResource target) {
			if ((boolean)fieldValue) {
				target.setFieldEntregaCieHidden(true);
				target.setFieldOrganGestorDisabled(true);
				target.setOrganGestor(ResourceReference.toResourceReference(
					0L,
					"A04003003, Govern de les Illes Balears"));
			} else {
				target.setFieldEntregaCieHidden(false);
				target.setFieldOrganGestorDisabled(false);
				target.setOrganGestor(null);
			}
		}
	}

}
