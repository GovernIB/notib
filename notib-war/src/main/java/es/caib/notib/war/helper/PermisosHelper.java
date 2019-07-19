/**
 * 
 */
package es.caib.notib.war.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.ProcedimentService;

/**
 * Utilitat per a gestionar els permisos de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosHelper {

	
	public static void comprovarPermisosProcedimentsUsuariActual(
			HttpServletRequest request,
			ProcedimentService procedimentService,
			AplicacioService aplicacioService) { 
		
		if (RolHelper.isUsuariActualUsuari(request)) {
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
			EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
			List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());
			
			List<ProcedimentGrupDto> grupsProcediment = procedimentService.findAllGrups();
			List<ProcedimentDto> procediments = new ArrayList<ProcedimentDto>();
			//Obté els procediments que tenen el mateix grup que el rol d'usuari
			for (ProcedimentGrupDto grupProcediment : grupsProcediment) {
				
				for (String rol : rolsUsuariActual) {
					if(rol.contains((grupProcediment.getGrup().getCodi()))) {
						if ((grupProcediment.getProcediment().getEntitat().getDir3Codi().equals(entitatActual.getDir3Codi()))) {
							procediments.add(grupProcediment.getProcediment());
						}
					}
				}
			}
			//Comprova quins permisos té aquest usuari sobre els procediments amb grups
			if(!procediments.isEmpty()) {
//				request.setAttribute(
//						"permisConsulta", 
//						procedimentService.hasGrupPermisConsultaProcediment(
//								procediments,
//								entitatActual));
				request.setAttribute(
						"permisNotificacio", 
						procedimentService.hasGrupPermisNotificacioProcediment(
								procediments,
								entitatActual));
			}
			//Comprova quins permisos té aquest usuari sobre els procediments sense grups
			if ((!grupsProcediment.isEmpty() && procediments.isEmpty()) || (grupsProcediment.isEmpty())) {
//				request.setAttribute(
//						"permisConsulta", 
//						procedimentService.hasPermisConsultaProcediment(entitatActual));
				request.setAttribute(
						"permisNotificacio", 
						procedimentService.hasPermisNotificacioProcediment(entitatActual));
			}
		}
	}
	
	public static void comprovarPermisosEntitatsUsuariActual(
			HttpServletRequest request,
			EntitatService entitatService) {
		//Comprovar si té permisos sobre alguna entitat
		request.setAttribute(
				"permisUsuariEntitat", 
				entitatService.hasPermisUsuariEntitat());
		request.setAttribute(
				"permisAdminEntitat", 
				entitatService.hasPermisAdminEntitat());
		request.setAttribute(
				"permisAplicacioEntitat", 
				entitatService.hasPermisAplicacioEntitat());

	}
}
