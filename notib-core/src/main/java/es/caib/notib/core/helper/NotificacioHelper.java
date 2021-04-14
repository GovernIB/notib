package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.ProgresActualitzacioCertificacioDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioCertificacioDto.TipusActInfo;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.repository.DocumentRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.ProcedimentOrganRepository;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Helper per notificacions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioHelper {

	private ProcedimentOrganRepository procedimentOrganRepository;
	@Autowired
	private GrupRepository grupRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Resource
	private MessageHelper messageHelper;
	@Autowired
	private NotificaHelper notificaHelper;

	@Transactional(timeout = 60, propagation = Propagation.REQUIRES_NEW)
	public void enviamentRefrescarEstat(
			Long enviamentId, 
			ProgresActualitzacioCertificacioDto progres,
			IntegracioInfo info) {
		logger.debug("Refrescant l'estat de la notificació de Notific@ (enviamentId=" + enviamentId + ")");
		try {
			progres.incrementProcedimentsActualitzats();
			String msgInfoUpdating = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant", new Object[] {enviamentId});
			progres.addInfo(TipusActInfo.INFO, msgInfoUpdating);
			info.getParams().add(new AccioParam("Msg. procés:", msgInfoUpdating + " [" + progres.getProgres() + "%]"));
			notificaHelper.enviamentRefrescarEstat(enviamentId);
			String msgInfoUpdated = messageHelper.getMessage("procediment.actualitzacio.auto.processar.enviaments.expirats.actualitzant.ok", new Object[] {enviamentId});
			progres.addInfo(TipusActInfo.SUB_INFO, msgInfoUpdated);
			info.getParams().add(new AccioParam("Msg. procés:", msgInfoUpdated));
		} catch (Exception ex) {
			throw new RuntimeException(ex); 
		}
	}

	public NotificacioData buildNotificacioData(EntitatEntity entitat,
												NotificacioDatabaseDto notificacio,
												boolean checkProcedimentPermissions) {
		GrupEntity grupNotificacio = null;
		OrganGestorEntity organGestor = null;
		ProcedimentEntity procediment = null;
		ProcedimentOrganEntity procedimentOrgan = null;

		//			### Recuperar procediment notificació
		if (notificacio.getProcediment() != null && notificacio.getProcediment().getId() != null) {
			procediment = entityComprovarHelper.comprovarProcediment(entitat, notificacio.getProcediment().getId());
		}

		// Si tenim procediment --> Comprovam permisos i consultam info òrgan gestor
		if (procediment != null) {
			if (!procediment.isComu()) { // || (procediment.isComu() && notificacio.getOrganGestor() == null)) { --> Tot procediment comú ha de informa un òrgan gestor
				organGestor = procediment.getOrganGestor();
			}

			if (procediment.isComu() && organGestor != null) {
				procedimentOrgan = procedimentOrganRepository.findByProcedimentIdAndOrganGestorId(procediment.getId(), organGestor.getId());
			}

			if (checkProcedimentPermissions) {
				procediment = entityComprovarHelper.comprovarProcedimentOrgan(
						entitat,
						notificacio.getProcediment().getId(),
						procedimentOrgan,
						false,
						false,
						true,
						false);
			}
		}

		// Recuperar òrgan gestor notificació
		if (organGestor == null && notificacio.getOrganGestorCodi() != null ) {
			organGestor = organGestorHelper.createOrganGestorFromNotificacio(notificacio, entitat);
		}

		// Recupera grup notificació a partir del codi
		if (notificacio.getGrup() != null && notificacio.getGrup().getId() != null) {
			grupNotificacio = grupRepository.findOne(notificacio.getGrup().getId());
		}
		DocumentEntity documentEntity = getDocumentEntity(notificacio.getDocument());
		DocumentEntity document2Entity = getDocumentEntity(notificacio.getDocument2());
		DocumentEntity document3Entity = getDocumentEntity(notificacio.getDocument3());
		DocumentEntity document4Entity = getDocumentEntity(notificacio.getDocument4());
		DocumentEntity document5Entity = getDocumentEntity(notificacio.getDocument5());

		return NotificacioData.builder()
				.notificacio(notificacio)
				.entitat(entitat)
				.grupNotificacio(grupNotificacio)
				.organGestor(organGestor)
				.procediment(procediment)
				.documentEntity(documentEntity)
				.document2Entity(document2Entity)
				.document3Entity(document3Entity)
				.document4Entity(document4Entity)
				.document5Entity(document5Entity)
				.build();
	}


	private DocumentEntity getDocumentEntity(DocumentDto document) {
		DocumentEntity documentEntity = null;

		if (document != null) {
			String documentGesdocId = null;
			if (document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) {
				documentGesdocId = pluginHelper.gestioDocumentalCreate(
						PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
						Base64.decodeBase64(document.getContingutBase64()));
			} else if (document.getUuid() != null) {
				DocumentDto doc = new DocumentDto();
				String arxiuUuid = document.getUuid();
				if (pluginHelper.isArxiuPluginDisponible()) {
					Document documentArxiu = pluginHelper.arxiuDocumentConsultar(arxiuUuid, null, true,true);
					doc.setArxiuNom(documentArxiu.getNom());
					doc.setNormalitzat(document.isNormalitzat());
					doc.setGenerarCsv(document.isGenerarCsv());
					doc.setUuid(arxiuUuid);
					doc.setMediaType(documentArxiu.getContingut().getTipusMime());
					doc.setMida(documentArxiu.getContingut().getTamany());
					document = doc;
				}
			} else if (document.getCsv() != null) {
				DocumentDto doc = new DocumentDto();
				String arxiuCsv = document.getCsv();
				if (pluginHelper.isArxiuPluginDisponible()) {
					DocumentContingut documentArxiu = pluginHelper.arxiuGetImprimible(arxiuCsv, false);
					doc.setArxiuNom(documentArxiu.getArxiuNom());
					doc.setNormalitzat(document.isNormalitzat());
					doc.setGenerarCsv(document.isGenerarCsv());
					doc.setMediaType(documentArxiu.getTipusMime());
					doc.setMida(documentArxiu.getTamany());
					doc.setCsv(arxiuCsv);
					document = doc;
				}
			}
			// Guardar document
			if (document.getCsv() != null ||
					document.getUuid() != null ||
					document.getContingutBase64() != null ||
					document.getArxiuGestdocId() != null) {

				if (document.getId() != null && !document.getId().isEmpty()) {
					documentEntity = documentRepository.findOne(Long.valueOf(document.getId()));
					documentEntity.update(
							documentGesdocId != null ? documentGesdocId : document.getArxiuGestdocId(),
							document.getArxiuNom(),
							document.getUrl(),
							document.isNormalitzat(),
							document.getUuid(),
							document.getCsv(),
							document.getMediaType(),
							document.getMida(),
							document.getOrigen(),
							document.getValidesa(),
							document.getTipoDocumental(),
							document.getModoFirma());
				} else {
					documentEntity = documentRepository.save(DocumentEntity.getBuilderV2(
							document.getArxiuGestdocId(),
							documentGesdocId != null ? documentGesdocId : document.getArxiuGestdocId(),
							document.getArxiuNom(),
							document.getUrl(),
							document.isNormalitzat(),
							document.getUuid(),
							document.getCsv(),
							document.getMediaType(),
							document.getMida(),
							document.getOrigen(),
							document.getValidesa(),
							document.getTipoDocumental(),
							document.getModoFirma()
					).build());
				}
			}
		}
		return documentEntity;
	}

	@Getter
	@Builder
	public static class NotificacioData {
		private NotificacioDatabaseDto notificacio;
		private EntitatEntity entitat;
		private GrupEntity grupNotificacio;
		private OrganGestorEntity organGestor;
		private ProcedimentEntity procediment;
		private DocumentEntity documentEntity;
		private DocumentEntity document2Entity;
		private DocumentEntity document3Entity;
		private DocumentEntity document4Entity;
		private DocumentEntity document5Entity;
		private ProcedimentOrganEntity procedimentOrgan;

	}

	private static final Logger logger = LoggerFactory.getLogger(NotificacioHelper.class);

}
