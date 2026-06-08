package es.caib.notib.logic.notificacions;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.service.JustificantService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.persist.resourcerepository.NotificacioEnviamentResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@RequiredArgsConstructor
public class CertificacioReportGenerator implements BaseReadonlyResourceService.ReportGenerator<NotificacioResourceEntity, Serializable, NotificacioResource> {

	private final NotificacioService notificacioService;
	private final MessageHelper messageHelper;

	@Override
	public List<NotificacioResource> generateData(String code, NotificacioResourceEntity entity, Serializable params) throws ReportGenerationException {

		var resource = new NotificacioResource();
		NotificacioEnviamentResource enviamentResource;
		List<NotificacioEnviamentResource> enviaments = new ArrayList<>();
		resource.setId(entity.getId());
		for (var enviament : entity.getEnviaments()) {
			if (enviament.getNotificaCertificacioArxiuId() == null) {
				continue;
			}
			enviamentResource = new NotificacioEnviamentResource();
			enviamentResource.setId(enviament.getId());
			enviamentResource.setCreatedBy("certificacio_" + enviament.getTitular().getNif());
			enviaments.add(enviamentResource);
		}
		resource.setEnviamentsInfo(enviaments);
		return List.of(resource);
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		var notificacio = (NotificacioResource) data.get(0);
		var mediaType = "application/zip";
		var nom = messageHelper.getMessage("notificacio.list.enviament.certificacio.zip.nom");
		nom += "_" + notificacio.getId() + ".zip";
		try (var baos = new ByteArrayOutputStream()) {
			try (var zos = new ZipOutputStream(baos);) {
				ArxiuDto certificacio;
				for (var enviament : notificacio.getEnviamentsInfo()) {
					try {
						certificacio = notificacioService.enviamentGetCertificacioArxiu(enviament.getId());
					} catch (Exception ex) {
						log.error("[CertificacioReportGenerator] Error descarregant la certificacio de l'enviament " + enviament.getId(), ex);
						continue;
					}
					var entry = new ZipEntry(enviament.getCreatedBy());
					entry.setSize(certificacio.getTamany());
					zos.putNextEntry(entry);
					zos.write(certificacio.getContingut());
				}
				zos.closeEntry();
			}
			return DownloadableFile.builder().name(nom).content(baos.toByteArray()).contentType(mediaType).build();
		} catch (Exception ex) {
			log.error("[CertificacioReportGenerator] Error inesperat generant les certificacions de la notificacio" + notificacio.getId(), ex);
			return DownloadableFile.builder().name(nom).content(new byte[]{}).contentType(mediaType).build();
		}
	}


	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}
