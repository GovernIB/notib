package es.caib.notib.war.config;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableSwagger2
public class SwaggerConfig {

//	@Bean
//    public Docket api() { 
//        return new Docket(DocumentationType.SWAGGER_2)  
//          .select()                                  
//          .apis(RequestHandlerSelectors.any())              
//          .paths(paths())                          
//          .build();                                           
//    }
	
//	@Bean
//	public Docket customDocket(){
//		return new Docket(DocumentationType.SWAGGER_2)
//    		  .groupName("Notificacio-API")
//    		  .select()
//    		  .paths(paths())
//    		  .build()
//    		  .apiInfo(apiInfo());
////              .securitySchemes(securitySchemes())
////              .securityContext(securityContext());
//	}
	
//	private Predicate<String> paths() {
//		return or(
//	        regex("/notificacio.*"),
//	        regex("/springsRestController.*"));
//	}
//	
//	private ApiInfo apiInfo() {
//		ApiInfo apiInfo = new ApiInfo(
//				"API Notificacio",
//				"API de Notificació REST",
//				"1.0",
//				"", 	// URL de temes de servei
//				"limit@limit.es",
//				"",		// Llicència
//				""); 	// URL Llicència
//				
//		return apiInfo;
//	}
}
