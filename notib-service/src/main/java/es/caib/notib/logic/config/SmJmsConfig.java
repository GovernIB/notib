package es.caib.notib.logic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
import org.apache.activemq.usage.SystemUsage;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;
import java.io.File;

//@Profile("!testNoSm")
@Configuration
@EnableJms
public class SmJmsConfig {

    @Value("${es.caib.notib.activemq.broker-url:tcp://localhost:61666}")
    private String BROKER_URL;
    @Value("${es.caib.notib.activemq.user:jmsUser}")
    private String BROKER_USERNAME;
    @Value("${es.caib.notib.activemq.password:jmsPass}")
    private String BROKER_PASSWORD;
    @Value("${es.caib.notib.plugin.gesdoc.filesystem.base.dir:target}")
    private String fileBaseDir;
    @Value("${es.caib.notib.activemq.max.concurrency:50}")
    private Integer BROKER_MAX_CONCURRENCY;

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // use ISO-8601 strings
        converter.setObjectMapper(mapper);
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all auto-configured defaults to this factory, including the message converter
//        factory.setSessionTransacted(true);
        factory.setConnectionFactory(new PooledConnectionFactory(BROKER_URL));
        factory.setSessionAcknowledgeMode(JmsProperties.AcknowledgeMode.CLIENT.getMode());
        factory.setConcurrency("5-" + BROKER_MAX_CONCURRENCY);
        configurer.configure(factory, connectionFactory);
        // You could still override some settings if necessary.
        return factory;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory(){

        ActiveMQConnectionFactory connectionFactory = new  ActiveMQConnectionFactory();
        connectionFactory.setTrustAllPackages(true);
        connectionFactory.setBrokerURL(BROKER_URL);
        connectionFactory.setUserName(BROKER_USERNAME);
        connectionFactory.setPassword(BROKER_PASSWORD);
        return connectionFactory;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile("!remoteBroker")
    public BrokerService broker() throws Exception {

        final BrokerService broker = new BrokerService();
        broker.addConnector(BROKER_URL);

        // KahaDB
        KahaDBPersistenceAdapter persistenceAdapter = new KahaDBPersistenceAdapter();
        File dir = new File(fileBaseDir + "/kaha");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        persistenceAdapter.setDirectory(dir);
        persistenceAdapter.setJournalMaxFileLength(1 * 1024 * 1024 * 1025); // 1Gb
        persistenceAdapter.setCleanupInterval(1000 * 60 * 60);              // 1h
        broker.setPersistenceAdapter(persistenceAdapter);
        broker.setPersistent(true);

        // Scheduler
        File schedDir = new File(fileBaseDir + "/scheduler");
        if (!schedDir.exists()) {
            schedDir.mkdirs();
        }
        broker.setSchedulerDirectoryFile(schedDir);
        broker.setSchedulerSupport(true);

        // Límit d’ús de storage (KahaDB + scheduler)
        SystemUsage usage = broker.getSystemUsage();
        // 1) Persistència “normal” (KahaDB)
        usage.getStoreUsage().setLimit(60L * 1024 * 1024 * 1024); // 60 GB
        // 2) Scheduler (PListStore)
        usage.getJobSchedulerUsage().setLimit(40L * 1024 * 1024 * 1024); // 40 GB
        // 3) Temporal (cursors / temp store)
        usage.getTempUsage().setLimit(10L * 1024 * 1024 * 1024);  // 10 GB (opcional)
        // 4) Memòria del broker
        usage.getMemoryUsage().setLimit(1024L * 1024 * 1024);      // 1 GB (opcional)
        // 5) No bloquejar productors si no hi ha espai, que falli el send() en lloc de bloquejar
        usage.setSendFailIfNoSpace(true);
        // opcional: si prefereixes “esperar una mica” i després fallar
        usage.setSendFailIfNoSpaceAfterTimeout(5_000); // 5s

        // 6) Configuració de polítiques per evitar el bloqueig per Producer Flow Control
        PolicyMap policyMap = new PolicyMap();
        PolicyEntry defaultEntry = new PolicyEntry();
        defaultEntry.setProducerFlowControl(true); // Activa el control de flux
        // Si el control de flux està actiu i sendFailIfNoSpace és cert, el productor rebrà una excepció en lloc de bloquejar-se
        policyMap.setDefaultEntry(defaultEntry);
        broker.setDestinationPolicy(policyMap);

        return broker;
    }

    @Bean
    public JmsTemplate jmsTemplate() {

        var jmsTemplate = new JmsTemplate(new PooledConnectionFactory(BROKER_URL));
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());
        return jmsTemplate;
    }
}
