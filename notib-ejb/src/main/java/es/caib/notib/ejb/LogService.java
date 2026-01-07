package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.logs.FitxerContingut;
import es.caib.notib.logic.intf.dto.logs.FitxerInfo;
import org.springframework.context.annotation.Primary;

import javax.ejb.Stateless;
import java.util.List;

@Primary
@Stateless
public class LogService extends AbstractService<es.caib.notib.logic.intf.service.LogService> implements es.caib.notib.logic.intf.service.LogService {

    @Override
    public List<FitxerInfo> llistarFitxers() {
        return getDelegateService().llistarFitxers();
    }

    @Override
    public FitxerContingut getFitxerByNom(String nom) {
        return getDelegateService().getFitxerByNom(nom);
    }
}
