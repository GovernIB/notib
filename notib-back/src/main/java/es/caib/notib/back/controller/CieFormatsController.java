package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatFullaDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.service.*;
import es.caib.notib.back.command.CieCommand;
import es.caib.notib.back.command.PagadorCieFormatFullaCommand;
import es.caib.notib.back.command.PagadorCieFormatSobreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla", method = RequestMethod.GET)
	public String getFormatsFulla(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		CieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		CieCommand cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", cieCommand);
		return "pagadorCieFullaAdminList";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/sobre", method = RequestMethod.GET)
	public String getFormatsSobre(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		CieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		CieCommand cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", cieCommand);
		return "pagadorCieSobreAdminList";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla/new", method = RequestMethod.GET)
	public String getFormFormatFullaNew(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		CieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		CieCommand cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", cieCommand);
		model.addAttribute(new PagadorCieFormatFullaCommand());
		return "pagadorCieFullaAdminForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla/{formatFullaId}", method = RequestMethod.GET)
	public String getFormFormatFulla(@PathVariable Long pagadorCieId, HttpServletRequest request, @PathVariable Long formatFullaId, Model model) {

		CieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		CieCommand cieCommand = CieCommand.asCommand(pagadorCie);
		PagadorCieFormatFullaCommand pagadorCieFormatFullaCommand;
		CieFormatFullaDto pagadorCieFormatFulla;
		pagadorCieFormatFulla = pagadorCieFormatFullaService.findById(formatFullaId);
		pagadorCieFormatFullaCommand = pagadorCieFormatFulla != null ? PagadorCieFormatFullaCommand.asCommand(pagadorCieFormatFulla) : new PagadorCieFormatFullaCommand();
		model.addAttribute("pagadorCie", cieCommand);
		model.addAttribute(pagadorCieFormatFullaCommand);
		return "pagadorCieFullaAdminForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla/{formatFullaId}/delete", method = RequestMethod.GET)
	public String formatFullaDelete(@PathVariable Long pagadorCieId, HttpServletRequest request, @PathVariable Long formatFullaId, Model model) {
		
		pagadorCieFormatFullaService.delete(formatFullaId);
		String url = "redirect:../../cie/" + pagadorCieId + "/formats/fulla";
		return getAjaxControllerReturnValueSuccess(request, url,"cie.format.fulla.controller.esborrat.ok");
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla/newOrModify", method = RequestMethod.POST)
	public String saveFormatFulla(@PathVariable Long pagadorCieId, HttpServletRequest request, @Valid PagadorCieFormatFullaCommand pagadorCieFormatFullCommand,
								  BindingResult bindingResult, Model model) {

		CieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		CieCommand cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", cieCommand);
		if (bindingResult.hasErrors()) {
			return "pagadorCieFullaAdminForm";
		}
		String url = "redirect:pagadorsCie";
		String msg = pagadorCieFormatFullCommand.getId() != null ? "cie.format.fulla.controller.modificat.ok" : "cie.format.fulla.controller.creat.ok";
		if (pagadorCieFormatFullCommand.getId() != null) {
			pagadorCieFormatFullaService.update(PagadorCieFormatFullaCommand.asDto(pagadorCieFormatFullCommand));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		pagadorCieFormatFullaService.create(pagadorCieId, PagadorCieFormatFullaCommand.asDto(pagadorCieFormatFullCommand));
		return getModalControllerReturnValueSuccess(request, url, msg);
	}

	@RequestMapping(value = "/{pagadorCieId}/formats/sobre/new", method = RequestMethod.GET)
	public String getFormFormatSobreNew(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		CieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		CieCommand cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", cieCommand);
		model.addAttribute(new PagadorCieFormatSobreCommand());
		return "pagadorCieSobreAdminForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/sobre/{formatSobreId}", method = RequestMethod.GET)
	public String getFormFormatSobre(@PathVariable Long pagadorCieId, HttpServletRequest request, @PathVariable Long formatSobreId, Model model) {

		CieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		CieCommand cieCommand = CieCommand.asCommand(pagadorCie);
		PagadorCieFormatSobreCommand pagadorCieFormatSobreCommand;
		CieFormatSobreDto pagadorCieFormatSobre;
		pagadorCieFormatSobre = pagadorCieFormatSobreService.findById(formatSobreId);
		pagadorCieFormatSobreCommand = pagadorCieFormatSobre != null ? PagadorCieFormatSobreCommand.asCommand(pagadorCieFormatSobre) : new PagadorCieFormatSobreCommand();
		model.addAttribute("pagadorCie", cieCommand);
		model.addAttribute(pagadorCieFormatSobreCommand);
		return "pagadorCieSobreAdminForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/sobre/{formatSobreId}/delete", method = RequestMethod.GET)
	public String formatSobreDelete(@PathVariable Long pagadorCieId, HttpServletRequest request, @PathVariable Long formatSobreId, Model model) {
		
		pagadorCieFormatSobreService.delete(formatSobreId);
		String url = "redirect:../../cie/" + pagadorCieId + "/formats/sobre";
		return getAjaxControllerReturnValueSuccess(request, url, "cie.format.sobre.controller.esborrat.ok");
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/sobre/newOrModify", method = RequestMethod.POST)
	public String saveFormatSobre(@PathVariable Long pagadorCieId, HttpServletRequest request, @Valid PagadorCieFormatSobreCommand pagadorCieFormatSobreCommand,
								  BindingResult bindingResult, Model model) {

		CieDto pagadorCie = pagadorCieService.findById(pagadorCieId);
		CieCommand cieCommand = CieCommand.asCommand(pagadorCie);
		model.addAttribute("pagadorCie", cieCommand);
		if (bindingResult.hasErrors()) {
			return "pagadorCieFullaAdminForm";
		}
		String url = "redirect:pagadorsCie";
		String msg = pagadorCieFormatSobreCommand.getId() != null ? "cie.format.sobre.controller.modificat.ok" : "cie.format.sobre.controller.creat.ok";
		if (pagadorCieFormatSobreCommand.getId() != null) {
			pagadorCieFormatSobreService.update(PagadorCieFormatSobreCommand.asDto(pagadorCieFormatSobreCommand));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		pagadorCieFormatSobreService.create(pagadorCieId, PagadorCieFormatSobreCommand.asDto(pagadorCieFormatSobreCommand));
		return getModalControllerReturnValueSuccess(request, url, msg);
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/fulla/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatableFulla(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		PaginaDto<CieFormatFullaDto> formatFullaPagadorCie = pagadorCieFormatFullaService.findAllPaginat(pagadorCieId, params);
		return DatatablesHelper.getDatatableResponse(request, formatFullaPagadorCie, "id");
	}
	
	@RequestMapping(value = "/{pagadorCieId}/formats/sobre/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatableSobre(@PathVariable Long pagadorCieId, HttpServletRequest request, Model model) {

		PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		PaginaDto<CieFormatSobreDto> formatSobrePagadorCie = pagadorCieFormatSobreService.findAllPaginat(pagadorCieId, params);
		return DatatablesHelper.getDatatableResponse(request, formatSobrePagadorCie, "id");
	}
	
}
