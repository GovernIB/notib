package es.caib.notib.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.api.service.PagadorPostalService;
import es.caib.notib.core.api.service.ProcedimentService;
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
	ProcedimentService procedimentService;
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
		model.addAttribute("organGestorFiltreCommand", getFiltreCommand(request));
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

			organs = procedimentService.findOrgansGestorsAmbFiltrePaginat(
					entitat.getId(),
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
	
	@RequestMapping(value = "/{organGestorCodi}/update", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable String organGestorCodi) {		
		
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		
		try {
			procedimentService.updateOrganGestorNom(
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
	public String delete(
			HttpServletRequest request) {		
		
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		
		try {
			procedimentService.updateOrgansGestorsNom(
					entitat.getId());
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
