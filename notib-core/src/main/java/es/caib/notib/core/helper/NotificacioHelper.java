package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.exception.NoDocumentException;
import es.caib.notib.core.api.exception.NoMetadadesException;
import es.caib.notib.core.api.ws.notificacio.OrigenEnum;
import es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum;
import es.caib.notib.core.api.ws.notificacio.ValidesaEnum;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.repository.DocumentRepository;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
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

import java.util.Map;

/**
 * Helper per notificacions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioHelper {

	@Autowired
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
	@Autowired
	private AuditNotificacioHelper auditNotificacioHelper;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private RegistreNotificaHelper registreNotificaHelper;

	public NotificacioEntity saveNotificacio(NotificacioHelper.NotificacioData data) {
		return 	auditNotificacioHelper.desaNotificacio(NotificacioEntity.
				getBuilderV2(
						data.getEntitat(),
						data.getNotificacio().getEmisorDir3Codi(),
						data.getOrganGestor(),
						pluginHelper.getNotibTipusComunicacioDefecte(),
						data.getNotificacio().getEnviamentTipus(),
						data.getNotificacio().getConcepte(),
						data.getNotificacio().getDescripcio(),
						data.getNotificacio().getEnviamentDataProgramada(),
						data.getNotificacio().getRetard(),
						data.getNotificacio().getCaducitat(),
						data.getNotificacio().getUsuariCodi(),
						data.getProcediment() != null ? data.getProcediment().getCodi() : null,
						data.getProcediment(),
						data.getGrupNotificacio() != null ? data.getGrupNotificacio().getCodi() : null,
						data.getNotificacio().getNumExpedient(),
						TipusUsuariEnumDto.INTERFICIE_WEB,
						data.getProcedimentOrgan(),
						data.getNotificacio().getIdioma())
				.document(data.getDocumentEntity())
				.document2(data.getDocument2Entity())
				.document3(data.getDocument3Entity())
				.document4(data.getDocument4Entity())
				.document5(data.getDocument5Entity())
				.build());
	}
	public NotificacioData buildNotificacioData(EntitatEntity entitat,
												NotificacioDatabaseDto notificacio,
												boolean checkProcedimentPermissions,
												Map<String, Long> documentsProcessatsMassiu) {
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
		DocumentEntity documentEntity = getDocumentEntity(notificacio.getDocument(), documentsProcessatsMassiu);
		DocumentEntity document2Entity = getDocumentEntity(notificacio.getDocument2(), null);
		DocumentEntity document3Entity = getDocumentEntity(notificacio.getDocument3(), null);
		DocumentEntity document4Entity = getDocumentEntity(notificacio.getDocument4(), null);
		DocumentEntity document5Entity = getDocumentEntity(notificacio.getDocument5(), null);

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


	private DocumentEntity getDocumentEntity(DocumentDto document, Map<String, Long> documentsProcessatsMassiu) {
		DocumentEntity documentEntity = null;

		if (document != null) {
			String documentGesdocId = null;
			if (document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) {
				if ( documentsProcessatsMassiu == null || //alta de notificacio web
						!documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
						( documentsProcessatsMassiu.containsKey(document.getArxiuNom()) && // alta massiu web
						documentsProcessatsMassiu.get(document.getArxiuNom()) == null) ) { 
					documentGesdocId = pluginHelper.gestioDocumentalCreate(
							PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
							Base64.decodeBase64(document.getContingutBase64()));
				}
			} else if (document.getUuid() != null) {
				if ( documentsProcessatsMassiu == null || //alta de notificacio web
						!documentsProcessatsMassiu.containsKey(document.getUuid()) ||
						( documentsProcessatsMassiu.containsKey(document.getUuid()) && // alta massiu web
						documentsProcessatsMassiu.get(document.getUuid()) == null) ) {
					DocumentDto doc = new DocumentDto();
					String arxiuUuid = document.getUuid();
					if (pluginHelper.isArxiuPluginDisponible()) {
						Document documentArxiu = pluginHelper.arxiuDocumentConsultar(arxiuUuid, null, true, true);
						doc.setArxiuNom(documentArxiu.getNom());
						doc.setNormalitzat(document.isNormalitzat());
						doc.setGenerarCsv(document.isGenerarCsv());
						doc.setUuid(arxiuUuid);
						doc.setMediaType(documentArxiu.getContingut().getTipusMime());
						doc.setMida(documentArxiu.getContingut().getTamany());
						//Metadades
						if (recuperarMetadadesArxiu(documentArxiu, document)) {
							doc.setOrigen(OrigenEnum.valorAsEnum(documentArxiu.getMetadades().getOrigen().ordinal()));
							doc.setValidesa(ValidesaEnum.valorAsEnum(pluginHelper.estatElaboracioToValidesa(documentArxiu.getMetadades().getEstatElaboracio())));
							doc.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(documentArxiu.getMetadades().getTipusDocumental().toString()));
							doc.setModoFirma(pluginHelper.getModeFirma(documentArxiu, documentArxiu.getContingut().getArxiuNom()) == 1 ? Boolean.TRUE : Boolean.FALSE);
						} else {
							doc.setOrigen(document.getOrigen());
							doc.setValidesa(document.getValidesa());
							doc.setTipoDocumental(document.getTipoDocumental());
							doc.setModoFirma(document.getModoFirma());
						}
						document = doc;
						
						// Recuperar csv
						Map<String, Object> metadadesAddicionals = documentArxiu.getMetadades().getMetadadesAddicionals();
						if (metadadesAddicionals != null) {
							if (metadadesAddicionals.containsKey("csv"))
								document.setCsv((String) metadadesAddicionals.get("csv"));
							else if (metadadesAddicionals.containsKey("eni:csv"))
								document.setCsv((String) metadadesAddicionals.get("eni:csv"));
						}
						
					}
				} else {
					documentEntity = documentRepository.findOne(documentsProcessatsMassiu.get(document.getUuid()));
					return documentEntity;
				}
			} else if (document.getCsv() != null) {
				if ( documentsProcessatsMassiu == null || //alta de notificacio web
						!documentsProcessatsMassiu.containsKey(document.getCsv()) ||
						( documentsProcessatsMassiu.containsKey(document.getCsv()) && // alta massiu web
						documentsProcessatsMassiu.get(document.getCsv()) == null) ) {
					DocumentDto doc = new DocumentDto();
					String arxiuCsv = document.getCsv();
					if (pluginHelper.isArxiuPluginDisponible()) {
						Document documentArxiu = pluginHelper.arxiuDocumentConsultar(arxiuCsv, null, true, false);
						doc.setArxiuNom(documentArxiu.getNom());
						doc.setNormalitzat(document.isNormalitzat());
						doc.setGenerarCsv(document.isGenerarCsv());
						doc.setMediaType(documentArxiu.getContingut().getTipusMime());
						doc.setMida(documentArxiu.getContingut().getTamany());
						doc.setCsv(arxiuCsv);
						//Metadades
						if (recuperarMetadadesArxiu(documentArxiu, document)) {
							doc.setOrigen(OrigenEnum.valorAsEnum(documentArxiu.getMetadades().getOrigen().ordinal()));
							doc.setValidesa(ValidesaEnum.valorAsEnum(pluginHelper.estatElaboracioToValidesa(documentArxiu.getMetadades().getEstatElaboracio())));
							doc.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(documentArxiu.getMetadades().getTipusDocumental().toString()));
							doc.setModoFirma(pluginHelper.getModeFirma(documentArxiu, documentArxiu.getContingut().getArxiuNom()) == 1 ? Boolean.TRUE : Boolean.FALSE);
						} else {
							doc.setOrigen(document.getOrigen());
							doc.setValidesa(document.getValidesa());
							doc.setTipoDocumental(document.getTipoDocumental());
							doc.setModoFirma(document.getModoFirma());
						}
						document = doc;
					}
				} else {
					documentEntity = documentRepository.findOne(documentsProcessatsMassiu.get(document.getCsv()));
					return documentEntity;
				}
			} else if ( documentsProcessatsMassiu != null && 
					documentsProcessatsMassiu.containsKey(document.getArxiuNom()) && 
					 documentsProcessatsMassiu.get(document.getArxiuNom()) != null ) {
					documentEntity = documentRepository.findOne(documentsProcessatsMassiu.get(document.getArxiuNom()));
					return documentEntity;
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
					
					if ( documentsProcessatsMassiu != null ) { // Si NO es alta de notificacio web
						if (document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) {
							documentsProcessatsMassiu.put(document.getArxiuNom(), documentEntity.getId());
						} else if (document.getUuid() != null) {
							documentsProcessatsMassiu.put(document.getUuid(), documentEntity.getId());
						} else if (document.getCsv() != null) {
							documentsProcessatsMassiu.put(document.getCsv(), documentEntity.getId());
						}
					}
				}
			}
		}
		return documentEntity;
	}
	
	private Boolean recuperarMetadadesArxiu (Document documentArxiu, DocumentDto document){
		if (documentArxiu == null) {
			throw new NoDocumentException("No s'ha pogut obtenir el document de l'arxiu.");
		}
		if (documentArxiu.getMetadades() == null || documentArxiu.getMetadades().getOrigen() == null 
				|| documentArxiu.getMetadades().getEstatElaboracio() == null
				|| documentArxiu.getMetadades().getTipusDocumental() == null
				|| documentArxiu.getContingut().getArxiuNom() == null) {
			if (document.getOrigen() == null || document.getValidesa() == null || document.getTipoDocumental() == null || document.getModoFirma()) {
				throw new NoMetadadesException("No s'han obtingut metadades de la consulta a l'arxiu ni de el fitxer CSV de càrrega.");
			} else {
				return false; // metadades de CSV o del formulario de alta web (no masiva)
			}
		} else {
			return true; // metadades de Arxiu
		}
	}

	public NotificacioEventEntity getNotificaErrorEvent(NotificacioEntity notificacio) {
		if (notificacio.getEstat().equals(NotificacioEstatEnumDto.ENVIADA)) {
			return null;
		}
		return notificacioEventRepository.findLastErrorEventByNotificacioId(notificacio.getId());
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
