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

import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFormatFullaDto;
import es.caib.notib.core.api.dto.PagadorCieFormatSobreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.PagadorCieFormatFullaService;
import es.caib.notib.core.api.service.PagadorCieFormatSobreService;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.PagadorCieCommand;
import es.caib.notib.war.command.PagadorCieFormatFullaCommand;
import es.caib.notib.war.command.PagadorCieFormatSobreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per el mantinemnt de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/pagadorCie")
public class PagadorCieFormatsController extends BaseUserController{
	
	@Autowired
	ProcedimentService procedimentService;
	@Autowired
	EntitatService entitatService;
	@Autowired
	PagadorCieService pagadorCieService;
	@Autowired
	PagadorCieFormatFullaService pagadorCieFormatFullaService;
	@Autowired
	PagadorCieFormatSobreService pagadorCieFormatSobreService;
	@Autowired
	GrupService grupsService;
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla", method = RequestMethod.GET)
	public String getFormatsFulla(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			Model model) {
		PagadorCieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		PagadorCieCommand pagadorCieCommand = PagadorCieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", pagadorCieCommand);
		
		return "pagadorCieFullaAdminList";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/sobre", method = RequestMethod.GET)
	public String getFormatsSobre(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			Model model) {
		PagadorCieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		PagadorCieCommand pagadorCieCommand = PagadorCieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", pagadorCieCommand);
		
		return "pagadorCieSobreAdminList";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla/new", method = RequestMethod.GET)
	public String getFormFormatFullaNew(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			Model model) {
		PagadorCieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		PagadorCieCommand pagadorCieCommand = PagadorCieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", pagadorCieCommand);
		
		model.addAttribute(new PagadorCieFormatFullaCommand());
		return "pagadorCieFullaAdminForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla/{formatFullaId}", method = RequestMethod.GET)
	public String getFormFormatFulla(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			@PathVariable Long formatFullaId,
			Model model) {
		PagadorCieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		PagadorCieCommand pagadorCieCommand = PagadorCieCommand.asCommand(pagadorCie);
		PagadorCieFormatFullaCommand pagadorCieFormatFullaCommand;
		PagadorCieFormatFullaDto pagadorCieFormatFulla;
		
		pagadorCieFormatFulla = pagadorCieFormatFullaService.findById(formatFullaId);
		
		if (pagadorCieFormatFulla != null)
			pagadorCieFormatFullaCommand = PagadorCieFormatFullaCommand.asCommand(pagadorCieFormatFulla);
		else
			pagadorCieFormatFullaCommand = new PagadorCieFormatFullaCommand();

		model.addAttribute("pagadorCie", pagadorCieCommand);
		model.addAttribute(pagadorCieFormatFullaCommand);
		
		return "pagadorCieFullaAdminForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla/{formatFullaId}/delete", method = RequestMethod.GET)
	public String formatFullaDelete(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			@PathVariable Long formatFullaId,
			Model model) {
		
		pagadorCieFormatFullaService.delete(formatFullaId);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../pagadorCie/" + pagadorCieId + "/formats/fulla",
				"pagadorcie.format.fulla.controller.esborrat.ok");
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla/newOrModify", method = RequestMethod.POST)
	public String saveFormatFulla(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			@Valid PagadorCieFormatFullaCommand pagadorCieFormatFullCommand,
			BindingResult bindingResult,
			Model model) {
		PagadorCieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		PagadorCieCommand pagadorCieCommand = PagadorCieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", pagadorCieCommand);
		
		if (bindingResult.hasErrors()) {
			return "pagadorCieFullaAdminForm";
		}
		
		if (pagadorCieFormatFullCommand.getId() != null) {
			pagadorCieFormatFullaService.update(
					PagadorCieFormatFullaCommand.asDto(pagadorCieFormatFullCommand));
		
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:pagadorsCie",
					"pagadorcie.format.fulla.controller.modificat.ok");
		} else {
			pagadorCieFormatFullaService.create(
					pagadorCieId, 
					PagadorCieFormatFullaCommand.asDto(pagadorCieFormatFullCommand));
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:pagadorsCie",
					"pagadorcie.format.fulla.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{pagadorCieId}/formats/sobre/new", method = RequestMethod.GET)
	public String getFormFormatSobreNew(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			Model model) {
		PagadorCieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		PagadorCieCommand pagadorCieCommand = PagadorCieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", pagadorCieCommand);

		model.addAttribute(new PagadorCieFormatSobreCommand());
		return "pagadorCieSobreAdminForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/sobre/{formatSobreId}", method = RequestMethod.GET)
	public String getFormFormatSobre(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			@PathVariable Long formatSobreId,
			Model model) {
		PagadorCieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		PagadorCieCommand pagadorCieCommand = PagadorCieCommand.asCommand(pagadorCie);
		PagadorCieFormatSobreCommand pagadorCieFormatSobreCommand;
		PagadorCieFormatSobreDto pagadorCieFormatSobre;
		
		pagadorCieFormatSobre = pagadorCieFormatSobreService.findById(formatSobreId);
		
		if (pagadorCieFormatSobre != null)
			pagadorCieFormatSobreCommand = PagadorCieFormatSobreCommand.asCommand(pagadorCieFormatSobre);
		else
			pagadorCieFormatSobreCommand = new PagadorCieFormatSobreCommand();

		model.addAttribute("pagadorCie", pagadorCieCommand);
		model.addAttribute(pagadorCieFormatSobreCommand);
		
		return "pagadorCieSobreAdminForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/sobre/{formatSobreId}/delete", method = RequestMethod.GET)
	public String formatSobreDelete(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			@PathVariable Long formatSobreId,
			Model model) {
		
		pagadorCieFormatSobreService.delete(formatSobreId);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../pagadorCie/" + pagadorCieId + "/formats/sobre",
				"pagadorcie.format.sobre.controller.esborrat.ok");
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/sobre/newOrModify", method = RequestMethod.POST)
	public String saveFormatSobre(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			@Valid PagadorCieFormatSobreCommand pagadorCieFormatSobreCommand,
			BindingResult bindingResult,
			Model model) {
		PagadorCieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		PagadorCieCommand pagadorCieCommand = PagadorCieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", pagadorCieCommand);
		
		if (bindingResult.hasErrors()) {
			return "pagadorCieFullaAdminForm";
		}
		if (pagadorCieFormatSobreCommand.getId() != null) {
			pagadorCieFormatSobreService.update(
					PagadorCieFormatSobreCommand.asDto(pagadorCieFormatSobreCommand));
		
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:pagadorsCie",
					"pagadorcie.format.sobre.controller.modificat.ok");
		} else {
			pagadorCieFormatSobreService.create(
					pagadorCieId, 
					PagadorCieFormatSobreCommand.asDto(pagadorCieFormatSobreCommand));
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:pagadorsCie",
					"pagadorcie.format.sobre.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatableFulla(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			Model model) {
		PaginaDto<PagadorCieFormatFullaDto> formatFullaPagadorCie = pagadorCieFormatFullaService.findAllPaginat(
				pagadorCieId,
				DatatablesHelper.getPaginacioDtoFromRequest(request));
		
		return DatatablesHelper.getDatatableResponse(
				request, 
				formatFullaPagadorCie, 
				"id");
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/sobre/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatableSobre(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			Model model) {
		PaginaDto<PagadorCieFormatSobreDto> formatSobrePagadorCie = pagadorCieFormatSobreService.findAllPaginat(
				pagadorCieId,
				DatatablesHelper.getPaginacioDtoFromRequest(request));
		
		return DatatablesHelper.getDatatableResponse(
				request, 
				formatSobrePagadorCie, 
				"id");
	}
	
}
