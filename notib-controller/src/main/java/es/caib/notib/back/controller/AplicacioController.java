/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.command.AplicacioCommand;
import es.caib.notib.back.command.AplicacioFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.UsuariAplicacioService;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

/**
 * Controlador per al manteniment d'aplicacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/entitat/{entitatId}/aplicacio")
public class AplicacioController extends BaseController {

	@Autowired
	private UsuariAplicacioService usuariAplicacioService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private EntitatService entitatService;

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
	public String save(HttpServletRequest request, Model model, @PathVariable Long entitatId, @Valid AplicacioCommand command, BindingResult bindingResult) {

		String redirect = "redirect:aplicacio";
		try {
			if (bindingResult.hasErrors()) {
				model.addAttribute("entitat", entitatService.findById(entitatId));
				return "aplicacioForm";
			}
			AplicacioDto aplicacio = AplicacioCommand.asDto(command);
			if (!aplicacioService.existeixUsuariNotib(aplicacio.getUsuariCodi())) {
				aplicacioService.crearUsuari(aplicacio.getUsuariCodi());
			}
			if (command.getId() == null) {
				usuariAplicacioService.create(aplicacio);
				return getModalControllerReturnValueSuccess(request, redirect, "aplicacio.controller.creada.ok");
			}
			usuariAplicacioService.update(aplicacio);
			return getModalControllerReturnValueSuccess(request, redirect, "aplicacio.controller.modificada.ok");
		} catch (Exception ex) {
			String msg = "Error creant l'aplicació d'usari";
			log.error(msg, ex);
			return getModalControllerReturnValueError(request, redirect, "aplicacio.controller.upsert.error");
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
