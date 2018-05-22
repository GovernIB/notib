/**
 * 
 */
package es.caib.notib.core.service;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
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

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.helper.PropertiesHelper;
import es.caib.notib.core.helper.SeuHelper;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
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
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
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



	@Transactional(readOnly = true)
	@Override
	public NotificacioDto findAmbId(Long id) {
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
	public PaginaDto<NotificacioDto> findAmbFiltrePaginat(
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
			Date dataInici = filtre.getDataInici();
			if (dataInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataInici);
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataInici = cal.getTime();
			}
			Date dataFi = filtre.getDataFi();
			if (dataFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataInici);
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataFi = cal.getTime();
			}
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams);
			notificacions = notificacioRepository.findAmbFiltre(
					filtre.getEntitatId() == null,
					filtre.getEntitatId(),
					//filtre.getComunicacioTipus() == null,
					//filtre.getComunicacioTipus(),
					false,
					NotificacioComunicacioTipusEnumDto.SINCRON,
					filtre.getEnviamentTipus() == null,
					filtre.getEnviamentTipus(),
					filtre.getConcepte() == null,
					filtre.getConcepte(),
					filtre.getEstat() == null,
					filtre.getEstat(),
					dataInici == null && dataFi == null,
					dataInici,
					dataFi,
					filtre.getTitular() == null || filtre.getTitular().isEmpty(), 
					filtre.getTitular(),
					pageable);
		}
		return paginacioHelper.toPaginaDto(
				notificacions,
				NotificacioDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEnviamentDto> enviamentFindAmbNotificacio(
			Long notificacioId) {
		logger.debug("Consulta els destinataris d'una notificació (" +
				"notificacioId=" + notificacioId + ")");
		entityComprovarHelper.comprovarPermisos(
				notificacioId,
				true,
				true);
		return enviamentsToDto(
				notificacioEnviamentRepository.findByNotificacioId(notificacioId));
	}

	@Override
	@Transactional(readOnly = true)
	public NotificacioEnviamentDto enviamentFindAmbId(Long destinatariId) {
		logger.debug("Consulta de destinatari donat el seu id (" +
				"destinatariId=" + destinatariId + ")");
		NotificacioEnviamentEntity destinatari =
				notificacioEnviamentRepository.findOne(destinatariId);
		NotificacioEntity notificacio = notificacioRepository.findOne( destinatari.getNotificacio().getId() );
		entityComprovarHelper.comprovarPermisos(
				notificacio.getEntitat().getId(),
				true,
				true);
		return enviamentToDto(destinatari);
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long notificacioId) {
		logger.debug("Consulta dels events de la notificació (" +
				"notificacioId=" + notificacioId + ")");
		entityComprovarHelper.comprovarPermisos(
				notificacioId,
				true,
				true);
		return conversioTipusHelper.convertirList(
				notificacioEventRepository.findByNotificacioIdOrderByDataAsc(notificacioId),
				NotificacioEventDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbEnviament(
			Long notificacioId,
			Long enviamentId) {
		logger.debug("Consulta dels events associats a un destinatari (" +
				"notificacioId=" + notificacioId + ", " + 
				"enviamentId=" + enviamentId + ")");
		NotificacioEnviamentEntity destinatari = notificacioEnviamentRepository.findOne(enviamentId);
		entityComprovarHelper.comprovarPermisos(
				destinatari.getNotificacio().getId(),
				true,
				true);
		return conversioTipusHelper.convertirList(
				notificacioEventRepository.findByNotificacioIdOrEnviamentIdOrderByDataAsc(
						notificacioId,
						enviamentId),
				NotificacioEventDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto getDocumentArxiu(
			Long notificacioId) {
		NotificacioEntity entity = notificacioRepository.findOne(notificacioId);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				entity.getDocumentArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				output);
		return new ArxiuDto(
				entity.getDocumentArxiuNom(),
				"PDF",
				output.toByteArray(),
				output.size());
	}

	@Override
	@Transactional(readOnly = true)
	public ArxiuDto enviamentGetCertificacioArxiu(
			Long enviamentId) {
		NotificacioEnviamentEntity enviament =
				notificacioEnviamentRepository.findOne(enviamentId);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				enviament.getNotificaCertificacioArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
				output);
		return new ArxiuDto(
				calcularNomArxiuCertificacio(enviament),
				enviament.getNotificaCertificacioMime(),
				output.toByteArray(),
				output.size());
	}

	@Override
	public boolean enviar(
			Long notificacioId) {
		logger.debug("Intentant enviament de la notificació pendent (" +
				"notificacioId=" + notificacioId + ")");
		return notificaHelper.notificacioEnviar(notificacioId);
	}

	@Override
	@Transactional
	public NotificacioEnviamenEstatDto enviamentRefrescarEstat(
			Long enviamentId) {
		logger.debug("Refrescant l'estat de la notificació de Notific@ (" +
				"enviamentId=" + enviamentId + ")");
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		notificaHelper.enviamentRefrescarEstat(enviament.getId());
		NotificacioEnviamenEstatDto estatDto = conversioTipusHelper.convertir(
				enviament,
				NotificacioEnviamenEstatDto.class);
		estatCalcularCampsAddicionals(
				enviament,
				estatDto);
		return estatDto;
	}

	@Override
	@Transactional
	public boolean enviamentComunicacioSeu(
			Long enviamentId) {
		logger.debug("Enviant canvi d'estat de la seu electrònica a Notific@ (" +
				"enviamentId=" + enviamentId + ")");
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		return notificaHelper.enviamentComunicacioSeu(
				enviament.getId(),
				new Date());
	}

	@Override
	@Transactional
	public boolean enviamentCertificacioSeu(
			Long enviamentId,
			ArxiuDto certificacioArxiu) {
		logger.debug("Enviant certificació de la seu electrònica a Notific@ (" +
				"enviamentId=" + enviamentId + ")");
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findOne(enviamentId);
		return notificaHelper.enviamentCertificacioSeu(
				enviament.getId(),
				certificacioArxiu,
				new Date());
	}

	// 1. Envia les notificacions pendents d'enviar a Notifica cap a Notifica
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.notifica.enviaments.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.notifica.enviaments.retard.inicial}")
	public void notificaEnviamentsPendents() {
		logger.debug("Cercant notificacions pendents d'enviar a Notifica");
		if (isTasquesActivesProperty() && notificaHelper.isConnexioNotificaDisponible()) {
			int maxPendents = getNotificaEnviamentsProcessarMaxProperty();
			List<NotificacioEntity> pendents = notificacioRepository.findByNotificaEstatPendent(
					pluginHelper.getNotificaReintentsMaxProperty(), 
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant enviaments a Notifica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEntity pendent: pendents) {
					notificaHelper.notificacioEnviar(pendent.getId());
				}
			} else {
				logger.debug("No hi ha notificacions pendents d'enviar a la seu electrònica");
			}
		} else {
			logger.warn("La connexió amb Notifica no està configurada i no es realitzarà cap enviament");
		}
	}

	// 2. Envia les notificacions ja enviades a Notifica, i pendents d'enviar a la Seu, cap a la Seu
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.seu.enviaments.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.seu.enviaments.retard.inicial}")
	public void seuEnviamentsPendents() {
		logger.debug("Cercant notificacions pendents d'enviar a la seu electrònica");
		if (pluginHelper.isSeuPluginDisponible() && isTasquesActivesProperty() && pluginHelper.isSeuPluginDisponible()) {
			int maxPendents = getSeuEnviamentsProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findBySeuEstatPendent(
					pluginHelper.getSeuReintentsMaxProperty(),
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant enviaments a la seu electrònica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					seuHelper.enviament(pendent.getId());
				}
			} else {
				logger.debug("No hi ha notificacions pendents d'enviar a la seu electrònica");
			}
		} else {
			logger.warn("La connexió amb la seu electrònica no està activa i no es realitzarà cap enviament");
		}
	}
	
	// 3. Consulta les notificacions que es troben a la Seu, i que encara no han estat finalitzades a Notifica
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.seu.consulta.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.seu.consulta.retard.inicial}")
	public void seuConsultaEstatNotificacions() {
		logger.debug("Cercant notificacions pendents de consulta d'estat a la seu electrònica");
		if (pluginHelper.isSeuPluginDisponible() && isTasquesActivesProperty() && pluginHelper.isSeuPluginDisponible()) {
			int maxPendents = getSeuConsultaProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findBySeuEstatEnviat(new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant consulta d'estat a la seu electrònica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					boolean estatActualitzat = seuHelper.consultaEstat(pendent.getId());
					if (estatActualitzat) {
						notificaHelper.enviamentSeu(pendent.getId());
					}
				}
			} else {
				logger.debug("No hi ha notificacions pendents de consultar estat a la seu electrònica");
			}
		} else {
			logger.warn("La connexió amb la seu electrònica no està activa i no es realitzarà cap enviament");
		}
	}
	
	// 4. Envia les actualitzacions d'estat les notificacions de la Seu cap a Notifica
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.notifica.enviament.estat.seu.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.notifica.enviament.estat.seu.retard.inicial}")
	public void notificaInformaCanviEstatSeu() {
		logger.debug("Cercant notificacions de la seu pendents d'actualitzar l'estat a Notifica");
		if (pluginHelper.isSeuPluginDisponible() && isTasquesActivesProperty() && pluginHelper.isSeuPluginDisponible()) {
			int maxPendents = getNotificaCanviEstatSeuProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findBySeuEstatModificat(new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant actualització d'estat a Notifica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					notificaHelper.enviamentSeu(pendent.getId());
				}
			} else {
				logger.debug("No hi ha notificacions pendents d'actualització d'estat a Notifica");
			}
		} else {
			logger.warn("La connexió amb la seu electrònica no està activa i no es realitzarà cap enviament");
		}
	}

	// 5. Refresca l'estat de les notificacions segons l'estat de Notifica
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
//	@Scheduled(
//			fixedRateString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.periode}",
//			initialDelayString = "${config:es.caib.notib.tasca.enviament.actualitzacio.estat.retard.inicial}")
	public void enviamentRefrescarEstatPendents() {
		logger.debug("Cercant enviaments pendents de refrescar l'estat de Notifica");
		if (isTasquesActivesProperty() && notificaHelper.isConnexioNotificaDisponible()) {
			int maxPendents = getEnviamentActualitzacioEstatProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioEnviamentRepository.findByNotificaEstatFinalFalseOrderByEstatDataActualitzacioAsc(
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant refresc de l'estat de Notifica per a " + pendents.size() + " enviaments (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					notificaHelper.enviamentRefrescarEstat(pendent.getId());
				}
			} else {
				logger.debug("No hi ha enviaments pendents de refrescar l'estat de Notifica");
			}
		} else {
			logger.warn("La connexió amb Notific@ no està activa i no es realitzarà cap refresca d'estat");
		}
	}



	private List<NotificacioEnviamentDto> enviamentsToDto(
			List<NotificacioEnviamentEntity> enviaments) {
		List<NotificacioEnviamentDto> destinatarisDto = conversioTipusHelper.convertirList(
				enviaments,
				NotificacioEnviamentDto.class);
		for (int i = 0; i < enviaments.size(); i++) {
			NotificacioEnviamentEntity destinatariEntity = enviaments.get(i);
			NotificacioEnviamentDto destinatariDto = destinatarisDto.get(i);
			destinatariCalcularCampsAddicionals(
					destinatariEntity,
					destinatariDto);
		}
		return destinatarisDto;
	}

	private NotificacioEnviamentDto enviamentToDto(
			NotificacioEnviamentEntity enviament) {
		NotificacioEnviamentDto destinatariDto = conversioTipusHelper.convertir(
				enviament,
				NotificacioEnviamentDto.class);
		destinatariCalcularCampsAddicionals(
				enviament,
				destinatariDto);
		return destinatariDto;
	}

	private void destinatariCalcularCampsAddicionals(
			NotificacioEnviamentEntity enviament,
			NotificacioEnviamentDto enviamentDto) {
		if (enviament.isNotificaError()) {
			NotificacioEventEntity event = enviament.getNotificaErrorEvent();
			if (event != null) {
				enviamentDto.setNotificaErrorData(event.getData());
				enviamentDto.setNotificaErrorDescripcio(event.getErrorDescripcio());
			}
		}
		if (enviament.isSeuError()) {
			NotificacioEventEntity event = enviament.getSeuErrorEvent();
			if (event != null) {
				enviamentDto.setSeuErrorData(event.getData());
				enviamentDto.setSeuErrorDescripcio(event.getErrorDescripcio());
			}
		}
		enviamentDto.setNotificaCertificacioArxiuNom(
				calcularNomArxiuCertificacio(enviament));
	}
	private void estatCalcularCampsAddicionals(
			NotificacioEnviamentEntity enviament,
			NotificacioEnviamenEstatDto estatDto) {
		if (enviament.isNotificaError()) {
			NotificacioEventEntity event = enviament.getNotificaErrorEvent();
			if (event != null) {
				estatDto.setNotificaErrorData(event.getData());
				estatDto.setNotificaErrorDescripcio(event.getErrorDescripcio());
			}
		}
		if (enviament.isSeuError()) {
			NotificacioEventEntity event = enviament.getSeuErrorEvent();
			if (event != null) {
				estatDto.setSeuErrorData(event.getData());
				estatDto.setSeuErrorDescripcio(event.getErrorDescripcio());
			}
		}
		estatDto.setNotificaCertificacioArxiuNom(
				calcularNomArxiuCertificacio(enviament));
	}

	private String calcularNomArxiuCertificacio(
			NotificacioEnviamentEntity enviament) {
		return "certificacio_" + enviament.getNotificaIdentificador() + ".pdf";
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
	private int getSeuConsultaProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.seu.consulta.processar.max",
				10);
	}
	private int getNotificaCanviEstatSeuProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.notifica.enviament.estat.seu.processar.max",
				10);
	}
	private int getEnviamentActualitzacioEstatProcessarMaxProperty() {
		return propertiesHelper.getAsInt(
				"es.caib.notib.tasca.enviament.actualitzacio.estat.processar.max",
				10);
	}

	private boolean isTasquesActivesProperty() {
		String actives = propertiesHelper.getProperty("es.caib.notib.tasques.actives");
		if (actives != null) {
			return new Boolean(actives).booleanValue();
		} else {
			return true;
		}
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceImpl.class);

}
