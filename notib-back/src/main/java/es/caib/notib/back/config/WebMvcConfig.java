/**
 * 
 */
package es.caib.notib.back.config;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import es.caib.notib.back.interceptor.AccesAdminInterceptor;
import es.caib.notib.back.interceptor.AccesPagadorsInterceptor;
import es.caib.notib.back.interceptor.AccesSuperInterceptor;
import es.caib.notib.back.interceptor.AccesUsuariInterceptor;
import es.caib.notib.back.interceptor.NotibInterceptor;
import es.caib.notib.back.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.util.List;

/**
 * Configuració de Spring web MVC.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
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

	@Bean
	public ViewResolver internalResourceViewResolver() {

		var bean = new InternalResourceViewResolver();
		bean.setViewClass(JstlView.class);
		bean.setPrefix("/WEB-INF/jsp/");
		bean.setSuffix(".jsp");
		return bean;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {

		var lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	@Bean
	public FilterRegistrationBean<SiteMeshFilter> sitemeshFilter() {

		var  registrationBean = new FilterRegistrationBean<SiteMeshFilter>();
		registrationBean.setFilter(new SiteMeshFilter());
		registrationBean.addUrlPatterns("*");
		return registrationBean;
	}

	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(10485760);
		return multipartResolver;
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
	private static final String[] ADMIN_PATHS = { "/organgestor**", "/organgestor/**", "/procediment**", "/procediment/**", "/servei**", "/servei/**", "/grup**", "/grup/**", "/massiu/registre/notificacionsError", "/massiu/registre/notificacionsError/**" };
	private static final String[] SUPER_PATHS = { "/avis**", "/avis/**", "/cache**", "/cache/**", "/config**", "/config/**", "/metrics", "/metrics/list", "/monitor", "/monitor/all", "/excepcio", "/excepcio/**", "/integracio", "/integracio/**", "/notificacio/refrescarEstatNotifica", "/notificacio/refrescarEstatNotifica/**", "/massiu/notificacions**", "/massiu/notificacions/**" };
	private static final String[] USUARI_PATHS = { "/entitat**", "/entitat/**" };
	private static final String[] USUARI_EXCLUSIONS = { "/entitat/organigrama/**", "/entitat/getEntitatLogoCap", "/entitat/getEntitatLogoPeu" };

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(notibInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS).order(0);
//		registry.addInterceptor(sessionInterceptor).excludePathPatterns(ALL_EXCLUSIONS).order(1);
		registry.addInterceptor(accesSuperInterceptor).addPathPatterns(SUPER_PATHS).order(2);
		registry.addInterceptor(accesAdminInterceptor).addPathPatterns(ADMIN_PATHS).order(3);
		registry.addInterceptor(accesUsuariInterceptor).addPathPatterns(USUARI_PATHS).excludePathPatterns(USUARI_EXCLUSIONS).order(4);
		registry.addInterceptor(accesPagadorsInterceptor).addPathPatterns(PAGADORS_PATHS).order(5);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

		var resolver = new CustomPageableHandlerMethodArgumentResolver();
		resolver.setFallbackPageable(Pageable.unpaged());
		resolvers.add(resolver);
		WebMvcConfigurer.super.addArgumentResolvers(resolvers);
	}

	public static class CustomPageableHandlerMethodArgumentResolver extends PageableHandlerMethodArgumentResolverSupport implements PageableArgumentResolver {
		private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new SortHandlerMethodArgumentResolver();
		private SortArgumentResolver sortResolver;
		public CustomPageableHandlerMethodArgumentResolver() {
			this((SortArgumentResolver) null);
		}
		public CustomPageableHandlerMethodArgumentResolver(SortHandlerMethodArgumentResolver sortResolver) {
			this((SortArgumentResolver) sortResolver);
		}
		public CustomPageableHandlerMethodArgumentResolver(@Nullable SortArgumentResolver sortResolver) {
			this.sortResolver = sortResolver == null ? DEFAULT_SORT_RESOLVER : sortResolver;
		}
		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return Pageable.class.equals(parameter.getParameterType());
		}
		@Override
		public Pageable resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {

			var page = webRequest.getParameter(getParameterNameToUse(getPageParameterName(), methodParameter));
			var pageSize = webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));
			var sort = sortResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
			var pageable = getPageable(methodParameter, page, pageSize);
			if (pageable.isPaged() && sort.isSorted()) {
				return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
			}
			return pageable;
		}
	}

}
