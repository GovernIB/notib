package es.caib.notib.ejb;

import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.annotation.Primary;

import javax.ejb.Stateless;
import java.time.LocalDate;
import java.util.List;

@Primary
@Stateless
public class EstadisticaService extends AbstractService<es.caib.notib.logic.intf.service.EstadisticaService> implements es.caib.notib.logic.intf.service.EstadisticaService {

    @Override
    public void generarDadesExplotacio() {
        getDelegateService().generarDadesExplotacio();
    }

    @Override
    @TransactionTimeout(value = 3600)
    public void generarDadesExplotacio(LocalDate data) {
        getDelegateService().generarDadesExplotacio(data);
    }

    @Override
    @TransactionTimeout(value = 3600)
    public void generarDadesExplotacioBasiques(LocalDate fromDate, LocalDate toDate) {
        getDelegateService().generarDadesExplotacioBasiques(fromDate, toDate);
    }

    @Override
    public RegistresEstadistics consultaUltimesEstadistiques() {
        return getDelegateService().consultaUltimesEstadistiques();
    }

    @Override
    public RegistresEstadistics consultaEstadistiques(LocalDate data) {
        return getDelegateService().consultaEstadistiques(data);
    }

    @Override
    public List<DimensioDesc> getDimensions() {
        return getDelegateService().getDimensions();
    }

    @Override
    public List<IndicadorDesc> getIndicadors() {
        return getDelegateService().getIndicadors();
    }
}
