/**
 * 
 */
package es.caib.notib.war.controller;

import java.util.List;
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
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.war.command.PermisCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment dels permisos d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/entitat/{entitatId}/permis")
public class EntitatPermisController extends BaseController {

	@Autowired
	private EntitatService entitatService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) {
		model.addAttribute(
				"entitat", 
				entitatService.findById(entitatId));
		return "entitatPermisList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		List<PermisDto> permisos = null;
		permisos = entitatService.permisFindByEntitatId(entitatId);
		return DatatablesHelper.getDatatableResponse(request, permisos);
	}

	@RequestMapping(value="/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) {
		model.addAttribute(
				"entitat", 
				entitatService.findById(entitatId));
		PermisCommand permisCommand = new PermisCommand();
		model.addAttribute(permisCommand);
		model.addAttribute("principalSize", permisCommand.getPrincipalDefaultSize());
		return "entitatPermisForm";
	}

	@RequestMapping(value="/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable Long permisId,
			Model model) {
		model.addAttribute(
				"entitat", 
				entitatService.findById(entitatId));
		List<PermisDto> permisos = null;
		permisos = entitatService.permisFindByEntitatId(entitatId);
		PermisDto permis = null;
		for (PermisDto p: permisos) {
			if (p.getId().equals(permisId)) {
				permis = p;
				break;
			}
		}
		model.addAttribute( PermisCommand.asCommand(permis) );
		return "entitatPermisForm";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(
			Model model,
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@Valid PermisCommand command,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"entitat",
					entitatService.findById(entitatId));
			model.addAttribute("principalSize", command.getPrincipalDefaultSize());
			return "entitatPermisForm";
		}
		
		entitatService.permisUpdate(
				entitatId, 
				PermisCommand.asDto(command));
		String msg;
		if (command.getId() == null) {
			msg = "entitat.controller.permis.creat.ok";
		} else {
			msg = "entitat.controller.permis.modificat.ok";
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/entitat/" + entitatId + "/permis/",
				msg);
	}

	@RequestMapping(value="/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable Long permisId,
			Model model) {
		entitatService.permisDelete(
				entitatId,
				permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/entitat/" + entitatId + "/permis/",
				"entitat.controller.permis.esborrat.ok");
	}

}
