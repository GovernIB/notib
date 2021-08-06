package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.CodiValorEstatDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.cie.CieDataDto;
import es.caib.notib.core.api.dto.cie.CieDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.war.command.CieCommand;
import es.caib.notib.war.command.CieFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.RequestSessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controlador per el mantinemnt de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/cie")
public class CieController extends BaseUserController{
	
	private final static String PAGADOR_CIE_FILTRE = "pagadorcie_filtre";
	@Autowired
	private PagadorCieService pagadorCieService;
	@Autowired
	private OrganGestorService organGestorService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		CieFiltreCommand cieFiltreCommand = getFiltreCommand(request);
		model.addAttribute("cieFiltreCommand", cieFiltreCommand);
		return "cieList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( 
			HttpServletRequest request ) {
		CieFiltreCommand cieFiltreCommand = getFiltreCommand(request);
		PaginaDto<CieDto> pagadorsCie = null;
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		if (organGestorActual != null) {
			cieFiltreCommand.setOrganGestorId(organGestorActual.getId());
		} else {
			cieFiltreCommand.setOrganGestorId(null);
		}
			
		pagadorsCie = pagadorCieService.findAmbFiltrePaginat(
							entitat.getId(),
				cieFiltreCommand.asDto(),
							DatatablesHelper.getPaginacioDtoFromRequest(request));
		
		return DatatablesHelper.getDatatableResponse(
				request, 
				pagadorsCie, 
				"id");
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			Model model) {
		String vista = formGet(request, null, model);
		return vista;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(	
			HttpServletRequest request,
			CieFiltreCommand command,
			Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request, 
				PAGADOR_CIE_FILTRE, 
				command);
		
		return "cieList";
	}
	
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid CieCommand cieCommand,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			List<CodiValorEstatDto> organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
			model.addAttribute("organsGestors", organsGestors);
			return "cieForm";
		}
		// if it is modified
		if (cieCommand.getId() != null) {
			pagadorCieService.update(
					cieCommand.asDto());
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:cie",
					"cie.controller.modificat.ok");
		//if it is new	
		} else {
			CieDataDto dto = cieCommand.asDto();
			OrganGestorDto organGestorActual = getOrganGestorActual(request);
			if (organGestorActual != null)
				dto.setOrganGestorId(organGestorActual.getId());
			
			pagadorCieService.create(
					entitatActual.getId(),
					dto);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:cie",
					"cie.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/{pagadorCieId}", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@PathVariable Long pagadorCieId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		CieCommand cieCommand = null;
		CieDto pagadorCie = null;
		
		if (pagadorCieId != null) {
			pagadorCie = pagadorCieService.findById(pagadorCieId);
			
			model.addAttribute(pagadorCie);
		}
		
		if (pagadorCie != null)
			cieCommand = CieCommand.asCommand(pagadorCie);
		else
			cieCommand = new CieCommand();
		
		model.addAttribute(cieCommand);
		List<CodiValorEstatDto> organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
		model.addAttribute("organsGestors", organsGestors);
		return "cieForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long pagadorCieId) {		
		try {
			pagadorCieService.delete(pagadorCieId);
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(
						request,
						"redirect:../../cie",
						"cie.controller.esborrat.ora.ko");
		}
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../cie",
				"cie.controller.esborrat.ok");
	}
	
	private CieFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		CieFiltreCommand cieFiltreCommand = (CieFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				PAGADOR_CIE_FILTRE);
		if (cieFiltreCommand == null) {
			cieFiltreCommand = new CieFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					PAGADOR_CIE_FILTRE,
					cieFiltreCommand);
		}
		return cieFiltreCommand;
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class, 
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
}
