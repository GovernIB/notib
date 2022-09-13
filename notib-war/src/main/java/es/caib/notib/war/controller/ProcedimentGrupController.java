package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.ProcSerGrupDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.ProcedimentGrupCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.MissatgesHelper;
import es.caib.notib.war.helper.RolHelper;
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
import java.util.List;

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

	@RequestMapping(value = "/{procedimentId}/grup", method = RequestMethod.GET)
	public String permis(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute("procediment", procedimentService.findById(entitatActual.getId(), isAdministrador(request), procedimentId));
		return "procedimentAdminGrup";
	}
	
	@RequestMapping(value = "/{procedimentId}/grup/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {
		return get(request, procedimentId, null, model);
	}
	
	@RequestMapping(value = "/{procedimentId}/grup/{grupId}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable Long grupId, Model model) {
		
		ProcedimentGrupCommand procedimentGrupCommand;
		ProcSerGrupDto procedimentGrup = emplenarModelGrups(request, procedimentId, grupId, model);
		procedimentGrupCommand = procedimentGrup != null ? ProcedimentGrupCommand.asCommand(procedimentGrup) : new ProcedimentGrupCommand();
		model.addAttribute(procedimentGrupCommand);
		return "procedimentAdminGrupForm";
	}
	
	@RequestMapping(value = "/{procedimentId}/grup/{grupId}/delete", method = RequestMethod.GET)
	@ResponseBody
	public String delete(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable Long grupId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		procedimentService.grupDelete(entitatActual.getId(), grupId);
		MissatgesHelper.success(request, getMessage(request, "procediment.controller.grup.esborrat.ok"));
		return "ok";
	}
	
	private ProcSerGrupDto emplenarModelGrups(HttpServletRequest request, Long procedimentId, Long grupId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ProcSerGrupDto procedimentGrups = null;
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		List<GrupDto> grups = organGestorActual == null ? grupService.findByEntitat(entitatActual.getId())
								: grupService.findByEntitatAndOrganGestor(entitatActual, organGestorActual);
		model.addAttribute("grups", grups);
		model.addAttribute("procediment", procedimentService.findById(entitatActual.getId(), isAdministrador(request), procedimentId));
		if (grupId == null) {
			return procedimentGrups;
		}
		procedimentGrups = grupService.findProcedimentGrupById(entitatActual.getId(), grupId);
		model.addAttribute(procedimentGrups);
		return procedimentGrups;
	}
	
	@RequestMapping(value = "/{procedimentId}/grup", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @PathVariable Long procedimentId, @Valid ProcedimentGrupCommand command, BindingResult bindingResult, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "procedimentAdminGrupForm";
		}
		String url = "redirect:../../procediment/" + procedimentId + "/grup";
		String msg = command.getId() != null ? "procediment.controller.grup.modificat.ok" : "procediment.controller.grup.create.ok";
		if (command.getId() != null) {
			procedimentService.grupUpdate(entitatActual.getId(), procedimentId, ProcedimentGrupCommand.asDto(command));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		procedimentService.grupCreate(entitatActual.getId(), procedimentId, ProcedimentGrupCommand.asDto(command));
		return getModalControllerReturnValueSuccess(request, url, msg);
	}

	@RequestMapping(value = "/{procedimentId}/grup/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, grupService.findByProcSer(entitatActual.getId(), procedimentId, params), "id");
	}
	
	private boolean isAdministrador(HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
	}
}
