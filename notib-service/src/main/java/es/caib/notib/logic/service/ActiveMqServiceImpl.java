package es.caib.notib.logic.service;

import es.caib.notib.logic.intf.service.ActiveMqService;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.springframework.stereotype.Service;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ActiveMqServiceImpl implements ActiveMqService {

    private final BrokerService brokerService;

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
}
