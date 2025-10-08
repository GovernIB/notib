package es.caib.notib.back.controller;

import es.caib.notib.back.command.GrupCommand;
import es.caib.notib.back.command.GrupFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.GrupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controlador per el mantinemnt de grups
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/grup")
public class GrupController extends BaseUserController{
	
	@Autowired
	EntitatService entitatService;
	@Autowired
	GrupService grupService;

	private static final String GRUP_FILTRE = "grup_filtre";
	
	@GetMapping
	public String get(HttpServletRequest request, Model model) {

		model.addAttribute(new GrupFiltreCommand());
		var grupFiltreCommand = getFiltreCommand(request);
		model.addAttribute("grupFiltreCommand", grupFiltreCommand);
		return "grupAdminList";
	}
	
	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {

		var grupFiltreCommand = getFiltreCommand(request);
		var entitat = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		grupFiltreCommand.setOrganGestorId(organGestorActual != null ? organGestorActual.getId() : null);
		var grup = grupService.findAmbFiltrePaginat(entitat.getId(), GrupFiltreCommand.asDto(grupFiltreCommand), DatatablesHelper.getPaginacioDtoFromRequest(request));
		return DatatablesHelper.getDatatableResponse(request, grup, "id");
	}
	
	@GetMapping(value = "/new")
	public String newGet(HttpServletRequest request, Model model) {
		return formGet(request, null, model);
	}
	
	@PostMapping
	public String post(HttpServletRequest request, GrupFiltreCommand command, Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(request, GRUP_FILTRE, command);
		return "grupAdminList";
	}

	@PostMapping(value = "/newOrModify")
	public String save(HttpServletRequest request, @Valid GrupCommand grupCommand, BindingResult bindingResult, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "grupAdminForm";
		}
		var url = "redirect:grupAdminList";
		var msg = grupCommand.getId() != null ? "grup.controller.modificat.ok" : "grup.controller.creat.ok";
		if (grupCommand.getId() != null) {
			grupService.update(GrupCommand.asDto(grupCommand));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		var dto = GrupCommand.asDto(grupCommand);
		var organGestorActual = getOrganGestorActual(request);
		dto.setOrganGestorId(organGestorActual != null ? organGestorActual.getId() : null);
		grupService.create(entitatActual.getId(), dto);
		return getModalControllerReturnValueSuccess(request, url, msg);
	}
	
	@GetMapping(value = "/{grupId}")
	public String formGet(HttpServletRequest request, @PathVariable Long grupId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		GrupDto grup = null;
		if (grupId != null) {
			grup = grupService.findById(entitatActual.getId(), grupId);
			model.addAttribute(grup);
		}
		var grupCommand = grup != null ? GrupCommand.asCommand(grup) : new GrupCommand();
		model.addAttribute(grupCommand);
		return "grupAdminForm";
	}
	
	@GetMapping(value = "/{grupId}/delete")
	public String delete(HttpServletRequest request, @PathVariable Long grupId) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var existeix = grupService.existProcedimentGrupByGrupId(entitatActual.getId(), grupId);
		var url = "redirect:../../grup";
		var msg = Boolean.TRUE.equals(existeix) ? "grup.controller.esborrat.ko.enus" : "grup.controller.esborrat.ok";
		// Comprova que el grup no s'utilitzi
		if (Boolean.TRUE.equals(existeix)) {
			return getAjaxControllerReturnValueError(request, url, msg);
		}
		grupService.delete(grupId);
		return getAjaxControllerReturnValueSuccess(request, url, msg);
	}
	
	private GrupFiltreCommand getFiltreCommand(HttpServletRequest request) {

		var grupFiltreCommand = (GrupFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, GRUP_FILTRE);
		if (grupFiltreCommand != null) {
			return grupFiltreCommand;
		}
		grupFiltreCommand = new GrupFiltreCommand();
		RequestSessionHelper.actualitzarObjecteSessio(request, GRUP_FILTRE, grupFiltreCommand);
		return grupFiltreCommand;
	}
}
