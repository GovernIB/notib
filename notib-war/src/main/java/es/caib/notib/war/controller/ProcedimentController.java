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
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.api.service.PagadorPostalService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.ProcedimentCommand;
import es.caib.notib.war.command.ProcedimentFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.RequestSessionHelper;
import es.caib.notib.war.helper.RolHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per el mantinemnt de procediments
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/procediment")
public class ProcedimentController extends BaseUserController{
	
	private final static String PROCEDIMENTS_FILTRE = "procediments_filtre";
	
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
		model.addAttribute(new ProcedimentFiltreCommand());
		ProcedimentFiltreCommand procedimentFiltreCommand = getFiltreCommand(request);
		model.addAttribute("procedimentFiltreCommand", procedimentFiltreCommand);
		return "procedimentAdminList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( 
			HttpServletRequest request ) {
		ProcedimentFiltreCommand procedimentFiltreCommand = getFiltreCommand(request);
		PaginaDto<ProcedimentDto> procediments = null;
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		procediments = procedimentService.findAmbFiltrePaginat(
				entitat.getId(),
				ProcedimentFiltreCommand.asDto(procedimentFiltreCommand),
				DatatablesHelper.getPaginacioDtoFromRequest(request));
				
		return DatatablesHelper.getDatatableResponse(
				request, 
				procediments,
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
			@Valid ProcedimentCommand procedimentCommand,
			BindingResult bindingResult,
			Model model) {		
		if (bindingResult.hasErrors()) {
			return "procedimentAdminForm";
		}
		// if it is modified
		if (procedimentCommand.getId() != null) {
			procedimentService.update(
					ProcedimentCommand.asDto(procedimentCommand));
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:procediments",
					"procediment.controller.modificat.ok");
		//if it is new	
		} else {
			procedimentService.create(
					ProcedimentCommand.asDto(procedimentCommand));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:procediments",
					"procediment.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/{procedimentId}", method = RequestMethod.GET)
	public String formGet(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {
		ProcedimentDto procediment = null;
		ProcedimentCommand procedimentCommand;
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		
		if (RolHelper.isUsuariActualAdministrador(request))
			model.addAttribute("entitats", entitatService.findAll());
		else
			model.addAttribute("entitat", entitat);
			model.addAttribute("entitatId", entitat.getId());
		
		if (procedimentId != null) {
			procediment = procedimentService.findById(procedimentId);

			model.addAttribute("grups", grupsService.findByIdProcediment(procedimentId));
			model.addAttribute(procediment);
		}

		if (procediment != null) 
			procedimentCommand = ProcedimentCommand.asCommand(procediment);
		else
			procedimentCommand = new ProcedimentCommand();
		
		model.addAttribute("pagadorsPostal", pagadorPostalService.findAll());
		model.addAttribute("pagadorsCie", pagadorCieService.findAll());
		model.addAttribute(procedimentCommand);

		return "procedimentAdminForm";
	}
	
	@RequestMapping(value = "/{procedimentId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long procedimentId) {		
		procedimentService.delete(procedimentId);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../procediment",
				"procediment.controller.esborrat.ok");
	}
	
	private ProcedimentFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ProcedimentFiltreCommand procedimentFiltreCommand = (ProcedimentFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				PROCEDIMENTS_FILTRE);
		if (procedimentFiltreCommand == null) {
			procedimentFiltreCommand = new ProcedimentFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					PROCEDIMENTS_FILTRE,
					procedimentFiltreCommand);
		}
		return procedimentFiltreCommand;
	}
	
}
