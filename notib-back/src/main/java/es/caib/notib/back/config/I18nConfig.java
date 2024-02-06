/**
 * 
 */
package es.caib.notib.back.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Configuració multiidioma de l'aplicació.
 * 
 * @author Límit Tecnologies
 */
@Configuration
public class I18nConfig {

	@Bean
//	public LocaleResolver localeResolver() {
//		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
//		return localeResolver;
//	}
	public LocaleResolver localeResolver() {

		var localeResolver = new CustomLocaleResolver(Arrays.asList(Locale.forLanguageTag("ca"), Locale.forLanguageTag("es")));
		localeResolver.setDefaultLocale(Locale.forLanguageTag("ca"));
		return localeResolver;
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultLocale(Locale.forLanguageTag("ca"));
		messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
		messageSource.setFallbackToSystemLocale(false);
		return messageSource;
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
