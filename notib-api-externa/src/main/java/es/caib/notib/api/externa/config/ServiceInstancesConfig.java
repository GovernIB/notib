package es.caib.notib.api.externa.config;

import es.caib.notib.logic.intf.service.NexeaAdviserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServiceInstancesConfig {

    @Autowired
    private NexeaAdviserService nexeaAdviserService;


    private static NexeaAdviserService nexeaAdviserServiceInstance;

    public static NexeaAdviserService getNexeaAdviserServiceInstance() {
        return nexeaAdviserServiceInstance;
    }


    @PostConstruct
    private void postConstruct() {
        nexeaAdviserServiceInstance = nexeaAdviserService;
    }

}
