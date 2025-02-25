package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.intf.dto.ActiveMqInfo;
import es.caib.notib.logic.intf.dto.ActiveMqMissatgeInfo;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.service.ActiveMqService;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.OpenDataException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ActiveMqServiceImpl implements ActiveMqService {

    private final BrokerService brokerService;
    private final PaginacioHelper paginacioHelper;
    private final MessageHelper messageHelper;
    private final NotificacioRepository notificacioRepository;
    private final NotificacioEnviamentRepository notificacioEnviamentRepository;

    @Override
    public PaginaDto<ActiveMqInfo> getInfoQueues(PaginacioParamsDto paginacioParams) {

        List<ActiveMqInfo> infoQueues = new ArrayList<>();
        try {
            var queues = getQueuesNames();
            ActiveMqInfo infoQueue;
            for (var queue : queues) {
                if (queue == null) {
                    continue;
                }
                infoQueue = getQueueInfo(queue);
                if (infoQueue != null) {
                    infoQueues.add(infoQueue);
                }
            }
        } catch (Exception ex) {
            log.error("[Monitor MQ] Error al obtenir la informacio de les queues ", ex);
        }
        return paginacioHelper.toPaginaDto(infoQueues, ActiveMqInfo.class);
    }

    private ActiveMqInfo getQueueInfo(String queueNom) throws MalformedObjectNameException {

        var connection = ManagementFactory.getPlatformMBeanServer();
        var activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
        var mBean = JMX.newMBeanProxy(connection, activeMQ, BrokerViewMBean.class);
        QueueViewMBean queueMBean;
        ActiveMqInfo queueInfo;
        for (var name : mBean.getQueues()) {
             queueMBean = JMX.newMBeanProxy(connection, name, QueueViewMBean.class);
            if (!queueMBean.getName().equals(queueNom)) {
                continue;
            }
            var queueSize = queueMBean.getQueueSize();
            var consumerCount = queueMBean.getConsumerCount();
            var enqueueCount = queueMBean.getEnqueueCount();
            var dequeueCount = queueMBean.getDequeueCount();
            var forwardCount = queueMBean.getForwardCount();
            var inFlightCount = queueMBean.getInFlightCount();
            var expiredCount = queueMBean.getExpiredCount();
            var storeMessageSize = queueMBean.getStoreMessageSize();

            var desc = messageHelper.getMessage("monitor.activemq.descripcio.cua." + queueNom);
            queueInfo = ActiveMqInfo.builder()
                    .nom(queueNom)
                    .descripcio(desc)
                    .mida(queueSize)
                    .consumersCount(consumerCount)
                    .enqueueCount(enqueueCount)
                    .dequeueCount(dequeueCount)
                    .forwardCount(forwardCount)
                    .inFlightCount(inFlightCount)
                    .expiredCount(expiredCount)
                    .storeMessageSize(storeMessageSize)
                    .build();
            return queueInfo;
        }
        return null;
    }

    @Override
    public List<ActiveMqMissatgeInfo> getMessages(String queueNom) {

        List<ActiveMqMissatgeInfo> missatges = new ArrayList<>();
        try {
            var connection = ManagementFactory.getPlatformMBeanServer();
            var activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
            var mBean = JMX.newMBeanProxy(connection, activeMQ, BrokerViewMBean.class);
            QueueViewMBean queueMBean;
            for (var name : mBean.getQueues()) {
                queueMBean = JMX.newMBeanProxy(connection, name, QueueViewMBean.class);
                if (!queueMBean.getName().equals(queueNom)) {
                    continue;
                }
                var messages = queueMBean.browse();
                String uuId;
                ActiveMqMissatgeInfo info;
                for (var message : messages) {
                    info = ActiveMqMissatgeInfo.builder().id(message.get("JMSMessageID").toString()).data((Date)message.get("JMSTimestamp")).build();
                    tractarMissatge(message.get("Text").toString(), info);
                    missatges.add(info);
                }
            }
        } catch (Exception ex) {
            log.error("[Monitor MQ] Error al obtenir la informacio dels missatges per la cua " + queueNom, ex);
        }
        return missatges;
    }

    private void tractarMissatge(String msg, ActiveMqMissatgeInfo missatge) {

        try {
            var uuId = msg.split("enviamentUuid")[1].split(",")[0].split("\"")[2];
            var enviament = notificacioEnviamentRepository.findByUuid(uuId).orElseThrow();
            missatge.setUuid(uuId);
            missatge.setNotificacioUuId(enviament.getNotificacio().getReferencia());
            return;
        } catch(Exception ex) {
        }
        try {
            var uuId = msg.split("enviamentUuid")[1].split(",")[0].split("\"")[2];
            missatge.setUuid(uuId);
        } catch (Exception ex) {

        }
        try {
            var notificacioId = msg.split("notificacioId")[1].split(":")[1].split("}")[0];
            var notificacio = notificacioRepository.findById(Long.parseLong(notificacioId)).orElseThrow();
            missatge.setNotificacioUuId(notificacio.getReferencia());
        } catch (Exception ex) {

        }
    }

    private List<String> getQueuesNames() throws Exception {
        List<String> queues = new ArrayList<>();

        MBeanServerConnection connection = ManagementFactory.getPlatformMBeanServer();
        ObjectName activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
        BrokerViewMBean mBean = JMX.newMBeanProxy(connection, activeMQ, BrokerViewMBean.class);

        for (ObjectName name : mBean.getQueues()) {
            QueueViewMBean queueMBean = JMX.newMBeanProxy(connection, name, QueueViewMBean.class);
            queues.add(queueMBean.getName());
        }

        return queues;
    }

    public List<String> getQueues() throws Exception {
        List<String> queues = new ArrayList<>();

        MBeanServerConnection connection = ManagementFactory.getPlatformMBeanServer();
        ObjectName activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
        BrokerViewMBean mBean = JMX.newMBeanProxy(connection, activeMQ, BrokerViewMBean.class);

        for (ObjectName name : mBean.getQueues()) {
            QueueViewMBean queueMBean = JMX.newMBeanProxy(connection, name, QueueViewMBean.class);
            queues.add(queueMBean.getName() + " - " + queueMBean.getQueueSize() + " messages");
        }

        return queues;
    }

    @Override
    public void createQueue(String queueName) throws Exception {
        MBeanServerConnection connection = ManagementFactory.getPlatformMBeanServer();
        ObjectName activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
        BrokerViewMBean mBean = JMX.newMBeanProxy(connection, activeMQ, BrokerViewMBean.class);
        mBean.addQueue(queueName);
    }

    @Override
    public void deleteQueue(String queueName) throws Exception {

        MBeanServerConnection connection = ManagementFactory.getPlatformMBeanServer();
        ObjectName activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
        BrokerViewMBean mBean = JMX.newMBeanProxy(connection, activeMQ, BrokerViewMBean.class);
        mBean.removeQueue(queueName);
    }

    @Override
    public String getQueueDetails(String queueName) throws Exception {

        MBeanServerConnection connection = ManagementFactory.getPlatformMBeanServer();
        ObjectName activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
        BrokerViewMBean mBean = JMX.newMBeanProxy(connection, activeMQ, BrokerViewMBean.class);

        for (ObjectName name : mBean.getQueues()) {
            QueueViewMBean queueMBean = JMX.newMBeanProxy(connection, name, QueueViewMBean.class);
            if (queueMBean.getName().equals(queueName)) {
                return "Queue: " + queueMBean.getName() +
                        " - Size: " + queueMBean.getQueueSize() +
                        " messages - Consumers: " + queueMBean.getConsumerCount();
            }
        }
        return "Queue not found";
    }

    @Override
    public void compactKahaDB() throws Exception {

        if (brokerService.getPersistenceAdapter() instanceof KahaDBPersistenceAdapter) {
            KahaDBPersistenceAdapter kahaDBStore = (KahaDBPersistenceAdapter) brokerService.getPersistenceAdapter();
            long originalCleanupInterval = kahaDBStore.getCleanupInterval();
            kahaDBStore.setCleanupInterval(1000); // 1 segon per a una neteja immediata

            // Iniciar un fil per esperar i restablir l'interval de neteja original després de la compactació
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Esperar 2 segons per assegurar que la compactació hagi tingut lloc
                    kahaDBStore.setCleanupInterval(originalCleanupInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            throw new IllegalStateException("Persistence adapter is not KahaDBStore");
        }
    }

    @Override
    public boolean deleteMessage(String queueName, String messageId) {

        try {
            var connection = ManagementFactory.getPlatformMBeanServer();
            var activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
            var mBean = JMX.newMBeanProxy(connection, activeMQ, BrokerViewMBean.class);
            for (var name : mBean.getQueues()) {
                var queueMBean = JMX.newMBeanProxy(connection, name, QueueViewMBean.class);
                if (!queueMBean.getName().equals(queueName)) {
                    continue;
                }
                queueMBean.removeMessage(messageId);
            }

            return true;
        } catch (Exception ex) {
            log.error("[Monitor MQ] Error al esborrar el missatge " + messageId, ex);
            return false;
        }
    }

    @Override
    public boolean buidarCua(String queueName) {

        try {
            var connection = ManagementFactory.getPlatformMBeanServer();
            var activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
            var mBean = JMX.newMBeanProxy(connection, activeMQ, BrokerViewMBean.class);
            for (var name : mBean.getQueues()) {
                var queueMBean = JMX.newMBeanProxy(connection, name, QueueViewMBean.class);
                if (!queueMBean.getName().equals(queueName)) {
                    continue;
                }
                queueMBean.purge();
            }
            return true;
        } catch (Exception ex) {
            log.error("[Monitor MQ] Error al buidar la cua " + queueName, ex);
            return false;
        }
    }
}
