package es.caib.notib.back.controller;

import es.caib.notib.back.command.ProcSerPermisCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.service.*;
import es.caib.notib.logic.intf.service.ProcedimentService.TipusPermis;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * Controlador per el mantinemnt de permisos de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/procediment")
public class ProcedimentPermisController extends BaseUserController{

	@Autowired
	ProcedimentService procedimentService;
	@Autowired
	EntitatService entitatService;
	@Autowired
	OperadorPostalService operadorPostalService;
	@Autowired
	PagadorCieService pagadorCieService;
	@Autowired
	OrganGestorService organGestorService;

	private static final String PROCEDIMENT = "procediment";
	private static final String ORGANS = "organs";
	private static final String PERMIS = "/permis";
	private static final String PERMIS_FORM = "procedimentAdminPermisForm";


	@GetMapping(value = "/{procedimentId}/permis")
	public String get(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		var isAdministrador = RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
		var entitatActual = getEntitatActualComprovantPermisos(request);
		var procediment = procedimentService.findById(entitatActual.getId(), isAdministrador, procedimentId);
		model.addAttribute(PROCEDIMENT, procediment);
		return "procedimentAdminPermis";
	}

	@GetMapping(value = "/{procedimentId}/permis/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		var paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		var entitatActual = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		var organCodi = organGestorActual != null ? organGestorActual.getCodi() : null;
		var permisos = procedimentService.permisFind(entitatActual.getId(), isAdministrador(request), procedimentId, null, organCodi, null, paginacioParams);
		return DatatablesHelper.getDatatableResponse(request, permisos, "id");
	}

	@GetMapping(value = "/{procedimentId}/permis/new")
	public String getNew(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		var permisCommand = new ProcSerPermisCommand();
		model.addAttribute("principalSize", permisCommand.getPrincipalDefaultSize());
		return get(request, procedimentId, null, model);
	}

	@GetMapping(value = "/{procedimentId}/permis/{permisId}")
	public String get(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable Long permisId, Model model) {
		return getPermis(request, procedimentId, permisId, model, TipusPermis.PROCEDIMENT, null, null);
	}

	@GetMapping(value = "/{procedimentId}/organ/{organ}/permis/{permisId}")
	public String getPermisOrgan(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable String organ, @PathVariable Long permisId, Model model) {
		return getPermis(request, procedimentId, permisId, model, TipusPermis.PROCEDIMENT_ORGAN, organ, null);
	}

	private String getPermis(HttpServletRequest request, Long procedimentId, Long permisId, Model model, TipusPermis tipus, String organ, PaginacioParamsDto paginacioParams) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		var procediment = procedimentService.findById(entitatActual.getId(), isAdministrador(request), procedimentId);
		model.addAttribute(PROCEDIMENT, procediment);
		PermisDto permis = null;
		if (permisId != null) {
			var organCodi = organGestorActual != null ? organGestorActual.getCodi() : null;
			var permisos = procedimentService.permisFind(entitatActual.getId(), isAdministrador(request), procedimentId, organ, organCodi, tipus, paginacioParams);
			for (var p: permisos) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		if (procediment.isComu()) {
			model.addAttribute(ORGANS, getOrganismes(request));
		}
		var command = permis != null ? ProcSerPermisCommand.asCommand(permis, ProcSerPermisCommand.EntitatPermis.PROCEDIMENT) : new ProcSerPermisCommand();
		if (!procediment.isComu()) {
			command.setOrgan(procediment.getOrganGestor());
		}
		model.addAttribute(command);
		return PERMIS_FORM;
	}

	@PostMapping(value = "/{procedimentId}/permis")
	public String save(HttpServletRequest request, @PathVariable Long procedimentId, @Valid ProcSerPermisCommand command, BindingResult bindingResult, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors() || StringUtils.isEmpty(command.getOrgan())) {
			model.addAttribute(PROCEDIMENT, procedimentService.findById(entitatActual.getId(), isAdministrador(request), procedimentId));
			if (command.getOrgan() != null) {
				model.addAttribute(ORGANS, getOrganismes(request));
			}
			model.addAttribute("principalSize", command.getPrincipalDefaultSize());
			return PERMIS_FORM;
		}
		if (TipusEnumDto.ROL.equals(command.getTipus()) && command.getPrincipal().equalsIgnoreCase("tothom") && RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual())) {
			model.addAttribute(PROCEDIMENT, procedimentService.findById(entitatActual.getId(), isAdministrador(request), procedimentId));
			if (command.getOrgan() != null) {
				model.addAttribute(ORGANS, getOrganismes(request));
			}
			return getModalControllerReturnValueError(request, PERMIS_FORM, "procediment.controller.permis.modificat.ko");
		}
		var organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisUpdate(entitatActual.getId(), organGestorActualId, procedimentId, ProcSerPermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(request, "redirect:../../procediment/" + procedimentId + PERMIS, "procediment.controller.permis.modificat.ok");
	}

	@GetMapping(value = "/{procedimentId}/permis/{permisId}/delete")
	public String delete(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable Long permisId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisDelete(entitatActual.getId(), organGestorActualId, procedimentId, null, permisId, TipusPermis.PROCEDIMENT);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../../../procediment/" + procedimentId + PERMIS, "procediment.controller.permis.esborrat.ok");
	}

	@GetMapping(value = "/{procedimentId}/organ/{organ}/permis/{permisId}/delete")
	public String deletePermisOrgan(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable String organ, @PathVariable Long permisId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisDelete(entitatActual.getId(), organGestorActualId, procedimentId, organ, permisId, TipusPermis.PROCEDIMENT_ORGAN);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../../../procediment/" + procedimentId + PERMIS, "procediment.controller.permis.esborrat.ok");
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
		int index = organismes.indexOf(organismeActual);
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
