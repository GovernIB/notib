package es.caib.notib.logic.intf.service;


import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;

public interface EstadisticaService {

    @PreAuthorize("hasRole('NOT_COM')")
    boolean generarDadesExplotacio();
    @PreAuthorize("hasRole('NOT_COM')")
    boolean generarDadesExplotacio(LocalDate data);
    @PreAuthorize("hasRole('NOT_COM')")
    void generarDadesExplotacio(LocalDate data, LocalDate toDate);
    @PreAuthorize("hasRole('NOT_COM')")
    void generarDadesExplotacioBasiques(LocalDate startDate, LocalDate endDate, boolean regenerar);
    @PreAuthorize("hasRole('NOT_COM')")
    RegistresEstadistics consultaUltimesEstadistiques();
    @PreAuthorize("hasRole('NOT_COM')")
    RegistresEstadistics consultaEstadistiques(LocalDate data);
    @PreAuthorize("hasRole('NOT_COM')")
    List<RegistresEstadistics> consultaEstadistiques(LocalDate dataInici, LocalDate dataFi);
    @PreAuthorize("hasRole('NOT_COM')")
    List<DimensioDesc> getDimensions();
    @PreAuthorize("hasRole('NOT_COM')")
    List<IndicadorDesc> getIndicadors();

}
