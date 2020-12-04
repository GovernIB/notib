package es.caib.notib.core.helper.historic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.historic.HistoricNotificacioEntity;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.historic.HistoricNotificacioRepository;

@Component
public class HistoricGenerator {

	@Autowired
	private HistoricNotificacioRepository historicNotificacioRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;

	@Transactional
	public void fillData(Long entitatId) {
		for (int i = 0; i <= 30 * 12 * 2; i++) {
			LocalDate date = (new LocalDate()).minusDays(i);
			Date currentDateIni = date.toDateTimeAtStartOfDay().toDate();
			Date currentDateEnd = date.toDateTimeAtStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(
					59).plusMillis(999).toDate();

			List<HistoricNotificacioEntity> historics = computeData(
					entitatId,
					currentDateIni,
					currentDateEnd,
					HistoricTipusEnumDto.DIARI);
			historicNotificacioRepository.save(historics);
		}

	}

	private List<HistoricNotificacioEntity> computeData(
			Long entitatId,
			Date currentDateIni,
			Date currentDateEnd,
			HistoricTipusEnumDto tipus) {
		List<NotificacioEntity> notificacions = notificacioRepository.findBetweenCreatedDate(
				entitatId,
				currentDateIni,
				currentDateEnd);
		List<HistoricNotificacioEntity> historics = new ArrayList<HistoricNotificacioEntity>();
		for (NotificacioEntity not : notificacions) {
			HistoricNotificacioEntity historic = historicNotificacioRepository.findByDataAndProcedimentAndUsuariCodiAndGrupCodiAndEstat(
					currentDateIni,
					not.getProcediment(),
					not.getUsuariCodi(),
					not.getGrupCodi(),
					not.getEstat());
			if (historic == null) {
				historic = new HistoricNotificacioEntity(currentDateIni, tipus, not);
			}

			if (not.getEnviamentTipus() == NotificaEnviamentTipusEnumDto.COMUNICACIO) {
				historic.setNumComTotal(historic.getNumComTotal() + 1);

			} else {
				historic.setNumNotTotal(historic.getNumNotTotal() + 1);
			}
			historics.add(historic);
		}
		return historics;
	}

}
