
/**
 *
 */
package es.caib.notib.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.DocumentCommand;
import es.caib.notib.war.command.NotificacioCommandV2;
import es.caib.notib.war.command.NotificacioFiltreCommand;
import es.caib.notib.war.command.PersonaCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.EntitatHelper;
import es.caib.notib.war.helper.EnumHelper;
import es.caib.notib.war.helper.MissatgesHelper;
import es.caib.notib.war.helper.RolHelper;

/**
 * Controlador per a la consulta i gestió de notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/notificacio")
public class NotificacioController extends BaseUserController {

	private final static String NOTIFICACIONS_FILTRE = "notificacions_filtre";

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private EnviamentService enviamentService;
	@Autowired
	private GrupService grupService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request, 
			Model model) {
		
		model.addAttribute(new NotificacioFiltreCommand());
		List<ProcedimentDto> procedimentsPermisConsulta = null;
		List<ProcedimentDto> procediments = new ArrayList<ProcedimentDto>();
		List<ProcedimentGrupDto> procedimentsAmbGrups = new ArrayList<ProcedimentGrupDto>();
		List<ProcedimentDto> procedimentsSenseGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsPermisConsultaSenseGrups = new ArrayList<ProcedimentDto>();
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());

		if (RolHelper.isUsuariActualAdministrador(request)) {
			model.addAttribute("entitat", entitatService.findAll());
		}
		if (RolHelper.isUsuariActualUsuari(request)) {
			// Llistat de procediments amb grups
			procedimentsAmbGrups = procedimentService.findAllGrups();
			procedimentsSenseGrups = procedimentService.findProcedimentsSenseGrups();
			procediments = new ArrayList<ProcedimentDto>();
			// Obté els procediments que tenen el mateix grup que el rol d'usuari
			for (ProcedimentGrupDto grupProcediment : procedimentsAmbGrups) {
				for (String rol : rolsUsuariActual) {
					if (rol.contains(grupProcediment.getGrup().getCodi())) {
						procediments.add(grupProcediment.getProcediment());
					}
				}
			}

			if (!procediments.isEmpty()) {
				procedimentsPermisConsulta = notificacioService.findProcedimentsAmbPermisConsultaAndGrups(procediments);
			} else if (procedimentsAmbGrups.isEmpty()) {
				procedimentsPermisConsulta = notificacioService.findProcedimentsAmbPermisConsulta();
			}

			procedimentsPermisConsultaSenseGrups = notificacioService.findProcedimentsAmbPermisConsultaSenseGrups(procedimentsSenseGrups);

			if ((procedimentsPermisConsulta == null || procedimentsPermisConsulta.size() < 0) || (procedimentsPermisConsultaSenseGrups == null || procedimentsPermisConsultaSenseGrups.size() < 0)) {
				MissatgesHelper.warning(request, getMessage(request, "notificacio.controller.sense.permis.lectura"));
			}
		}
		model.addAttribute("notificacioEstats", 
				EnumHelper.getOptionsForEnum(NotificacioEstatEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEstatEnumDto."));
		model.addAttribute("notificacioEnviamentEstats",
				EnumHelper.getOptionsForEnum(NotificacioEnviamentEstatEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto."));
		model.addAttribute("notificacioComunicacioTipus",
				EnumHelper.getOptionsForEnum(NotificacioComunicacioTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto."));
		model.addAttribute("notificacioEnviamentTipus", 
				EnumHelper.getOptionsForEnum(NotificaEnviamentTipusEnumDto.class, 
						"es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto."));
		return "notificacioList";
	}

	@RequestMapping(value = "/new/{procedimentId}")
	public String altaForm(
			HttpServletRequest request, 
			@PathVariable Long procedimentId, 
			Model model) {
		ProcedimentDto procedimentActual = procedimentService.findById(
				null, 
				isAdministrador(request), 
				procedimentId);
		NotificacioCommandV2 notificacio = new NotificacioCommandV2();
		
		model.addAttribute("notificacioCommandV2", notificacio);
		model.addAttribute("entitat", procedimentActual.getEntitat());
		model.addAttribute("procediment", procedimentService.findById(null, isAdministrador(request), procedimentId));
		model.addAttribute("grups", grupService.findByGrupsProcediment(procedimentId));

		return "notificacioForm";
	}

	@RequestMapping(value = "/procediments", method = RequestMethod.GET)
	public String formProcediments(
			HttpServletRequest request, 
			Model model) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		List<ProcedimentDto> procedimentsPermisNotificacioAmbGrupsAndSenseGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsPermisNotificacioSenseGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsPermisNotificacio = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsAmbGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsSenseGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentGrupDto> grupsProcediment = new ArrayList<ProcedimentGrupDto>();
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());

		model.addAttribute("entitat", entitat);

		if (RolHelper.isUsuariActualUsuari(request)) {

			// Llistat de procediments amb grups
			grupsProcediment = procedimentService.findAllGrups();

			procedimentsAmbGrups = new ArrayList<ProcedimentDto>();
			procedimentsSenseGrups = procedimentService.findProcedimentsSenseGrups();
			// Obté els procediments que tenen el mateix grup que el rol d'usuari
			for (ProcedimentGrupDto grupProcediment : grupsProcediment) {

				for (String rol : rolsUsuariActual) {
					if (rol.contains(grupProcediment.getGrup().getCodi())) {
						procedimentsAmbGrups.add(grupProcediment.getProcediment());
					}
				}
			}
		}

		if (!procedimentsAmbGrups.isEmpty()) {
			// Procedimments amb i sense grups amb permís notificació
			procedimentsPermisNotificacioAmbGrupsAndSenseGrups = notificacioService.findProcedimentsAmbPermisNotificacioAndGrups(procedimentsAmbGrups);

			model.addAttribute("procediments", procedimentsPermisNotificacioAmbGrupsAndSenseGrups);
		} else if (grupsProcediment.isEmpty()) {
			// Procediments sense grups amb permís notificació
			procedimentsPermisNotificacio = notificacioService.findProcedimentsAmbPermisNotificacio();
			model.addAttribute("procediments", procedimentsPermisNotificacio);
		}

		procedimentsPermisNotificacioSenseGrups = notificacioService.findProcedimentsAmbPermisNotificacioSenseGrups(procedimentsSenseGrups);

		if (procedimentsPermisNotificacioSenseGrups != null)
			for (ProcedimentDto procedimentSenseGrupAmbPermis : procedimentsPermisNotificacioSenseGrups) {
				procedimentsPermisNotificacioAmbGrupsAndSenseGrups.add(procedimentSenseGrupAmbPermis);
			}

		return "notificacioProcedimentsForm";
	}

	@RequestMapping(value = "/new/destinatari", method = RequestMethod.GET)
	public PersonaCommand altaDestinatari(
			HttpServletRequest request, 
			Model model) {
		PersonaCommand destinatari = new PersonaCommand();
		return destinatari;
	}

	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request, 
			@Valid NotificacioCommandV2 notificacioCommand,
			BindingResult bindingResult, 
			Model model) throws IOException {
		DocumentCommand document = notificacioCommand.getDocument();
		ProcedimentDto procedimentActual = procedimentService.findById(
				null, 
				isAdministrador(request), 
				notificacioCommand.getProcedimentId());
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("enviosGuardats", notificacioCommand.getEnviaments());
            model.addAttribute("errors", bindingResult.getAllErrors());
			return "notificacioForm";
		}
		if (RolHelper.isUsuariActualAdministrador(request)) {
			model.addAttribute("entitat", entitatService.findAll());
		}
		model.addAttribute(new NotificacioFiltreCommand());

		switch (notificacioCommand.getTipusDocument()) {
		case ARXIU:
			if (notificacioCommand.getArxiu() != null && !notificacioCommand.getArxiu().isEmpty()) {
				document.setArxiuNom(notificacioCommand.getArxiu().getOriginalFilename());
				document.setNormalitzat(notificacioCommand.getDocument().isNormalitzat());
				document.setContingutBase64(notificacioCommand.getArxiu().getBytes());
			}
			break;
		case CSV:
			if (notificacioCommand.getDocumentArxiuUuidCsv() != null
					&& !notificacioCommand.getDocumentArxiuUuidCsv().isEmpty()) {
				document.setCsv(notificacioCommand.getDocumentArxiuUuidCsv());
			}
			break;
		case UUID:
			if (notificacioCommand.getDocumentArxiuUuidCsv() != null
					&& !notificacioCommand.getDocumentArxiuUuidCsv().isEmpty()) {
				document.setUuid(notificacioCommand.getDocumentArxiuUuidCsv());
			}
			break;
		}
		
		if (notificacioCommand.getId() != null) {
			notificacioService.update(
					notificacioCommand.getProcedimentId(),
					NotificacioCommandV2.asDto(notificacioCommand));
		} else {
			notificacioService.create(
					procedimentActual.getEntitat().getId(), 
					NotificacioCommandV2.asDto(notificacioCommand));
			
			model.addAttribute("notificacioEstats", 
					EnumHelper.getOptionsForEnum(NotificacioEstatEnumDto.class,
							"es.caib.notib.core.api.dto.NotificacioEstatEnumDto."));
			model.addAttribute("notificacioEnviamentEstats",
					EnumHelper.getOptionsForEnum(NotificacioEnviamentEstatEnumDto.class,
							"es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto."));
			model.addAttribute("notificacioComunicacioTipus",
					EnumHelper.getOptionsForEnum(NotificacioComunicacioTipusEnumDto.class,
							"es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto."));
			model.addAttribute("notificacioEnviamentTipus", 
					EnumHelper.getOptionsForEnum(NotificaEnviamentTipusEnumDto.class, 
							"es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto."));
		}
		return "notificacioList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request, 
			NotificacioFiltreCommand command, 
			Model model) {
		request.getSession().setAttribute(NOTIFICACIONS_FILTRE, NotificacioFiltreCommand.asDto(command));
		return "notificacioList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {
		NotificacioFiltreDto filtre = (NotificacioFiltreDto) request.getSession().getAttribute(NOTIFICACIONS_FILTRE);
		PaginaDto<NotificacioDto> notificacions = null;
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<ProcedimentDto> procediments = new ArrayList<ProcedimentDto>();
		List<ProcedimentGrupDto> grupsProcediment = new ArrayList<ProcedimentGrupDto>();
		List<ProcedimentDto> procedimentsSenseGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsPermisConsultaSenseGrups = new ArrayList<ProcedimentDto>();
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());
		boolean isUsuari = RolHelper.isUsuariActualUsuari(request);
		boolean isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
		boolean isAdministrador = RolHelper.isUsuariActualAdministrador(request);

		if (RolHelper.isUsuariActualAdministradorEntitat(request)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request);
			if (filtre != null) {
				filtre.setEntitatId(entitat.getId());
			}
		}
		if (RolHelper.isUsuariActualUsuari(request)) {
			// Llistat de procediments amb grups
			grupsProcediment = procedimentService.findAllGrups();
			procediments = new ArrayList<ProcedimentDto>();
			// Obté els procediments que tenen el mateix grup que el rol d'usuari
			for (ProcedimentGrupDto grupProcediment : grupsProcediment) {
				for (String rol : rolsUsuariActual) {
					if (rol.contains(grupProcediment.getGrup().getCodi())) {
						procediments.add(grupProcediment.getProcediment());
					}
				}
			}
			// Procediments sense grups però amb perís consulta
			procedimentsSenseGrups = procedimentService.findProcedimentsSenseGrups();

			if (!procedimentsSenseGrups.isEmpty()) {
				procedimentsPermisConsultaSenseGrups = notificacioService
						.findProcedimentsAmbPermisConsultaSenseGrups(procedimentsSenseGrups);

				for (ProcedimentDto procedimentSenseGrupAmbPermis : procedimentsPermisConsultaSenseGrups) {
					procediments.add(procedimentSenseGrupAmbPermis);
				}
			}

		}

		notificacions = notificacioService.findAmbFiltrePaginat(
				entitatActual.getId(), 
				isUsuari, 
				isUsuariEntitat,
				isAdministrador, 
				grupsProcediment, 
				procediments, 
				filtre,
				DatatablesHelper.getPaginacioDtoFromRequest(request));
		return DatatablesHelper.getDatatableResponse(request, notificacions);
	}

	@RequestMapping(value = "/{notificacioId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			Model model,
			@PathVariable Long notificacioId) {
		emplenarModelNotificacioInfo(
				notificacioId, 
				"dades", 
				model);
		return "notificacioInfo";
	}

	@RequestMapping(value = "/{notificacioId}/processar", method = RequestMethod.GET)
	public String processar(
			HttpServletRequest request, 
			Model model, 
			@PathVariable 
			Long notificacioId) {
		notificacioService.marcarComProcessada(notificacioId);

		return getModalControllerReturnValueSuccess(request, "redirect:../../notificacio",
				"notificacio.controller.refrescar.estat.ok");
	}

	@RequestMapping(value = "/{notificacioId}/event", method = RequestMethod.GET)
	public String eventList(
			HttpServletRequest request, 
			Model model, 
			@PathVariable Long notificacioId) {
		model.addAttribute("notificacioId", notificacioId);
		model.addAttribute("eventTipus", 
				EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
		return "notificacioEvents";
	}

	@RequestMapping(value = "/{notificacioId}/event/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse eventDatatable(
			HttpServletRequest request,
			@PathVariable Long notificacioId) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		return DatatablesHelper.getDatatableResponse(
				request, 
				notificacioService.eventFindAmbNotificacio(entitatActual.getId(), notificacioId));
	}

	@RequestMapping(value = "/{notificacioId}/enviar", method = RequestMethod.GET)
	public String enviar(
			HttpServletRequest request, 
			@PathVariable Long notificacioId, 
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		boolean enviada = notificacioService.enviar(entitatActual.getId(), notificacioId);
		emplenarModelNotificacioInfo(notificacioId, "accions", model);
		if (enviada) {
			return getAjaxControllerReturnValueSuccess(request, "notificacioInfo",
					"notificacio.controller.enviament.ok");
		} else {
			return getAjaxControllerReturnValueError(request, "notificacioInfo",
					"notificacio.controller.enviament.error");
		}
	}

	@RequestMapping(value = "/{notificacioId}/enviament", method = RequestMethod.GET)
	@ResponseBody
	public List<NotificacioEnviamentDto> enviamentList(
			HttpServletRequest request, 
			Model model,
			@PathVariable Long notificacioId) {
		List<NotificacioEnviamentDto> destinataris = enviamentService.enviamentFindAmbNotificacio(notificacioId);
		return destinataris;
	}

	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}", method = RequestMethod.GET)
	public String enviamentInfo(
			HttpServletRequest request, 
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId, 
			Model model) {
		emplenarModelEnviamentInfo(
				notificacioId, 
				enviamentId, 
				"dades", 
				model);
		return "enviamentInfo";
	}

	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/event/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse enviamentEventsDatatable(
			HttpServletRequest request, 
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		return DatatablesHelper.getDatatableResponse(
				request,
				notificacioService.eventFindAmbEnviament(entitatActual.getId(), notificacioId, enviamentId));
	}

	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/refrescarEstatNotifica", method = RequestMethod.GET)
	public String refrescarEstatNotifica(
			HttpServletRequest request, 
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId, 
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		NotificacioEnviamenEstatDto enviamentEstat = notificacioService.enviamentRefrescarEstat(
				entitatActual.getId(),
				enviamentId);
		boolean totbe = !enviamentEstat.isNotificaError();
		if (totbe) {
			MissatgesHelper.success(request, getMessage(request, "notificacio.controller.refrescar.estat.ok"));
		} else {
			MissatgesHelper.error(request, getMessage(request, "notificacio.controller.refrescar.estat.error"));
		}
		emplenarModelEnviamentInfo(notificacioId, enviamentId, "estatNotifica", model);
		return "enviamentInfo";
	}

	@RequestMapping(value = "/{notificacioId}/documentDescarregar", method = RequestMethod.GET)
	@ResponseBody
	public void documentDescarregar(
			HttpServletResponse response, 
			@PathVariable Long notificacioId) throws IOException {
		ArxiuDto arxiu = notificacioService.getDocumentArxiu(notificacioId);
		writeFileToResponse(arxiu.getNom(), arxiu.getContingut(), response);
	}

	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/certificacioDescarregar", method = RequestMethod.GET)
	@ResponseBody
	public void documentDescarregar(
			HttpServletResponse response,
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId) throws IOException {
		ArxiuDto arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
		writeFileToResponse(
				arxiu.getNom(), 
				arxiu.getContingut(), 
				response);
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(
				Date.class, 
				new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(
				Boolean.class, 
				new CustomBooleanEditor("SI", "NO", false));
	}

	private void emplenarModelNotificacioInfo(
			Long notificacioId, 
			String pipellaActiva, 
			Model model) {
		NotificacioDtoV2 notificacio = notificacioService.findAmbId(notificacioId);
		
		model.addAttribute("pipellaActiva", pipellaActiva);
		model.addAttribute("notificacio", notificacio);
		model.addAttribute("eventTipus", 
				EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
		if (notificacio.getProcediment() != null && !notificacio.getProcedimentCodiNotib().isEmpty()) {
			model.addAttribute("permisGestio", procedimentService.hasPermisGestioProcediment(notificacio.getProcedimentCodiNotib()));
		} else {
			model.addAttribute("permisGestio", null);
		}
	}

	private void emplenarModelEnviamentInfo(
			Long notificacioId, 
			Long enviamentId, 
			String pipellaActiva, 
			Model model) {
		model.addAttribute("notificacio", notificacioService.findAmbId(notificacioId));
		model.addAttribute("pipellaActiva", pipellaActiva);
		NotificacioEnviamentDto enviament = enviamentService.enviamentFindAmbId(enviamentId);
		model.addAttribute("enviament", enviament);
		model.addAttribute("pluginSeuDisponible", aplicacioService.pluginSeuDisponible());
		model.addAttribute("eventTipus", EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class,
				"es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
	}

	private boolean isAdministrador(HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
	}
}
