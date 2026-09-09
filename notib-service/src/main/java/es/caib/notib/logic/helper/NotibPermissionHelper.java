package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.notib.logic.intf.base.model.Resource;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.base.util.HttpRequestUtil;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentOrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentResourceRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;

import java.util.*;
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
	private final ProcedimentOrganGestorResourceRepository procedimentOrganGestorResourceRepository;
	// Mateixa propietat que WebSecurityConfig.selectedRoleHttpHeader, per a poder determinar el rol
	// actualment seleccionat directament per capçalera i evitar dependre de si l'Authentication ja té
	// les autoritats correctament restringides a únicament aquest rol.
	@Value("${" + BaseConfig.PROP_SECURITY_ROLE_HTTP_HEADER + ":X-App-Role}")
	private String selectedRoleHttpHeader;

	/**
	 * Crea una expressió Spring Filter per a consultar únicament les entitats sobre les que es tenen permisos.
	 *
	 * @param filterProperty la propietat Spring Filter sobre la que s'ha de fer el filtre.
	 * @return l'expressió Spring Filter a aplicar.
	 */
	public String entitatAdditionalSpringFilter(String filterProperty) {

		// Es determina el rol pel qual s'ha de filtrar directament per la capçalera del rol seleccionat,
		// en lloc de mirar quins rols individuals té l'usuari (isCurrentUserInRole): l'usuari pot tenir
		// concedits més d'un rol alhora (p.ex. usuari i administrador de lectura), i comprovar-los per
		// separat no garanteix que es filtri únicament pel rol amb el que està treballant actualment.
		var rolActual = getRolActualHttpHeader();
		if (rolActual != null) {
			return entitatAdditionalSpringFilter(filterProperty, BaseConfig.ROLE_SUPER.equals(rolActual),
					BaseConfig.ROLE_USER.equals(rolActual),
					BaseConfig.ROLE_ADMIN.equals(rolActual),
					BaseConfig.ROLE_ADMIN_LECTURA.equals(rolActual),
					BaseConfig.ROLE_ORGAN.equals(rolActual));
		}
		// Si no es pot determinar el rol actual per capçalera (p.ex. petició que no prové de la SPA de
		// React) es recorre als rols concedits a l'Authentication actual.
		return entitatAdditionalSpringFilter(filterProperty,
				authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER),
				authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_USER),
				authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN),
				authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN_LECTURA),
				authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN));
	}

	private String entitatAdditionalSpringFilter(
			String filterProperty, boolean isRoleSuper, boolean isRoleUser, boolean isRoleAdmin, boolean isRoleAdminLectura, boolean isRoleAdminOrgan) {

		// Restringeix la consulta si no es te el rol NOT_SUPER o si no es tenen permisos sobre l'entitat del tipus de document.
		if (isRoleSuper) {
			return null;
		}
		// Es calcula el permís a comprovar depenent del rol actual
		Permission permission = null;
		if (isRoleUser) {
			permission = ExtendedPermission.PERM0;
		} else if (isRoleAdmin || isRoleAdminOrgan) {
			permission = ExtendedPermission.PERM2;
		} else if (isRoleAdminLectura) {
			permission = ExtendedPermission.PERMX;
		}
		if (permission != null) {
			// S'obtenen la llista d'ids d'entitats a les qual l'usuari actual te permisos
			var allowedIds = aclHelper.findIdsWithAnyPermission(AclHelper.ENTITAT_CLASS, List.of(permission), aclHelper.getCurrentUserSids().toArray(Sid[]::new));
			var joinedIds = String.join(",", allowedIds);
			if (!joinedIds.isEmpty()) {
				return filterProperty + " in (" + joinedIds + ")";
			}
		}
		// Si no s'ha pogut calcular el permís o no s'ha trobat cap id no dona accés a cap entitat
		return filterProperty + " is null";
	}

	private String getRolActualHttpHeader() {
		return HttpRequestUtil.getCurrentHttpRequest().map(r -> r.getHeader(selectedRoleHttpHeader)).orElse(null);
	}

	/**
	 * Verifica si es tenen permisos per a administrar l'entitat. Si no es tenen permisos es llença una excepció
	 * ResourceNotCreatedException o ResourceNotUpdatedException, depenent del permís que s'està comprovant.
	 *
	 * @param resourceClass la classe del recurs.
	 * @param id l'id del recurs.
	 * @param entitatId l'id de l'entitat.
	 * @param permission el permís que s'està comprovant (només per a crear l'excepció).
	 */
	public void entitatCheckAdminPermissionThrows(Class<? extends Resource<?>> resourceClass, Long id, Long entitatId, Permission permission) {

		var isRoleSuper = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPER);
		if (isRoleSuper) {
			return;
		}
		var permissionGranted = false;
		if (entitatId != null) {
			var userSids =  aclHelper.getCurrentUserSids().toArray(Sid[]::new);
			// PERM2 = Permís d'administrador d'entitat
			permissionGranted = aclHelper.anyPermissionGranted(AclHelper.ENTITAT_CLASS, entitatId, List.of(ExtendedPermission.PERM2), userSids);
		}
		if (permissionGranted) {
			return;
		}
		if (Objects.equals(permission, BasePermission.CREATE)) {
			throw new ResourceNotCreatedException(resourceClass, "Not allowed to create " + resourceClass.getSimpleName());
		}
		var permissionText = Objects.equals(permission, BasePermission.DELETE) ? "delete" : "update";
		throw new ResourceNotUpdatedException(resourceClass, "" + id, "Not allowed to " + permissionText + " " + resourceClass.getSimpleName());
	}

	/**
	 * Verifica si es te el permís sobre l'entitat especificada.
	 *
	 * @param entitatId id de l'entitat sobre la qual es vol comprovar el permís.
	 * @param permission el permís que es vol comprovar.
	 * @return true si es tenen permisos o false en cas contrari.
	 */
	public boolean entitatPermissionAllowed(Long entitatId, Permission permission) {

		if (entitatId == null) {
			return false;
		}
		return aclHelper.anyPermissionGranted(AclHelper.ENTITAT_CLASS, entitatId, List.of(permission), aclHelper.getCurrentUserSids().toArray(Sid[]::new));
	}

	/**
	 * Verifica si es te el permís especificat sobre l'entitat actual.
	 *
	 * @param permission el permís que es vol comprovar.
	 * @return true si es tenen permisos o false en cas contrari.
	 */
	public boolean currentEntitatPermissionAllowed(Permission permission) {
		return entitatPermissionAllowed(userSessionHelper.getCurrentEntitatId(), permission);
	}

	/**
	 * Verifica si es te el permís sobre l'òrgan gestor especificat.
	 *
	 * @param organGestorId id de l'òrgan gestor sobre el qual es vol comprovar el permís.
	 * @param permission el permís que es vol comprovar.
	 * @return true si es tenen permisos o false en cas contrari.
	 */
	public boolean organGestorPermissionAllowed(Long organGestorId, Permission permission) {

		if (organGestorId == null) {
			return false;
		}
		var userSids = aclHelper.getCurrentUserSids().toArray(Sid[]::new);
		return aclHelper.anyPermissionGranted(AclHelper.ORGAN_GESTOR_CLASS, organGestorId, List.of(permission), userSids);
	}

	/**
	 * Verifica si es te el permís especificat sobre l'òrgan gestor actual.
	 *
	 * @param permission el permís que es vol comprovar.
	 * @return true si es tenen permisos o false en cas contrari.
	 */
	public boolean currentOrganGestorPermissionAllowed(Permission permission) {
		return organGestorPermissionAllowed(userSessionHelper.getCurrentOrganGestorId(), permission);
	}

	/**
	 * Retorna la llista d'ids d'òrgans gestors de l'entitat actual sobre els que l'usuari actual te el permís
	 * especificat. Aquesta llista s'emplena de forma recursiva perquè si es tenen permisos sobre un òrgan gestor
	 * pare també es tenen permisos sobre els seus fills. Aquesta recursivitat no és infinita, només es revisen
	 * els fills amb 4 nivells de profunditat.
	 *
	 * @param permission el permís que es vol comprovar.
	 * @return la llista d'ids d'òrgans gestors.
	 */
	public List<Long> organGestorIdsWithPermissionRecursive(Permission permission) {

		// Obté la llista d'organs gestors amb el permís assignat directament
		var userSids = aclHelper.getCurrentUserSids().toArray(Sid[]::new);
		var idsWithPermission = aclHelper.findIdsWithAnyPermission(AclHelper.ORGAN_GESTOR_CLASS, List.of(permission), userSids)
							  	.stream().map(Long::valueOf).collect(Collectors.toSet());
		// Retorna la llista d'òrgans gestors amb el permís assignat directament o a algun dels seus pares.
		// Per a optimitzar la consulta només mira els pares fins a 4 nivells per damunt.
		var currentEntitatId = userSessionHelper.getCurrentEntitatId();
		var codis = organGestorResourceRepository.findCodisByIdsIn(idsWithPermission);
		return organGestorResourceRepository.findIdsByEntitatIdAndCodisRecursiveL4(currentEntitatId, codis);
	}

	/**
	 * Retorna la llista d'ids de procediments/serveis no comuns de l'entitat actual sobre els que l'usuari actual te
	 * el permís especificat.
	 *
	 * @param permission el permís que es vol comprovar.
	 * @param isServei false si es volen consultar els procediments, true si es volen consultar els serveis o null si és volen consultar tant procediments com serveis.
	 * @return la llista d'ids de procediments/serveis no comuns.
	 */
	public List<Long> procedimentServeiNoComuIdsWithPermission(Permission permission, Boolean isServei) {

		// Obté la llista de procediments/serveis amb el permís assignat.
		Set<Long> idsWithPermission = aclHelper.findIdsWithAnyPermission(
			AclHelper.PROCEDIMENT_CLASS,
			List.of(permission),
			aclHelper.getCurrentUserSids().toArray(Sid[]::new)).
			stream().map(Long::valueOf).collect(Collectors.toSet());
		// Només retorna els procediments/serveis no comuns que existeixen a la base de dades.
		Long currentEntitatId = userSessionHelper.getCurrentEntitatId();
		ProcSerTipusEnum procSerTipus = getProcSerTipusForQuery(isServei);
		return procedimentResourceRepository.findIdsByEntitatIdAndTipusAndIdInAndComuFalseAndActiuTrue(currentEntitatId, procSerTipus, idsWithPermission);
	}

	/**
	 * Retorna la llista d'ids de les combinacions procediment/servei - organ gestor de l'entitat actual sobre
	 * els que l'usuari actual te el permís especificat.
	 *
	 * @param permission el permís que es vol comprovar.
	 * @param isServei false si es volen consultar els procediments,
 *                     true si es volen consultar els serveis
	 *                 null si és volen consultar tant procediments com serveis.
	 * @return la llista d'ids de procediments/serveis comuns.
	 */
	public List<Long> procedimentServeiComuOrganGestorIdsWithPermission(Permission permission, Boolean isServei) {

		// Obté la llista de combinacions procediment/servei amb el permís assignat.
		var userSids = aclHelper.getCurrentUserSids().toArray(Sid[]::new);
		var idsWithPermissionString = aclHelper.findIdsWithAnyPermission(AclHelper.PROCEDIMENT_ORGAN_CLASS, List.of(permission), userSids);
		var idsWithPermission = idsWithPermissionString.stream().map(Long::valueOf).collect(Collectors.toSet());
		var procedimentOrganGestorIdsRequireDirectPermissionTrue = procedimentOrganGestorIdsWithRequireDirectPermission(idsWithPermission, true, isServei);
		var procedimentOrganGestorIdsRequireDirectPermissionFalse = procedimentOrganGestorIdsWithRequireDirectPermission(idsWithPermission, false, isServei);
		var allIds = new HashSet<>(procedimentOrganGestorIdsRequireDirectPermissionTrue);
		allIds.addAll(procedimentOrganGestorIdsRequireDirectPermissionFalse);
		return new ArrayList<>(allIds);
	}

	/**
	 * Retorna el permís de l'òrgan gestor per a la creació d'enviaments d'un tipus determinat.
	 *
	 * @param enviamentTipus el tipus d'enviament.
	 * @return el permís corresponent.
	 */
	public Permission getOrganGestorNotificacioCreatePermission(EnviamentTipus enviamentTipus) {

		if (EnviamentTipus.COMUNICACIO.equals(enviamentTipus)) {
			return ExtendedPermission.PERM5;
		}
		if (EnviamentTipus.SIR.equals(enviamentTipus)) {
			return ExtendedPermission.PERM6;
		}
		return ExtendedPermission.PERM4;
	}

	/**
	 * Retorna el permís del procediment per a la creació d'enviaments d'un tipus determinat.
	 *
	 * @param enviamentTipus el tipus d'enviament.
	 * @return el permís corresponent.
	 */
	public Permission getProcedimentNotificacioCreatePermission(EnviamentTipus enviamentTipus) {

		if (EnviamentTipus.COMUNICACIO.equals(enviamentTipus)) {
			return ExtendedPermission.PERM8;
		}
		if (EnviamentTipus.SIR.equals(enviamentTipus)) {
			return ExtendedPermission.PERM7;
		}
		return ExtendedPermission.PERM5;
	}

	/**
	 * Retorna el conjunt d'ids necessari per a fer una verificació de permisos de notificacions.
	 *
	 * @param organGestorPermission el permís a verificar per l'òrgan gestor.
	 * @param procedimentPermission el permís a verificar pel procediment.
	 * @return la llista d'ids
	 */
	public IdsToCheckNotificacioPermission getIdsToCheckNotificacioPermission(Permission organGestorPermission, Permission procedimentPermission) {

		var organGestorIds = organGestorIdsWithPermissionRecursive(organGestorPermission);
		var procedimentNoComuIds = procedimentServeiNoComuIdsWithPermission(procedimentPermission, null);
		var procedimentComuOrganGestorIds = procedimentServeiComuOrganGestorIdsWithPermission(procedimentPermission, null);
		return new IdsToCheckNotificacioPermission(organGestorIds, procedimentNoComuIds, procedimentComuOrganGestorIds);
	}

	/**
	 * Filtra la llista d'ids de les combinacions procediment/servei - organ gestor segons el valor del camp
	 * requireDirectPermission al procediment/servei:
	 *   - Si el procediment/servei te el camp a true es verifica si es te el permís sobre la combinació organ gestor -
	 *     procediment.
	 *   - Si el procediment/servei te el camp a false es verifica si es te el permís sobre la combinació organ gestor -
	 *     procediment i si la combinació òrgan gestor - procediment és d'un òrgan gestor amb permís de procediments
	 *     comuns.
	 *
	 * @param idsWithPermission la llista de combinacions procediment/servei.
	 * @param requireDirectPermission el valor del camp "requereix permís directe" del procediment/servei. Si aquest camp és false només
 *           						  es retornen les combinacions procediment/servei - organ gestor que tenen l'òrgan gestor amb el permís de procediments comuns.
	 * @param isServei false si es volen consultar els procediments, true si es volen consultar els serveis o null si és volen consultar tant procediments com serveis.
	 * @return el conjunt d'ids de les combinacions procediment/servei - organ gestor.
	 */
	private Set<Long> procedimentOrganGestorIdsWithRequireDirectPermission(Set<Long> idsWithPermission, boolean requireDirectPermission, Boolean isServei) {

		// Només retorna les combinacions procediment/servei comuns que existeixen a la base de dades.
		var currentEntitatId = userSessionHelper.getCurrentEntitatId();
		var procSerTipus = getProcSerTipusForQuery(isServei);
		var organGestorIds = !requireDirectPermission ? organGestorWithProcedimentsComunsPermission() : null;
		return procedimentOrganGestorResourceRepository.findIdsComprovacioPermisos(currentEntitatId, procSerTipus, requireDirectPermission, true, organGestorIds, idsWithPermission);
	}

	private Set<Long> organGestorWithProcedimentsComunsPermission() {

		var userSids = aclHelper.getCurrentUserSids().toArray(Sid[]::new);
		var idsWithPermission = aclHelper.findIdsWithAnyPermission(AclHelper.ORGAN_GESTOR_CLASS, List.of(ExtendedPermission.PERM3),userSids)
								.stream().map(Long::valueOf).collect(Collectors.toSet());

		// Si no n'hi ha cap no pot retornar null perquè la consulta retornaria tots els resultats i no h'ha de retornar cap.
		return !idsWithPermission.isEmpty() ? idsWithPermission : Set.of(-1L);
	}

	private ProcSerTipusEnum getProcSerTipusForQuery(Boolean isServei) {

		if (isServei == null) {
			return null;
		}
		return isServei ? ProcSerTipusEnum.SERVEI : ProcSerTipusEnum.PROCEDIMENT;
	}

	@Getter
	@AllArgsConstructor
	public static class IdsToCheckNotificacioPermission {

		private List<Long> organGestorIds;
		private List<Long> procedimentNoComuIds;
		private List<Long> procedimentComuOrganGestorIds;

		public boolean isEmpty() {

			return organGestorIds != null && organGestorIds.isEmpty()
					&& procedimentNoComuIds != null && procedimentNoComuIds.isEmpty()
					&& procedimentComuOrganGestorIds != null && procedimentComuOrganGestorIds.isEmpty();
		}
	}

}
