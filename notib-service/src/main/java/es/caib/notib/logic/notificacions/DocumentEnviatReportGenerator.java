package es.caib.notib.logic.notificacions;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.DocumentResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.JustificantService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DocumentEnviatReportGenerator implements BaseReadonlyResourceService.ReportGenerator<NotificacioResourceEntity, NotificacioResource.DocumentParams, NotificacioResource> {

	private final NotificacioService notificacioService;

	@Override
	public List<NotificacioResource> generateData(String code, NotificacioResourceEntity entity, NotificacioResource.DocumentParams params) throws ReportGenerationException {

		var resource = new NotificacioResource();
		ConfigHelper.setEntitatCodi(entity.getEntitat().getCodi());
		resource.setId(entity.getId());
		ResourceReference<DocumentResource, Long> document = new ResourceReference<>();
		document.setId(params.getDocId());
		resource.setDocument(document);
		return List.of(resource);
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		var notificacio = (NotificacioResource) data.get(0);
		try {
			var document = notificacioService.getDocumentArxiu(notificacio.getId(), notificacio.getDocument().getId());
			if (document.getContingut() == null || document.getContingut().length == 0) {
				log.error("[JusitficantEnviamentReportGenerator] Error generant contingut pel justificant de la notificacio" + notificacio.getId());
				return DownloadableFile.builder().name(document.getNom()).content(new byte[]{}).contentType(document.getContentType()).build();
			}
			return DownloadableFile.builder().name(document.getNom()).content(document.getContingut()).contentType(document.getContentType()).build();
		} catch (Exception ex) {
			log.error("[JusitficantEnviamentReportGenerator] Error inesperat generant contingut pel justificant de la notificacio" + notificacio.getId(), ex);
			return DownloadableFile.builder().name("error_document_adjunt.pdf").content(new byte[]{}).contentType(MediaType.APPLICATION_PDF_VALUE).build();
		}
	}

	@Override
	public void onChange(Serializable id, NotificacioResource.DocumentParams previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, NotificacioResource.DocumentParams target) {

	}
}
