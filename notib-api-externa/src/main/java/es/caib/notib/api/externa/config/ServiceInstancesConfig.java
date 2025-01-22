package es.caib.notib.api.externa.config;

import es.caib.notib.logic.intf.service.CieAdviserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServiceInstancesConfig {

    @Autowired
    private CieAdviserService cieAdviserService;


    private static CieAdviserService cieAdviserServiceInstance;

    public static CieAdviserService getCieAdviserServiceInstance() {
        return cieAdviserServiceInstance;
    }


    @PostConstruct
    private void postConstruct() {
        cieAdviserServiceInstance = cieAdviserService;
    }

}
