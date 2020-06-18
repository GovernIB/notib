/**
 * 
 */
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
import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.UsuariAplicacioService;
import es.caib.notib.war.command.AplicacioCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;

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

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) {
		model.addAttribute(
				"entitat", 
				entitatService.findById(entitatId));
		return "aplicacioList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		return DatatablesHelper.getDatatableResponse(
				request,
				usuariAplicacioService.findPaginatByEntitat(
						entitatId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String create(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) {
		model.addAttribute(new AplicacioCommand());
		model.addAttribute(
				"entitat", 
				entitatService.findById(entitatId));
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

	@RequestMapping(method = RequestMethod.POST)
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

}
