package es.caib.notib.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.api.service.PagadorPostalService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.PagadorPostalCommand;
import es.caib.notib.war.command.PagadorPostalFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.RequestSessionHelper;
import es.caib.notib.war.helper.RolHelper;

/**
 * Controlador per el mantinemnt de pagadors postals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/pagadorPostal")
public class PagadorPostalController extends BaseUserController{
	
	private final static String PAGADOR_POSTAL_FILTRE = "pagadorpostal_filtre";
	
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
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new PagadorPostalFiltreCommand());
		PagadorPostalFiltreCommand pagadorPostalFiltreCommand = getFiltreCommand(request);
		model.addAttribute("pagadorPostalFiltreCommand", pagadorPostalFiltreCommand);
		return "pagadorPostalAdminList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( 
			HttpServletRequest request ) {
		PagadorPostalFiltreCommand pagadorPostalFiltreCommand = getFiltreCommand(request);
		PaginaDto<PagadorPostalDto> pagadorsPostals = null;
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		if (RolHelper.isUsuariActualAdministradorEntitat(request)) {
			//procedimentFiltreCommand.setEntitatId(entitat.getId());
		}
		pagadorsPostals = pagadorPostalService.findAmbFiltrePaginat(
							entitat.getId(),
							PagadorPostalFiltreCommand.asDto(pagadorPostalFiltreCommand),
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
		String vista = formGet(request, null, model);
		return vista;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(	
			HttpServletRequest request,
			PagadorPostalFiltreCommand command,
			Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request, 
				PAGADOR_POSTAL_FILTRE, 
				command);
		
		return "pagadorPostalAdminList";
	}
	
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid PagadorPostalCommand pagadorPostalCommand,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "pagadorPostalAdminForm";
		}
		// if it is modified
		if (pagadorPostalCommand.getId() != null) {
			pagadorPostalService.update(
					PagadorPostalCommand.asDto(pagadorPostalCommand));
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:pagadorsPostals",
					"pagadorPostal.controller.modificat.ok");
		//if it is new	
		} else {
			pagadorPostalService.create(
					entitatActual.getId(),
					PagadorPostalCommand.asDto(pagadorPostalCommand));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:pagadorsPostals",
					"pagadorPostal.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/{pagadorPostalId}", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@PathVariable Long pagadorPostalId,
			Model model) {
		//EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		PagadorPostalCommand pagadorPostalCommand = null;
		PagadorPostalDto pagadorPostal = null;
		
		if (pagadorPostalId != null) {
			pagadorPostal = pagadorPostalService.findById(pagadorPostalId);
			model.addAttribute(pagadorPostal);
		}
		
		if (pagadorPostal != null)
			pagadorPostalCommand = PagadorPostalCommand.asCommand(pagadorPostal);
		else
			pagadorPostalCommand = new PagadorPostalCommand();
		
		model.addAttribute(pagadorPostalCommand);
		return "pagadorPostalAdminForm";
	}
	
	@RequestMapping(value = "/{pagadorPostalId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long pagadorPostalId) {
		//EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		try {
			pagadorPostalService.delete(pagadorPostalId);
		} catch (Exception e) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../pagadorPostal",
						"pagadorPostal.controller.esborrat.ora.ko");
			
		}
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../pagadorPostal",
				"pagadorPostal.controller.esborrat.ok");
	}
	
	private PagadorPostalFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		PagadorPostalFiltreCommand pagadorPostalFiltreCommand = (PagadorPostalFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				PAGADOR_POSTAL_FILTRE);
		if (pagadorPostalFiltreCommand == null) {
			pagadorPostalFiltreCommand = new PagadorPostalFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					PAGADOR_POSTAL_FILTRE,
					pagadorPostalFiltreCommand);
		}
		return pagadorPostalFiltreCommand;
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class, 
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
}
