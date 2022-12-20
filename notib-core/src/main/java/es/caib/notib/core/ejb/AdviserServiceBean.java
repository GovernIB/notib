package es.caib.notib.core.ejb;

import es.caib.notib.core.api.dto.EnviamentAdviser;
import es.caib.notib.core.api.service.AdviserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AdviserServiceBean implements AdviserService {

    @Autowired
    AdviserService delegate;

    @Override
    public void sincronitzarEnviament(EnviamentAdviser env) {
        delegate.sincronitzarEnviament(env);
    }
}
