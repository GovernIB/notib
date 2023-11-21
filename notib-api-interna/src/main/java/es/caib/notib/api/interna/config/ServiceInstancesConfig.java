package es.caib.notib.api.interna.config;

import es.caib.notib.logic.intf.service.AdviserServiceWs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServiceInstancesConfig {

    @Autowired
    private AdviserServiceWs adviserServiceWs;


    private static AdviserServiceWs adviserServiceInstance;

    public static AdviserServiceWs getAdviserServiceInstance() {
        return adviserServiceInstance;
    }




    @PostConstruct
    private void postConstruct() {
        adviserServiceInstance = adviserServiceWs;
    }

}
