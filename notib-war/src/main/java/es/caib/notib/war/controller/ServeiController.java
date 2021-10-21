package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganismeDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.dto.procediment.ProcSerFormDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.*;
import es.caib.notib.war.command.ProcSerCommand;
import es.caib.notib.war.command.ProcSerFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
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
		model.addAttribute("organsGestors", findOrgansGestorsAccessibles(entitat, organGestorActual));
		model.addAttribute("isCodiDir3Entitat", Boolean.parseBoolean(aplicacioService.propertyGet("es.caib.notib.plugin.codi.dir3.entitat", "false")));
		
		return "procedimentListPage";
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
		model.addAttribute("organCodi", organCodi);
		model.addAttribute("procSerFiltreCommand", procSerFiltreCommand);
		model.addAttribute("organsGestors", findOrgansGestorsAccessibles(entitat, organGestorActual));
		model.addAttribute("isCodiDir3Entitat", Boolean.parseBoolean(aplicacioService.propertyGet("es.caib.notib.plugin.codi.dir3.entitat", "false")));
		return "procedimentListModal";
	}

	private List<CodiValorEstatDto> findOrgansGestorsAccessibles (EntitatDto entitatActual, OrganGestorDto organGestorActual) {

		List<CodiValorEstatDto> organsGestors = new ArrayList<CodiValorEstatDto>();
		if (organGestorActual == null) {
			organsGestors = organGestorService.findOrgansGestorsCodiByEntitat(entitatActual.getId());
		} else {
			List<OrganGestorDto> organsDto = organGestorService.findDescencentsByCodi(entitatActual.getId(),
					organGestorActual.getCodi());
			for (OrganGestorDto organ: organsDto) {
				organsGestors.add(new CodiValorEstatDto(organ.getCodi(), organ.getCodi() + " - " + organ.getNom(),
						organ.getEstat()));
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
		PaginaDto<ProcSerFormDto> procediments = new PaginaDto<>();
		
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);

			procediments = serveiService.findAmbFiltrePaginat(
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
				procediments,
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
		
		return "procedimentListPage";
	}
	
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid ProcSerCommand procSerCommand,
			BindingResult bindingResult,
			Model model) {		
		
		if (bindingResult.hasErrors()) {
			emplenarModelServei(
					request,
					procSerCommand.getId(),
					model);
			model.addAttribute("errors", bindingResult.getAllErrors());
			List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findAllIdentificadorText();
			model.addAttribute("operadorPostalList", operadorPostalList);
			List<IdentificadorTextDto> cieList = cieService.findAllIdentificadorText();
			model.addAttribute("cieList", cieList);
			return "procedimentAdminForm";
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
					"redirect:../procediment",
					"procediment.controller.modificat.ok");
		} else {
			serveiService.create(
					procSerCommand.getEntitatId(),
					ProcSerCommand.asDto(procSerCommand));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../procediment",
					"procediment.controller.creat.ok");
		}
	}
	
	@RequestMapping(value = "/{procedimentId}", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request, 
			@PathVariable Long procedimentId, 
			Model model) {
		ProcSerCommand procSerCommand;
		ProcSerDto procediment = emplenarModelServei(
				request, 
				procedimentId,
				model);
		if (procediment != null) {
			procSerCommand = ProcSerCommand.asCommand(procediment);
			procSerCommand.setEntitatId(procediment.getEntitat().getId());
//			if (procediment.getPagadorcie() != null)
//				procSerCommand.setPagadorCieId(procediment.getPagadorcie().getId());
//			if (procediment.getPagadorpostal() != null)
//				procSerCommand.setPagadorPostalId(procediment.getPagadorpostal().getId());
		} else {
			procSerCommand = new ProcSerCommand();
		}
		model.addAttribute(procSerCommand);
		List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findAllIdentificadorText();
		model.addAttribute("operadorPostalList", operadorPostalList);
		List<IdentificadorTextDto> cieList = cieService.findAllIdentificadorText();
		model.addAttribute("cieList", cieList);
		return "procedimentAdminForm";
	}
	
	@RequestMapping(value = "/{procedimentId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long procedimentId) {		
		
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			
			if (serveiService.serveiEnUs(procedimentId)) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../procediment",
						"procediment.controller.esborrat.enUs");
			} else {
				serveiService.delete(
						entitat.getId(),
						procedimentId,
						RolHelper.isUsuariActualAdministradorEntitat(request));
				
				return getAjaxControllerReturnValueSuccess(
						request,
						"redirect:../../procediment",
						"procediment.controller.esborrat.ok");
			}
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../procediment",
					"procediment.controller.esborrat.ko",
					e);
		}
	}
	
	@RequestMapping(value = "/update/auto", method = RequestMethod.GET)
	public String actualitzacioAutomaticaGet(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		model.addAttribute("isUpdatingProcediments", serveiService.isUpdatingServeis(entitat));
		return "procedimentsActualitzacioForm";
	}
	
	@RequestMapping(value = "/update/auto", method = RequestMethod.POST)
	public String actualitzacioAutomaticaPost(
			HttpServletRequest request,
			Model model) {
				
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		try {
			serveiService.actualitzaServeis(entitat);
		} catch (Exception e) {
			logger.error("Error inesperat al actualitzar els procediments", e);
			model.addAttribute("errors", e.getMessage());
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			MissatgesHelper.error(request, "Error: \n" + sw.toString());
			return "procedimentsActualitzacioForm";
		}
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"/procedimentsActualitzacioForm",
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
			Long procedimentId,
			Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		ProcSerDto procediment = null;
		
		if (procedimentId != null) {
			procediment = serveiService.findById(
					entitat.getId(),
					isAdministrador(request),
					procedimentId);
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
		if (RolHelper.isUsuariActualAdministrador(request))
			model.addAttribute("entitats", entitatService.findAll());
		else
			model.addAttribute("entitat", entitat);
		
		return procediment;
	}
	
	@RequestMapping(value = "/organisme/{organGestorCodi}", method = RequestMethod.GET)
	private String emplenarOrganismeProcediment(
			HttpServletRequest request,
			@PathVariable String organGestorCodi,
			Model model) {
		model.addAttribute("organisme", organGestorCodi);
		return "redirect:/procediment/new";
	}
	
	@RequestMapping(value = "/tipusAssumpte/{entitatId}", method = RequestMethod.GET)
	@ResponseBody
	private List<TipusAssumpteDto> getTipusAssumpte(
		HttpServletRequest request,
		Model model,
		@PathVariable Long entitatId) {
		EntitatDto entitat = entitatService.findById(entitatId);
		
		model.addAttribute("tipusAssumpte", procedimentService.findTipusAssumpte(entitat));
		
		return procedimentService.findTipusAssumpte(entitat);
	}
	
	@RequestMapping(value = "/codiAssumpte/{entitatId}/{codiTipusAssumpte}", method = RequestMethod.GET)
	@ResponseBody
	private List<CodiAssumpteDto> getCodiAssumpte(
		HttpServletRequest request,
		Model model,
		@PathVariable Long entitatId,
		@PathVariable String codiTipusAssumpte) {
		EntitatDto entitat = entitatService.findById(entitatId);
		
		return procedimentService.findCodisAssumpte(
				entitat,
				codiTipusAssumpte);
	}
	
	@RequestMapping(value = "/organismes/{entitatId}", method = RequestMethod.GET)
	@ResponseBody
	private List<OrganismeDto> getOrganismes(
		HttpServletRequest request,
		Model model,
		@PathVariable Long entitatId) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		OrganGestorDto organGestorActual = getOrganGestorActual(request);
		List<OrganismeDto> organismes;
		if (organGestorActual != null) {
			organismes = organGestorService.findOrganismes(entitat, organGestorActual);
		} else {
			organismes = organGestorService.findOrganismes(entitat);
		}
		return organismes;
	}
	
	@RequestMapping(value = "/cache/refrescar", method = RequestMethod.GET)
	private String refrescar(
		HttpServletRequest request,
		Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		procedimentService.refrescarCache(entitat);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../procediment",
				"procediment.controller.esborrat.cache.ok");
	}
	
	private boolean isAdministrador(
			HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ServeiController.class);
}
