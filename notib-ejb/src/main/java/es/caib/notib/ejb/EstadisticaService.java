package es.caib.notib.ejb;

import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.time.LocalDate;
import java.util.List;

@Primary
@Stateless
public class EstadisticaService extends AbstractService<es.caib.notib.logic.intf.service.EstadisticaService> implements es.caib.notib.logic.intf.service.EstadisticaService {

    @Override
    @RolesAllowed({"NOT_COM"})
    public boolean generarDadesExplotacio() {
        return getDelegateService().generarDadesExplotacio();
    }

    @Override
    @TransactionTimeout(value = 3600)
    @RolesAllowed({"NOT_COM"})
    public boolean generarDadesExplotacio(LocalDate data) {
        return getDelegateService().generarDadesExplotacio(data);
    }

    @Override
    @TransactionTimeout(value = 3600)
    @RolesAllowed({"NOT_COM"})
    public void generarDadesExplotacio(LocalDate data, LocalDate toDate) {
        getDelegateService().generarDadesExplotacio(data, toDate);
    }

    @Override
    @TransactionTimeout(value = 3600)
    @RolesAllowed({"NOT_COM"})
    public void generarDadesExplotacioBasiques(LocalDate fromDate, LocalDate toDate, boolean regenerar) {
        getDelegateService().generarDadesExplotacioBasiques(fromDate, toDate, regenerar);
    }

    @Override
    @RolesAllowed({"NOT_COM"})
    public RegistresEstadistics consultaUltimesEstadistiques() {
        return getDelegateService().consultaUltimesEstadistiques();
    }

    @Override
    @RolesAllowed({"NOT_COM"})
    public RegistresEstadistics consultaEstadistiques(LocalDate data) {
        return getDelegateService().consultaEstadistiques(data);
    }

    @Override
    @RolesAllowed({"NOT_COM"})
    public List<RegistresEstadistics> consultaEstadistiques(LocalDate dataInici, LocalDate dataFi) {
        return getDelegateService().consultaEstadistiques(dataInici, dataFi);
    }

    @Override
    @RolesAllowed({"NOT_COM"})
    public List<DimensioDesc> getDimensions() {
        return getDelegateService().getDimensions();
    }

    @Override
    @RolesAllowed({"NOT_COM"})
    public List<IndicadorDesc> getIndicadors() {
        return getDelegateService().getIndicadors();
    }
}
