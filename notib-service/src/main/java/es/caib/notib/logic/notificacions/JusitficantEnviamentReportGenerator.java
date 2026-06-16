package es.caib.notib.logic.notificacions;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.JustificantService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JusitficantEnviamentReportGenerator implements BaseReadonlyResourceService.ReportGenerator<NotificacioResourceEntity, Serializable, NotificacioResource> {

	private final JustificantService justificantService;

	@Override
	public List<NotificacioResource> generateData(String code, NotificacioResourceEntity entity, Serializable params) throws ReportGenerationException {

		var resource = new NotificacioResource();
		resource.setId(entity.getId());
		return List.of(resource);
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		var notificacio = (NotificacioResource) data.get(0);
		try {
			var justificant = justificantService.generarJustificantEnviament(notificacio.getId(), "sequence" + UUID.randomUUID());
			if (justificant.getContingut() == null || justificant.getContingut().length == 0) {
				log.error("[JusitficantEnviamentReportGenerator] Error generant contingut pel justificant de la notificacio" + notificacio.getId());
				return DownloadableFile.builder().name(justificant.getNom()).content(new byte[]{}).contentType(justificant.getContentType()).build();
			}
			return DownloadableFile.builder().name(justificant.getNom()).content(justificant.getContingut()).contentType(justificant.getContentType()).build();
		} catch (Exception ex) {
			log.error("[JusitficantEnviamentReportGenerator] Error inesperat generant contingut pel justificant de la notificacio" + notificacio.getId(), ex);
			return DownloadableFile.builder().name("errorJustificant.pdf").content(new byte[]{}).contentType(MediaType.APPLICATION_PDF_VALUE).build();
		}
	}


	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}
