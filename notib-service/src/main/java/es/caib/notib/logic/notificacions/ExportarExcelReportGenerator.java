package es.caib.notib.logic.notificacions;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.model.AccioMassivaParams;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.AccioMassivaService;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class ExportarExcelReportGenerator implements BaseReadonlyResourceService.ReportGenerator<NotificacioResourceEntity, AccioMassivaParams , AccioMassivaParams > {

	private final AccioMassivaService accioMassivaService;
	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;
	private final EnviamentService enviamentService;
	private final NotificacioService notificacioService;

	@Override
	public List<AccioMassivaParams > generateData(String code, NotificacioResourceEntity entity, AccioMassivaParams params) throws ReportGenerationException {

		if (params == null || params.idsEmpty()) {
			throw new ReportGenerationException(NotificacioResource.class, null, "Error", "La selecció no pot ser buida");
		}
		var max = notificacioService.getMaxAccionesMassives();
		if (params.getIds() != null && params.getIds().size() > max) {
			var msg = "S'han seleccionat " + params.getIds().size() + " elements. El màxim permès és: " + max;
			throw new ReportGenerationException(NotificacioResource.class, null, "Error", msg);
		}
		if (!SeleccioTipus.NOTIFICACIO.equals(params.getSeleccioTipus())) {
			return List.of(params);
		}
		Set<Long> ids = enviamentService.findIdsByNotificacioIds(params.getIds());
		params.setIds(new ArrayList<>(ids));
		return List.of(params);
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		var params = (AccioMassivaParams) data.get(0);
		try {
			var entitatActual = userSessionHelper.getCurrentEntitatId();
			boolean isAdminEntitat = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
			var accio = AccioMassivaExecucio.builder()
							.isAdminEntitat(isAdminEntitat)
							.tipus(AccioMassivaTipus.EXPORTAR_FULL_CALCUL)
//							.tipusElementSeleccionat(params.getSeleccioTipus())
							.tipusElementSeleccionat(SeleccioTipus.ENVIAMENT)
							.entitatId(entitatActual)
							.seleccio(params.getIds())
							.format(fileType.name())
							.build();
			var accioId = accioMassivaService.altaAccioMassiva(accio);
			accio.setAccioId(accioId);
			var fitxer = accioMassivaService.exportar(accio);
			if (fitxer == null) {
				log.error("[ExportarExcelReportGenerator] Error generant la exportació a excel");
				return DownloadableFile.builder().name(fitxer.getNom()).content(new byte[]{}).contentType(fitxer.getContentType()).build();
			}
			return DownloadableFile.builder().name(fitxer.getNom()).content(fitxer.getContingut()).contentType(fitxer.getContentType()).build();
		} catch (Exception ex) {
			log.error("[ExportarExcelReportGenerator] Error inesperat en la exporació a excel", ex);
			return DownloadableFile.builder().name("error_exportacio.csv").content(new byte[]{}).contentType(MediaType.APPLICATION_PDF_VALUE).build();
		}
	}

	@Override
	public void onChange(Serializable id, AccioMassivaParams previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, AccioMassivaParams target) {

	}
}
