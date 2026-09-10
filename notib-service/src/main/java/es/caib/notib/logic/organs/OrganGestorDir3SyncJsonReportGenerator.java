package es.caib.notib.logic.organs;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Genera el JSON amb la darrera consulta DIR3 dels òrgans gestors de l'entitat actual, per
 * a descàrrega.
 */
@Slf4j
@RequiredArgsConstructor
public class OrganGestorDir3SyncJsonReportGenerator implements BaseReadonlyResourceService.ReportGenerator<OrganGestorResourceEntity, Serializable, Serializable> {

	private final OrganGestorService organGestorService;
	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;

	@Override
	public List<Serializable> generateData(String code, OrganGestorResourceEntity entity, Serializable params) throws ReportGenerationException {
		return List.of();
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		if (!authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)) {
			log.warn("[DIR3-JSON] Usuari sense permisos ha intentat descarregar el JSON DIR3");
			return DownloadableFile.builder().name("organsDir3JSON.json").content(new byte[]{}).contentType("application/json").build();
		}
		var entitatId = userSessionHelper.getCurrentEntitatId();
		var arxiu = organGestorService.getJsonOrgansGestorDir3(entitatId);
		return DownloadableFile.builder().name("organsDir3JSON.json").content(arxiu).contentType("application/json").build();
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
	}
}
