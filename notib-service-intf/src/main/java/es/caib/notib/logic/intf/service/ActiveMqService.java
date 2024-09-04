package es.caib.notib.logic.intf.service;

import java.util.List;

public interface ActiveMqService {

    public List<String> getQueues() throws Exception;
    public void createQueue(String queueName) throws Exception;
    public void deleteQueue(String queueName) throws Exception;
    public String getQueueDetails(String queueName) throws Exception;
    public void compactKahaDB() throws Exception;

}
