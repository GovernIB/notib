package es.caib.notib.back.controller;

import es.caib.notib.back.command.PermisCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.TipusEnumDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import javax.xml.bind.ValidationException;
import java.util.ArrayList;

/**
 * Controlador per el mantinemnt de permisos de organs gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/organgestor")
public class OrganGestorPermisController extends BaseUserController{
	
	@Autowired
	OrganGestorService organGestorService;
	@Autowired
	EntitatService entitatService;

	private static final String ORGAN_GESTOR = "organGestor";
	private static final String ORGAN_GESTOR_PERMIS_FORM = "organGestorPermisForm";


	@GetMapping(value = "/{organGestorId}/permis")
	public String get(HttpServletRequest request, @PathVariable Long organGestorId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var organGestor = organGestorService.findById(entitatActual.getId(), organGestorId);
		model.addAttribute(ORGAN_GESTOR, organGestor);
		return "organGestorPermis";
	}

	@GetMapping(value = "/{organGestorId}/permis/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, @PathVariable Long organGestorId, Model model) {

		if (organGestorId == 0) {
			return DatatablesHelper.getDatatableResponse(request, new ArrayList<PermisDto>(), "id");
		}
		try {
			var entitatActual = getEntitatActualComprovantPermisos(request);
			var paginacio = DatatablesHelper.getPaginacioDtoFromRequest(request);
			var permisos = organGestorService.permisFind(entitatActual.getId(), organGestorId, paginacio);
			return DatatablesHelper.getDatatableResponse(request, permisos, "id");
		} catch(Exception ex) {
			String msg = getMessage(request, "organgestor.permis.datatable.error", new Object[] {
					"<button class=\"btn btn-default btn-xs pull-right\" data-toggle=\"collapse\" data-target=\"#collapseError\" aria-expanded=\"false\" aria-controls=\"collapseError\">\n" +
					"\t\t\t\t<span class=\"fa fa-bars\"></span>\n" +
					"\t\t\t</button>\n" +
					"\t\t\t<div id=\"collapseError\" class=\"collapse\">\n" +
					"\t\t\t\t<br/>\n" +
					"\t\t\t\t<textarea rows=\"10\" style=\"width:100%\">" + ExceptionUtils.getStackTrace(ex) +"</textarea>\n" +
					"\t\t\t</div>"});
			MissatgesHelper.error(request, msg);

		}

		return DatatablesHelper.getDatatableResponse(request, new ArrayList<>(), "id");
	}

	@GetMapping(value = "/{codiSia}/permisos")
	@ResponseBody
	public DatatablesResponse getPermisos(HttpServletRequest request, @PathVariable String codiSia, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		if (entitatActual == null) {
			return DatatablesHelper.getDatatableResponse(request, new ArrayList<PermisDto>(), "id");
		}
		var paginacio = DatatablesHelper.getPaginacioDtoFromRequest(request);
		var organ = organGestorService.findByCodi(entitatActual.getId(), codiSia);
		if (organ == null) {
			return DatatablesHelper.getDatatableResponse(request, new ArrayList<PermisDto>(), "id");
		}
		var permisos = organGestorService.permisFind(entitatActual.getId(), organ.getId(), paginacio);
		return DatatablesHelper.getDatatableResponse(request, permisos, "id");
	}

	@GetMapping(value = "/{organGestorId}/permis/new")
	public String getNew(HttpServletRequest request, @PathVariable Long organGestorId, Model model) throws ValidationException {

		var permisCommand = new PermisCommand();
		model.addAttribute("principalSize", permisCommand.getPrincipalDefaultSize());
		return get(request, organGestorId, null, model);
	}
	
	@GetMapping(value = "/{organGestorId}/permis/{permisId}")
	public String get(HttpServletRequest request, @PathVariable Long organGestorId, @PathVariable Long permisId, Model model) throws ValidationException {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(ORGAN_GESTOR, organGestorService.findById(entitatActual.getId(), organGestorId));
		PermisDto permis = null;
		var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());
		if (permisId != null) {
			var permisos = organGestorService.permisFind(entitatActual.getId(), organGestorId, null);
			for (var p: permisos) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		if (isAdminOrgan && permis != null && permis.isAdministrador()) {
			throw new ValidationException("Un administrador d'òrgan no pot gestionar el permís d'admministrador d'òrgans gestors");
		}
		model.addAttribute(permis != null ? PermisCommand.asCommand(permis, PermisCommand.EntitatPermis.ORGAN) : new PermisCommand());
		return ORGAN_GESTOR_PERMIS_FORM;
	}
	
	@PostMapping(value = "/{organGestorId}/permis")
	public String save(HttpServletRequest request, @PathVariable Long organGestorId, @Valid PermisCommand command, BindingResult bindingResult, Model model) throws NotFoundException, ValidationException {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(ORGAN_GESTOR, organGestorService.findById(entitatActual.getId(), organGestorId));
			model.addAttribute("principalSize", command.getPrincipalDefaultSize());
			return ORGAN_GESTOR_PERMIS_FORM;
		}

		var msg = command.getId() == null ? "creat" : "modificat";
		if (TipusEnumDto.ROL.equals(command.getTipus()) && command.getPrincipal().equalsIgnoreCase("tothom") &&
				RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual())) {

			model.addAttribute(ORGAN_GESTOR, organGestorService.findById(entitatActual.getId(), organGestorId));
			return getModalControllerReturnValueError(request,ORGAN_GESTOR_PERMIS_FORM, "organgestor.controller.permis." + msg + ".ko");
		}

		var isAdminOrgan= RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());
		organGestorService.permisUpdate(entitatActual.getId(), organGestorId, isAdminOrgan, PermisCommand.asDto(command));
		var text = "organgestor.controller.permis." + msg + ".ok";
		return getModalControllerReturnValueSuccess(request, "redirect:../../organgestor/" + organGestorId + "/permis", text);
	}
	
	@GetMapping(value = "/{organGestorId}/permis/{permisId}/delete")
	public String delete(HttpServletRequest request, @PathVariable Long organGestorId, @PathVariable Long permisId, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		organGestorService.permisDelete(entitatActual.getId(), organGestorId, permisId);
		var url = "redirect:../../../../organgestor/" + organGestorId + "/permis";
		return getAjaxControllerReturnValueSuccess(request, url, "organgestor.controller.permis.esborrat.ok");
	}
}
