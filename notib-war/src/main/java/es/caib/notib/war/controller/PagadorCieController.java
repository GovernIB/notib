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
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.PagadorCieCommand;
import es.caib.notib.war.command.PagadorCieFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.RequestSessionHelper;
import es.caib.notib.war.helper.RolHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per el mantinemnt de pagadors cie
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/pagadorCie")
public class PagadorCieController extends BaseUserController{
	
	private final static String PAGADOR_CIE_FILTRE = "pagadorcie_filtre";
	
	@Autowired
	ProcedimentService procedimentService;
	@Autowired
	EntitatService entitatService;
	@Autowired
	PagadorCieService pagadorCieService;
	@Autowired
	GrupService grupsService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new PagadorCieFiltreCommand());
		PagadorCieFiltreCommand pagadorCieFiltreCommand = getFiltreCommand(request);
		model.addAttribute("pagadorCieFiltreCommand", pagadorCieFiltreCommand);
		return "pagadorCieAdminList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( 
			HttpServletRequest request ) {
		PagadorCieFiltreCommand pagadorCieFiltreCommand = getFiltreCommand(request);
		PaginaDto<PagadorCieDto> pagadorsCie = null;
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		if (RolHelper.isUsuariActualAdministradorEntitat(request)) {
			//procedimentFiltreCommand.setEntitatId(entitat.getId());
		}
		pagadorsCie = pagadorCieService.findAmbFiltrePaginat(
							entitat.getId(),
							PagadorCieFiltreCommand.asDto(pagadorCieFiltreCommand),
							DatatablesHelper.getPaginacioDtoFromRequest(request));
		
		return DatatablesHelper.getDatatableResponse(
				request, 
				pagadorsCie, 
				"id");
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			Model model) {
		String vista = formGet(request, null, model);
		return vista;
	}
	
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid PagadorCieCommand pagadorCieCommand,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "pagadorCieAdminForm";
		}
		// if it is modified
		if (pagadorCieCommand.getId() != null) {
			pagadorCieService.update(
					PagadorCieCommand.asDto(pagadorCieCommand));
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:pagadorsCie",
					"procediment.controller.modificat.ok");
		//if it is new	
		} else {
			pagadorCieService.create(
					entitatActual.getId(),
					PagadorCieCommand.asDto(pagadorCieCommand));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:pagadorsCie",
					"procediment.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/{pagadorCieId}", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@PathVariable Long pagadorCieId,
			Model model) {
		//EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		PagadorCieCommand pagadorCieCommand = null;
		PagadorCieDto pagadorCie = null;
		
		if (pagadorCieId != null) {
			pagadorCie = pagadorCieService.findById(pagadorCieId);
			
			model.addAttribute(pagadorCie);
		}
		
		if (pagadorCie != null)
			pagadorCieCommand = PagadorCieCommand.asCommand(pagadorCie);
		else
			pagadorCieCommand = new PagadorCieCommand();
		
		model.addAttribute(pagadorCieCommand);
		return "pagadorCieAdminForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long pagadorCieId) {
		//EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		pagadorCieService.delete(pagadorCieId);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../pagadorCie",
				"pagadorCie.controller.esborrat.ok");
	}
	
	private PagadorCieFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		PagadorCieFiltreCommand pagadorCieFiltreCommand = (PagadorCieFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				PAGADOR_CIE_FILTRE);
		if (pagadorCieFiltreCommand == null) {
			pagadorCieFiltreCommand = new PagadorCieFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					PAGADOR_CIE_FILTRE,
					pagadorCieFiltreCommand);
		}
		return pagadorCieFiltreCommand;
	}
	
}
