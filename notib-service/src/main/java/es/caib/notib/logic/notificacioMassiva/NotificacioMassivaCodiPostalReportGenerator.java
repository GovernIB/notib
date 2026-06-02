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

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class NotificacioMassivaCodiPostalReportGenerator implements BaseReadonlyResourceService.ReportGenerator<NotificacioMassivaResourceEntity, Serializable, NotificacioMassivaResource> {

	private final NotificacioMassivaService notificacioMassivaService;

	@Override
	public List<NotificacioMassivaResource> generateData(String code, NotificacioMassivaResourceEntity entity, Serializable params) throws ReportGenerationException {
		return List.of();
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		var nomFitxer = "codisEntregaPostal.pdf";
		var mediaType = "application/pdf";
		try {
			var contingut = notificacioMassivaService.getCodisEntregaPostal();
			if (contingut == null || contingut.length == 0) {
				log.error("[NotificacioMassivaZipNotificacioReportGenerator] Error obtinguent el PDF amb els codis postals");
				return DownloadableFile.builder().name(nomFitxer).content(new byte[]{}).contentType(mediaType).build();
			}
			return DownloadableFile.builder().name(nomFitxer).content(contingut).contentType(mediaType).build();
		} catch (Exception ex) {
			log.error("[NotificacioMassivaCodiPostalReportGenerator] Error inesperat generant contingut del PDF amb els codis postals", ex);
			return DownloadableFile.builder().name(nomFitxer).content(new byte[]{}).contentType(mediaType).build();
		}
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}
