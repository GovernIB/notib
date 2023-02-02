/**
 * 
 */
package es.caib.notib;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.jar.Manifest;

/**
 * Aplicació base amb Spring Boot de NOTIB.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public abstract class NotibApp extends SpringBootServletInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		try {
			var manifest = new Manifest(servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"));
			var attributes = manifest.getMainAttributes();
			var version = attributes.getValue("Implementation-Version");
			var buildTimestamp = attributes.getValue("Build-Timestamp");
			log.info("Carregant l'aplicació NOTIB versió " + version + " generada en data " + buildTimestamp);
			/*
			Implementation-SCM-Revision: 
			Implementation-Title: notib-back-war
			Build-Timestamp: 2021-04-05T04:01:49Z
			Implementation-Version: 2.0.1
			Implementation-SCM-Branch: 
			Build-Jdk-Spec: 1.8
			Created-By: Maven WAR Plugin 3.3.1
			Manifest-Version: 1.0
			Implementation-Vendor: Límit Tecnologies
			*/
		} catch (IOException ex) {
			throw new ServletException("Couldn't read MANIFEST.MF", ex);
		}
		super.onStartup(servletContext);
	}

}
