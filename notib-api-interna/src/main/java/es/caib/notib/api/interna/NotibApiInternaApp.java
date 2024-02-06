/**
 * 
 */
package es.caib.notib.api.interna;

import es.caib.notib.logic.intf.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.jersey.JerseyServerMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


/**
 * Aplicació Spring Boot de NOTIB per a ser executada des de JBoss.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@ConditionalOnWarDeployment
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		TransactionAutoConfiguration.class,
		LiquibaseAutoConfiguration.class,
		FreeMarkerAutoConfiguration.class,
		WebSocketServletAutoConfiguration.class,
		JerseyServerMetricsAutoConfiguration.class,
//		SecurityAutoConfiguration.class,
//		SpringDataWebAutoConfiguration.class
})
@ComponentScan(
		excludeFilters = @ComponentScan.Filter(
				type = FilterType.REGEX,
				pattern = {
						"es\\.caib\\.notib\\.logic\\..*",
						"es\\.caib\\.notib\\.persist\\..*",
						"es\\.caib\\.notib\\.ejb\\..*"}))
//						"es\\.caib\\.notib\\.backoffice\\..*",
//						"es\\.caib\\.notib\\.back\\..*",
//						"es\\.caib\\.notib\\.war\\..*"}))
@PropertySource(
		ignoreResourceNotFound = true,
		value = {"classpath:application.yaml",
				"file://${" + ConfigService.APP_PROPERTIES + "}",
				"file://${" + ConfigService.APP_SYSTEM_PROPERTIES + "}"})
public class NotibApiInternaApp extends SpringBootServletInitializer {

//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
//		return builder.sources(NotibApiInternaApp.class);
//	}

	public static void main(String[] args) {
		SpringApplication.run(NotibApiInternaApp.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		try {
			Manifest manifest = new Manifest(servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"));
			Attributes attributes = manifest.getMainAttributes();
			String version = attributes.getValue("Implementation-Version");
			String buildTimestamp = attributes.getValue("Build-Timestamp");
			log.info("Carregant l'aplicació notib-api-interna versió " + version + " generada en data " + buildTimestamp);
		} catch (IOException ex) {
			throw new ServletException("Couldn't read MANIFEST.MF", ex);
		}
		super.onStartup(servletContext);
	}

}
