/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.Locale;

import es.caib.notib.core.api.dto.UsuariDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.service.AplicacioService;

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
		String msg = "???" + (keys.length > 0 ? keys[keys.length-1] : "") + "???";
		boolean found = false;
		int i = 0;
		while( ! found && i < keys.length) {		
			try {
				msg = messageSource.getMessage(
						keys[i],
						vars,
						locale);
				found = true;
			} catch (NoSuchMessageException ex) {
				i++;
			}
		}
		if( ! found ) {
			String key = keys[keys.length-1]; 
			if (key.startsWith("enum.")){
				msg = key.substring(key.lastIndexOf(".") + 1);
			}			
		}
		return msg;
	}
	public String getMessage(String key, Object[] vars, Locale locale) {
		try {
			if (locale == null) {
				String lang = "ca";
				UsuariDto usuariActual = aplicacioService.getUsuariActual();
				if (usuariActual != null && usuariActual.getIdioma() != null && !usuariActual.getIdioma().isEmpty())
					lang = usuariActual.getIdioma();
				locale = new Locale(lang);
			}
			return messageSource.getMessage(
					key,
					vars,
					locale);
		} catch (NoSuchMessageException ex) {
			if (key.startsWith("enum.")){
				return key.substring(key.lastIndexOf(".") + 1);
			}
			return "???" + key + "???";
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

//	public void setMessageSource(MessageSource messageSource) {
//		INSTANCE.messageSource = messageSource;
//	}
//
//	public static MessageHelper INSTANCE = new MessageHelper();
//
//	public static MessageHelper getInstance() {
//		return INSTANCE;
//	}

}