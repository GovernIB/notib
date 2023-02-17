/**
 * 
 */
package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Helper per a mostrar missatges multiidioma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class MessageHelper implements MessageSourceAware {

	private MessageSource messageSource;
	@Autowired
	private AplicacioService aplicacioService;

	public String getMessage(String[] keys, Object[] vars, Locale locale) {

		var msg = "???" + (keys.length > 0 ? keys[keys.length-1] : "") + "???";
		var found = false;
		var i = 0;
		while( ! found && i < keys.length) {		
			try {
				msg = messageSource.getMessage(keys[i], vars, locale);
				found = true;
			} catch (NoSuchMessageException ex) {
				i++;
			}
		}
		if (found) {
			return msg;
		}
		var key = keys[keys.length-1];
		if (key.startsWith("enum.")){
			msg = key.substring(key.lastIndexOf(".") + 1);
		}
		return msg;
	}

	public String getMessage(String key, Object[] vars, Locale locale) {

		try {
			if (locale != null) {
				return messageSource.getMessage(key, vars, locale);
			}
			String idioma = aplicacioService.getIdiomaUsuariActual();
			locale = new Locale(!Strings.isNullOrEmpty(idioma) ? idioma : "ca");
			return messageSource.getMessage(key, vars, locale);
		} catch (NoSuchMessageException ex) {
			return key.startsWith("enum.") ? key.substring(key.lastIndexOf(".") + 1) :"???" + key + "???";
		}
	}
	public String getMessage(String key, Object[] vars) {
		return getMessage(key, vars, null);
	}
	public String getMessage(String key) {
		return getMessage(key, null, null);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
