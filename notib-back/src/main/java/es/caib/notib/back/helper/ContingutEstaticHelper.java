/**
 * 
 */
package es.caib.notib.back.helper;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilitat per a verificar si una petició HTTP correspon a
 * contingut estàtic.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutEstaticHelper {

	private ContingutEstaticHelper() {
		throw new IllegalStateException("ContingutEstaticHelper no es pot instanciar");
	}

	public static boolean isContingutEstatic(HttpServletRequest request) {

		var uri = request.getRequestURI();
		var path = uri.substring(request.getContextPath().length());
		for (var pce : pathsContingutEstatic) {
			if (path.startsWith(pce)) {
				return true;
			}
		}
		return false;
	}

	private static final String[] pathsContingutEstatic = {"/css/", "/font/", "/img/", "/js/"};

}
