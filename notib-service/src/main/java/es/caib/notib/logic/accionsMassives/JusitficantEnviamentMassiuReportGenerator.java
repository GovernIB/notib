package es.caib.notib.logic.accionsMassives;

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
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.AccioMassivaService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
public class JusitficantEnviamentMassiuReportGenerator implements BaseReadonlyResourceService.ReportGenerator<NotificacioResourceEntity, AccioMassivaParams, AccioMassivaParams> {

	private final AccioMassivaService accioMassivaService;
	private final NotificacioService notificacioService;
	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;

	@Override
	public List<AccioMassivaParams> generateData(String code, NotificacioResourceEntity entity, AccioMassivaParams params) throws ReportGenerationException {


		if (params == null || params.idsEmpty()) {
			throw new ReportGenerationException(NotificacioResource.class, null, "Error", "La selecció no pot ser buida");
		}
		var max = notificacioService.getMaxAccionesMassives();
		if (params.getIds() != null && params.getIds().size() > max) {
			var msg = "S'han seleccionat " + params.getIds().size() + " elements. El màxim permès és: " + max;
			throw new ReportGenerationException(NotificacioResource.class, null, "Error", msg);
		}
		return List.of(params);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		var params = (AccioMassivaParams) data.get(0);
		var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		var date = sdf.format(new Date()).replace(":", "_");
		var nom = "justificantsMassiu_" + date + ".zip";
		var mediaType = "application/zip";
		try {
			var entitatActual = userSessionHelper.getCurrentEntitatId();
			boolean isAdminEntitat = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
			var accio = AccioMassivaExecucio.builder()
				.isAdminEntitat(isAdminEntitat)
				.tipus(AccioMassivaTipus.DESCARREGA_JUSTIFICANT_ENVIAMENT)
				.tipusElementSeleccionat(params.getSeleccioTipus())
				.entitatId(entitatActual)
				.seleccio(params.getIds())
				.format(fileType.name())
				.build();
			var accioId = accioMassivaService.altaAccioMassiva(accio);
			accio.setAccioId(accioId);
			var justificants = accioMassivaService.descarregarJustificant(accio);
			if (justificants == null || justificants.isEmpty()) {
				log.error("[JusitficantEnviamentMassiuReportGenerator] Error generant contingut per la descarrega massiva de justifcants");
				return DownloadableFile.builder().name(nom).content(new byte[]{}).contentType(mediaType).build();
			}
			try (var baos = new ByteArrayOutputStream(); var zos = new ZipOutputStream(baos)) {
				for (var just : justificants) {
					var entry = new ZipEntry(StringUtils.stripAccents(just.getNom()));
					entry.setSize(just.getContingut().length);
					zos.putNextEntry(entry);
					zos.write(just.getContingut());
					zos.closeEntry();
				}
				return DownloadableFile.builder().name(nom).content(baos.toByteArray()).contentType(mediaType).build();
			}
		} catch (Exception ex) {
			log.error("[JusitficantEnviamentMassiuReportGenerator] Error inesperat generant contingut per la descarrega massiva de justifcants", ex);
			return DownloadableFile.builder().name(nom).content(new byte[]{}).contentType(mediaType).build();
		}
	}


	@Override
	public void onChange(Serializable id, AccioMassivaParams previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, AccioMassivaParams target) {

	}
}
