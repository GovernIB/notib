package es.caib.notib.core.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregation;
import es.caib.notib.core.api.dto.historic.HistoricAggregationEstatDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationGrupDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationOrganDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationProcedimentDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationUsuariDto;
import es.caib.notib.core.api.dto.historic.HistoricFiltreDto;
import es.caib.notib.core.api.service.HistoricService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.helper.ConversioTipusHelper;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.historic.HistoricGenerator;
import es.caib.notib.core.repository.GrupRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.repository.UsuariRepository;
import es.caib.notib.core.repository.historic.HistoricNotificacioRepository;

@Service
public class HistoricServiceImpl implements HistoricService {

	@Resource
	private HistoricNotificacioRepository historicNotificacioRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private ProcedimentRepository procedimentRepository;
	@Resource
	private GrupRepository grupRepository;
	@Resource
	private HistoricGenerator historicGenerator;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private UsuariRepository usuariRepository;
	
	@Override
	public Map<OrganGestorDto, List<HistoricAggregationOrganDto>> getHistoricsByOrganGestor(
			Long entitatId,
			HistoricFiltreDto filtre) {
//		historicGenerator.fillData(entitatId);
		List<OrganGestorEntity> organGestors;
		if (filtre.emptyOrgansGestors()) {
			organGestors = organGestorRepository.findByEntitatId(entitatId);
		} else {
			organGestors = organGestorRepository.findByCodiIn(filtre.getOrganGestorsCodis());
		}
		Map<OrganGestorDto, List<HistoricAggregationOrganDto>> response = new HashMap<OrganGestorDto, List<HistoricAggregationOrganDto>>();
		for (OrganGestorEntity organ : organGestors) {
			List<HistoricAggregationOrganDto> historics = historicNotificacioRepository.findByOrganGestorAndDateRangeGroupedBydDate(
					organ,
					filtre.getTipusAgrupament(),
					filtre.getDataInici(),
					filtre.getDataFi());
			historics = fillEmptyData(filtre, historics, HistoricAggregationOrganDto.class);
			response.put(conversioTipusHelper.convertir(organ, OrganGestorDto.class), historics);
		}

		return response;
	}

	@Override
	public Map<ProcedimentDto, List<HistoricAggregationProcedimentDto>> getHistoricsByProcediment(
			HistoricFiltreDto filtre) {
		if (filtre.emptyProcediments()) {
			return new HashMap<ProcedimentDto, List<HistoricAggregationProcedimentDto>>();
		}
		List<ProcedimentEntity> procediments = procedimentRepository.findByIdIn(filtre.getProcedimentsIds());

		Map<ProcedimentDto, List<HistoricAggregationProcedimentDto>> response = new HashMap<ProcedimentDto, List<HistoricAggregationProcedimentDto>>();
		for (ProcedimentEntity procediment : procediments) {
			List<HistoricAggregationProcedimentDto> historics = historicNotificacioRepository.findByProcedimentAndDateRangeGroupedBydDate(
					procediment,
					filtre.getTipusAgrupament(),
					filtre.getDataInici(),
					filtre.getDataFi());
			historics = fillEmptyData(filtre, historics, HistoricAggregationProcedimentDto.class);
			response.put(conversioTipusHelper.convertir(procediment, ProcedimentDto.class), historics);
		}

		return response;
	}

	@Override
	public Map<NotificacioEstatEnumDto, List<HistoricAggregationEstatDto>> getHistoricsByEstat(
			HistoricFiltreDto filtre) {
		Map<NotificacioEstatEnumDto, List<HistoricAggregationEstatDto>> response = new HashMap<NotificacioEstatEnumDto, List<HistoricAggregationEstatDto>>();
		for (NotificacioEstatEnumDto estat : NotificacioEstatEnumDto.values()) {
			List<HistoricAggregationEstatDto> historics = historicNotificacioRepository.findByEstatAndDateRangeGroupedByDate(
					estat,
					filtre.getTipusAgrupament(),
					filtre.getDataInici(),
					filtre.getDataFi());
			historics = fillEmptyData(filtre, historics, HistoricAggregationEstatDto.class);
			response.put(estat, historics);
		}

		return response;
	}

	@Transactional
	@Override
	public Map<GrupDto, List<HistoricAggregationGrupDto>> getHistoricsByGrup(Long entitatId, HistoricFiltreDto filtre) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);
		List<GrupDto> grups;
		if (filtre.emptyOrgansGestors()) {
			grups = conversioTipusHelper.convertirList(
					grupRepository.findByEntitat(entitat), 
					GrupDto.class);
		} else {
			grups = conversioTipusHelper.convertirList(
					grupRepository.findByEntitatIdAndOrganGestorCodiIn(entitatId, filtre.getOrganGestorsCodis()), 
					GrupDto.class);
		}
		Map<GrupDto, List<HistoricAggregationGrupDto>> response = new HashMap<GrupDto, List<HistoricAggregationGrupDto>>();
		for (GrupDto grup : grups) {
			List<HistoricAggregationGrupDto> historics = historicNotificacioRepository.findByGrupAndDateRangeGroupedByDate(
					grup.getCodi(),
					filtre.getTipusAgrupament(),
					filtre.getDataInici(),
					filtre.getDataFi());
			historics = fillEmptyData(filtre, historics, HistoricAggregationGrupDto.class);
			response.put(grup, historics);
		}

		return response;
	}

	@Override
	public Map<UsuariDto, List<HistoricAggregationUsuariDto>> getHistoricsByUsuariAplicacio(HistoricFiltreDto filtre, List<String> usersCodis) {
		Map<UsuariDto, List<HistoricAggregationUsuariDto>> response = new HashMap<UsuariDto, List<HistoricAggregationUsuariDto>>();
		if (usersCodis == null || usersCodis.isEmpty()) {
			return response;
		}
		
		for (String codi : usersCodis) {
			UsuariDto usuari = conversioTipusHelper.convertir(usuariRepository.findByCodi(codi), UsuariDto.class);
			List<HistoricAggregationUsuariDto> historics = historicNotificacioRepository.findByUsuariAndDateRangeGroupedByDate(
					codi,
					filtre.getTipusAgrupament(),
					filtre.getDataInici(),
					filtre.getDataFi());
			historics = fillEmptyData(filtre, historics, HistoricAggregationUsuariDto.class);
			response.put(usuari, historics);
		}

		return response;
	}

	@Override
	public List<HistoricAggregationOrganDto> getDadesActualsByOrgansGestor(HistoricFiltreDto filtre) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HistoricAggregationProcedimentDto> getDadesActualsByProcediment(HistoricFiltreDto filtre) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HistoricAggregationEstatDto> getDadesActualsByEstat(HistoricFiltreDto filtre) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HistoricAggregationGrupDto> getDadesActualsByGrup(HistoricFiltreDto filtre) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HistoricAggregationUsuariDto> getDadesActualsByUsuariAplicacio(HistoricFiltreDto filtre) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param <T>
	 * @param filtre
	 * @param historics Llista d'històrics, ha d'estar ordenada descendentment per
	 *                  data
	 * @return
	 */
	private <T extends HistoricAggregation> List<T> fillEmptyData(HistoricFiltreDto filtre, List<T> historics, Class<T> cls) {
		List<Date> dates = filtre.getQueriedDates();
		Iterator<T> it = historics.iterator();
		T currentHistoric = null;
		if (it.hasNext())
			currentHistoric = it.next();
		else
			currentHistoric = emptyInstance(dates.get(0), cls);
		List<T> results = new ArrayList<T>();
		for (Date data : dates) {
			T dateHistoric = null;
			if (data.compareTo(currentHistoric.getData()) < 0) { // anterior que l'actual
				dateHistoric = emptyInstance(data, cls);

			} else if (data.compareTo(currentHistoric.getData()) == 0) { // igual que l'actual
				dateHistoric = currentHistoric;
				if (it.hasNext())
					currentHistoric = it.next();

			} else { // major que l'actual
				dateHistoric = emptyInstance(data, cls);
				
			}
			results.add(dateHistoric);
		}
		return results;
	}

	private <T extends HistoricAggregation> T emptyInstance(Date date, Class<T> cls) {
		try {
			return cls.getConstructor(Date.class).newInstance(date);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
				InvocationTargetException | NoSuchMethodException | SecurityException e) {
			return null;
		}
	}
}
