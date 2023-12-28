package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.command.ProcSerCommand;
import es.caib.notib.back.command.ProcSerFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.CodiAssumpteDto;
import es.caib.notib.logic.intf.dto.CodiValorEstatDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.TipusAssumpteDto;
import es.caib.notib.logic.intf.dto.cie.Operadors;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerFormDto;
import es.caib.notib.logic.intf.dto.procediment.ProcedimentEstat;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.ValidationException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/procediment")
public class ProcedimentController extends BaseUserController {

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

	private static final String PROCEDIMENTS_FILTRE = "procediments_filtre";
	private static final String PROCEDIMENTS_FILTRE_MODAL = "procediments_filtre_modal";
	private static final String REDIRECT_PROCEDIMENT = "redirect:../../procediment";
	private String currentFiltre = PROCEDIMENTS_FILTRE;


	@GetMapping
	public String get(HttpServletRequest request, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		this.currentFiltre = PROCEDIMENTS_FILTRE;
		var procSerFiltreCommand = getFiltreCommand(request);
		var codi = request.getParameter("codi");
		if (!Strings.isNullOrEmpty(codi)) {
			procSerFiltreCommand.setCodi(codi);
		}
		model.addAttribute("procedimentEstats", EnumHelper.getOptionsForEnum(ProcedimentEstat.class, "es.caib.notib.logic.intf.dto.procediment.ProcedimentEstat."));
		model.addAttribute("procSerFiltreCommand", procSerFiltreCommand);
		model.addAttribute("organsGestors", findOrgansGestorsAccessibles(entitat, organGestorActual));
		model.addAttribute("isCodiDir3Entitat", Boolean.parseBoolean(aplicacioService.propertyGetByEntitat("es.caib.notib.plugin.codi.dir3.entitat", "false")));
		model.addAttribute("isModal", false);
		return "procedimentListPage";
	}

	@GetMapping(value = "/filtre/codi/{procCodi}")
	public String getFiltratByOrganGestor(HttpServletRequest request,  @PathVariable String procCodi, Model model) {

		currentFiltre = PROCEDIMENTS_FILTRE;
		var procSerFiltreCommand = getFiltreCommand(request);
		procSerFiltreCommand.setCodi(procCodi);
		RequestSessionHelper.actualitzarObjecteSessio(request, currentFiltre, procSerFiltreCommand);
		return "redirect:/procediment";
	}

	@GetMapping(value = "/organ/{organCodi}")
	public String getByOrganGestor(HttpServletRequest request, @PathVariable String organCodi, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		currentFiltre = PROCEDIMENTS_FILTRE_MODAL;
		var procSerFiltreCommand = getFiltreCommand(request);
		procSerFiltreCommand.setOrganGestor(organCodi);
		model.addAttribute("isModal", true);
		model.addAttribute("organCodi", organCodi);
		model.addAttribute("procSerFiltreCommand", procSerFiltreCommand);
		model.addAttribute("organsGestors", findOrgansGestorsAccessibles(entitat, organGestorActual));
		model.addAttribute("isCodiDir3Entitat", Boolean.parseBoolean(aplicacioService.propertyGetByEntitat("es.caib.notib.plugin.codi.dir3.entitat", "false")));
		return "procedimentListModal";
	}

	private List<CodiValorEstatDto> findOrgansGestorsAccessibles (EntitatDto entitatActual, OrganGestorDto organGestorActual) {

		List<CodiValorEstatDto> organsGestors = new ArrayList<>();
		if (organGestorActual == null) {
			return organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
		}
		var organsDto = organGestorService.findDescencentsByCodi(entitatActual.getId(), organGestorActual.getCodi());
		for (var organ: organsDto) {
			organsGestors.add(CodiValorEstatDto.builder().codi(organ.getCodi()).valor(organ.getCodi() + " - " + organ.getNom()).estat(organ.getEstat()).build());
		}
		return organsGestors;
	}

	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request ) {
		
		var isUsuari = RolHelper.isUsuariActualUsuari(sessionScopedContext.getRolActual());
		var isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
		var isAdministrador = RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
		var organGestorActual = getOrganGestorActual(request);
		var procSerFiltreCommand = getFiltreCommand(request);
		var procediments = new PaginaDto<ProcSerFormDto>();
		try {
			var entitat = getEntitatActualComprovantPermisos(request);
 			procediments = procedimentService.findAmbFiltrePaginat(entitat.getId(), isUsuari, isUsuariEntitat, isAdministrador, organGestorActual, procSerFiltreCommand.asDto(), DatatablesHelper.getPaginacioDtoFromRequest(request));
		} catch (SecurityException e) {
			MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
		} catch (Exception ex) {
			log.error("Error en el llistat de procediments", ex);
		}
		return DatatablesHelper.getDatatableResponse(request, procediments, "id");
	}
	
	@GetMapping(value = "/new")
	public String newGet(HttpServletRequest request, Model model) {
		return formGet(request, null, model);
	}
	
	@PostMapping
	public String post(HttpServletRequest request, ProcSerFiltreCommand command, Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(request, this.currentFiltre, command);
		return "procedimentListPage";
	}
	
	@PostMapping(value = "/newOrModify")
	public String save(HttpServletRequest request, @Valid ProcSerCommand procSerCommand, BindingResult bindingResult, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var redirect = "redirect:../procediment";
		if (bindingResult.hasErrors()) {
			emplenarModelProcediment(request, procSerCommand.getId(), model);
			model.addAttribute("errors", bindingResult.getAllErrors());
			var operadorPostalList = operadorPostalService.findNoCaducatsByEntitat(entitat);
			model.addAttribute("operadorPostalList", operadorPostalList);
			var cieList = pagadorCieService.findNoCaducatsByEntitat(entitat);
			model.addAttribute("cieList", cieList);
			return "procedimentAdminForm";
		}
		if (procSerCommand.getId() == null) {
			procedimentService.create(procSerCommand.getEntitatId(), ProcSerCommand.asDto(procSerCommand));
			return getModalControllerReturnValueSuccess(request, redirect, "procediment.controller.creat.ok");
		}
		try {
			var rol = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
			procedimentService.update(procSerCommand.getEntitatId(), ProcSerCommand.asDto(procSerCommand), isAdministrador(), rol);
		} catch(NotFoundException | ValidationException ev) {
			log.debug("Error al actualitzar el procediment", ev);
		}
		return getModalControllerReturnValueSuccess(request, redirect, "procediment.controller.modificat.ok");
	}

	@ResponseBody
	@GetMapping(value = "/operadors/{organ}")
	public Operadors getOperadors(HttpServletRequest request, @PathVariable String organ) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());
		var postal = operadorPostalService.findNoCaducatsByEntitatAndOrgan(entitat, organ, isAdminOrgan);
		var cie = pagadorCieService.findNoCaducatsByEntitatAndOrgan(entitat, organ, isAdminOrgan);
		return Operadors.builder().operadorsPostal(postal).operadorsCie(cie).build();
	}

	@GetMapping(value = "/{procedimentId}/delete")
	public String delete(HttpServletRequest request, @PathVariable Long procedimentId) {

		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			if (procedimentService.procedimentEnUs(procedimentId)) {
				return getAjaxControllerReturnValueError(request, REDIRECT_PROCEDIMENT, "procediment.controller.esborrat.enUs");
			}
			procedimentService.delete(entitat.getId(), procedimentId, RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual()));
			return getAjaxControllerReturnValueSuccess(request, REDIRECT_PROCEDIMENT, "procediment.controller.esborrat.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(request, REDIRECT_PROCEDIMENT, "procediment.controller.esborrat.ko", e);
		}
	}

	@GetMapping(value = "/{procedimentId}")
	public String formGet(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		ProcSerCommand procSerCommand;
		var procediment = emplenarModelProcediment(request, procedimentId, model);
		if (procediment != null) {
			procSerCommand = ProcSerCommand.asCommand(procediment);
			procSerCommand.setEntitatId(procediment.getEntitat().getId());
		} else {
			procSerCommand = new ProcSerCommand();
		}
		model.addAttribute(procSerCommand);
		var operadorPostalList = operadorPostalService.findNoCaducatsByEntitat(entitat);
		model.addAttribute("operadorPostalList", operadorPostalList);
		var cieList = pagadorCieService.findNoCaducatsByEntitat(entitat);
		model.addAttribute("cieList", cieList);
		return "procedimentAdminForm";
	}

	@GetMapping(value = "/{procedimentId}/enable")
	public String enable(HttpServletRequest request, @PathVariable Long procedimentId) {

		procedimentService.updateActiu(procedimentId, true);
		return getAjaxControllerReturnValueSuccess(request, REDIRECT_PROCEDIMENT, "procediment.controller.activada.ok");
	}

	@GetMapping(value = "/{procedimentId}/disable")
	public String disable(HttpServletRequest request, @PathVariable Long procedimentId) {

		procedimentService.updateActiu(procedimentId, false);
		return getAjaxControllerReturnValueSuccess(request,REDIRECT_PROCEDIMENT, "procediment.controller.desactivada.ok");
	}

	@GetMapping(value = "/{procedimentId}/sync_manual")
	public String manual(HttpServletRequest request, @PathVariable Long procedimentId) {

		procedimentService.updateManual(procedimentId, true);
		return getAjaxControllerReturnValueSuccess(request, REDIRECT_PROCEDIMENT, "procediment.controller.manual.ok");
	}

	@GetMapping(value = "/{procedimentId}/sync_auto")
	public String auto(HttpServletRequest request, @PathVariable Long procedimentId) {

		procedimentService.updateManual(procedimentId, false);
		return getAjaxControllerReturnValueSuccess(request,REDIRECT_PROCEDIMENT, "procediment.controller.auto.ok");
	}

	@GetMapping(value = "/{codiSia}/update")
	public String actualitzarProcediment(HttpServletRequest request, @PathVariable String codiSia) {

		var urlResponse = REDIRECT_PROCEDIMENT;
		try {
			var entitat = getEntitatActualComprovantPermisos(request);
			var trobat = procedimentService.actualitzarProcediment(codiSia, entitat);
			return trobat ?  getAjaxControllerReturnValueSuccess(request, urlResponse, "procediment.controller.update.ok")
					:  getAjaxControllerReturnValueError(request, urlResponse, "procediment.controller.update.no.trobat");
		} catch (Exception ex) {
			return getAjaxControllerReturnValueError(request, urlResponse, "procediment.controller.update.ko");
		}
	}


	@GetMapping(value = "/update/auto")
	public String actualitzacioAutomaticaGet(HttpServletRequest request, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		model.addAttribute("isUpdatingProcediments", procedimentService.isUpdatingProcediments(entitat));
		return "procedimentsActualitzacioForm";
	}
	
	@PostMapping(value = "/update/auto")
	public String actualitzacioAutomaticaPost(HttpServletRequest request, Model model) {
				
		var entitat = getEntitatActualComprovantPermisos(request);
		try {
			procedimentService.actualitzaProcediments(entitat);
		} catch (Exception e) {
			log.error("Error inesperat al actualitzar els procediments", e);
			model.addAttribute("errors", e.getMessage());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			MissatgesHelper.error(request, "Error: \n" + sw.toString());
			return "procedimentsActualitzacioForm";
		}
		
		return getAjaxControllerReturnValueSuccess(request, "/procedimentsActualitzacioForm", "procediment.controller.update.auto.ok");
	}
	
	@GetMapping(value = "/update/auto/progres")
	@ResponseBody
	public ProgresActualitzacioDto getProgresActualitzacio(HttpServletRequest request) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		return procedimentService.getProgresActualitzacio(entitat.getDir3Codi());
	}
			
	
	private ProcSerFiltreCommand getFiltreCommand(HttpServletRequest request) {

		var procSerFiltreCommand = (ProcSerFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, this.currentFiltre);
		if (procSerFiltreCommand != null) {
			return procSerFiltreCommand;
		}
		procSerFiltreCommand = new ProcSerFiltreCommand();
		procSerFiltreCommand.setEstat(ProcedimentEstat.ACTIU);
		RequestSessionHelper.actualitzarObjecteSessio(request, this.currentFiltre, procSerFiltreCommand);
		return procSerFiltreCommand;
	}
	
	private ProcSerDto emplenarModelProcediment(HttpServletRequest request, Long procedimentId, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		ProcSerDto procediment = null;
		if (procedimentId != null) {
			procediment = procedimentService.findById(entitat.getId(), isAdministrador(), procedimentId);
			if (procediment != null && procediment.getOrganGestor() != null) {
				procediment.setOrganGestorNom(procediment.getOrganGestor() + " - " + procediment.getOrganGestorNom());
			}
			model.addAttribute(procediment);
		}
		
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		if (organGestorActual != null) {
			model.addAttribute("pagadorsPostal", operadorPostalService.findByEntitatAndOrganGestor(entitat, organGestorActual));
			model.addAttribute("pagadorsCie", pagadorCieService.findByEntitatAndOrganGestor(entitat, organGestorActual));
		} else {
			model.addAttribute("pagadorsPostal", operadorPostalService.findByEntitat(entitat.getId()));
			model.addAttribute("pagadorsCie", pagadorCieService.findByEntitat(entitat.getId()));
		}
		
		if (procediment != null) {
			model.addAttribute("entitatId", procediment.getEntitat().getId());
		}
		if (RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual())) {
			model.addAttribute("entitats", entitatService.findAll());
		} else {
			model.addAttribute("entitat", entitat);
		}
		return procediment;
	}
	
	@GetMapping(value = "/organisme/{organGestorCodi}")
	public String emplenarOrganismeProcediment(HttpServletRequest request, @PathVariable String organGestorCodi, Model model) {

		model.addAttribute("organisme", organGestorCodi);
		return "redirect:/procediment/new";
	}
	
	@GetMapping(value = "/tipusAssumpte/{entitatId}")
	@ResponseBody
	public List<TipusAssumpteDto> getTipusAssumpte(HttpServletRequest request, Model model, @PathVariable Long entitatId) {

		var entitat = entitatService.findById(entitatId);
		model.addAttribute("tipusAssumpte", procedimentService.findTipusAssumpte(entitat));
		return procedimentService.findTipusAssumpte(entitat);
	}
	
	@GetMapping(value = "/codiAssumpte/{entitatId}/{codiTipusAssumpte}")
	@ResponseBody
	public List<CodiAssumpteDto> getCodiAssumpte(HttpServletRequest request, Model model, @PathVariable Long entitatId, @PathVariable String codiTipusAssumpte) {

		var entitat = entitatService.findById(entitatId);
		return procedimentService.findCodisAssumpte(entitat, codiTipusAssumpte);
	}
	
	@GetMapping(value = "/organismes/{entitatId}")
	@ResponseBody
	public List<OrganismeDto> getOrganismes(HttpServletRequest request, Model model, @PathVariable Long entitatId) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		return organGestorActual != null ? organGestorService.findOrganismes(entitat, organGestorActual) : organGestorService.findOrganismes(entitat);
	}
	
	@GetMapping(value = "/cache/refrescar")
	public String refrescar(HttpServletRequest request, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		procedimentService.refrescarCache(entitat);
		return getAjaxControllerReturnValueSuccess(request, REDIRECT_PROCEDIMENT, "procediment.controller.esborrat.cache.ok");
	}
	
	private boolean isAdministrador() {
		return RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
	}
	
}
