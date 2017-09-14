/**
 * 
 */
package es.caib.notib.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.core.util.Base64;

import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaRespostaDatatDto;
import es.caib.notib.core.api.dto.NotificaRespostaEstatDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.ws.notificacio2.ErrorOrigenEnum;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioDestinatariEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.helper.SeuHelper;
import es.caib.notib.core.repository.NotificacioDestinatariRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;

/**
 * Implementació del servei de gestió d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class NotificacioServiceImpl implements NotificacioService {

	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioDestinatariRepository notificacioDestinatariRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;

	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private PropertiesHelper propertiesHelper;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private SeuHelper seuHelper;

	@Autowired
	private PluginHelper pluginHelper;



	@Transactional
	@Override
	public NotificacioDto alta(
			String entitatDir3Codi,
			NotificacioDto notificacio) {
		logger.debug("Alta de notificació (" +
				"entitatDir3Codi=" + entitatDir3Codi + ", " +
				"tipus=" + notificacio.getEnviamentTipus() + ", " +
				"concepte=" + notificacio.getConcepte() + ")");
		String dir3Codi = (entitatDir3Codi != null) ? entitatDir3Codi : notificacio.getCifEntitat();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitatAplicacio(dir3Codi);
		String documentGesdocId = pluginHelper.gestioDocumentalCreate(
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				new ByteArrayInputStream(
						Base64.decode(notificacio.getDocumentContingutBase64())));
		NotificacioEntity notificacioEntity = NotificacioEntity.getBuilder(
				notificacio.getEnviamentTipus(), 
				notificacio.getEnviamentDataProgramada(),
				notificacio.getConcepte(),
				notificacio.getDocumentArxiuNom(),
				documentGesdocId,
				notificacio.getDocumentSha1(),
				notificacio.getSeuAvisText(),
				notificacio.getSeuAvisTitol(),
				notificacio.getSeuOficiTitol(),
				notificacio.getSeuOficiText(),
				notificacio.getSeuIdioma(),
				notificacio.getSeuRegistreLlibre(),
				notificacio.getSeuRegistreOficina(),
				notificacio.getSeuExpedientTitol(),
				notificacio.getSeuExpedientIdentificadorEni(),
				notificacio.getSeuExpedientUnitatOrganitzativa(),
				notificacio.getSeuExpedientSerieDocumental(),
				notificacio.isDocumentNormalitzat(),
				notificacio.isDocumentGenerarCsv(),
				null,
				entitat).
				pagadorCorreusCodiDir3(notificacio.getPagadorCorreusCodiDir3()).
				pagadorCorreusContracteNum(notificacio.getPagadorCorreusContracteNum()).
				pagadorCorreusCodiClientFacturacio(notificacio.getPagadorCorreusCodiClientFacturacio()).
				pagadorCieDataVigencia(notificacio.getPagadorCorreusDataVigencia()).
				pagadorCieCodiDir3(notificacio.getPagadorCieCodiDir3()).
				pagadorCorreusDataVigencia(notificacio.getPagadorCorreusDataVigencia()).
				procedimentCodiSia(notificacio.getProcedimentCodiSia()).
				procedimentDescripcioSia(notificacio.getProcedimentDescripcioSia()).
				seuAvisTextMobil(notificacio.getSeuAvisTextMobil()).
				build();
		notificacioRepository.saveAndFlush(notificacioEntity);
		List<String> resposta = new ArrayList<String>();
		List<NotificacioDestinatariEntity> destinataris = new ArrayList<NotificacioDestinatariEntity>();
		for (NotificacioDestinatariDto destinatari: notificacio.getDestinataris()) {
			NotificacioDestinatariEntity destinatariEntity = NotificacioDestinatariEntity.getBuilder(
					destinatari.getTitularNom(),
					destinatari.getTitularNif(),
					destinatari.getDestinatariNom(),
					destinatari.getDestinatariNif(),
					destinatari.getServeiTipus(),
					destinatari.isDehObligat(),
					notificacioEntity).
					titularLlinatge1(destinatari.getTitularLlinatge1()).
					titularLlinatge2(destinatari.getTitularLlinatge2()).
					titularTelefon(destinatari.getTitularTelefon()).
					titularEmail(destinatari.getTitularEmail()).
					destinatariLlinatge1(destinatari.getDestinatariLlinatge1()).
					destinatariLlinatge2(destinatari.getDestinatariLlinatge2()).
					destinatariTelefon(destinatari.getDestinatariTelefon()).
					destinatariEmail(destinatari.getDestinatariEmail()).
					domiciliTipus(destinatari.getDomiciliTipus()).
					domiciliConcretTipus(destinatari.getDomiciliConcretTipus()).
					domiciliViaTipus(destinatari.getDomiciliViaTipus()).
					domiciliViaNom(destinatari.getDomiciliViaNom()).
					domiciliNumeracioTipus(destinatari.getDomiciliNumeracioTipus()).
					domiciliNumeracioNumero(destinatari.getDomiciliNumeracioNumero()).
					domiciliNumeracioPuntKm(destinatari.getDomiciliNumeracioPuntKm()).
					domiciliApartatCorreus(destinatari.getDomiciliApartatCorreus()).
					domiciliBloc(destinatari.getDomiciliBloc()).
					domiciliPortal(destinatari.getDomiciliPortal()).
					domiciliEscala(destinatari.getDomiciliEscala()).
					domiciliPlanta(destinatari.getDomiciliPlanta()).
					domiciliPorta(destinatari.getDomiciliPorta()).
					domiciliComplement(destinatari.getDomiciliComplement()).
					domiciliPoblacio(destinatari.getDomiciliPoblacio()).
					domiciliMunicipiCodiIne(destinatari.getDomiciliMunicipiCodiIne()).
					domiciliMunicipiNom(destinatari.getDomiciliMunicipiNom()).
					domiciliCodiPostal(destinatari.getDomiciliCodiPostal()).
					domiciliProvinciaCodi(destinatari.getDomiciliProvinciaCodi()).
					domiciliProvinciaNom(destinatari.getDomiciliProvinciaNom()).
					domiciliPaisCodiIso(destinatari.getDomiciliPaisCodiIso()).
					domiciliPaisNom(destinatari.getDomiciliPaisNom()).
					domiciliLinea1(destinatari.getDomiciliLinea1()).
					domiciliLinea2(destinatari.getDomiciliLinea2()).
					domiciliCie(destinatari.getDomiciliCie()).
				dehObligat(destinatari.isDehObligat()).
				dehNif(destinatari.getDehNif()).
				dehProcedimentCodi(destinatari.getDehProcedimentCodi()).
				retardPostal(destinatari.getRetardPostal()).
				caducitat(destinatari.getCaducitat()).
				build();
			NotificacioDestinatariEntity destinatariSaved = notificacioDestinatariRepository.saveAndFlush(
					destinatariEntity);
			String referencia;
			try {
				referencia = notificaHelper.generarReferencia(destinatariSaved);
			} catch (GeneralSecurityException ex) {
				throw new RuntimeException(
						"No s'ha pogut crear la referencia per al destinatari",
						ex);
			}
			destinatariSaved.updateReferencia(referencia);
			resposta.add(referencia);
			destinataris.add(destinatariSaved);
		}
		notificacioEntity.updateDestinataris(destinataris);
		notificacioRepository.saveAndFlush(notificacioEntity);
		// TODO decidir si es fa l'enviament immediatament o si s'espera
		// a que l'envii la tasca programada.
		// notificaHelper.intentarEnviament(notificacioEntity);
		NotificacioDto dto = conversioTipusHelper.convertir(
				notificacioEntity,
				NotificacioDto.class);
		dto.setDestinataris(
				destinatarisToDto(notificacioEntity.getDestinataris()));
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public NotificacioDto consulta(
			String referencia) {
		logger.debug("Consulta l'estat d'un enviament (referencia=" + referencia + ")");
		NotificacioEntity notificacio = entityComprovarHelper.comprovarNotificacioAplicacio(
				referencia);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				notificacio.getDocumentArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				baos);
		NotificacioDto dto = conversioTipusHelper.convertir(
				notificacio,
				NotificacioDto.class);
		NotificacioDestinatariEntity destinatari = notificacioDestinatariRepository.findByNotificacioAndReferencia(
				notificacio,
				referencia);
		if (destinatari != null) {
			dto.setDestinataris(
					destinatarisToDto(Arrays.asList(destinatari)));
		}
		if (notificacio.isError()) {
			dto.setError(true);
			NotificacioEventEntity errorEvent = notificacio.getErrorEvent();
			switch (errorEvent.getTipus()) {
			case CALLBACK_CLIENT:
				dto.setErrorOrigen(ErrorOrigenEnum.NOTIB);
				break;
			case NOTIFICA_CALLBACK_CERTIFICACIO:
				dto.setErrorOrigen(ErrorOrigenEnum.NOTIB);
				break;
			case NOTIFICA_CALLBACK_DATAT:
				dto.setErrorOrigen(ErrorOrigenEnum.NOTIB);
				break;
			case NOTIFICA_CONSULTA_CERT:
				dto.setErrorOrigen(ErrorOrigenEnum.NOTIFICA);
				break;
			case NOTIFICA_CONSULTA_DATAT:
				dto.setErrorOrigen(ErrorOrigenEnum.NOTIFICA);
				break;
			case NOTIFICA_CONSULTA_ESTAT:
				dto.setErrorOrigen(ErrorOrigenEnum.NOTIFICA);
				break;
			case NOTIFICA_CONSULTA_INFO:
				dto.setErrorOrigen(ErrorOrigenEnum.NOTIFICA);
				break;
			case NOTIFICA_ENVIAMENT:
				dto.setErrorOrigen(ErrorOrigenEnum.NOTIFICA);
				break;
			case SEU_CAIB_CONSULTA_ESTAT:
				dto.setErrorOrigen(ErrorOrigenEnum.SEU_CAIB);
				break;
			case SEU_CAIB_ENVIAMENT:
				dto.setErrorOrigen(ErrorOrigenEnum.SEU_CAIB);
				break;
			case SEU_NOTIFICA_CERTIFICACIO:
				dto.setErrorOrigen(ErrorOrigenEnum.SEU_CAIB);
				break;
			case SEU_NOTIFICA_COMUNICACIO:
				dto.setErrorOrigen(ErrorOrigenEnum.SEU_CAIB);
				break;
			}
			dto.setErrorEventData(errorEvent.getData());
			dto.setErrorEventError(errorEvent.getErrorDescripcio());
		}
		dto.setDocumentContingutBase64(
				new String(Base64.encode(baos.toByteArray())));
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public NotificacioDto findById(Long id) {
		logger.debug("Consulta de la notificacio amb id (id=" + id + ")");
		NotificacioEntity dto = notificacioRepository.findOne(id);
		entityComprovarHelper.comprovarPermisos(
				dto.getEntitat().getId(),
				true,
				true);
		return  conversioTipusHelper.convertir(
				dto,
				NotificacioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<NotificacioDto> findByFiltrePaginat(
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				false);
		Page<NotificacioEntity> notificacions;
		if (filtre == null) {
			notificacions = notificacioRepository.findByEntitatActiva(
					true,
					paginacioHelper.toSpringDataPageable(paginacioParams));
		} else {
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams);
			notificacions = notificacioRepository.findFilteredByEntitatId(
							filtre.getConcepte(),
							filtre.getDataInici(),
							filtre.getDataFi(),
							filtre.getDestinatari().equals(""), 
							filtre.getDestinatari(),
							filtre.getEntitatId() == null,
							filtre.getEntitatId(),
							pageable);
		}
		return paginacioHelper.toPaginaDto(
				notificacions,
				NotificacioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<NotificacioDto> findByEntitatIFiltrePaginat(
			Long entitatId,
			NotificacioFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta paginada de notificacions d'una entitat segons el filtre (" +
				"entitatId=" + entitatId + ", " +
				"filtre=" + filtre + ", " +
				"paginacioParams=" + paginacioParams + ")");
		entityComprovarHelper.comprovarPermisos(
				entitatId,
				false,
				true);
		PaginaDto<NotificacioDto> notificacions;
		if (filtre == null) {
			notificacions = paginacioHelper.toPaginaDto(
					notificacioRepository.findByEntitatId(
							entitatId,
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					NotificacioDto.class);
		} else {
			notificacions = paginacioHelper.toPaginaDto(
					notificacioRepository.findFilteredByEntitat(
							entitatId,
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					NotificacioDto.class);
		}
		return notificacions;
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioDestinatariDto> destinatariFindByNotificacio(
			Long notificacioId) {
		logger.debug("Consulta els destinataris d'una notificació (" +
				"notificacioId=" + notificacioId + ")");
		entityComprovarHelper.comprovarPermisos(
				notificacioId,
				true,
				true);
		return destinatarisToDto(
				notificacioDestinatariRepository.findByNotificacioId(notificacioId));
	}

	@Override
	@Transactional(readOnly = true)
	public NotificacioDestinatariDto destinatariFindById(Long destinatariId) {
		logger.debug("Consulta de destinatari donat el seu id (" +
				"destinatariId=" + destinatariId + ")");
		NotificacioDestinatariEntity destinatari =
				notificacioDestinatariRepository.findOne(destinatariId);
		NotificacioEntity notificacio = notificacioRepository.findOne( destinatari.getNotificacio().getId() );
		entityComprovarHelper.comprovarPermisos(
				notificacio.getEntitat().getId(),
				true,
				true);
		return destinatariToDto(destinatari);
	}

	@Override
	@Transactional(readOnly = true)
	public NotificacioDestinatariDto destinatariFindByReferencia(String referencia) {
		logger.debug("Consulta de destinatari donat el seu id (" +
				"referencia=" + referencia + ")");
		NotificacioDestinatariEntity destinatari =
				notificacioDestinatariRepository.findByReferencia(referencia);
		return destinatariToDto(destinatari);
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindByNotificacio(
			Long notificacioId) {
		logger.debug("Anam a cercar els events de la notificació amb ID=" + notificacioId);
		entityComprovarHelper.comprovarPermisos(
				notificacioId,
				true,
				true);
		return conversioTipusHelper.convertirList(
				notificacioEventRepository.findByNotificacioId(notificacioId),
				NotificacioEventDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindByNotificacioIDestinatari(
			Long notificacioId,
			Long destinatariId) {
		logger.debug("Consulta dels events associats a un destinatari (" +
				"notificacioId=" + notificacioId + ", " + 
				"destinatariId=" + destinatariId + ")");
		NotificacioDestinatariEntity destinatari = notificacioDestinatariRepository.findOne(destinatariId);
		entityComprovarHelper.comprovarPermisos(
				destinatari.getNotificacio().getId(),
				true,
				true);
		return conversioTipusHelper.convertirList(
				notificacioEventRepository.findByNotificacioDestinatariId(destinatariId),
				NotificacioEventDto.class);
	}

	@Override
	public void enviar(
			Long notificacioId) {
		logger.debug("Intentant enviament a Notifica i a la seu de la notificació pendent (" +
				"notificacioId=" + notificacioId + ")");
		notificaHelper.enviament(notificacioId);
		List<NotificacioDestinatariEntity> pendents = notificacioDestinatariRepository.findBySeuEstatInOrderBySeuDataNotificaDarreraPeticioAsc(
				new NotificacioDestinatariEstatEnumDto[] {
						NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT});
		for (NotificacioDestinatariEntity pendent: pendents) {
			seuHelper.enviament(pendent.getId());
		}
	}

	@Override
	@Transactional
	public NotificacioDto consultarInformacio(
			String referencia) {
		logger.debug("Consultant a Notifica la informació de la notificació (" +
				"referencia=" + referencia + ")");
		NotificacioDestinatariEntity destinatari = notificacioDestinatariRepository.findByReferencia(referencia);
		return notificaHelper.enviamentInfo(destinatari);
	}

	@Override
	@Transactional
	public NotificaRespostaEstatDto consultarEstat(
			String referencia) {
		logger.debug("Consultant a Notifica l'estat de la notificació (" +
				"referencia=" + referencia + ")");
		NotificacioDestinatariEntity destinatari = notificacioDestinatariRepository.findByReferencia(referencia);
		return notificaHelper.enviamentEstat(destinatari);
	}

	@Override
	@Transactional
	public NotificaRespostaDatatDto consultarDatat(
			String referencia) {
		logger.debug("Consultant a Notifica el datat de la notificació (" +
				"referencia=" + referencia + ")");
		NotificacioDestinatariEntity destinatari = notificacioDestinatariRepository.findByReferencia(referencia);
		return notificaHelper.enviamentDatat(destinatari);
	}

	@Override
	@Scheduled(fixedRateString = "${config:es.caib.notib.tasca.notifica.enviaments.periode}")
	public void notificaEnviamentsPendents() {
		logger.debug("Cercant notificacions pendents d'enviar a Notifica");
		if (notificaHelper.isConnexioNotificaDisponible()) {
			int maxPendents = getNotificaEnviamentsProcessarMaxProperty();
			List<NotificacioEntity> pendents = notificacioRepository.findByEstatOrderByCreatedDateAsc(
					NotificacioEstatEnumDto.PENDENT,
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant enviaments a Notifica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEntity pendent: pendents) {
					notificaHelper.enviament(pendent.getId());
				}
			} else {
				logger.debug("No hi ha notificacions pendents d'enviar a la seu electrònica");
			}
		} else {
			logger.warn("La connexió amb Notifica no està configurada i no es realitzarà cap enviament");
		}
	}

	@Override
	@Scheduled(fixedRateString = "${config:es.caib.notib.tasca.seu.enviaments.periode}")
	public void seuEnviamentsPendents() {
		logger.debug("Cercant notificacions pendents d'enviar a la seu electrònica");
		if (pluginHelper.isSeuPluginConfigurat()) {
			int maxPendents = getSeuEnviamentsProcessarMaxProperty();
			List<NotificacioDestinatariEntity> pendents = notificacioDestinatariRepository.findBySeuEstatInOrderBySeuDataNotificaDarreraPeticioAsc(
					new NotificacioDestinatariEstatEnumDto[] {
							NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT},
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant enviaments a la seu electrònica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioDestinatariEntity pendent: pendents) {
					seuHelper.enviament(pendent.getId());
				}
			} else {
				logger.debug("No hi ha notificacions pendents d'enviar a la seu electrònica");
			}
		} else {
			logger.warn("La connexió amb la seu electrònica no està activa i no es realitzarà cap enviament");
		}
	}
	@Override
	@Scheduled(fixedRateString = "${config:es.caib.notib.tasca.seu.justificants.periode}")
	public void seuNotificacionsPendents() {
		logger.debug("Cercant notificacions pendents de consulta d'estat a la seu electrònica");
		if (pluginHelper.isSeuPluginConfigurat()) {
			int maxPendents = getSeuJustificantsProcessarMaxProperty();
			List<NotificacioDestinatariEntity> pendents = notificacioDestinatariRepository.findBySeuEstatInOrderBySeuDataNotificaDarreraPeticioAsc(
					new NotificacioDestinatariEstatEnumDto[] {
							NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA},
					new PageRequest(0, maxPendents));
			// TODO excloure les notificacions ja processades amb Notifica
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant consulta d'estat a la seu electrònica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioDestinatariEntity pendent: pendents) {
					boolean estatActualitzat = seuHelper.consultaEstat(pendent.getId());
					if (estatActualitzat) {
						notificaHelper.comunicacioCanviEstatSeu(pendent.getId());
					}
				}
			} else {
				logger.debug("No hi ha notificacions pendents de consultar estat a la seu electrònica");
			}
		} else {
			logger.warn("La connexió amb la seu electrònica no està activa i no es realitzarà cap enviament");
		}
	}
	@Override
	@Scheduled(fixedRateString = "${config:es.caib.notib.tasca.seu.notifica.estat.periode}")
	public void seuNotificaComunicarEstatPendents() {
		logger.debug("Cercant notificacions provinents de la seu pendents d'actualització d'estat a Notifica");
		if (pluginHelper.isSeuPluginConfigurat()) {
			int maxPendents = getSeuNotificaEstatProcessarMaxProperty();
			List<NotificacioDestinatariEntity> pendents = notificacioDestinatariRepository.findBySeuEstatInOrderBySeuDataNotificaDarreraPeticioAsc(
					new NotificacioDestinatariEstatEnumDto[] {
							NotificacioDestinatariEstatEnumDto.LLEGIDA,
							NotificacioDestinatariEstatEnumDto.REBUTJADA},
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant actualització d'estat a Notifica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioDestinatariEntity pendent: pendents) {
					notificaHelper.comunicacioCanviEstatSeu(pendent.getId());
				}
			} else {
				logger.debug("No hi ha notificacions pendents d'actualització d'estat a Notifica");
			}
		} else {
			logger.warn("La connexió amb la seu electrònica no està activa i no es realitzarà cap enviament");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public FitxerDto findFitxer(Long notificacioId) {
		NotificacioEntity entity = notificacioRepository.findOne(notificacioId);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				entity.getDocumentArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				output);
		return new FitxerDto(
				entity.getDocumentArxiuNom(),
				"PDF",
				output.toByteArray(),
				output.size());
	}

	@Override
	@Transactional(readOnly = true)
	public FitxerDto findCertificacio(String referencia) {
		NotificacioDestinatariEntity entity =
				notificacioDestinatariRepository.findByReferencia(referencia);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				entity.getNotificaCertificacioArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
				output);
		return new FitxerDto(
				entity.getNotificaCertificacioArxiuId(),
				entity.getNotificaCertificacioArxiuTipus().toString(),
				output.toByteArray(),
				output.size());
	}

	@Override
	public void updateDestinatariEstat(
			String referencia,
			NotificacioDestinatariEstatEnumDto notificaEstat,
			Date notificaEstatData,
			String notificaEstatReceptorNom,
			String notificaEstatReceptorNif,
			String notificaEstatOrigen,
			String notificaEstatNumSeguiment,
			NotificacioDestinatariEstatEnumDto seuEstat) {
		NotificacioDestinatariEntity entity =
				notificacioDestinatariRepository.findByReferencia(referencia);
		entityComprovarHelper.comprovarPermisos(
				entity.getNotificacio().getId(),
				true,
				false);
		entity.updateNotificaEstat(
				notificaEstat,
				notificaEstatData,
				notificaEstatReceptorNom,
				notificaEstatReceptorNif,
				notificaEstatOrigen,
				notificaEstatNumSeguiment);
		entity.updateSeuNotificaInformat();
	}

	@Override
	public void updateCertificacio(
			String referencia, 
			NotificaCertificacioTipusEnumDto notificaCertificacioTipus,
			NotificaCertificacioArxiuTipusEnumDto notificaCertificacioArxiuTipus,
			String notificaCertificacioArxiuId,
			String notificaCertificacioNumSeguiment, 
			Date notificaCertificacioDataActualitzacio) {
		NotificacioDestinatariEntity entity =
				notificacioDestinatariRepository.findByReferencia(referencia);
		entityComprovarHelper.comprovarPermisos(
				entity.getNotificacio().getId(),
				true,
				false);
		entity.updateNotificaCertificacio(
				notificaCertificacioTipus,
				notificaCertificacioArxiuTipus,
				notificaCertificacioArxiuId,
				notificaCertificacioNumSeguiment,
				notificaCertificacioDataActualitzacio);
		
	}



	private List<NotificacioDestinatariDto> destinatarisToDto(
			List<NotificacioDestinatariEntity> destinatarisEntity) {
		List<NotificacioDestinatariDto> destinatarisDto = conversioTipusHelper.convertirList(
				destinatarisEntity,
				NotificacioDestinatariDto.class);
		for (int i = 0; i < destinatarisEntity.size(); i++) {
			NotificacioDestinatariEntity destinatariEntity = destinatarisEntity.get(i);
			NotificacioDestinatariDto destinatariDto = destinatarisDto.get(i);
			destinatariDto.setEstat(
					NotificacioDestinatariEntity.calcularEstatNotificacioDestinatari(
							destinatariEntity));
		}
		return destinatarisDto;
	}

	private NotificacioDestinatariDto destinatariToDto(
			NotificacioDestinatariEntity destinatariEntity) {
		NotificacioDestinatariDto destinatariDto = conversioTipusHelper.convertir(
				destinatariEntity,
				NotificacioDestinatariDto.class);
		destinatariDto.setEstat(
				NotificacioDestinatariEntity.calcularEstatNotificacioDestinatari(
						destinatariEntity));
		return destinatariDto;
	}

	private int getNotificaEnviamentsProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.notifica.enviaments.processar.max",
				10);
	}
	private int getSeuEnviamentsProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.seu.enviaments.processar.max",
				10);
	}
	private int getSeuJustificantsProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.seu.justificants.processar.max",
				10);
	}
	private int getSeuNotificaEstatProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.seu.notifica.estat.processar.max",
				10);
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceImpl.class);

}
