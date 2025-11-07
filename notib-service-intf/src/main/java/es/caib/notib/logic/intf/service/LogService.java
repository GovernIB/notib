package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.logs.FitxerContingut;
import es.caib.notib.logic.intf.dto.logs.FitxerInfo;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface LogService {

    @PreAuthorize("hasRole('NOT_COM')")
    List<FitxerInfo> llistarFitxers();

    @PreAuthorize("hasRole('NOT_COM')")
    FitxerContingut getFitxerByNom(String nom);

    @PreAuthorize("hasRole('NOT_COM')")
    void tailLogFile(String filePath);

    @PreAuthorize("hasRole('NOT_COM')")
    BlockingQueue<String> getQueue();

    @PreAuthorize("hasRole('NOT_COM')")
    List<String> readLastNLines(String nomFitxer, Long nLinies);
}
