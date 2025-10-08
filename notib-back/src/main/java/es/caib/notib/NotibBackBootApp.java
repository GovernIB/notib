package es.caib.notib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Classe principal del backoffice de NOTIB per executar amb SpringBoot.
 * 
 * @author Límit Tecnologies
 */
@SpringBootApplication
@EnableAsync
@PropertySource(
		ignoreResourceNotFound = true,
		value = { "classpath:application.yaml" })
public class NotibBackBootApp {

	public static void main(String[] args) {
		SpringApplication.run(NotibBackBootApp.class, args);
	}

}
