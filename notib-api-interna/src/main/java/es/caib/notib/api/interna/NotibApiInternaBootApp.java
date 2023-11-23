/**
 * 
 */
package es.caib.notib.api.interna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.context.annotation.PropertySource;


/**
 * Aplicació Spring Boot de NOTIB per a ser executada des de JBoss.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@ConditionalOnNotWarDeployment
@SpringBootApplication
@PropertySource(
		ignoreResourceNotFound = true,
		value = "classpath:application.yaml")
public class NotibApiInternaBootApp {

	public static void main(String[] args) {
		SpringApplication.run(NotibApiInternaBootApp.class, args);
	}

}
