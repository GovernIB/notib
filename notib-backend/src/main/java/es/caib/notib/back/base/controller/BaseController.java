package es.caib.notib.back.base.controller;

import es.caib.notib.logic.intf.base.service.PermissionEvaluatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;

/**
 * Classe base pels controladors de l'API REST.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class BaseController {

	boolean isVisibleInApiIndex() {
		return true;
	}

	public boolean isPublic() {
		return false;
	}

	public boolean forbiddenCreateLogic() {
		return false;
	}
	public boolean forbiddenUpdateLogic() {
		return false;
	}
	public boolean forbiddenPatchLogic() {
		return false;
	}
	public boolean forbiddenDeleteLogic() {
		return false;
	}
	public boolean forbiddenExportLogic() {
		return false;
	}
	public boolean forbiddenOnChangeLogic() {
		return false;
	}
	public boolean forbiddenArtifactLogic() {
		return false;
	}
	public boolean forbiddenFieldsLogic() {
		return false;
	}

	public PermissionEvaluatorService.RestApiOperation getOperation(String operationName) {
		return operationName != null ? PermissionEvaluatorService.RestApiOperation.valueOf(operationName) : null;
	}

	protected abstract Link getIndexLink();

}
