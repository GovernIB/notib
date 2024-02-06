/**
 * 
 */
package es.caib.notib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.PropertySource;

/**
 * Aplicació Spring Boot de NOTIB per a ser executada des de Tomcat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@ConditionalOnNotWarDeployment
@SpringBootApplication
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application_boot.yaml" })
public class NotibBackBootApp {

	public static void main(String[] args) {
		SpringApplication.run(NotibBackBootApp.class, args);
	}

}
