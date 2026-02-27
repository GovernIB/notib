package es.caib.notib.logic.helper;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.notib.logic.intf.base.model.Resource;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper per a la comprovació de permisos propis de NOTIB.
 *
 * @author Límit Tecnologies
 */
@Component
@RequiredArgsConstructor
public class NotibPermissionHelper {

	private final AclHelper aclHelper;
	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;
	private final OrganGestorResourceRepository organGestorResourceRepository;
	private final ProcedimentResourceRepository procedimentResourceRepository;

	/**
	 * Crea una expressió Spring Filter per a consultar únicament les entitats sobre les que es tenen permisos.
	 *
	 * @param filterProperty
	 *            la propietat Spring Filter sobre la que s'ha de fer el filtre.
	 * @return l'expressió Spring Filter a aplicar.
	 */
	public String entitatAdditionalSpringFilter(String filterProperty) {
		// Restringeix la consulta si no es te el rol NOT_SUPER o si no es tenen permisos sobre l'entitat del
		// tipus de document.
		boolean isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (!isRoleSuper) {
			// Es calcula el permís a comprovar depenent del rol actual
			boolean isRoleUser = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_USER);
			boolean isRoleAdmin = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
			boolean isRoleAdminLectura = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN_LECTURA);
			Permission permission = null;
			if (isRoleUser) {
				permission = ExtendedPermission.PERM0;
			} else if (isRoleAdmin) {
				permission = ExtendedPermission.PERM2;
			} else if (isRoleAdminLectura) {
				permission = ExtendedPermission.PERMX;
			}
			if (permission != null) {
				// S'obtenen la llista d'ids d'entitats a les qual l'usuari actual te permisos
				Set<String> allowedIds = aclHelper.findIdsWithAnyPermission(
					AclHelper.ENTITAT_CLASS,
					List.of(permission),
					aclHelper.getCurrentUserSids().toArray(Sid[]::new));
				String joinedIds = String.join(",", allowedIds);
				if (!joinedIds.isEmpty()) {
					return filterProperty + " in (" + joinedIds + ")";
				}
			}
			// Si no s'ha pogut calcular el permís o no s'ha trobat cap id no dona accés a cap entitat
			return filterProperty + " is null";
		} else {
			return null;
		}
	}

	/**
	 * Verifica si es tenen permisos per a administrar l'entitat.
	 *
	 * @param resourceClass
	 *            la classe del recurs.
	 * @param id
	 *            l'id del recurs.
	 * @param entitatId
	 *            l'id de l'entitat.
	 * @param permission
	 *            el permís que s'està comprovant (només per a crear l'excepció).
	 */
	public void entitatCheckAdminPermission(
		Class<? extends Resource<?>> resourceClass,
		Long id,
		Long entitatId,
		Permission permission) {
		boolean isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (!isRoleSuper) {
			boolean permissionGranted = false;
			if (entitatId != null) {
				permissionGranted = aclHelper.anyPermissionGranted(
					AclHelper.ENTITAT_CLASS,
					entitatId,
					List.of(ExtendedPermission.PERM2), // Permís d'administrador d'entitat
					aclHelper.getCurrentUserSids().toArray(Sid[]::new));
			}
			if (!permissionGranted) {
				if (Objects.equals(permission, BasePermission.CREATE)) {
					throw new ResourceNotCreatedException(
						resourceClass,
						"Not allowed to create " + resourceClass.getSimpleName());
				} else {
					String permissionText = Objects.equals(permission, BasePermission.DELETE) ? "delete" : "update";
					throw new ResourceNotUpdatedException(
						resourceClass,
						"" + id,
						"Not allowed to " + permissionText + " " + resourceClass.getSimpleName());
				}
			}
		}
	}

	/**
	 * Retorna la llista d'ids d'òrgans gestors de l'entitat actual sobre els que l'usuari actual te el permís
	 * especificat.
	 *
	 * @param permission
	 *            el permís que es vol comprovar.
	 * @return la llista d'ids d'òrgans gestors.
	 */
	public List<Long> organGestorIdsWithPermissionRecursive(Permission permission) {
		// Obté la llista d'organs gestors amb el permís assignat directament
		Set<Long> idsWithPermission = aclHelper.findIdsWithAnyPermission(
			AclHelper.ORGAN_GESTOR_CLASS,
			List.of(permission),
			aclHelper.getCurrentUserSids().toArray(Sid[]::new)).
			stream().map(Long::valueOf).collect(Collectors.toSet());
		// Retorna la llista d'òrgans gestors amb el permís assignat directament o a algun dels seus pares.
		// Per a optimitzar la consulta només mira els pares fins a 4 nivells per damunt.
		return organGestorResourceRepository.findIdsByEntitatIdAndCodisRecursiveL4(
			userSessionHelper.getCurrentEntitatId(),
			organGestorResourceRepository.findCodisByIdsIn(idsWithPermission));
	}

	/**
	 * Retorna la llista d'ids de procediments/serveis no comuns de l'entitat actual sobre els que l'usuari actual te
	 * el permís especificat.
	 *
	 * @param permission
	 *            el permís que es vol comprovar.
	 * @param isServei
	 *            false si es volen consultar els procediments, true si es volen consultar els serveis o null si és
	 *            volen consultar tant procediments com serveis.
	 * @return la llista d'ids de procediments/serveis no comuns.
	 */
	public List<Long> procedimentsServeisNoComunsWithPermission(Permission permission, Boolean isServei) {
		// Obté la llista de procediments/serveis amb el permís assignat.
		Set<Long> idsWithPermission = (Set<Long>)aclHelper.findIdsWithAnyPermission(
			AclHelper.ORGAN_GESTOR_CLASS,
			List.of(permission),
			aclHelper.getCurrentUserSids().toArray(Sid[]::new)).
			stream().map(Long::valueOf).collect(Collectors.toSet());
		// Només retorna els procediments/serveis no comuns que existeixen a la base de dades.
		return procedimentResourceRepository.findIdsByEntitatIdAndTipusAndIdInAndComuFalse(
			userSessionHelper.getCurrentEntitatId(),
			getProcSerTipusForQuery(isServei),
			idsWithPermission);
	}

	/**
	 * Retorna la llista d'ids de procediments/serveis comuns de l'entitat actual sobre els que l'usuari actual te el
	 * permís especificat.
	 *
	 * @param permission
	 *            el permís que es vol comprovar.
	 * @param isServei
	 *            false si es volen consultar els procediments, true si es volen consultar els serveis o null si és
	 *            volen consultar tant procediments com serveis.
	 * @return la llista d'ids de procediments/serveis comuns.
	 */
	public List<Long> procedimentsServeisComunsWithPermission(Permission permission, Boolean isServei) {
		// Mira si hi ha algun òrgan gestor amb permís per a procediments/serveis comuns.
		Set<Long> idsWithPermission = (Set<Long>)aclHelper.findIdsWithAnyPermission(
			AclHelper.ORGAN_GESTOR_CLASS,
			List.of(ExtendedPermission.PERM3),
			aclHelper.getCurrentUserSids().toArray(Sid[]::new)).
			stream().map(Long::valueOf).collect(Collectors.toSet());
		if (!idsWithPermission.isEmpty()) {
			// Si hi ha òrgans gestors amb permís per a procediments/serveis comuns retorna la llista de
			// procediments/serveis comuns que no requereixen permís directe.
			return procedimentResourceRepository.findIdsByEntitatIdAndTipusAndComuTrueAndPermisDirecteFalse(
				userSessionHelper.getCurrentEntitatId(),
				getProcSerTipusForQuery(isServei));
		} else {
			return new ArrayList<>();
		}
	}

	private ProcSerTipusEnum getProcSerTipusForQuery(Boolean isServei) {
		if (isServei == null) return null;
		return isServei ? ProcSerTipusEnum.SERVEI : ProcSerTipusEnum.PROCEDIMENT;
	}

}
