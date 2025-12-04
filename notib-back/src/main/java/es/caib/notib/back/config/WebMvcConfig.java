package es.caib.notib.back.config;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import es.caib.notib.back.base.config.BaseWebMvcConfig;
import es.caib.notib.back.interceptor.*;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Configuració de Spring web MVC.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@Order
public class WebMvcConfig extends BaseWebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private NotibInterceptor notibInterceptor;
	@Autowired
	private SessionInterceptor sessionInterceptor;
	@Autowired
	private AccesPagadorsInterceptor accesPagadorsInterceptor;
	@Autowired
	private AccesAdminInterceptor accesAdminInterceptor;
	@Autowired
	private AccesSuperInterceptor accesSuperInterceptor;
	@Autowired
	private AccesUsuariInterceptor accesUsuariInterceptor;

	private static final long MAX_UPLOAD_SIZE = 52428800;

	@Bean
	public FilterRegistrationBean<SiteMeshFilter> sitemeshFilter() {
		FilterRegistrationBean<SiteMeshFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new SiteMeshFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(2);
		return registrationBean;
	}

    @Override
    protected boolean isJsAppResourceHandlerEnabled() {
        return false;
    }

    @Override
    protected String getJsAppStaticFolder() {
        return "/reactapp";
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ResourceHandler per a que totes les peticions desconegudes passin per l'index.html
        registry.
                addResourceHandler(getJsAppStaticFolder() + "/**").
                addResourceLocations(getJsAppStaticFolder() + "/").
                resourceChain(true).
                addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        } else {
                            return location.createRelative("index.html");
                        }
                    }
                });
    }


    @Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("/WEB-INF/jsp/", ".jsp");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		/*registry.
				addMapping("/**").
				allowedOrigins("http://localhost:5173", "http://localhost:8080").
				allowCredentials(true).
				allowedHeaders("Accept", "Content-Type", "Origin", "Authorization", "X-Auth-Token").
				exposedHeaders("X-Auth-Token", "Authorization").
				allowedMethods("POST", "GET", "DELETE", "PUT", "OPTIONS");*/
				/*allowedMethods("*").
				allowedHeaders("*").
				allowedMethods("POST", "GET", "DELETE", "PUT", "OPTIONS");*/
		registry.
				addMapping("/**").
				allowedOrigins("http://localhost:5173", "http://localhost:8080").
				allowCredentials(true).
				allowedHeaders("*").
				allowedMethods("*");
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(MAX_UPLOAD_SIZE);
		return multipartResolver;
	}

	@Bean
	public LocaleResolver localeResolver() {
		var localeResolver = new CustomLocaleResolver(Arrays.asList(Locale.forLanguageTag("ca"), Locale.forLanguageTag("es")));
		localeResolver.setDefaultLocale(Locale.forLanguageTag("ca"));
		return localeResolver;
	}

	/*@Bean
	public ViewResolver internalResourceViewResolver() {
		var bean = new InternalResourceViewResolver();
		bean.setViewClass(JstlView.class);
		bean.setPrefix("/WEB-INF/jsp/");
		bean.setSuffix(".jsp");
		return bean;
	}*/

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		var lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	private static final String[] INTERCEPTOR_EXCLUSIONS = 	{
			BaseConfig.API_PATH + "/**",
			BaseConfig.PING_PATH,
			BaseConfig.SYSENV_PATH,
			BaseConfig.MANIFEST_PATH,
			BaseConfig.AUTH_TOKEN_PATH,
			BaseConfig.REACT_APP_PATH + "/**",
			"/js/**",
			"/css/**",
			"/fonts/**",
			"/img/**",
			"/images/**",
			"/extensions/**",
			"/webjars/**",
			"/**/datatable/**",
			"/**/selection/**",
			"/api/rest/**",
			"/api/apidoc**",
			"/api-docs/**",
			"/**/api-docs/",
			"/api/consulta/**",
			"/api/services/**",
			"/notificacio/refrescarEstatNotifica/estat",
			"/notificacio/procedimentsOrgan",
			"/notificacio/serveisOrgan",
			"/error",
			"/**/monitor/tasques"
	};
	private static final String[] ALL_EXCLUSIONS = {"/js/**", "/css/**", "/fonts/**", "/img/**", "/images/**", "/extensions/**", "/webjars/**", "/**/datatable/**", "/**/selection/**", "/api/rest/**", "/api/apidoc**", "/api-docs/**", "/**/api-docs/", "/api/consulta/**", "/api/services/**", "/usuari/configuracio/**"};
	// Urls accés
	private static final String[] PAGADORS_PATHS = { "/cie**", "/cie/**", "/operadorPostal**", "/operadorPostal/**" };
	private static final String[] ADMIN_PATHS = { "/callback**", "/callback/**", "/organgestor**", "/organgestor/**", "/procediment**", "/procediment/**", "/servei**", "/servei/**", "/grup**", "/grup/**", "/massiu/registre/notificacionsError", "/massiu/registre/notificacionsError/**" };
	private static final String[] SUPER_PATHS = { "/avis**", "/avis/**", "/cache**", "/cache/**", "/config**", "/config/**", "/metrics", "/metrics/list", "/monitor", "/monitor/all", "/excepcio", "/excepcio/**", "/integracio", "/integracio/**", "/notificacio/refrescarEstatNotifica", "/notificacio/refrescarEstatNotifica/**", "/massiu/notificacions**", "/massiu/notificacions/**" };
	private static final String[] USUARI_PATHS = { "/entitat**", "/entitat/**" };
	private static final String[] USUARI_EXCLUSIONS = { "/entitat/organigrama/**", "/entitat/getEntitatLogoCap", "/entitat/getEntitatLogoPeu" };

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(notibInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS).order(0);
//		registry.addInterceptor(sessionInterceptor).excludePathPatterns(ALL_EXCLUSIONS).order(1);
		registry.addInterceptor(accesSuperInterceptor).addPathPatterns(SUPER_PATHS).order(2);
		registry.addInterceptor(accesAdminInterceptor).addPathPatterns(ADMIN_PATHS).order(3);
		registry.addInterceptor(accesUsuariInterceptor).addPathPatterns(USUARI_PATHS).excludePathPatterns(USUARI_EXCLUSIONS).order(4);
		registry.addInterceptor(accesPagadorsInterceptor).addPathPatterns(PAGADORS_PATHS).order(5);
//		registry.addInterceptor(new CsrfTokenInterceptor());
	}

	public static class CustomLocaleResolver extends SessionLocaleResolver {
		private AcceptHeaderLocaleResolver acceptHeaderLocaleResolver;
		public CustomLocaleResolver(List<Locale> supportedLocales) {
			acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
			acceptHeaderLocaleResolver.setSupportedLocales(supportedLocales);
		}
		@Override
		protected Locale determineDefaultLocale(HttpServletRequest request) {

			var acceptHeaderLocale = acceptHeaderLocaleResolver.resolveLocale(request);
			if (acceptHeaderLocale != null) {
				return acceptHeaderLocale;
			}
			Locale defaultLocale = getDefaultLocale();
			if (defaultLocale == null) {
				defaultLocale = request.getLocale();
			}
			return defaultLocale;
		}
	}

}
