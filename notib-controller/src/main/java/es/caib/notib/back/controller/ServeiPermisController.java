package es.caib.notib.back.controller;

import es.caib.notib.back.command.ProcSerPermisCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
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
		PaginacioParamsDto paginacioParams = DatatablesHelper.getPaginacioDtoFromRequest(request);
		String organCodi = organGestorActual != null ? organGestorActual.getCodi() : null;
		List<PermisDto> permisos = procedimentService.permisFind(entitatActual.getId(), isAdministrador(request), serveiId, null, organCodi, null, paginacioParams);
		return DatatablesHelper.getDatatableResponse(request, permisos,	"id");
	}
	
	@RequestMapping(value = "/{serveiId}/permis/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, @PathVariable Long serveiId, Model model) {

		ProcSerPermisCommand permisCommand = new ProcSerPermisCommand();
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
			String organCodi = organGestorActual != null ? organGestorActual.getCodi() : null;
			List<PermisDto> permisos = procedimentService.permisFind(entitatActual.getId(), isAdministrador(request), serveiId, organ, organCodi, tipus, paginacioParams);
			for (PermisDto p: permisos) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		if (servei.isComu()) {
			model.addAttribute("organs", getOrganismes(request));
		}

		ProcSerPermisCommand command = permis != null ? ProcSerPermisCommand.asCommand(permis, ProcSerPermisCommand.EntitatPermis.SERVEI) : new ProcSerPermisCommand();
		if (!servei.isComu()) {
			command.setOrgan(servei.getOrganGestor());
		}
		model.addAttribute(command);
		return "serveiAdminPermisForm";
	}
	
	@RequestMapping(value = "/{serveiId}/permis", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @PathVariable Long serveiId, @Valid ProcSerPermisCommand command, BindingResult bindingResult, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute("servei", serveiService.findById(entitatActual.getId(), isAdministrador(request), serveiId));
			if (command.getOrgan() != null) {
				model.addAttribute("organs", getOrganismes(request));
			}
			model.addAttribute("principalSize", command.getPrincipalDefaultSize());
			return "serveiAdminPermisForm";
		}
		
		if (TipusEnumDto.ROL.equals(command.getTipus()) && command.getPrincipal().equalsIgnoreCase("tothom") && RolHelper.isUsuariActualUsuariAdministradorOrgan(request)) {
			model.addAttribute("servei", serveiService.findById(entitatActual.getId(), isAdministrador(request), serveiId));
			if (command.getOrgan() != null) {
				model.addAttribute("organs", getOrganismes(request));
			}
			return getModalControllerReturnValueError(request, "serveiAdminPermisForm", "servei.controller.permis.modificat.ko");
		}
		Long organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisUpdate(entitatActual.getId(), organGestorActualId, serveiId, ProcSerPermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(request, "redirect:../../servei/" + serveiId + "/permis", "servei.controller.permis.modificat.ok");
	}
	
	@RequestMapping(value = "/{serveiId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable Long permisId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Long organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisDelete(entitatActual.getId(), organGestorActualId, serveiId, null, permisId, TipusPermis.PROCEDIMENT);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../../../servei/" + serveiId + "/permis", "servei.controller.permis.esborrat.ok");
	}
	
	@RequestMapping(value = "/{serveiId}/organ/{organ}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String deletePermisOrgan(HttpServletRequest request, @PathVariable Long serveiId, @PathVariable String organ, @PathVariable Long permisId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Long organGestorActualId = getOrganGestorActualId(request);
		procedimentService.permisDelete(entitatActual.getId(), organGestorActualId, serveiId, organ, permisId, TipusPermis.PROCEDIMENT_ORGAN);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../../../servei/" + serveiId + "/permis", "servei.controller.permis.esborrat.ok");
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
//			organismeActual.setCodi(entitat.getDir3Codi());
//			organismeActual.setNom("Global");
		}
		int index = organismes.indexOf(organismeActual);
		if (index != -1) {
			organismes.remove(index);
			organismes.add(0, organismeActual);
		}
		return organismes.subList(1, organismes.size());
	}
	
	private boolean isAdministrador(HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
	}
	
}
