package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.Document;
import es.caib.notib.logic.intf.dto.notificacio.Enviament;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.NoDocumentException;
import es.caib.notib.logic.intf.exception.NoMetadadesException;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.AuditService.TipusOperacio;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.GrupEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.NotificacioMassivaEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.repository.DocumentRepository;
import es.caib.notib.persist.repository.GrupRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Helper per notificacions
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class NotificacioHelper {

	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private ProcSerOrganRepository procedimentOrganRepository;
	@Autowired
	private GrupRepository grupRepository;
	@Autowired
	private DocumentRepository documentRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Autowired
	private PersonaHelper personaHelper;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;
	@Autowired
	private EnviamentTableHelper enviamentTableHelper;
	@Autowired
	private AuditHelper auditHelper;
	@Autowired
	private MessageHelper messageHelper;


	public NotificacioEntity altaEnviamentsWeb(EntitatEntity entitat, NotificacioEntity notificacioEntity, List<Enviament> enviaments) throws RegistreNotificaException {

		log.trace("Alta Notificació web - Preparam enviaments");
//		List<Enviament> enviaments = new ArrayList<>();
//		for(var enviament: enviamentsDto) {
//			if (enviament.getEntregaPostal() != null && (enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty())) {
//				enviament.getEntregaPostal().setCodiPostal(enviament.getEntregaPostal().getCodiPostalNorm());
//			}
//			enviaments.add(conversioTipusHelper.convertir(enviament, Enviament.class));
//		}
		List<NotificacioEnviamentEntity> enviamentsCreats = new ArrayList<>();
		PersonaEntity titular;
		List<PersonaEntity> destinataris;
		NotificacioEnviamentEntity nouEnviament;
		for (var enviament: enviaments) {
			log.trace("Alta Notificació web - Alta enviament titular={}", enviament.getTitular() != null ? enviament.getTitular().getNif() : "");
			if (enviament.getTitular() == null) {
				continue;
			}
			ServeiTipus serveiTipus = null;
			if (enviament.getServeiTipus() != null) {
				switch (enviament.getServeiTipus()) {
					case NORMAL:
						serveiTipus = ServeiTipus.NORMAL;
						break;
					case URGENT:
						serveiTipus = ServeiTipus.URGENT;
						break;
				}
			}

			titular = personaHelper.create(enviament.getTitular(),enviament.getTitular().isIncapacitat());
			destinataris = new ArrayList<>();
			if (enviament.getDestinataris() != null) {
				for(var persona: enviament.getDestinataris()) {
					if ((persona.getNif() != null && !persona.getNif().isEmpty()) || (persona.getDir3Codi() != null && !persona.getDir3Codi().isEmpty())) {
						var destinatari = personaHelper.create(persona, false);
						destinataris.add(destinatari);
					}
				}
			}
			// Rellenar dades enviament titular
			var env = NotificacioEnviamentEntity.getBuilderV2(enviament, entitat.isAmbEntregaDeh(), serveiTipus, notificacioEntity, titular, destinataris, UUID.randomUUID().toString()).build();
			nouEnviament = notificacioEnviamentRepository.saveAndFlush(env);
			enviamentsCreats.add(nouEnviament);
			enviamentTableHelper.crearRegistre(nouEnviament);
			auditHelper.auditaEnviament(nouEnviament, TipusOperacio.CREATE, "NotificacioHelper.altaEnviamentsWeb");
		}
		notificacioEntity.getEnviaments().addAll(enviamentsCreats);

		return notificacioEntity;
	}
	public NotificacioEntity saveNotificacio(EntitatEntity entitat, Notificacio notificacio, boolean checkProcedimentPermissions, NotificacioMassivaEntity notificacioMassivaEntity, Map<String, Long> documentsProcessatsMassiu) {

		var notData = buildNotificacioData(entitat, notificacio, checkProcedimentPermissions, notificacioMassivaEntity, documentsProcessatsMassiu);
		// Dades generals de la notificació
		return saveNotificacio(notData);
	}

	public NotificacioEntity saveNotificacio(NotificacioHelper.NotificacioData data) {

		var notificacio = NotificacioEntity.getBuilderV2(
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
						data.getProcSer() != null ? data.getProcSer().getCodi() : null,
						data.getProcSer(),
						data.getGrupNotificacio() != null ? data.getGrupNotificacio().getCodi() : null,
						data.getNotificacio().getNumExpedient(),
						TipusUsuariEnumDto.INTERFICIE_WEB,
						data.getProcedimentOrgan(),
						data.getNotificacio().getIdioma(),
						UUID.randomUUID().toString())
				.document(data.getDocumentEntity())
				.document2(data.getDocument2Entity())
				.document3(data.getDocument3Entity())
				.document4(data.getDocument4Entity())
				.document5(data.getDocument5Entity())
				.notificacioMassiva(data.getNotificacioMassivaEntity())
				.build();
		notificacio = notificacioRepository.saveAndFlush(notificacio);
		notificacioTableHelper.crearRegistre(notificacio);
		return notificacio;
	}

	public NotificacioData buildNotificacioData(EntitatEntity entitat, Notificacio notificacio, boolean checkProcedimentPermissions) {
		return buildNotificacioData(entitat, notificacio, checkProcedimentPermissions, null, null);
	}

	public NotificacioData buildNotificacioData(EntitatEntity entitat, Notificacio notificacio, boolean checkProcedimentPermissions, NotificacioMassivaEntity notificacioMassivaEntity, Map<String, Long> documentsProcessatsMassiu) {

		var notificacioData = buildDadesComunes(entitat, notificacio, checkProcedimentPermissions, notificacioMassivaEntity, documentsProcessatsMassiu);
		log.trace("Processam documents");
		var documentEntity = getDocumentEntity(notificacio.getDocument(), documentsProcessatsMassiu);
		var document2Entity = getDocumentEntity(notificacio.getDocument2());
		var document3Entity = getDocumentEntity(notificacio.getDocument3());
		var document4Entity = getDocumentEntity(notificacio.getDocument4());
		var document5Entity = getDocumentEntity(notificacio.getDocument5());
		if (documentEntity == null) {
			throw new NoDocumentException(messageHelper.getMessage("error.alta.remesa.sense.document"));
		}
		notificacioData.setDocumentEntity(documentEntity);
		notificacioData.setDocument2Entity(document2Entity);
		notificacioData.setDocument3Entity(document3Entity);
		notificacioData.setDocument4Entity(document4Entity);
		notificacioData.setDocument5Entity(document5Entity);
		return notificacioData;
	}

	public List<NotificacioData> buildNotificacioSirDividides(EntitatEntity entitat, Notificacio notificacio, boolean checkProcedimentPermissions) {

		List<NotificacioData> notificacions = new ArrayList<>();
		var notificacioData = buildDadesComunes(entitat, notificacio, checkProcedimentPermissions, null, null);
		var numDocuments = notificacio.getNumDocuments();
		var concepte = notificacio.getConcepte();
		for (int i = 0; i < numDocuments; i++) {
			notificacioData.getNotificacio().setConcepte(concepte + "PARCIAL " + i + " DE " + numDocuments);
//			notificacioData.getNotificacio().setDescripcio(concepte + "PARCIAL " + i + " DE " + numDocuments);
			switch (i) {
				case 0:
					notificacioData.setDocumentEntity(getDocumentEntity(notificacio.getDocument()));
					break;
				case 1:
					notificacioData.setDocumentEntity(getDocumentEntity(notificacio.getDocument2()));
					break;
				case 2:
					notificacioData.setDocumentEntity(getDocumentEntity(notificacio.getDocument3()));
					break;
				case 3:
					notificacioData.setDocumentEntity(getDocumentEntity(notificacio.getDocument4()));
					break;
				case 4:
					notificacioData.setDocumentEntity(getDocumentEntity(notificacio.getDocument5()));
					break;
			}
			notificacions.add(notificacioData);
		}
		return notificacions;
	}

	private NotificacioData buildDadesComunes(EntitatEntity entitat, Notificacio notificacio, boolean checkProcedimentPermissions, NotificacioMassivaEntity notificacioMassivaEntity, Map<String, Long> documentsProcessatsMassiu) {

		log.debug("Construint les dades d'una notificació");
		GrupEntity grupNotificacio = null;
		OrganGestorEntity organGestor = null;
		ProcSerEntity procSer = null;
		ProcSerOrganEntity procedimentOrgan = null;
		//			### Recuperar procediment notificació
		if (notificacio.getProcedimentId() != null) {
			procSer = entityComprovarHelper.comprovarProcediment(entitat, notificacio.getProcedimentId());
		}

		// Si tenim procediment --> Comprovam permisos i consultam info òrgan gestor
		log.trace("Processam procediment/servei");
		if (procSer != null) {
			if (!procSer.isComu()) { //Tot procediment comú ha de informa un òrgan gestor
				organGestor = procSer.getOrganGestor();
			}

			if (procSer.isComu() && organGestor != null) {
				procedimentOrgan = procedimentOrganRepository.findByProcSerIdAndOrganGestorId(procSer.getId(), organGestor.getId());
			}

			if (checkProcedimentPermissions) {
				var enviamentTipus = notificacio.getEnviamentTipus();
				var checkProcedimentNotificacioPermis = (enviamentTipus.equals(EnviamentTipus.NOTIFICACIO));
				var checkProcedimentComunicacioPermis = (enviamentTipus.equals(EnviamentTipus.COMUNICACIO) && isAllEnviamentsAAdministracio(notificacio));
				procSer = entityComprovarHelper.comprovarProcedimentOrgan(entitat, notificacio.getProcedimentId(), procedimentOrgan, false,
						false, checkProcedimentNotificacioPermis, false, checkProcedimentComunicacioPermis);
			}
		}

		// Recuperar òrgan gestor notificació
		log.trace("Processam organ gestor");
		if (organGestor == null && notificacio.getOrganGestor() != null ) {
			organGestor = organGestorRepository.findByEntitatAndCodi(entitat, notificacio.getOrganGestor());
		}
		if (organGestor == null) {
			throw new NotFoundException(notificacio.getOrganGestor(), OrganGestorEntity.class);
		}

		// Recupera grup notificació a partir del codi
		log.trace("Processam grup");
		if (!Strings.isNullOrEmpty(notificacio.getGrupCodi())) {
			grupNotificacio = grupRepository.findByCodiAndEntitat(notificacio.getGrupCodi(), entitat);
		}

		return NotificacioData.builder().notificacio(notificacio)
										.entitat(entitat)
										.grupNotificacio(grupNotificacio)
										.organGestor(organGestor)
										.procSer(procSer)
										.notificacioMassivaEntity(notificacioMassivaEntity).build();
	}


	private boolean isAllEnviamentsAAdministracio(Notificacio notificacio) {

		for(var enviament : notificacio.getEnviaments()) {
			if(!enviament.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
				return false;
			}
		}
		return true;
	}

	private DocumentEntity getDocumentEntity(Document document) {
		return getDocumentEntity(document, null);
	}

	private DocumentEntity getDocumentEntity(Document document, Map<String, Long> documentsProcessatsMassiu) {

		DocumentEntity documentEntity = null;
		if (document == null) {
			return null;
		}
		String documentGesdocId = null;
		log.info("Processant el document " + document.getArxiuNom());
		if (document.getContingutBase64() != null && !document.getContingutBase64().isEmpty() && isDocumentNotProcessat(document.getArxiuNom(), documentsProcessatsMassiu)) {

			log.info("Processam document gestió documental");
			documentGesdocId = // !Strings.isNullOrEmpty(document.getArxiuGestdocId()) ? document.getArxiuGestdocId() :
					pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS, Base64.decodeBase64(document.getContingutBase64()));
			log.info("Document creat al gestor documental amb id " + documentGesdocId);
		} else if (document.getUuid() != null) {
			log.info("Processam document desde UUID");
			if (isDocumentNotProcessat(document.getUuid(), documentsProcessatsMassiu)) {

				var doc = new Document();
				var arxiuUuid = document.getUuid();
				if (pluginHelper.isArxiuPluginDisponible()) {
					es.caib.plugins.arxiu.api.Document documentArxiu ;
					try {
						documentArxiu = pluginHelper.arxiuDocumentConsultar(arxiuUuid, null, true, true);
					} catch (Exception ex) {
						throw new ArxiuException(messageHelper.getMessage("error.document.inexistent") + " - " + arxiuUuid);
					}
					doc.setArxiuNom(documentArxiu.getNom());
					doc.setNormalitzat(document.isNormalitzat());
					doc.setGenerarCsv(document.isGenerarCsv());
					doc.setUuid(arxiuUuid);
					doc.setMediaType(documentArxiu.getContingut().getTipusMime());
					doc.setMida(documentArxiu.getContingut().getTamany());
					//Metadades
					if (Boolean.TRUE.equals(recuperarMetadadesArxiu(documentArxiu, document))) {
						DocumentMetadades metadades = documentArxiu.getMetadades();
						doc.setOrigen(OrigenEnum.valorAsEnum(metadades.getOrigen().ordinal()));
						doc.setValidesa(ValidesaEnum.valorAsEnum(pluginHelper.estatElaboracioToValidesa(metadades.getEstatElaboracio())));
						if (metadades.getTipusDocumental() != null)
							doc.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(metadades.getTipusDocumental().toString()));
						else if (metadades.getTipusDocumentalAddicional() != null)
							doc.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(metadades.getTipusDocumentalAddicional()));
						doc.setModoFirma(pluginHelper.getModeFirma(documentArxiu, documentArxiu.getContingut().getArxiuNom()) == 1 ? Boolean.TRUE : Boolean.FALSE);
						if (metadades.getIdentificador() != null && !metadades.getIdentificador().isEmpty() &&
								metadades.getExtensio() != null && !metadades.getExtensio().name().isEmpty() ) {
							doc.setArxiuNom(metadades.getIdentificador() + "." + metadades.getExtensio().name());
						}
					} else {
						doc.setOrigen(document.getOrigen());
						doc.setValidesa(document.getValidesa());
						doc.setTipoDocumental(document.getTipoDocumental());
						doc.setModoFirma(document.getModoFirma());
					}
					document = doc;

					// Recuperar csv
					if (documentArxiu.getMetadades() != null ) {
						Map<String, Object> metadadesAddicionals = documentArxiu.getMetadades().getMetadadesAddicionals();
						if (metadadesAddicionals != null) {
							if (metadadesAddicionals.containsKey("csv"))
								document.setCsv((String) metadadesAddicionals.get("csv"));
							else if (metadadesAddicionals.containsKey("eni:csv"))
								document.setCsv((String) metadadesAddicionals.get("eni:csv"));
						}
					} else {
						log.info("Metadades null per el document amb uuid" + document.getUuid());
					}

				}
			} else {
				log.info("Document UUID amb processat massiu");
				documentEntity = documentRepository.findById(documentsProcessatsMassiu.get(document.getUuid())).get();
				return documentEntity;
			}
		} else if (document.getCsv() != null) {
			log.info("Processam document desde CSV");
			if (isDocumentNotProcessat(document.getCsv(), documentsProcessatsMassiu)) {

				var doc = new Document();
				var arxiuCsv = document.getCsv();
				if (pluginHelper.isArxiuPluginDisponible()) {
					es.caib.plugins.arxiu.api.Document documentArxiu;
					try {
						documentArxiu = pluginHelper.arxiuDocumentConsultar(arxiuCsv, null, true, false);
					} catch (Exception ex) {
						throw new ArxiuException(messageHelper.getMessage("error.document.inexistent") + " - " + arxiuCsv);
					}
					doc.setArxiuNom(documentArxiu.getNom());
					doc.setNormalitzat(document.isNormalitzat());
					doc.setGenerarCsv(document.isGenerarCsv());
					doc.setMediaType(documentArxiu.getContingut().getTipusMime());
					doc.setMida(documentArxiu.getContingut().getTamany());
					doc.setCsv(arxiuCsv);
					//Metadades
					if (Boolean.TRUE.equals(recuperarMetadadesArxiu(documentArxiu, document))) {
						var metadades = documentArxiu.getMetadades();
						doc.setOrigen(OrigenEnum.valorAsEnum(metadades.getOrigen().ordinal()));
						doc.setValidesa(ValidesaEnum.valorAsEnum(pluginHelper.estatElaboracioToValidesa(metadades.getEstatElaboracio())));
						doc.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(metadades.getTipusDocumental().toString()));
						if (metadades.getIdentificador() != null && !metadades.getIdentificador().isEmpty() &&
								metadades.getExtensio() != null && !metadades.getExtensio().name().isEmpty() ) {

							doc.setArxiuNom(metadades.getIdentificador() + "." + metadades.getExtensio().name());
						}
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
				log.info("Document CSV amb processat massiu");
				return documentRepository.findById(documentsProcessatsMassiu.get(document.getCsv())).get();
			}
		} else if (documentsProcessatsMassiu != null && documentsProcessatsMassiu.containsKey(document.getArxiuNom()) && documentsProcessatsMassiu.get(document.getArxiuNom()) != null ) {
			log.info("Obtenint document amb processat massiu ja existent ");
			return documentRepository.findById(documentsProcessatsMassiu.get(document.getArxiuNom())).orElseThrow();
		}

		// Guardar document
		if (document.getCsv() == null && document.getUuid() == null && document.getContingutBase64() == null && document.getArxiuId() != null) {
			log.info("Document sense codi csv, uuId, contingutBase64 i am arxiuId " + document.getArxiuId());
			return documentEntity;
		}

		if (Strings.isNullOrEmpty(document.getCsv()) && Strings.isNullOrEmpty(document.getUuid()) && Strings.isNullOrEmpty(document.getContingutBase64()) && Strings.isNullOrEmpty(document.getArxiuId())) {
			var error = "Document sense codi csv, uuId, contingutBase64, arxiuId ";
			log.error(error);
			return null;
		}

		log.info("Enregistram el document llegit a la base de dades " + document.getArxiuNom());
		if (!Strings.isNullOrEmpty(document.getId())) {
			log.info("Actualitzant el document " + document.getId() + " documentGesdocId " + documentGesdocId + " document.getArxiuId " + document.getArxiuId());
			documentEntity = documentRepository.findById(Long.valueOf(document.getId())).orElseThrow();
			documentEntity.update(documentGesdocId != null ? documentGesdocId : document.getArxiuId(), document.getArxiuNom(),
					document.isNormalitzat(), document.getUuid(), document.getCsv(), document.getMediaType(), document.getMida(), document.getOrigen(),
					document.getValidesa(), document.getTipoDocumental(), document.getModoFirma());
		} else {
			log.info("Creant el document - documentGesdocId " + documentGesdocId + " document.getArxiuId " + document.getArxiuId());
			documentEntity = documentRepository.save(DocumentEntity.getBuilderV2(
					documentGesdocId != null ? documentGesdocId : document.getArxiuId(),
					document.getArxiuNom(),
					document.isNormalitzat(),
					document.getUuid(),
					document.getCsv(),
					document.getMediaType(),
					document.getMida(),
					document.getOrigen(),
					document.getValidesa(),
					document.getTipoDocumental(),
					document.getModoFirma()).build());

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
		return documentEntity;
	}

	private static boolean isDocumentNotProcessat(String documentKey, Map<String, Long> documentsProcessatsMassiu) {
		return documentsProcessatsMassiu == null || //alta de notificacio web
				!documentsProcessatsMassiu.containsKey(documentKey) ||
				(documentsProcessatsMassiu.containsKey(documentKey) && // alta massiu web
						documentsProcessatsMassiu.get(documentKey) == null);
	}

	private Boolean recuperarMetadadesArxiu (es.caib.plugins.arxiu.api.Document documentArxiu, Document document){

		if (documentArxiu == null) {
			throw new NoDocumentException("No s'ha pogut obtenir el document de l'arxiu.");
		}
		var metadades = documentArxiu.getMetadades();
		if (metadades == null || metadades.getOrigen() == null || metadades.getEstatElaboracio() == null
				|| (metadades.getTipusDocumental() == null  && metadades.getTipusDocumentalAddicional() == null)
				|| documentArxiu.getContingut().getArxiuNom() == null) {

			if (document.getOrigen() == null || document.getValidesa() == null || document.getTipoDocumental() == null || document.getModoFirma() == null) {
				throw new NoMetadadesException("No s'han obtingut metadades de la consulta a l'arxiu ni del fitxer CSV de càrrega.");
			}
			return false; // metadades de CSV o del formulario de alta web (no masiva)
		}
		return true; // metadades de Arxiu
	}

	public NotificacioEventEntity getNotificaErrorEvent(NotificacioEntity notificacio) {

		if (notificacio.getEstat().equals(NotificacioEstatEnumDto.ENVIADA)) {
			return null;
		}
		List<NotificacioEventEntity> eventsError = new ArrayList<>();
		NotificacioEventEntity event;
		for (var env : notificacio.getEnviaments()) {
			event = env.getUltimEvent();
			if (event != null && event.isError()) {
				eventsError.add(event);
			}
		}
		return !eventsError.isEmpty() ? eventsError.get(0) : null;
	}

	@Getter
	@Setter
	@Builder
	public static class NotificacioData {

		private Notificacio notificacio;
		private EntitatEntity entitat;
		private GrupEntity grupNotificacio;
		private OrganGestorEntity organGestor;
		private ProcSerEntity procSer;
		private DocumentEntity documentEntity;
		private DocumentEntity document2Entity;
		private DocumentEntity document3Entity;
		private DocumentEntity document4Entity;
		private DocumentEntity document5Entity;
		private ProcSerOrganEntity procedimentOrgan;
		@Builder.Default
		private NotificacioMassivaEntity notificacioMassivaEntity = null;
	}
}
