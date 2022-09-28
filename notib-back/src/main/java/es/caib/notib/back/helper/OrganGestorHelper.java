/**
 * 
 */
package es.caib.notib.back.helper;

import es.caib.notib.logic.intf.dto.EntitatDto;
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

	public static List<OrganGestorDto> getOrgansGestorsUsuariActual(
			HttpServletRequest request) {
		return getOrgansGestorsUsuariActual(request, null);
	}
	
	@SuppressWarnings("unchecked")
	public static List<OrganGestorDto> getOrgansGestorsUsuariActual(
			HttpServletRequest request,
			OrganGestorService organGestorService) {
		List<OrganGestorDto> organsGestorsUsuariActual = null; 
		if (organGestorService == null) {
			organsGestorsUsuariActual = (List<OrganGestorDto>)request.getSession().getAttribute(ORGANS_ACCESSIBLES);
		} else {
			String rolActual = RolHelper.getRolActual(request);
			if (rolActual != null && RolEnumDto.NOT_ADMIN_ORGAN.name().equals(rolActual)) {
				organsGestorsUsuariActual = organGestorService.findAccessiblesByUsuariActual();
				request.getSession().setAttribute(
						ORGANS_ACCESSIBLES,
						organsGestorsUsuariActual);
			}
		}
		
		if (organsGestorsUsuariActual != null) {
			String canviOrgan = request.getParameter(REQUEST_PARAMETER_CANVI_ORGAN);
			if (canviOrgan != null && !canviOrgan.isEmpty()) {
				setOrganGestorUsuariActual(request, canviOrgan);
			} else if (request.getSession().getAttribute(ORGAN_ACTUAL) == null && organsGestorsUsuariActual != null && !organsGestorsUsuariActual.isEmpty()) { 
				request.getSession().setAttribute(
						ORGAN_ACTUAL,
						organsGestorsUsuariActual.get(0));
			}
		}
		return organsGestorsUsuariActual;
	}
	
	@SuppressWarnings("unchecked")
	public static void setOrganGestorUsuariActual(
			HttpServletRequest request, 
			String strOrganId) {
		if (request.getSession().getAttribute(ORGANS_ACCESSIBLES) != null) {
			List<OrganGestorDto> organsGestorsUsuariActual = (List<OrganGestorDto>)request.getSession().getAttribute(ORGANS_ACCESSIBLES);
			if (!organsGestorsUsuariActual.isEmpty()) {
				Long organId = null;
				try {
					organId = Long.parseLong(strOrganId);
				} catch (Exception e) {}
				if(organId != null) {
					for (OrganGestorDto organGestor: organsGestorsUsuariActual) {
						if (organGestor.getId().equals(organId)) {
							request.getSession().setAttribute(
									ORGAN_ACTUAL,
									organGestor);
							break;
						}
							
					}
				} else {
					request.getSession().setAttribute(
							ORGAN_ACTUAL,
							organsGestorsUsuariActual.get(0));
				}
			}
		}
	}
	
	public static OrganGestorDto getOrganGestorUsuariActual(HttpServletRequest request) {
		OrganGestorDto organGestorActual = null;
		if (request.getSession().getAttribute(ORGAN_ACTUAL) != null) {
			organGestorActual = (OrganGestorDto)request.getSession().getAttribute(ORGAN_ACTUAL);
		}
		return organGestorActual;
	}
	
	public static String getRequestParameterCanviOrgan() {
		return REQUEST_PARAMETER_CANVI_ORGAN;
	}

	public static void setOrgansProcedimentsNoSincronitzats(HttpServletRequest request, ProcedimentService procedimentService) {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual != null && procedimentService != null)
			request.getSession().setAttribute(
					OrganGestorHelper.SESSION_ATTRIBUTE_ORGANS_PROC_NO_SYNC,
					procedimentService.getProcedimentsAmbOrganNoSincronitzat(entitatActual.getId()));
	}

	public static void setOrgansServeisNoSincronitzats(HttpServletRequest request, ServeiService serveiService) {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual != null && serveiService != null)
			request.getSession().setAttribute(
					OrganGestorHelper.SESSION_ATTRIBUTE_ORGANS_SERV_NO_SYNC,
					serveiService.getServeisAmbOrganNoSincronitzat(entitatActual.getId()));
	}

	public static Integer getOrgansProcNoSincronitzats(HttpServletRequest request) {
		Integer organsNoSincronitzats = (Integer) request.getSession().getAttribute(SESSION_ATTRIBUTE_ORGANS_PROC_NO_SYNC);
		return organsNoSincronitzats != null ? organsNoSincronitzats : 0;
	}

	public static Integer getOrgansServNoSincronitzats(HttpServletRequest request) {
		Integer organsNoSincronitzats = (Integer) request.getSession().getAttribute(SESSION_ATTRIBUTE_ORGANS_SERV_NO_SYNC);
		return organsNoSincronitzats != null ? organsNoSincronitzats : 0;
	}



//	private static final Logger LOGGER = LoggerFactory.getLogger(OrganGestorHelper.class);

}
