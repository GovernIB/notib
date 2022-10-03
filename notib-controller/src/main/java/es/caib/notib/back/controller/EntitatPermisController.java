/**
 * 
 */
package es.caib.notib.back.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.back.command.PermisCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment dels permisos d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/entitat/{entitatId}/permis")
public class EntitatPermisController extends BaseController {

	@Autowired
	private EntitatService entitatService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long entitatId, Model model) {

		model.addAttribute("entitat", entitatService.findById(entitatId));
		return "entitatPermisList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long entitatId) {

		PaginacioParamsDto paginacio = DatatablesHelper.getPaginacioDtoFromRequest(request);
		List<PermisDto> permisos = entitatService.permisFindByEntitatId(entitatId, paginacio);
		return DatatablesHelper.getDatatableResponse(request, permisos);
	}

	@RequestMapping(value="/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, @PathVariable Long entitatId, Model model) {

		model.addAttribute("entitat", entitatService.findById(entitatId));
		PermisCommand permisCommand = new PermisCommand();
		model.addAttribute(permisCommand);
		model.addAttribute("principalSize", permisCommand.getPrincipalDefaultSize());
		return "entitatPermisForm";
	}

	@ResponseBody
	@RequestMapping(value="/{principal}/existeix", method = RequestMethod.GET)
	public boolean existeixPrincipal(HttpServletRequest request, @PathVariable Long entitatId, @PathVariable String principal, Model model) {

		System.out.println(principal);
		if (Strings.isNullOrEmpty(principal)) {
			return false;
		}
		try {
			return entitatService.existeixPermis(entitatId, principal);
		} catch (Exception ex) {
			log.error("Error consultant la existencia de l'usuari/rol " + principal, ex);
			return false;
		}
	}

	@RequestMapping(value="/{permisId}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long entitatId, @PathVariable Long permisId, Model model) {

		model.addAttribute("entitat", entitatService.findById(entitatId));
		List<PermisDto> permisos = null;
		permisos = entitatService.permisFindByEntitatId(entitatId, null);
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
	public String save(Model model, HttpServletRequest request, @PathVariable Long entitatId, @Valid PermisCommand command, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("entitat", entitatService.findById(entitatId));
			model.addAttribute("principalSize", command.getPrincipalDefaultSize());
			return "entitatPermisForm";
		}
		entitatService.permisUpdate(entitatId, PermisCommand.asDto(command));
		String msg = command.getId() == null ?  "entitat.controller.permis.creat.ok" : "entitat.controller.permis.modificat.ok";
		return getModalControllerReturnValueSuccess(request, "redirect:/entitat/" + entitatId + "/permis/", msg);
	}

	@RequestMapping(value="/{permisId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long entitatId, @PathVariable Long permisId, Model model) {

		entitatService.permisDelete(entitatId, permisId);
		return getAjaxControllerReturnValueSuccess(request, "redirect:/entitat/" + entitatId + "/permis/", "entitat.controller.permis.esborrat.ok");
	}
}
