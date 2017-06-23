package es.caib.notib.war.config;

import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriComponentsBuilder;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
 
public class ApiPathProvider extends SwaggerPathProvider {
 
    private SwaggerPathProvider defaultSwaggerPathProvider;
    
    @Autowired
    private ServletContext servletContext;
 
//    private String basePath;
 
    public ApiPathProvider() { //String basePath) {
//        this.basePath = basePath;
    }
 
    @Override
    public String getApiResourcePrefix() {
        return defaultSwaggerPathProvider.getApiResourcePrefix();
    }
 
    public void setDefaultSwaggerPathProvider(SwaggerPathProvider defaultSwaggerPathProvider) {
        this.defaultSwaggerPathProvider = defaultSwaggerPathProvider;
    }
 
	@Override
	protected String applicationPath() {
//        return UriComponentsBuilder
//        		.fromPath("/")
//                .fromHttpUrl(basePath)
//                .path(servletContext.getContextPath())
//                .build()
//                .toString();
		return "/";
	}
 
	@Override
	protected String getDocumentationPath() {
//        return UriComponentsBuilder
//                .fromHttpUrl(applicationPath())
//                .pathSegment("api-docs/")
//                .build()
//                .toString();
		return "/api-docs/";
	}
}