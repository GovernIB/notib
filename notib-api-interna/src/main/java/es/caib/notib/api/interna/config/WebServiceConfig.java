/**
 * 
 */
package es.caib.notib.api.interna.config;

import es.caib.notib.logic.intf.ws.adviser.AdviserServiceWsV2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * @author limit
 *
 */
@ConditionalOnNotWarDeployment
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

	@Bean
	public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean<>(servlet, "/ws/*");
	}

	@Bean(name = "adviser")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema adviserSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName(AdviserServiceWsV2.SERVICE_NAME + "Port");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace(AdviserServiceWsV2.NAMESPACE_URI);
		wsdl11Definition.setSchema(adviserSchema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema adviserSchema() {
		return new SimpleXsdSchema(new ClassPathResource("adviser.xsd"));
	}

//	@Bean
//	public KeycloakConfigResolver KeycloakConfigResolver() {
//		return new KeycloakSpringBootConfigResolver();
//	}

}
