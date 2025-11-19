package es.caib.notib.logic.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.store.kahadb.KahaDBPersistenceAdapter;
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
        connectionFactory.setPassword(BROKER_USERNAME);
        connectionFactory.setUserName(BROKER_PASSWORD);
        return connectionFactory;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile("!remoteBroker")
    public BrokerService broker() throws Exception {

        final BrokerService broker = new BrokerService();
        broker.addConnector(BROKER_URL);
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
        broker.setSchedulerSupport(true);
        return broker;
    }

    @Bean
    public JmsTemplate jmsTemplate() {

        var jmsTemplate = new JmsTemplate(new PooledConnectionFactory(BROKER_URL));
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());
        return jmsTemplate;
    }
}
