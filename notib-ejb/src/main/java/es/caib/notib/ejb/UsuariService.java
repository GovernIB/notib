package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.UsuariDto;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Primary
@Stateless
public class UsuariService extends AbstractService<es.caib.notib.logic.intf.service.UsuariService> implements es.caib.notib.logic.intf.service.UsuariService {

    @RolesAllowed({"NOT_SUPER"})
    @Override
    public UsuariDto findByCodi(String codi) {
        return getDelegateService().findByCodi(codi);
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    @TransactionTimeout(value = 1200)
    public void updateUsuariCodi(String codiAntic, String codiNou) {
        getDelegateService().updateUsuariCodi(codiAntic, codiNou);
    }
}
