package es.caib.notib.logic.intf.service;


import es.caib.notib.logic.intf.monitor.MonitorTascaEstat;
import es.caib.notib.logic.intf.monitor.MonitorTascaInfo;

import java.util.Date;
import java.util.List;

/**
 * Declaració dels mètodes per a la gestió del item monitorIntegracio
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MonitorTasquesService {
	

	 MonitorTascaInfo addTasca(String codiTasca);

	 void updateTasca(String codiTasca, MonitorTascaEstat estat, Date inici, Date fi, Date properaExecucio, String observacions);

	 void updateProperaExecucio(String codi, Long plusValue);

	 List<MonitorTascaInfo> findAll();

	 MonitorTascaInfo findByCodi(String codi);

	 void inici(String codiTasca);

	 void fi(String codiTasca);

	 void error(String codiTasca);
}
