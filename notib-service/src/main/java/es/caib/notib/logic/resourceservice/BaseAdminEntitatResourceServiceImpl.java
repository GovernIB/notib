package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.EntitatPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotDeletedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.notib.logic.intf.base.model.Resource;
import es.caib.notib.persist.resourceentity.AdminEntitatResourceEntity;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * Servei base pels recursos que depenen d'una entitat i son gestionats normalment pels administradors d'entitat.
 * Per a que els serveis que estenen d'aquesta classe no donin errors és important tenir em compte aquests punts:
 * - L'entitat JPA que gestiona aquest servei ha d'estendre de la classe BaseAdminEntitatResourceEntity.
 * - L'entitat JPA que gestiona aquest servei han de tenir un camp anomenat "entitat" que fa referència l'entitat a la
 *   qual pertany.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public abstract class BaseAdminEntitatResourceServiceImpl<R extends Resource<Long>, E extends AdminEntitatResourceEntity<R>>
	extends BaseMutableResourceService<R, Long, E> {

	protected final UserSessionHelper userSessionHelper;
	protected final AuthenticationHelper authenticationHelper;
	protected final EntitatPermissionHelper entitatPermissionHelper;

	/*
	 * Si l'usuari actual no és un superadministrador, només es mostren els recursos amb la mateixa entitat que la
	 * seleccionada a la sessió.
	 */
	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		boolean isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (!isRoleSuper) {
			Long currentEntitatId = userSessionHelper.getCurrentEntitatId();
			if (currentEntitatId != null) {
				return "entitat.id:" + currentEntitatId;
			} else {
				return "entitat.id is null";
			}
		} else {
			return null;
		}
	}

	/*
	 * Comprovacions per a crear un recurs:
	 *   - Hi ha una entitat seleccionada a la sessió.
	 *   - L'entitat del recurs que es crea és la mateixa que l'entitat seleccionada a la sessió.
	 *   - L'usuari actual te permisos d'administració sobre l'entitat seleccionada a la sessió.
	 */
	@Override
	protected void beforeCreateSave(
		E entity,
		R resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		if (currentEntitat != null) {
			if (Objects.equals(entity.getEntitat(), currentEntitat)) {
				entitatPermissionHelper.checkEntitatAdminPermission(
					getResourceClass(),
					null,
					entity.getEntitat().getId(),
					BasePermission.CREATE);
			} else {
				throw new ResourceNotCreatedException(
					getResourceClass(),
					"Not allowed to create a " + getResourceClass() + " belonging to a different entitat than the " +
						"one selected in the session (sessionEntitatId=" + currentEntitat.getId() + ")");
			}
		} else {
			throw new ResourceNotCreatedException(
				getResourceClass(),
				"Not allowed to create a " + getResourceClass() + " without any entitat selected in session");
		}
	}

	/*
	 * Comprovacions per a modificar un recurs:
	 *   - Hi ha una entitat seleccionada a la sessió.
	 *   - L'entitat del recurs que es modifica és la mateixa que l'entitat seleccionada a la sessió.
	 *   - L'usuari actual te permisos d'administració sobre l'entitat seleccionada a la sessió.
	 */
	@Override
	protected void beforeUpdateEntity(
		E entity,
		R resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		if (currentEntitat != null) {
			if (Objects.equals(entity.getEntitat(), currentEntitat)) {
				entitatPermissionHelper.checkEntitatAdminPermission(
					getResourceClass(),
					entity.getId(),
					entity.getEntitat().getId(),
					BasePermission.WRITE);
			} else {
				throw new ResourceNotUpdatedException(
					getResourceClass(),
					"" + entity.getId(),
					"Not allowed to update a " + getResourceClass() + " belonging to a different entitat than the " +
						"one selected in the session (sessionEntitatId=" + currentEntitat.getId() + ")");
			}
		} else {
			throw new ResourceNotUpdatedException(
				getResourceClass(),
				"" + entity.getId(),
				"Not allowed to update a " + getResourceClass() + " without any entitat selected in session");
		}
	}

	/*
	 * Comprovacions per a esborrar un recurs:
	 *   - Hi ha una entitat seleccionada a la sessió.
	 *   - L'entitat del recurs que s'esborra és la mateixa que l'entitat seleccionada a la sessió.
	 *   - L'usuari actual te permisos d'administració sobre l'entitat seleccionada a la sessió.
	 */
	@Override
	protected void beforeDelete(
		E entity,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		if (currentEntitat != null) {
			if (Objects.equals(entity.getEntitat(), currentEntitat)) {
				entitatPermissionHelper.checkEntitatAdminPermission(
					getResourceClass(),
					entity.getId(),
					entity.getEntitat().getId(),
					BasePermission.DELETE);
			} else {
				throw new ResourceNotDeletedException(
					getResourceClass(),
					"" + entity.getId(),
					"Not allowed to update a " + getResourceClass() + " belonging to a different entitat than the " +
						"one selected in the session (sessionEntitatId=" + currentEntitat.getId() + ")");
			}
		} else {
			throw new ResourceNotDeletedException(
				getResourceClass(),
				"" + entity.getId(),
				"Not allowed to delete a " + getResourceClass() + " entity without any entitat selected in session");
		}
	}

}
