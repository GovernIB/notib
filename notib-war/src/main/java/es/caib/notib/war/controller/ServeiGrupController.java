package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.ProcSerGrupDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.api.service.ServeiService;
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
@RequestMapping("/servei")
public class ServeiGrupController extends BaseUserController{

	
	@Autowired
	EntitatService entitatService;
	@Autowired
	ProcedimentService procedimentService;
	@Autowired
	ServeiService serveiService;
	@Autowired
	GrupService grupService;

	@RequestMapping(value = "/{serveiId}/grup", method = RequestMethod.GET)
	public String permis(HttpServletRequest request, @PathVariable Long serveiId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute("servei", serveiService.findById(entitatActual.getId(), isAdministrador(request), serveiId));
		return "serveiAdminGrup";
	}
	
	@RequestMapping(value = "/{serveiId}/grup/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, @PathVariable Long serveiId, Model model) {
		return get(request, serveiId, null, model);
	}
	
	@RequestMapping(value = "/{serveiId}/grup/{grupId}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable Long grupId, Model model) {
		
		ProcSerGrupDto serveiGrup = emplenarModelGrups(request, serveiId, grupId, model);
		ProcedimentGrupCommand serveiGrupCommand = serveiGrup != null ? ProcedimentGrupCommand.asCommand(serveiGrup) : new ProcedimentGrupCommand();
		model.addAttribute("serveiGrupCommand", serveiGrupCommand);
		return "serveiAdminGrupForm";
	}
	
	@RequestMapping(value = "/{serveiId}/grup/{grupId}/delete", method = RequestMethod.GET)
	@ResponseBody
	public String delete(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable Long grupId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		procedimentService.grupDelete(entitatActual.getId(), grupId);
		MissatgesHelper.success(request, getMessage(request, "servei.controller.grup.esborrat.ok"));
		return "ok";
	}
	
	private ProcSerGrupDto emplenarModelGrups(HttpServletRequest request, Long serveiId, Long grupId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		List<GrupDto> grups = organGestorActual == null ? grupService.findByEntitat(entitatActual.getId())
								: grupService.findByEntitatAndOrganGestor(entitatActual, organGestorActual);
		model.addAttribute("grups", grups);
		model.addAttribute("servei", serveiService.findById(entitatActual.getId(), isAdministrador(request), serveiId));
		if (grupId == null) {
			return null;
		}
		ProcSerGrupDto serveiGrups = grupService.findProcedimentGrupById(entitatActual.getId(), grupId);
		model.addAttribute(serveiGrups);
		return serveiGrups;
	}
	
	@RequestMapping(value = "/{serveiId}/grup", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @PathVariable Long serveiId, @Valid ProcedimentGrupCommand command, BindingResult bindingResult, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "serveiAdminGrupForm";
		}
		String url = "redirect:../../servei/" + serveiId + "/grup";
		String msg = command.getId() != null ? "servei.controller.grup.modificat.ok" : "servei.controller.grup.creat.ok";
		if (command.getId() != null) {
			procedimentService.grupUpdate(entitatActual.getId(), serveiId, ProcedimentGrupCommand.asDto(command));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		procedimentService.grupCreate(entitatActual.getId(), serveiId, ProcedimentGrupCommand.asDto(command));
		return getModalControllerReturnValueSuccess(request, url, msg);
	}

	@RequestMapping(value = "/{serveiId}/grup/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long serveiId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return DatatablesHelper.getDatatableResponse(request, grupService.findByProcSer(entitatActual.getId(), serveiId, params), "id");
	}
	
	private boolean isAdministrador(HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
	}
}
