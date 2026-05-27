package es.caib.notib.logic.notificacioMassiva;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.model.NotificacioMassivaResource;
import es.caib.notib.logic.intf.service.NotificacioMassivaService;
import es.caib.notib.persist.resourceentity.NotificacioMassivaResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class NotificacioMassivaZipNotificacioReportGenerator implements BaseReadonlyResourceService.ReportGenerator<NotificacioMassivaResourceEntity, Serializable, NotificacioMassivaResource> {


	private final NotificacioMassivaService notificacioMassivaService;

	@Override
	public List<NotificacioMassivaResource> generateData(String code, NotificacioMassivaResourceEntity entity, Serializable params) throws ReportGenerationException {

		var resource = new NotificacioMassivaResource();
		resource.setId(entity.getId());
		resource.setZipFilename(entity.getZipFilename());
		var entitat = new ResourceReference<EntitatResource, Long>();
		entitat.setId(entity.getEntitat().getId());
		resource.setEntitat(entitat);
		ConfigHelper.setEntitatCodi(entity.getEntitat().getCodi());
		return List.of(resource);
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		var mediaType = "application/zip";
		var massiva = (NotificacioMassivaResource) data.get(0);
		try {
			var file = notificacioMassivaService.getZipFile(massiva.getEntitat().getId(), massiva.getId());
			if (file.getContingut() == null || file.getContingut().length == 0) {
				log.error("[NotificacioMassivaZipNotificacioReportGenerator] Error generant ZIP de la notificacio massiva " + massiva.getId());
				return DownloadableFile.builder().name(massiva.getZipFilename()).content(new byte[]{}).contentType(mediaType).build();
			}
			return DownloadableFile.builder().name(massiva.getZipFilename()).content(file.getContingut()).contentType(mediaType).build();
		} catch (Exception ex) {
			log.error("[NotificacioMassivaZipNotificacioReportGenerator] Error inesperat generant contingut pel ZIP de la notificacio massiva" + massiva.getId(), ex);
			return DownloadableFile.builder().name(massiva.getZipFilename()).content(new byte[]{}).contentType(mediaType).build();
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}
