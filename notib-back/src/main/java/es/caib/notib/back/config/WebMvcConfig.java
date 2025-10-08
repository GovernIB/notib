package es.caib.notib.back.config;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import es.caib.notib.back.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolverSupport;
import org.springframework.data.web.SortArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;

/**
 * Configuració de Spring web MVC.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@Order
public class WebMvcConfig implements WebMvcConfigurer {

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

	/*@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
				.addResourceHandler("/reactapp/**")
				.addResourceLocations("/reactapp/");
	}*/

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("/WEB-INF/jsp/", ".jsp");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.
				addMapping("/**").
				allowedOrigins("*").
				allowCredentials(false).
				maxAge(3600).
				allowedHeaders("Accept", "Content-Type", "Origin", "Authorization", "X-Auth-Token").
				exposedHeaders("X-Auth-Token", "Authorization").
				allowedMethods("POST", "GET", "DELETE", "PUT", "OPTIONS");
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(MAX_UPLOAD_SIZE);
		return multipartResolver;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		CustomPageableHandlerMethodArgumentResolver resolver = new CustomPageableHandlerMethodArgumentResolver();
		resolver.setFallbackPageable(Pageable.unpaged());
		resolvers.add(resolver);
		WebMvcConfigurer.super.addArgumentResolvers(resolvers);
	}

	/*@Bean
	public LocaleResolver localeResolver() {
		var localeResolver = new CustomLocaleResolver(Arrays.asList(Locale.forLanguageTag("ca"), Locale.forLanguageTag("es")));
		localeResolver.setDefaultLocale(Locale.forLanguageTag("ca"));
		return localeResolver;
	}

	@Bean
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
			"/**/monitor/tasques"};
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

	public static class CustomPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolverSupport implements PageableArgumentResolver {
		private final SortArgumentResolver sortResolver = new SortHandlerMethodArgumentResolver();
		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return Pageable.class.equals(parameter.getParameterType());
		}
		@Override
		public Pageable resolveArgument(
				MethodParameter methodParameter,
				@Nullable ModelAndViewContainer mavContainer,
				NativeWebRequest webRequest,
				@Nullable WebDataBinderFactory binderFactory) {
			String page = webRequest.getParameter(getParameterNameToUse(getPageParameterName(), methodParameter));
			String pageSize = webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));
			Sort sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
			boolean withPageOrSort = page != null || pageSize != null || sort.isSorted();
			if (!withPageOrSort) {
				return null;
			} else {
				Pageable pageable = getPageable(
						methodParameter,
						page == null ? "0" : page,
						pageSize == null || "0".equals(pageSize) ? "10" : pageSize);
				if (sort.isSorted()) {
					return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
				}
				return pageable;
			}
		}
	}

}
