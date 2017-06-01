/**
 * 
 */
package es.caib.notib.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.NotificacioSeuEstatEnumDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.entity.NotificacioDestinatariEntity;
import es.caib.notib.core.entity.NotificacioEntity;
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

	@Transactional
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
			notificacions = notificacioRepository.findAll(
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

	@Transactional
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
	@Transactional(readOnly = true)
	@Scheduled(fixedRateString = "${config:es.caib.notib.tasca.seu.enviaments.periode}")
	public void seuEnviamentsPendents() {
		logger.debug("Cercant notificacions pendents d'enviar a la seu electrònica");
		int maxPendents = getSeuEnviamentsProcessarMaxProperty();
		List<NotificacioDestinatariEntity> pendents = notificacioDestinatariRepository.findBySeuEstatInOrderBySeuEstatAscSeuDarreraPeticioDataAsc(
				new NotificacioSeuEstatEnumDto[] {
						NotificacioSeuEstatEnumDto.PENDENT,
						NotificacioSeuEstatEnumDto.ERROR_ENVIAMENT},
				new PageRequest(0, maxPendents));
		if (!pendents.isEmpty()) {
			logger.debug("Realitzant enviaments a la seu electrònica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
			for (NotificacioDestinatariEntity pendent: pendents) {
				seuHelper.enviament(pendent);
			}
		} else {
			logger.debug("No hi ha notificacions pendents d'enviar a la seu electrònica");
		}
	}
	@Override
	@Transactional(readOnly = true)
	@Scheduled(fixedRateString = "${config:es.caib.notib.tasca.seu.justificants.periode}")
	public void seuJustificantsPendents() {
		logger.debug("Cercant notificacions pendents de consulta d'estat a la seu electrònica");
		int maxPendents = getSeuJustificantsProcessarMaxProperty();
		List<NotificacioDestinatariEntity> pendents = notificacioDestinatariRepository.findBySeuEstatInOrderBySeuEstatAscSeuDarreraPeticioDataAsc(
				new NotificacioSeuEstatEnumDto[] {
						NotificacioSeuEstatEnumDto.ENVIADA,
						NotificacioSeuEstatEnumDto.ERROR_PROCESSAMENT},
				new PageRequest(0, maxPendents));
		// TODO excloure les notificacions ja processades amb Notifica
		if (!pendents.isEmpty()) {
			logger.debug("Realitzant consulta d'estat a la seu electrònica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
			for (NotificacioDestinatariEntity pendent: pendents) {
				logger.debug(
						"Consulta d'estat a la seu electrònica de la notificació (" +
						"notificacioId=" + pendent.getNotificacio().getId() + ", " +
						"notificacioConcepte=" + pendent.getNotificacio().getConcepte() + ", " +
						"destinatariNom=" + pendent.getDestinatariNom() + ", " +
						"destinatariLlinatges=" + pendent.getDestinatariLlinatges() + ", " +
						"destinatariNif=" + pendent.getDestinatariNif() + ")");
				boolean estatActualitzat = seuHelper.consultaEstat(pendent);
				if (estatActualitzat) {
					notificaHelper.comunicacioSeu(pendent);
				}
			}
		} else {
			logger.debug("No hi ha notificacions pendents de consultar estat a la seu electrònica");
		}
	}
	@Override
	@Transactional(readOnly = true)
	@Scheduled(fixedRateString = "${config:es.caib.notib.tasca.seu.notifica.estat.periode}")
	public void seuNotificaComunicarEstatPendents() {
		logger.debug("Cercant notificacions provinents de la seu pendents d'actualització d'estat a Notifica");
		int maxPendents = getSeuNotificaEstatProcessarMaxProperty();
		List<NotificacioDestinatariEntity> pendents = notificacioDestinatariRepository.findBySeuEstatInOrderBySeuEstatAscSeuDarreraPeticioDataAsc(
				new NotificacioSeuEstatEnumDto[] {
						NotificacioSeuEstatEnumDto.LLEGIDA,
						NotificacioSeuEstatEnumDto.REBUTJADA,
						NotificacioSeuEstatEnumDto.ERROR_NOTIFICA},
				new PageRequest(0, maxPendents));
		if (!pendents.isEmpty()) {
			logger.debug("Realitzant actualització d'estat a Notifica per a " + pendents.size() + " notificacions pendents (màxim=" + maxPendents + ")");
			for (NotificacioDestinatariEntity pendent: pendents) {
				notificaHelper.comunicacioSeu(pendent);
			}
		} else {
			logger.debug("No hi ha notificacions pendents d'actualització d'estat a Notifica");
		}
	}
	
	@Override
	public FitxerDto findFitxer(Long notificacioId) {
		
		NotificacioEntity entity = notificacioRepository.findOne(notificacioId);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		pluginHelper.gestioDocumentalGet(
				entity.getDocumentArxiuId(),
				PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
				output);
		
		return new FitxerDto(
				entity.getDocumentArxiuNom(),
				"pdf",
				output.toByteArray(),
				output.size());
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
