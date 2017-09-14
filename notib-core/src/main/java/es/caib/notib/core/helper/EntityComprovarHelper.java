/**
 * 
 */
package es.caib.notib.core.helper;

import javax.annotation.Resource;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.PermissionDeniedException;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioDestinatariEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.security.ExtendedPermission;


/**
 * Helper per a la comprovació de l'existencia d'entitats de base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EntityComprovarHelper {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private NotificacioRepository notificacioRepository;

	@Resource
	private PermisosHelper permisosHelper;



	public void comprovarPermisos(
			Long entitatId,
			boolean comprovarAdmin,
			boolean comprovarRep) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean esAdministradorEntitat = false;
		boolean esRepresentantEntitat = false;
		if (comprovarAdmin) {
			for (GrantedAuthority ga: auth.getAuthorities()) {
				if (ga.toString().equals("NOT_ADMIN")) {
					esAdministradorEntitat = true;
					break;
				}
			}
		}
		if (comprovarRep) {
			esRepresentantEntitat = permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.REPRESENTANT},
					auth);
		}
		if (comprovarAdmin && !esAdministradorEntitat) {
			if ((!comprovarRep) || (comprovarRep && !esRepresentantEntitat)) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"ADMINISTRATION");
			}
		} else if (comprovarAdmin && esAdministradorEntitat) {
			return;
		}
		if (comprovarRep && !esRepresentantEntitat) {
			throw new PermissionDeniedException(
					entitatId,
					EntitatEntity.class,
					auth.getName(),
					"REPRESENTANT");
		}
	}

	public EntitatEntity comprovarEntitatAplicacio(
			String dir3Codi) {
		EntitatEntity entitat = entitatRepository.findByDir3Codi(dir3Codi);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!permisosHelper.isGrantedAll(
				entitat.getId(),
				EntitatEntity.class,
				new Permission[] {ExtendedPermission.APLICACIO},
				auth)) {
			throw new PermissionDeniedException(
					entitat.getId(),
					EntitatEntity.class,
					auth.getName(),
					"APLICACIO");
			
		}
		return entitat;
	}

	public NotificacioEntity comprovarNotificacioAplicacio(
			String referencia) {
		NotificacioEntity notificacio = notificacioRepository.findByDestinatariReferencia(
				referencia);
		if (notificacio == null) {
			throw new NotFoundException(
					"ref:" + referencia,
					NotificacioDestinatariEntity.class);
		}
		EntitatEntity entitat = notificacio.getEntitat();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!permisosHelper.isGrantedAll(
				entitat.getId(),
				EntitatEntity.class,
				new Permission[] {ExtendedPermission.APLICACIO},
				auth)) {
			throw new PermissionDeniedException(
					entitat.getId(),
					EntitatEntity.class,
					auth.getName(),
					"APLICACIO");
			
		}
		return notificacio;
	}

}
