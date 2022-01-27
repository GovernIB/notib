package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto.OrdreDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.war.command.PermisCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.PermisosHelper;
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
import javax.xml.bind.ValidationException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

	@RequestMapping(value = "/{organGestorId}/permis", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long organGestorId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		OrganGestorDto organGestor = organGestorService.findById(
				entitatActual.getId(),
				organGestorId);
					
		model.addAttribute(
				"organGestor",
				organGestor);
		
		return "organGestorPermis";
	}

	@RequestMapping(value = "/{organGestorId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request, 
			@PathVariable Long organGestorId, 
			Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		PaginacioParamsDto paginacio = DatatablesHelper.getPaginacioDtoFromRequest(request);
		List<PermisDto> permisos = organGestorService.permisFind(entitatActual.getId(), organGestorId, paginacio);
		return DatatablesHelper.getDatatableResponse(request, permisos, "id");
	}
	
	@RequestMapping(value = "/{organGestorId}/permis/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long organGestorId,
			Model model) throws ValidationException {
		PermisCommand permisCommand = new PermisCommand();
		model.addAttribute("principalSize", permisCommand.getPrincipalDefaultSize());
		return get(request, organGestorId, null, model);
	}
	
	@RequestMapping(value = "/{organGestorId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long organGestorId,
			@PathVariable Long permisId,
			Model model) throws ValidationException {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute("organGestor", organGestorService.findById(entitatActual.getId(), organGestorId));
		PermisDto permis = null;
		boolean isAdminOrgan= RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
		if (permisId != null) {
			List<PermisDto> permisos = organGestorService.permisFind(entitatActual.getId(), organGestorId, null);
			for (PermisDto p: permisos) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		if (isAdminOrgan && permis != null && permis.isAdministrador()) {
			throw new ValidationException("Un administrador d'òrgan no pot gestionar el permís d'admministrador d'òrgans gestors");
		}
		model.addAttribute(permis != null ? PermisCommand.asCommand(permis) : new PermisCommand());
		return "organGestorPermisForm";
	}
	
	@RequestMapping(value = "/{organGestorId}/permis", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long organGestorId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) throws NotFoundException, ValidationException {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute("organGestor", organGestorService.findById(entitatActual.getId(), organGestorId));
			model.addAttribute("principalSize", command.getPrincipalDefaultSize());
			return "organGestorPermisForm";
		}

		String msg = command.getId() == null ? "creat" : "modificat";
		if (TipusEnumDto.ROL.equals(command.getTipus()) && command.getPrincipal().equalsIgnoreCase("tothom") &&
				RolHelper.isUsuariActualUsuariAdministradorOrgan(request)) {

			model.addAttribute("organGestor", organGestorService.findById(entitatActual.getId(), organGestorId));
			return getModalControllerReturnValueError(request,"organGestorPermisForm",
					"organgestor.controller.permis." + msg + ".ko");
		}


		boolean isAdminOrgan= RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
		organGestorService.permisUpdate(entitatActual.getId(), organGestorId, isAdminOrgan, PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(request, "redirect:../../organgestor/" + organGestorId + "/permis",
				"organgestor.controller.permis." + msg + ".ok");
	}
	
	@RequestMapping(value = "/{organGestorId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long organGestorId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		organGestorService.permisDelete(
				entitatActual.getId(),
				organGestorId,
				permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../organgestor/" + organGestorId + "/permis",
				"organgestor.controller.permis.esborrat.ok");
	}
}
