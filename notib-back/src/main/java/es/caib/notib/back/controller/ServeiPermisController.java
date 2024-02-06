package es.caib.notib.back.controller;

import es.caib.notib.back.command.ProcSerPermisCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ProcedimentService.TipusPermis;
import es.caib.notib.logic.intf.service.ServeiService;
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
import java.util.List;

/**
 * Controlador per el mantinemnt de permisos de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/servei")
public class ServeiPermisController extends BaseUserController{
	
	@Autowired
	ProcedimentService procedimentService;
	@Autowired
	ServeiService serveiService;
	@Autowired
	EntitatService entitatService;
	@Autowired
	OperadorPostalService operadorPostalService;
	@Autowired
	PagadorCieService pagadorCieService;
	@Autowired
	OrganGestorService organGestorService;

	private static final String SERVEI = "servei";
	private static final String ORGANS = "organs";
	private static final String SERVEI_ADMIN_PERMIS_FORM = "serveiAdminPermisForm";
	private static final String PERMIS = "/permis";


	@GetMapping(value = "/{serveiId}/permis")
	public String get(HttpServletRequest request, @PathVariable Long serveiId, Model model) {

		var isAdministrador = RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
		var entitatActual = getEntitatActualComprovantPermisos(request);
		var servei = serveiService.findById(entitatActual.getId(), isAdministrador, serveiId);
		model.addAttribute(SERVEI, servei);
		return "serveiAdminPermis";
	}

	@GetMapping(value = "/{serveiId}/permis/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long serveiId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		var paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		var organCodi = organGestorActual != null ? organGestorActual.getCodi() : null;
		var permisos = procedimentService.permisFind(entitatActual.getId(), isAdministrador(request), serveiId, null, organCodi, null, paginacioParams);
		return DatatablesHelper.getDatatableResponse(request, permisos,	"id");
	}

	@GetMapping(value = "/{serveiId}/permis/new")
	public String getNew(HttpServletRequest request, @PathVariable Long serveiId, Model model) {

		var permisCommand = new ProcSerPermisCommand();
		model.addAttribute("principalSize", permisCommand.getPrincipalDefaultSize());
		return get(request, serveiId, null, model);
	}
	
	@GetMapping(value = "/{serveiId}/permis/{permisId}")
	public String get(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable Long permisId, Model model) {

		var paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return getPermis(request, serveiId, permisId, model, TipusPermis.PROCEDIMENT, null, paginacioParams);
	}
	
	@GetMapping(value = "/{serveiId}/organ/{organ}/permis/{permisId}")
	public String getPermisOrgan(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable String organ, @PathVariable Long permisId, Model model) {

		var paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return getPermis(request, serveiId, permisId, model, TipusPermis.PROCEDIMENT_ORGAN, organ, paginacioParams);
	}

	private String getPermis(HttpServletRequest request, Long serveiId, Long permisId, Model model, TipusPermis tipus, String organ, PaginacioParamsDto paginacioParams) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		var servei = serveiService.findById(entitatActual.getId(), isAdministrador(request), serveiId);
		model.addAttribute(SERVEI, servei);
		PermisDto permis = null;
		if (permisId != null) {
			var organCodi = organGestorActual != null ? organGestorActual.getCodi() : null;
			var permisos = procedimentService.permisFind(entitatActual.getId(), isAdministrador(request), serveiId, organ, organCodi, tipus, paginacioParams);
			for (var p: permisos) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		if (servei.isComu()) {
			model.addAttribute(ORGANS, getOrganismes(request));
		}
		var command = permis != null ? ProcSerPermisCommand.asCommand(permis, ProcSerPermisCommand.EntitatPermis.SERVEI) : new ProcSerPermisCommand();
		if (!servei.isComu()) {
			command.setOrgan(servei.getOrganGestor());
		}
		model.addAttribute(command);
		return SERVEI_ADMIN_PERMIS_FORM;
	}
	
	@PostMapping(value = "/{serveiId}/permis")
	public String save(HttpServletRequest request, @PathVariable Long serveiId, @Valid ProcSerPermisCommand command, BindingResult bindingResult, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(SERVEI, serveiService.findById(entitatActual.getId(), isAdministrador(request), serveiId));
			if (command.getOrgan() != null) {
				model.addAttribute(ORGANS, getOrganismes(request));
			}
			model.addAttribute("principalSize", command.getPrincipalDefaultSize());
			return SERVEI_ADMIN_PERMIS_FORM;
		}
		
		if (TipusEnumDto.ROL.equals(command.getTipus()) && command.getPrincipal().equalsIgnoreCase("tothom") && RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual())) {
			model.addAttribute(SERVEI, serveiService.findById(entitatActual.getId(), isAdministrador(request), serveiId));
			if (command.getOrgan() != null) {
				model.addAttribute(ORGANS, getOrganismes(request));
			}
			return getModalControllerReturnValueError(request, SERVEI_ADMIN_PERMIS_FORM, "servei.controller.permis.modificat.ko");
		}
		var organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisUpdate(entitatActual.getId(), organGestorActualId, serveiId, ProcSerPermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(request, "redirect:../../servei/" + serveiId + PERMIS, "servei.controller.permis.modificat.ok");
	}
	
	@GetMapping(value = "/{serveiId}/permis/{permisId}/delete")
	public String delete(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable Long permisId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisDelete(entitatActual.getId(), organGestorActualId, serveiId, null, permisId, TipusPermis.PROCEDIMENT);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../../../servei/" + serveiId + PERMIS, "servei.controller.permis.esborrat.ok");
	}
	
	@GetMapping(value = "/{serveiId}/organ/{organ}/permis/{permisId}/delete")
	public String deletePermisOrgan(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable String organ, @PathVariable Long permisId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisDelete(entitatActual.getId(), organGestorActualId, serveiId, organ, permisId, TipusPermis.PROCEDIMENT_ORGAN);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../../../servei/" + serveiId + PERMIS, "servei.controller.permis.esborrat.ok");
	}
	
	private List<OrganismeDto> getOrganismes(HttpServletRequest request) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		List<OrganismeDto> organismes;
		var organismeActual = OrganismeDto.builder().build();
		if (organGestorActual != null) {
			organismes = organGestorService.findOrganismes(entitat, organGestorActual);
			organismeActual.setCodi(organGestorActual.getCodi());
			organismeActual.setNom(organGestorActual.getNom());
		} else {
			organismes = organGestorService.findOrganismes(entitat);
		}
		var index = organismes.indexOf(organismeActual);
		if (index != -1) {
			organismes.remove(index);
			organismes.add(0, organismeActual);
		}
		return organismes.subList(1, organismes.size());
	}
	
	private boolean isAdministrador(HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
	}
	
}
