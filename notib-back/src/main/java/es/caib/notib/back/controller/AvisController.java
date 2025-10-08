package es.caib.notib.back.controller;

import es.caib.notib.back.command.AvisCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.dto.AvisDto;
import es.caib.notib.logic.intf.service.AvisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Controlador per al manteniment de avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/avis")
public class AvisController extends TableAccionsMassivesController {
	
	@Autowired
	private AvisService avisService;

	private static final String SESSION_ATTRIBUTE_SELECCIO = "AvisController.session.seleccio";
	private static final String REDIRECT = "redirect:../../avis";

	public AvisController() {
		super.sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO;
	}


	@GetMapping
	public String get() {
		return "avisList";
	}

	@ResponseBody
	@GetMapping(value = "/datatable")
	public DatatablesResponse datatable(HttpServletRequest request) {

		var avisos = avisService.findPaginat(DatatablesHelper.getPaginacioDtoFromRequest(request));
		return DatatablesHelper.getDatatableResponse(request, avisos, "id", SESSION_ATTRIBUTE_SELECCIO);
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

		var avis = avisService.updateActiva(avisId, true);
		return avis != null ? getAjaxControllerReturnValueSuccess(request, "redirect:../../avis", "avis.controller.activat.ok")
				: getAjaxControllerReturnValueError(request, "redirect:../../avis", "avis.controller.activat.ko");
	}

	@GetMapping(value = "/enable/massiu")
	public String enableMassiu(HttpServletRequest request) {

		var ids = getIdsSeleccionats(request);
		AvisDto avis;
		List<Long> idsError = new ArrayList<>();
		for (var id : ids) {
			avis = avisService.updateActiva(id, true);
			if (avis == null) {
				idsError.add(id);
			}
		}
		return idsError.isEmpty() ? getAjaxControllerReturnValueSuccess(request, REDIRECT, "avis.controller.activat.massiu.ok")
				: getAjaxControllerReturnValueError(request, REDIRECT, "avis.controller.activat.massiu.ko", new Object[]{idsError});
	}

	@GetMapping(value = "/{avisId}/disable")
	public String disable(HttpServletRequest request, @PathVariable Long avisId) {

		var avis = avisService.updateActiva(avisId, false);
		return avis != null ? getAjaxControllerReturnValueSuccess(request, "redirect:../../avis", "avis.controller.desactivat.ok")
				: getAjaxControllerReturnValueError(request, "redirect:../../avis", "avis.controller.desactivat.ko");
	}

	@GetMapping(value = "/disable/massiu")
	public String disableMassiu(HttpServletRequest request) {

		var ids = getIdsSeleccionats(request);
		AvisDto avis;
		List<Long> idsError = new ArrayList<>();
		for (var id : ids) {
			avis = avisService.updateActiva(id, false);
			if (avis == null) {
				idsError.add(id);
			}
		}
		return idsError.isEmpty() ? getAjaxControllerReturnValueSuccess(request, REDIRECT, "avis.controller.desactivat.massiu.ok")
				: getAjaxControllerReturnValueError(request, REDIRECT, "avis.controller.desactivat.massiu.ko", idsError.toArray());
	}

	@GetMapping(value = "/{avisId}/delete")
	public String delete(HttpServletRequest request, @PathVariable Long avisId) {

		var avis = avisService.delete(avisId);
		return avis != null ? getAjaxControllerReturnValueSuccess(request, REDIRECT, "avis.controller.esborrat.ok")
				: getAjaxControllerReturnValueError(request, REDIRECT, "avis.controller.esborrat.ko");
	}

	@GetMapping(value = "/delete/massiu")
	public String deleteMassiu(HttpServletRequest request) {

		var ids = getIdsSeleccionats(request);
		AvisDto avis;
		List<Long> idsError = new ArrayList<>();
		for (var id : ids) {
			avis = avisService.delete(id);
			if (avis == null) {
				idsError.add(id);
			}
		}
		RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<Long>());
		if (idsError.isEmpty()) {
			return getAjaxControllerReturnValueSuccess(request, REDIRECT, "avis.controller.esborrat.massiu.ok");
		} else {
			return getAjaxControllerReturnValueError(request, REDIRECT, "avis.controller.esborrat.massiu.ko", new Object[]{idsError});
		}
	}


	@Override
	protected List<Long> getIdsElementsFiltrats(HttpServletRequest request) throws ParseException {
		return avisService.findAllIds();
	}
}
