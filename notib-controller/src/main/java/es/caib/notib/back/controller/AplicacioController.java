/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.UsuariAplicacioService;
import es.caib.notib.back.command.AplicacioCommand;
import es.caib.notib.back.command.AplicacioFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RequestSessionHelper;
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
import java.util.ArrayList;

/**
 * Controlador per al manteniment d'aplicacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/entitat/{entitatId}/aplicacio")
public class AplicacioController extends BaseController {

	@Autowired private UsuariAplicacioService usuariAplicacioService;
	@Autowired private EntitatService entitatService;

	private final static String APLICACIO_FILTRE = "aplicacio_filtre";

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long entitatId, Model model) {

		var command = getFiltreCommand(request);
		model.addAttribute("aplicacioFiltreCommand", command);
		model.addAttribute("entitat", entitatService.findById(entitatId));
		return "aplicacioList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(HttpServletRequest request, @PathVariable Long entitatId, AplicacioFiltreCommand command, Model model ) {

		RequestSessionHelper.actualitzarObjecteSessio(request, APLICACIO_FILTRE, command);
		model.addAttribute("aplicacioFiltreCommand", command);
		model.addAttribute("entitat", entitatService.findById(entitatId));
		return "aplicacioList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long entitatId) {


		var params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		prepararFiltres(request, params);
		var apps = usuariAplicacioService.findPaginatByEntitat(entitatId, params);
		return DatatablesHelper.getDatatableResponse(request, apps);
	}

	private void prepararFiltres(HttpServletRequest request, PaginacioParamsDto params) {

		var command = getFiltreCommand(request);
		if (command == null) {
			return;
		}
		params.setFiltres(new ArrayList<>());
		params.afegirFiltre("codiUsuari", command.getCodiUsuari());
		params.afegirFiltre("callbackUrl", command.getCallbackUrl());
		params.afegirFiltre("activa", command.getActiva());
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String create(HttpServletRequest request, @PathVariable Long entitatId, Model model) {

		model.addAttribute(new AplicacioCommand());
		model.addAttribute("entitat", entitatService.findById(entitatId));
		return "aplicacioForm";
	}

	@RequestMapping(value = "/{aplicacioId}", method = RequestMethod.GET)
	public String update(HttpServletRequest request, Model model, @PathVariable Long entitatId, @PathVariable Long aplicacioId) {

		var dto = aplicacioId != null ? usuariAplicacioService.findByEntitatAndId(entitatId, aplicacioId) : null;
		model.addAttribute(dto != null ? AplicacioCommand.asCommand(dto) : new AplicacioCommand());
		model.addAttribute("entitat", entitatService.findById(entitatId));
		return "aplicacioForm";
	}

	@RequestMapping(value = "newOrModify", method = RequestMethod.POST)
	public String save(HttpServletRequest request, Model model, @PathVariable Long entitatId, @Valid AplicacioCommand command, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {model.addAttribute("entitat", entitatService.findById(entitatId));
			return "aplicacioForm";
		}
		var url = "redirect:aplicacio";
		var msg = command.getId() == null ? "aplicacio.controller.creada.ok" : "aplicacio.controller.modificada.ok";
		if (command.getId() == null) {
			usuariAplicacioService.create(AplicacioCommand.asDto(command));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		usuariAplicacioService.update(AplicacioCommand.asDto(command));
		return getModalControllerReturnValueSuccess(request, url, msg);
	}

	@RequestMapping(value = "/{aplicacioId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, Model model, @PathVariable Long entitatId, @PathVariable Long aplicacioId) {

		usuariAplicacioService.delete(aplicacioId, entitatId);
		return getAjaxControllerReturnValueSuccess(request, "redirect:aplicacio", "aplicacio.controller.esborrada.ok");
	}
	
	@RequestMapping(value = "/{aplicacioId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long aplicacioId) {

		usuariAplicacioService.updateActiva(aplicacioId, true);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "aplicacio.controller.activada.ok");
	}
	@RequestMapping(value = "/{aplicacioId}/disable", method = RequestMethod.GET)
	public String disable(HttpServletRequest request, @PathVariable Long aplicacioId) {

		usuariAplicacioService.updateActiva(aplicacioId, false);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "aplicacio.controller.desactivada.ok");
	}

	private AplicacioFiltreCommand getFiltreCommand(HttpServletRequest request) {

		var command = (AplicacioFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, APLICACIO_FILTRE);
		if (command != null) {
			return command;
		}
		command = new AplicacioFiltreCommand();
		RequestSessionHelper.actualitzarObjecteSessio(request, APLICACIO_FILTRE, command);
		return command;
	}

}
