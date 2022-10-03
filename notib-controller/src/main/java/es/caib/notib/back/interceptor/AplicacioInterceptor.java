/**
 * 
 */
package es.caib.notib.back.interceptor;

import es.caib.notib.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AplicacioInterceptor implements AsyncHandlerInterceptor {

	public static final String REQUEST_ATTRIBUTE_MANIFEST_ATRIBUTES = "manifestAtributes";
	private static Map<String, Object> manifestAtributsMap;

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ServletContext servletContext;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		//AplicacioHelper.comprovarVersioActual(request, aplicacioService);
		if (manifestAtributsMap == null) {
			InputStream is = servletContext.getResourceAsStream("/" + JarFile.MANIFEST_NAME);
			if (is != null) {
				Manifest manifest = new Manifest(is);
				Attributes manifestAtributs = manifest.getMainAttributes();
				manifestAtributsMap = new HashMap<String, Object>();
				for (Object key : new HashMap(manifestAtributs).keySet()) {
					manifestAtributsMap.put(key.toString(), manifestAtributs.get(key));
				}
				aplicacioService.setAppVersion((String) manifestAtributsMap.get("Implementation-Version"));
				Locale locale = new Locale(RequestContextUtils.getLocale(request).getLanguage(),
						RequestContextUtils.getLocale(request).getCountry());
				manifestAtributsMap.put(
						"Build-Timestamp",
						formatBuildTimestamp(
								manifestAtributsMap.get("Build-Timestamp").toString(), locale));
			}
		}
		request.setAttribute(
				REQUEST_ATTRIBUTE_MANIFEST_ATRIBUTES,
				manifestAtributsMap);
		request.setAttribute(
				"requestLocale",
				RequestContextUtils.getLocale(request).getLanguage());
		return true;
	}

	private String formatBuildTimestamp(String timestamp, Locale locale) throws ParseException {
		String ISO_DATE_FORMAT_ZERO_OFFSET = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT_ZERO_OFFSET);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY.MM.dd HH:mm", locale);
		Date date = simpleDateFormat.parse(timestamp);
		return dateFormat.format(date) + "h";
	}

}
