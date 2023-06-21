/**
 * 
 */
package es.caib.notib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicació Spring Boot de NOTIB per a ser executada des de Tomcat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SpringBootApplication
public class NotibServletApp extends NotibApp {

	public static void main(String[] args) {
		SpringApplication.run(NotibServletApp.class);
	}

}
