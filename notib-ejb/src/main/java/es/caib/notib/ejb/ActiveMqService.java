package es.caib.notib.ejb;

import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

@Primary
@Stateless
public class ActiveMqService extends AbstractService<es.caib.notib.logic.intf.service.ActiveMqService> implements es.caib.notib.logic.intf.service.ActiveMqService {

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public List<String> getQueues() throws Exception {
        return getDelegateService().getQueues();
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public void createQueue(String queueName) throws Exception {
        getDelegateService().createQueue(queueName);
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public void deleteQueue(String queueName) throws Exception {
        getDelegateService().deleteQueue(queueName);
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public String getQueueDetails(String queueName) throws Exception {
        return getDelegateService().getQueueDetails(queueName);
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public void compactKahaDB() throws Exception {
        getDelegateService().compactKahaDB();
    }

}
