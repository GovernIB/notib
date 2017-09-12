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
import es.caib.notib.core.api.dto.NotificacioDestinatariDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
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
import es.caib.notib.core.repository.EntitatRepository;
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
	private EntitatRepository entitatRepository;
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
		EntitatEntity entitat = entitatRepository.findByDir3Codi(entitatDir3Codi);
		entityComprovarHelper.comprovarPermisosAplicacio(entitat.getId());
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
					titularLlinatges(destinatari.getTitularLlinatges()).
					titularTelefon(destinatari.getTitularTelefon()).
					titularEmail(destinatari.getTitularEmail()).
					destinatariLlinatges(destinatari.getDestinatariLlinatges()).
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
				conversioTipusHelper.convertirList(
						notificacioEntity.getDestinataris(),
						NotificacioDestinatariDto.class));
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public NotificacioDto consulta(
			String referencia) {
		logger.debug("Consulta l'estat d'un enviament (referencia=" + referencia + ")");
		NotificacioEntity notificacio = notificacioRepository.findByDestinatariReferencia(
				referencia);
		if (notificacio == null) {
			throw new NotFoundException(
					"ref:" + referencia,
					NotificacioDestinatariEntity.class);
		}
		entityComprovarHelper.comprovarPermisosAplicacio(notificacio.getEntitat().getId());
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
		NotificacioDestinatariDto destinatariDto = conversioTipusHelper.convertir(
				destinatari,
				NotificacioDestinatariDto.class);
		if (destinatariDto != null) {
			destinatariDto.setEstat(
					NotificacioDestinatariEntity.calcularEstatNotificacioDestinatari(destinatari));
		}
		dto.setDestinataris(Arrays.asList(destinatariDto));
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
	public PaginaDto<NotificacioDestinatariDto> destinatariFindByNotificacioPaginat(
			Long notificacioId,
			PaginacioParamsDto paginacioParams ) {
		NotificacioEntity notificacio = notificacioRepository.findOne(notificacioId);
		entityComprovarHelper.comprovarPermisos(
				notificacio.getEntitat().getId(),
				true,
				true);
		return paginacioHelper.toPaginaDto( 
				notificacioDestinatariRepository.findByNotificacioId(
					notificacioId,
					paginacioHelper.toSpringDataPageable(paginacioParams)
					),
				NotificacioDestinatariDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioDestinatariDto> destinatariFindByNotificacio(
			Long notificacioId) {
		entityComprovarHelper.comprovarPermisos(
				notificacioId,
				true,
				true);
		return conversioTipusHelper.convertirList( 
				notificacioDestinatariRepository.findByNotificacioId(notificacioId),
				NotificacioDestinatariDto.class);
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
		return conversioTipusHelper.convertir(
				destinatari,
				NotificacioDestinatariDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public NotificacioDestinatariDto destinatariFindByReferencia(String referencia) {
		logger.debug("Consulta de destinatari donat el seu id (" +
				"referencia=" + referencia + ")");
		NotificacioDestinatariEntity destinatari =
				notificacioDestinatariRepository.findByReferencia(referencia);
		NotificacioEntity notificacio = notificacioRepository.findOne( destinatari.getNotificacio().getId() );
		entityComprovarHelper.comprovarPermisosAplicacio(
				notificacio.getEntitat().getId() );
		return conversioTipusHelper.convertir(
				destinatari,
				NotificacioDestinatariDto.class);
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
	@Scheduled(fixedRateString = "${config:es.caib.notib.tasca.notifica.enviaments.periode}")
	public void notificaEnviamentsPendents() {
		logger.debug("Cercant notificacions pendents d'enviar a Notifica");
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
	}

	@Override
	@Scheduled(fixedRateString = "${config:es.caib.notib.tasca.seu.enviaments.periode}")
	public void seuEnviamentsPendents() {
		logger.debug("Cercant notificacions pendents d'enviar a la seu electrònica");
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
	}
	@Override
	@Scheduled(fixedRateString = "${config:es.caib.notib.tasca.seu.justificants.periode}")
	public void seuNotificacionsPendents() {
		logger.debug("Cercant notificacions pendents de consulta d'estat a la seu electrònica");
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
	}
	@Override
	@Scheduled(fixedRateString = "${config:es.caib.notib.tasca.seu.notifica.estat.periode}")
	public void seuNotificaComunicarEstatPendents() {
		logger.debug("Cercant notificacions provinents de la seu pendents d'actualització d'estat a Notifica");
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
