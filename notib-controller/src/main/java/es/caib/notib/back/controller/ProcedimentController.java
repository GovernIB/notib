package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.cie.Operadors;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
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
@RequestMapping("/procediment")
public class ProcedimentController extends BaseUserController{
	
	private final static String PROCEDIMENTS_FILTRE = "procediments_filtre";
	private final static String PROCEDIMENTS_FILTRE_MODAL = "procediments_filtre_modal";

	private String currentFiltre = PROCEDIMENTS_FILTRE;

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

		var entitat = getEntitatActualComprovantPermisos(request);
		var organGestorActual = getOrganGestorActual(request);
		this.currentFiltre = PROCEDIMENTS_FILTRE;
		var procSerFiltreCommand = getFiltreCommand(request);
		var codi = request.getParameter("codi");
		if (!Strings.isNullOrEmpty(codi)) {
			procSerFiltreCommand.setCodi(codi);
		}
		model.addAttribute("procSerFiltreCommand", procSerFiltreCommand);
		model.addAttribute("procedimentEstats", EnumHelper.getOptionsForEnum(ProcedimentEstat.class, "es.caib.notib.core.api.dto.procediment.ProcedimentEstat."));
		model.addAttribute("organsGestors", findOrgansGestorsAccessibles(entitat, organGestorActual));
		var property = aplicacioService.propertyGetByEntitat("es.caib.notib.plugin.codi.dir3.entitat", "false");
		model.addAttribute("isCodiDir3Entitat", Boolean.parseBoolean(property));
		model.addAttribute("isModal", false);
		return "procedimentListPage";
	}

	@RequestMapping(value = "/filtre/codi/{procCodi}", method = RequestMethod.GET)
	public String getFiltratByOrganGestor(HttpServletRequest request,  @PathVariable String procCodi, Model model) {

		this.currentFiltre = PROCEDIMENTS_FILTRE;
		ProcSerFiltreCommand procSerFiltreCommand = getFiltreCommand(request);
		procSerFiltreCommand.setCodi(procCodi);
		RequestSessionHelper.actualitzarObjecteSessio(request, this.currentFiltre, procSerFiltreCommand);
		return "redirect:/procediment";
	}

	@RequestMapping(value = "/organ/{organCodi}", method = RequestMethod.GET)
	public String getByOrganGestor(HttpServletRequest request,
								   @PathVariable String organCodi,
								   Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		this.currentFiltre = PROCEDIMENTS_FILTRE_MODAL;
		ProcSerFiltreCommand procSerFiltreCommand = getFiltreCommand(request);
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
		List<OrganGestorDto> organsDto = organGestorService.findDescencentsByCodi(entitatActual.getId(), organGestorActual.getCodi());
		for (OrganGestorDto organ: organsDto) {
			organsGestors.add(new CodiValorEstatDto(organ.getCodi(), organ.getCodi() + " - " + organ.getNom(), organ.getEstat()));
		}
		return organsGestors;
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request ) {
		
		boolean isUsuari = RolHelper.isUsuariActualUsuari(request);
		boolean isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
		boolean isAdministrador = RolHelper.isUsuariActualAdministrador(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		ProcSerFiltreCommand procSerFiltreCommand = getFiltreCommand(request);
		PaginaDto<ProcSerFormDto> procediments = new PaginaDto<>();
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			procediments = procedimentService.findAmbFiltrePaginat(entitat.getId(), isUsuari, isUsuariEntitat, isAdministrador, organGestorActual,
							procSerFiltreCommand.asDto(), DatatablesHelper.getPaginacioDtoFromRequest(request));
		} catch (SecurityException e) {
			MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
		}
		return DatatablesHelper.getDatatableResponse(request, procediments, "id");
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, Model model) {
		return formGet(request, null, model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(HttpServletRequest request, ProcSerFiltreCommand command, Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(request, this.currentFiltre, command);
		return "procedimentListPage";
	}
	
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid ProcSerCommand procSerCommand, BindingResult bindingResult, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelProcediment(request, procSerCommand.getId(), model);
			model.addAttribute("errors", bindingResult.getAllErrors());
			var operadorPostalList = operadorPostalService.findNoCaducatsByEntitat(entitat);
			model.addAttribute("operadorPostalList", operadorPostalList);
			var cieList = cieService.findNoCaducatsByEntitat(entitat);
			model.addAttribute("cieList", cieList);
			return "procedimentAdminForm";
		}
		var url = "redirect:../procediment";
		var msg = procSerCommand.getId() != null ? "procediment.controller.modificat.ok" : "procediment.controller.creat.ok";
		if (procSerCommand.getId() == null) {
			procedimentService.create(procSerCommand.getEntitatId(), ProcSerCommand.asDto(procSerCommand));
			return getModalControllerReturnValueSuccess(request, url, msg);
		}
		try {
			var usuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
			procedimentService.update(procSerCommand.getEntitatId(), ProcSerCommand.asDto(procSerCommand), isAdministrador(request), usuariEntitat);
		} catch(NotFoundException | ValidationException ev) {
			log.debug("Error al actualitzar el procediment", ev);
		}
		return getModalControllerReturnValueSuccess(request, url, msg);
	}

	@ResponseBody
	@RequestMapping(value = "/operadors/{organ}", method = RequestMethod.GET)
	public Operadors getOperadors(HttpServletRequest request, @PathVariable String organ) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		boolean isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
		List<IdentificadorTextDto> postal = operadorPostalService.findNoCaducatsByEntitatAndOrgan(entitat, organ, isAdminOrgan);
		List<IdentificadorTextDto> cie = cieService.findNoCaducatsByEntitatAndOrgan(entitat, organ, isAdminOrgan);
		return Operadors.builder().operadorsPostal(postal).operadorsCie(cie).build();
	}
	
	@RequestMapping(value = "/{procedimentId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long procedimentId) {

		String url = "redirect:../../procediment";
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			if (procedimentService.procedimentEnUs(procedimentId)) {
				return getAjaxControllerReturnValueError(request, url, "procediment.controller.esborrat.enUs");
			}
			procedimentService.delete(entitat.getId(), procedimentId, RolHelper.isUsuariActualAdministradorEntitat(request));
			return getAjaxControllerReturnValueSuccess(request, url, "procediment.controller.esborrat.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(request, url, "procediment.controller.esborrat.ko", e);
		}
	}

	@RequestMapping(value = "/{procedimentId}", method = RequestMethod.GET)
	public String formGet(HttpServletRequest request, @PathVariable Long procedimentId, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var procSerCommand = new ProcSerCommand();
		var procediment = emplenarModelProcediment(request, procedimentId, model);
		if (procediment != null) {
			procSerCommand = ProcSerCommand.asCommand(procediment);
			procSerCommand.setEntitatId(procediment.getEntitat().getId());
		}
		model.addAttribute(procSerCommand);
		var operadorPostalList = operadorPostalService.findNoCaducatsByEntitat(entitat);
		model.addAttribute("operadorPostalList", operadorPostalList);
		var cieList = cieService.findNoCaducatsByEntitat(entitat);
		model.addAttribute("cieList", cieList);
		return "procedimentAdminForm";
	}

	@RequestMapping(value = "/{procedimentId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long procedimentId) {
		procedimentService.updateActiu(procedimentId, true);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "procediment.controller.activada.ok");
	}
	@RequestMapping(value = "/{procedimentId}/disable", method = RequestMethod.GET)
	public String disable(HttpServletRequest request, @PathVariable Long procedimentId) {
		procedimentService.updateActiu(procedimentId, false);
		return getAjaxControllerReturnValueSuccess(request,"redirect:../../entitat", "procediment.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{codiSia}/update", method = RequestMethod.GET)
	public String actualitzarProcediment(HttpServletRequest request, @PathVariable String codiSia) {

		String urlResponse = "redirect:../../procediment";
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			boolean trobat = procedimentService.actualitzarProcediment(codiSia, entitat);
			String msg = trobat ? "procediment.controller.update.ok" : "procediment.controller.update.no.trobat";
			return trobat ?  getAjaxControllerReturnValueSuccess(request, urlResponse, msg) : getAjaxControllerReturnValueError(request, urlResponse, msg);
		} catch (Exception ex) {
			return getAjaxControllerReturnValueError(request, urlResponse, "procediment.controller.update.ko");
		}
	}


	@RequestMapping(value = "/update/auto", method = RequestMethod.GET)
	public String actualitzacioAutomaticaGet(HttpServletRequest request, Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		model.addAttribute("isUpdatingProcediments", procedimentService.isUpdatingProcediments(entitat));
		return "procedimentsActualitzacioForm";
	}
	
	@RequestMapping(value = "/update/auto", method = RequestMethod.POST)
	public String actualitzacioAutomaticaPost(HttpServletRequest request, Model model) {
				
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
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
		return getAjaxControllerReturnValueSuccess(request, "/procedimentsActualitzacioForm","procediment.controller.update.auto.ok");
	}
	
	@RequestMapping(value = "/update/auto/progres", method = RequestMethod.GET)
	@ResponseBody
	public ProgresActualitzacioDto getProgresActualitzacio(HttpServletRequest request) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		return procedimentService.getProgresActualitzacio(entitat.getDir3Codi());
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
	
	private ProcSerDto emplenarModelProcediment(HttpServletRequest request, Long procedimentId, Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		ProcSerDto procediment = null;
		if (procedimentId != null) {
			procediment = procedimentService.findById(entitat.getId(), isAdministrador(request), procedimentId);
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
		if (RolHelper.isUsuariActualAdministrador(request)) {
			model.addAttribute("entitats", entitatService.findAll());
			return procediment;
		}
		model.addAttribute("entitat", entitat);
		return procediment;
	}
	
	@RequestMapping(value = "/organisme/{organGestorCodi}", method = RequestMethod.GET)
	private String emplenarOrganismeProcediment(HttpServletRequest request, @PathVariable String organGestorCodi, Model model) {

		model.addAttribute("organisme", organGestorCodi);
		return "redirect:/procediment/new";
	}
	
	@RequestMapping(value = "/tipusAssumpte/{entitatId}", method = RequestMethod.GET)
	@ResponseBody
	private List<TipusAssumpteDto> getTipusAssumpte(HttpServletRequest request, Model model, @PathVariable Long entitatId) {

		EntitatDto entitat = entitatService.findById(entitatId);
		model.addAttribute("tipusAssumpte", procedimentService.findTipusAssumpte(entitat));
		return procedimentService.findTipusAssumpte(entitat);
	}
	
	@RequestMapping(value = "/codiAssumpte/{entitatId}/{codiTipusAssumpte}", method = RequestMethod.GET)
	@ResponseBody
	private List<CodiAssumpteDto> getCodiAssumpte(HttpServletRequest request, Model model, @PathVariable Long entitatId, @PathVariable String codiTipusAssumpte) {

		EntitatDto entitat = entitatService.findById(entitatId);
		return procedimentService.findCodisAssumpte(entitat, codiTipusAssumpte);
	}
	
	@RequestMapping(value = "/organismes/{entitatId}", method = RequestMethod.GET)
	@ResponseBody
	private List<OrganismeDto> getOrganismes(HttpServletRequest request, Model model, @PathVariable Long entitatId) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		List<OrganismeDto> organismes;
		return organGestorActual != null ? organGestorService.findOrganismes(entitat, organGestorActual) : organGestorService.findOrganismes(entitat);
	}
	
	@RequestMapping(value = "/cache/refrescar", method = RequestMethod.GET)
	private String refrescar(HttpServletRequest request, Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		procedimentService.refrescarCache(entitat);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../procediment","procediment.controller.esborrat.cache.ok");
	}
	
	private boolean isAdministrador(HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
	}
}
