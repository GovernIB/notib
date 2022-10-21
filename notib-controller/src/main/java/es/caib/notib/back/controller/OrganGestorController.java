package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.PrediccioSincronitzacio;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ServeiService;
import es.caib.notib.back.command.OrganGestorCommand;
import es.caib.notib.back.command.OrganGestorFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
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
import java.util.List;

/**
 * Controlador per el mantinemnt organs gestors format llista.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Slf4j
@Controller
@RequestMapping("/organgestor")
public class OrganGestorController extends BaseUserController{
	
	private final static String ORGANS_FILTRE = "organs_filtre";
	
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private OperadorPostalService operadorPostalService;
	@Autowired
	private PagadorCieService cieService;
	@Autowired
	private OrganGestorService organService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {

		EntitatDto entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
		OrganGestorFiltreCommand filtres = getFiltreCommand(request);
		model.addAttribute("organGestorFiltreCommand", filtres);
		model.addAttribute("organsEntitat", organService.getOrgansAsList(entitat));
		model.addAttribute("organGestorFiltreCommand", getFiltreCommand(request));
		String prefix = "es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum.";
		model.addAttribute("organGestorEstats", EnumHelper.getOptionsForEnum(OrganGestorEstatEnum.class, prefix));
		model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
		model.addAttribute("setOficina", !entitat.isOficinaEntitat());
		if (!entitat.isOficinaEntitat()) {
			model.addAttribute("oficinesEntitat", organGestorService.getOficinesSIR(entitat.getId(), entitat.getDir3Codi(), true));
		}
		return "organGestorList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request ) {

		OrganGestorFiltreCommand organGestorFiltreCommand = getFiltreCommand(request);
		PaginaDto<OrganGestorDto> organs = new PaginaDto<>();
		try {
			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
			OrganGestorDto organGestorActual = getOrganGestorActual(request);
			String organActualCodiDir3 = null;
			if (organGestorActual != null) {
				organActualCodiDir3 = organGestorActual.getCodi();
			}
			organs = organGestorService.findAmbFiltrePaginat(entitat.getId(), organActualCodiDir3,
					organGestorFiltreCommand.asDto(), DatatablesHelper.getPaginacioDtoFromRequest(request));

		} catch (SecurityException e) {
			MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
		}
		return DatatablesHelper.getDatatableResponse(request, organs, "codi");
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(HttpServletRequest request, OrganGestorFiltreCommand command, Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(request, ORGANS_FILTRE, command);
		return "organGestorList";
	}
	
//	@RequestMapping(value = "/new", method = RequestMethod.GET)
//	public String newGet(HttpServletRequest request, Model model) {
//
//		OrganGestorCommand organGestorCommand = new OrganGestorCommand();
//		EntitatDto entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
//		model.addAttribute(organGestorCommand);
//		model.addAttribute("entitat", getEntitatActualComprovantPermisos(request));
//		model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
//		model.addAttribute("setOficina", !entitat.isOficinaEntitat());
//		model.addAttribute("isModificacio", false);
//		List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findAllIdentificadorText();
//		model.addAttribute("operadorPostalList", operadorPostalList);
//		List<IdentificadorTextDto> cieList = cieService.findAllIdentificadorText();
//		model.addAttribute("cieList", cieList);
//		return "organGestorForm";
//	}
//
	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid OrganGestorCommand organGestorCommand, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			EntitatDto entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
			model.addAttribute("entitat", entitat);
			model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
			model.addAttribute("setOficina", !entitat.isOficinaEntitat());
			List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findAllIdentificadorText();
			model.addAttribute("operadorPostalList", operadorPostalList);
			List<IdentificadorTextDto> cieList = cieService.findAllIdentificadorText();
			model.addAttribute("cieList", cieList);
			if (organGestorCommand.getId() != null) {
				model.addAttribute("isModificacio", true);
			}
			return "organGestorForm";
		}
		if (organGestorCommand.getId() != null) {
			organGestorService.update(OrganGestorCommand.asDto(organGestorCommand));
		}
		return getModalControllerReturnValueSuccess(request,"redirect:organgestor","organgestor.controller.creat.ok");
	}
	
	@RequestMapping(value = "/{organGestorId}", method = RequestMethod.GET)
	public String update(HttpServletRequest request, Model model, @PathVariable Long organGestorId) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		try {
			OrganGestorDto organGestorDto = organGestorService.findById(entitat.getId(), organGestorId);
			OrganGestorCommand organGestorCommand = OrganGestorCommand.asCommand(organGestorDto);
			entitat = entitatService.findById(entitat.getId());
			model.addAttribute(organGestorCommand);
			model.addAttribute("entitat", entitat);
			model.addAttribute("setLlibre", !entitat.isLlibreEntitat());
			model.addAttribute("setOficina", !entitat.isOficinaEntitat());
			model.addAttribute("isModificacio", true);
			List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findAllIdentificadorText();
			model.addAttribute("operadorPostalList", operadorPostalList);
			List<IdentificadorTextDto> cieList = cieService.findAllIdentificadorText();
			model.addAttribute("cieList", cieList);
			return "organGestorForm";
		} catch (Exception e) {
			log.error(String.format("Excepció intentant actualitzar l'òrgan gestor (Id=%d):", organGestorId), e);
			return getAjaxControllerReturnValueError(request, "redirect:../../organgestor", "organgestor.controller.update.nom.error");
		}
	}
	
//	@RequestMapping(value = "/update/auto", method = RequestMethod.GET)
//	public String actualitzacioAutomaticaGet(HttpServletRequest request, Model model) {
//
//		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
//		model.addAttribute("isUpdatingOrgans", organGestorService.isUpdatingOrgans(entitat));
//		return "organGestorActualitzacioForm";
//	}

	@RequestMapping(value = "/update/auto/progres", method = RequestMethod.GET)
	@ResponseBody
	public ProgresActualitzacioDto getProgresActualitzacio(HttpServletRequest request) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		ProgresActualitzacioDto progresActualitzacio = organGestorService.getProgresActualitzacio(entitat.getDir3Codi());
		if (progresActualitzacio == null) {
//			log.error("No s'ha trobat el progres actualització d'organs gestors per a l'entitat {}", entitat.getDir3Codi());
			return new ProgresActualitzacioDto();
		}
		if (progresActualitzacio.getFase() == 2) {
//			ProgresActualitzacioDto progresProc = ProcedimentServiceImpl.progresActualitzacio.get(entitat.getDir3Codi());
			ProgresActualitzacioDto progresProc = procedimentService.getProgresActualitzacio(entitat.getDir3Codi());
			if (progresProc != null && progresProc.getInfo() != null && ! progresProc.getInfo().isEmpty()) {
				ProgresActualitzacioDto progresAcumulat = new ProgresActualitzacioDto();
				progresAcumulat.setProgres(27 + (progresProc.getProgres() * 18 / 100));
				progresAcumulat.getInfo().addAll(progresActualitzacio.getInfo());
				progresAcumulat.getInfo().addAll(progresProc.getInfo());
//				log.info("Progres actualització organs gestors fase 2: {}",  progresAcumulat.getProgres());
				return progresAcumulat;
			}
		}
		if (progresActualitzacio.getFase() != 3) {
			return progresActualitzacio;
		}
//		ProgresActualitzacioDto progresSer = ServeiServiceImpl.progresActualitzacioServeis.get(entitat.getDir3Codi());
		ProgresActualitzacioDto progresSer = serveiService.getProgresActualitzacio(entitat.getDir3Codi());
		if (progresSer != null && progresSer.getInfo() != null && ! progresSer.getInfo().isEmpty()) {
			ProgresActualitzacioDto progresAcumulat = new ProgresActualitzacioDto();
			progresAcumulat.setProgres(45 + (progresSer.getProgres() * 18 / 100));
			progresAcumulat.getInfo().addAll(progresActualitzacio.getInfo());
			progresAcumulat.getInfo().addAll(progresSer.getInfo());
//			log.info("Progres actualització organs gestors fase 3: {}", progresAcumulat.getProgres());
			return progresAcumulat;
		}
//		log.info("Progres actualització organs gestors fase {}: {}",progresActualitzacio.getFase(), progresActualitzacio.getProgres());
		return progresActualitzacio;
	}

//	@RequestMapping(value = "/update", method = RequestMethod.POST)
//	public String updateNoms(HttpServletRequest request, Model model) {
//
//		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
//		String url = "redirect:../organgestor";
//		String msg = "organgestor.controller.update.nom.tots.ok";
//		try {
//			OrganGestorDto organGestorActual = getOrganGestorActual(request);
//			organGestorService.updateAll(entitat.getId(), organGestorActual != null ? organGestorActual.getCodi() : null);
//			return getAjaxControllerReturnValueSuccess(request, url, msg);
//		} catch (Exception e) {
//			log.error("Excepció intentant actualitzar tots els òrgans gestors", e);
//			msg = "organgestor.controller.update.nom.tots.error";
//			return getAjaxControllerReturnValueError(request, url, msg);
//		}
//	}

	@RequestMapping(value = "/sync/dir3", method = RequestMethod.GET)
	public String syncDir3(HttpServletRequest request, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var redirect = "redirect:../../organgestor";
		if (entitat.getDir3Codi() == null || entitat.getDir3Codi().isEmpty()) {
			return getAjaxControllerReturnValueError(request, redirect, "L'entitat actual no té cap codi DIR3 associat");
		}
		try {
			PrediccioSincronitzacio prediccio = organGestorService.predictSyncDir3OrgansGestors(entitat.getId());
			model.addAttribute("isFirstSincronization", prediccio.isFirstSincronization());
			model.addAttribute("splitMap", prediccio.getSplitMap());
			model.addAttribute("mergeMap", prediccio.getMergeMap());
			model.addAttribute("substMap", prediccio.getSubstMap());
			model.addAttribute("unitatsVigents", prediccio.getUnitatsVigents());
			model.addAttribute("unitatsNew", prediccio.getUnitatsNew());
			model.addAttribute("unitatsExtingides", prediccio.getUnitatsExtingides());
			model.addAttribute("isUpdatingOrgans", organGestorService.isUpdatingOrgans(entitat));
			return "synchronizationPrediction";
		} catch (Exception ex) {
			log.error("Error al obtenir la predicció de la sincronitzacio", ex);
			var msg = "[NC-007]";
			var text = "organgestor.actualitzacio.sense.canvis.no.codi.error";
			return ex.getMessage() != null && ex.getMessage().contains(msg)
					? getModalControllerReturnValueSuccess(request, redirect, text, new Object[] {entitat.getDir3Codi()})
					: getModalControllerReturnValueErrorMessageText(request, redirect, ex.getMessage());
		}
	}

	@RequestMapping(value = "/saveSynchronize", method = RequestMethod.POST)
	public String synchronizePost(HttpServletRequest request) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			organGestorService.syncDir3OrgansGestors(entitatActual);
			return getModalControllerReturnValueSuccess(request, "redirect:unitatOrganitzativa", "organgestor.controller.synchronize.ok");
		} catch (Exception e) {
			log.error("Error al syncronitzar", e);
			return getModalControllerReturnValueErrorMessageText(request, "redirect:../../organgestor", e.getMessage());
		}
	}

	
//	@RequestMapping(value = "/{organGestorCodi}/delete", method = RequestMethod.GET)
//	public String delete(HttpServletRequest request, @PathVariable String organGestorCodi) {
//
//		String redirect = "redirect:../../procediment";
//		try {
//			EntitatDto entitat = getEntitatActualComprovantPermisos(request);
//			OrganGestorDto organ = organGestorService.findByCodi(entitat.getId(), organGestorCodi);
//			if (organ == null) {
//				return getAjaxControllerReturnValueError(request, redirect,"organgestor.controller.esborrat.ko");
//			}
//			if (organGestorService.organGestorEnUs(organ.getId())) {
//				return getAjaxControllerReturnValueError(request, redirect,"organgestor.controller.esborrat.us");
//			}
//			organGestorService.delete(entitat.getId(), organ.getId());
//			return getAjaxControllerReturnValueSuccess(request, redirect,"organgestor.controller.esborrat.ok");
//		} catch (Exception e) {
//			log.error(String.format("Excepció intentant esborrar l'òrgan gestor %s:", organGestorCodi), e);
//			return getAjaxControllerReturnValueError(request, redirect,"organgestor.controller.esborrat.ko");
//		}
//	}

	@ResponseBody
	@RequestMapping(value = "/llibre/{organGestorDir3Codi}", method = RequestMethod.GET)
	private LlibreDto getLlibreOrgan(HttpServletRequest request, Model model, @PathVariable String organGestorDir3Codi) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		return organGestorService.getLlibreOrganisme(entitat.getId(), organGestorDir3Codi);
	}

	@ResponseBody
	@RequestMapping(value = "/oficines/{organGestorDir3Codi}", method = RequestMethod.GET)
	private List<OficinaDto> getOficinesOrgan(HttpServletRequest request, Model model, @PathVariable String organGestorDir3Codi) {

		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		return organGestorService.getOficinesSIR(entitat.getId(), organGestorDir3Codi, false);
	}

	public OrganGestorFiltreCommand getFiltreCommand(HttpServletRequest request) {

		OrganGestorFiltreCommand organGestorFiltreCommand = (OrganGestorFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, ORGANS_FILTRE);
		if (organGestorFiltreCommand != null) {
			return organGestorFiltreCommand;
		}
		organGestorFiltreCommand = new OrganGestorFiltreCommand();
		organGestorFiltreCommand.setEstat(OrganGestorEstatEnum.V);
		RequestSessionHelper.actualitzarObjecteSessio(request, ORGANS_FILTRE, organGestorFiltreCommand);
		return organGestorFiltreCommand;
	}
}
