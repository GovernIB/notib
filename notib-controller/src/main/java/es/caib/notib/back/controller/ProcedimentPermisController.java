package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ProcedimentService.TipusPermis;
import es.caib.notib.back.command.PermisCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RolHelper;
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
	GrupService grupsService;
	@Autowired
	OrganGestorService organGestorService;

	@RequestMapping(value = "/{procedimentId}/permis", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		boolean isAdministrador = RolHelper.isUsuariActualAdministrador(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ProcSerDto procediment = procedimentService.findById(entitatActual.getId(), isAdministrador, procedimentId);
		model.addAttribute("procediment", procediment);
		return "procedimentAdminPermis";
	}

	@RequestMapping(value = "/{procedimentId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		String codi = organGestorActual != null ? organGestorActual.getCodi() : null;
		List<PermisDto> permisos = procedimentService.permisFind(entitatActual.getId(), isAdministrador(request), procedimentId, null, codi, null, paginacioParams);
		return DatatablesHelper.getDatatableResponse(request, permisos,	"id");
	}
	
	@RequestMapping(value = "/{procedimentId}/permis/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		PermisCommand permisCommand = new PermisCommand();
		model.addAttribute("principalSize", permisCommand.getPrincipalDefaultSize());
		return get(request, procedimentId, null, model);
	}
	
	@RequestMapping(value = "/{procedimentId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable Long permisId, Model model) {
		return getPermis(request, procedimentId, permisId, model, TipusPermis.PROCEDIMENT, null, null);
	}
	
	@RequestMapping(value = "/{procedimentId}/organ/{organ}/permis/{permisId}", method = RequestMethod.GET)
	public String getPermisOrgan(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable String organ, @PathVariable Long permisId, Model model) {
		return getPermis(request, procedimentId, permisId, model, TipusPermis.PROCEDIMENT_ORGAN, organ, null);
	}

	private String getPermis(HttpServletRequest request, Long procedimentId, Long permisId, Model model, TipusPermis tipus, String organ, PaginacioParamsDto paginacioParams) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		var procediment = procedimentService.findById(entitatActual.getId(), isAdministrador(request), procedimentId);
		model.addAttribute("procediment", procediment);
		PermisDto permis = null;
		if (permisId != null) {
			var codi = organGestorActual != null ? organGestorActual.getCodi() : null;
			var permisos = procedimentService.permisFind(entitatActual.getId(), isAdministrador(request), procedimentId, organ, codi, tipus, paginacioParams);
			for (var p: permisos) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		model.addAttribute(permis != null ? PermisCommand.asCommand(permis, PermisCommand.EntitatPermis.PROCEDIMENT) : new PermisCommand());
		if (procediment.isComu()) {
			model.addAttribute("organs", getOrganismes(request));
		}
		return "procedimentAdminPermisForm";
	}

	@RequestMapping(value = "/{procedimentId}/permis", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @PathVariable Long procedimentId, @Valid PermisCommand command, BindingResult bindingResult, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute("entitat", procedimentService.findById(entitatActual.getId(), isAdministrador(request), procedimentId));
			if (command.getOrgan() != null) {
				model.addAttribute("organs", getOrganismes(request));
			}
			model.addAttribute("principalSize", command.getPrincipalDefaultSize());
			return "procedimentAdminPermisForm";
		}

		boolean isRol = RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
		if (TipusEnumDto.ROL.equals(command.getTipus()) && command.getPrincipal().equalsIgnoreCase("tothom") && isRol) {
			model.addAttribute("procediment", procedimentService.findById(entitatActual.getId(), isAdministrador(request), procedimentId));
			if (command.getOrgan() != null) {
				model.addAttribute("organs", getOrganismes(request));
			}
			return getModalControllerReturnValueError(request, "procedimentAdminPermisForm", "procediment.controller.permis.modificat.ko");
		}
		
		Long organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisUpdate(entitatActual.getId(), organGestorActualId, procedimentId, PermisCommand.asDto(command));
		String url = "redirect:../../procediment/" + procedimentId + "/permis";
		return getModalControllerReturnValueSuccess(request, url, "procediment.controller.permis.modificat.ok");
	}
	
	@RequestMapping(value = "/{procedimentId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable Long permisId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Long organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisDelete(entitatActual.getId(), organGestorActualId, procedimentId, null, permisId, TipusPermis.PROCEDIMENT);
		String url = "redirect:../../../../procediment/" + procedimentId + "/permis";
		return getAjaxControllerReturnValueSuccess(request, url, "procediment.controller.permis.esborrat.ok");
	}
	
	@RequestMapping(value = "/{procedimentId}/organ/{organ}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String deletePermisOrgan(HttpServletRequest request, @PathVariable Long procedimentId, @PathVariable String organ, @PathVariable Long permisId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Long organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisDelete(entitatActual.getId(), organGestorActualId, procedimentId, organ, permisId, TipusPermis.PROCEDIMENT_ORGAN);
		String url = "redirect:../../../../procediment/" + procedimentId + "/permis";
		return getAjaxControllerReturnValueSuccess(request, url, "procediment.controller.permis.esborrat.ok");
	}
	
	private List<OrganismeDto> getOrganismes(HttpServletRequest request) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		List<OrganismeDto> organismes;
		OrganismeDto organismeActual = OrganismeDto.builder().build();
		if (organGestorActual != null) {
			organismes = organGestorService.findOrganismes(entitat, organGestorActual);
			organismeActual.setCodi(organGestorActual.getCodi());
			organismeActual.setNom(organGestorActual.getNom());
		} else {
			organismes = organGestorService.findOrganismes(entitat);
			organismeActual.setCodi(entitat.getDir3Codi());
			organismeActual.setNom("Global");
		}
		int index = organismes.indexOf(organismeActual);
		if (index != -1) {
			organismes.remove(index);
			organismes.add(0, organismeActual);
		}
		return organismes;
	}
	
	private boolean isAdministrador(HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
	}
}
