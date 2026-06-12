package es.caib.notib.logic.enviaments;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.model.EntregaPostalResource;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class EntregaPostalCertificacioReportGenerator implements BaseReadonlyResourceService.ReportGenerator<NotificacioEnviamentResourceEntity, Serializable, NotificacioEnviamentResource> {

	private final PluginHelper pluginHelper;
	private final MetricsHelper metricsHelper;

	@Override
	public List<NotificacioEnviamentResource> generateData(String code, NotificacioEnviamentResourceEntity entity, Serializable params) throws ReportGenerationException {

		var entregaPostal = entity.getEntregaPostal();
		if (entregaPostal == null || entregaPostal.getCieCertificacioArxiuId() == null) {
			throw new RuntimeException("No s'ha trobat la certificació de l'entrega postal per l'enviament id: " + entity.getId());
		}
		try {
			ConfigHelper.setEntitatCodi(entity.getNotificacio().getEntitat().getCodi());
		} catch (Exception ex) {
			log.error("[EntregaPostalCertificacioReportGenerator] No s'ha pogut definir l'entitat actual.", ex);
		}
		var resource = new NotificacioEnviamentResource();
		resource.setId(entity.getId());
		var entregaPostalResource = new EntregaPostalResource();
		entregaPostalResource.setCieCertificacioArxiuNom(entregaPostal.getCieCertificacioArxiuNom());
		entregaPostalResource.setCieCertificacioArxiuId(entregaPostal.getCieCertificacioArxiuId());
		resource.setEntregaPostalInfo(entregaPostalResource);
		return List.of(resource);
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var enviament = (NotificacioEnviamentResource) data.get(0);
			var entregaPostal = enviament.getEntregaPostalInfo();
			var output = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(entregaPostal.getCieCertificacioArxiuId(), PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, output, false);
			var nom = entregaPostal.getCieCertificacioArxiuNom();
			var mimeType = "applicattion/pdf";
			if (output.size() == 0) {
				log.error("Error generant InputStream per " + nom);
				return DownloadableFile.builder().name(nom).content(new byte[]{}).contentType(mimeType).build();
			}
			try {
				return DownloadableFile.builder().name(nom).content(output.toByteArray()).contentType(mimeType).build();
			} catch (Exception ex) {
				log.error("Error convertint a byte[] l'InputStream per " + nom);
				return DownloadableFile.builder().name(nom).content(new byte[]{}).contentType(mimeType).build();
			}
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}


	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}
