/**
 * 
 */
package es.caib.notib.back.helper;

import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ServeiService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Utilitat per a gestionar les entitats de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrganGestorHelper {

	private static final String ORGANS_ACCESSIBLES= "organs.accessiblesUsuari";
	private static final String ORGAN_ACTUAL= "organs.actual";
	private static final String REQUEST_PARAMETER_CANVI_ORGAN= "canviOrgan";
	private static final String SESSION_ATTRIBUTE_ORGANS_PROC_NO_SYNC = "OrganGestorHelper.organsProcNoSincronitzats";
	private static final String SESSION_ATTRIBUTE_ORGANS_SERV_NO_SYNC = "OrganGestorHelper.organsServNoSincronitzats";

	public static List<OrganGestorDto> getOrgansGestorsUsuariActual(HttpServletRequest request) {
		return getOrgansGestorsUsuariActual(request, null);
	}
	
	@SuppressWarnings("unchecked")
	public static List<OrganGestorDto> getOrgansGestorsUsuariActual(HttpServletRequest request, OrganGestorService organGestorService) {

		List<OrganGestorDto> organsGestorsUsuariActual = null; 
		if (organGestorService == null) {
			organsGestorsUsuariActual = (List<OrganGestorDto>)request.getSession().getAttribute(ORGANS_ACCESSIBLES);
		} else {
			var rolActual = RolHelper.getRolActual(request);
			if (rolActual != null && RolEnumDto.NOT_ADMIN_ORGAN.name().equals(rolActual)) {
				organsGestorsUsuariActual = organGestorService.findAccessiblesByUsuariActual();
				request.getSession().setAttribute(ORGANS_ACCESSIBLES, organsGestorsUsuariActual);
			}
		}
		
		if (organsGestorsUsuariActual != null) {
			var canviOrgan = request.getParameter(REQUEST_PARAMETER_CANVI_ORGAN);
			if (canviOrgan != null && !canviOrgan.isEmpty()) {
				setOrganGestorUsuariActual(request, canviOrgan);
			} else if (request.getSession().getAttribute(ORGAN_ACTUAL) == null || organsGestorsUsuariActual != null && !organsGestorsUsuariActual.isEmpty()) {
				request.getSession().setAttribute(ORGAN_ACTUAL, organsGestorsUsuariActual.get(0));
			}
		}
		return organsGestorsUsuariActual;
	}
	
	@SuppressWarnings("unchecked")
	public static void setOrganGestorUsuariActual(HttpServletRequest request, String strOrganId) {

		if (request.getSession().getAttribute(ORGANS_ACCESSIBLES) == null) {
			return;
		}
		List<OrganGestorDto> organsGestorsUsuariActual = (List<OrganGestorDto>)request.getSession().getAttribute(ORGANS_ACCESSIBLES);
		if (organsGestorsUsuariActual.isEmpty()) {
			return;
		}
		Long organId = null;
		try {
			organId = Long.parseLong(strOrganId);
		} catch (Exception e) {

		}
		if(organId == null) {
			request.getSession().setAttribute(ORGAN_ACTUAL, organsGestorsUsuariActual.get(0));
			return;
		}
		for (var organGestor: organsGestorsUsuariActual) {
			if (organGestor.getId().equals(organId)) {
				request.getSession().setAttribute(ORGAN_ACTUAL, organGestor);
				break;
			}

		}
	}
	
	public static OrganGestorDto getOrganGestorUsuariActual(HttpServletRequest request) {
		return request.getSession().getAttribute(ORGAN_ACTUAL) != null ? (OrganGestorDto)request.getSession().getAttribute(ORGAN_ACTUAL) : null;
	}
	
	public static String getRequestParameterCanviOrgan() {
		return REQUEST_PARAMETER_CANVI_ORGAN;
	}

	public static void setOrgansProcedimentsNoSincronitzats(HttpServletRequest request, ProcedimentService procedimentService) {

		var entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual == null || procedimentService == null) {
			return;
		}
		var procs = procedimentService.getProcedimentsAmbOrganNoSincronitzat(entitatActual.getId());
		request.getSession().setAttribute(OrganGestorHelper.SESSION_ATTRIBUTE_ORGANS_PROC_NO_SYNC, procs);
	}

	public static void setOrgansServeisNoSincronitzats(HttpServletRequest request, ServeiService serveiService) {

		var entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual == null || serveiService == null) {
			return;
		}
		var serveis = serveiService.getServeisAmbOrganNoSincronitzat(entitatActual.getId());
		request.getSession().setAttribute(OrganGestorHelper.SESSION_ATTRIBUTE_ORGANS_SERV_NO_SYNC, serveis);
	}

	public static Integer getOrgansProcNoSincronitzats(HttpServletRequest request) {

		var organsNoSincronitzats = (Integer) request.getSession().getAttribute(SESSION_ATTRIBUTE_ORGANS_PROC_NO_SYNC);
		return organsNoSincronitzats != null ? organsNoSincronitzats : 0;
	}

	public static Integer getOrgansServNoSincronitzats(HttpServletRequest request) {

		var organsNoSincronitzats = (Integer) request.getSession().getAttribute(SESSION_ATTRIBUTE_ORGANS_SERV_NO_SYNC);
		return organsNoSincronitzats != null ? organsNoSincronitzats : 0;
	}

}
