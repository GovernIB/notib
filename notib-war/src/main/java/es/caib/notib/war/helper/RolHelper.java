/**
 * 
 */
package es.caib.notib.war.helper;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.api.service.AplicacioService;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilitat per a gestionar el canvi de rol de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolHelper {

	public static final String ROLE_SUPER = RolEnumDto.NOT_SUPER.name(); 				// "NOT_SUPER";
	public static final String ROLE_ADMIN_ENTITAT = RolEnumDto.NOT_ADMIN.name(); 		// "NOT_ADMIN";
	public static final String ROLE_USUARI = RolEnumDto.tothom.name(); 				// "tothom";
	public static final String ROLE_APLICACIO = RolEnumDto.NOT_APL.name(); 			// "NOT_APL";
	public static final String ROLE_ADMIN_ORGAN = RolEnumDto.NOT_ADMIN_ORGAN.name(); 	// "NOT_ADMIN_ORGAN";

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
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		String canviRol = request.getParameter(REQUEST_PARAMETER_CANVI_ROL);
		if (canviRol != null && canviRol.length() > 0) {
			LOGGER.debug("Processant canvi rol (rol=" + canviRol + ")");
			if (request.isUserInRole(canviRol) || 
				(RolEnumDto.NOT_ADMIN_ORGAN.name().equals(canviRol) && 
					request.isUserInRole(RolEnumDto.tothom.name()) &&
					(boolean)request.getAttribute("permisAdminOrgan"))) {
				updateUltimRol(request, aplicacioService, canviRol);
			}
		}
	}
	
	private static void updateUltimRol(
			HttpServletRequest request,
			AplicacioService aplicacioService,
			String rol) {
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL,
				rol);
		if (aplicacioService != null)
			aplicacioService.updateRolUsuariActual(rol);
	}

	public static String getRolActual(HttpServletRequest request) {
		return getRolActual(request, null);
	}
	
	public static String getRolActual(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		if (rolActual == null && aplicacioService != null) {
			rolActual = aplicacioService.getUsuariActual().getUltimRol();
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_ROL_ACTUAL,
					rolActual);
		}
		List<String> rolsDisponibles = getRolsUsuariActual(request);
		if (rolActual == null || !rolsDisponibles.contains(rolActual)) {
			if (request.isUserInRole(ROLE_USUARI) && rolsDisponibles.contains(ROLE_USUARI)) {
				rolActual = ROLE_USUARI;
			} else if (request.isUserInRole(ROLE_ADMIN_ENTITAT) && rolsDisponibles.contains(ROLE_ADMIN_ENTITAT)) {
				rolActual = ROLE_ADMIN_ENTITAT;
			} else if (request.isUserInRole(ROLE_SUPER) && rolsDisponibles.contains(ROLE_SUPER)) {
				rolActual = ROLE_SUPER; 
			} else if (request.isUserInRole(ROLE_APLICACIO) && rolsDisponibles.contains(ROLE_APLICACIO)) {
				rolActual = ROLE_APLICACIO;
			}
			if (rolActual != null)
				updateUltimRol(request, aplicacioService, rolActual);
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
	public static boolean isUsuariActualAplicacio(HttpServletRequest request) {
		return ROLE_APLICACIO.equals(getRolActual(request));
	}
	public static boolean isUsuariActualUsuariAdministradorOrgan(HttpServletRequest request) {
		return ROLE_ADMIN_ORGAN.equals(getRolActual(request));
	}

	public static List<String> getRolsUsuariActual(@NonNull HttpServletRequest request) {
		LOGGER.debug("Obtenint rols disponibles per a l'usuari actual");
		List<String> rols = new ArrayList<String>();
		boolean permisUsuariSobreEntitat = request.isUserInRole(ROLE_USUARI);
		boolean permisAdminSobreEntitat = request.isUserInRole(ROLE_ADMIN_ENTITAT);
		boolean permisAplicacioSobreEntitat = request.isUserInRole(ROLE_APLICACIO);
		boolean permisAdminSobreOrgan = false;
		
		if (request.getAttribute("permisUsuariEntitat") != null) 
			permisUsuariSobreEntitat = (boolean) request.getAttribute("permisUsuariEntitat");
		if (request.getAttribute("permisAdminEntitat") != null)
			permisAdminSobreEntitat = (boolean) request.getAttribute("permisAdminEntitat");
		if (request.getAttribute("permisAplicacioEntitat") != null)
			permisAplicacioSobreEntitat = (boolean) request.getAttribute("permisAplicacioEntitat");
		if (request.getAttribute("permisAdminOrgan") != null) {
			permisAdminSobreOrgan = (boolean) request.getAttribute("permisAdminOrgan");
		}
		
		if (request.isUserInRole(ROLE_SUPER)) {
			rols.add(ROLE_SUPER);
		}
		if (request.isUserInRole(ROLE_USUARI) && permisUsuariSobreEntitat) {
			rols.add(ROLE_USUARI);
		}
		if (request.isUserInRole(ROLE_APLICACIO) && permisAplicacioSobreEntitat) {
			rols.add(ROLE_APLICACIO);
		}
		if (request.isUserInRole(ROLE_USUARI) && permisAdminSobreOrgan) {
			rols.add(ROLE_ADMIN_ORGAN);
		}
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual != null) {
			if (entitatActual.isUsuariActualAdministradorEntitat() && request.isUserInRole(ROLE_ADMIN_ENTITAT) && permisAdminSobreEntitat)
				rols.add(ROLE_ADMIN_ENTITAT);
		} else {
			if (request.isUserInRole(ROLE_ADMIN_ENTITAT) && permisAdminSobreEntitat)
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
