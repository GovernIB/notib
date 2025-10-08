package es.caib.notib.back.controller;

import es.caib.notib.back.command.CieCommand;
import es.caib.notib.back.command.PagadorCieFormatFullaCommand;
import es.caib.notib.back.command.PagadorCieFormatSobreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.logic.intf.service.PagadorCieFormatFullaService;
import es.caib.notib.logic.intf.service.PagadorCieFormatSobreService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controlador per el mantinemnt de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/cie")
public class CieFormatsController extends BaseUserController{

	@Autowired
	private PagadorCieService pagadorCieService;
	@Autowired
	private PagadorCieFormatFullaService pagadorCieFormatFullaService;
	@Autowired
	private PagadorCieFormatSobreService pagadorCieFormatSobreService;

	private static final String PAGADOR_CIE = "pagadorCie";
	private static final String FORM = "pagadorCieFullaAdminForm";

	
	@GetMapping(value = "/{pagadorCieId}/formats/fulla")
	public String getFormatsFulla(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		var pagadorCie = pagadorCieService.findById(pagadorCieId);
		var cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute(PAGADOR_CIE, cieCommand);
		return "pagadorCieFullaAdminList";
	}
	
	@GetMapping(value = "/{pagadorCieId}/formats/sobre")
	public String getFormatsSobre(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		var pagadorCie = pagadorCieService.findById(pagadorCieId);
		var cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute(PAGADOR_CIE, cieCommand);
		return "pagadorCieSobreAdminList";
	}
	
	@GetMapping(value = "/{pagadorCieId}/formats/fulla/new")
	public String getFormFormatFullaNew(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		var pagadorCie = pagadorCieService.findById(pagadorCieId);
		var cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute(PAGADOR_CIE, cieCommand);
		model.addAttribute(new PagadorCieFormatFullaCommand());
		return FORM;
	}
	
	@GetMapping(value = "/{pagadorCieId}/formats/fulla/{formatFullaId}")
	public String getFormFormatFulla(@PathVariable Long pagadorCieId, HttpServletRequest request, @PathVariable Long formatFullaId, Model model) {

		var pagadorCie = pagadorCieService.findById(pagadorCieId);
		var cieCommand = CieCommand.asCommand(pagadorCie);
		var pagadorCieFormatFulla = pagadorCieFormatFullaService.findById(formatFullaId);
		var pagadorCieFormatFullaCommand = pagadorCieFormatFulla != null ? PagadorCieFormatFullaCommand.asCommand(pagadorCieFormatFulla) : new PagadorCieFormatFullaCommand();
		model.addAttribute(PAGADOR_CIE, cieCommand);
		model.addAttribute(pagadorCieFormatFullaCommand);
		return FORM;
	}
	
	@GetMapping(value = "/{pagadorCieId}/formats/fulla/{formatFullaId}/delete")
	public String formatFullaDelete(@PathVariable Long pagadorCieId, HttpServletRequest request, @PathVariable Long formatFullaId, Model model) {
		
		pagadorCieFormatFullaService.delete(formatFullaId);
		var url = "redirect:../../cie/" + pagadorCieId + "/formats/fulla";
		return getAjaxControllerReturnValueSuccess(request, url,"cie.format.fulla.controller.esborrat.ok");
	}
	
	@PostMapping(value = "/{pagadorCieId}/formats/fulla/newOrModify")
	public String saveFormatFulla(@PathVariable Long pagadorCieId, HttpServletRequest request, @Valid PagadorCieFormatFullaCommand pagadorCieFormatFullCommand,
								  BindingResult bindingResult, Model model) {

		var pagadorCie = pagadorCieService.findById(pagadorCieId);
		var cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute(PAGADOR_CIE, cieCommand);
		if (bindingResult.hasErrors()) {
			return FORM;
		}
		var url = "redirect:pagadorsCie";
		var msg = pagadorCieFormatFullCommand.getId() != null ? "cie.format.fulla.controller.modificat.ok" : "cie.format.fulla.controller.creat.ok";
		if (pagadorCieFormatFullCommand.getId() != null) {
			pagadorCieFormatFullaService.update(PagadorCieFormatFullaCommand.asDto(pagadorCieFormatFullCommand));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		pagadorCieFormatFullaService.create(pagadorCieId, PagadorCieFormatFullaCommand.asDto(pagadorCieFormatFullCommand));
		return getModalControllerReturnValueSuccess(request, url, msg);
	}

	@GetMapping(value = "/{pagadorCieId}/formats/sobre/new")
	public String getFormFormatSobreNew(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		var pagadorCie = pagadorCieService.findById(pagadorCieId);
		var cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute(PAGADOR_CIE, cieCommand);
		model.addAttribute(new PagadorCieFormatSobreCommand());
		return "pagadorCieSobreAdminForm";
	}
	
	@GetMapping(value = "/{pagadorCieId}/formats/sobre/{formatSobreId}")
	public String getFormFormatSobre(@PathVariable Long pagadorCieId, HttpServletRequest request, @PathVariable Long formatSobreId, Model model) {

		var pagadorCie = pagadorCieService.findById(pagadorCieId);
		var cieCommand = CieCommand.asCommand(pagadorCie);
		var pagadorCieFormatSobre = pagadorCieFormatSobreService.findById(formatSobreId);
		var pagadorCieFormatSobreCommand = pagadorCieFormatSobre != null ? PagadorCieFormatSobreCommand.asCommand(pagadorCieFormatSobre) : new PagadorCieFormatSobreCommand();
		model.addAttribute(PAGADOR_CIE, cieCommand);
		model.addAttribute(pagadorCieFormatSobreCommand);
		return "pagadorCieSobreAdminForm";
	}
	
	@GetMapping(value = "/{pagadorCieId}/formats/sobre/{formatSobreId}/delete")
	public String formatSobreDelete(@PathVariable Long pagadorCieId, HttpServletRequest request, @PathVariable Long formatSobreId, Model model) {
		
		pagadorCieFormatSobreService.delete(formatSobreId);
		var url = "redirect:../../cie/" + pagadorCieId + "/formats/sobre";
		return getAjaxControllerReturnValueSuccess(request, url, "cie.format.sobre.controller.esborrat.ok");
	}
	
	@GetMapping(value = "/{pagadorCieId}/formats/sobre/newOrModify")
	public String saveFormatSobre(@PathVariable Long pagadorCieId, HttpServletRequest request, @Valid PagadorCieFormatSobreCommand pagadorCieFormatSobreCommand,
								  BindingResult bindingResult, Model model) {

		var pagadorCie = pagadorCieService.findById(pagadorCieId);
		var cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute(PAGADOR_CIE, cieCommand);
		if (bindingResult.hasErrors()) {
			return FORM;
		}
		var url = "redirect:pagadorsCie";
		var msg = pagadorCieFormatSobreCommand.getId() != null ? "cie.format.sobre.controller.modificat.ok" : "cie.format.sobre.controller.creat.ok";
		if (pagadorCieFormatSobreCommand.getId() != null) {
			pagadorCieFormatSobreService.update(PagadorCieFormatSobreCommand.asDto(pagadorCieFormatSobreCommand));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		pagadorCieFormatSobreService.create(pagadorCieId, PagadorCieFormatSobreCommand.asDto(pagadorCieFormatSobreCommand));
		return getModalControllerReturnValueSuccess(request, url, msg);
	}
	
	@GetMapping(value = "/{pagadorCieId}/formats/fulla/datatable")
	@ResponseBody
	public DatatablesResponse datatableFulla(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		var params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		var formatFullaPagadorCie = pagadorCieFormatFullaService.findAllPaginat(pagadorCieId, params);
		return DatatablesHelper.getDatatableResponse(request, formatFullaPagadorCie, "id");
	}
	
	@GetMapping(value = "/{pagadorCieId}/formats/sobre/datatable")
	@ResponseBody
	public DatatablesResponse datatableSobre(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		var params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		var formatSobrePagadorCie = pagadorCieFormatSobreService.findAllPaginat(pagadorCieId, params);
		return DatatablesHelper.getDatatableResponse(request, formatSobrePagadorCie, "id");
	}
	
}
