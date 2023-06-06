/**
 * 
 */
package es.caib.notib.api.externa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;


/**
 * Aplicació Spring Boot de NOTIB per a ser executada des de JBoss.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		JpaRepositoriesAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		TransactionAutoConfiguration.class,
		FreeMarkerAutoConfiguration.class,
		WebSocketServletAutoConfiguration.class,
		SecurityAutoConfiguration.class,
//		OrikaAutoConfiguration.class,
//		SpringDocWebMvcConfiguration.class,
//		MultipleOpenApiSupportConfiguration.class,
		SpringDataWebAutoConfiguration.class
})
@ComponentScan(excludeFilters = @ComponentScan.Filter(
				type = FilterType.REGEX,
				pattern = {
						"es\\.caib\\.notib\\.logic\\..*",
						"es\\.caib\\.notib\\.persist\\..*",
						"es\\.caib\\.notib\\.ejb\\..*",
						"es\\.caib\\.notib\\.backoffice\\..*",
						"es\\.caib\\.notib\\.back\\..*",
						"es\\.caib\\.notib\\.war\\..*"}))
@PropertySource(value = "classpath:application.yaml")
public class NotibApiExternaApp extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
		return builder.sources(NotibApiExternaApp.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(NotibApiExternaApp.class, args);
	}

}
