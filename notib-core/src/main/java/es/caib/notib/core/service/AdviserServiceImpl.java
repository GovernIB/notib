package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.EnviamentAdviser;
import es.caib.notib.core.api.service.AdviserService;
import es.caib.notib.core.wsdl.adviser.AdviserWsV2PortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdviserServiceImpl implements AdviserService {

    @Autowired
    private AdviserWsV2PortType adviser;

    @Override
    public void sincronitzarEnviament(EnviamentAdviser env) {
//        adviser.sincronizarEnvio();
    }
}
