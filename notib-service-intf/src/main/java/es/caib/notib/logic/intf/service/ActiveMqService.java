package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.ActiveMqInfo;
import es.caib.notib.logic.intf.dto.ActiveMqMissatgeInfo;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.management.MalformedObjectNameException;
import javax.management.openmbean.OpenDataException;
import java.util.List;

public interface ActiveMqService {


    @PreAuthorize("hasRole('NOT_SUPER')")
    PaginaDto<ActiveMqInfo> getInfoQueues(PaginacioParamsDto paginacioParams);
    @PreAuthorize("hasRole('NOT_SUPER')")
    List<ActiveMqMissatgeInfo> getMessages(String queueNom);
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
