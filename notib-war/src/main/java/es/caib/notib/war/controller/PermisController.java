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
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/entitats/{entitatId}/permis")
public class PermisController extends BaseController {
	
	@Autowired
	private EntitatService entitatService;


	@RequestMapping(value="", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) {
		
		model.addAttribute(
				"entitat", 
				entitatService.findById(entitatId)
				);
		
		return "adminPermis";
		
	}
	
	@RequestMapping(value="/entitatactual", method = RequestMethod.GET)
	public String getPermisActual(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) {
		
		model.addAttribute(
				"entitat", 
				entitatService.findById(entitatId)
				);
		
		return "adminPermis";
		
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		
		List<PermisDto> permisos = null;
		permisos = entitatService.findPermis(entitatId);
		
		return DatatablesHelper.getDatatableResponse(request, permisos);
		
	}
	
	@RequestMapping(value="/new", method = RequestMethod.GET)
	public String New(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) {
		
		model.addAttribute( new PermisCommand() );
		
		model.addAttribute( "entitatId", entitatId );
		
		return "adminPermisForm";
		
	}
	
	@RequestMapping(value="/{permisId}", method = RequestMethod.GET)
	public String Update(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable Long permisId,
			Model model) {
		
		model.addAttribute( "entitatId", entitatId );
		
		List<PermisDto> permisos = null;
		permisos = entitatService.findPermis(entitatId);
		
		PermisDto permis = null;
		for (PermisDto p: permisos) {
			if (p.getId().equals(permisId)) {
				permis = p;
				break;
			}
		}
		
		model.addAttribute( PermisCommand.asCommand(permis) );
		
		return "adminPermisForm";
		
	}
	
	@RequestMapping(value="/create", method = RequestMethod.POST)
	public String Create(
			Model model,
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@Valid PermisCommand command,
			BindingResult bindingResult) {
		
		if( bindingResult.hasErrors() ) return "adminPermisForm";
		
		PermisDto dto = PermisCommand.asDto( command );
		
		entitatService.updatePermis(entitatId, dto);
		
		String msg;
		if (command.getId() == null)
			msg = "entitat.controller.permis.creat.ok";
		else
			msg = "entitat.controller.permis.modificat.ok";
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/entitats/" + entitatId + "/permis/",
				msg);
		
	}
	
	@RequestMapping(value="/{permisId}/delete", method = RequestMethod.GET)
	public String Delete(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable Long permisId,
			Model model) {
		
		entitatService.deletePermis( entitatId, permisId );
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/entitats/" + entitatId + "/permis/",
				"entitat.controller.permis.esborrat.ok");
		
	}
	

}
