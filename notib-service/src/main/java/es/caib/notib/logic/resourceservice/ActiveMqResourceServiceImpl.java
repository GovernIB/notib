package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.notib.logic.base.service.BaseNoDatabaseReadonlyResourceService;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.intf.dto.ActiveMqInfo;
import es.caib.notib.logic.intf.model.ActiveMqResource;
import es.caib.notib.logic.intf.model.CacheResource;
import es.caib.notib.logic.intf.resourceservice.ActiveMqResourceService;
import es.caib.notib.logic.intf.resourceservice.CacheResourceService;
import es.caib.notib.persist.base.entity.NoDatabaseResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Implementació del servei de consulta de caches de l'aplciació
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActiveMqResourceServiceImpl extends BaseNoDatabaseMutableResourceService<ActiveMqResource, String> implements ActiveMqResourceService {

	private final MetricsHelper metricsHelper;
	private final CacheHelper cacheHelper;
	private final MessageHelper messageHelper;

	@Override
	protected Page<NoDatabaseResourceEntity<ActiveMqResource, String>> entityRepositoryFindEntities(String quickFilter, String filter, String[] namedQueries, Pageable pageable) {

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
			Page<ActiveMqResource> page = new PageImpl<>(infoQueues, pageable, infoQueues.size());
			return page.map(this::toResourceEntity);
		} catch (Exception ex) {
			log.error("Error obtinguent la info de l'ActiveMQ");
			return Page.empty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
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

	private NoDatabaseResourceEntity<ActiveMqResource, String> toResourceEntity(ActiveMqResource resource) {
		return new NoDatabaseResourceEntity<>(resource.getNom(), resource);
	}


	private static final Map<String, Integer> ordreCaches;

	static {
		ordreCaches = new HashMap<>();
		ordreCaches.put("aclCache", 0);
		ordreCaches.put("usuariAmbCodi", 1);
		ordreCaches.put("rolsAmbCodi", 2);
		ordreCaches.put("entitatsUsuari", 3);
		ordreCaches.put("organsGestorsUsuari", 4);
		ordreCaches.put("findUsuarisAmbPermis", 5);
		ordreCaches.put("organismes", 6);
		ordreCaches.put("organigrama", 7);
		ordreCaches.put("organigramaOriginal", 8);
		ordreCaches.put("codisOrgansFills", 9);
		ordreCaches.put("organCodisAncestors", 10);
		ordreCaches.put("unitatPerCodi", 11);
		ordreCaches.put("findOficinesEntitat", 12);
		ordreCaches.put("findLlibreOrganisme", 13);
		ordreCaches.put("llistarNivellsAdministracions", 14);
		ordreCaches.put("llistarComunitatsAutonomes", 15);
		ordreCaches.put("llistarProvincies", 16);
		ordreCaches.put("llistarLocalitats", 17);
		ordreCaches.put("oficinesSIREntitat", 18);
		ordreCaches.put("oficinesSIRUnitat", 19);
		ordreCaches.put("getPermisosEntitatsUsuariActual", 20);
		ordreCaches.put("procsersPermisNotificacioMenu", 21);
		ordreCaches.put("procsersPermisComunicacioMenu", 22);
		ordreCaches.put("procsersPermisComunicacioSirMenu", 23);
		ordreCaches.put("procserAmbPermis", 24);
		ordreCaches.put("procedimentsAmbPermis", 25);
		ordreCaches.put("serveisAmbPermis", 26);
		ordreCaches.put("organsAmbPermis", 27);
		ordreCaches.put("organsAmbPermisPerConsulta", 28);
		ordreCaches.put("organsPermisPerProcedimentComu", 29);
		ordreCaches.put("procserOrgansCodisAmbPermis", 30);
		ordreCaches.put("findUsuariByCodi", 31);
		ordreCaches.put("findEntitatByCodi", 32);
	}
}
