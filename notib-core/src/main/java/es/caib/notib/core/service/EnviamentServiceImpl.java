/**
 * 
 */
package es.caib.notib.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.ColumnesDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentFiltreDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioTipusEnviamentEnumDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.entity.ColumnesEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.MessageHelper;
import es.caib.notib.core.helper.PaginacioHelper;
import es.caib.notib.core.repository.ColumnesRepository;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.UsuariRepository;

/**
 * Implementació del servei de gestió de enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class EnviamentServiceImpl implements EnviamentService {

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
	private EntitatRepository entitatRepository;
	@Autowired
	private MessageHelper messageHelper;
	
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
		NotificacioEntity notificacio = notificacioRepository.findById(notificacioId);
		List<NotificacioEnviamentEntity> enviaments = notificacioEnviamentRepository.findByNotificacio(notificacio);
		return enviamentsToDto(enviaments);
	}


	@Override
	public List<Long> findIdsAmbFiltre(
			Long entitatId, 
			NotificacioEnviamentFiltreDto filtre) throws NotFoundException, ParseException  {
		logger.debug("Consultant els ids d'expedient segons el filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		entityComprovarHelper.comprovarPermisos(
				entitatId,
				true,
				false,
				false);
		return findIdsAmbFiltrePaginat(
				entitatId,
				filtre);
	}
	
	private List<Long> findIdsAmbFiltrePaginat(
			Long entitatId,
			NotificacioEnviamentFiltreDto filtre) throws ParseException {
		logger.debug("Consulta els enviaments de les notificacións que te una entitat");
		Date dataEnviamentInici = null,
			 dataEnviamentFi = null,
			 dataProgramadaDisposicioInici = null,
			 dataProgramadaDisposicioFi = null,
			 dataRegistreInici = null,
			 dataRegistreFi = null,
			 dataCaducitatInici = null,
			 dataCaducitatFi = null;
		
		EntitatEntity entitatEntity = entitatRepository.findOne(entitatId);
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		
		if (filtre.getDataEnviamentInici() != null && filtre.getDataEnviamentInici() != "") {
			dataEnviamentInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentInici());
			if (dataEnviamentInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataEnviamentInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataEnviamentInici = cal.getTime();
			}
		}
		if (filtre.getDataEnviamentFi() != null && filtre.getDataEnviamentFi() != "") {
			dataEnviamentFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentFi());
			if (dataEnviamentFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataEnviamentFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataEnviamentFi = cal.getTime();
			}
		}
		if (filtre.getDataProgramadaDisposicioInici() != null && filtre.getDataProgramadaDisposicioInici() != "") {
			dataProgramadaDisposicioInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioInici());
			if (dataProgramadaDisposicioInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataProgramadaDisposicioInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataProgramadaDisposicioInici = cal.getTime();
			}
		}
		if (filtre.getDataProgramadaDisposicioFi() != null && filtre.getDataProgramadaDisposicioFi() != "") {
			dataProgramadaDisposicioFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioFi());
			if (dataProgramadaDisposicioFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataProgramadaDisposicioFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataProgramadaDisposicioFi = cal.getTime();
			}
		}
		if (filtre.getDataRegistreInici() != null && filtre.getDataRegistreInici() != "") {
			dataRegistreInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreInici());
			if (dataRegistreInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataRegistreInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataRegistreInici = cal.getTime();
			}
		}
		if (filtre.getDataRegistreFi() != null && filtre.getDataRegistreFi() != "") {
			dataRegistreFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreFi());
			if (dataRegistreFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataRegistreFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataRegistreFi = cal.getTime();
			}
		}
		if (filtre.getDataCaducitatInici() != null && filtre.getDataCaducitatInici() != "") {
			dataCaducitatInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatInici());
			if (dataCaducitatInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataCaducitatInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataCaducitatInici = cal.getTime();
			}
		}
		if (filtre.getDataCaducitatFi() != null && filtre.getDataCaducitatFi() != "") {
			dataCaducitatFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatFi());
			if (dataCaducitatFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataCaducitatFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataCaducitatFi = cal.getTime();
			}
		}
		//Filtres camps procediment
		Integer estat = null;
		Integer tipusEnviament = null;
		if(filtre.getEstat()!=null){
			estat = filtre.getEstat().getNumVal();
		}else{
			estat = 0;
		}
		if(filtre.getEnviamentTipus()!=null){
			tipusEnviament = NotificacioTipusEnviamentEnumDto.getNumVal(filtre.getEnviamentTipus());
		}else{
			tipusEnviament = 0;
		}
		List<NotificacioEnviamentEntity> enviament = null;
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
			
			enviament = notificacioEnviamentRepository.findByNotificacio(
					filtre.getCodiProcediment() == null || filtre.getCodiProcediment().isEmpty(),
					filtre.getCodiProcediment() == null ? "" : filtre.getCodiProcediment(),
					filtre.getGrup() == null || filtre.getGrup().isEmpty(),
					filtre.getGrup() == null ? "" : filtre.getGrup(),
					filtre.getConcepte() == null || filtre.getConcepte().isEmpty(),
					filtre.getConcepte() == null ? "" : filtre.getConcepte(),
					filtre.getDescripcio() == null || filtre.getDescripcio().isEmpty(),
					filtre.getDescripcio() == null ? "" : filtre.getDescripcio(),
					(dataProgramadaDisposicioInici == null),
					dataProgramadaDisposicioInici,
					(dataProgramadaDisposicioFi == null),
					dataProgramadaDisposicioFi,
					(dataCaducitatInici == null),
					dataCaducitatInici,
					(dataCaducitatFi == null),
					dataCaducitatFi,
					(filtre.getEnviamentTipus() == null),
					(tipusEnviament),
					(filtre.getCsvUuid() == null || filtre.getCsvUuid().isEmpty()),
					filtre.getCsvUuid(),
					(filtre.getEstat() == null),
					(estat),
					entitatEntity,
					(dataEnviamentInici == null),
					dataEnviamentInici,
					(dataEnviamentFi == null),
					dataEnviamentFi,
					(filtre.getCodiNotifica() == null || filtre.getCodiNotifica().isEmpty()),
					filtre.getCodiNotifica() == null ? "" : filtre.getCodiNotifica(),
					(filtre.getCreatedBy() == null || filtre.getCreatedBy().getCodi().isEmpty()),
					conversioTipusHelper.convertir(filtre.getCreatedBy(), UsuariEntity.class),
					(filtre.getNifTitular() == null || filtre.getNifTitular().isEmpty()),
					filtre.getNifTitular() == null ? "" : filtre.getNifTitular(),
					(filtre.getTitularNomLlinatge() == null || filtre.getTitularNomLlinatge().isEmpty()),
					filtre.getTitularNomLlinatge() == null ? "" : filtre.getTitularNomLlinatge(),
					(filtre.getEmailTitular() == null || filtre.getEmailTitular().isEmpty()),
					filtre.getEmailTitular() == null ? "" : filtre.getEmailTitular(),
					(filtre.getDir3Codi() == null || filtre.getDir3Codi().isEmpty()),
					filtre.getDir3Codi() == null ? "" : filtre.getDir3Codi(),
					(filtre.getNumeroCertCorreus() == null || filtre.getNumeroCertCorreus().isEmpty()),
					filtre.getNumeroCertCorreus() == null ? "" : filtre.getNumeroCertCorreus(),
					(filtre.getUsuari() == null || filtre.getUsuari().isEmpty()),
					filtre.getUsuari() == null ? "" : filtre.getUsuari(),
					(filtre.getRegistreLlibre() == null || filtre.getRegistreLlibre().isEmpty()),
					filtre.getRegistreLlibre() == null ? "" : filtre.getRegistreLlibre(),
					(filtre.getRegistreNumero() == null || filtre.getRegistreNumero().isEmpty()),
					filtre.getRegistreNumero() == null ? "" : filtre.getRegistreNumero(),
					(dataRegistreInici == null),
					dataRegistreInici,
					(dataRegistreFi == null),
					dataRegistreFi);
			
		List<Long> enviamentIds = new ArrayList<Long>();
		for(NotificacioEnviamentEntity nee: enviament) {
			enviamentIds.add(nee.getId());
			nee.setNotificacio(notificacioRepository.findById(nee.getNotificacioId()));
		}
		return enviamentIds;
	}
	
	@Override
	@Transactional(readOnly = true)
	public NotificacioEnviamentDto enviamentFindAmbId(Long enviamentId) {
		logger.debug("Consulta de destinatari donat el seu id (" +
				"destinatariId=" + enviamentId + ")");
		NotificacioEnviamentEntity enviament = notificacioEnviamentRepository.findById(enviamentId);
		//NotificacioEntity notificacio = notificacioRepository.findOne( destinatari.getNotificacio().getId() );
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		return enviamentToDto(enviament);
	}

	@Override
	public PaginaDto<NotificacioEnviamentDtoV2> enviamentFindByEntityAndFiltre(
			EntitatDto entitat,
			NotificacioEnviamentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws ParseException {
		logger.debug("Consulta els enviaments de les notificacións que te una entitat");
		Date dataEnviamentInici = null,
			 dataEnviamentFi = null,
			 dataProgramadaDisposicioInici = null,
			 dataProgramadaDisposicioFi = null,
			 dataRegistreInici = null,
			 dataRegistreFi = null,
			 dataCaducitatInici = null,
			 dataCaducitatFi = null;
		
		EntitatEntity entitatEntity = entitatRepository.findOne(entitat.getId());
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		
		if (filtre.getDataEnviamentInici() != null && filtre.getDataEnviamentInici() != "") {
			dataEnviamentInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentInici());
			if (dataEnviamentInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataEnviamentInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataEnviamentInici = cal.getTime();
			}
		}
		if (filtre.getDataEnviamentFi() != null && filtre.getDataEnviamentFi() != "") {
			dataEnviamentFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentFi());
			if (dataEnviamentFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataEnviamentFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataEnviamentFi = cal.getTime();
			}
		}
		if (filtre.getDataProgramadaDisposicioInici() != null && filtre.getDataProgramadaDisposicioInici() != "") {
			dataProgramadaDisposicioInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioInici());
			if (dataProgramadaDisposicioInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataProgramadaDisposicioInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataProgramadaDisposicioInici = cal.getTime();
			}
		}
		if (filtre.getDataProgramadaDisposicioFi() != null && filtre.getDataProgramadaDisposicioFi() != "") {
			dataProgramadaDisposicioFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioFi());
			if (dataProgramadaDisposicioFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataProgramadaDisposicioFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataProgramadaDisposicioFi = cal.getTime();
			}
		}
		if (filtre.getDataRegistreInici() != null && filtre.getDataRegistreInici() != "") {
			dataRegistreInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreInici());
			if (dataRegistreInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataRegistreInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataRegistreInici = cal.getTime();
			}
		}
		if (filtre.getDataRegistreFi() != null && filtre.getDataRegistreFi() != "") {
			dataRegistreFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreFi());
			if (dataRegistreFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataRegistreFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataRegistreFi = cal.getTime();
			}
		}
		if (filtre.getDataCaducitatInici() != null && filtre.getDataCaducitatInici() != "") {
			dataCaducitatInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatInici());
			if (dataCaducitatInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataCaducitatInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataCaducitatInici = cal.getTime();
			}
		}
		if (filtre.getDataCaducitatFi() != null && filtre.getDataCaducitatFi() != "") {
			dataCaducitatFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatFi());
			if (dataCaducitatFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataCaducitatFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataCaducitatFi = cal.getTime();
			}
		}
		//Filtres camps procediment
		Integer estat = null;
		Integer tipusEnviament = null;
		if(filtre.getEstat()!=null){
			estat = filtre.getEstat().getNumVal();
		}else{
			estat = 0;
		}
		if(filtre.getEnviamentTipus()!=null){
			tipusEnviament = NotificacioTipusEnviamentEnumDto.getNumVal(filtre.getEnviamentTipus());
		}else{
			tipusEnviament = 0;
		}
		Page<NotificacioEnviamentEntity> enviament = null;
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
			
			enviament = notificacioEnviamentRepository.findByNotificacio(
					filtre.getCodiProcediment() == null || filtre.getCodiProcediment().isEmpty(),
					filtre.getCodiProcediment() == null ? "" : filtre.getCodiProcediment(),
					filtre.getGrup() == null || filtre.getGrup().isEmpty(),
					filtre.getGrup() == null ? "" : filtre.getGrup(),
					filtre.getConcepte() == null || filtre.getConcepte().isEmpty(),
					filtre.getConcepte() == null ? "" : filtre.getConcepte(),
					filtre.getDescripcio() == null || filtre.getDescripcio().isEmpty(),
					filtre.getDescripcio() == null ? "" : filtre.getDescripcio(),
					(dataProgramadaDisposicioInici == null),
					dataProgramadaDisposicioInici,
					(dataProgramadaDisposicioFi == null),
					dataProgramadaDisposicioFi,
					(dataCaducitatInici == null),
					dataCaducitatInici,
					(dataCaducitatFi == null),
					dataCaducitatFi,
					(filtre.getEnviamentTipus() == null),
					(tipusEnviament),
					(filtre.getCsvUuid() == null || filtre.getCsvUuid().isEmpty()),
					filtre.getCsvUuid(),
					(filtre.getEstat() == null),
					(estat),
					entitatEntity,
					(dataEnviamentInici == null),
					dataEnviamentInici,
					(dataEnviamentFi == null),
					dataEnviamentFi,
					(filtre.getCodiNotifica() == null || filtre.getCodiNotifica().isEmpty()),
					filtre.getCodiNotifica() == null ? "" : filtre.getCodiNotifica(),
					(filtre.getCreatedBy() == null || filtre.getCreatedBy().getCodi().isEmpty()),
					conversioTipusHelper.convertir(filtre.getCreatedBy(), UsuariEntity.class),
					(filtre.getNifTitular() == null || filtre.getNifTitular().isEmpty()),
					filtre.getNifTitular() == null ? "" : filtre.getNifTitular(),
					(filtre.getTitularNomLlinatge() == null || filtre.getTitularNomLlinatge().isEmpty()),
					filtre.getTitularNomLlinatge() == null ? "" : filtre.getTitularNomLlinatge(),
					(filtre.getEmailTitular() == null || filtre.getEmailTitular().isEmpty()),
					filtre.getEmailTitular() == null ? "" : filtre.getEmailTitular(),
					(filtre.getDir3Codi() == null || filtre.getDir3Codi().isEmpty()),
					filtre.getDir3Codi() == null ? "" : filtre.getDir3Codi(),
					(filtre.getNumeroCertCorreus() == null || filtre.getNumeroCertCorreus().isEmpty()),
					filtre.getNumeroCertCorreus() == null ? "" : filtre.getNumeroCertCorreus(),
					(filtre.getUsuari() == null || filtre.getUsuari().isEmpty()),
					filtre.getUsuari() == null ? "" : filtre.getUsuari(),
					(filtre.getRegistreLlibre() == null || filtre.getRegistreLlibre().isEmpty()),
					filtre.getRegistreLlibre() == null ? "" : filtre.getRegistreLlibre(),
					(filtre.getRegistreNumero() == null || filtre.getRegistreNumero().isEmpty()),
					filtre.getRegistreNumero() == null ? "" : filtre.getRegistreNumero(),
					(dataRegistreInici == null),
					dataRegistreInici,
					(dataRegistreFi == null),
					dataRegistreFi,
							
					paginacioHelper.toSpringDataPageable(paginacioParams));
			
		if(enviament == null || !enviament.hasContent()) {
			enviament = new PageImpl<>(new ArrayList<NotificacioEnviamentEntity>());
		}
		
		for(NotificacioEnviamentEntity nee: enviament.getContent()) {
			nee.setNotificacio(notificacioRepository.findById(nee.getNotificacioId()));
		}
		
		return paginacioHelper.toPaginaDto(
				enviament,
				NotificacioEnviamentDtoV2.class);
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto exportacio(
			Long entitatId,
			Collection<Long> enviamentIds,
			String format,
			NotificacioEnviamentFiltreDto filtre) throws IOException, ParseException {
		logger.debug("Exportant informació dels enviaments (" +
				"entitatId=" + entitatId + ", " +
				"enviamentsIds=" + enviamentIds + ", " +
				"format=" + format + ")");
			Date dataEnviamentInici = null,
			 dataEnviamentFi = null,
			 dataProgramadaDisposicioInici = null,
			 dataProgramadaDisposicioFi = null,
			 dataRegistreInici = null,
			 dataRegistreFi = null,
			 dataCaducitatInici = null,
			 dataCaducitatFi = null;
		
		EntitatEntity entitatEntity = entitatRepository.findOne(entitatId);
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
		
		if (filtre.getDataEnviamentInici() != null && filtre.getDataEnviamentInici() != "") {
			dataEnviamentInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentInici());
			if (dataEnviamentInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataEnviamentInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataEnviamentInici = cal.getTime();
			}
		}
		if (filtre.getDataEnviamentFi() != null && filtre.getDataEnviamentFi() != "") {
			dataEnviamentFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataEnviamentFi());
			if (dataEnviamentFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataEnviamentFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataEnviamentFi = cal.getTime();
			}
		}
		if (filtre.getDataProgramadaDisposicioInici() != null && filtre.getDataProgramadaDisposicioInici() != "") {
			dataProgramadaDisposicioInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioInici());
			if (dataProgramadaDisposicioInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataProgramadaDisposicioInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataProgramadaDisposicioInici = cal.getTime();
			}
		}
		if (filtre.getDataProgramadaDisposicioFi() != null && filtre.getDataProgramadaDisposicioFi() != "") {
			dataProgramadaDisposicioFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataProgramadaDisposicioFi());
			if (dataProgramadaDisposicioFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataProgramadaDisposicioFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataProgramadaDisposicioFi = cal.getTime();
			}
		}
		if (filtre.getDataRegistreInici() != null && filtre.getDataRegistreInici() != "") {
			dataRegistreInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreInici());
			if (dataRegistreInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataRegistreInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataRegistreInici = cal.getTime();
			}
		}
		if (filtre.getDataRegistreFi() != null && filtre.getDataRegistreFi() != "") {
			dataRegistreFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataRegistreFi());
			if (dataRegistreFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataRegistreFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataRegistreFi = cal.getTime();
			}
		}
		if (filtre.getDataCaducitatInici() != null && filtre.getDataCaducitatInici() != "") {
			dataCaducitatInici = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatInici());
			if (dataCaducitatInici != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataCaducitatInici);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataCaducitatInici = cal.getTime();
			}
		}
		if (filtre.getDataCaducitatFi() != null && filtre.getDataCaducitatFi() != "") {
			dataCaducitatFi = new SimpleDateFormat("dd/MM/yyyy").parse(filtre.getDataCaducitatFi());
			if (dataCaducitatFi != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataCaducitatFi);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				dataCaducitatFi = cal.getTime();
			}
		}
		//Filtres camps procediment
		Integer estat = null;
		Integer tipusEnviament = null;
		if(filtre.getEstat()!=null){
			estat = filtre.getEstat().getNumVal();
		}else{
			estat = 0;
		}
		if(filtre.getEnviamentTipus()!=null){
			tipusEnviament = NotificacioTipusEnviamentEnumDto.getNumVal(filtre.getEnviamentTipus());
		}else{
			tipusEnviament = 0;
		}
		List<NotificacioEnviamentEntity> enviaments = null;
		
		entityComprovarHelper.comprovarPermisos(
				null,
				true,
				true,
				false);
			
		enviaments = notificacioEnviamentRepository.findByNotificacio(
				filtre.getCodiProcediment() == null || filtre.getCodiProcediment().isEmpty(),
				filtre.getCodiProcediment() == null ? "" : filtre.getCodiProcediment(),
				filtre.getGrup() == null || filtre.getGrup().isEmpty(),
				filtre.getGrup() == null ? "" : filtre.getGrup(),
				filtre.getConcepte() == null || filtre.getConcepte().isEmpty(),
				filtre.getConcepte() == null ? "" : filtre.getConcepte(),
				filtre.getDescripcio() == null || filtre.getDescripcio().isEmpty(),
				filtre.getDescripcio() == null ? "" : filtre.getDescripcio(),
				(dataProgramadaDisposicioInici == null),
				dataProgramadaDisposicioInici,
				(dataProgramadaDisposicioFi == null),
				dataProgramadaDisposicioFi,
				(dataCaducitatInici == null),
				dataCaducitatInici,
				(dataCaducitatFi == null),
				dataCaducitatFi,
				(filtre.getEnviamentTipus() == null),
				(tipusEnviament),
				(filtre.getCsvUuid() == null || filtre.getCsvUuid().isEmpty()),
				filtre.getCsvUuid(),
				(filtre.getEstat() == null),
				(estat),
				entitatEntity,
				(dataEnviamentInici == null),
				dataEnviamentInici,
				(dataEnviamentFi == null),
				dataEnviamentFi,
				(filtre.getCodiNotifica() == null || filtre.getCodiNotifica().isEmpty()),
				filtre.getCodiNotifica() == null ? "" : filtre.getCodiNotifica(),
				(filtre.getCreatedBy() == null || filtre.getCreatedBy().getCodi().isEmpty()),
				conversioTipusHelper.convertir(filtre.getCreatedBy(), UsuariEntity.class),
				(filtre.getNifTitular() == null || filtre.getNifTitular().isEmpty()),
				filtre.getNifTitular() == null ? "" : filtre.getNifTitular(),
				(filtre.getTitularNomLlinatge() == null || filtre.getTitularNomLlinatge().isEmpty()),
				filtre.getTitularNomLlinatge() == null ? "" : filtre.getTitularNomLlinatge(),
				(filtre.getEmailTitular() == null || filtre.getEmailTitular().isEmpty()),
				filtre.getEmailTitular() == null ? "" : filtre.getEmailTitular(),
				(filtre.getDir3Codi() == null || filtre.getDir3Codi().isEmpty()),
				filtre.getDir3Codi() == null ? "" : filtre.getDir3Codi(),
				(filtre.getNumeroCertCorreus() == null || filtre.getNumeroCertCorreus().isEmpty()),
				filtre.getNumeroCertCorreus() == null ? "" : filtre.getNumeroCertCorreus(),
				(filtre.getUsuari() == null || filtre.getUsuari().isEmpty()),
				filtre.getUsuari() == null ? "" : filtre.getUsuari(),
				(filtre.getRegistreLlibre() == null || filtre.getRegistreLlibre().isEmpty()),
				filtre.getRegistreLlibre() == null ? "" : filtre.getRegistreLlibre(),
				(filtre.getRegistreNumero() == null || filtre.getRegistreNumero().isEmpty()),
				filtre.getRegistreNumero() == null ? "" : filtre.getRegistreNumero(),
				(dataRegistreInici == null),
				dataRegistreInici,
				(dataRegistreFi == null),
				dataRegistreFi);
			
		
		
		
		
		
		
		
		
		
		for(NotificacioEnviamentEntity nee: enviaments) {
			nee.setNotificacio(notificacioRepository.findById(nee.getNotificacioId()));
		}
		
		
		
		
		
		//Genera les columnes
		int numColumnes = 22;
		String[] columnes = new String[numColumnes];
		columnes[0] = messageHelper.getMessage("enviament.service.exportacio.dataenviament");
		columnes[1] = messageHelper.getMessage("enviament.service.exportacio.dataprogramada");
		columnes[2] = messageHelper.getMessage("enviament.service.exportacio.codinotifica");
		columnes[3] = messageHelper.getMessage("enviament.service.exportacio.codiprocediment");
		columnes[4] = messageHelper.getMessage("enviament.service.exportacio.codigrup");
		columnes[5] = messageHelper.getMessage("enviament.service.exportacio.dir3codi");
		columnes[6] = messageHelper.getMessage("enviament.service.exportacio.usuari");
		columnes[7] = messageHelper.getMessage("enviament.service.exportacio.tipusenviament");
		columnes[8] = messageHelper.getMessage("enviament.service.exportacio.concepte");
		columnes[9] = messageHelper.getMessage("enviament.service.exportacio.descripcio");
		columnes[10] = messageHelper.getMessage("enviament.service.exportacio.niftitular");
		columnes[11] = messageHelper.getMessage("enviament.service.exportacio.nomLlinatgetitular");
		columnes[12] = messageHelper.getMessage("enviament.service.exportacio.emailtitular");
		columnes[13] = messageHelper.getMessage("enviament.service.exportacio.destinataris");
		columnes[14] = messageHelper.getMessage("enviament.service.exportacio.llibreregistre");
		columnes[15] = messageHelper.getMessage("enviament.service.exportacio.numeroregistre");
		columnes[16] = messageHelper.getMessage("enviament.service.exportacio.dataregistre");
		columnes[17] = messageHelper.getMessage("enviament.service.exportacio.datacaducitat");
		columnes[18] = messageHelper.getMessage("enviament.service.exportacio.codinotibenviament");
		columnes[19] = messageHelper.getMessage("enviament.service.exportacio.numerocertificatcorreus");
		columnes[20] = messageHelper.getMessage("enviament.service.exportacio.codicsvuuid");
		columnes[21] = messageHelper.getMessage("enviament.service.exportacio.estat");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		List<String[]> files = new ArrayList<String[]>();
		
		String[] fila = new String[numColumnes];
		
		
			
		for (NotificacioEnviamentEntity enviament : enviaments) {
			if(enviamentIds.contains(enviament.getId())) {
				String csvUuid = "";
				if(enviament.getNotificacio().getDocument().getCsv() != null) {
					csvUuid = enviament.getNotificacio().getDocument().getCsv();
				}
				if(enviament.getNotificacio().getDocument().getUuid() != null) {
					csvUuid = enviament.getNotificacio().getDocument().getUuid();
				}
				
				
				fila[0] = enviament.getCreatedDate().toDate() != null ? sdf.format(enviament.getCreatedDate().toDate()) : "";
				fila[1] = enviament.getNotificacio().getEnviamentDataProgramada() != null ? sdf.format(enviament.getNotificacio().getEnviamentDataProgramada()) : "";
				fila[2] = enviament.getNotificaIdentificador();
				fila[3] = enviament.getNotificacio().getProcedimentCodiNotib();
				fila[4] = enviament.getNotificacio().getGrupCodi();
				fila[5] = enviament.getNotificacio().getEmisorDir3Codi();
				fila[6] = enviament.getCreatedBy().getCodi();
				fila[7] = enviament.getNotificacio().getEnviamentTipus().getText();
				fila[8] = enviament.getNotificacio().getConcepte();
				fila[9] = enviament.getNotificacio().getDescripcio();
				fila[10] = enviament.getTitular().getNif();
				fila[11] = enviament.getTitular().getNom();
				fila[12] = enviament.getTitular().getEmail();
				fila[13] = (enviament.getDestinataris().size() > 0) ? enviament.getDestinataris().get(0).getNif() : null;
				fila[14] = enviament.getNotificacio().getRegistreLlibre();
				fila[15] = enviament.getNotificacio().getRegistreNumero();
				fila[16] = (enviament.getNotificacio().getRegistreData() != null)? enviament.getNotificacio().getRegistreData().toString() : "";
				fila[17] = enviament.getNotificacio().getCaducitat() != null ? sdf.format(enviament.getNotificacio().getCaducitat()) : "";
				fila[19] = enviament.getNotificaCertificacioNumSeguiment();
				fila[20] = csvUuid;
				fila[21] = enviament.getNotificacio().getEstat().name();	
				
				files.add(fila);	
			}
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
		} else {
			throw new ValidationException("Format de fitxer no suportat: " + format);
		}
		return fitxer;
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
	



	private List<NotificacioEnviamentDto> enviamentsToDto(
			List<NotificacioEnviamentEntity> enviaments) {
		List<NotificacioEnviamentDto> destinatarisDto = new ArrayList<NotificacioEnviamentDto>();
		for(NotificacioEnviamentEntity enviament : enviaments) {
			destinatarisDto.add(conversioTipusHelper.convertir(enviament, NotificacioEnviamentDto.class));
			
		}
		
//		List<NotificacioEnviamentDto> destinatarisDto = conversioTipusHelper.convertirList(
//				enviaments,
//				NotificacioEnviamentDto.class);
		
		for (int i = 0; i < enviaments.size(); i++) {
			NotificacioEnviamentEntity destinatariEntity = enviaments.get(i);
			NotificacioEnviamentDto destinatariDto = destinatarisDto.get(i);
			destinatariCalcularCampsAddicionals(
					destinatariEntity,
					destinatariDto);
		}
		return destinatarisDto;
	}

	@Transactional
	private NotificacioEnviamentDto enviamentToDto(
			NotificacioEnviamentEntity enviament) {
		enviament.setNotificacio(notificacioRepository.findById(enviament.getNotificacioId()));
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
	public NotificacioEnviamentDtoV2 getOne(Long entitatId) {
		return conversioTipusHelper.convertir(notificacioEnviamentRepository.findOne(entitatId), NotificacioEnviamentDtoV2.class);
	}



}
