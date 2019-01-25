/**
 * 
 */
package es.caib.notib.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.caib.notib.core.api.dto.ColumnesDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentFiltreDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.entity.ColumnesEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.NotificacioEnviamentHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.ColumnesRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.UsuariRepository;

/**
 * Implementació del servei de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class EnviamentServiceImpl implements EnviamentService {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private ColumnesRepository columnesRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private NotificacioEventRepository notificacioEventRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private NotificacioEnviamentHelper notificacioEnviamentHelper;
	
	
	@Transactional(readOnly = true)
	@Override
	public NotificacioDto findAmbId(Long id) {
		logger.debug("Consulta de la notificacio amb id (id=" + id + ")");
		NotificacioEntity dto = notificacioRepository.findOne(id);
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		
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
				null,
				true,
				true,
				false);
		return enviamentsToDto(notificacioEnviamentRepository.findByNotificacioId(notificacioId));
	}

	@Override
	@Transactional(readOnly = true)
	public NotificacioEnviamentDto enviamentFindAmbId(Long destinatariId) {
		logger.debug("Consulta de destinatari donat el seu id (" +
				"destinatariId=" + destinatariId + ")");
		NotificacioEnviamentEntity destinatari =
				notificacioEnviamentRepository.findOne(destinatariId);
		//NotificacioEntity notificacio = notificacioRepository.findOne( destinatari.getNotificacio().getId() );
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		return enviamentToDto(destinatari);
	}

	@Override
	public PaginaDto<NotificacioEnviamentDtoV2> enviamentFindByUser(
			NotificacioEnviamentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta els enviaments d'una notificació que ha realitzat un usuari");
		
		NotificacioEnviamentDtoV2 enviamentDto = new NotificacioEnviamentDtoV2();
		List<NotificacioEnviamentDtoV2> enviamentsDto = new ArrayList<NotificacioEnviamentDtoV2>();
		List<NotificacioEntity> notificacions = new ArrayList<NotificacioEntity>();
		
		UsuariDto usuariActualDto = aplicacioService.getUsuariActual();
		UsuariEntity usuariActual = usuariRepository.findByCodi(usuariActualDto.getCodi());
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		
		notificacions = notificacioRepository.findByCreatedBy_Codi(
				usuariActual.getCodi());
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		Page<NotificacioEnviamentEntity> enviament = null;
		//Obtenim els enviaments d'una notificació
		for (NotificacioEntity notificacio : notificacions) {
			enviament=  notificacioEnviamentRepository.findByNotificacio(
					filtre.getCodiNotifica() == null || "".equals(filtre.getCodiNotifica().trim()),
					filtre.getCodiNotifica() == null ? "" : filtre.getCodiNotifica(),
					notificacio,
					paginacioHelper.toSpringDataPageable(paginacioParams));
			
			if(enviament != null){
				enviamentDto = notificacioEnviamentHelper.toNotificacioEnviamentDto(
						notificacio, 
						enviament);
				if (enviamentDto != null) 
					enviamentsDto.add(enviamentDto);
			}
		}
		return paginacioHelper.toPaginaDto(
				enviament,
				enviamentsDto,
				NotificacioEnviamentDtoV2.class);
	}


	@Override
	public void columnesCreate(
			UsuariDto usuari,
			Long entitatId, 
			ColumnesDto columnes) {
		
		EntitatEntity entitatEntity = entityComprovarHelper.comprovarEntitat(entitatId);
		UsuariEntity usuariEntity = usuariRepository.findByCodi(usuari.getCodi());
		
		if (columnes == null) {
			columnes = new ColumnesDto();
			
			columnes.setDataEnviament(true);
			columnes.setCodiNotibEnviament(true);
			columnes.setProCodi(true);
			columnes.setGrupCodi(true);
			columnes.setEnviamentTipus(true);
			columnes.setConcepte(true);
			columnes.setTitularNif(true);
			columnes.setTitularNomLlinatge(true);
		}
		// Dades generals de la notificació
		ColumnesEntity.Builder columnesBuilder = ColumnesEntity.getBuilder(
				columnes.isDataEnviament(),
				columnes.isDataProgramada(), 
				columnes.isNotIdentificador(), 
				columnes.isProCodi(),
				columnes.isGrupCodi(),
				columnes.isDir3Codi(), 
				columnes.isUsuari(), 
				columnes.isEnviamentTipus(), 
				columnes.isConcepte(),
				columnes.isDescripcio(), 
				columnes.isTitularNif(), 
				columnes.isTitularNomLlinatge(),
				columnes.isTitularEmail(),
				columnes.isDestinataris(),
				columnes.isLlibreRegistre(),
				columnes.isNumeroRegistre(), 
				columnes.isDataRegistre(), 
				columnes.isDataCaducitat(),
				columnes.isCodiNotibEnviament(), 
				columnes.isNumCertificacio(), 
				columnes.isCsvUuid(), 
				columnes.isEstat(),
				entitatEntity,
				usuariEntity);

		ColumnesEntity columnesEntity = columnesBuilder.build();
		columnesRepository.saveAndFlush(columnesEntity);

	}
	
	@Override
	public void columnesUpdate(
			Long entitatId, 
			ColumnesDto columnes) {

		ColumnesEntity columnesEntity = columnesRepository.findOne(columnes.getId());

		columnesEntity.update(
				columnes.isDataEnviament(), 
				columnes.isDataProgramada(), 
				columnes.isNotIdentificador(),
				columnes.isProCodi(), 
				columnes.isGrupCodi(), 
				columnes.isDir3Codi(), 
				columnes.isUsuari(),
				columnes.isEnviamentTipus(), 
				columnes.isConcepte(), 
				columnes.isDescripcio(), 
				columnes.isTitularNif(),
				columnes.isTitularNomLlinatge(), 
				columnes.isTitularEmail(), 
				columnes.isDestinataris(),
				columnes.isLlibreRegistre(), 
				columnes.isNumeroRegistre(), 
				columnes.isDataRegistre(),
				columnes.isDataCaducitat(), 
				columnes.isCodiNotibEnviament(), 
				columnes.isNumCertificacio(),
				columnes.isCsvUuid(), 
				columnes.isEstat());

		columnesRepository.saveAndFlush(columnesEntity);
	}
	@Override
	public ColumnesDto getColumnesUsuari(
			Long entitatId,
			UsuariDto usuariDto) {
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		UsuariEntity usuari = usuariRepository.findByCodi(usuariDto.getCodi());
		
		ColumnesEntity columnes = columnesRepository.findByEntitatAndUser(
				entitat, 
				usuari);
		
		return conversioTipusHelper.convertir(
				columnes, 
				ColumnesDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<NotificacioEventDto> eventFindAmbNotificacio(
			Long notificacioId) {
		logger.debug("Consulta dels events de la notificació (" +
				"notificacioId=" + notificacioId + ")");
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		return conversioTipusHelper.convertirList(
				notificacioEventRepository.findByNotificacioIdOrderByDataAsc(notificacioId),
				NotificacioEventDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public FitxerDto exportacio(
			Long entitatId,
			Long enviamentId,
			Collection<Long> enviamentIds,
			String format) throws IOException {
		logger.debug("Exportant informació dels enviaments (" +
				"entitatId=" + entitatId + ", " +
				"enviamentId=" + enviamentId + ", " +
				"enviamentsIds=" + enviamentIds + ", " +
				"format=" + format + ")");
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		/*MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
				entitat,
				metaExpedientId,
				true,
				false,
				false,
				false);
		List<ExpedientEntity> expedients = expedientRepository.findByEntitatAndAndMetaNodeAndIdInOrderByIdAsc(
				metaExpedient.getEntitat(),
				metaExpedient,
				expedientIds);
		List<MetaDadaEntity> metaDades = dadaRepository.findDistinctMetaDadaByNodeIdInOrderByMetaDadaCodiAsc(expedientIds);
		List<DadaEntity> dades = dadaRepository.findByNodeIdInOrderByNodeIdAscMetaDadaCodiAsc(expedientIds);
		int numColumnes = 5 + metaDades.size();
		String[] columnes = new String[numColumnes];
		columnes[0] = messageHelper.getMessage("expedient.service.exportacio.numero");
		columnes[1] = messageHelper.getMessage("expedient.service.exportacio.titol");
		columnes[2] = messageHelper.getMessage("expedient.service.exportacio.estat");
		columnes[3] = messageHelper.getMessage("expedient.service.exportacio.datcre");
		columnes[4] = messageHelper.getMessage("expedient.service.exportacio.idnti");
		for (int i = 0; i < metaDades.size(); i++) {
			MetaDadaEntity metaDada = metaDades.get(i);
			columnes[5 + i] = metaDada.getNom() + " (" + metaDada.getCodi() + ")";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		List<String[]> files = new ArrayList<String[]>();
		int dadesIndex = 0;
		for (ExpedientEntity expedient: expedients) {
			String[] fila = new String[numColumnes];
			fila[0] = expedient.getNumero();
			fila[1] = expedient.getNom();
			fila[2] = expedient.getEstat().name();
			fila[3] = sdf.format(expedient.getCreatedDate().toDate());
			fila[4] = expedient.getNtiIdentificador();
			if (!dades.isEmpty()) {
				DadaEntity dadaActual = dades.get(dadesIndex);
				if (dadaActual.getNode().getId().equals(expedient.getId())) {
					for (int i = 0; i < metaDades.size(); i++) {
						MetaDadaEntity metaDada = metaDades.get(i);
						int dadesIndexIncrement = 1;
						while (dadaActual.getNode().getId().equals(expedient.getId())) {
							if (dadaActual.getMetaDada().getCodi().equals(metaDada.getCodi())) {
								break;
							}
							dadaActual = dades.get(dadesIndex + dadesIndexIncrement++);
						}
						if (dadaActual.getMetaDada().getCodi().equals(metaDada.getCodi())) {
							fila[5 + i] = dadaActual.getValorComString();
						}
					}
				}
			}
			files.add(fila);
		}
		FitxerDto fitxer = new FitxerDto();
		if ("ODS".equalsIgnoreCase(format)) {
			Object[][] filesArray = files.toArray(new Object[files.size()][numColumnes]);
			TableModel model = new DefaultTableModel(filesArray, columnes);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			SpreadSheet.createEmpty(model).getPackage().save(baos);
			fitxer.setNom("exportacio.ods");
			fitxer.setContentType("application/vnd.oasis.opendocument.spreadsheet");
			fitxer.setContingut(baos.toByteArray());
		} else if ("CSV".equalsIgnoreCase(format)) {
			fitxer.setNom("exportacio.csv");
			fitxer.setContentType("text/csv");
			StringBuilder sb = new StringBuilder();
			csvHelper.afegirLinia(sb, columnes, ';');
			for (String[] fila: files) {
				csvHelper.afegirLinia(sb, fila, ';');
			}
			fitxer.setContingut(sb.toString().getBytes());
		} else {
			throw new ValidationException("Format de fitxer no suportat: " + format);
		}*/
		return null;
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
		enviamentDto.setNotificaCertificacioArxiuNom(
				calcularNomArxiuCertificacio(enviament));
	}

	private String calcularNomArxiuCertificacio(
			NotificacioEnviamentEntity enviament) {
		return "certificacio_" + enviament.getNotificaIdentificador() + ".pdf";
	}
	
	private static final Logger logger = LoggerFactory.getLogger(EnviamentServiceImpl.class);


	@Override
	public List<Long> findIdsAmbFiltre(Long entitatId, NotificacioEnviamentFiltreDto filtre) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

}
