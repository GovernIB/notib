/**
 * 
 */
package es.caib.notib.war.helper;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.ProcedimentService;

/**
 * Utilitat per a gestionar les entitats de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentHelper {

	public static boolean sensePermisos(
			HttpServletRequest request,
			EntitatDto entitatActual,
			AplicacioService aplicacioService,
			NotificacioService notificacioService,
			ProcedimentService procedimentService) {
		LOGGER.debug("Cercant procediments amb grups i/o permís de consulta");
		boolean sensePermis = false;
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());
		
		if (RolHelper.isUsuariActualUsuari(request)) {
			sensePermis = !procedimentService.hasAnyProcedimentsWithPermis(entitatActual.getId(), rolsUsuariActual, PermisEnum.CONSULTA);
		}	
		return sensePermis;
	}
	
	public static void setProcedimentsAndGrups(
			AplicacioService aplicacioService,
			ProcedimentService procedimentService,
			NotificacioService notificacioService,
			List<ProcedimentGrupDto> grupsProcediment,
			List<ProcedimentDto> procediments,
			EntitatDto entitatActual) {
		
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());

		procediments = procedimentService.findProcedimentsAmbGrupsWithPermis(entitatActual.getId(), rolsUsuariActual, PermisEnum.CONSULTA);
		procediments.addAll(procedimentService.findProcedimentsSenseGrupsWithPermis(entitatActual.getId(), PermisEnum.CONSULTA));
		
		// Llistat de procediments amb grups
		grupsProcediment = procedimentService.findAllGrups();
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcedimentHelper.class);

}
