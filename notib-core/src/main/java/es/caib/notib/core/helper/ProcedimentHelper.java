package es.caib.notib.core.helper;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.security.ExtendedPermission;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ProcedimentHelper {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PermisosHelper permisosHelper;
	
	public void omplirPermisosPerMetaNode(
			ProcedimentDto procediment,
			boolean ambLlistaPermisos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		procediment.setUsuariActualRead(
				permisosHelper.isGrantedAll(
						procediment.getId(),
						ProcedimentEntity.class,
						new Permission[] {ExtendedPermission.READ},
						auth));
		procediment.setUsuariActualProcessar(
				permisosHelper.isGrantedAll(
						procediment.getId(),
						ProcedimentEntity.class,
						new Permission[] {ExtendedPermission.PROCESSAR},
						auth));
		procediment.setUsuariActualNotificacio(
				permisosHelper.isGrantedAll(
						procediment.getId(),
						ProcedimentEntity.class,
						new Permission[] {ExtendedPermission.NOTIFICACIO},
						auth));
		procediment.setUsuariActualAdministration(
				permisosHelper.isGrantedAll(
						procediment.getId(),
						ProcedimentEntity.class,
						new Permission[] {ExtendedPermission.ADMINISTRATION},
						auth));
		if (ambLlistaPermisos) {
			List<PermisDto> permisos = permisosHelper.findPermisos(
					procediment.getId(),
					ProcedimentEntity.class);
			procediment.setPermisos(permisos);
		}
	}
	
}
