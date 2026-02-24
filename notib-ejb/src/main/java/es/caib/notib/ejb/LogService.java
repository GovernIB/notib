package es.caib.notib.ejb;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.comanda.ms.log.helper.LogFileStream;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Primary
@Stateless
public class LogService extends AbstractService<es.caib.notib.logic.intf.service.LogService> implements es.caib.notib.logic.intf.service.LogService {

    @Override
    @RolesAllowed({"NOT_COM"})
    public List<FitxerInfo> llistarFitxers() {
        return getDelegateService().llistarFitxers();
    }

    @Override
    @RolesAllowed({"NOT_COM"})
    public FitxerContingut getFitxerByNom(String nom) {
        return getDelegateService().getFitxerByNom(nom);
    }

    @Override
    @RolesAllowed({"NOT_COM"})
    public LogFileStream descarregarFitxerDirecte(String nom) {
        return getDelegateService().descarregarFitxerDirecte(nom);
    }

    @Override
    @RolesAllowed({"NOT_COM"})
    public void tailLogFile(String filePath) {
        getDelegateService().tailLogFile(filePath);
    }

    @Override
    @RolesAllowed({"NOT_COM"})
    public BlockingQueue<String> getQueue() {
        return getDelegateService().getQueue();
    }

    @Override
    @RolesAllowed({"NOT_COM"})
    public List<String> readLastNLines(String nomFitxer, Long nLinies) {
        return getDelegateService().readLastNLines(nomFitxer, nLinies);
    }
}
