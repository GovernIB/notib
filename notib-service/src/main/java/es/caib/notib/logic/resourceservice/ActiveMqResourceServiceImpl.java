package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.activeMq.ActiveMqJobSchedullerReportGenerator;
import es.caib.notib.logic.activeMq.BuidarCuaActiveMqActionExecutor;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.intf.model.ActiveMqResource;
import es.caib.notib.logic.intf.resourceservice.ActiveMqResourceService;
import es.caib.notib.logic.intf.service.ActiveMqService;
import es.caib.notib.persist.resourceentity.ActiveMqResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementació del servei de consulta de l'ActiveMq
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActiveMqResourceServiceImpl extends BaseMutableResourceService<ActiveMqResource, String, ActiveMqResourceEntity> implements ActiveMqResourceService {

	private final MetricsHelper metricsHelper;
	private final MessageHelper messageHelper;
	private final ActiveMqService activeMqService;

	@PostConstruct
	public void init() {

		register(ActiveMqResource.REPORT_DESCARREGAR_JOB_SCHEDULER_JSON, new ActiveMqJobSchedullerReportGenerator(activeMqService));
		register(ActiveMqResource.ACTION_BUIDAR_CUA, new BuidarCuaActiveMqActionExecutor(activeMqService));
	}

	@Override
	public Page<ActiveMqResource> findPage(String quickFilter, String filter, String[] namedQueries, String[] perspectives, Pageable pageable) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var queues = getQueuesNames();
			ActiveMqResource infoQueue;
			List<ActiveMqResource> infoQueues = new ArrayList<>();
			for (var queue : queues) {
				if (queue == null || "ActiveMQ.DLQ".equals(queue)) {
					continue;
				}
				infoQueue = getQueueInfo(queue);
				if (infoQueue != null) {
					infoQueues.add(infoQueue);
				}
			}
			return new PageImpl<>(infoQueues, pageable, infoQueues.size());
		} catch (Exception ex) {
			log.error("Error obtinguent la info de l'ActiveMQ");
			return Page.empty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public boolean isEntityRepositoryOptional() {
		return true;
	}

	private ActiveMqResource getQueueInfo(String queueNom) throws MalformedObjectNameException {

		var connection = ManagementFactory.getPlatformMBeanServer();
		var activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
		var mBean = JMX.newMBeanProxy(connection, activeMQ, BrokerViewMBean.class);
		QueueViewMBean queueMBean;
		ActiveMqResource queueInfo;
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
			queueInfo = ActiveMqResource.builder()
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
			queueInfo.setId(queueNom);
			return queueInfo;
		}
		return null;
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

}
