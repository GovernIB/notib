package es.caib.notib.back.controller;

import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFormDto;
import es.caib.notib.logic.intf.dto.procediment.ProcedimentEstat;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.*;
import es.caib.notib.back.command.ProcSerCommand;
import es.caib.notib.back.command.ProcSerFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import lombok.extern.slf4j.Slf4j;
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per el mantinemnt de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Slf4j
@Controller
@RequestMapping("/servei")
public class ServeiController extends BaseUserController{
	
	private final static String SERVEIS_FILTRE = "serveis_filtre";
	private final static String SERVEIS_FILTRE_MODAL = "serveis_filtre_modal";

	private String currentFiltre = SERVEIS_FILTRE;

	@Autowired
	private ServeiService serveiService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private OperadorPostalService operadorPostalService;
	@Autowired
	private PagadorCieService pagadorCieService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private PagadorCieService cieService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		this.currentFiltre = SERVEIS_FILTRE;
		ProcSerFiltreCommand procSerFiltreCommand = getFiltreCommand(request);
		model.addAttribute("procSerFiltreCommand", procSerFiltreCommand);
		model.addAttribute("procedimentEstats", EnumHelper.getOptionsForEnum(ProcedimentEstat.class, "es.caib.notib.logic.intf.dto.procediment.ProcedimentEstat."));
		model.addAttribute("organsGestors", findOrgansGestorsAccessibles(entitat, organGestorActual));
		String property = aplicacioService.propertyGetByEntitat("es.caib.notib.plugin.codi.dir3.entitat", "false");
		model.addAttribute("isCodiDir3Entitat", Boolean.parseBoolean(property));
		return "serveiListPage";
	}

	@RequestMapping(value = "/filtre/codi/{serveiCodi}", method = RequestMethod.GET)
	public String getFiltratByOrganGestor(HttpServletRequest request,  @PathVariable String serveiCodi, Model model) {

		this.currentFiltre = SERVEIS_FILTRE;
		ProcSerFiltreCommand procSerFiltreCommand = getFiltreCommand(request);
		procSerFiltreCommand.setCodi(serveiCodi);
		RequestSessionHelper.actualitzarObjecteSessio(request, this.currentFiltre, procSerFiltreCommand);
		return "redirect:/servei";
	}

	@RequestMapping(value = "/organ/{organCodi}", method = RequestMethod.GET)
	public String getByOrganGestor(HttpServletRequest request, @PathVariable String organCodi, Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		this.currentFiltre = SERVEIS_FILTRE_MODAL;
		ProcSerFiltreCommand procSerFiltreCommand = getFiltreCommand(request);
		procSerFiltreCommand.setOrganGestor(organCodi);
		model.addAttribute("isModal", true);
		model.addAttribute("organCodi", organCodi);
		model.addAttribute("procSerFiltreCommand", procSerFiltreCommand);
		model.addAttribute("organsGestors", findOrgansGestorsAccessibles(entitat, organGestorActual));
		String property = aplicacioService.propertyGetByEntitat("es.caib.notib.plugin.codi.dir3.entitat", "false");
		model.addAttribute("isCodiDir3Entitat", Boolean.parseBoolean(property));
		return "serveiListModal";
	}

	private List<CodiValorEstatDto> findOrgansGestorsAccessibles (EntitatDto entitatActual, OrganGestorDto organGestorActual) {

		if (organGestorActual == null) {
			return organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
		}
		List<CodiValorEstatDto> organsGestors = new ArrayList<>();
		List<OrganGestorDto> organsDto = organGestorService.findDescencentsByCodi(entitatActual.getId(), organGestorActual.getCodi());
		for (OrganGestorDto organ: organsDto) {
			organsGestors.add(CodiValorEstatDto.builder().codi(organ.getCodi()).valor(organ.getCodi() + " - " + organ.getNom()).estat(organ.getEstat()).build());
		}
		return organsGestors;
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request ) {


		PaginaDto<ProcSerFormDto> serveis = new PaginaDto<>();
		try {
			boolean isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
			boolean isAdministrador = RolHelper.isUsuariActualAdministrador(request);
			OrganGestorDto organGestorActual = getOrganGestorActual(request);
			ProcSerFiltreCommand procSerFiltreCommand = getFiltreCommand(request);
			boolean isUsuari = RolHelper.isUsuariActualUsuari(request);
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			PaginacioParamsDto params = DatatablesHelper.getPaginacioDtoFromRequest(request);
			serveis = serveiService.findAmbFiltrePaginat(entitat.getId(), isUsuari, isUsuariEntitat, isAdministrador, organGestorActual, procSerFiltreCommand.asDto(), params);
		} catch (SecurityException e) {
			MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
		}
		return DatatablesHelper.getDatatableResponse(request, serveis, "id");
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, Model model) {

		return formGet(request, null, model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(HttpServletRequest request, ProcSerFiltreCommand command, Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(request, this.currentFiltre, command);
		return "serveiListPage";
	}
	
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid ProcSerCommand procSerCommand, BindingResult bindingResult, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelServei(request, procSerCommand.getId(), model);
			model.addAttribute("errors", bindingResult.getAllErrors());
			var operadorPostalList = operadorPostalService.findNoCaducatsByEntitat(entitat);
			model.addAttribute("operadorPostalList", operadorPostalList);
			var cieList = cieService.findNoCaducatsByEntitat(entitat);
			model.addAttribute("cieList", cieList);
			return "serveiAdminForm";
		}

		var url = "redirect:../servei";
		var msg = procSerCommand.getId() == null ? "servei.controller.creat.ok" : "servei.controller.modificat.ok";
		if (procSerCommand.getId() == null) {
			serveiService.create(procSerCommand.getEntitatId(), ProcSerCommand.asDto(procSerCommand));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		try {
			var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
			serveiService.update(procSerCommand.getEntitatId(), ProcSerCommand.asDto(procSerCommand), isAdministrador(request), isAdminEntitat);
		} catch (NotFoundException | ValidationException ev) {
			log.debug("Error al actualitzar el procediment", ev);
		}
		return getModalControllerReturnValueSuccess(request, url, msg);
	}
	
	@RequestMapping(value = "/{serveiId}", method = RequestMethod.GET)
	public String formGet(HttpServletRequest request, @PathVariable Long serveiId, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		ProcSerCommand procSerCommand;
		var servei = emplenarModelServei(request, serveiId, model);
		procSerCommand = servei != null ? ProcSerCommand.asCommand(servei) : new ProcSerCommand();
		model.addAttribute(procSerCommand);
		var operadorPostalList = operadorPostalService.findNoCaducatsByEntitat(entitat);
		model.addAttribute("operadorPostalList", operadorPostalList);
		var cieList = cieService.findNoCaducatsByEntitat(entitat);
		model.addAttribute("cieList", cieList);
		return "serveiAdminForm";
	}
	
	@RequestMapping(value = "/{serveiId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long serveiId) {

		String url = "redirect:../../servei";
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			if (serveiService.serveiEnUs(serveiId)) {
				return getAjaxControllerReturnValueError(request, url, "servei.controller.esborrat.enUs");
			}
			serveiService.delete(entitat.getId(), serveiId, RolHelper.isUsuariActualAdministradorEntitat(request));
			return getAjaxControllerReturnValueSuccess(request, url, "servei.controller.esborrat.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(request, url, "servei.controller.esborrat.ko", e);
		}
	}

	@RequestMapping(value = "/{serveiId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long serveiId) {
		serveiService.updateActiu(serveiId, true);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "servei.controller.activada.ok");
	}
	@RequestMapping(value = "/{serveiId}/disable", method = RequestMethod.GET)
	public String disable(HttpServletRequest request, @PathVariable Long serveiId) {
		serveiService.updateActiu(serveiId, false);
		return getAjaxControllerReturnValueSuccess(request,"redirect:../../entitat", "servei.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{codiSia}/update", method = RequestMethod.GET)
	public String actualitzarProcediment(HttpServletRequest request, @PathVariable String codiSia) {

		String url = "redirect:../../servei";
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			boolean trobat = serveiService.actualitzarServei(codiSia, entitat);
			String msg = trobat ? "servei.controller.update.ok" : "servei.controller.update.no.trobat";
			return trobat ?  getAjaxControllerReturnValueSuccess(request, url, msg) : getAjaxControllerReturnValueError(request, url, msg);
		} catch (Exception ex) {
			return getAjaxControllerReturnValueError(request, url, "servei.controller.update.ko");
		}
	}
	
	@RequestMapping(value = "/update/auto", method = RequestMethod.GET)
	public String actualitzacioAutomaticaGet(HttpServletRequest request, Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		model.addAttribute("isUpdatingProcediments", serveiService.isUpdatingServeis(entitat));
		return "serveisActualitzacioForm";
	}
	
	@RequestMapping(value = "/update/auto", method = RequestMethod.POST)
	public String actualitzacioAutomaticaPost(HttpServletRequest request, Model model) {
				
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		try {
			serveiService.actualitzaServeis(entitat);
			return getAjaxControllerReturnValueSuccess(request, "/serveisActualitzacioForm", "procediment.controller.update.auto.ok");
		} catch (Exception e) {
			log.error("Error inesperat al actualitzar els serveis", e);
			model.addAttribute("errors", e.getMessage());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			MissatgesHelper.error(request, "Error: \n" + sw.toString());
			return "serveisActualitzacioForm";
		}
	}
	
	@RequestMapping(value = "/update/auto/progres", method = RequestMethod.GET)
	@ResponseBody
	public ProgresActualitzacioDto getProgresActualitzacio(HttpServletRequest request) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		return serveiService.getProgresActualitzacio(entitat.getDir3Codi());
	}
			
	
	private ProcSerFiltreCommand getFiltreCommand(HttpServletRequest request) {

		ProcSerFiltreCommand procSerFiltreCommand = (ProcSerFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, this.currentFiltre);
		if (procSerFiltreCommand != null) {
			return procSerFiltreCommand;
		}
		procSerFiltreCommand = new ProcSerFiltreCommand();
		RequestSessionHelper.actualitzarObjecteSessio(request, this.currentFiltre, procSerFiltreCommand);
		return procSerFiltreCommand;
	}
	
	private ProcSerDto emplenarModelServei(HttpServletRequest request, Long serveiId, Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		ProcSerDto servei = null;
		if (serveiId != null) {
			servei = serveiService.findById(entitat.getId(), isAdministrador(request), serveiId);
			if (servei != null && servei.getOrganGestor() != null) {
				servei.setOrganGestorNom(servei.getOrganGestor() + " - " + servei.getOrganGestorNom());
			}
			model.addAttribute(servei);
		}
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		if (organGestorActual != null) {
			model.addAttribute("pagadorsPostal", operadorPostalService.findByEntitatAndOrganGestor(entitat, organGestorActual));
			model.addAttribute("pagadorsCie", pagadorCieService.findByEntitatAndOrganGestor(entitat, organGestorActual));
		} else {
			model.addAttribute("pagadorsPostal", operadorPostalService.findByEntitat(entitat.getId()));
			model.addAttribute("pagadorsCie", pagadorCieService.findByEntitat(entitat.getId()));
		}
		if (servei != null) {
			model.addAttribute("entitatId", servei.getEntitat().getId());
		}
		if (RolHelper.isUsuariActualAdministrador(request)) {
			model.addAttribute("entitats", entitatService.findAll());
			return servei;
		}
		model.addAttribute("entitat", entitat);
		return servei;
	}
	
	@RequestMapping(value = "/organisme/{organGestorCodi}", method = RequestMethod.GET)
	private String emplenarOrganismeServei(HttpServletRequest request, @PathVariable String organGestorCodi, Model model) {

		model.addAttribute("organisme", organGestorCodi);
		return "redirect:/servei/new";
	}
	

	private boolean isAdministrador(HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
	}
}
