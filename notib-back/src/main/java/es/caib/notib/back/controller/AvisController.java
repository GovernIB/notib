package es.caib.notib.back.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.logic.intf.dto.AvisDto;
import es.caib.notib.logic.intf.service.AvisService;
import es.caib.notib.back.command.AvisCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/avis")
public class AvisController extends BaseUserController {
	
	@Autowired
	private AvisService avisService;


	@GetMapping
	public String get() {
		return "avisList";
	}

	@ResponseBody
	@GetMapping(value = "/datatable")
	public DatatablesResponse datatable(HttpServletRequest request) {
		return DatatablesHelper.getDatatableResponse(request, avisService.findPaginat(DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}

	@GetMapping(value = "/new")
	public String getNew(Model model) {
		return get(null, model);
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
	@GetMapping(value = "/{avisId}")
	public String get(@PathVariable Long avisId, Model model) {

		AvisDto avis = null;
		if (avisId != null) {
			avis = avisService.findById(avisId);
		}
		if (avis != null) {
			model.addAttribute(AvisCommand.asCommand(avis));
			return "avisForm";
		}
		var avisCommand = new AvisCommand();
		avisCommand.setDataInici(new Date());
		model.addAttribute(avisCommand);
		return "avisForm";
	}

	@PostMapping
	public String save(HttpServletRequest request, @Valid AvisCommand command, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return "avisForm";
		}
		var url = "redirect:avis";
		var msg = command.getId() != null ? "avis.controller.modificat.ok" : "avis.controller.creat.ok";
		if (command.getId() != null) {
			avisService.update(AvisCommand.asDto(command));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		avisService.create(AvisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(request, url, msg);
	}

	@GetMapping(value = "/{avisId}/enable")
	public String enable(HttpServletRequest request, @PathVariable Long avisId) {

		avisService.updateActiva(avisId, true);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../avis", "avis.controller.activat.ok");
	}

	@GetMapping(value = "/{avisId}/disable")
	public String disable(HttpServletRequest request, @PathVariable Long avisId) {

		avisService.updateActiva(avisId, false);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../avis", "avis.controller.desactivat.ok");
	}

	@GetMapping(value = "/{avisId}/delete")
	public String delete(HttpServletRequest request, @PathVariable Long avisId) {

		avisService.delete(avisId);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../avis", "avis.controller.esborrat.ok");
	}
}
