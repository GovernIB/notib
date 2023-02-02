package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.back.command.OperadorPostalCommand;
import es.caib.notib.back.command.OperadorPostalFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RequestSessionHelper;
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
	
	private final static String PAGADOR_POSTAL_FILTRE = "pagadorpostal_filtre";

	@Autowired
	private OperadorPostalService operadorPostalService;
	@Autowired
	private OrganGestorService organGestorService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var operadorPostalFiltreCommand = getFiltreCommand(request);
		model.addAttribute("operadorPostalFiltreCommand", operadorPostalFiltreCommand);
		var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitat.getId());
		model.addAttribute("organsGestors", organsGestors);
		return "operadorPostalList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request ) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		var operadorPostalFiltreCommand = getFiltreCommand(request);
		operadorPostalFiltreCommand.setOrganGestorId(organGestorActual != null ? organGestorActual.getId() : null) ;
		var params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		var pagadorsPostals = operadorPostalService.findAmbFiltrePaginat(entitat.getId(), operadorPostalFiltreCommand.asDto(), params);
		return DatatablesHelper.getDatatableResponse(request, pagadorsPostals, "id");
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, Model model) {
		return formGet(request, null, model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(HttpServletRequest request, OperadorPostalFiltreCommand command, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		RequestSessionHelper.actualitzarObjecteSessio(request, PAGADOR_POSTAL_FILTRE, command);
		var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitat.getId());
		model.addAttribute("organsGestors", organsGestors);
		return "operadorPostalList";
	}
	
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid OperadorPostalCommand operadorPostalCommand, BindingResult bindingResult, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
			model.addAttribute("organsGestors", organsGestors);
			return "operadorPostalForm";
		}
		var msg = operadorPostalCommand.getId() != null ? "operadorpostal.controller.modificat.ok" : "operadorpostal.controller.creat.ok";
		var dto = operadorPostalCommand.asDto();
		operadorPostalService.upsert(entitatActual.getId(), dto);
		return getModalControllerReturnValueSuccess(request, "redirect:pagadorsPostals", msg);
	}
	
	@RequestMapping(value = "/{operadorPostalId}", method = RequestMethod.GET)
	public String formGet(HttpServletRequest request, @PathVariable Long operadorPostalId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		OperadorPostalDto operadorPostal = null;
		if (operadorPostalId != null) {
			operadorPostal = operadorPostalService.findById(operadorPostalId);
			model.addAttribute(operadorPostal);
		}
		var operadorPostalCommand = operadorPostal != null ? OperadorPostalCommand.asCommand(operadorPostal) : new OperadorPostalCommand();
		model.addAttribute(operadorPostalCommand);
		var organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
		model.addAttribute("organsGestors", organsGestors);
		return "operadorPostalForm";
	}
	
	@RequestMapping(value = "/{operadorPostalId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long operadorPostalId) {

		//EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		var url = "redirect:../../operadorPostal";
		try {
			operadorPostalService.delete(operadorPostalId);
			return getAjaxControllerReturnValueSuccess(request, url, "operadorpostal.controller.esborrat.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(request, url, "operadorpostal.controller.esborrat.ora.ko");
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
