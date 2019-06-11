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
	public String getFormFormatsFulla(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			BindingResult bindingResult,
			Model model) {
		PagadorCieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		PagadorCieCommand pagadorCieCommand = PagadorCieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", pagadorCieCommand);
		
		if (bindingResult.hasErrors()) {
			return "pagadorCieFullaAdminForm";
		}
		
		return "pagadorCieFullaAdminForm";
	}
//	if (pagadorCieFormatFullCommand.getId() != null) {
//		pagadorCieFormatFullaService.update(
//				PagadorCieFormatFullaCommand.asDto(pagadorCieFormatFullCommand));
//		
//		return getModalControllerReturnValueSuccess(
//				request,
//				"redirect:pagadorsCie",
//				"procediment.controller.modificat.ok");
//	} else {
//		pagadorCieFormatFullaService.create(
//				pagadorCieId, 
//				PagadorCieFormatFullaCommand.asDto(pagadorCieFormatFullCommand));
//	}
	@RequestMapping(value = "/{pagadorCieId}/formats/sobre/new", method = RequestMethod.GET)
	public String getFormFormatsSobre(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			BindingResult bindingResult,
			Model model) {
		PagadorCieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		PagadorCieCommand pagadorCieCommand = PagadorCieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", pagadorCieCommand);
		if (bindingResult.hasErrors()) {
			return "pagadorCieSobreAdminForm";
		}
		return "pagadorCieSobreAdminForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatableFulla(
			@PathVariable Long pagadorCieId,
			HttpServletRequest request,
			Model model) {
		PaginaDto<PagadorCieFormatFullaCommand> formatFullaPagadorCie = new PaginaDto<PagadorCieFormatFullaCommand>();
		
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
		PaginaDto<PagadorCieFormatSobreCommand> formatSobrePagadorCie = new PaginaDto<PagadorCieFormatSobreCommand>();
		
		return DatatablesHelper.getDatatableResponse(
				request, 
				formatSobrePagadorCie, 
				"id");
	}
	
}
