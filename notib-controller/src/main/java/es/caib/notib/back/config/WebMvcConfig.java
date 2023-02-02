/**
 * 
 */
package es.caib.notib.back.config;

import com.opensymphony.sitemesh.webapp.SiteMeshFilter;
import es.caib.notib.back.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
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
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Configuració de Spring web MVC.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private AplicacioInterceptor aplicacioInterceptor;
	@Autowired
	private PermisosEntitatInterceptor permisosEntitatInterceptor;
	@Autowired
	private SessioInterceptor sessioInterceptor;
	@Autowired
	private LlistaRolsInterceptor llistaRolsInterceptor;
	@Autowired
	private LlistaEntitatsInterceptor llistaEntitatsInterceptor;
	@Autowired
	private ModalInterceptor modalInterceptor;
	@Autowired
	private NodecoInterceptor nodecoInterceptor;
	@Autowired
	private AjaxInterceptor ajaxInterceptor;
	@Autowired
	private PermisosInterceptor permisosInterceptor;
	@Autowired
	private AccesPagadorsInterceptor accesPagadorsInterceptor;
	@Autowired
	private AccesAdminInterceptor accesAdminInterceptor;
	@Autowired
	private AccesSuperInterceptor accesSuperInterceptor;
	@Autowired
	private AccesUsuariInterceptor accesUsuariInterceptor;
	@Autowired
	private AvisosInterceptor avisosInterceptor;

	@Bean
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
	}

//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/webjars/**")
//				.addResourceLocations("/webjars/", "/META-INF/resources/webjars/" , "classpath:/META-INF/resources/webjars/")
//				.resourceChain(false)
//				.addResolver(new WebJarsResourceResolver());
//		registry.setOrder(1);
//	}

//	@Override
//	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//		configurer.enable("notibServlet");
//	}

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

	private static final String[] PERMISOS_INTERCEPTOR_EXCLUSIONS = { "/js/**", "/css/**", "/fonts/**", "/img/**", "/images/**", "/extensions/**", "/webjars/**", "/error", "/api/consulta/**", "/api/consulta/**" };
	private static final String[] APLICACIO_INTERCEPTOR_EXCLUSIONS = { "/js/**", "/css/**", "/fonts/**", "/img/**", "/images/**", "/extensions/**", "/webjars/**", "/**/datatable/**", "/**/selection/**", "/api/rest/**", "/api/apidoc**", "/api-docs/**", "/**/api-docs/", "/api/consulta/**", "/notificacio/refrescarEstatNotifica/estat" };
	private static final String[] INTERCEPTOR_EXCLUSIONS = { "/js/**", "/css/**", "/fonts/**", "/img/**", "/images/**", "/extensions/**", "/webjars/**", "/**/datatable/**", "/**/selection/**", "/api/rest/**", "/api/apidoc**", "/api-docs/**", "/**/api-docs/", "/api/consulta/**", "/error", "/notificacio/refrescarEstatNotifica/estat"	};
	private static final String[] USUARI_EXCLUSIONS = { "/entitat/organigrama/**", "/entitat/getEntitatLogoCap", "/entitat/getEntitatLogoPeu" };

	private static final String[] PAGADORS_PATHS = { "/cie**", "/cie/**", "/operadorPostal**", "/operadorPostal/**" };
	private static final String[] ADMIN_PATHS = { "/organgestor**", "/organgestor/**", "/procediment**", "/procediment/**", "/servei**", "/servei/**", "/grup**", "/grup/**", "/massiu/registre/notificacionsError", "/massiu/registre/notificacionsError/**" };
	private static final String[] SUPER_PATHS = { "/avis**", "/avis/**", "/cache**", "/cache/**", "/config**", "/config/**", "/metrics", "/metrics/list", "/monitor", "/monitor/all", "/excepcio", "/excepcio/**", "/integracio", "/integracio/**", "/notificacio/refrescarEstatNotifica", "/notificacio/refrescarEstatNotifica/**", "/massiu/notificacions**", "/massiu/notificacions/**" };
	private static final String[] USUARI_PATHS = { "/entitat**", "/entitat/**" };

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(aplicacioInterceptor).excludePathPatterns(APLICACIO_INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(permisosEntitatInterceptor).excludePathPatterns(PERMISOS_INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(sessioInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(llistaRolsInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(llistaEntitatsInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(modalInterceptor).excludePathPatterns(APLICACIO_INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(nodecoInterceptor).excludePathPatterns(APLICACIO_INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(ajaxInterceptor).excludePathPatterns(APLICACIO_INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(permisosInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(accesPagadorsInterceptor).addPathPatterns(PAGADORS_PATHS);
		registry.addInterceptor(accesAdminInterceptor).addPathPatterns(ADMIN_PATHS);
		registry.addInterceptor(accesSuperInterceptor).addPathPatterns(SUPER_PATHS);
		registry.addInterceptor(accesUsuariInterceptor).addPathPatterns(USUARI_PATHS).excludePathPatterns(USUARI_EXCLUSIONS);
		registry.addInterceptor(avisosInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
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
