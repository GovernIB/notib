package es.caib.notib.logic.resourceservice;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.AclHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.resourceservice.EntitatResourceService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Implementació del servei de gestió d'entitats.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatResourceServiceImpl
	extends BaseMutableResourceService<EntitatResource, Long, EntitatResourceEntity>
	implements EntitatResourceService {

	private final AclHelper aclHelper;
	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;
	private final NotibPermissionHelper notibPermissionHelper;

	@PostConstruct
	public void init() {
		register(EntitatResource.Fields.logoCapsalera, new EntitatResourceLogoCapsaleraFieldFileManager());
		register(
			EntitatResource.PERSPECTIVE_PERMISSIONS,
			new EntitatResourcePermisosPerspectiveApplicator(
				authenticationHelper,
				userSessionHelper,
				notibPermissionHelper));
	}

	@Override
	protected void afterConversion(EntitatResourceEntity entity, EntitatResource resource) {
		resource.setAclEntryCount(
				aclHelper.count(AclHelper.ENTITAT_CLASS, entity.getId(), null));
	}

	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		return notibPermissionHelper.entitatAdditionalSpringFilter("id");
	}

	@Override
	protected void beforeCreateEntity(
		EntitatResourceEntity entity,
		EntitatResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		notibPermissionHelper.entitatCheckAdminPermission(
			getResourceClass(),
			null,
			null,
			BasePermission.CREATE);
	}

	@Override
	protected void beforeUpdateEntity(
		EntitatResourceEntity entity,
		EntitatResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		notibPermissionHelper.entitatCheckAdminPermission(
			getResourceClass(),
			resource.getId(),
			resource.getId(),
			BasePermission.WRITE);
	}

	@Override
	protected void beforeDelete(
		EntitatResourceEntity entity,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		notibPermissionHelper.entitatCheckAdminPermission(
			getResourceClass(),
			entity.getId(),
			entity.getId(),
			BasePermission.DELETE);
	}

	/**
	 * Perspectiva per a emplenar els camps que indiquen els tipus de remeses que es poden crear.
	 *   - crearNotificacions
	 *   - crearComunicacions
	 *   - crearSir
	 */
	@RequiredArgsConstructor
	public static class EntitatResourcePermisosPerspectiveApplicator implements PerspectiveApplicator<EntitatResourceEntity, EntitatResource> {
		private final AuthenticationHelper authenticationHelper;
		private final UserSessionHelper userSessionHelper;
		private final NotibPermissionHelper notibPermissionHelper;
		@Override
		public void applySingle(
			String code,
			EntitatResourceEntity entity,
			EntitatResource resource) throws PerspectiveApplicationException {
			boolean isRoleUser = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_USER);
			Long currentEntitatId = userSessionHelper.getCurrentEntitatId();
			// Només els usuaris normals poden crear remeses
			if (isRoleUser && currentEntitatId != null) {
				resource.setCrearNotificacions(
					checkPermisRemesa(
						notibPermissionHelper.getOrganGestorCreatePermissionForEnviamentTipus(EnviamentTipus.NOTIFICACIO),
						notibPermissionHelper.getProcedimentCreatePermissionForEnviamentTipus(EnviamentTipus.NOTIFICACIO),
						false,
						false));
				resource.setCrearComunicacions(
					checkPermisRemesa(
						notibPermissionHelper.getOrganGestorCreatePermissionForEnviamentTipus(EnviamentTipus.COMUNICACIO),
						notibPermissionHelper.getProcedimentCreatePermissionForEnviamentTipus(EnviamentTipus.COMUNICACIO),
						null,
						true));
				resource.setCrearSir(
					checkPermisRemesa(
						notibPermissionHelper.getOrganGestorCreatePermissionForEnviamentTipus(EnviamentTipus.SIR),
						notibPermissionHelper.getProcedimentCreatePermissionForEnviamentTipus(EnviamentTipus.SIR),
						null,
						true));
			} else {
				resource.setCrearNotificacions(false);
				resource.setCrearComunicacions(false);
				resource.setCrearSir(false);
			}
		}
		/**
		 * Es mira si es tenen permisos per a crear un tipus de remesa.Es verifica si es te el permís corresponent
		 * sobre algun òrgan gestor, sobre algun procediment/servei o sobre alguna combinació procediment/servei -
		 * òrgan gestor.
		 *
		 * @param permisOrgansGestors
		 *            el permís sobre els òrgans gestors que es vol comprovar.
		 * @param permisProcediments
		 *            el permís sobre els procediments/serveis que es vol comprovar.
		 * @param isServei
		 *            false si es volen consultar els procediments, true si es volen consultar els serveis o null si és
		 *            volen consultar tant procediments com serveis.
		 * @param isComunicacio
		 *            indica si s'està comprovant una comunicació.
		 * @return true si es tenen permisos per a crear el tipus de remesa o false en cas contrari.
		 */
		private boolean checkPermisRemesa(
			Permission permisOrgansGestors,
			Permission permisProcediments,
			Boolean isServei,
			boolean isComunicacio) {
			// Si no té permís sobre cap òrgan gestor → fora
			if (notibPermissionHelper.organGestorIdsWithPermissionRecursive(permisOrgansGestors).isEmpty()) {
				return false;
			}
			// Si és comunicació, comprovam si té permís per a fer comunicacions sense procediment sobre algun òrgan
			// gestor.
			if (isComunicacio && !notibPermissionHelper.
				organGestorIdsWithPermissionRecursive(ExtendedPermission.PERM7).
				isEmpty()) {
				return true;
			}
			// Comprovam si es tenen permisos sobre algun procediment/servei no comú
			if (!notibPermissionHelper.
				procedimentServeiNoComuIdsWithPermission(permisProcediments, isServei).
				isEmpty()) {
				return true;
			}
			// Comprovam si es tenen permisos sobre algun procediment/servei comú
			return !notibPermissionHelper.
				procedimentServeiComuOrganGestorIdsWithPermission(permisProcediments, isServei).
				isEmpty();
		}
	}

	/**
	 * FieldFileManager pel camp de logo de la capçalera.
	 */
	public static class EntitatResourceLogoCapsaleraFieldFileManager implements FieldFileManager<EntitatResourceEntity> {
		@Override
		public FileReference read(
				EntitatResourceEntity entity,
				String fieldName) {
			byte[] content = entity.getLogoCapsalera();
			if (content != null) {
				return new FileReference(
						"logo.jpg",
						content,
						"image/jpeg",
						content.length);
			} else {
				return null;
			}
		}
		@Override
		public void save(EntitatResourceEntity entity, String fieldName, FileReference fileReference) {
			entity.setLogoCapsalera(fileReference != null ? fileReference.getContent() : null);
		}
		@Override
		public void delete(EntitatResourceEntity entity, String fieldName) {
			entity.setLogoCapsalera(null);
		}
	}

}
