/**
 * 
 */
package es.caib.notib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;

/**
 * Aplicació Spring Boot de NOTIB per a ser executada des de JBoss.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Profile("!boot")
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class, 
		DataSourceTransactionManagerAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		TransactionAutoConfiguration.class,
		FreeMarkerAutoConfiguration.class,
		WebSocketServletAutoConfiguration.class
})
@ComponentScan(
		excludeFilters = @ComponentScan.Filter(
				type = FilterType.REGEX,
				pattern = {
						"es\\.caib\\.notib\\.logic\\..*",
						"es\\.caib\\.notib\\.persist\\..*",
						"es\\.caib\\.notib\\.ejb\\..*"}))
public class NotibEjbApp extends NotibApp {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(NotibEjbApp.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(NotibEjbApp.class, args);
	}

}
