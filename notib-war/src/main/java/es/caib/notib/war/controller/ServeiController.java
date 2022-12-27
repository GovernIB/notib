package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.dto.procediment.ProcSerFormDto;
import es.caib.notib.core.api.dto.procediment.ProcedimentEstat;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.*;
import es.caib.notib.war.command.ProcSerCommand;
import es.caib.notib.war.command.ProcSerFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.EnumHelper;
import es.caib.notib.war.helper.MissatgesHelper;
import es.caib.notib.war.helper.RequestSessionHelper;
import es.caib.notib.war.helper.RolHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
		model.addAttribute("procedimentEstats", EnumHelper.getOptionsForEnum(ProcedimentEstat.class, "es.caib.notib.core.api.dto.procediment.ProcedimentEstat."));
		model.addAttribute("organsGestors", findOrgansGestorsAccessibles(entitat, organGestorActual));
		model.addAttribute("isCodiDir3Entitat", Boolean.parseBoolean(aplicacioService.propertyGetByEntitat("es.caib.notib.plugin.codi.dir3.entitat", "false")));
		
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
	public String getByOrganGestor(HttpServletRequest request,
								   @PathVariable String organCodi,
								   Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		this.currentFiltre = SERVEIS_FILTRE_MODAL;
		ProcSerFiltreCommand procSerFiltreCommand = getFiltreCommand(request);
		procSerFiltreCommand.setOrganGestor(organCodi);
		model.addAttribute("isModal", true);
		model.addAttribute("organCodi", organCodi);
		model.addAttribute("procSerFiltreCommand", procSerFiltreCommand);
		model.addAttribute("organsGestors", findOrgansGestorsAccessibles(entitat, organGestorActual));
		model.addAttribute("isCodiDir3Entitat", Boolean.parseBoolean(aplicacioService.propertyGetByEntitat("es.caib.notib.plugin.codi.dir3.entitat", "false")));
		return "serveiListModal";
	}

	private List<CodiValorEstatDto> findOrgansGestorsAccessibles (EntitatDto entitatActual, OrganGestorDto organGestorActual) {

		List<CodiValorEstatDto> organsGestors = new ArrayList<CodiValorEstatDto>();
		if (organGestorActual == null) {
			organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
		} else {
			List<OrganGestorDto> organsDto = organGestorService.findDescencentsByCodi(entitatActual.getId(),
					organGestorActual.getCodi());
			for (OrganGestorDto organ: organsDto) {
				organsGestors.add(CodiValorEstatDto.builder().codi(organ.getCodi()).valor(organ.getCodi() + " - " + organ.getNom()).estat(organ.getEstat()).build());
			}
		}
		return organsGestors;
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( 
			HttpServletRequest request ) {
		
		boolean isUsuari = RolHelper.isUsuariActualUsuari(request);
		boolean isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
		boolean isAdministrador = RolHelper.isUsuariActualAdministrador(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		
		ProcSerFiltreCommand procSerFiltreCommand = getFiltreCommand(request);
		PaginaDto<ProcSerFormDto> serveis = new PaginaDto<>();
		
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);

			serveis = serveiService.findAmbFiltrePaginat(
					entitat.getId(),
					isUsuari,
					isUsuariEntitat,
					isAdministrador,
					organGestorActual,
					procSerFiltreCommand.asDto(),
					DatatablesHelper.getPaginacioDtoFromRequest(request));
		}catch(SecurityException e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"notificacio.controller.entitat.cap.assignada"));
		}
		return DatatablesHelper.getDatatableResponse(
				request, 
				serveis,
				"id");
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			Model model) {
		return formGet(request, null, model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(	
			HttpServletRequest request,
			ProcSerFiltreCommand command,
			Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				this.currentFiltre,
				command);
		
		return "serveiListPage";
	}
	
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid ProcSerCommand procSerCommand,
			BindingResult bindingResult,
			Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelServei(
					request,
					procSerCommand.getId(),
					model);
			model.addAttribute("errors", bindingResult.getAllErrors());
			List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findNoCaducatsByEntitat(entitat);
			model.addAttribute("operadorPostalList", operadorPostalList);
			List<IdentificadorTextDto> cieList = cieService.findNoCaducatsByEntitat(entitat);
			model.addAttribute("cieList", cieList);
			return "serveiAdminForm";
		}
		
		if (procSerCommand.getId() != null) {
			try {
				serveiService.update(
						procSerCommand.getEntitatId(),
						ProcSerCommand.asDto(procSerCommand),
						isAdministrador(request),
						RolHelper.isUsuariActualAdministradorEntitat(request));
				
			} catch(NotFoundException | ValidationException ev) {
				logger.debug("Error al actualitzar el procediment", ev);
			}
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../servei",
					"servei.controller.modificat.ok");
		} else {
			serveiService.create(
					procSerCommand.getEntitatId(),
					ProcSerCommand.asDto(procSerCommand));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../servei",
					"servei.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/{serveiId}", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request, 
			@PathVariable Long serveiId,
			Model model) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		ProcSerCommand procSerCommand;
		ProcSerDto servei = emplenarModelServei(
				request,
				serveiId,
				model);
		if (servei != null) {
			procSerCommand = ProcSerCommand.asCommand(servei);
//			procSerCommand.setEntitatId(servei.getEntitat().getId());
//			if (servei.getPagadorcie() != null)
//				procSerCommand.setPagadorCieId(servei.getPagadorcie().getId());
//			if (servei.getPagadorpostal() != null)
//				procSerCommand.setPagadorPostalId(servei.getPagadorpostal().getId());
		} else {
			procSerCommand = new ProcSerCommand();
		}
		model.addAttribute(procSerCommand);
		List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findNoCaducatsByEntitat(entitat);
		model.addAttribute("operadorPostalList", operadorPostalList);
		List<IdentificadorTextDto> cieList = cieService.findNoCaducatsByEntitat(entitat);
		model.addAttribute("cieList", cieList);
		return "serveiAdminForm";
	}
	
	@RequestMapping(value = "/{serveiId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long serveiId) {
		
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			
			if (serveiService.serveiEnUs(serveiId)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../servei",
						"servei.controller.esborrat.enUs");
			} else {
				serveiService.delete(
						entitat.getId(),
						serveiId,
						RolHelper.isUsuariActualAdministradorEntitat(request));
				
				return getAjaxControllerReturnValueSuccess(
						request,
						"redirect:../../servei",
						"servei.controller.esborrat.ok");
			}
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../servei",
					"servei.controller.esborrat.ko",
					e);
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

		String urlResponse = "redirect:../../servei";
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			boolean trobat = serveiService.actualitzarServei(codiSia, entitat);
			return trobat ?  getAjaxControllerReturnValueSuccess(request, urlResponse, "servei.controller.update.ok")
					:  getAjaxControllerReturnValueError(request, urlResponse, "servei.controller.update.no.trobat");
		} catch (Exception ex) {
			return getAjaxControllerReturnValueError(request, urlResponse, "servei.controller.update.ko");
		}
	}
	
	@RequestMapping(value = "/update/auto", method = RequestMethod.GET)
	public String actualitzacioAutomaticaGet(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		model.addAttribute("isUpdatingProcediments", serveiService.isUpdatingServeis(entitat));
		return "serveisActualitzacioForm";
	}
	
	@RequestMapping(value = "/update/auto", method = RequestMethod.POST)
	public String actualitzacioAutomaticaPost(
			HttpServletRequest request,
			Model model) {
				
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		try {
			serveiService.actualitzaServeis(entitat);
		} catch (Exception e) {
			logger.error("Error inesperat al actualitzar els serveis", e);
			model.addAttribute("errors", e.getMessage());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			MissatgesHelper.error(request, "Error: \n" + sw.toString());
			return "serveisActualitzacioForm";
		}
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"/serveisActualitzacioForm",
				"procediment.controller.update.auto.ok");
	}
	
	@RequestMapping(value = "/update/auto/progres", method = RequestMethod.GET)
	@ResponseBody
	public ProgresActualitzacioDto getProgresActualitzacio(HttpServletRequest request) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		return serveiService.getProgresActualitzacio(entitat.getDir3Codi());
	}
			
	
	private ProcSerFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ProcSerFiltreCommand procSerFiltreCommand = (
				ProcSerFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
						request,
						this.currentFiltre);
		if (procSerFiltreCommand == null) {
			procSerFiltreCommand = new ProcSerFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					this.currentFiltre,
					procSerFiltreCommand);
		}
		return procSerFiltreCommand;
	}
	
	private ProcSerDto emplenarModelServei(
			HttpServletRequest request,
			Long serveiId,
			Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		ProcSerDto servei = null;
		
		if (serveiId != null) {
			servei = serveiService.findById(
					entitat.getId(),
					isAdministrador(request),
					serveiId);
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
		if (RolHelper.isUsuariActualAdministrador(request))
			model.addAttribute("entitats", entitatService.findAll());
		else
			model.addAttribute("entitat", entitat);
		
		return servei;
	}
	
	@RequestMapping(value = "/organisme/{organGestorCodi}", method = RequestMethod.GET)
	private String emplenarOrganismeServei(
			HttpServletRequest request,
			@PathVariable String organGestorCodi,
			Model model) {
		model.addAttribute("organisme", organGestorCodi);
		return "redirect:/servei/new";
	}
	

	private boolean isAdministrador(
			HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ServeiController.class);
}
