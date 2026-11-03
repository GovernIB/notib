package es.caib.notib.logic.resourceservice;

import com.google.common.base.Strings;
import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.model.AccioMassivaResource;
import es.caib.notib.logic.intf.resourceservice.AccioMassivaResourceService;
import es.caib.notib.persist.resourceentity.AccioMassivaResourceEntity;
import es.caib.notib.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementació del servei d'accions massives.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccioMassivaResourceServiceImpl extends BaseMutableResourceService<AccioMassivaResource, Long, AccioMassivaResourceEntity> implements AccioMassivaResourceService {

	private final UserSessionHelper userSessionHelper;
	private final UsuariResourceRepository usuariRepository;
	private final AuthenticationHelper authenticationHelper;
	private final NotibPermissionHelper notibPermissionHelper;

	@PostConstruct
	public void init() {

	}

	@Override
	protected void afterConversion(AccioMassivaResourceEntity entity, AccioMassivaResource resource) {

		var usuari = usuariRepository.findById(entity.getCreatedBy()).orElse(null);
		var nomComplert = entity.getCreatedBy();
		if (usuari == null) {
			log.error("[AccioMassivaResourceServiceImpl.afterConversion] Error buscant l'usuari amb codi " + entity.getCreatedBy());
		} else {
			nomComplert = usuari.getId() + " (" + usuari.getNomSencer() + ")";
		}
		resource.setUsuariNomComplet(nomComplert);
		if (entity.getNumErrors() == entity.getElements().size()) {
			resource.setNumOk(0);
			resource.setNumPendent(0);
			return;
		}
		var numErrors = 0;
		var numOk = 0;
		var numPendent = 0;
		for (var element : entity.getElements()) {
			if (element.getDataExecucio() == null && Strings.isNullOrEmpty(element.getErrorDescripcio())) {
				numPendent++;
				continue;
			}
			if (element.getDataExecucio() != null && !Strings.isNullOrEmpty(element.getErrorDescripcio())) {
				numErrors++;
				continue;
			}
			numOk++;
		}
		resource.setNumErrors(numErrors);
		resource.setNumOk(numOk);
		resource.setNumPendent(numPendent);
	}

	private String estatFiltreActual;

	@Override
	public Page<AccioMassivaResource> findPage(String quickFilter, String filter, String[] namedQueries, String[] perspectives, Pageable pageable) {

		String[] estat = { null };
		String cleaned = filter;
		if (filter != null) {
			java.util.regex.Pattern p = java.util.regex.Pattern.compile("(?i)estat\\s*[:~]\\s*(?:'%?([^'%)]+)%?'|\"%?([^\"%)]+)%?\"|([^\\s)]+))");
			java.util.regex.Matcher m = p.matcher(filter);
			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				String v = m.group(1) != null ? m.group(1) : (m.group(2) != null ? m.group(2) : m.group(3));
				estat[0] = v == null ? null : v.trim().toUpperCase();
				m.appendReplacement(sb, "");
			}
			m.appendTail(sb);
			cleaned = sb.toString().trim();
			cleaned = cleaned.replaceAll("(?i)\\s*AND\\s*AND", " AND ").trim();
			if (cleaned.equalsIgnoreCase("AND") || cleaned.isEmpty()) cleaned = "";
		}
		this.estatFiltreActual = estat[0];
		return super.findPage(quickFilter, cleaned, namedQueries, perspectives, pageable);
	}

	@Override
	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries, boolean isSingleResult) {

		var isRoleAdmin = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
		var isRoleAdminLectura = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN_LECTURA);
		var isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
		var entitatFilter = "entitat.id:" + userSessionHelper.getCurrentEntitatId();

		String base;
		if ((isRoleAdmin && notibPermissionHelper.currentEntitatPermissionAllowed(ExtendedPermission.PERM2)) ||
			(isRoleAdminLectura && notibPermissionHelper.currentEntitatPermissionAllowed(ExtendedPermission.PERMX))) {
			base = entitatFilter;
		} else if (isRoleAdminOrgan && notibPermissionHelper.currentOrganGestorPermissionAllowed(BasePermission.ADMINISTRATION)) {
			base = entitatFilter + " and organGestor.id:" + userSessionHelper.getCurrentOrganGestorId();
		} else {
			base = "";
		}

		// Afegir condició d'estat (equivalent a repositori línies 20-23)
		String estatCond = null;
		if (estatFiltreActual != null) {
			switch (estatFiltreActual) {
				case "FINALITZAT":
					estatCond = "(elements.dataExecucio is not null and elements.errorDescripcio is null)";
					break;
				case "ERROR":
					estatCond = "(elements.dataExecucio is not null and elements.errorDescripcio is not null)";
					break;
				case "PENDENT":
					estatCond = "(elements.dataExecucio is null and elements.errorDescripcio is null)";
					break;
			}
		}

		String result = base;
		if (estatCond != null) {
			result = result.isEmpty() ? estatCond : result + " and " + estatCond;
		}
		if (currentSpringFilter != null && !currentSpringFilter.trim().isEmpty()) {
			result = result.isEmpty() ? currentSpringFilter : result + " and " + currentSpringFilter;
		}
		return result;
	}

//	@Override
//	protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries, boolean isSingleResult) {
//
//		// Condició per a mostrar només les notificacions de l'entitat actual
//		var isRoleAdmin = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
//		var isRoleAdminLectura = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN_LECTURA);
//		var isRoleAdminOrgan = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ORGAN);
//		var entitatFilter = "entitat.id:" + userSessionHelper.getCurrentEntitatId();
//		if ((isRoleAdmin && notibPermissionHelper.currentEntitatPermissionAllowed(ExtendedPermission.PERM2)) ||
//			(isRoleAdminLectura && notibPermissionHelper.currentEntitatPermissionAllowed(ExtendedPermission.PERMX))) {
//			return entitatFilter;
//		}
//
//		if (isRoleAdminOrgan && notibPermissionHelper.currentOrganGestorPermissionAllowed(BasePermission.ADMINISTRATION)) {
//			return entitatFilter + " and organGestor.id:" + userSessionHelper.getCurrentOrganGestorId();
//		}
//		return "";
//	}


}
