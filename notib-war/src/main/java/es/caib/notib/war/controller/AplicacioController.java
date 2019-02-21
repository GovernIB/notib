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
@RequestMapping("/aplicacio")
public class AplicacioController extends BaseController {

	@Autowired
	private UsuariAplicacioService usuariAplicacioService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		return "aplicacioList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		return DatatablesHelper.getDatatableResponse(
				request,
				usuariAplicacioService.findPaginat(
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String create(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new AplicacioCommand());
		return "aplicacioForm";
	}

	@RequestMapping(value = "/{aplicacioId}", method = RequestMethod.GET)
	public String update(
			HttpServletRequest request,
			Model model,
			@PathVariable Long aplicacioId) {
		AplicacioDto dto = null;
		if (aplicacioId != null) {
			dto = usuariAplicacioService.findById(aplicacioId);
		}
		if (dto != null) {
			model.addAttribute(AplicacioCommand.asCommand(dto));
		} else {
			model.addAttribute(new AplicacioCommand());
		}
		return "aplicacioForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			Model model,
			@Valid AplicacioCommand command,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
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
			@PathVariable Long aplicacioId) {
		usuariAplicacioService.delete(aplicacioId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:aplicacio",
				"aplicacio.controller.esborrada.ok");
	}

}
