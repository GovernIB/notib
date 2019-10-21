/**
 * 
 */
package es.caib.notib.war.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.dto.EntitatDto;
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
		List<ProcedimentDto> procedimentsPermisConsulta = null;
		Map<String, ProcedimentDto> uniqueProcediments = new HashMap<String, ProcedimentDto>();
//		List<ProcedimentDto> procediments = new ArrayList<ProcedimentDto>();
		List<ProcedimentGrupDto> procedimentsAmbGrups = new ArrayList<ProcedimentGrupDto>();
		List<ProcedimentDto> procedimentsSenseGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsPermisConsultaSenseGrups = new ArrayList<ProcedimentDto>();
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());

		if (RolHelper.isUsuariActualUsuari(request)) {
			// Llistat de procediments amb grups
			procedimentsAmbGrups = procedimentService.findAllGrups();
			procedimentsSenseGrups = procedimentService.findProcedimentsSenseGrups(entitatActual);
			// Obté els procediments que tenen el mateix grup que el rol d'usuari
			for (ProcedimentGrupDto grupProcediment : procedimentsAmbGrups) {
				for (String rol : rolsUsuariActual) {
					if (rol.contains(grupProcediment.getGrup().getCodi())) {
						if ((grupProcediment.getProcediment().getEntitat().getDir3Codi().equals(entitatActual.getDir3Codi()))) {
							uniqueProcediments.put(grupProcediment.getProcediment().getCodi(), grupProcediment.getProcediment());
//							procediments.add(grupProcediment.getProcediment());
						}
					}
				}
			}

			if (!uniqueProcediments.isEmpty()) {
				procedimentsPermisConsulta = notificacioService.findProcedimentsAmbPermisConsultaAndGrupsAndEntitat(
						uniqueProcediments,
						entitatActual);
			} else if (procedimentsAmbGrups.isEmpty()) {
				procedimentsPermisConsulta = notificacioService.findProcedimentsEntitatAmbPermisConsulta(entitatActual);
			}

			procedimentsPermisConsultaSenseGrups = notificacioService.findProcedimentsAmbPermisConsultaSenseGrupsAndEntitat(
					procedimentsSenseGrups,
					entitatActual);

			if (((procedimentsPermisConsulta == null || procedimentsPermisConsulta.size() < 0) && (procedimentsPermisConsultaSenseGrups != null && procedimentsPermisConsultaSenseGrups.isEmpty())) 
					|| ((procedimentsPermisConsulta != null && procedimentsPermisConsulta.isEmpty()) && (procedimentsPermisConsultaSenseGrups == null || procedimentsPermisConsultaSenseGrups.size() < 0))) {
				sensePermis = true;
			}
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
		List<ProcedimentDto> procedimentsSenseGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsPermisConsultaSenseGrups = new ArrayList<ProcedimentDto>();
		List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());
		
		// Llistat de procediments amb grups
		grupsProcediment = procedimentService.findAllGrups();
		procediments = new ArrayList<ProcedimentDto>();
		// Obté els procediments que tenen el mateix grup que el rol d'usuari
		for (ProcedimentGrupDto grupProcediment : grupsProcediment) {
			for (String rol : rolsUsuariActual) {
				if (rol.contains(grupProcediment.getGrup().getCodi())) {
					if ((grupProcediment.getProcediment().getEntitat().getDir3Codi().equals(entitatActual.getDir3Codi()))) {
						procediments.add(grupProcediment.getProcediment());
					}
				}
			}
		}
		// Procediments sense grups però amb perís consulta
		procedimentsSenseGrups = procedimentService.findProcedimentsSenseGrups(entitatActual);

		if (!procedimentsSenseGrups.isEmpty()) {
			procedimentsPermisConsultaSenseGrups = notificacioService.findProcedimentsAmbPermisConsultaSenseGrupsAndEntitat(
							procedimentsSenseGrups,
							entitatActual);

			for (ProcedimentDto procedimentSenseGrupAmbPermis : procedimentsPermisConsultaSenseGrups) {
				procediments.add(procedimentSenseGrupAmbPermis);
			}
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcedimentHelper.class);

}
