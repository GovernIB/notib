package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.service.*;
import es.caib.notib.war.command.OrganGestorCommand;
import es.caib.notib.war.command.OrganGestorFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.EnumHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.MissatgesHelper;
import es.caib.notib.war.helper.RequestSessionHelper;
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
import java.util.List;

/**
 * Controlador per el mantinemnt organs gestors format llista.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/organgestor")
public class OrganGestorController extends BaseUserController{
	
	private final static String ORGANS_FILTRE = "organs_filtre";
	
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private OperadorPostalService operadorPostalService;
	@Autowired
	private PagadorCieService cieService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
		model.addAttribute("organGestorFiltreCommand", getFiltreCommand(request));
		model.addAttribute("organGestorEstats",
				EnumHelper.getOptionsForEnum(OrganGestorEstatEnum.class,
	                        "es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum."));
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
					organGestorFiltreCommand.asDto(),
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
		List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findAllIdentificadorText();
		model.addAttribute("operadorPostalList", operadorPostalList);
		List<IdentificadorTextDto> cieList = cieService.findAllIdentificadorText();
		model.addAttribute("cieList", cieList);
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
			List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findAllIdentificadorText();
			model.addAttribute("operadorPostalList", operadorPostalList);
			List<IdentificadorTextDto> cieList = cieService.findAllIdentificadorText();
			model.addAttribute("cieList", cieList);
			if (organGestorCommand.getId() != null)
				model.addAttribute("isModificacio", true);

			return "organGestorForm";
		}
		if (organGestorCommand.getId() != null) {
			organGestorService.update(OrganGestorCommand.asDto(organGestorCommand));
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
				List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findAllIdentificadorText();
				model.addAttribute("operadorPostalList", operadorPostalList);
				List<IdentificadorTextDto> cieList = cieService.findAllIdentificadorText();
				model.addAttribute("cieList", cieList);
				return "organGestorForm";
			}
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../organgestor",
					"organgestor.controller.update.nom.error");
		} catch (Exception e) {
			logger.error(String.format("Excepció intentant actualitzar l'òrgan gestor (Id=%d):", organGestorId), e);
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
			organGestorService.updateOne(
					entitat.getId(),
					organGestorCodi);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../organgestor",
					"organgestor.controller.update.nom.ok");
		} catch (Exception e) {
			logger.error(String.format("Excepció intentant esborrar l'òrgan gestor %s:", organGestorCodi), e);
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../organgestor",
					"organgestor.controller.update.nom.error");
		}
	}

	@RequestMapping(value = "/update/auto", method = RequestMethod.GET)
	public String actualitzacioAutomaticaGet(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		model.addAttribute("isUpdatingOrgans", organGestorService.isUpdatingOrgans(entitat));
		return "organGestorActualitzacioForm";
	}

	@RequestMapping(value = "/update/auto/progres", method = RequestMethod.GET)
	@ResponseBody
	public ProgresActualitzacioDto getProgresActualitzacio(HttpServletRequest request) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		return organGestorService.getProgresActualitzacio(entitat.getDir3Codi());
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateNoms(
			HttpServletRequest request, Model model) {
		
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		
		try {
			OrganGestorDto organGestorActual = getOrganGestorActual(request);
			String codiDir3OrganActual=null;
			if (organGestorActual!=null) codiDir3OrganActual = organGestorActual.getCodi();
			
			organGestorService.updateAll(
					entitat.getId(), codiDir3OrganActual);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../organgestor",
					"organgestor.controller.update.nom.tots.ok");
		} catch (Exception e) {
			logger.error("Excepció intentant actualitzar tots els òrgans gestors", e);
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
			logger.error(String.format("Excepció intentant esborrar l'òrgan gestor %s:", organGestorCodi), e);
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../procediment",
					"organgestor.controller.esborrat.ko");
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
	
	public OrganGestorFiltreCommand getFiltreCommand(
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
