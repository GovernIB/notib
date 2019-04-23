package es.caib.notib.war.controller;

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
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.war.command.GrupCommand;
import es.caib.notib.war.command.GrupFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.RequestSessionHelper;
import es.caib.notib.war.helper.RolHelper;

/**
 * Controlador per el mantinemnt de grups
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/grup")
public class GrupController extends BaseUserController{
	
	private final static String GRUP_FILTRE = "grup_filtre";
	
	@Autowired
	EntitatService entitatService;
	@Autowired
	GrupService grupService;
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new GrupFiltreCommand());
		GrupFiltreCommand grupFiltreCommand = getFiltreCommand(request);
		model.addAttribute("grupFiltreCommand", grupFiltreCommand);
		return "grupAdminList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( 
			HttpServletRequest request ) {
		GrupFiltreCommand grupFiltreCommand = getFiltreCommand(request);
		PaginaDto<GrupDto> grup = null;
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		if (RolHelper.isUsuariActualAdministradorEntitat(request)) {
			//procedimentFiltreCommand.setEntitatId(entitat.getId());
		}
		grup = grupService.findAmbFiltrePaginat(
							entitat.getId(),
							GrupFiltreCommand.asDto(grupFiltreCommand),
							DatatablesHelper.getPaginacioDtoFromRequest(request));
		
		return DatatablesHelper.getDatatableResponse(
				request, 
				grup, 
				"id");
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			Model model) {
		String vista = formGet(request, null, model);
		return vista;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(	
			HttpServletRequest request,
			GrupFiltreCommand command,
			Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request, 
				GRUP_FILTRE, 
				command);
		
		return "grupAdminList";
	}
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid GrupCommand grupCommand,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "pagadorCieAdminForm";
		}
		// if it is modified
		if (grupCommand.getId() != null) {
			grupService.update(
					GrupCommand.asDto(grupCommand));
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:pagadorsCie",
					"procediment.controller.modificat.ok");
		//if it is new	
		} else {
			grupService.create(
					entitatActual.getId(),
					GrupCommand.asDto(grupCommand));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:pagadorsCie",
					"procediment.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/{grupId}", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@PathVariable Long grupId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		GrupCommand grupCommand = null;
		GrupDto grup = null;
		
		if (grupId != null) {
			grup = grupService.findById(
					entitatActual.getId(),
					grupId);
			
			model.addAttribute(grup);
		}
		
		if (grup != null)
			grupCommand = GrupCommand.asCommand(grup);
		else
			grupCommand = new GrupCommand();
		
		model.addAttribute(grupCommand);
		return "grupAdminForm";
	}
	
	@RequestMapping(value = "/{grupId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long grupId) {
		//EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		grupService.delete(grupId);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../grup",
				"grup.controller.esborrat.ok");
	}
	
	private GrupFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		GrupFiltreCommand grupFiltreCommand = (GrupFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				GRUP_FILTRE);
		if (grupFiltreCommand == null) {
			grupFiltreCommand = new GrupFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					GRUP_FILTRE,
					grupFiltreCommand);
		}
		return grupFiltreCommand;
	}
	
}
