package es.caib.notib.ejb;

import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
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
    public void generarDadesExplotacio(LocalDate data) {
        getDelegateService().generarDadesExplotacio(data);
    }

    @Override
    public RegistresEstadistics consultaUltimesEstadistiques() {
        return getDelegateService().consultaUltimesEstadistiques();
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
