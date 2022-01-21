/**
 * 
 */
package es.caib.notib.war.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.google.common.base.Strings;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.war.command.AplicacioFiltreCommand;
import es.caib.notib.war.command.ProcSerFiltreCommand;
import es.caib.notib.war.helper.RequestSessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.UsuariAplicacioService;
import es.caib.notib.war.command.AplicacioCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;

import java.util.ArrayList;
import java.util.List;

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

		AplicacioFiltreCommand command = getFiltreCommand(request);
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


		PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		prepararFiltres(request, params);
		PaginaDto<AplicacioDto> apps = usuariAplicacioService.findPaginatByEntitat(entitatId, params);
		return DatatablesHelper.getDatatableResponse(request, apps);
	}

	private void prepararFiltres(HttpServletRequest request, PaginacioParamsDto params) {

		List<PaginacioParamsDto.FiltreDto> filtres = new ArrayList<>();
		AplicacioFiltreCommand command = getFiltreCommand(request);
		if (command == null) {
			return;
		}
		params.setFiltres(new ArrayList<PaginacioParamsDto.FiltreDto>());
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
	public String update(
			HttpServletRequest request,
			Model model,
			@PathVariable Long entitatId,
			@PathVariable Long aplicacioId) {
		AplicacioDto dto = null;
		if (aplicacioId != null) {
			dto = usuariAplicacioService.findByEntitatAndId(entitatId, aplicacioId);
		}
		if (dto != null) {
			model.addAttribute(AplicacioCommand.asCommand(dto));
		} else {
			model.addAttribute(new AplicacioCommand());
		}
		model.addAttribute(
				"entitat", 
				entitatService.findById(entitatId));
		return "aplicacioForm";
	}

	@RequestMapping(value = "newOrModify", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			Model model,
			@PathVariable Long entitatId,
			@Valid AplicacioCommand command,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"entitat", 
					entitatService.findById(entitatId));
			return "aplicacioForm";
		}
		if (command.getId() == null) {
			usuariAplicacioService.create(
					AplicacioCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:aplicacio",
					"aplicacio.controller.creada.ok");
		} else {
			usuariAplicacioService.update(
					AplicacioCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:aplicacio",
					"aplicacio.controller.modificada.ok");
		}
	}

	@RequestMapping(value = "/{aplicacioId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			Model model,
			@PathVariable Long entitatId,
			@PathVariable Long aplicacioId) {
		usuariAplicacioService.delete(aplicacioId, entitatId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:aplicacio",
				"aplicacio.controller.esborrada.ok");
	}
	
	@RequestMapping(value = "/{aplicacioId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long aplicacioId) {
		usuariAplicacioService.updateActiva(aplicacioId, true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"aplicacio.controller.activada.ok");
	}
	@RequestMapping(value = "/{aplicacioId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long aplicacioId) {
		usuariAplicacioService.updateActiva(aplicacioId, false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"aplicacio.controller.desactivada.ok");
	}

	private AplicacioFiltreCommand getFiltreCommand(HttpServletRequest request) {

		AplicacioFiltreCommand command = (AplicacioFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, APLICACIO_FILTRE);
		if (command != null) {
			return command;
		}
		command = new AplicacioFiltreCommand();
		RequestSessionHelper.actualitzarObjecteSessio(request, APLICACIO_FILTRE, command);
		return command;
	}

}
