package es.caib.notib.logic.enviaments;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class EnviamentCertificacioReportGenerator implements BaseReadonlyResourceService.ReportGenerator<NotificacioEnviamentResourceEntity, Serializable, NotificacioEnviamentResource> {

	private final NotificacioService notificacioService;

	@Override
	public List<NotificacioEnviamentResource> generateData(String code, NotificacioEnviamentResourceEntity entity, Serializable params) throws ReportGenerationException {

		var resource = new NotificacioEnviamentResource();
		resource.setId(entity.getId());
		return List.of(resource);
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		var enviament = (NotificacioEnviamentResource) data.get(0);
		var mimeType = "applicattion/pdf";
		try {
			var certificacio = notificacioService.enviamentGetCertificacioArxiu(enviament.getId());
			return DownloadableFile.builder().name(certificacio.getNom()).content(certificacio.getContingut()).contentType(certificacio.getContentType()).build();
		} catch (Exception ex) {
			log.error("[EnviamentCertificacioReportGenerator] Error obtinguent la certificacio per l'enviament" + enviament.getId());
			return DownloadableFile.builder().name("error_certificacio.df").content(new byte[]{}).contentType(mimeType).build();
		}
	}


	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}
