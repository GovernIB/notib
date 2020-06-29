package es.caib.notib.war.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.PermisCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per el mantinemnt de permisos de organs gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/organgestor")
public class OrganGestorPermisController extends BaseUserController{
	
	@Autowired
	ProcedimentService procedimentService;
	@Autowired
	EntitatService entitatService;
	
	@RequestMapping(value = "/{organGestorId}/permis", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long organGestorId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		OrganGestorDto organGestor = procedimentService.findOrganGestorById(
				entitatActual.getId(),
				organGestorId);
					
		model.addAttribute(
				"organGestor",
				organGestor);
		
		return "organGestorPermis";
	}

	@RequestMapping(value = "/{organGestorId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request, 
			@PathVariable Long organGestorId, 
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(request,
				procedimentService.permisOrganGestorFind(
						entitatActual.getId(), 
						organGestorId), 
						"id");
	}
	
	@RequestMapping(value = "/{organGestorId}/permis/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long organGestorId,
			Model model) {
		return get(request, organGestorId, null, model);
	}
	
	@RequestMapping(value = "/{organGestorId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long organGestorId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"organGestor",
				procedimentService.findOrganGestorById(
						entitatActual.getId(),
						organGestorId));
		PermisDto permis = null;
		if (permisId != null) {
			List<PermisDto> permisos = procedimentService.permisOrganGestorFind(
					entitatActual.getId(),
					organGestorId);
			for (PermisDto p: permisos) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		if (permis != null)
			model.addAttribute(PermisCommand.asCommand(permis));
		else
			model.addAttribute(new PermisCommand());
		return "organGestorPermisForm";
	}
	
	@RequestMapping(value = "/{organGestorId}/permis", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long organGestorId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"organGestor",
					procedimentService.findOrganGestorById(
							entitatActual.getId(),
							organGestorId));
			return "organGestorPermisForm";
		}
		procedimentService.permisOrganGestorUpdate(
				entitatActual.getId(),
				organGestorId,
				PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../organgestor/" + organGestorId + "/permis",
				"procediment.controller.permis.modificat.ok");
	}
	
	@RequestMapping(value = "/{organGestorId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long organGestorId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		procedimentService.permisOrganGestorDelete(
				entitatActual.getId(),
				organGestorId,
				permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../organgestor/" + organGestorId + "/permis",
				"procediment.controller.permis.esborrat.ok");
	}
	
//	private boolean isAdministrador(
//			HttpServletRequest request) {
//		return RolHelper.isUsuariActualAdministrador(request);
//	}
	
}
