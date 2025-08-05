package es.caib.notib.logic.intf.service;


import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;

import java.time.LocalDate;
import java.util.List;

public interface EstadisticaService {

    public void generarDadesExplotacio();
    public void generarDadesExplotacio(LocalDate data);
    public void generarDadesExplotacio(LocalDate data, LocalDate toDate);
    public void generarDadesExplotacioBasiques(LocalDate fromDate, LocalDate toDate);

    public RegistresEstadistics consultaUltimesEstadistiques();
    public RegistresEstadistics consultaEstadistiques(LocalDate data);
    public List<RegistresEstadistics> consultaEstadistiques(LocalDate dataInici, LocalDate dataFi);

    public List<DimensioDesc> getDimensions();
    public List<IndicadorDesc> getIndicadors();
}
