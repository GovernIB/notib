package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.logs.FitxerContingut;
import es.caib.notib.logic.intf.dto.logs.FitxerInfo;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Primary
@Stateless
public class LogService extends AbstractService<es.caib.notib.logic.intf.service.LogService> implements es.caib.notib.logic.intf.service.LogService {

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public List<FitxerInfo> llistarFitxers() {
        return getDelegateService().llistarFitxers();
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public FitxerContingut getFitxerByNom(String nom) {
        return getDelegateService().getFitxerByNom(nom);
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public void tailLogFile(String filePath) {
        getDelegateService().tailLogFile(filePath);
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public BlockingQueue<String> getQueue() {
        return getDelegateService().getQueue();
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public List<String> readLastNLines(String nomFitxer, Long nLinies) {
        return getDelegateService().readLastNLines(nomFitxer, nLinies);
    }
}
