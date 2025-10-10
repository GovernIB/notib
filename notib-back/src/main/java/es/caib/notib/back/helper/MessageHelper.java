/**
 * 
 */
package es.caib.notib.back.helper;

import es.caib.notib.back.config.scopedata.SessionScopedContext;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

/**
 * Helper per a mostrar missatges multiidioma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component("backMessageHelper")
public class MessageHelper implements MessageSourceAware {

	private MessageSource messageSource;

	public String getMessage(String[] keys, Object[] vars, Locale locale) {

		var msg = "???" + (keys.length > 0 ? keys[keys.length-1] : "") + "???";
		var found = false;
		var i = 0;
		while(!found && i < keys.length) {
			try {
				msg = messageSource.getMessage(keys[i], vars, locale);
				found = true;
			} catch (NoSuchMessageException ex) {
				i++;
			}
		}
		if(!found) {
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
//				var context = (SessionScopedContext) getLocale().getAttribute("sessionScopedContext");
//				locale = new Locale(context != null ? context.getIdiomaUsuari() : "ca");
				locale = getLocale();
			}
			return messageSource.getMessage(key, vars, locale);
		} catch (NoSuchMessageException ex) {
			return key.startsWith("enum.") ? key.substring(key.lastIndexOf(".") + 1) :"???" + key + "???";
		}
	}

	public static Locale getLocale() {

		var ca = "ca";
		var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			return new Locale(ca);
		}
		var attr = (SessionScopedContext) requestAttributes.getRequest().getSession().getAttribute("scopedTarget.sessionScopedContext");
		if (attr == null) {
			return new Locale(ca);
		}
		var idioma = attr.getIdiomaUsuari();
		return new Locale(StringUtils.isEmpty(idioma) ? idioma : ca);
//		((SessionScopedContext)(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession().getAttribute("scopedTarget.sessionScopedContext"))).getIdiomaUsuari();
//		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//		return attr.getRequest().getSession(true); // true == allow create
	}

	public String getMessage(String key, Object[] vars) {
		return getMessage(key, vars, null);
	}
	public String getMessage(String key) {

		return getMessage(key, null, null);
	}

	public void setMessageSource(@NotNull MessageSource messageSource) {
		INSTANCE.messageSource = messageSource;
	}

	private static MessageHelper INSTANCE = new MessageHelper();
	
	public static MessageHelper getInstance() {
		return INSTANCE;
	}
}
