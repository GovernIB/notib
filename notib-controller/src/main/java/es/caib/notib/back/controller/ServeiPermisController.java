package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.service.*;
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
	GrupService grupsService;
	@Autowired
	OrganGestorService organGestorService;
	
	@RequestMapping(value = "/{serveiId}/permis", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long serveiId, Model model) {

		boolean isAdministrador = RolHelper.isUsuariActualAdministrador(request);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ProcSerDto servei = serveiService.findById(entitatActual.getId(), isAdministrador, serveiId);
		model.addAttribute("servei", servei);
		return "serveiAdminPermis";
	}

	@RequestMapping(value = "/{serveiId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long serveiId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		String codi = organGestorActual != null ? organGestorActual.getCodi() : null;
		List<PermisDto> permisos = procedimentService.permisFind(entitatActual.getId(), isAdministrador(request), serveiId, null, codi, null, params);
		return DatatablesHelper.getDatatableResponse(request, permisos,	"id");
	}
	
	@RequestMapping(value = "/{serveiId}/permis/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, @PathVariable Long serveiId, Model model) {

		PermisCommand permisCommand = new PermisCommand();
		model.addAttribute("principalSize", permisCommand.getPrincipalDefaultSize());
		return get(request, serveiId, null, model);
	}
	
	@RequestMapping(value = "/{serveiId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable Long permisId, Model model) {

		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return getPermis(request, serveiId, permisId, model, TipusPermis.PROCEDIMENT, null, paginacioParams);
	}
	
	@RequestMapping(value = "/{serveiId}/organ/{organ}/permis/{permisId}", method = RequestMethod.GET)
	public String getPermisOrgan(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable String organ, @PathVariable Long permisId, Model model) {

		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		return getPermis(request, serveiId, permisId, model, TipusPermis.PROCEDIMENT_ORGAN, organ, paginacioParams);
	}

	private String getPermis(HttpServletRequest request, Long serveiId, Long permisId, Model model, TipusPermis tipus, String organ, PaginacioParamsDto paginacioParams) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		ProcSerDto servei = serveiService.findById(entitatActual.getId(), isAdministrador(request), serveiId);
		model.addAttribute("servei", servei);
		PermisDto permis = null;
		if (permisId != null) {
			String codi = organGestorActual != null ? organGestorActual.getCodi() : null;
			List<PermisDto> permisos = procedimentService.permisFind(entitatActual.getId(), isAdministrador(request), serveiId, organ, codi, tipus, paginacioParams);
			for (PermisDto p: permisos) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		model.addAttribute(permis != null ? PermisCommand.asCommand(permis, PermisCommand.EntitatPermis.SERVEI) : new PermisCommand());
		if (servei.isComu()) {
			model.addAttribute("organs", getOrganismes(request));
		}
		return "serveiAdminPermisForm";
	}
	
	@RequestMapping(value = "/{serveiId}/permis", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @PathVariable Long serveiId, @Valid PermisCommand command, BindingResult bindingResult, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute("servei", serveiService.findById(entitatActual.getId(), isAdministrador(request), serveiId));
			if (command.getOrgan() != null) {
				model.addAttribute("organs", getOrganismes(request));
			}
			model.addAttribute("principalSize", command.getPrincipalDefaultSize());
			return "serveiAdminPermisForm";
		}
		boolean isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
		if (TipusEnumDto.ROL.equals(command.getTipus()) && command.getPrincipal().equalsIgnoreCase("tothom") && isAdminOrgan) {
			model.addAttribute("servei", serveiService.findById(entitatActual.getId(), isAdministrador(request), serveiId));
			if (command.getOrgan() != null) {
				model.addAttribute("organs", getOrganismes(request));
			}
			return getModalControllerReturnValueError(request, "serveiAdminPermisForm", "servei.controller.permis.modificat.ko");
		}
		Long organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisUpdate(entitatActual.getId(), organGestorActualId, serveiId, PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(request, "redirect:../../servei/" + serveiId + "/permis", "servei.controller.permis.modificat.ok");
	}
	
	@RequestMapping(value = "/{serveiId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable Long permisId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Long organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisDelete(entitatActual.getId(), organGestorActualId, serveiId, null, permisId, TipusPermis.PROCEDIMENT);
		String url = "redirect:../../../../servei/" + serveiId + "/permis";
		return getAjaxControllerReturnValueSuccess(request, url, "servei.controller.permis.esborrat.ok");
	}
	
	@RequestMapping(value = "/{serveiId}/organ/{organ}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String deletePermisOrgan(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable String organ, @PathVariable Long permisId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Long organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisDelete(entitatActual.getId(), organGestorActualId, serveiId, organ, permisId, TipusPermis.PROCEDIMENT_ORGAN);
		String url = "redirect:../../../../servei/" + serveiId + "/permis";
		return getAjaxControllerReturnValueSuccess(request, url, "servei.controller.permis.esborrat.ok");
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
