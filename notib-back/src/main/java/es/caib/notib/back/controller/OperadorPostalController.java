package es.caib.notib.back.controller;

import es.caib.notib.back.command.OperadorPostalCommand;
import es.caib.notib.back.command.OperadorPostalFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controlador per el mantinemnt de pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/operadorPostal")
public class OperadorPostalController extends BaseUserController{

	@Autowired
	private OperadorPostalService operadorPostalService;
	@Autowired
	private OrganGestorService organGestorService;

	private static final String PAGADOR_POSTAL_FILTRE = "pagadorpostal_filtre";
	private static final String ORGANS_GESTORS = "organsGestors";


	@GetMapping
	public String get(HttpServletRequest request, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var operadorPostalFiltreCommand = getFiltreCommand(request);
		model.addAttribute("operadorPostalFiltreCommand", operadorPostalFiltreCommand);
		var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitat.getId());
		model.addAttribute(ORGANS_GESTORS, organsGestors);
		return "operadorPostalList";
	}
	
	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request ) {

		var operadorPostalFiltreCommand = getFiltreCommand(request);
		var entitat = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		operadorPostalFiltreCommand.setOrganGestorId(organGestorActual != null ? organGestorActual.getId() : null);
		var paginacio = DatatablesHelper.getPaginacioDtoFromRequest(request);
		var pagadorsPostals = operadorPostalService.findAmbFiltrePaginat(entitat.getId(), operadorPostalFiltreCommand.asDto(), paginacio);
		return DatatablesHelper.getDatatableResponse(request, pagadorsPostals, "id");
	}
	
	@GetMapping(value = "/new")
	public String newGet(HttpServletRequest request, Model model) {
		return formGet(request, null, model);
	}
	
	@PostMapping
	public String post(HttpServletRequest request, OperadorPostalFiltreCommand command, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		RequestSessionHelper.actualitzarObjecteSessio(request, PAGADOR_POSTAL_FILTRE, command);
		var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitat.getId());
		model.addAttribute(ORGANS_GESTORS, organsGestors);
		return "operadorPostalList";
	}
	
	@PostMapping(value = "/newOrModify")
	public String save(HttpServletRequest request, @Valid OperadorPostalCommand operadorPostalCommand, BindingResult bindingResult, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
			model.addAttribute(ORGANS_GESTORS, organsGestors);
			return "operadorPostalForm";
		}
		var msg = operadorPostalCommand.getId() != null ? "operadorpostal.controller.modificat.ok" : "operadorpostal.controller.creat.ok";
		var dto = operadorPostalCommand.asDto();
		operadorPostalService.upsert(entitatActual.getId(), dto);
		return getModalControllerReturnValueSuccess(request, "redirect:pagadorsPostals", msg);
	}
	
	@GetMapping(value = "/{operadorPostalId}")
	public String formGet(HttpServletRequest request, @PathVariable Long operadorPostalId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		OperadorPostalCommand operadorPostalCommand = null;
		OperadorPostalDto operadorPostal = null;
		if (operadorPostalId != null) {
			operadorPostal = operadorPostalService.findById(operadorPostalId);
			model.addAttribute(operadorPostal);
		}
		operadorPostalCommand = operadorPostal != null ? OperadorPostalCommand.asCommand(operadorPostal) : new OperadorPostalCommand();
		model.addAttribute(operadorPostalCommand);
		var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
		model.addAttribute(ORGANS_GESTORS, organsGestors);
		return "operadorPostalForm";
	}
	
	@GetMapping(value = "/{operadorPostalId}/delete")
	public String delete(HttpServletRequest request, @PathVariable Long operadorPostalId) {

		var redirect = "redirect:../../operadorPostal";
		try {
			operadorPostalService.delete(operadorPostalId);
			return getAjaxControllerReturnValueSuccess(request, redirect, "operadorpostal.controller.esborrat.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(request, redirect, "operadorpostal.controller.esborrat.ora.ko");
		}
	}

	private OperadorPostalFiltreCommand getFiltreCommand(HttpServletRequest request) {

		var operadorPostalFiltreCommand = (OperadorPostalFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, PAGADOR_POSTAL_FILTRE);
		if (operadorPostalFiltreCommand != null) {
			return operadorPostalFiltreCommand;
		}
		operadorPostalFiltreCommand = new OperadorPostalFiltreCommand();
		RequestSessionHelper.actualitzarObjecteSessio(request, PAGADOR_POSTAL_FILTRE, operadorPostalFiltreCommand);
		return operadorPostalFiltreCommand;
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
}
