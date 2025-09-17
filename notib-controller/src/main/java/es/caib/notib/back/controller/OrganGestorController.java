package es.caib.notib.back.controller;

import es.caib.notib.back.command.OrganGestorCommand;
import es.caib.notib.back.command.OrganGestorFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.organisme.NumeroPermisos;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ServeiService;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
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
	private PagadorCieService pagadorCieService;

	private static final String ORGANS_FILTRE = "organs_filtre";
	private static final String REDIRECT = "redirect:../../organgestor";
	private static final String SET_LLIBRE = "setLlibre";
	private static final String SET_OFICINA = "setOficina";
	private static final String ARBRE = "ARBRE";
	private static final String ARBRE_REDIRECT = "redirect:../../../organgestorArbre";
	private static final String LIST_REDIRECT = "redirect:../../../organgestor";

	private static final String SET_COOKIE = "Set-cookie";
	private static final String FILE_DOWNLOAD = "fileDownload=true; path=/";


	@GetMapping
	public String get(HttpServletRequest request, Model model) {

		var entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
		var filtres = getFiltreCommand(request);
        var usuari = sessionScopedContext.getUsuariActual();
        if (usuari.getOrganDefecte() != null) {
            var o = organGestorService.findById(entitat.getId(), usuari.getOrganDefecte());
            filtres.setCodi(o.getCodi());
            filtres.setIsFiltre("true");
        }
		model.addAttribute("organGestorFiltreCommand", filtres);
		model.addAttribute("organsEntitat", organGestorService.getOrgansAsList(entitat));
		model.addAttribute("organGestorFiltreCommand", getFiltreCommand(request));
		model.addAttribute("numeroPermisosList", EnumHelper.getOptionsForEnum(NumeroPermisos.class, "es.caib.notib.logic.intf.dto.organisme.NumeroPermisos."));
		var estats = EnumHelper.getOptionsForEnum(OrganGestorEstatEnum.class, "es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum.");
		model.addAttribute("organGestorEstats", estats);
		model.addAttribute(SET_LLIBRE, !entitat.isLlibreEntitat());
		model.addAttribute(SET_OFICINA, !entitat.isOficinaEntitat());
		if (!entitat.isOficinaEntitat()) {
			model.addAttribute("oficinesEntitat", organGestorService.getOficinesSIR(entitat.getId(), entitat.getDir3Codi(), true));
		}
		return "organGestorList";
	}

	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request ) {

		var organGestorFiltreCommand = getFiltreCommand(request);
		var organs = new PaginaDto<OrganGestorDto>();
		try {
			var entitat = getEntitatActualComprovantPermisos(request);
			var organGestorActual = getOrganGestorActual(request);
			String organActualCodiDir3 = null;
			if (organGestorActual != null) {
				organActualCodiDir3 = organGestorActual.getCodi();
			}
			var paginacio = DatatablesHelper.getPaginacioDtoFromRequest(request);
			organs = organGestorService.findAmbFiltrePaginat(entitat.getId(), organActualCodiDir3, organGestorFiltreCommand.asDto(), paginacio);
		} catch (SecurityException e) {
			MissatgesHelper.error(request, getMessage(request, "notificacio.controller.entitat.cap.assignada"));
		}
		return DatatablesHelper.getDatatableResponse(request, organs, "codi");
	}

	@GetMapping(value = "/export")
	public String export(HttpServletRequest request, HttpServletResponse response) throws IOException {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			FitxerDto fitxer = organGestorService.exportacio(entitatActual.getId());
			writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
		} catch (NotFoundException ex) {
			log.error("Error generant la exportació dels òrgans", ex);
		}
		return null;
	}

	@PostMapping
	public String post(HttpServletRequest request, OrganGestorFiltreCommand command, Model model) {
		
		RequestSessionHelper.actualitzarObjecteSessio(request, ORGANS_FILTRE, command);
		return "organGestorList";
	}

	@PostMapping(value = "/new")
	public String save(HttpServletRequest request, @Valid OrganGestorCommand organGestorCommand, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			var entitat = entitatService.findById(getEntitatActualComprovantPermisos(request).getId());
			model.addAttribute("entitat", entitat);
			model.addAttribute(SET_LLIBRE, !entitat.isLlibreEntitat());
			model.addAttribute(SET_OFICINA, !entitat.isOficinaEntitat());
			var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());
			List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findByEntitatAndOrgan(entitat, organGestorCommand.getCodi(), isAdminOrgan);
			model.addAttribute("operadorPostalList", operadorPostalList);
			var cieList = pagadorCieService.findByEntitatAndOrgan(entitat, organGestorCommand.getCodi(), isAdminOrgan);
			model.addAttribute("cieList", cieList);
			if (organGestorCommand.getId() != null) {
				model.addAttribute("isModificacio", true);
			}
			return "organGestorForm";
		}
		if (organGestorCommand.getId() != null) {
			organGestorService.update(OrganGestorCommand.asDto(organGestorCommand));
		}
		return getModalControllerReturnValueSuccess(request,"redirect:organgestor","organgestor.controller.update.nom.ok");
	}
	
	@GetMapping(value = "/{organGestorId}")
	public String update(HttpServletRequest request, Model model, @PathVariable Long organGestorId) {

		var entitat = getEntitatActualComprovantPermisos(request);
		try {
			var organGestorDto = organGestorService.findById(entitat.getId(), organGestorId);
			var organGestorCommand = OrganGestorCommand.asCommand(organGestorDto);
			entitat = entitatService.findById(entitat.getId());
			model.addAttribute(organGestorCommand);
			model.addAttribute("entitat", entitat);
			model.addAttribute(SET_LLIBRE, !entitat.isLlibreEntitat());
			model.addAttribute(SET_OFICINA, !entitat.isOficinaEntitat());
			model.addAttribute("isModificacio", true);
			var isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());
			List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findByEntitatAndOrgan(entitat, organGestorDto.getCodi(), isAdminOrgan);
			model.addAttribute("operadorPostalList", operadorPostalList);
			var cieList = pagadorCieService.findByEntitatAndOrgan(entitat, organGestorDto.getCodi(), isAdminOrgan);
			model.addAttribute("cieList", cieList);
			return "organGestorForm";
		} catch (Exception e) {
			log.error(String.format("Excepció intentant actualitzar l'òrgan gestor (Id=%d):", organGestorId), e);
			return getAjaxControllerReturnValueError(request, REDIRECT, "organgestor.controller.update.nom.error");
		}
	}

	@GetMapping(value = "/update/auto/progres")
	@ResponseBody
	public ProgresActualitzacioDto getProgresActualitzacio(HttpServletRequest request) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var progresActualitzacio = organGestorService.getProgresActualitzacio(entitat.getDir3Codi());
		if (progresActualitzacio == null) {
			return new ProgresActualitzacioDto();
		}

		if (progresActualitzacio.getFase() == 3) {
			var progresProc = procedimentService.getProgresActualitzacio(entitat.getDir3Codi());
			if (progresProc != null && progresProc.getInfo() != null && ! progresProc.getInfo().isEmpty()) {
				ProgresActualitzacioDto progresAcumulat = new ProgresActualitzacioDto();
				progresAcumulat.setProgres(45 + (progresProc.getProgres() * 18 / 100));
				progresAcumulat.getInfo().addAll(progresActualitzacio.getInfo());
				progresAcumulat.getInfo().addAll(progresProc.getInfo());
				return progresAcumulat;
			}
		}
		if (progresActualitzacio.getFase() == 4) {
			var progresSer = serveiService.getProgresActualitzacio(entitat.getDir3Codi());
			if (progresSer != null && progresSer.getInfo() != null && ! progresSer.getInfo().isEmpty()) {
				ProgresActualitzacioDto progresAcumulat = new ProgresActualitzacioDto();
				progresAcumulat.setProgres(63 + (progresSer.getProgres() * 18 / 100));
				progresAcumulat.getInfo().addAll(progresActualitzacio.getInfo());
				progresAcumulat.getInfo().addAll(progresSer.getInfo());
				return progresAcumulat;
			}
		}
		return progresActualitzacio;
	}

	@GetMapping(value = "/{organGestorId}/sincronitzar/{lloc}")
	public String sincronitzar(HttpServletRequest request, @PathVariable Long organGestorId, @PathVariable String lloc, Model model) {

		String redirect = ARBRE.equalsIgnoreCase(lloc) ? ARBRE_REDIRECT : LIST_REDIRECT;
		try {
			organGestorService.sincronitzar(organGestorId);
			return getAjaxControllerReturnValueSuccess(request, redirect, "organgestor.controller.sincronitzada.ok");
		} catch (Exception ex) {
			return getAjaxControllerReturnValueError(request, redirect, "organgestor.controller.sincronitzada.ko");
		}
	}

	@GetMapping(value = "/sync/dir3")
	public String syncDir3(HttpServletRequest request, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var redirect = REDIRECT;
		if (entitat.getDir3Codi() == null || entitat.getDir3Codi().isEmpty()) {
			return getAjaxControllerReturnValueError(request, redirect, "L'entitat actual no té cap codi DIR3 associat");
		}
		try {
			var prediccio = organGestorService.predictSyncDir3OrgansGestors(entitat.getId());
			model.addAttribute("isFirstSincronization", prediccio.isFirstSincronization());
			model.addAttribute("splitMap", prediccio.getSplitMap() != null && !prediccio.getSplitMap().isEmpty() ? prediccio.getSplitMap() : null);
			model.addAttribute("mergeMap", prediccio.getMergeMap() != null && !prediccio.getMergeMap().isEmpty() ? prediccio.getMergeMap() : null);
			model.addAttribute("substMap", prediccio.getSubstMap() != null && !prediccio.getSubstMap().isEmpty() ? prediccio.getSubstMap() : null);
			model.addAttribute("unitatsVigents", prediccio.getUnitatsVigents());
			model.addAttribute("unitatsNew", prediccio.getUnitatsNew());
			model.addAttribute("unitatsExtingides", prediccio.getUnitatsExtingides());
			model.addAttribute("isUpdatingOrgans", organGestorService.isUpdatingOrgans(entitat));
		} catch (Exception ex) {
			log.error("Error al obtenir la predicció de la sincronitzacio", ex);
			var msg = "[NC-007]";
			var text = "organgestor.actualitzacio.sense.canvis.no.codi.error";
			return ex.getMessage() != null && ex.getMessage().contains(msg)
					? getModalControllerReturnValueSuccess(request, redirect, text, new Object[] {entitat.getDir3Codi()})
					: getModalControllerReturnValueErrorMessageText(request, redirect, ex.getMessage());
		}
		return "synchronizationPrediction";
	}

	@GetMapping(value = "/descarregar/json/organs/dir3")
	@ResponseBody
	public void descarregarDiagramaStateMachine(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
		try {
			var entitat = getEntitatActualComprovantPermisos(request);
			var arxiu = organGestorService.getJsonOrgansGestorDir3(entitat.getId());
			writeFileToResponse("organsDir3JSON.json", arxiu, response);
		} catch (Exception ex) {
			log.debug("Error al obtenir la plantilla de el model de dades CSV de càrrega massiva", ex);
		}
	}

	@PostMapping(value = "/saveSynchronize")
	public String synchronizePost(HttpServletRequest request) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			organGestorService.syncDir3OrgansGestors(entitatActual);
			organGestorService.deleteHistoricSincronitzacio();
			return getModalControllerReturnValueSuccess(request, "redirect:unitatOrganitzativa", "organgestor.controller.synchronize.ok");
		} catch (Exception e) {
			log.error("Error al syncronitzar", e);
			return getModalControllerReturnValueErrorMessageText(request, REDIRECT, e.getMessage());
		}
	}

	@GetMapping(value = "/sync/oficines/{lloc}")
	public String syncOficinesSIR(HttpServletRequest request, @PathVariable String lloc, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var redirect = ARBRE.equalsIgnoreCase(lloc) ? ARBRE_REDIRECT : LIST_REDIRECT;
		try {
			organGestorService.syncOficinesSIR(entitat.getId());
			return getAjaxControllerReturnValueSuccess(request, redirect,"organgestor.list.boto.actualitzar.oficines.ok");
		} catch (Exception ex) {
			log.error("Error actualitzant les oficines SIR ", ex);
			return getAjaxControllerReturnValueError(request, redirect,"organgestor.list.boto.actualitzar.oficines.error");
		}
	}

	@GetMapping(value = "/sync/noms/{lloc}")
	public String syncNoms(HttpServletRequest request, @PathVariable String lloc, Model model) {

		var entitat = getEntitatActualComprovantPermisos(request);
		var redirect = ARBRE.equalsIgnoreCase(lloc) ? ARBRE_REDIRECT : LIST_REDIRECT;
		try {
			List<Long> ids = new ArrayList<>();
			ids.add(entitat.getId());
			organGestorService.sincronitzarOrganNomMultidioma(ids);
			return getAjaxControllerReturnValueSuccess(request, redirect,"organgestor.list.boto.actualitzar.noms.ok");
		} catch (Exception ex) {
			log.error("Error actualitzant les oficines SIR ", ex);
			return getAjaxControllerReturnValueError(request, redirect,"organgestor.list.boto.actualitzar.noms.error");
		}
	}

	@ResponseBody
	@GetMapping(value = "/llibre/{organGestorDir3Codi}")
	public LlibreDto getLlibreOrgan(HttpServletRequest request, Model model, @PathVariable String organGestorDir3Codi) {

		var entitat = getEntitatActualComprovantPermisos(request);
		return organGestorService.getLlibreOrganisme(entitat.getId(), organGestorDir3Codi);
	}

	@ResponseBody
	@GetMapping(value = "/oficines/{organGestorDir3Codi}")
	public List<OficinaDto> getOficinesOrgan(HttpServletRequest request, Model model, @PathVariable String organGestorDir3Codi) {

		var entitat = getEntitatActualComprovantPermisos(request);
		return organGestorService.getOficinesSIR(entitat.getId(), organGestorDir3Codi, false);
	}

	public static OrganGestorFiltreCommand getFiltreCommand(HttpServletRequest request) {

		var organGestorFiltreCommand = (OrganGestorFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(request, ORGANS_FILTRE);
		if (organGestorFiltreCommand != null) {
			return organGestorFiltreCommand;
		}
		organGestorFiltreCommand = new OrganGestorFiltreCommand();
		organGestorFiltreCommand.setEstat(OrganGestorEstatEnum.V);
		RequestSessionHelper.actualitzarObjecteSessio(request, ORGANS_FILTRE, organGestorFiltreCommand);
		return organGestorFiltreCommand;
	}
	
}
