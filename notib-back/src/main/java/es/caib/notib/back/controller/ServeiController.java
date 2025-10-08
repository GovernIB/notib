package es.caib.notib.back.controller;

import es.caib.notib.back.command.ProcSerCommand;
import es.caib.notib.back.command.ProcSerFiltreCommand;
import es.caib.notib.back.helper.*;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.logic.intf.dto.CodiValorEstatDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFormDto;
import es.caib.notib.logic.intf.dto.procediment.ProcedimentEstat;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
public class ServeiController extends BaseUserController {

	@Autowired
	private ServeiService serveiService;
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

	private static final String SERVEIS_FILTRE = "serveis_filtre";
	private static final String SERVEIS_FILTRE_MODAL = "serveis_filtre_modal";
	private static final String REDIRECT_SERVEI = "redirect:../../servei";
	private String currentFiltre = SERVEIS_FILTRE;

	@GetMapping
	public String get(HttpServletRequest request, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		currentFiltre = SERVEIS_FILTRE;
		var procSerFiltreCommand = getFiltreCommand(request);
		model.addAttribute("procSerFiltreCommand", procSerFiltreCommand);
		model.addAttribute("procedimentEstats", EnumHelper.getOptionsForEnum(ProcedimentEstat.class, "es.caib.notib.logic.intf.dto.procediment.ProcedimentEstat."));
		model.addAttribute("organsGestors", findOrgansGestorsAccessibles(entitat, organGestorActual));
		model.addAttribute("isCodiDir3Entitat", Boolean.parseBoolean(aplicacioService.propertyGetByEntitat("es.caib.notib.plugin.codi.dir3.entitat", "false")));

		return "serveiListPage";
	}

	@GetMapping(value = "/filtre/codi/{serveiCodi}")
	public String getFiltratByOrganGestor(HttpServletRequest request, @PathVariable String serveiCodi, Model model) {

		currentFiltre = SERVEIS_FILTRE;
		var procSerFiltreCommand = getFiltreCommand(request);
		procSerFiltreCommand.setCodi(serveiCodi);
		RequestSessionHelper.actualitzarObjecteSessio(request, this.currentFiltre, procSerFiltreCommand);
		return "redirect:/servei";
	}

	@GetMapping(value = "/organ/{organCodi}")
	public String getByOrganGestor(HttpServletRequest request, @PathVariable String organCodi, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		currentFiltre = SERVEIS_FILTRE_MODAL;
		var procSerFiltreCommand = getFiltreCommand(request);
		procSerFiltreCommand.setOrganGestor(organCodi);
		model.addAttribute("isModal", true);
		model.addAttribute("organCodi", organCodi);
		model.addAttribute("procSerFiltreCommand", procSerFiltreCommand);
		model.addAttribute("organsGestors", findOrgansGestorsAccessibles(entitat, organGestorActual));
		model.addAttribute("isCodiDir3Entitat", Boolean.parseBoolean(aplicacioService.propertyGetByEntitat("es.caib.notib.plugin.codi.dir3.entitat", "false")));
		return "serveiListModal";
	}

	private List<CodiValorEstatDto> findOrgansGestorsAccessibles(EntitatDto entitatActual, OrganGestorDto organGestorActual) {

		if (organGestorActual == null) {
			return organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
		}
		var organsDto = organGestorService.findDescencentsByCodi(entitatActual.getId(), organGestorActual.getCodi());
		List<CodiValorEstatDto> organsGestors = new ArrayList<>();
		for (var organ : organsDto) {
			organsGestors.add(CodiValorEstatDto.builder().id(organ.getId()).codi(organ.getCodi()).valor(organ.getCodi() + " - " + organ.getNom()).estat(organ.getEstat()).build());
		}
		return organsGestors;
	}

	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {

		var isUsuari = RolHelper.isUsuariActualUsuari(sessionScopedContext.getRolActual());
		var isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual())
				|| RolHelper.isUsuariActualAdministradorLectura(sessionScopedContext.getRolActual());
		var isAdministrador = RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
		var organGestorActual = getOrganGestorActual(request);
		var procSerFiltreCommand = getFiltreCommand(request);
		var serveis = new PaginaDto<ProcSerFormDto>();
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			serveis = serveiService.findAmbFiltrePaginat(entitat.getId(), isUsuari, isUsuariEntitat, isAdministrador, organGestorActual,
					procSerFiltreCommand.asDto(), DatatablesHelper.getPaginacioDtoFromRequest(request));
		} catch (SecurityException e) {
			MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
		}
		return DatatablesHelper.getDatatableResponse(request, serveis, "id");
	}

	@GetMapping(value = "/new")
	public String newGet(HttpServletRequest request, Model model) {
		return formGet(request, null, model);
	}

	@PostMapping
	public String post(HttpServletRequest request, ProcSerFiltreCommand command, Model model) {

		RequestSessionHelper.actualitzarObjecteSessio(request, this.currentFiltre, command);
		return "serveiListPage";
	}

	@PostMapping(value = "/newOrModify")
	public String save(HttpServletRequest request, @Valid ProcSerCommand procSerCommand, BindingResult bindingResult, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelServei(request, procSerCommand.getId(), model);
			model.addAttribute("errors", bindingResult.getAllErrors());
			var operadorPostalList = operadorPostalService.findByEntitat(entitat);
			model.addAttribute("operadorPostalList", operadorPostalList);
			var cieList = pagadorCieService.findByEntitat(entitat);
			model.addAttribute("cieList", cieList);
			return "serveiAdminForm";
		}

		if (procSerCommand.getId() != null) {
			try {
				serveiService.update(procSerCommand.getEntitatId(), ProcSerCommand.asDto(procSerCommand), isAdministrador(), RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual()));
			} catch (NotFoundException | ValidationException ev) {
				log.debug("Error al actualitzar el procediment", ev);
			}
			return getModalControllerReturnValueSuccess(request, "redirect:../servei", "servei.controller.modificat.ok");
		}
		serveiService.create(procSerCommand.getEntitatId(), ProcSerCommand.asDto(procSerCommand));
		return getModalControllerReturnValueSuccess(request, "redirect:../servei", "servei.controller.creat.ok");
	}

	@GetMapping(value = "/{serveiId}")
	public String formGet(HttpServletRequest request, @PathVariable Long serveiId, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var servei = emplenarModelServei(request, serveiId, model);
		var procSerCommand = servei != null ? ProcSerCommand.asCommand(servei) : new ProcSerCommand();
		model.addAttribute(procSerCommand);
		var operadorPostalList = operadorPostalService.findByEntitat(entitat);
		model.addAttribute("operadorPostalList", operadorPostalList);
		var cieList = pagadorCieService.findByEntitat(entitat);
		model.addAttribute("cieList", cieList);
		return "serveiAdminForm";
	}

	@GetMapping(value = "/{serveiId}/delete")
	public String delete(HttpServletRequest request, @PathVariable Long serveiId) {

		try {
			var entitat = getEntitatActualComprovantPermisos(request);
			if (serveiService.serveiEnUs(serveiId)) {
				return getAjaxControllerReturnValueError(request, REDIRECT_SERVEI, "servei.controller.esborrat.enUs");
			}
			serveiService.delete(entitat.getId(), serveiId, RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual()));
			return getAjaxControllerReturnValueSuccess(request, REDIRECT_SERVEI, "servei.controller.esborrat.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(request, REDIRECT_SERVEI, "servei.controller.esborrat.ko", e);
		}
	}

	@GetMapping(value = "/{serveiId}/enable")
	public String enable(HttpServletRequest request, @PathVariable Long serveiId) {

		serveiService.updateActiu(serveiId, true);
		return getAjaxControllerReturnValueSuccess(request, REDIRECT_SERVEI, "servei.controller.activada.ok");
	}

	@GetMapping(value = "/{serveiId}/disable")
	public String disable(HttpServletRequest request, @PathVariable Long serveiId) {

		serveiService.updateActiu(serveiId, false);
		return getAjaxControllerReturnValueSuccess(request, REDIRECT_SERVEI, "servei.controller.desactivada.ok");
	}

	@GetMapping(value = "/{serveiId}/sync_manual")
	public String manual(HttpServletRequest request, @PathVariable Long serveiId) {

		serveiService.updateManual(serveiId, true);
		return getAjaxControllerReturnValueSuccess(request, REDIRECT_SERVEI, "procediment.controller.manual.ok");
	}

	@GetMapping(value = "/{serveiId}/sync_auto")
	public String auto(HttpServletRequest request, @PathVariable Long serveiId) {

		serveiService.updateManual(serveiId, false);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "procediment.controller.auto.ok");
	}

	@GetMapping(value = "/{codiSia}/update")
	public String actualitzarProcediment(HttpServletRequest request, @PathVariable String codiSia) {

		try {
			var entitat = getEntitatActualComprovantPermisos(request);
			var trobat = serveiService.actualitzarServei(codiSia, entitat);
			return trobat ? getAjaxControllerReturnValueSuccess(request, REDIRECT_SERVEI, "servei.controller.update.ok")
					: getAjaxControllerReturnValueError(request, REDIRECT_SERVEI, "servei.controller.update.no.trobat");
		} catch (Exception ex) {
			return getAjaxControllerReturnValueError(request, REDIRECT_SERVEI, "servei.controller.update.ko");
		}
	}

	@GetMapping(value = "/update/auto")
	public String actualitzacioAutomaticaGet(HttpServletRequest request, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		model.addAttribute("isUpdatingProcediments", serveiService.isUpdatingServeis(entitat));
		return "serveisActualitzacioForm";
	}

	@PostMapping(value = "/update/auto")
	public String actualitzacioAutomaticaPost(HttpServletRequest request, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		try {
			serveiService.actualitzaServeis(entitat);
		} catch (Exception e) {
			log.error("Error inesperat al actualitzar els serveis", e);
			model.addAttribute("errors", e.getMessage());
			var sw = new StringWriter();
			var pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			MissatgesHelper.error(request, "Error: \n" + sw.toString());
			return "serveisActualitzacioForm";
		}
		return getAjaxControllerReturnValueSuccess(request, "/serveisActualitzacioForm", "procediment.controller.update.auto.ok");
	}

	@GetMapping(value = "/update/auto/progres")
	@ResponseBody
	public ProgresActualitzacioDto getProgresActualitzacio(HttpServletRequest request) {

		var entitat = getEntitatActualComprovantPermisos(request);
		return serveiService.getProgresActualitzacio(entitat.getDir3Codi());
	}

	private ProcSerFiltreCommand getFiltreCommand(HttpServletRequest request) {

		var procSerFiltreCommand = (ProcSerFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request, currentFiltre);
		if (procSerFiltreCommand != null) {
			return procSerFiltreCommand;
		}
		procSerFiltreCommand = new ProcSerFiltreCommand();
		procSerFiltreCommand.setEstat(ProcedimentEstat.ACTIU);
		RequestSessionHelper.actualitzarObjecteSessio(request, currentFiltre, procSerFiltreCommand);
		return procSerFiltreCommand;
	}
	
	private ProcSerDto emplenarModelServei(HttpServletRequest request, Long serveiId, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		ProcSerDto servei = null;
		if (serveiId != null) {
			servei = serveiService.findById(entitat.getId(), isAdministrador(), serveiId);
			if (servei != null && servei.getOrganGestor() != null) {
				servei.setOrganGestorNom(servei.getOrganGestor() + " - " + servei.getOrganGestorNom());
			}
			model.addAttribute(servei);
		}
		
		var organGestorActual = getOrganGestorActual(request);
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
		if (RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual())) {
			model.addAttribute("entitats", entitatService.findAll());
		} else {
			model.addAttribute("entitat", entitat);
		}
		return servei;
	}
	
	@GetMapping(value = "/organisme/{organGestorCodi}")
	public String emplenarOrganismeServei(HttpServletRequest request, @PathVariable String organGestorCodi, Model model) {

		model.addAttribute("organisme", organGestorCodi);
		return "redirect:/servei/new";
	}

	private boolean isAdministrador() {
		return RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
	}
}
