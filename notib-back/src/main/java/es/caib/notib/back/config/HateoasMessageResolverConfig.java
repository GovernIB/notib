package es.caib.notib.back.config;

import es.caib.notib.back.base.config.BaseHateoasMessageResolverConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * Configuració del MessageResolver per a spring-hateoas.
 * 
 * @author Límit Tecnologies
 */
@Configuration
public class HateoasMessageResolverConfig extends BaseHateoasMessageResolverConfig {

	@Override
	protected String getBasename() {
		return "notib-back-rest-messages";
	}

    @Override
    protected Locale getDefaultLocale() {
        return Locale.forLanguageTag(BaseConfig.DEFAULT_LOCALE);
    }
}
