package es.caib.notib.ejb;

import es.caib.notib.logic.intf.monitor.MonitorTascaEstat;
import es.caib.notib.logic.intf.monitor.MonitorTascaInfo;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

@Stateless
public class MonitorTasquesService extends AbstractService<es.caib.notib.logic.intf.service.MonitorTasquesService> implements es.caib.notib.logic.intf.service.MonitorTasquesService {

    @Override
    public MonitorTascaInfo addTasca(String codiTasca) {
        return getDelegateService().addTasca(codiTasca);
    }

    @Override
    public void updateTasca(String codiTasca, MonitorTascaEstat estat, Date inici, Date fi, Date properaExecucio, String observacions) {
        getDelegateService().updateTasca(codiTasca, estat, inici, fi, properaExecucio, observacions);
    }

    @Override
    public List<MonitorTascaInfo> findAll() {
        return getDelegateService().findAll();
    }

    @Override
    public MonitorTascaInfo findByCodi(String codi) {
        return getDelegateService().findByCodi(codi);
    }

    @Override
    public void updateProperaExecucio(String codi, Long plusValue) {
        getDelegateService().updateProperaExecucio(codi, plusValue);
    }

    @Override
    public void inici(String codiTasca) {
        getDelegateService().inici(codiTasca);
    }

    @Override
    public void fi(String codiTasca) {
        getDelegateService().fi(codiTasca);
    }

    @Override
    public void error(String codiTasca) {
        getDelegateService().error(codiTasca);
    }
}
