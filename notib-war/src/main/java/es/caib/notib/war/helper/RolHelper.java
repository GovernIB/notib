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

	private static final String ROLE_ADMIN = "NOT_ADMIN";
	private static final String ROLE_REPRESENTANT = "NOT_REP";
	private static final String ROLE_APLICACIO = "NOT_APL";

	private static final String REQUEST_PARAMETER_CANVI_ROL = "canviRol";
	private static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";
	
	public static boolean teRolAdministrador(HttpServletRequest request) {
		return request.isUserInRole(ROLE_ADMIN);
	}
	
	public static boolean teRolRepresentant(HttpServletRequest request) {
		return request.isUserInRole(ROLE_REPRESENTANT);
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
			if (request.isUserInRole(ROLE_ADMIN) && rolsDisponibles.contains(ROLE_ADMIN)) {
				rolActual = ROLE_ADMIN;
			} else if (request.isUserInRole(ROLE_REPRESENTANT) && rolsDisponibles.contains(ROLE_REPRESENTANT)) {
				rolActual = ROLE_REPRESENTANT;
			} else if (request.isUserInRole(ROLE_APLICACIO) && rolsDisponibles.contains(ROLE_APLICACIO)) {
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
		return ROLE_ADMIN.equals(getRolActual(request));
	}
	public static boolean isUsuariActualRepresentant( HttpServletRequest request ) {
		return ROLE_REPRESENTANT.equals(getRolActual(request));
	}
	public static boolean isUsuariActualAplicacio( HttpServletRequest request ) {
		return ROLE_APLICACIO.equals(getRolActual(request));
	}

	public static List<String> getRolsUsuariActual(HttpServletRequest request) {
		LOGGER.debug("Obtenint rols disponibles per a l'usuari actual");
		List<String> rols = new ArrayList<String>();
		if (request.isUserInRole(ROLE_ADMIN)) {
			rols.add(ROLE_ADMIN);
		}
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual != null) {
			if (entitatActual.isUsuariActualRepresentant() && request.isUserInRole(ROLE_REPRESENTANT))
				rols.add(ROLE_REPRESENTANT);
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
