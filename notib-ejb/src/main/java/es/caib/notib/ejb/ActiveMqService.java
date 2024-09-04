package es.caib.notib.ejb;

import org.springframework.context.annotation.Primary;

import javax.ejb.Stateless;
import java.util.List;

@Primary
@Stateless
public class ActiveMqService extends AbstractService<es.caib.notib.logic.intf.service.ActiveMqService> implements es.caib.notib.logic.intf.service.ActiveMqService {

    @Override
    public List<String> getQueues() throws Exception {
        return getDelegateService().getQueues();
    }

    @Override
    public void createQueue(String queueName) throws Exception {
        getDelegateService().createQueue(queueName);
    }

    @Override
    public void deleteQueue(String queueName) throws Exception {
        getDelegateService().deleteQueue(queueName);
    }

    @Override
    public String getQueueDetails(String queueName) throws Exception {
        return getDelegateService().getQueueDetails(queueName);
    }

    @Override
    public void compactKahaDB() throws Exception {
        getDelegateService().compactKahaDB();
    }

}
