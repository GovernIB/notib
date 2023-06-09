package es.caib.notib.back.controller;

import es.caib.notib.back.command.ProcedimentGrupCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.procediment.ProcSerGrupDto;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controlador per el mantinemnt dels grups d'un procediments (VERSIÓ ANTERIOR)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/procediment")
public class ProcedimentGrupController extends BaseUserController{
	
	@Autowired
	EntitatService entitatService;
	@Autowired
	ProcedimentService procedimentService;
	@Autowired
	GrupService grupService;

	@GetMapping(value = "/{procedimentId}/grup")
	public String permis(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute("procediment", procedimentService.findById(entitatActual.getId(), isAdministrador(), procedimentId));
		return "procedimentAdminGrup";
	}
	
	@GetMapping(value = "/{procedimentId}/grup/new")
	public String getNew(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {
		return get(request, procedimentId, null, model);
	}
	
	@GetMapping(value = "/{procedimentId}/grup/{grupId}")
	public String get(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable Long grupId, Model model) {
		
		var procedimentGrup = emplenarModelGrups(request, procedimentId, grupId, model);
		var procedimentGrupCommand = procedimentGrup != null ? ProcedimentGrupCommand.asCommand(procedimentGrup) : new ProcedimentGrupCommand();
		model.addAttribute(procedimentGrupCommand);
		return "procedimentAdminGrupForm";
	}
	
	@GetMapping(value = "/{procedimentId}/grup/{grupId}/delete")
	@ResponseBody
	public String delete(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable Long grupId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		procedimentService.grupDelete(entitatActual.getId(), grupId);
		MissatgesHelper.success(request, getMessage(request, "procediment.controller.grup.esborrat.ok"));
		return "ok";
	}
	
	private ProcSerGrupDto emplenarModelGrups(HttpServletRequest request, Long procedimentId, Long grupId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		ProcSerGrupDto procedimentGrups = null;
		var organGestorActual = getOrganGestorActual(request);
		var grups = organGestorActual == null ? grupService.findByEntitat(entitatActual.getId())
								: grupService.findByEntitatAndOrganGestor(entitatActual, organGestorActual);
		model.addAttribute("grups", grups);
		model.addAttribute("procediment", procedimentService.findById(entitatActual.getId(), isAdministrador(), procedimentId));
		if (grupId == null) {
			return procedimentGrups;
		}
		procedimentGrups = grupService.findProcedimentGrupById(entitatActual.getId(), grupId);
		model.addAttribute(procedimentGrups);
		return procedimentGrups;
	}
	
	@PostMapping(value = "/{procedimentId}/grup")
	public String save(HttpServletRequest request, @PathVariable Long procedimentId, @Valid ProcedimentGrupCommand command, BindingResult bindingResult, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "procedimentAdminGrupForm";
		}
		var url = "redirect:../../procediment/" + procedimentId + "/grup";
		var msg = command.getId() != null ? "procediment.controller.grup.modificat.ok" : "procediment.controller.grup.create.ok";
		if (command.getId() != null) {
			procedimentService.grupUpdate(entitatActual.getId(), procedimentId, ProcedimentGrupCommand.asDto(command));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		procedimentService.grupCreate(entitatActual.getId(), procedimentId, ProcedimentGrupCommand.asDto(command));
		return getModalControllerReturnValueSuccess(request, url, msg);
	}

	@GetMapping(value = "/{procedimentId}/grup/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, grupService.findByProcSer(entitatActual.getId(), procedimentId, params), "id");
	}
	
	private boolean isAdministrador() {
		return RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
	}
}
