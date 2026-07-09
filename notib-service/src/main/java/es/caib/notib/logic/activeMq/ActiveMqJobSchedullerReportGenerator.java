package es.caib.notib.logic.activeMq;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.model.ActiveMqResource;
import es.caib.notib.logic.intf.model.MetriquesResource;
import es.caib.notib.logic.intf.service.ActiveMqService;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.persist.resourceentity.ActiveMqResourceEntity;
import es.caib.notib.persist.resourceentity.MetriquesResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ActiveMqJobSchedullerReportGenerator implements BaseReadonlyResourceService.ReportGenerator<ActiveMqResourceEntity, Serializable, ActiveMqResource> {

	private final ActiveMqService activeMqService;

	@Override
	public List<ActiveMqResource> generateData(String code, ActiveMqResourceEntity entity, Serializable params) throws ReportGenerationException {
		return List.of();
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		var nom = "jobscheduler-stats.json";
		var mimeType = "application/json; charset=utf-8";
		try {
			var stats = activeMqService.getJobSchedulerStats();
			if (StringUtils.isEmpty(stats)) {
				log.error("Error generant InputStream per " + nom);
				return DownloadableFile.builder().name(nom).content(new byte[]{}).contentType(mimeType).build();
			}
			var output = new ByteArrayOutputStream();
			output.write(stats.getBytes());
			return DownloadableFile.builder().name(nom).content(output.toByteArray()).contentType(mimeType).build();
		} catch (Exception ex) {
			log.error("Error convertint a byte[] l'InputStream per " + nom);
			return DownloadableFile.builder().name(nom).content(new byte[]{}).contentType(mimeType).build();
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}
