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

import es.caib.notib.core.api.exception.PermissionDeniedException;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.EntitatUsuariRepository;
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
	private EntitatUsuariRepository entitatUsuariRepository;

	@Resource
	private PermisosHelper permisosHelper;

	
	public void comprovarPermisos(
			Long entitatId,
			boolean comprovarAdmin,
			boolean comprovarRep ) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if(comprovarAdmin) {
			
			boolean esAdministradorEntitat = false;
			for(GrantedAuthority ga: auth.getAuthorities()) {
				if( ga.toString().equals("NOT_ADMIN") ) {
					esAdministradorEntitat = true;
					break;
				}
			}
			
			if (!esAdministradorEntitat) {
				throw new PermissionDeniedException(
						new Long(-1),
						EntitatEntity.class,
						auth.getName(),
						"ADMINISTRATION");
				
			} else return;
		
		}
		
		if(comprovarRep) {
			
			boolean esRepresentantEntitat = permisosHelper.isGrantedAll(
					entitatId,
					EntitatEntity.class,
					new Permission[] {ExtendedPermission.REPRESENTANT},
					auth);
			if (!esRepresentantEntitat) {
				throw new PermissionDeniedException(
						entitatId,
						EntitatEntity.class,
						auth.getName(),
						"REPRESENTANT");
				
			} else return;
		
		}	
		
	}
	
	public void comprovarPermisosAplicacio(
			Long entitatId ) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		boolean esUsuariAplicacio = false;
		for(GrantedAuthority ga: auth.getAuthorities()) {
			if( ga.toString().equals("NOT_APL") ) {
				esUsuariAplicacio = true;
				break;
			}
		}
		
		if (!esUsuariAplicacio) {
			throw new PermissionDeniedException(
					new Long(-1),
					EntitatEntity.class,
					auth.getName(),
					"APLICATION");
			
		}
		
	}

//	public EntitatEntity comprovarEntitatAdmin(
//			Long entitatId) throws NotFoundException {
//		
//		EntitatEntity entitat = entitatRepository.findOne(entitatId);
//		
//		if (entitat == null) {
//			throw new NotFoundException(
//					entitatId,
//					EntitatEntity.class);
//		}
//		
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		
//		boolean esAdministradorEntitat = false;
//		for(GrantedAuthority ga: auth.getAuthorities()) {
//			if( ga.toString().equals("NOT_ADMIN") ) {
//				esAdministradorEntitat = true;
//				break;
//			}
//		}
//		
//		if (!esAdministradorEntitat) {
//			throw new PermissionDeniedException(
//					entitatId,
//					EntitatEntity.class,
//					auth.getName(),
//					"ADMINISTRATION");
//		}
//		
//		return entitat;
//		
//	}
//	
//	public EntitatEntity comprovarEntitatRep(
//			Long entitatId) throws NotFoundException {
//		
//		EntitatEntity entitat = entitatRepository.findOne(entitatId);
//		
//		if (entitat == null) {
//			throw new NotFoundException(
//					entitatId,
//					EntitatEntity.class);
//		}
//		
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		
//		boolean esRepresentantEntitat = permisosHelper.isGrantedAll(
//				entitatId,
//				EntitatEntity.class,
//				new Permission[] {ExtendedPermission.REPRESENTANT},
//				auth);
//		if (!esRepresentantEntitat) {
//			throw new PermissionDeniedException(
//					entitatId,
//					EntitatEntity.class,
//					auth.getName(),
//					"REPRESENTANT");
//		}
//		
//		return entitat;
//	}
//	
//	
//	
//	public EntitatUsuariEntity comprovarEntitatUsuariAdmin(
//			Long entitatUsuariId ) throws NotFoundException {
//		
//		EntitatUsuariEntity entitatUsuari = entitatUsuariRepository.findOne(entitatUsuariId);
//		
//		if (entitatUsuari == null) {
//			throw new NotFoundException(
//					entitatUsuariId,
//					EntitatUsuariEntity.class);
//		}
//		
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		
//		boolean esAdministrador = false;
//		for(GrantedAuthority ga: auth.getAuthorities()) {
//			if( ga.toString().equals("NOT_ADMIN") ) {
//				esAdministrador = true;
//				break;
//			}
//		}
//		
//		if (!esAdministrador) {
//			throw new PermissionDeniedException(
//					entitatUsuari.getEntitat().getId(),
//					EntitatEntity.class,
//					auth.getName(),
//					"ADMINISTRATION");
//		}
//		
//		return entitatUsuari;
//		
//	}
//	
//	public EntitatUsuariEntity comprovarEntitatUsuariRep(
//			Long entitatUsuariId ) throws NotFoundException {
//		
//		EntitatUsuariEntity entitatUsuari = entitatUsuariRepository.findOne(entitatUsuariId);
//		
//		if (entitatUsuari == null) {
//			throw new NotFoundException(
//					entitatUsuariId,
//					EntitatUsuariEntity.class);
//		}
//		
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		
//		boolean esRepresentant = permisosHelper.isGrantedAll(
//				entitatUsuari.getEntitat().getId(),
//				EntitatEntity.class,
//				new Permission[] {ExtendedPermission.REPRESENTANT},
//				auth);
//		if (!esRepresentant) {
//			throw new PermissionDeniedException(
//					entitatUsuari.getEntitat().getId(),
//					EntitatEntity.class,
//					auth.getName(),
//					"REPRESENTANT");
//		}
//		
//		return entitatUsuari;
//	}


}
