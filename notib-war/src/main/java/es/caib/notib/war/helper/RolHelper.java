/**
 * 
 */
package es.caib.notib.war.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.dto.EntitatDto;

/**
 * Utilitat per a gestionar el canvi de rol de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolHelper {

	private static final String ROLE_SUPER = "NOT_SUPER";
	private static final String ROLE_ADMIN_ENTITAT = "NOT_ADMIN";
	private static final String ROLE_USUARI = "NOT_USER";
	private static final String ROLE_APLICACIO = "NOT_APL";

	private static final String REQUEST_PARAMETER_CANVI_ROL = "canviRol";
	private static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";
	
	public static boolean teRolAdministrador(HttpServletRequest request) {
		return request.isUserInRole(ROLE_SUPER);
	}
	
	public static boolean teRolAdministradorEntitat(HttpServletRequest request) {
		return request.isUserInRole(ROLE_ADMIN_ENTITAT);
	}
	public static boolean teRolUsuari(HttpServletRequest request) {
		return request.isUserInRole(ROLE_USUARI);
	}
	public static boolean teRolAplicacio(HttpServletRequest request) {
		return request.isUserInRole(ROLE_APLICACIO);
	}

	public static void processarCanviRols(
			HttpServletRequest request) {
		String canviRol = request.getParameter(REQUEST_PARAMETER_CANVI_ROL);
		if (canviRol != null && canviRol.length() > 0) {
			LOGGER.debug("Processant canvi rol (rol=" + canviRol + ")");
			if (request.isUserInRole(canviRol)) {
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_ROL_ACTUAL,
						canviRol);
			}
		}
	}

	public static String getRolActual(HttpServletRequest request) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		List<String> rolsDisponibles = getRolsUsuariActual(request);
		if (rolActual == null || !rolsDisponibles.contains(rolActual)) {
			if (request.isUserInRole(ROLE_SUPER) && rolsDisponibles.contains(ROLE_SUPER)) {
				rolActual = ROLE_SUPER;
			}else if (request.isUserInRole(ROLE_ADMIN_ENTITAT) && rolsDisponibles.contains(ROLE_ADMIN_ENTITAT)) {
				rolActual = ROLE_ADMIN_ENTITAT;
			}else if (request.isUserInRole(ROLE_USUARI) && rolsDisponibles.contains(ROLE_USUARI)) {
				rolActual = ROLE_USUARI;
			}else if (request.isUserInRole(ROLE_APLICACIO) && rolsDisponibles.contains(ROLE_APLICACIO)) {
				rolActual = ROLE_APLICACIO;
			}
			if (rolActual != null)
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_ROL_ACTUAL,
						rolActual);
		}
		LOGGER.debug("Obtenint rol actual (rol=" + rolActual + ")");
		return rolActual;
	}

	public static boolean isUsuariActualAdministrador(HttpServletRequest request) {
		return ROLE_SUPER.equals(getRolActual(request));
	}
	public static boolean isUsuariActualAdministradorEntitat( HttpServletRequest request ) {
		return ROLE_ADMIN_ENTITAT.equals(getRolActual(request));
	}
	public static boolean isUsuariActualUsuari(HttpServletRequest request) {
		return ROLE_USUARI.equals(getRolActual(request));
	}
	public static boolean isUsuariActualAplicacio( HttpServletRequest request ) {
		return ROLE_APLICACIO.equals(getRolActual(request));
	}

	public static List<String> getRolsUsuariActual(HttpServletRequest request) {
		LOGGER.debug("Obtenint rols disponibles per a l'usuari actual");
		List<String> rols = new ArrayList<String>();
		if (request.isUserInRole(ROLE_SUPER)) {
			rols.add(ROLE_SUPER);
		}
		if (request.isUserInRole(ROLE_USUARI)) {
			rols.add(ROLE_USUARI);
		}
		if (request.isUserInRole(ROLE_APLICACIO)) {
			rols.add(ROLE_APLICACIO);
		}
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual != null) {
			if (entitatActual.isUsuariActualAdministradorEntitat() && request.isUserInRole(ROLE_ADMIN_ENTITAT))
				rols.add(ROLE_ADMIN_ENTITAT);
		}
		return rols;
	}

	public static void esborrarRolActual(HttpServletRequest request) {
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
	}

	public static String getRequestParameterCanviRol() {
		return REQUEST_PARAMETER_CANVI_ROL;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RolHelper.class);

}
