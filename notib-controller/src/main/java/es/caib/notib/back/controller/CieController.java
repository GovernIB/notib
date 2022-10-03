package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.dto.CodiValorEstatDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.CieTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.back.command.CieCommand;
import es.caib.notib.back.command.CieFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.RequestSessionHelper;
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
	public String get(HttpServletRequest request, Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		CieFiltreCommand cieFiltreCommand = getFiltreCommand(request);
		model.addAttribute("cieFiltreCommand", cieFiltreCommand);
		List<CodiValorEstatDto> organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitat.getId());
		model.addAttribute("organsGestors", organsGestors);
		return "cieList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request ) {

		CieFiltreCommand cieFiltreCommand = getFiltreCommand(request);
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		cieFiltreCommand.setOrganGestorId(organGestorActual != null ? organGestorActual.getId() : null);
		PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		PaginaDto<CieTableItemDto> pagadorsCie = pagadorCieService.findAmbFiltrePaginat(entitat.getId(), cieFiltreCommand.asDto(), params);
		return DatatablesHelper.getDatatableResponse(request, pagadorsCie, "id");
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, Model model) {
		return formGet(request, null, model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(HttpServletRequest request, CieFiltreCommand command, Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		RequestSessionHelper.actualitzarObjecteSessio(request, PAGADOR_CIE_FILTRE, command);
		List<CodiValorEstatDto> organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitat.getId());
		model.addAttribute("organsGestors", organsGestors);
		return "cieList";
	}
	
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid CieCommand cieCommand, BindingResult bindingResult, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			List<CodiValorEstatDto> organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
			model.addAttribute("organsGestors", organsGestors);
			return "cieForm";
		}
		String url = "redirect:cie";
		String msg = cieCommand.getId() != null ? "cie.controller.modificat.ok" : "cie.controller.creat.ok";
		if (cieCommand.getId() != null) {
			pagadorCieService.update(cieCommand.asDto());
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		CieDataDto dto = cieCommand.asDto();
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		if (organGestorActual != null) {
			dto.setOrganGestorId(organGestorActual.getId());
		}
		pagadorCieService.create(entitatActual.getId(), dto);
		return getModalControllerReturnValueSuccess(request, url, msg);
	}
	
	@RequestMapping(value = "/{pagadorCieId}", method = RequestMethod.GET)
	public String formGet(HttpServletRequest request, @PathVariable Long pagadorCieId, Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		CieCommand cieCommand = null;
		CieDto pagadorCie = null;
		if (pagadorCieId != null) {
			pagadorCie = pagadorCieService.findById(pagadorCieId);
			model.addAttribute(pagadorCie);
		}
		cieCommand = pagadorCie != null ? CieCommand.asCommand(pagadorCie) : new CieCommand();
		model.addAttribute(cieCommand);
		List<CodiValorEstatDto> organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
		model.addAttribute("organsGestors", organsGestors);
		return "cieForm";
	}
	
	@RequestMapping(value = "/{pagadorCieId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long pagadorCieId) {

		String url = "redirect:../../cie";
		try {
			pagadorCieService.delete(pagadorCieId);
			return getAjaxControllerReturnValueSuccess(request, url, "cie.controller.esborrat.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(request, url, "cie.controller.esborrat.ora.ko");
		}
	}
	
	private CieFiltreCommand getFiltreCommand(HttpServletRequest request) {

		CieFiltreCommand cieFiltreCommand = (CieFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, PAGADOR_CIE_FILTRE);
		if (cieFiltreCommand != null) {
			return cieFiltreCommand;
		}
		cieFiltreCommand = new CieFiltreCommand();
		RequestSessionHelper.actualitzarObjecteSessio(request, PAGADOR_CIE_FILTRE, cieFiltreCommand);
		return cieFiltreCommand;
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
}
