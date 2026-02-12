package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.PagadorCieResource;
import es.caib.notib.logic.intf.resourceservice.PagadorCieResourceService;
import es.caib.notib.persist.resourceentity.PagadorCieResourceEntity;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de gestió de pagadors CIE.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class PagadorCieResourceServiceImpl
	extends BaseAdminEntitatResourceServiceImpl<PagadorCieResource, PagadorCieResourceEntity>
	implements PagadorCieResourceService {

	private final OrganGestorResourceRepository organGestorResourceRepository;

	public PagadorCieResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		EntitatPermissionHelper entitatPermissionHelper,
		OrganGestorResourceRepository organGestorResourceRepository) {
		super(userSessionHelper, authenticationHelper, entitatPermissionHelper);
		this.organGestorResourceRepository = organGestorResourceRepository;
	}

	@Override
	protected void beforeCreateSave(
		PagadorCieResourceEntity entity,
		PagadorCieResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		super.beforeCreateSave(entity, resource, answers);
		updateOrgansGestors(entity, resource);
	}

	@Override
	protected void beforeUpdateEntity(
		PagadorCieResourceEntity entity,
		PagadorCieResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		super.beforeUpdateEntity(entity, resource, answers);
		updateOrgansGestors(entity, resource);
	}

	@Override
	protected void afterConversion(
		PagadorCieResourceEntity entity,
		PagadorCieResource resource) {
		// Emplena el camp organGestorEmissor del recurs a partir de l'òrgan gestor de l'entitat.
		resource.setOrganGestorEmissor(
			ResourceReference.toResourceReference(
				entity.getOrganGestor().getId(),
				entity.getOrganGestor().getNom()));
		// Emplena el camp organGestorPagador del recurs a partir del camp organismePagadorCodi de l'entitat.
		organGestorResourceRepository.findByEntitatAndCodi(
			entity.getEntitat(),
			entity.getOrganismePagadorCodi()).
			ifPresent(o -> resource.setOrganGestorPagador(
				ResourceReference.toResourceReference(
					o.getId(),
					o.getNom())));
	}

	private void updateOrgansGestors(
		PagadorCieResourceEntity entity,
		PagadorCieResource resource) {
		// Emplena el camp organGestor de l'entitat a partir del camp organGestorEmissor del recurs.
		organGestorResourceRepository.findById(resource.getOrganGestorEmissor().getId()).
			ifPresent(entity::setOrganGestor);
		// Emplena el camp organismePagadorCodi de l'entitat a partir del camp organGestorPagador del recurs.
		organGestorResourceRepository.findById(resource.getOrganGestorPagador().getId()).
			ifPresent(o -> entity.setOrganismePagadorCodi(o.getCodi()));
	}

}
