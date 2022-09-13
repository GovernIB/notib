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
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.war.command.GrupCommand;
import es.caib.notib.war.command.GrupFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.RequestSessionHelper;

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
	public String get(HttpServletRequest request, Model model) {

		model.addAttribute(new GrupFiltreCommand());
		GrupFiltreCommand grupFiltreCommand = getFiltreCommand(request);
		model.addAttribute("grupFiltreCommand", grupFiltreCommand);
		return "grupAdminList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {

		GrupFiltreCommand grupFiltreCommand = getFiltreCommand(request);
		PaginaDto<GrupDto> grup = null;
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		grupFiltreCommand.setOrganGestorId(organGestorActual != null ? organGestorActual.getId() : null);
		grup = grupService.findAmbFiltrePaginat(entitat.getId(), GrupFiltreCommand.asDto(grupFiltreCommand), DatatablesHelper.getPaginacioDtoFromRequest(request));
		return DatatablesHelper.getDatatableResponse(request, grup, "id");
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, Model model) {
		return formGet(request, null, model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(HttpServletRequest request, GrupFiltreCommand command, Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(request, GRUP_FILTRE, command);
		return "grupAdminList";
	}
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid GrupCommand grupCommand, BindingResult bindingResult, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "grupAdminForm";
		}
		String url = "redirect:grupAdminList";
		String msg = grupCommand.getId() != null ? "grup.controller.modificat.ok" : "grup.controller.creat.ok";
		if (grupCommand.getId() != null) {
			grupService.update(GrupCommand.asDto(grupCommand));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		GrupDto dto = GrupCommand.asDto(grupCommand);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		dto.setOrganGestorId(organGestorActual != null ? organGestorActual.getId() : null);
		grupService.create(entitatActual.getId(), dto);
		return getModalControllerReturnValueSuccess(request, url, msg);
	}
	
	@RequestMapping(value = "/{grupId}", method = RequestMethod.GET)
	public String formGet(HttpServletRequest request, @PathVariable Long grupId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		GrupCommand grupCommand = null;
		GrupDto grup = null;
		if (grupId != null) {
			grup = grupService.findById(entitatActual.getId(), grupId);
			model.addAttribute(grup);
		}
		grupCommand = grup != null ? GrupCommand.asCommand(grup) : new GrupCommand();
		model.addAttribute(grupCommand);
		return "grupAdminForm";
	}
	
	@RequestMapping(value = "/{grupId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long grupId) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean existeix = grupService.existProcedimentGrupByGrupId(entitatActual.getId(), grupId);
		String url = "redirect:../../grup";
		String msg = existeix ? "grup.controller.esborrat.ko.enus" : "grup.controller.esborrat.ok";
		// Comprova que el grup no s'utilitzi
		if (existeix) {
			return getAjaxControllerReturnValueError(request, url, msg);
		}
		grupService.delete(grupId);
		return getAjaxControllerReturnValueSuccess(request, url, msg);
	}
	
	private GrupFiltreCommand getFiltreCommand(HttpServletRequest request) {

		GrupFiltreCommand grupFiltreCommand = (GrupFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, GRUP_FILTRE);
		if (grupFiltreCommand != null) {
			return grupFiltreCommand;
		}
		grupFiltreCommand = new GrupFiltreCommand();
		RequestSessionHelper.actualitzarObjecteSessio(request, GRUP_FILTRE, grupFiltreCommand);
		return grupFiltreCommand;
	}
}
