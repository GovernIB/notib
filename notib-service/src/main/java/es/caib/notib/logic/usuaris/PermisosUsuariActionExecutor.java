package es.caib.notib.logic.usuaris;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.permis.PermisosUsuari;
import es.caib.notib.logic.intf.model.UsuariPermisResource;
import es.caib.notib.logic.intf.service.UsuariService;
import es.caib.notib.persist.resourceentity.UsuariPermisResourceEntity;
import liquibase.pro.packaged.O;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class PermisosUsuariActionExecutor implements BaseMutableResourceService.ActionExecutor<UsuariPermisResourceEntity, UsuariPermisResource.PermisUsuariForm, PermisosUsuari> {

	private final UsuariService usuariService;
	private final UserSessionHelper userSessionHelper;

	@Override
	public PermisosUsuari exec(String code, UsuariPermisResourceEntity entity, UsuariPermisResource.PermisUsuariForm params) throws ActionExecutionException {

		try {
			var entitatActual = userSessionHelper.getCurrentEntitat();
			var entitatDto = new EntitatDto();
			entitatDto.setId(entitatActual.getId());
			entitatDto.setCodi(entitatActual.getCodi());
			entitatDto.setDir3Codi(entitatActual.getDir3Codi());
			var organAdmin = userSessionHelper.getCurrentOrganGestor();
			OrganGestorDto organDto = null;
			if (organAdmin != null) {
				organDto = new OrganGestorDto();
				organDto.setId(organAdmin.getId());
				organDto.setCodi(organAdmin.getCodi());
			}
			return usuariService.getPermisosUsuari(entitatDto, params.getCodi(), organDto);
		} catch (Exception ex) {
			var msg = "Error inesperat obtenint els permisos de l'usuari " + params.getCodi();
			log.error("[PermisosUsuariActionExecutor] " + msg + " - " + ex.getMessage());
			throw new ActionExecutionException(UsuariPermisResourceEntity.class, null, code, msg + ex.getMessage());
		}
	}

	@Override
	public void onChange(Serializable id, UsuariPermisResource.PermisUsuariForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, UsuariPermisResource.PermisUsuariForm target) {

	}
}
