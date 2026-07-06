package es.caib.notib.logic.metriques;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MetriquesJsonReportGenerator implements BaseReadonlyResourceService.ReportGenerator<NotificacioEnviamentResourceEntity, Serializable, NotificacioEnviamentResource> {

	private final AplicacioService aplicacioService;

	@Override
	public List<NotificacioEnviamentResource> generateData(String code, NotificacioEnviamentResourceEntity entity, Serializable params) throws ReportGenerationException {
		return List.of();
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {


		var nom = "metrics.json";
		var mimeType = "application/json";
		var input = aplicacioService.getMetrics();
		if (StringUtils.isEmpty(input)) {
			log.error("Error generant InputStream per " + nom);
			return DownloadableFile.builder().name(nom).content(new byte[]{}).contentType(mimeType).build();
		}
		try {
			return DownloadableFile.builder().name(nom).content(input.getBytes()).contentType(mimeType).build();
		} catch (Exception ex) {
			log.error("Error convertint a byte[] l'InputStream per " + nom);
			return DownloadableFile.builder().name(nom).content(new byte[]{}).contentType(mimeType).build();
		}
	}


	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}
