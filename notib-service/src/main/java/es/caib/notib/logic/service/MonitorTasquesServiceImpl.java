package es.caib.notib.logic.service;

import es.caib.notib.logic.intf.monitor.MonitorTascaEstat;
import es.caib.notib.logic.intf.monitor.MonitorTascaInfo;
import es.caib.notib.logic.intf.service.MonitorTasquesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Implementació dels mètodes per a gestionar el monitor de tasques.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */

@Slf4j
@Service
public class MonitorTasquesServiceImpl implements MonitorTasquesService {

	private static Map<String, MonitorTascaInfo> tasques = new HashMap<>();

	@Override
	public MonitorTascaInfo addTasca(String codiTasca) {

		MonitorTascaInfo monitorTascaInfo = new MonitorTascaInfo();
		monitorTascaInfo.setCodi(codiTasca);
		monitorTascaInfo.setEstat(MonitorTascaEstat.EN_ESPERA);
		MonitorTasquesServiceImpl.tasques.put(codiTasca, monitorTascaInfo);
		return monitorTascaInfo;
	}

	@Override
	public void updateTasca(String codiTasca, MonitorTascaEstat estat, Date inici, Date fi, Date properaExecucio, String observacions) {
		log.info("Actualitzant la tasca " + codiTasca);
	}

	private void updateEstat(String codi, MonitorTascaEstat estat) {

		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codi);
		monitorTascaInfo.setEstat(estat);
		MonitorTasquesServiceImpl.tasques.put(codi, monitorTascaInfo);

	}

	private void updateDataInici(String codi) {

		Date dataInici = updateData(0L);
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codi);
		monitorTascaInfo.setDataInici(dataInici);
		MonitorTasquesServiceImpl.tasques.put(codi, monitorTascaInfo);
	}

	private void updateDataFi(String codi, boolean iniciant) {

		Date dataFi = updateData(0L);
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codi);
		monitorTascaInfo.setDataFi(iniciant ? null : dataFi);
		MonitorTasquesServiceImpl.tasques.put(codi, monitorTascaInfo);
	}

	@Override
	public void updateProperaExecucio(String codi, Long plusValue) {

		Date dataProperaExecucio = updateData(plusValue);
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codi);
		monitorTascaInfo.setProperaExecucio(dataProperaExecucio);
		MonitorTasquesServiceImpl.tasques.put(codi, monitorTascaInfo);
	}

	private Date updateData(Long plusValue) {
		return plusValue != null ? new Date(System.currentTimeMillis() + plusValue) : null;
	}

	@Override
	public List<MonitorTascaInfo> findAll() {

		List<MonitorTascaInfo> monitorTasques = new ArrayList<>();
		for(Map.Entry<String, MonitorTascaInfo> tasca : MonitorTasquesServiceImpl.tasques.entrySet()) {
			monitorTasques.add(tasca.getValue());
		}
		return monitorTasques;
	}

	@Override
	public MonitorTascaInfo findByCodi(String codi) {
		return MonitorTasquesServiceImpl.tasques.get(codi);
	}

	@Override
	public void inici(String codiTasca) {

    	updateDataInici(codiTasca);
    	updateDataFi(codiTasca, true);
    	updateEstat(codiTasca, MonitorTascaEstat.EN_EXECUCIO);
    	updateProperaExecucio(codiTasca, null);
	}

	@Override
	public void fi(String codiTasca) {

		updateEstat(codiTasca, MonitorTascaEstat.EN_ESPERA);
		updateDataFi(codiTasca, false);
	}

	@Override
	public void error(String codiTasca) {

		updateEstat(codiTasca, MonitorTascaEstat.ERROR);
		updateDataFi(codiTasca, false);
	}
}
