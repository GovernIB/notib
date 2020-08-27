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
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.api.service.PagadorPostalService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.PermisCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.RolHelper;

/**
 * Controlador per el mantinemnt de permisos de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/procediment")
public class ProcedimentPermisController extends BaseUserController{
	
	@Autowired
	ProcedimentService procedimentService;
	@Autowired
	EntitatService entitatService;
	@Autowired
	PagadorPostalService pagadorPostalService;
	@Autowired
	PagadorCieService pagadorCieService;
	@Autowired
	GrupService grupsService;
	
	@RequestMapping(value = "/{procedimentId}/permis", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			Model model) {
		boolean isAdministrador = RolHelper.isUsuariActualAdministrador(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		ProcedimentDto procediment = procedimentService.findById(
				entitatActual.getId(),
				isAdministrador,
				procedimentId);
					
		model.addAttribute(
				"procediment",
				 procediment);
		
		return "procedimentAdminPermis";
	}

	@RequestMapping(value = "/{procedimentId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request, 
			@PathVariable Long procedimentId, 
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(request,
				procedimentService.permisFind(
						entitatActual.getId(), 
						isAdministrador(request),
						procedimentId), 
						"id");
	}
	
	@RequestMapping(value = "/{procedimentId}/permis/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			Model model) {
		return get(request, procedimentId, null, model);
	}
	
	@RequestMapping(value = "/{procedimentId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"procediment",
				procedimentService.findById(
						entitatActual.getId(),
						isAdministrador(request),
						procedimentId));
		PermisDto permis = null;
		if (permisId != null) {
			List<PermisDto> permisos = procedimentService.permisFind(
					entitatActual.getId(),
					isAdministrador(request),
					procedimentId);
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
		return "procedimentAdminPermisForm";
	}
	
	@RequestMapping(value = "/{procedimentId}/permis", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"entitat",
					procedimentService.findById(
							entitatActual.getId(),
							isAdministrador(request),
							procedimentId));
			return "procedimentAdminPermisForm";
		}
		Long organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisUpdate(
				entitatActual.getId(),
				organGestorActualId,
				procedimentId,
				PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../procediment/" + procedimentId + "/permis",
				"procediment.controller.permis.modificat.ok");
	}
	
	@RequestMapping(value = "/{procedimentId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Long organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisDelete(
				entitatActual.getId(),
				organGestorActualId,
				procedimentId,
				permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../procediment/" + procedimentId + "/permis",
				"procediment.controller.permis.esborrat.ok");
	}
	
	private boolean isAdministrador(
			HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
	}
	
}
