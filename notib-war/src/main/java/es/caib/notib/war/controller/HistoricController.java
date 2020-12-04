package es.caib.notib.war.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationEstatDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationGrupDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationOrganDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationProcedimentDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationUsuariDto;
import es.caib.notib.core.api.service.HistoricService;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.HistoricFiltreCommand;
import es.caib.notib.war.helper.RequestSessionHelper;
import es.caib.notib.war.historic.ExportacioActionHistoric;

@Controller
@RequestMapping("/historic")
public class HistoricController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "HistoricController.session.filtre";
	private static final String SESSION_ATTRIBUTE_USUARIS = "HistoricController.session.usuaris";
	
	@Autowired
	private HistoricService historicService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private OrganGestorService organGestorService;

	@Autowired
	private ExportacioActionHistoric exportacioActionHistoric;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		getEntitatActualComprovantPermisos(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		model.addAttribute(historicFiltreCommand);
		model.addAttribute("showDadesOrganGestor", historicFiltreCommand.showingDadesOrganGestor());
		model.addAttribute("showDadesProcediment", historicFiltreCommand.showingDadesProcediment());
		model.addAttribute("showDadesEstat", historicFiltreCommand.showingDadesEstat());
		model.addAttribute("showDadesGrup", historicFiltreCommand.showingDadesGrups());
		model.addAttribute("showDadesUsuari", historicFiltreCommand.showingDadesUsuari());

		model.addAttribute("showingDadesActuals", historicFiltreCommand.getTipusAgrupament() == null);
		model.addAttribute("procedimentsPermisLectura", procedimentService.findAll());
		model.addAttribute("organsGestorsPermisLectura", organGestorService.findAll());
		
		String[] usuaris = (String[])RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS);
		if (usuaris == null) {
			usuaris = new String[0];
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS, usuaris);
		}
		model.addAttribute("usuarisSeleccionats", usuaris);
		
		return "historic";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid HistoricFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisos(request);
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
			return "redirect:historic";
		}
		if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return "redirect:historic";
	}

	@RequestMapping(value = "/organgestors", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricAggregationOrganDto>> getHistoricsActualsByOrganGestor(HttpServletRequest request) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		Map<OrganGestorDto, List<HistoricAggregationOrganDto>> dades = historicService.getHistoricsByOrganGestor(
				entitat.getId(),
				historicFiltreCommand.asDto());
		Map<String, List<HistoricAggregationOrganDto>> response = new HashMap<String, List<HistoricAggregationOrganDto>>();
		for (OrganGestorDto organ : dades.keySet()) {
			response.put(organ.getCodi() /* + " - " + organ.getNom()*/, dades.get(organ));
		}
		return response;
	}

	@RequestMapping(value = "/organgestors/actual", method = RequestMethod.POST)
	@ResponseBody
	public List<HistoricAggregationOrganDto> getHistoricsByOrganGestor(HttpServletRequest request) {
		getEntitatActualComprovantPermisos(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		List<HistoricAggregationOrganDto> dades = historicService.getDadesActualsByOrgansGestor(
				historicFiltreCommand.asDto());

		return dades;
	}
	
	@RequestMapping(value = "/procediments", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricAggregationProcedimentDto>> getHistoricsActualsByProcediment(HttpServletRequest request) {
		getEntitatActualComprovantPermisos(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		Map<ProcedimentDto, List<HistoricAggregationProcedimentDto>> dades = historicService.getHistoricsByProcediment(
				historicFiltreCommand.asDto());
		Map<String, List<HistoricAggregationProcedimentDto>> response = new HashMap<String, List<HistoricAggregationProcedimentDto>>();
		for (ProcedimentDto procediment : dades.keySet()) {
			response.put(procediment.getCodi() /* + " - " + procediment.getNom()*/, dades.get(procediment));
		}
		return response;
	}

	@RequestMapping(value = "/procediments/actual", method = RequestMethod.POST)
	@ResponseBody
	public List<HistoricAggregationProcedimentDto> getHistoricsByProcediment(HttpServletRequest request) {
		getEntitatActualComprovantPermisos(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		List<HistoricAggregationProcedimentDto> dades = historicService.getDadesActualsByProcediment(
				historicFiltreCommand.asDto());

		return dades;
	}
	
	@RequestMapping(value = "/estat", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricAggregationEstatDto>> getHistoricsActualsByEstat(HttpServletRequest request) {
		getEntitatActualComprovantPermisos(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		Map<NotificacioEstatEnumDto, List<HistoricAggregationEstatDto>> dades = historicService.getHistoricsByEstat(
				historicFiltreCommand.asDto());
		Map<String, List<HistoricAggregationEstatDto>> response = new HashMap<String, List<HistoricAggregationEstatDto>>();
		for (NotificacioEstatEnumDto estat : dades.keySet()) {
			response.put(estat.name(), dades.get(estat));
		}
		return response;
	}

	@RequestMapping(value = "/estat/actual", method = RequestMethod.POST)
	@ResponseBody
	public List<HistoricAggregationEstatDto> getHistoricsByEstat(HttpServletRequest request) {
		getEntitatActualComprovantPermisos(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		List<HistoricAggregationEstatDto> dades = historicService.getDadesActualsByEstat(
				historicFiltreCommand.asDto());

		return dades;
	}
	
	@RequestMapping(value = "/grups", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricAggregationGrupDto>> getHistoricsActualsByGrup(HttpServletRequest request) {
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		Map<GrupDto, List<HistoricAggregationGrupDto>> dades = historicService.getHistoricsByGrup(
				entitat.getId(),
				historicFiltreCommand.asDto());
		Map<String, List<HistoricAggregationGrupDto>> response = new HashMap<String, List<HistoricAggregationGrupDto>>();
		for (GrupDto grup : dades.keySet()) {
			response.put(grup.getCodi(), dades.get(grup));
		}
		return response;
	}

	@RequestMapping(value = "/grups/actual", method = RequestMethod.POST)
	@ResponseBody
	public List<HistoricAggregationGrupDto> getHistoricsByGrup(HttpServletRequest request) {
		getEntitatActualComprovantPermisos(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		List<HistoricAggregationGrupDto> dades = historicService.getDadesActualsByGrup(
				historicFiltreCommand.asDto());

		return dades;
	}
	
	@RequestMapping(value = "/usuaris", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<HistoricAggregationUsuariDto>> getHistoricsActualsByUsuari(HttpServletRequest request,
			@RequestParam("usuaris[]") String[] usuarisCodi) {
		// registram els usuaris consultats a la sessió
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS, usuarisCodi);

		getEntitatActualComprovantPermisos(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		Map<UsuariDto, List<HistoricAggregationUsuariDto>> dades = historicService.getHistoricsByUsuariAplicacio(
				historicFiltreCommand.asDto(),
				Arrays.asList(usuarisCodi));
		Map<String, List<HistoricAggregationUsuariDto>> response = new HashMap<String, List<HistoricAggregationUsuariDto>>();
		for (UsuariDto usuari : dades.keySet()) {
			response.put(usuari.getCodi(), dades.get(usuari));
		}
		return response;
	}

	@RequestMapping(value = "/usuaris/actual", method = RequestMethod.POST)
	@ResponseBody
	public List<HistoricAggregationUsuariDto> getHistoricsByUsuari(HttpServletRequest request) {
		getEntitatActualComprovantPermisos(request);

		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		List<HistoricAggregationUsuariDto> dades = historicService.getDadesActualsByUsuariAplicacio(
				historicFiltreCommand.asDto());

		return dades;
	}
	

	@RequestMapping(value = "/exportar", method = RequestMethod.POST)
	public String export(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam String format) throws Exception {
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto fitxer = null;
		if (historicFiltreCommand.showingDadesOrganGestor()) {
			fitxer = exportacioActionHistoric.exportarHistoricOrgansGestors(entitatActual.getId(), historicFiltreCommand.asDto(), format);
			
		} else if (historicFiltreCommand.showingDadesUsuari()) {
			String[] usuaris = (String[])RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_USUARIS);
			usuaris = usuaris == null ? new String[0] : usuaris;
			fitxer = exportacioActionHistoric.exportarHistoricUsuaris(usuaris, historicFiltreCommand.asDto(), format);
			
		} else if (historicFiltreCommand.showingDadesProcediment()) {
			fitxer = exportacioActionHistoric.exportarHistoricProcediments(historicFiltreCommand.asDto(), format);
			
		} else if (historicFiltreCommand.showingDadesEstat()) {
			fitxer = exportacioActionHistoric.exportarHistoricEstats(historicFiltreCommand.asDto(), format);
		
		} else if (historicFiltreCommand.showingDadesGrups()) {
			fitxer = exportacioActionHistoric.exportarHistoricGrups(entitatActual.getId(), historicFiltreCommand.asDto(), format);
			
		} else {
			throw new Exception("No s'han seleccionat el tipus de dades a generar");
		}

		writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
		return null;
	}
	
	////
	// PRIVATE CONTENT
	////

	private HistoricFiltreCommand getFiltreCommand(HttpServletRequest request) {
		HistoricFiltreCommand filtreCommand = (HistoricFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new HistoricFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
//		Cookie cookie = WebUtils.getCookie(request, COOKIE_MEUS_EXPEDIENTS);
//		filtreCommand.setMeusExpedients(cookie != null && "true".equals(cookie.getValue()));
		return filtreCommand;
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

}
