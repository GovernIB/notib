package es.caib.notib.logic.intf.service;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ActiveMqService {

    @PreAuthorize("hasRole('NOT_SUPER')")
    List<String> getQueues() throws Exception;
    @PreAuthorize("hasRole('NOT_SUPER')")
    void createQueue(String queueName) throws Exception;
    @PreAuthorize("hasRole('NOT_SUPER')")
    void deleteQueue(String queueName) throws Exception;
    @PreAuthorize("hasRole('NOT_SUPER')")
    String getQueueDetails(String queueName) throws Exception;
    @PreAuthorize("hasRole('NOT_SUPER')")
    void compactKahaDB() throws Exception;

}
