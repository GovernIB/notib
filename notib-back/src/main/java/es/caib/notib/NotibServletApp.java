/**
 * 
 */
package es.caib.notib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

/**
 * Aplicació Spring Boot de NOTIB per a ser executada des de Tomcat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Profile("!boot")
@SpringBootApplication
public class NotibServletApp extends NotibApp {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(NotibServletApp.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(NotibServletApp.class, args);
	}

}
