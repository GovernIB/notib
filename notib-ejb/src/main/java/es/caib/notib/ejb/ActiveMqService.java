package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.ActiveMqInfo;
import es.caib.notib.logic.intf.dto.ActiveMqMissatgeInfo;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import org.springframework.context.annotation.Primary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.management.MalformedObjectNameException;
import javax.management.openmbean.OpenDataException;
import java.util.List;

@Primary
@Stateless
public class ActiveMqService extends AbstractService<es.caib.notib.logic.intf.service.ActiveMqService> implements es.caib.notib.logic.intf.service.ActiveMqService {

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public PaginaDto<ActiveMqInfo> getInfoQueues(PaginacioParamsDto paginacioParams) {
        return getDelegateService().getInfoQueues(paginacioParams);
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public List<ActiveMqMissatgeInfo> getMessages(String queueNom) {
        return getDelegateService().getMessages(queueNom);
    }

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

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public boolean deleteMessage(String queueName, String messageId) {
        return getDelegateService().deleteMessage(queueName, messageId);
    }

    @Override
    @RolesAllowed({"NOT_SUPER"})
    public boolean buidarCua(String queueName) {
        return getDelegateService().buidarCua(queueName);
    }

}
