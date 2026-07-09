package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.activeMq.EsborrarMissatgeCuaActiveMqActionExecutor;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.intf.model.ActiveMqDetailResource;
import es.caib.notib.logic.intf.resourceservice.ActiveMqDetailResourceService;
import es.caib.notib.logic.intf.service.ActiveMqService;
import es.caib.notib.persist.resourceentity.ActiveMqDetailResourceEntity;
import es.caib.notib.persist.resourcerepository.NotificacioEnviamentResourceRepository;
import es.caib.notib.persist.resourcerepository.NotificacioResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.management.JMX;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Implementació del servei de consulta de missatges d'una cua de l'ActiveMq
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActiveMqDetailResourceServiceImpl extends BaseMutableResourceService<ActiveMqDetailResource, String, ActiveMqDetailResourceEntity> implements ActiveMqDetailResourceService {

	private final MetricsHelper metricsHelper;
	private final ActiveMqService activeMqService;
	private final NotificacioResourceRepository notificacioRepository;
	private final NotificacioEnviamentResourceRepository enviamentRepository;

	@PostConstruct
	public void init() {

		register(ActiveMqDetailResource.ACTION_ESBORRAR_MISSATGE, new EsborrarMissatgeCuaActiveMqActionExecutor(activeMqService));
	}

	@Override
	public Page<ActiveMqDetailResource> findPage(String quickFilter, String filter, String[] namedQueries, String[] perspectives, Pageable pageable) {

		var timer = metricsHelper.iniciMetrica();
		try {
			List<ActiveMqDetailResource> missatges = new ArrayList<>();
			var connection = ManagementFactory.getPlatformMBeanServer();
			var activeMQ = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
			var mBean = JMX.newMBeanProxy(connection, activeMQ, BrokerViewMBean.class);
			QueueViewMBean queueMBean;
			var filterSplit = !StringUtils.isEmpty(filter) ? filter.split(" ") : null;
			if (filterSplit == null || filterSplit.length != 2) {
				return Page.empty();
			}
			var queueNom = filterSplit[1];
			for (var name : mBean.getQueues()) {
				queueMBean = JMX.newMBeanProxy(connection, name, QueueViewMBean.class);
				if (!queueMBean.getName().equals(queueNom)) {
					continue;
				}
				var messages = queueMBean.browse();
				ActiveMqDetailResource info;
				for (var message : messages) {
					info = ActiveMqDetailResource.builder().id(message.get("JMSMessageID").toString()).data((Date)message.get("JMSTimestamp")).build();
					tractarMissatge(message.get("Text").toString(), info);
					missatges.add(info);
				}
			}
			return new PageImpl<>(missatges, pageable, missatges.size());
		} catch (Exception ex) {
			log.error("Error obtinguent la info dels missatges de l'ActiveMQ");
			return Page.empty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	private void tractarMissatge(String msg, ActiveMqDetailResource missatge) {

		try {
			var uuId = msg.split("enviamentUuid")[1].split(",")[0].split("\"")[2];
			var enviament = enviamentRepository.findByUuid(uuId).orElseThrow();
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

	@Override
	public boolean isEntityRepositoryOptional() {
		return true;
	}


}
