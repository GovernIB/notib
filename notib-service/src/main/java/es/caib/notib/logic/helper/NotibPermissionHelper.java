package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.notib.logic.intf.base.model.Resource;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentOrganGestorResourceRepository;
import es.caib.notib.persist.resourcerepository.ProcedimentResourceRepository;
import lombok.RequiredArgsConstructor;
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
	 * especificat. Aquesta llista s'emplena de forma recursiva perquè si es tenen permisos sobre un òrgan gestor
	 * pare també es tenen permisos sobre els seus fills. Aquesta recursivitat no és infinita, només es revisen
	 * els fills amb 4 nivells de profunditat.
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
		Long currentEntitatId = userSessionHelper.getCurrentEntitatId();
		List<String> codis = organGestorResourceRepository.findCodisByIdsIn(idsWithPermission);
		return organGestorResourceRepository.findIdsByEntitatIdAndCodisRecursiveL4(
			currentEntitatId,
			codis);
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
		return procedimentResourceRepository.findIdsByEntitatIdAndTipusAndIdInAndComuFalseAndActiuTrue(
			currentEntitatId,
			procSerTipus,
			idsWithPermission);
	}

	/**
	 * Retorna la llista d'ids de les combinacions procediment/servei - organ gestor de l'entitat actual sobre
	 * els que l'usuari actual te el permís especificat.
	 *
	 * @param permission
	 *            el permís que es vol comprovar.
	 * @param isServei
	 *            false si es volen consultar els procediments, true si es volen consultar els serveis o null si és
	 *            volen consultar tant procediments com serveis.
	 * @return la llista d'ids de procediments/serveis comuns.
	 */
	public List<Long> procedimentServeiComuOrganGestorIdsWithPermission(
		Permission permission,
		Boolean isServei) {
		// Obté la llista de combinacions procediment/servei amb el permís assignat.
		Set<Long> idsWithPermission = aclHelper.findIdsWithAnyPermission(
				AclHelper.PROCEDIMENT_ORGAN_CLASS,
				List.of(permission),
				aclHelper.getCurrentUserSids().toArray(Sid[]::new)).
			stream().map(Long::valueOf).collect(Collectors.toSet());
		Set<Long> procedimentOrganGestorIdsRequireDirectPermissionTrue = procedimentOrganGestorIdsWithRequireDirectPermission(
			idsWithPermission,
			true,
			isServei);
		Set<Long> procedimentOrganGestorIdsRequireDirectPermissionFalse = procedimentOrganGestorIdsWithRequireDirectPermission(
			idsWithPermission,
			false,
			isServei);
		Set<Long> allIds = new HashSet<>(procedimentOrganGestorIdsRequireDirectPermissionTrue);
		allIds.addAll(procedimentOrganGestorIdsRequireDirectPermissionFalse);
		return new ArrayList<>(allIds);
	}

	/**
	 * Retorna el permís de l'òrgan gestor per a la creació d'enviaments d'un tipus determinat.
	 *
	 * @param enviamentTipus
	 *            el tipus d'enviament.
	 * @return el permís corresponent.
	 */
	public Permission getOrganGestorNotificacioCreatePermission(EnviamentTipus enviamentTipus) {
		if (EnviamentTipus.COMUNICACIO.equals(enviamentTipus)) {
			return ExtendedPermission.PERM5;
		} else if (EnviamentTipus.SIR.equals(enviamentTipus)) {
			return ExtendedPermission.PERM6;
		} else {
			return ExtendedPermission.PERM4;
		}
	}

	/**
	 * Retorna el permís del procediment per a la creació d'enviaments d'un tipus determinat.
	 *
	 * @param enviamentTipus
	 *            el tipus d'enviament.
	 * @return el permís corresponent.
	 */
	public Permission getProcedimentNotificacioCreatePermission(EnviamentTipus enviamentTipus) {
		if (EnviamentTipus.COMUNICACIO.equals(enviamentTipus)) {
			return ExtendedPermission.PERM8;
		} else if (EnviamentTipus.SIR.equals(enviamentTipus)) {
			return ExtendedPermission.PERM7;
		} else {
			return ExtendedPermission.PERM5;
		}
	}

	/*
	 * Condició en format Spring Filter per a mostrar només les notificacions sobre les que es tenen permisos. Les
	 * notificacions es poden veure si es compleix alguna de les següents condicions:
	 *   a) L'usuari te permís sobre l'òrgan gestor de la notificació.
	 *   b) La notificació te un procediment no comú i l'usuari te permís sobre aquest procediment.
	 *   c) La notificació te un procediment comú amb "requereix permisos directes" i l'usuari te permís
	 *      sobre la combinació organ gestor - procediment de la notificació.
	 *   d) La notificació te un procediment comú sense "requereix permisos directes",
	 *      l'usuari te permís sobre la combinació organ gestor - procediment de la notificació i les combinacions
	 *      òrgan gestor - procediment son únicament dels òrgans gestors amb permís de procediments comuns.
	 */
	public String notificacioSpringFilterWithReadPermission(String fieldPrefix) {
		List<Long> organGestorIds = organGestorIdsWithPermissionRecursive(BasePermission.READ);
		List<Long> procedimentNoComuIds = procedimentServeiNoComuIdsWithPermission(
			BasePermission.READ,
			null);
		List<Long> procedimentComuOrganGestorIds = procedimentServeiComuOrganGestorIdsWithPermission(
			BasePermission.READ,
			null);
		List<String> permissionOrConditions = new ArrayList<>();
		// a)
		String joinedOrganGestorIds = organGestorIds.stream().
			map(String::valueOf).collect(Collectors.joining(","));
		if (!joinedOrganGestorIds.isEmpty()) {
			permissionOrConditions.add(fieldPrefix + "organGestor.id in (" + joinedOrganGestorIds + ")");
		}
		// b)
		String joinedProcedimentNoComuIds = procedimentNoComuIds.stream().
			map(String::valueOf).collect(Collectors.joining(","));
		if (!joinedProcedimentNoComuIds.isEmpty()) {
			permissionOrConditions.add(fieldPrefix + "procediment.id in (" + joinedProcedimentNoComuIds + ")");
		}
		// c) o d)
		String joinedProcedimentComuOrganGestorIds = procedimentComuOrganGestorIds.stream().
			map(String::valueOf).collect(Collectors.joining(","));
		if (!joinedProcedimentComuOrganGestorIds.isEmpty()) {
			permissionOrConditions.add(fieldPrefix + "procedimentOrganGestor.id in (" + joinedProcedimentComuOrganGestorIds + ")");
		}
		return String.join(" or ", permissionOrConditions);
	}

	/*
	 * Es verifica si es tenen permisos per a crear la notificació. Les condicions que es verifiquen son les mateixes
	 * del mètode springFilterWithReadPermission().
	 */
	public void notificacioCheckCreatePermission(NotificacioResourceEntity entity) {
		Permission organGestorPermission = getOrganGestorNotificacioCreatePermission(
			entity.getEnviamentTipus());
		Permission procedimentPermission = getProcedimentNotificacioCreatePermission(
			entity.getEnviamentTipus());
		List<Long> organGestorIds = organGestorIdsWithPermissionRecursive(organGestorPermission);
		List<Long> procedimentNoComuIds = procedimentServeiNoComuIdsWithPermission(
			procedimentPermission,
			null);
		List<Long> procedimentComuOrganGestorIds = procedimentServeiComuOrganGestorIdsWithPermission(
			procedimentPermission,
			null);
		Long organGestorId = entity.getOrganGestor().getId();
		Long procedimentId = entity.getProcediment().getId();
		Long procedimentOrganGestorId = entity.getProcedimentOrganGestor().getId();
		boolean permissionGranted = (organGestorId != null && organGestorIds.contains(organGestorId)) || // a)
			(procedimentId != null && procedimentNoComuIds.contains(procedimentId)) || // b)
			(procedimentOrganGestorId != null && procedimentComuOrganGestorIds.contains(procedimentOrganGestorId)); // c) o d)
		if (!permissionGranted) {
			throw new ResourceNotCreatedException(
				NotificacioResource.class,
				"Not allowed to create notification. Permission check failed.");
		}
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
	 * @param idsWithPermission
	 *            la llista de combinacions procediment/servei.
	 * @param requireDirectPermission
	 *            el valor del camp "requereix permís directe" del procediment/servei. Si aquest camp és false només
	 *            es retornen les combinacions procediment/servei - organ gestor que tenen l'òrgan gestor amb el permís
	 *            de procediments comuns.
	 * @param isServei
	 *            false si es volen consultar els procediments, true si es volen consultar els serveis o null si és
	 *            volen consultar tant procediments com serveis.
	 * @return el conjunt d'ids de les combinacions procediment/servei - organ gestor.
	 */
	private Set<Long> procedimentOrganGestorIdsWithRequireDirectPermission(
		Set<Long> idsWithPermission,
		boolean requireDirectPermission,
		Boolean isServei) {
		// Només retorna les combinacions procediment/servei comuns que existeixen a la base de dades.
		Long currentEntitatId = userSessionHelper.getCurrentEntitatId();
		ProcSerTipusEnum procSerTipus = getProcSerTipusForQuery(isServei);
		Set<Long> organGestorIds = !requireDirectPermission ? organGestorWithProcedimentsComunsPermission() : null;
		return procedimentOrganGestorResourceRepository.findIdsComprovacioPermisos(
			currentEntitatId,
			procSerTipus,
			requireDirectPermission,
			true,
			organGestorIds,
			idsWithPermission);
	}

	private Set<Long> organGestorWithProcedimentsComunsPermission() {
		Set<Long> idsWithPermission = aclHelper.findIdsWithAnyPermission(
				AclHelper.ORGAN_GESTOR_CLASS,
				List.of(ExtendedPermission.PERM3),
				aclHelper.getCurrentUserSids().toArray(Sid[]::new)).
			stream().map(Long::valueOf).collect(Collectors.toSet());
		if (!idsWithPermission.isEmpty()) {
			return idsWithPermission;
		} else {
			// Si no n'hi ha cap no pot retornar null perquè la consulta retornaria tots els resultats i no h'ha de
			// retornar cap.
			return Set.of(-1L);
		}
	}

	private ProcSerTipusEnum getProcSerTipusForQuery(Boolean isServei) {
		if (isServei == null) return null;
		return isServei ? ProcSerTipusEnum.SERVEI : ProcSerTipusEnum.PROCEDIMENT;
	}

}
