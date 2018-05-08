/**
 * 
 */
package es.caib.notib.core.service;

import java.io.ByteArrayOutputStream;
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
import es.caib.notib.core.api.dto.NotificaRespostaEstatDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
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
	private NotificacioEnviamentRepository notificacioDestinatariRepository;
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
			Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams);
			notificacions = notificacioRepository.findAmbFiltre(
							filtre.getConcepte(),
							filtre.getDataInici(),
							filtre.getDataFi(),
							filtre.getDestinatari() == null || filtre.getDestinatari().isEmpty(), 
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
	public PaginaDto<NotificacioDto> findAmbEntitatIFiltrePaginat(
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
					notificacioRepository.findByEntitatId(
							entitatId,
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					NotificacioDto.class);
		}
		return notificacions;
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
				notificacioDestinatariRepository.findByNotificacioId(notificacioId));
	}

	@Override
	@Transactional(readOnly = true)
	public NotificacioEnviamentDto enviamentFindAmbId(Long destinatariId) {
		logger.debug("Consulta de destinatari donat el seu id (" +
				"destinatariId=" + destinatariId + ")");
		NotificacioEnviamentEntity destinatari =
				notificacioDestinatariRepository.findOne(destinatariId);
		NotificacioEntity notificacio = notificacioRepository.findOne( destinatari.getNotificacio().getId() );
		entityComprovarHelper.comprovarPermisos(
				notificacio.getEntitat().getId(),
				true,
				true);
		return enviamentToDto(destinatari);
	}

	@Override
	@Transactional(readOnly = true)
	public NotificacioEnviamentDto enviamentFindAmbReferencia(String referencia) {
		logger.debug("Consulta de destinatari donada la referència (" +
				"referencia=" + referencia + ")");
		NotificacioEnviamentEntity destinatari =
				notificacioDestinatariRepository.findByNotificaReferencia(
						referencia);
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
				notificacioEventRepository.findByNotificacioIdOrderByDataDesc(notificacioId),
				NotificacioEventDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbNotificacioIEnviament(
			Long notificacioId,
			Long enviamentId) {
		logger.debug("Consulta dels events associats a un destinatari (" +
				"notificacioId=" + notificacioId + ", " + 
				"enviamentId=" + enviamentId + ")");
		NotificacioEnviamentEntity destinatari = notificacioDestinatariRepository.findOne(enviamentId);
		entityComprovarHelper.comprovarPermisos(
				destinatari.getNotificacio().getId(),
				true,
				true);
		return conversioTipusHelper.convertirList(
				notificacioEventRepository.findByEnviamentIdOrderByDataDesc(enviamentId),
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
				notificacioDestinatariRepository.findOne(enviamentId);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				enviament.getNotificaCertificacioArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
				output);
		return new ArxiuDto(
				enviament.getNotificaCertificacioArxiuId(),
				enviament.getNotificaCertificacioArxiuTipus().toString(),
				output.toByteArray(),
				output.size());
	}

	@Override
	public boolean enviar(
			Long notificacioId) {
		logger.debug("Intentant enviament de la notificació pendent (" +
				"notificacioId=" + notificacioId + ")");
		boolean enviada = notificaHelper.enviament(notificacioId);
		/*if (enviada && pluginHelper.isSeuPluginDisponible()) {
			List<NotificacioEnviamentEntity> pendents = notificacioDestinatariRepository.findBySeuEstatInOrderBySeuDataNotificaDarreraPeticioAsc(
					new NotificacioDestinatariEstatEnumDto[] {
							NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT});
			for (NotificacioEnviamentEntity pendent: pendents) {
				seuHelper.enviament(pendent.getId());
			}
		}*/
		return enviada;
	}

	@Override
	@Transactional
	public NotificaRespostaEstatDto enviamentRefrescarEstat(
			Long destinatariId) {
		logger.debug("Refrescant l'estat de la notificació amb informació de Notific@ (" +
				"destinatariId=" + destinatariId + ")");
		NotificacioEnviamentEntity destinatari = notificacioDestinatariRepository.findOne(destinatariId);
		return notificaHelper.refrescarEstat(destinatari);
	}

	@Override
	@Transactional
	public boolean enviamentComunicacioSeu(
			Long destinatariId) {
		logger.debug("Enviant canvi d'estat de la seu electrònica a Notific@ (" +
				"destinatariId=" + destinatariId + ")");
		return notificaHelper.comunicacioSeu(destinatariId);
	}

	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.notifica.enviaments.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.retard.inicial}")
	public void notificaEnviamentsPendents() {
		logger.debug("Cercant notificacions pendents d'enviar a Notifica");
		if (isTasquesActivesProperty() && notificaHelper.isConnexioNotificaDisponible()) {
			int maxPendents = getNotificaEnviamentsProcessarMaxProperty();
			List<NotificacioEntity> pendents = notificacioRepository.findByNotificaEstatPendent(new PageRequest(0, maxPendents));
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
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.seu.enviaments.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.retard.inicial}")
	public void seuEnviamentsPendents() {
		logger.debug("Cercant notificacions pendents d'enviar a la seu electrònica");
		if (pluginHelper.isSeuPluginDisponible() && isTasquesActivesProperty() && pluginHelper.isSeuPluginDisponible()) {
			int maxPendents = getSeuEnviamentsProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioDestinatariRepository.findBySeuEstatInAndMaxReintentsOrderBySeuDataNotificaDarreraPeticioAsc(
					new NotificacioDestinatariEstatEnumDto[] {NotificacioDestinatariEstatEnumDto.NOTIB_PENDENT},
					3,
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
	@Override
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.seu.justificants.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.retard.inicial}")
	public void seuNotificacionsPendents() {
		logger.debug("Cercant notificacions pendents de consulta d'estat a la seu electrònica");
		if (pluginHelper.isSeuPluginDisponible() && isTasquesActivesProperty() && pluginHelper.isSeuPluginDisponible()) {
			int maxPendents = getSeuJustificantsProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioDestinatariRepository.findBySeuEstatInAndMaxReintentsOrderBySeuDataNotificaDarreraPeticioAsc(
					new NotificacioDestinatariEstatEnumDto[] {NotificacioDestinatariEstatEnumDto.NOTIB_ENVIADA},
					3,
					new PageRequest(0, maxPendents));
			// TODO excloure les notificacions ja processades amb Notifica
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant consulta d'estat a la seu electrònica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					boolean estatActualitzat = seuHelper.consultaEstat(pendent.getId());
					if (estatActualitzat) {
						notificaHelper.comunicacioSeu(pendent.getId());
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
	@Scheduled(
			fixedRateString = "${config:es.caib.notib.tasca.seu.notifica.estat.periode}",
			initialDelayString = "${config:es.caib.notib.tasca.retard.inicial}")
	public void seuNotificaComunicarEstatPendents() {
		logger.debug("Cercant notificacions provinents de la seu pendents d'actualització d'estat a Notifica");
		if (pluginHelper.isSeuPluginDisponible() && isTasquesActivesProperty() && pluginHelper.isSeuPluginDisponible()) {
			int maxPendents = getSeuNotificaEstatProcessarMaxProperty();
			List<NotificacioEnviamentEntity> pendents = notificacioDestinatariRepository.findBySeuEstatInAndMaxReintentsOrderBySeuDataNotificaDarreraPeticioAsc(
					new NotificacioDestinatariEstatEnumDto[] {
							NotificacioDestinatariEstatEnumDto.LLEGIDA,
							NotificacioDestinatariEstatEnumDto.REBUTJADA},
					3,
					new PageRequest(0, maxPendents));
			if (!pendents.isEmpty()) {
				logger.debug("Realitzant actualització d'estat a Notifica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
				for (NotificacioEnviamentEntity pendent: pendents) {
					notificaHelper.comunicacioSeu(pendent.getId());
				}
			} else {
				logger.debug("No hi ha notificacions pendents d'actualització d'estat a Notifica");
			}
		} else {
			logger.warn("La connexió amb la seu electrònica no està activa i no es realitzarà cap enviament");
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
			destinatariEmplenarDadesAddicionals(
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
		destinatariEmplenarDadesAddicionals(
				enviament,
				destinatariDto);
		return destinatariDto;
	}

	private void destinatariEmplenarDadesAddicionals(
			NotificacioEnviamentEntity enviament,
			NotificacioEnviamentDto enviamentDto) {
		enviamentDto.setEstat(enviament.getNotificaEstat());
		if (enviament.isNotificaError()) {
			enviamentDto.setNotificaError(true);
			NotificacioEventEntity event = enviament.getNotificaErrorEvent();
			if (event != null) {
				enviamentDto.setNotificaErrorData(event.getData());
				enviamentDto.setNotificaErrorError(event.getErrorDescripcio());
			}
		}
		if (enviament.isSeuError()) {
			enviamentDto.setSeuError(true);
			NotificacioEventEntity event = enviament.getSeuErrorEvent();
			if (event != null) {
				enviamentDto.setSeuErrorData(event.getData());
				enviamentDto.setSeuErrorError(event.getErrorDescripcio());
			}
		}
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
