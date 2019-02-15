/**
 * 
 */
package es.caib.notib.war.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.helper.EntityComprovarHelper;

/**
 * Utilitat per a gestionar els permisos de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PermisosHelper {

	
	public static void comprovarPermisosUsuariActual(
			HttpServletRequest request,
			ProcedimentService procedimentService,
			AplicacioService aplicacioService,
			EntityComprovarHelper entityComprovarHelper) { 
		
		if (RolHelper.isUsuariActualUsuari(request)) {
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
			List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());
			
			List<ProcedimentGrupDto> grupsProcediment = procedimentService.findAllGrups();
			List<ProcedimentDto> procediments = new ArrayList<ProcedimentDto>();
			//Obté els procediments que tenen el mateix grup que el rol d'usuari
			for (ProcedimentGrupDto grupProcediment : grupsProcediment) {
				
				for (String rol : rolsUsuariActual) {
					if(rol.contains((grupProcediment.getGrup().getCodi()))) {
						procediments.add(grupProcediment.getProcediment());
					}
				}
			}
			//Comprova quins permisos té aquest usuari sobre els procediments amb grups
			if(!procediments.isEmpty()) {
				request.setAttribute(
						"permisConsulta", 
						entityComprovarHelper.hasGrupPermisConsultaProcediment(procediments));
				request.setAttribute(
						"permisNotificacio", 
						entityComprovarHelper.hasGrupPermisNotificacioProcediment(procediments));
			}
			//Comprova quins permisos té aquest usuari sobre els procediments sense grups
			if (grupsProcediment.isEmpty()) {
				request.setAttribute(
						"permisConsulta", 
						entityComprovarHelper.hasPermisConsultaProcediment());
				request.setAttribute(
						"permisNotificacio", 
						entityComprovarHelper.hasPermisNotificacioProcediment());
			}
		}
	}
	private static final Logger LOGGER = LoggerFactory.getLogger(PermisosHelper.class);

}
