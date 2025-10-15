package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.logs.FitxerContingut;
import es.caib.notib.logic.intf.dto.logs.FitxerInfo;

import java.util.List;

public interface LogService {

    List<FitxerInfo> llistarFitxers();

    FitxerContingut getFitxerByNom(String nom);
}
