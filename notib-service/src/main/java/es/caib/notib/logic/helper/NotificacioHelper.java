package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.Enviament;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.RegistreIdDto;
import es.caib.notib.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.NoDocumentException;
import es.caib.notib.logic.intf.exception.NoMetadadesException;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.persist.entity.*;
import es.caib.notib.persist.repository.DocumentRepository;
import es.caib.notib.persist.repository.GrupRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
	private OrganGestorHelper organGestorHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private AuditNotificacioHelper auditNotificacioHelper;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;

	@Autowired
	private RegistreNotificaHelper registreNotificaHelper;
	@Autowired
	private PersonaHelper personaHelper;
	@Autowired
	private AuditEnviamentHelper auditEnviamentHelper;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private EmailNotificacioSenseNifHelper emailNotificacioSenseNifHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private EntityManager entityManager;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<RegistreIdDto> registrarNotificar(Long notificacioId) throws RegistreNotificaException {

		log.info("Intentant registrar la notificació pendent (notificacioId=" + notificacioId + ")");
		List<RegistreIdDto> registresIdDto = new ArrayList<>();
		var notificacio = notificacioRepository.findById(notificacioId).orElseThrow();
		log.info(" [REG] Inici registre notificació [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		var startTime = System.nanoTime();
		double elapsedTime;
		synchronized(SemaforNotificacio.agafar(notificacioId)) {
			log.info("Comprovant estat actual notificació (id: " + notificacio.getId() + ")...");
			var estatActual = notificacio.getEstat();
			log.info("Estat notificació [Id:" + notificacio.getId() + ", Estat: "+ estatActual + "]");

			if (estatActual.equals(NotificacioEstatEnumDto.PENDENT)) {
				// Registrar la notificació
				var startTime2 = System.nanoTime();
				var notificar = registreNotificaHelper.realitzarProcesRegistrar(notificacio);
				elapsedTime = (System.nanoTime() - startTime2) / 10e6;
				log.info(" [TIMER-REG] Realitzar procés registrar [Id: " + notificacio.getId() + "]: " + elapsedTime + " ms");
				for (NotificacioEnviamentEntity enviament: notificacio.getEnviaments()) {
					registresIdDto.add(RegistreIdDto.builder().data(enviament.getRegistreData()).numeroRegistreFormat(enviament.getRegistreNumeroFormatat()).build());
				}

				if (notificar){
					// Enviar la notificació
					startTime2 = System.nanoTime();
					var enviamentsSenseNifNoEnviats = notificacio.getEnviamentsPerEmailNoEnviats();
					// 3 possibles casuístiques
					// 1. Tots els enviaments a Notifica
					if (enviamentsSenseNifNoEnviats.isEmpty()) {
						notificaHelper.notificacioEnviar(notificacio.getId());
					}
					// 2. Tots els enviaments per email
					else if (notificacio.getEnviamentsNoEnviats().size() <= enviamentsSenseNifNoEnviats.size()) {
						emailNotificacioSenseNifHelper.notificacioEnviarEmail(enviamentsSenseNifNoEnviats, true);
					}
					// 3. Una part dels enviaments a Notifica i l'altre via email
					else {
						notificacio = notificaHelper.notificacioEnviar(notificacio.getId(), true);
						// Fa falta enviar els restants per email
						emailNotificacioSenseNifHelper.notificacioEnviarEmail(enviamentsSenseNifNoEnviats, false);
					}
					elapsedTime = (System.nanoTime() - startTime2) / 10e6;
					log.info(" [TIMER-REG] Notificació enviar [Id: " + notificacio.getId() + "]: " + elapsedTime + " ms");
				}
			}
		}
		SemaforNotificacio.alliberar(notificacioId);
		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-REG] Temps global registrar notificar amb esperes concurrents [Id: " + notificacio.getId() + "]: " + elapsedTime + " ms");
		log.info(" [REG] Fi registre notificació [Id: " + notificacio.getId() + ", Estat: " + notificacio.getEstat() + "]");
		entityManager.flush();
		entityManager.clear();
		return registresIdDto;
	}

	public NotificacioEntity altaEnviamentsWeb(EntitatEntity entitat, NotificacioEntity notificacioEntity, List<NotEnviamentDatabaseDto> enviamentsDto) throws RegistreNotificaException {

		log.trace("Alta Notificació web - Preparam enviaments");
		List<Enviament> enviaments = new ArrayList<>();
		for(var enviament: enviamentsDto) {
			if (enviament.getEntregaPostal() != null && (enviament.getEntregaPostal().getCodiPostal() == null || enviament.getEntregaPostal().getCodiPostal().isEmpty())) {
				enviament.getEntregaPostal().setCodiPostal(enviament.getEntregaPostal().getCodiPostalNorm());
			}
			enviaments.add(conversioTipusHelper.convertir(enviament, Enviament.class));
		}
		List<NotificacioEnviamentEntity> enviamentsCreats = new ArrayList<NotificacioEnviamentEntity>();
		for (var enviament: enviaments) {
			log.trace("Alta Notificació web - Alta enviament id={}", enviament.getId());
			if (enviament.getTitular() != null) {
				ServeiTipusEnumDto serveiTipus = null;
				if (enviament.getServeiTipus() != null) {
					switch (enviament.getServeiTipus()) {
						case NORMAL:
							serveiTipus = ServeiTipusEnumDto.NORMAL;
							break;
						case URGENT:
							serveiTipus = ServeiTipusEnumDto.URGENT;
							break;
					}
				}
				var titular = personaHelper.create(enviament.getTitular(),enviament.getTitular().isIncapacitat());
				List<PersonaEntity> destinataris = new ArrayList<PersonaEntity>();
				if (enviament.getDestinataris() != null) {
					for(var persona: enviament.getDestinataris()) {
						if ((persona.getNif() != null && !persona.getNif().isEmpty()) ||
								(persona.getDir3Codi() != null && !persona.getDir3Codi().isEmpty())) {
							PersonaEntity destinatari = personaHelper.create(persona, false);
							destinataris.add(destinatari);
						}
					}
				}
				// Rellenar dades enviament titular
				enviamentsCreats.add(auditEnviamentHelper.desaEnviament(entitat, notificacioEntity, enviament, serveiTipus, titular, destinataris));
			}
		}
		notificacioEntity.getEnviaments().addAll(enviamentsCreats);
		// Comprovar on s'ha d'enviar ara
		if (NotificacioComunicacioTipusEnumDto.SINCRON.equals(pluginHelper.getNotibTipusComunicacioDefecte())) {
			synchronized(SemaforNotificacio.agafar(notificacioEntity.getId())) {
				var notificar = registreNotificaHelper.realitzarProcesRegistrar(notificacioEntity);
				if (notificar) {
					notificaHelper.notificacioEnviar(notificacioEntity.getId());
				}
			}
			SemaforNotificacio.alliberar(notificacioEntity.getId());
		}
		return notificacioEntity;
	}

	public NotificacioEntity saveNotificacio(EntitatEntity entitat, NotificacioDatabaseDto notificacio, boolean checkProcedimentPermissions,
											 NotificacioMassivaEntity notificacioMassivaEntity, Map<String, Long> documentsProcessatsMassiu) {

		var notData = buildNotificacioData(entitat, notificacio, checkProcedimentPermissions, notificacioMassivaEntity, documentsProcessatsMassiu);
		// Dades generals de la notificació
		return saveNotificacio(notData);
	}

	public NotificacioEntity saveNotificacio(NotificacioHelper.NotificacioData data) {
//		NotificacioEntity not = NotificacioEntity.builder().entitat(data.getEntitat())
//				.emisorDir3Codi(data.getNotificacio().getEmisorDir3Codi())
//				.organGestor(data.getOrganGestor())
//				.comunicacioTipus(pluginHelper.getNotibTipusComunicacioDefecte())
//				.enviamentTipus(data.getNotificacio().getEnviamentTipus())
//				.concepte(data.getNotificacio().getConcepte())
//				.descripcio(data.getNotificacio().getDescripcio())
//				.enviamentDataProgramada(data.getNotificacio().getEnviamentDataProgramada())
//				.retard(data.getNotificacio().getRetard())
//				.caducitat(data.getNotificacio().getCaducitat())
//				.usuariCodi(data.getNotificacio().getUsuariCodi())
//				.procedimentCodiNotib(data.getProcSer() != null ? data.getProcSer().getCodi() : null)
//				.procediment(data.getProcSer())
//				.grupCodi(data.getGrupNotificacio() != null ? data.getGrupNotificacio().getCodi() : null)
//				.numExpedient(data.getNotificacio().getNumExpedient())
//				.tipusUsuari(TipusUsuariEnumDto.INTERFICIE_WEB)
//				.procedimentOrgan(data.getProcedimentOrgan())
//				.idioma(data.getNotificacio().getIdioma())
//				.referencia(UUID.randomUUID().toString())
//				.document(data.getDocumentEntity())
//				.document2(data.getDocument2Entity())
//				.document3(data.getDocument3Entity())
//				.document4(data.getDocument4Entity())
//				.document5(data.getDocument5Entity())
//				.notificacioMassivaEntity(data.getNotificacioMassivaEntity())
//				.build();
//		return 	auditNotificacioHelper.desaNotificacio(not);
		return 	auditNotificacioHelper.desaNotificacio(NotificacioEntity.
				getBuilderV2(data.getEntitat(), data.getNotificacio().getEmisorDir3Codi(), data.getOrganGestor(), pluginHelper.getNotibTipusComunicacioDefecte(),
						data.getNotificacio().getEnviamentTipus(), data.getNotificacio().getConcepte(), data.getNotificacio().getDescripcio(),
						data.getNotificacio().getEnviamentDataProgramada(), data.getNotificacio().getRetard(), data.getNotificacio().getCaducitat(),
						data.getNotificacio().getUsuariCodi(),
						data.getProcSer() != null ? data.getProcSer().getCodi() : null, data.getProcSer(),
						data.getGrupNotificacio() != null ? data.getGrupNotificacio().getCodi() : null,
						data.getNotificacio().getNumExpedient(), TipusUsuariEnumDto.INTERFICIE_WEB, data.getProcedimentOrgan(), data.getNotificacio().getIdioma(),
						UUID.randomUUID().toString()).document(data.getDocumentEntity()).document2(data.getDocument2Entity()).document3(data.getDocument3Entity())
					.document4(data.getDocument4Entity()).document5(data.getDocument5Entity()).notificacioMassiva(data.getNotificacioMassivaEntity()).build());
	}

	public NotificacioData buildNotificacioData(EntitatEntity entitat, NotificacioDatabaseDto notificacio, boolean checkProcedimentPermissions){
		return buildNotificacioData(entitat, notificacio, checkProcedimentPermissions, null, null);
	}
	public NotificacioData buildNotificacioData(EntitatEntity entitat, NotificacioDatabaseDto notificacio, boolean checkProcedimentPermissions,
												NotificacioMassivaEntity notificacioMassivaEntity, Map<String, Long> documentsProcessatsMassiu) {

		log.debug("Construint les dades d'una notificació");
		GrupEntity grupNotificacio = null;
		OrganGestorEntity organGestor = null;
		ProcSerEntity procSer = null;
		ProcSerOrganEntity procedimentOrgan = null;
		//			### Recuperar procediment notificació
		if (notificacio.getProcediment() != null && notificacio.getProcediment().getId() != null) {
			procSer = entityComprovarHelper.comprovarProcediment(entitat, notificacio.getProcediment().getId());
		}
		// Si tenim procediment --> Comprovam permisos i consultam info òrgan gestor
		log.trace("Processam procediment/servei");
		if (procSer != null) {
			if (!procSer.isComu()) { // || (procediment.isComu() && notificacio.getOrganGestor() == null)) { --> Tot procediment comú ha de informa un òrgan gestor
				organGestor = procSer.getOrganGestor();
			}

			if (procSer.isComu() && organGestor != null) {
				procedimentOrgan = procedimentOrganRepository.findByProcSerIdAndOrganGestorId(procSer.getId(), organGestor.getId());
			}
			if (checkProcedimentPermissions) {
				NotificaEnviamentTipusEnumDto enviamentTipus = notificacio.getEnviamentTipus();
				boolean checkProcedimentNotificacioPermis = (enviamentTipus.equals(NotificaEnviamentTipusEnumDto.NOTIFICACIO));
				boolean checkProcedimentComunicacioPermis = (enviamentTipus.equals(NotificaEnviamentTipusEnumDto.COMUNICACIO) && isAllEnviamentsAAdministracio(notificacio));
				procSer = entityComprovarHelper.comprovarProcedimentOrgan(entitat, notificacio.getProcediment().getId(), procedimentOrgan, false,
						false, checkProcedimentNotificacioPermis, false, checkProcedimentComunicacioPermis);
			}
		}
		// Recuperar òrgan gestor notificació
		log.trace("Processam organ gestor");
		if (organGestor == null && notificacio.getOrganGestorCodi() != null ) {
			organGestor = organGestorRepository.findByCodi(notificacio.getOrganGestorCodi());
		}
		if (organGestor == null) {
			throw new NotFoundException(notificacio.getOrganGestorCodi(), OrganGestorEntity.class);
		}
		// Recupera grup notificació a partir del codi
		log.trace("Processam grup");
		if (notificacio.getGrup() != null && notificacio.getGrup().getId() != null) {
			grupNotificacio = grupRepository.findById(notificacio.getGrup().getId()).orElse(null);
		}
		log.trace("Processam documents");
		var documentEntity = getDocumentEntity(notificacio.getDocument(), documentsProcessatsMassiu);
		var document2Entity = getDocumentEntity(notificacio.getDocument2(), null);
		var document3Entity = getDocumentEntity(notificacio.getDocument3(), null);
		var document4Entity = getDocumentEntity(notificacio.getDocument4(), null);
		var document5Entity = getDocumentEntity(notificacio.getDocument5(), null);
		if (documentEntity == null) {
			throw new NoDocumentException(messageHelper.getMessage("error.alta.remesa.sense.document"));
		}
		return NotificacioData.builder().notificacio(notificacio).entitat(entitat).grupNotificacio(grupNotificacio).organGestor(organGestor).procSer(procSer)
				.documentEntity(documentEntity).document2Entity(document2Entity).document3Entity(document3Entity).document4Entity(document4Entity)
				.document5Entity(document5Entity).notificacioMassivaEntity(notificacioMassivaEntity).build();
	}

	private boolean isAllEnviamentsAAdministracio(NotificacioDatabaseDto notificacio) {

		for(var enviament : notificacio.getEnviaments()) {
			if(!enviament.getTitular().getInteressatTipus().equals(InteressatTipus.ADMINISTRACIO)) {
				return false;
			}
		}
		return true;
	}
	
	private DocumentEntity getDocumentEntity(DocumentDto document, Map<String, Long> documentsProcessatsMassiu) {

		DocumentEntity documentEntity = null;
		if (document == null) {
			return null;
		}
		String documentGesdocId = null;
		if (document.getContingutBase64() != null && !document.getContingutBase64().isEmpty()) {
			log.trace("Processam document gestió documental");
			if ( documentsProcessatsMassiu == null || //alta de notificacio web
					!documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
					( documentsProcessatsMassiu.containsKey(document.getArxiuNom()) && // alta massiu web
					documentsProcessatsMassiu.get(document.getArxiuNom()) == null) ) {
				documentGesdocId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS, Base64.decodeBase64(document.getContingutBase64()));
			}
		} else if (document.getUuid() != null) {
			log.trace("Processam document desde UUID");
			if ( documentsProcessatsMassiu == null || //alta de notificacio web
					!documentsProcessatsMassiu.containsKey(document.getUuid()) ||
					( documentsProcessatsMassiu.containsKey(document.getUuid()) && // alta massiu web
					documentsProcessatsMassiu.get(document.getUuid()) == null) ) {

				var doc = new DocumentDto();
				var arxiuUuid = document.getUuid();
				if (pluginHelper.isArxiuPluginDisponible()) {
					Document documentArxiu = null;
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
					if (recuperarMetadadesArxiu(documentArxiu, document)) {
						var metadades = documentArxiu.getMetadades();
						doc.setOrigen(OrigenEnum.valorAsEnum(metadades.getOrigen().ordinal()));
						doc.setValidesa(ValidesaEnum.valorAsEnum(pluginHelper.estatElaboracioToValidesa(metadades.getEstatElaboracio())));
						if (metadades.getTipusDocumental() != null) {
							doc.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(metadades.getTipusDocumental().toString()));
						} else if (metadades.getTipusDocumentalAddicional() != null) {
							doc.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(metadades.getTipusDocumentalAddicional()));
						}
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
					var metadadesAddicionals = documentArxiu.getMetadades().getMetadadesAddicionals();
					if (metadadesAddicionals != null) {
						if (metadadesAddicionals.containsKey("csv")) {
							document.setCsv((String) metadadesAddicionals.get("csv"));
						} else if (metadadesAddicionals.containsKey("eni:csv")) {
							document.setCsv((String) metadadesAddicionals.get("eni:csv"));
						}
					}

				}
			} else {
				documentEntity = documentRepository.findById(documentsProcessatsMassiu.get(document.getUuid())).orElseThrow();
				return documentEntity;
			}
		} else if (document.getCsv() != null) {
			log.trace("Processam document desde CSV");
			if ( documentsProcessatsMassiu == null || //alta de notificacio web
					!documentsProcessatsMassiu.containsKey(document.getCsv()) ||
					( documentsProcessatsMassiu.containsKey(document.getCsv()) && // alta massiu web
					documentsProcessatsMassiu.get(document.getCsv()) == null) ) {

				var doc = new DocumentDto();
				var arxiuCsv = document.getCsv();
				if (pluginHelper.isArxiuPluginDisponible()) {
					Document documentArxiu = null;
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
					if (recuperarMetadadesArxiu(documentArxiu, document)) {
						DocumentMetadades metadades = documentArxiu.getMetadades();
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
				documentEntity = documentRepository.findById(documentsProcessatsMassiu.get(document.getCsv())).orElseThrow();
				return documentEntity;
			}
		} else if (documentsProcessatsMassiu != null && documentsProcessatsMassiu.containsKey(document.getArxiuNom()) &&
						documentsProcessatsMassiu.get(document.getArxiuNom()) != null ) {
			documentEntity = documentRepository.findById(documentsProcessatsMassiu.get(document.getArxiuNom())).orElseThrow();
			return documentEntity;
		}

		// Guardar document
		if (document.getCsv() != null || document.getUuid() != null || document.getContingutBase64() != null || document.getArxiuGestdocId() != null) {
			log.trace("Enregistram el document llegit a la base de dades");

			if (document.getId() != null && !document.getId().isEmpty()) {
				documentEntity = documentRepository.findById(Long.valueOf(document.getId())).orElseThrow();
				documentEntity.update(documentGesdocId != null ? documentGesdocId : document.getArxiuGestdocId(), document.getArxiuNom(), document.getUrl(),
										document.isNormalitzat(), document.getUuid(), document.getCsv(), document.getMediaType(), document.getMida(), document.getOrigen(),
										document.getValidesa(), document.getTipoDocumental(), document.getModoFirma());
			} else {
				documentEntity = documentRepository.save(DocumentEntity.getBuilderV2(document.getArxiuGestdocId(),
														documentGesdocId != null ? documentGesdocId : document.getArxiuGestdocId(), document.getArxiuNom(),
														document.getUrl(), document.isNormalitzat(), document.getUuid(), document.getCsv(), document.getMediaType(),
														document.getMida(), document.getOrigen(), document.getValidesa(), document.getTipoDocumental(),
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
		}
		return documentEntity;
	}
	
	private Boolean recuperarMetadadesArxiu (Document documentArxiu, DocumentDto document){

		if (documentArxiu == null) {
			throw new NoDocumentException("No s'ha pogut obtenir el document de l'arxiu.");
		}
		var metadades = documentArxiu.getMetadades();
		if (metadades == null || metadades.getOrigen() == null || metadades.getEstatElaboracio() == null
				|| (metadades.getTipusDocumental() == null  && metadades.getTipusDocumentalAddicional() == null) || documentArxiu.getContingut().getArxiuNom() == null) {

			if (document.getOrigen() == null || document.getValidesa() == null || document.getTipoDocumental() == null || document.getModoFirma() == null) {
				throw new NoMetadadesException("No s'han obtingut metadades de la consulta a l'arxiu ni del fitxer CSV de càrrega.");
			}
			return false; // metadades de CSV o del formulario de alta web (no masiva)
		}
		return true; // metadades de Arxiu
	}

	public NotificacioEventEntity getNotificaErrorEvent(NotificacioEntity notificacio) {
		return !notificacio.getEstat().equals(NotificacioEstatEnumDto.ENVIADA) ? notificacioEventRepository.findLastErrorEventByNotificacioId(notificacio.getId()) : null;
	}

	@Getter
	@Builder
	public static class NotificacioData {

		private NotificacioDatabaseDto notificacio;
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
