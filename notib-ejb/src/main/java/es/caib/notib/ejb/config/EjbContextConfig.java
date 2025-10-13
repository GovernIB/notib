/**
 * 
 */
package es.caib.notib.ejb.config;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.jersey.JerseyServerMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Creació del context Spring per a la capa dels EJBs.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Configuration
@EnableAutoConfiguration(exclude = {
		FreeMarkerAutoConfiguration.class,
		JerseyServerMetricsAutoConfiguration.class
})
@ComponentScan({
		BaseConfig.BASE_PACKAGE + ".logic",
		BaseConfig.BASE_PACKAGE + ".persist"
})
//@EnableJpaRepositories({ "es.caib.notib.persist", "org.springframework.statemachine.data.jpa" })
//@EntityScan({"es.caib.notib.persist", "org.springframework.statemachine.data.jpa"})
@PropertySource(ignoreResourceNotFound = true, value = {
		"classpath:application.properties",
		"file://${" + BaseConfig.APP_PROPERTIES + "}",
		"file://${" + BaseConfig.APP_SYSTEM_PROPERTIES + "}"})
public class EjbContextConfig {

	/*
	@Value("${es.caib.notib.datasource.jndi:java:jboss/datasources/notibDS}")
	private String dataSourceJndiName;
	@Value("${es.caib.notib.hibernate.dialect:es.caib.notib.persist.dialect.OracleCaibDialect}")
	private String hibernateDialect;
	@Value("${es.caib.notib.hibernate.ddl.auto:none}")
	private String hibernateDdlAuto;
	@Value("${es.caib.notib.hibernate.show_sql:false}")
	private String showSql;
	@Value("${es.caib.notib.hibernate.format_sql:false}")
	private String formatSql;
	@Value("${es.caib.notib.servidor.jboss:true}")
	private String jboss;*/

	private static boolean initialized;
	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		if (!initialized) {
			initialized = true;
			log.info("Starting EJB spring application...");
			applicationContext = new AnnotationConfigApplicationContext(EjbContextConfig.class);
			log.info("...EJB spring application started.");
		}
		return applicationContext;
	}

	/*@Bean
	public AbstractEntityManagerFactoryBean entityManagerFactory() throws NamingException {

		log.debug("Creating EntityManagerFactory " + dataSource().getClass() + "...");
		var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		if (isJBoss()) {
			entityManagerFactoryBean.setJtaDataSource(dataSource());
		} else {
			entityManagerFactoryBean.setDataSource(dataSource());
		}
		entityManagerFactoryBean.setPackagesToScan("es.caib.notib.persist", "org.springframework.statemachine.data.jpa");
		entityManagerFactoryBean.setJpaDialect(new HibernateJpaDialect());
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		Properties jpaProperties = new Properties();
		jpaProperties.setProperty("hibernate.dialect", hibernateDialect);
		jpaProperties.setProperty("hibernate.hbm2ddl.auto", hibernateDdlAuto);
		jpaProperties.setProperty("hibernate.show_sql", showSql);
		jpaProperties.setProperty("hibernate.format_sql", formatSql);
		if (isJBoss()) {
			jpaProperties.setProperty("hibernate.transaction.manager_lookup_class", "org.hibernate.transaction.JBossTransactionManagerLookup");
		}
		entityManagerFactoryBean.setJpaProperties(jpaProperties);
		log.debug("...EntityManagerFactory successfully created.");
		return entityManagerFactoryBean;
	}

	@Bean
	public DataSource dataSource() {

		log.debug("Retrieving DataSource...");
		var lookup = new JndiDataSourceLookup();
		var dataSource = lookup.getDataSource(dataSourceJndiName);
		log.debug("...DataSource successfully retrieved.");
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {

		log.debug("Creating TransactionManager...");
		PlatformTransactionManager transactionManager;
		if (isJBoss()) {
			JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
			jtaTransactionManager.setTransactionManagerName("java:/TransactionManager");
			transactionManager = jtaTransactionManager;
		} else {
			JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
			jpaTransactionManager.setEntityManagerFactory(emf);
			transactionManager = jpaTransactionManager;
		}
		log.debug("...TransactionManager successfully created.");
		return transactionManager;
	}

	private boolean isJBoss() {
		return Boolean.parseBoolean(jboss);
	}*/

}
