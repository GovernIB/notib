package es.caib.notib.back.controller;

import es.caib.notib.back.command.CieCommand;
import es.caib.notib.back.command.CieFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.CieTableItemDto;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieService;
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
 * Controlador per el mantinemnt de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/cie")
public class CieController extends BaseUserController{
	

	@Autowired
	private PagadorCieService pagadorCieService;
	@Autowired
	private OrganGestorService organGestorService;

	private static final  String PAGADOR_CIE_FILTRE = "pagadorcie_filtre";
	private static final String ORGANS_GESTORS = "organsGestors";

	
	@GetMapping
	public String get(HttpServletRequest request, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var cieFiltreCommand = getFiltreCommand(request);
		model.addAttribute("cieFiltreCommand", cieFiltreCommand);
		var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitat.getId());
		model.addAttribute(ORGANS_GESTORS, organsGestors);
		return "cieList";
	}
	
	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request ) {

		var cieFiltreCommand = getFiltreCommand(request);
		var entitat = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		cieFiltreCommand.setOrganGestorId(organGestorActual != null ? organGestorActual.getId() : null);
		var paginacio = DatatablesHelper.getPaginacioDtoFromRequest(request);
		PaginaDto<CieTableItemDto> pagadorsCie = pagadorCieService.findAmbFiltrePaginat(entitat.getId(), cieFiltreCommand.asDto(), paginacio);
		return DatatablesHelper.getDatatableResponse(request, pagadorsCie, "id");
	}
	
	@GetMapping(value = "/new")
	public String newGet(HttpServletRequest request, Model model) {
		return formGet(request, null, model);
	}
	
	@PostMapping
	public String post(HttpServletRequest request, CieFiltreCommand command, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		RequestSessionHelper.actualitzarObjecteSessio(request, PAGADOR_CIE_FILTRE, command);
		var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitat.getId());
		model.addAttribute(ORGANS_GESTORS, organsGestors);
		return "cieList";
	}
	
	@PostMapping(value = "/newOrModify")
	public String save(HttpServletRequest request, @Valid CieCommand cieCommand, BindingResult bindingResult, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
			model.addAttribute(ORGANS_GESTORS, organsGestors);
			return "cieForm";
		}
		var msg = cieCommand.getId() != null ? "cie.controller.modificat.ok" : "cie.controller.creat.ok";
		var dto = cieCommand.asDto();
		pagadorCieService.upsert(entitatActual.getId(), dto);
		return getModalControllerReturnValueSuccess(request, "redirect:cie", msg);
	}
	
	@GetMapping(value = "/{pagadorCieId}")
	public String formGet(HttpServletRequest request, @PathVariable Long pagadorCieId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		CieCommand cieCommand = null;
		CieDto pagadorCie = null;
		if (pagadorCieId != null) {
			pagadorCie = pagadorCieService.findById(pagadorCieId);
			model.addAttribute(pagadorCie);
		}
		cieCommand = pagadorCie != null ? CieCommand.asCommand(pagadorCie) : new CieCommand();
		model.addAttribute(cieCommand);
		var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
		model.addAttribute(ORGANS_GESTORS, organsGestors);
		return "cieForm";
	}
	
	@GetMapping(value = "/{pagadorCieId}/delete")
	public String delete(HttpServletRequest request, @PathVariable Long pagadorCieId) {

		var redirect = "redirect:../../cie";
		try {
			pagadorCieService.delete(pagadorCieId);
			return getAjaxControllerReturnValueSuccess(request, redirect, "cie.controller.esborrat.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(request, redirect, "cie.controller.esborrat.ora.ko");
		}
	}
	
	private CieFiltreCommand getFiltreCommand(HttpServletRequest request) {

		var cieFiltreCommand = (CieFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, PAGADOR_CIE_FILTRE);
		if (cieFiltreCommand != null) {
			return cieFiltreCommand;
		}
		cieFiltreCommand = new CieFiltreCommand();
		RequestSessionHelper.actualitzarObjecteSessio(request, PAGADOR_CIE_FILTRE, cieFiltreCommand);
		return cieFiltreCommand;
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
}
