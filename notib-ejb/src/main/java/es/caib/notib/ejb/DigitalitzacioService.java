package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioPerfil;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioResultat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioTransaccioResposta;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

@Primary
@Stateless
public class DigitalitzacioService extends AbstractService<es.caib.notib.logic.intf.service.DigitalitzacioService> implements es.caib.notib.logic.intf.service.DigitalitzacioService {

    @Override
    @RolesAllowed("**")
    public List<DigitalitzacioPerfil> getPerfilsDisponibles() {
        return getDelegateService().getPerfilsDisponibles();
    }

    @Override
    @RolesAllowed("**")
    public DigitalitzacioTransaccioResposta iniciarDigitalitzacio(String codiPerfil, String urlReturn) {
        return getDelegateService().iniciarDigitalitzacio(codiPerfil, urlReturn);
    }

    @Override
    @RolesAllowed("**")
    public DigitalitzacioResultat recuperarResultat(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile) {
        return getDelegateService().recuperarResultat(idTransaccio, returnScannedFile, returnSignedFile);
    }

    @Override
    @RolesAllowed("**")
    public void tancarTransaccio(String idTransaccio) {
        getDelegateService().tancarTransaccio(idTransaccio);
    }
}
