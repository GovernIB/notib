package es.caib.notib.back.controller;

import es.caib.notib.back.command.OperadorPostalCommand;
import es.caib.notib.back.command.OperadorPostalFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.dto.CodiValorEstatDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDataDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
	public String get(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OperadorPostalFiltreCommand operadorPostalFiltreCommand = getFiltreCommand(request);
		model.addAttribute("operadorPostalFiltreCommand", operadorPostalFiltreCommand);
		List<CodiValorEstatDto> organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitat.getId());
		model.addAttribute("organsGestors", organsGestors);
		return "operadorPostalList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request ) {
		OperadorPostalFiltreCommand operadorPostalFiltreCommand = getFiltreCommand(request);
		PaginaDto<OperadorPostalTableItemDto> pagadorsPostals = null;
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		if (organGestorActual != null) {
			operadorPostalFiltreCommand.setOrganGestorId(organGestorActual.getId());
		} else {
			operadorPostalFiltreCommand.setOrganGestorId(null);
		}
		
		pagadorsPostals = operadorPostalService.findAmbFiltrePaginat(
							entitat.getId(),
							operadorPostalFiltreCommand.asDto(),
							DatatablesHelper.getPaginacioDtoFromRequest(request));
		
		return DatatablesHelper.getDatatableResponse(
				request, 
				pagadorsPostals, 
				"id");
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			Model model) {
		return formGet(request, null, model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(	
			HttpServletRequest request,
			OperadorPostalFiltreCommand command,
			Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request, 
				PAGADOR_POSTAL_FILTRE, 
				command);

		List<CodiValorEstatDto> organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitat.getId());
		model.addAttribute("organsGestors", organsGestors);
		return "operadorPostalList";
	}
	
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid OperadorPostalCommand operadorPostalCommand, BindingResult bindingResult, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			List<CodiValorEstatDto> organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
			model.addAttribute("organsGestors", organsGestors);
			return "operadorPostalForm";
		}
		String msg = operadorPostalCommand.getId() != null ? "operadorpostal.controller.modificat.ok" : "operadorpostal.controller.creat.ok";
		OperadorPostalDataDto dto = operadorPostalCommand.asDto();
		operadorPostalService.upsert(entitatActual.getId(), dto);
		return getModalControllerReturnValueSuccess(request, "redirect:pagadorsPostals", msg);
	}
	
	@RequestMapping(value = "/{operadorPostalId}", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@PathVariable Long operadorPostalId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		OperadorPostalCommand operadorPostalCommand = null;
		OperadorPostalDto operadorPostal = null;
		
		if (operadorPostalId != null) {
			operadorPostal = operadorPostalService.findById(operadorPostalId);
			model.addAttribute(operadorPostal);
		}
		
		if (operadorPostal != null)
			operadorPostalCommand = OperadorPostalCommand.asCommand(operadorPostal);
		else
			operadorPostalCommand = new OperadorPostalCommand();
		
		model.addAttribute(operadorPostalCommand);
		List<CodiValorEstatDto> organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
		model.addAttribute("organsGestors", organsGestors);
		return "operadorPostalForm";
	}
	
	@RequestMapping(value = "/{operadorPostalId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long operadorPostalId) {
		//EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		try {
			operadorPostalService.delete(operadorPostalId);
		} catch (Exception e) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../operadorPostal",
						"operadorpostal.controller.esborrat.ora.ko");
			
		}
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../operadorPostal",
				"operadorpostal.controller.esborrat.ok");
	}

	private OperadorPostalFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		OperadorPostalFiltreCommand operadorPostalFiltreCommand = (OperadorPostalFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				PAGADOR_POSTAL_FILTRE);
		if (operadorPostalFiltreCommand == null) {
			operadorPostalFiltreCommand = new OperadorPostalFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					PAGADOR_POSTAL_FILTRE,
					operadorPostalFiltreCommand);
		}
		return operadorPostalFiltreCommand;
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class, 
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
}
