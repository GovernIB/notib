package es.caib.notib.logic.intf.service;


import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;

import java.time.LocalDate;
import java.util.List;

public interface EstadisticaService {

    boolean generarDadesExplotacio();
    boolean generarDadesExplotacio(LocalDate data);
    void generarDadesExplotacio(LocalDate data, LocalDate toDate);
    void generarDadesExplotacioBasiques(LocalDate startDate, LocalDate endDate, boolean regenerar);

    RegistresEstadistics consultaUltimesEstadistiques();
    RegistresEstadistics consultaEstadistiques(LocalDate data);
    List<RegistresEstadistics> consultaEstadistiques(LocalDate dataInici, LocalDate dataFi);

    List<DimensioDesc> getDimensions();
    List<IndicadorDesc> getIndicadors();

}
