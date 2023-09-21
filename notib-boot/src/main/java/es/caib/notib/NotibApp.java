/**
 * 
 */
package es.caib.notib;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Aplicació Spring Boot de NOTIB.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SpringBootApplication
//@PropertySource(value = "classpath:application.yaml")
public class NotibApp {

	public static void main(String[] args) {
		new SpringApplicationBuilder(NotibApp.class).run();
	}

}
