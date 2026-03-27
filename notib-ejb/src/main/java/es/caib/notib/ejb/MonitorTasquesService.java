package es.caib.notib.ejb;

import es.caib.notib.logic.intf.monitor.MonitorTascaEstat;
import es.caib.notib.logic.intf.monitor.MonitorTascaInfo;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

@Primary
@Stateless
public class MonitorTasquesService extends AbstractService<es.caib.notib.logic.intf.service.MonitorTasquesService> implements es.caib.notib.logic.intf.service.MonitorTasquesService {

    @Override
    @PermitAll
    public MonitorTascaInfo addTasca(String codiTasca) {
        return getDelegateService().addTasca(codiTasca);
    }

    @Override
    @PermitAll
    public void updateTasca(String codiTasca, MonitorTascaEstat estat, Date inici, Date fi, Date properaExecucio, String observacions) {
        getDelegateService().updateTasca(codiTasca, estat, inici, fi, properaExecucio, observacions);
    }

    @Override
    @PermitAll
    public List<MonitorTascaInfo> findAll() {
        return getDelegateService().findAll();
    }

    @Override
    @PermitAll
    public MonitorTascaInfo findByCodi(String codi) {
        return getDelegateService().findByCodi(codi);
    }

    @Override
    @PermitAll
    public void updateProperaExecucio(String codi, Long plusValue) {
        getDelegateService().updateProperaExecucio(codi, plusValue);
    }

    @Override
    @PermitAll
    public void inici(String codiTasca) {
        getDelegateService().inici(codiTasca);
    }

    @Override
    @PermitAll
    public void fi(String codiTasca) {
        getDelegateService().fi(codiTasca);
    }

    @Override
    @PermitAll
    public void error(String codiTasca) {
        getDelegateService().error(codiTasca);
    }

}
