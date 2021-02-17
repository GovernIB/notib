package es.caib.notib.war.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.api.service.PagadorPostalService;
import es.caib.notib.war.command.OrganGestorCommand;
import es.caib.notib.war.command.OrganGestorFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.MissatgesHelper;
import es.caib.notib.war.helper.RequestSessionHelper;

/**
 * Controlador per el mantinemnt de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/organgestor")
public class OrganGestorController extends BaseUserController{
	
	private final static String ORGANS_FILTRE = "organs_filtre";
	
	@Autowired
	OrganGestorService organGestorService;
	@Autowired
	EntitatService entitatService;
	@Autowired
	PagadorPostalService pagadorPostalService;
	@Autowired
	PagadorCieService pagadorCieService;
	@Autowired
	GrupService grupsService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
		model.addAttribute("organGestorFiltreCommand", getFiltreCommand(request));
		model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
		model.addAttribute("setOficina", !entitat.isOficinaEntitat());
		model.addAttribute("oficinesEntitat",
				organGestorService.getOficinesSIR(
						entitat.getId(), 
						entitat.getDir3Codi(), 
						true));
		return "organGestorList";
	}
	
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( 
			HttpServletRequest request ) {
		
		OrganGestorFiltreCommand organGestorFiltreCommand = getFiltreCommand(request);
		PaginaDto<OrganGestorDto> organs = new PaginaDto<OrganGestorDto>();
		
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);

			OrganGestorDto organGestorActual = getOrganGestorActual(request);
			String organActualCodiDir3=null;
			if (organGestorActual!=null) organActualCodiDir3 = organGestorActual.getCodi();
			
			organs = organGestorService.findAmbFiltrePaginat(
					entitat.getId(),
					organActualCodiDir3,
					OrganGestorFiltreCommand.asDto(organGestorFiltreCommand),
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
				organs,
				"codi");
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(	
			HttpServletRequest request,
			OrganGestorFiltreCommand command,
			Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(
				request, 
				ORGANS_FILTRE, 
				command);
		
		return "organGestorList";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			Model model) {
		OrganGestorCommand organGestorCommand = new OrganGestorCommand();
		EntitatDto entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
		model.addAttribute(organGestorCommand);
		model.addAttribute("entitat", getEntitatActualComprovantPermisos(request));
		model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
		model.addAttribute("setOficina", !entitat.isOficinaEntitat());
		model.addAttribute("isModificacio", false);
		return "organGestorForm";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid OrganGestorCommand organGestorCommand,
			BindingResult bindingResult,
			Model model) {		
		
		if (bindingResult.hasErrors()) {
			EntitatDto entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
			model.addAttribute("entitat", entitat);
			model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
			model.addAttribute("setOficina", !entitat.isOficinaEntitat());
			if (organGestorCommand.getId() != null)
				model.addAttribute("isModificacio", true);
			
			return "organGestorForm";
		}
		if (organGestorCommand.getId() != null) {
			organGestorService.updateOficina(OrganGestorCommand.asDto(organGestorCommand));
		} else {
			organGestorService.create(OrganGestorCommand.asDto(organGestorCommand));
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:organgestor",
				"organgestor.controller.creat.ok");
	}
	
	@RequestMapping(value = "/{organGestorId}", method = RequestMethod.GET)
	public String update(
			HttpServletRequest request,
			Model model,
			@PathVariable Long organGestorId) {		
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		try {
			boolean isOficinaEntitat = entitat.isOficinaEntitat();
			
			if (!isOficinaEntitat) {
				OrganGestorDto organGestorDto = organGestorService.findById(
						entitat.getId(),
						organGestorId);
				OrganGestorCommand organGestorCommand = OrganGestorCommand.asCommand(organGestorDto);
	
				model.addAttribute(organGestorCommand);
				model.addAttribute("entitat", entitat);
				model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
				model.addAttribute("setOficina", !isOficinaEntitat);
				model.addAttribute("isModificacio", true);
				return "organGestorForm";
			}
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../organgestor",
					"organgestor.controller.update.nom.error");
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../organgestor",
					"organgestor.controller.update.nom.error");
		}
	}
	
	@RequestMapping(value = "/{organGestorCodi}/update", method = RequestMethod.GET)
	public String updateNom(
			HttpServletRequest request,
			@PathVariable String organGestorCodi) {		
		
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		
		try {
			organGestorService.updateNom(
					entitat.getId(),
					organGestorCodi);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../organgestor",
					"organgestor.controller.update.nom.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../organgestor",
					"organgestor.controller.update.nom.error");
		}
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String updateNoms(
			HttpServletRequest request) {		
		
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		
		try {
			OrganGestorDto organGestorActual = getOrganGestorActual(request);
			String codiDir3OrganActual=null;
			if (organGestorActual!=null) codiDir3OrganActual = organGestorActual.getCodi();
			
			organGestorService.updateNoms(
					entitat.getId(), codiDir3OrganActual);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../organgestor",
					"organgestor.controller.update.nom.tots.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../organgestor",
					"organgestor.controller.update.nom.tots.error");
		}
	}
	
	@RequestMapping(value = "/{organGestorCodi}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable String organGestorCodi) {		
		
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			OrganGestorDto organ = organGestorService.findByCodi(entitat.getId(), organGestorCodi);
			if (organ!=null) {
				if (organGestorService.organGestorEnUs(organ.getId())) {
					return getAjaxControllerReturnValueError(
							request,
							"redirect:../../procediment",
							"organgestor.controller.esborrat.us");
				} else {
					organGestorService.delete(
							entitat.getId(),
							organ.getId());
					return getAjaxControllerReturnValueSuccess(
							request,
							"redirect:../../procediment",
							"organgestor.controller.esborrat.ok");
				}
			}else{
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../procediment",
						"organgestor.controller.esborrat.ko");
			}
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../procediment",
					"organgestor.controller.esborrat.ko",
					e);
		}
	}
	
	@RequestMapping(value = "/llibre/{organGestorDir3Codi}", method = RequestMethod.GET)
	@ResponseBody
	private LlibreDto getLlibreOrgan(
		HttpServletRequest request,
		Model model,
		@PathVariable String organGestorDir3Codi) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		return organGestorService.getLlibreOrganisme(
				entitat.getId(),
				organGestorDir3Codi);
	}
	
	@RequestMapping(value = "/oficines/{organGestorDir3Codi}", method = RequestMethod.GET)
	@ResponseBody
	private List<OficinaDto> getOficinesOrgan(
		HttpServletRequest request,
		Model model,
		@PathVariable String organGestorDir3Codi) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		return organGestorService.getOficinesSIR(
				entitat.getId(),
				organGestorDir3Codi,
				false);
	}
	
	private OrganGestorFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		OrganGestorFiltreCommand organGestorFiltreCommand = (
				OrganGestorFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
						request,
						ORGANS_FILTRE);
		if (organGestorFiltreCommand == null) {
			organGestorFiltreCommand = new OrganGestorFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					ORGANS_FILTRE,
					organGestorFiltreCommand);
		}
		return organGestorFiltreCommand;
	}
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(OrganGestorController.class);
}
