
/**
 *
 */
package es.caib.notib.war.controller;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.IdiomaEnumDto;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.LocalitatsDto;
import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamenEstatDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaisosDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProcedimentGrupDto;
import es.caib.notib.core.api.dto.ProvinciesDto;
import es.caib.notib.core.api.dto.RegistreDocumentacioFisicaEnumDto;
import es.caib.notib.core.api.dto.RegistreIdDto;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.PagadorCieFormatFullaService;
import es.caib.notib.core.api.service.PagadorCieFormatSobreService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.EntregapostalCommand;
import es.caib.notib.war.command.EnviamentCommand;
import es.caib.notib.war.command.MarcarProcessatCommand;
import es.caib.notib.war.command.NotificacioCommandV2;
import es.caib.notib.war.command.NotificacioFiltreCommand;
import es.caib.notib.war.command.PersonaCommand;
import es.caib.notib.war.helper.CaducitatHelper;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.EntitatHelper;
import es.caib.notib.war.helper.EnumHelper;
import es.caib.notib.war.helper.MissatgesHelper;
import es.caib.notib.war.helper.ProcedimentHelper;
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
	@Autowired
	private PagadorCieFormatSobreService pagadorCieFormatSobreService;
	@Autowired
	private PagadorCieFormatFullaService pagadorCieFormatFullaService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request, 
			Model model) {
		
		model.addAttribute(new NotificacioFiltreCommand());
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (RolHelper.isUsuariActualUsuari(request)) {
			if (ProcedimentHelper.sensePermisos(
					request, 
					entitatActual, 
					aplicacioService, 
					notificacioService, 
					procedimentService)) {
				MissatgesHelper.warning(request, getMessage(request, "notificacio.controller.sense.permis.lectura"));
			}
		}
		model.addAttribute("procedimentsPermisLectura", notificacioService.findProcedimentsEntitatAmbPermisConsulta(entitatActual));
		if (RolHelper.isUsuariActualAdministrador(request)) {
			model.addAttribute("entitat", entitatService.findAll());
			model.addAttribute("procedimentsPermisLectura", notificacioService.findProcedimentsAmbPermisConsulta());
		}
		model.addAttribute("notificacioEstats", 
				EnumHelper.getOptionsForEnum(NotificacioEstatEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEstatEnumDto."));
		model.addAttribute("tipusUsuari", 
				EnumHelper.getOptionsForEnum(TipusUsuariEnumDto.class,
						"es.caib.notib.core.api.dto.TipusUsuariEnumDto."));
		model.addAttribute("notificacioEnviamentEstats",
				EnumHelper.getOptionsForEnum(NotificacioEnviamentEstatEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto."));
		model.addAttribute("notificacioComunicacioTipus",
				EnumHelper.getOptionsForEnum(NotificacioComunicacioTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto."));
		model.addAttribute("notificacioEnviamentTipus", 
				EnumHelper.getOptionsForEnum(NotificaEnviamentTipusEnumDto.class, 
						"es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto."));
		model.addAttribute("mostrarColumnaEntitat", 
				aplicacioService.propertyGet("es.caib.notib.columna.entitat"));
		model.addAttribute("mostrarColumnaNumExpedient", 
				aplicacioService.propertyGet("es.caib.notib.columna.num.expedient"));
		return "notificacioList";
	}

	@RequestMapping(value = "/new/{procedimentId}")
	public String altaForm(
			HttpServletRequest request, 
			@PathVariable Long procedimentId, 
			Model model) {
		emplenarModelNotificacio(request, procedimentId, model);
		return "notificacioForm";
	}

	@RequestMapping(value = "/procediments", method = RequestMethod.GET)
	public String formProcediments(
			HttpServletRequest request, 
			Model model) {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		List<ProcedimentDto> procedimentsPermisNotificacioAmbGrupsAndSenseGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsPermisNotificacioSenseGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsPermisNotificacio = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsSenseGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentGrupDto> grupsProcediment = new ArrayList<ProcedimentGrupDto>();
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());
		Map<String, ProcedimentDto> uniqueProcediments = new HashMap<String, ProcedimentDto>();
		model.addAttribute("entitat", entitatActual);

		if (RolHelper.isUsuariActualUsuari(request)) {

			// Llistat de procediments amb grups
			grupsProcediment = procedimentService.findAllGrups();
			procedimentsSenseGrups = procedimentService.findProcedimentsSenseGrups(entitatActual);
			// Obté els procediments que tenen el mateix grup que el rol d'usuari
			for (ProcedimentGrupDto grupProcediment : grupsProcediment) {
				for (String rol : rolsUsuariActual) {
					if (rol.contains(grupProcediment.getGrup().getCodi())) {
						if ((grupProcediment.getProcediment().getEntitat().getDir3Codi().equals(entitatActual.getDir3Codi()))) {
							uniqueProcediments.put(grupProcediment.getProcediment().getCodi(), grupProcediment.getProcediment());
//							procedimentsAmbGrups.add(grupProcediment.getProcediment());
						}
					}
				}
			}
		}

		if (!uniqueProcediments.isEmpty()) {
			// Procedimments amb i sense grups amb permís notificació
			procedimentsPermisNotificacioAmbGrupsAndSenseGrups = notificacioService.findProcedimentsAmbPermisNotificacioAndGrupsAndEntitat(
					uniqueProcediments,
					entitatActual);			
			model.addAttribute("procediments", procedimentsPermisNotificacioAmbGrupsAndSenseGrups);
		} else if (grupsProcediment.isEmpty()) {
			// Procediments sense grups amb permís notificació
			procedimentsPermisNotificacio = notificacioService.findProcedimentsAmbPermisNotificacio(entitatActual);
			model.addAttribute("procediments", procedimentsPermisNotificacio);
			
		}
		
		procedimentsPermisNotificacioSenseGrups = notificacioService.findProcedimentsAmbPermisNotificacioSenseGrupsAndEntitat(
				procedimentsSenseGrups,
				entitatActual);
		
		if (procedimentsPermisNotificacioSenseGrups != null && !procedimentsPermisNotificacioSenseGrups.isEmpty()) {
			for (ProcedimentDto procedimentSenseGrupAmbPermis : procedimentsPermisNotificacioSenseGrups) {
				if (!procedimentsPermisNotificacioAmbGrupsAndSenseGrups.contains(procedimentSenseGrupAmbPermis)) {
					procedimentsPermisNotificacioAmbGrupsAndSenseGrups.add(procedimentSenseGrupAmbPermis);
				}
			}
			model.addAttribute("procediments", procedimentsPermisNotificacioAmbGrupsAndSenseGrups);
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
		List<String> tipusDocumentEnumDto = new ArrayList<String>();
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		ProcedimentDto procedimentActual = procedimentService.findById(
				null, 
				isAdministrador(request), 
				notificacioCommand.getProcedimentId());
		notificacioCommand.setUsuariCodi(aplicacioService.getUsuariActual().getCodi());
		if (bindingResult.hasErrors()) {
			ompliModelFormulari(
					procedimentActual, 
					entitatActual,
					notificacioCommand,
					bindingResult,
					tipusDocumentEnumDto,
					model);
			return "notificacioForm";
		}
		if (RolHelper.isUsuariActualAdministrador(request)) {
			model.addAttribute("entitat", entitatService.findAll());
		}
		model.addAttribute(new NotificacioFiltreCommand());
		if (notificacioCommand.getTipusDocument() != null) {
			switch (notificacioCommand.getTipusDocument()) {
			case ARXIU:
				if (notificacioCommand.getArxiu() != null && !notificacioCommand.getArxiu().isEmpty()) {
					notificacioCommand.getDocument().setArxiuNom(notificacioCommand.getArxiu().getOriginalFilename());
					notificacioCommand.getDocument().setNormalitzat(notificacioCommand.getDocument().isNormalitzat());
					String contingutBase64 = Base64.encodeBase64String(notificacioCommand.getArxiu().getBytes());
					notificacioCommand.getDocument().setContingutBase64(contingutBase64);
					notificacioCommand.getDocument().setMetadadesKeys(notificacioCommand.getDocument().getMetadadesKeys());
					notificacioCommand.getDocument().setMetadadesValues(notificacioCommand.getDocument().getMetadadesValues());
				}
				break;
			case CSV:
				if (notificacioCommand.getDocumentArxiuCsv() != null
						&& !notificacioCommand.getDocumentArxiuCsv().isEmpty()) {
					notificacioCommand.getDocument().setCsv(notificacioCommand.getDocumentArxiuCsv());
				}
				break;
			case UUID:
				if (notificacioCommand.getDocumentArxiuUuid() != null
						&& !notificacioCommand.getDocumentArxiuUuid().isEmpty()) {
					notificacioCommand.getDocument().setUuid(notificacioCommand.getDocumentArxiuUuid());
				}
				break;
			case URL:
				if (notificacioCommand.getDocumentArxiuUrl() != null
						&& !notificacioCommand.getDocumentArxiuUrl().isEmpty()) {
					notificacioCommand.getDocument().setUrl(notificacioCommand.getDocumentArxiuUrl());
				}
				break;
			}
		}
		
		try {
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
				model.addAttribute("tipusUsuari", 
						EnumHelper.getOptionsForEnum(TipusUsuariEnumDto.class,
								"es.caib.notib.core.api.dto.TipusUsuariEnumDto."));
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
		} catch (Exception ex) {
			logger.error("Error creant una notificació", ex);
			MissatgesHelper.error(request, ex.getMessage());
			ompliModelFormulari(
					procedimentActual, 
					entitatActual,
					notificacioCommand,
					bindingResult,
					tipusDocumentEnumDto,
					model);
			return "notificacioForm";
		}
		
		return "redirect:../notificacio";
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
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		PaginaDto<NotificacioDto> notificacions = new PaginaDto<NotificacioDto>();
//		List<ProcedimentDto> procediments = new ArrayList<ProcedimentDto>();
		Map<String, ProcedimentDto> uniqueProcediments = new HashMap<String, ProcedimentDto>();
		List<ProcedimentGrupDto> grupsProcediment = new ArrayList<ProcedimentGrupDto>();
		List<ProcedimentDto> procedimentsSenseGrups = new ArrayList<ProcedimentDto>();
		List<ProcedimentDto> procedimentsPermisConsultaSenseGrups = new ArrayList<ProcedimentDto>();
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());
		boolean isUsuari = RolHelper.isUsuariActualUsuari(request);
		boolean isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
		boolean isAdministrador = RolHelper.isUsuariActualAdministrador(request);

		try {
			if (RolHelper.isUsuariActualAdministradorEntitat(request)) {
				EntitatDto entitat = EntitatHelper.getEntitatActual(request);
				if (filtre != null) {
					filtre.setEntitatId(entitat.getId());
				}
			}
			if (RolHelper.isUsuariActualUsuari(request)) {
				// Llistat de procediments amb grups
				grupsProcediment = procedimentService.findAllGrups();
				// Obté els procediments que tenen el mateix grup que el rol d'usuari
				for (ProcedimentGrupDto grupProcediment : grupsProcediment) {
					for (String rol : rolsUsuariActual) {
						if (rol.contains(grupProcediment.getGrup().getCodi())) {
							//si el procediment es de l'entitat actual
							if ((grupProcediment.getProcediment().getEntitat().getDir3Codi().equals(entitatActual.getDir3Codi()))) {
								uniqueProcediments.put(grupProcediment.getProcediment().getCodi(), grupProcediment.getProcediment());
//								procediments.add(grupProcediment.getProcediment());
							}
						}
					}
				}
				// Procediments sense grups però amb perís consulta
				procedimentsSenseGrups = procedimentService.findProcedimentsSenseGrups(entitatActual);
	
				if (!procedimentsSenseGrups.isEmpty()) {
					procedimentsPermisConsultaSenseGrups = notificacioService.findProcedimentsAmbPermisConsultaSenseGrupsAndEntitat(
									procedimentsSenseGrups,
									entitatActual);
	
					for (ProcedimentDto procedimentSenseGrupAmbPermis : procedimentsPermisConsultaSenseGrups) {
						uniqueProcediments.put(procedimentSenseGrupAmbPermis.getCodi(), procedimentSenseGrupAmbPermis);
					}
				}
	
			}
			notificacions = notificacioService.findAmbFiltrePaginat(
					entitatActual.getId(), 
					isUsuari, 
					isUsuariEntitat,
					isAdministrador, 
					grupsProcediment, 
					uniqueProcediments, 
					filtre,
					DatatablesHelper.getPaginacioDtoFromRequest(request));
		}catch(SecurityException e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"notificacio.controller.entitat.cap.assignada"));
		}
		
		return DatatablesHelper.getDatatableResponse(request, notificacions);
	}

	@RequestMapping(value = "/{notificacioId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			Model model,
			@PathVariable Long notificacioId) {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		
		emplenarModelNotificacioInfo(
				entitatActual,
				notificacioId, 
				request,
				"dades", 
				model);
		return "notificacioInfo";
	}

	@RequestMapping(value = "/{notificacioId}/processar", method = RequestMethod.GET)
	public String processarGet(
			HttpServletRequest request, 
			Model model, 
			@PathVariable 
			Long notificacioId) {
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute(command);
		return "notificacioMarcarProcessat";
	}
	
	@RequestMapping(value = "/{notificacioId}/processar", method = RequestMethod.POST)
	public String processarPost(
			HttpServletRequest request, 
			Model model, 
			@PathVariable 
			Long notificacioId,
			@Valid MarcarProcessatCommand command) throws MessagingException {
		try {
			String resposta = notificacioService.marcarComProcessada(
					notificacioId,
					command.getMotiu());

			if (resposta != null) {
				MissatgesHelper.warning(request, resposta);
			}
			return getModalControllerReturnValueSuccess(
					request, 
					"redirect:../../notificacio",
					"notificacio.controller.refrescar.estat.ok");
		} catch (Exception exception) {
			return "notificacioMarcarProcessat";
		}

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

		boolean enviada = notificacioService.enviar(notificacioId);
		emplenarModelNotificacioInfo(
				entitatActual,
				notificacioId, 
				request,
				"accions", 
				model);
		if (enviada) {
			return getAjaxControllerReturnValueSuccess(request, "notificacioInfo",
					"notificacio.controller.enviament.ok");
		} else {
			return getAjaxControllerReturnValueError(request, "notificacioInfo",
					"notificacio.controller.enviament.error");
		}
	}
	
	@RequestMapping(value = "/{notificacioId}/registrar", method = RequestMethod.GET)
	public String registrar(
			HttpServletRequest request, 
			@PathVariable Long notificacioId, 
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		List<RegistreIdDto> registresIdDto = notificacioService.registrarNotificar(notificacioId);
		
		emplenarModelNotificacioInfo(
				entitatActual,
				notificacioId, 
				request,
				"accions", 
				model);
		if(registresIdDto.size() > 0) {
			for(RegistreIdDto registreIdDto :registresIdDto) {
				if (registreIdDto.getNumero() != null) {
					MissatgesHelper.success(request, "(" + registreIdDto.getNumeroRegistreFormat() + ")" + getMessage(
							request,
							"notificacio.controller.registrar.ok"));
				} else {
					MissatgesHelper.error(request, getMessage(
							request,
							"notificacio.controller.registrar.error"));
				}
			}	
		} else {
			MissatgesHelper.error(request, getMessage(
					request, 
					"notificacio.controller.registrar.error"));
		}
		
		return "notificacioInfo";
//		if (registreIdDto.getNumeroRegistreFormat() != null) {
//			return getAjaxControllerReturnValueSuccess(request, "notificacioInfo",
//					"notificacio.controller.registrar.ok");
//		} else {
//			return getAjaxControllerReturnValueError(request, "notificacioInfo",
//					"notificacio.controller.registrar.error");
//		}
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
				model,
				request);
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
		emplenarModelEnviamentInfo(notificacioId, enviamentId, "estatNotifica", model, request);
		return "enviamentInfo";
	}

	@RequestMapping(value = "/{notificacioId}/documentDescarregar", method = RequestMethod.GET)
	@ResponseBody
	public void documentDescarregar(
			HttpServletResponse response,
			@PathVariable Long notificacioId) throws IOException {
		ArxiuDto arxiu = notificacioService.getDocumentArxiu(notificacioId);
		String mimeType = "";
		if(arxiu.getContentType() == "application_pdf" || arxiu.getContentType() == "application/pdf" || arxiu.getContentType() == "PDF" && !arxiu.getNom().contains(".pdf")) {
			mimeType = ".pdf";
		}
		writeFileToResponse(arxiu.getNom() + mimeType, arxiu.getContingut(), response);
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
	
	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/justificantDescarregar", method = RequestMethod.GET)
	@ResponseBody
	public void justificantDescarregar(
			HttpServletResponse response,
			HttpServletRequest request, 
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId) throws IOException {
		ArxiuDto arxiu = new ArxiuDto();
		arxiu.setContingut(enviamentService.getDocumentJustificant(enviamentId));
		arxiu.setNom("justificant");
		String mimeType = ".pdf";
		writeFileToResponse(arxiu.getNom() + mimeType, arxiu.getContingut(), response);
	}
	
	@RequestMapping(value = "/{notificacioId}/refrescarEstatClient", method = RequestMethod.GET)
	public String refrescarEstatClient(
			HttpServletResponse response,
			HttpServletRequest request, 
			Model model,
			@PathVariable Long notificacioId) throws IOException {
		List<NotificacioEventDto> events = enviamentService.eventFindAmbNotificacio(notificacioId);
		boolean notificat = false;
		
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		
		emplenarModelNotificacioInfo(
				entitatActual,
				notificacioId, 
				request,
				"dades", 
				model);
		
		if (events != null && events.size() > 0) {
			NotificacioEventDto lastEvent = events.get(events.size() - 1);
			
			if(lastEvent.isError() && 
					(lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.CALLBACK_CLIENT) ||
					lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT) ||
					lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO) ||
					lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE) || 
					lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT))) {
				logger.info("Preparant per notificar canvi del event : " + lastEvent.getId() + " de tipus " + lastEvent.getTipus().name());
				notificat = enviamentService.reintentarCallback(lastEvent.getId());
			}
		}
		
		if (notificat) {
			MissatgesHelper.success(request, 
					getMessage(
							request,
							"notificacio.controller.notificar.client.ok"));
		} else {
			MissatgesHelper.error(request, 
					getMessage(
							request,
							"notificacio.controller.notificar.client.error"));
		}
		return "notificacioInfo";
	}

	private void emplenarModelNotificacioInfo(
			EntitatDto entitatActual,
			Long notificacioId, 
			HttpServletRequest request,
			String pipellaActiva, 
			Model model) {
		NotificacioDtoV2 notificacio = notificacioService.findAmbId(
				notificacioId,
				isAdministrador(request));

		if (notificacio.getGrupCodi() != null) {
			GrupDto grup = grupService.findByCodi(
					notificacio.getGrupCodi(), 
					entitatActual.getId());
			notificacio.setGrup(grup);
		}
		
		model.addAttribute("pipellaActiva", pipellaActiva);
		model.addAttribute("notificacio", notificacio);
		model.addAttribute("eventTipus", 
				EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
		if (notificacio.getProcediment() != null && !notificacio.getProcedimentCodiNotib().isEmpty()) {
			model.addAttribute("permisGestio", procedimentService.hasPermisGestioProcediment(
					notificacio.getProcediment().getId()));
		} else {
			model.addAttribute("permisGestio", null);
		}
		model.addAttribute("permisAdmin", request.isUserInRole("NOT_ADMIN"));
	}
	
	

	private void emplenarModelEnviamentInfo(
			Long notificacioId, 
			Long enviamentId, 
			String pipellaActiva, 
			Model model,
			HttpServletRequest request) {
		model.addAttribute("notificacio", notificacioService.findAmbId(notificacioId, isAdministrador(request)));
		model.addAttribute("pipellaActiva", pipellaActiva);
		NotificacioEnviamentDto enviament = enviamentService.enviamentFindAmbId(enviamentId);
		model.addAttribute("enviament", enviament);
		model.addAttribute("eventTipus", EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class,
				"es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
	}

	private void emplenarModelNotificacio(
			HttpServletRequest request, 
			@PathVariable Long procedimentId, 
			Model model) {
		
		ProcedimentDto procedimentActual = procedimentService.findById(
				null, 
				isAdministrador(request), 
				procedimentId);
		NotificacioCommandV2 notificacio = new NotificacioCommandV2();
		List<EnviamentCommand> enviaments = new ArrayList<EnviamentCommand>();
		EnviamentCommand enviament = new EnviamentCommand();
		EntregapostalCommand entregaPostal = new EntregapostalCommand();
		entregaPostal.setPaisCodi("ES");
//		entregaPostal.setProvincia("7");
		enviament.setEntregaPostal(entregaPostal);
		enviaments.add(enviament);
		notificacio.setEnviaments(enviaments);
		List<String> tipusDocumentEnumDto = new ArrayList<String>();
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		List<TipusDocumentDto>  tipusDocuments =  entitatService.findTipusDocumentByEntitat(entitatActual.getId());
		TipusDocumentEnumDto tipusDocumentDefault = entitatService.findTipusDocumentDefaultByEntitat(entitatActual.getId());
		notificacio.setCaducitat(CaducitatHelper.sumarDiesLaborals(procedimentActual.getCaducitat()));

		if (tipusDocuments != null) {
			for (TipusDocumentDto tipusDocument: tipusDocuments) {
				tipusDocumentEnumDto.add(tipusDocument.getTipusDocEnum().name());
			}
			if (tipusDocumentDefault != null) {
				notificacio.setTipusDocumentDefault(tipusDocumentDefault.name());
			}
		}
		model.addAttribute("isTitularAmbIncapacitat", aplicacioService.propertyGet("es.caib.notib.titular.incapacitat"));
		model.addAttribute("isMultiplesDestinataris", aplicacioService.propertyGet("es.caib.notib.destinatari.multiple"));
		model.addAttribute("notificacioCommandV2", notificacio);
		model.addAttribute("ambEntregaDeh", entitatActual.isAmbEntregaDeh());
		model.addAttribute("ambEntregaCie", entitatActual.isAmbEntregaCie());
		model.addAttribute("tipusDocumentEnumDto", tipusDocumentEnumDto);
		model.addAttribute("entitat", procedimentActual.getEntitat());
		model.addAttribute("procediment", procedimentService.findById(
				null, 
				isAdministrador(request), 
				procedimentId));
		model.addAttribute("amagat", Boolean.FALSE);
		
		model.addAttribute("grups", grupService.findByProcedimentGrups(procedimentId));
		if (procedimentActual.getPagadorcie() != null) {
			model.addAttribute("formatsFulla", pagadorCieFormatFullaService.findFormatFullaByPagadorCie(procedimentActual.getPagadorcie().getId()));
			model.addAttribute("formatsSobre", pagadorCieFormatSobreService.findFormatSobreByPagadorCie(procedimentActual.getPagadorcie().getId()));
		}
		
		model.addAttribute("comunicacioTipus", 
				EnumHelper.getOptionsForEnum(
						NotificacioComunicacioTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto."));
		model.addAttribute("enviamentTipus", 
				EnumHelper.getOptionsForEnum(
						NotificaEnviamentTipusEnumDto.class,
						"notificacio.tipus.enviament.enum."));
		model.addAttribute("serveiTipus", 
				EnumHelper.getOptionsForEnum(
						ServeiTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto."));
		model.addAttribute("interessatTipus", 
				EnumHelper.getOptionsForEnum(
						InteressatTipusEnumDto.class,
						"es.caib.notib.core.api.dto.interessatTipusEnumDto."));
		model.addAttribute("entregaPostalTipus", 
				EnumHelper.getOptionsForEnum(
						NotificaDomiciliConcretTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto."));
		model.addAttribute("registreDocumentacioFisica", 
				EnumHelper.getOptionsForEnum(
						RegistreDocumentacioFisicaEnumDto.class,
						"es.caib.notib.core.api.dto.registreDocumentacioFisicaEnumDto."));
		model.addAttribute("idioma", 
				EnumHelper.getOptionsForEnum(
						IdiomaEnumDto.class,
						"es.caib.notib.core.api.dto.idiomaEnumDto."));
		
		try {
			model.addAttribute("concepteSize", notificacio.getConcepteDefaultSize());
			model.addAttribute("descripcioSize", notificacio.getDescripcioDefaultSize());
		} catch (Exception ex) {
			logger.error("No s'ha pogut recuperar la longitud del concepte: " + ex.getMessage());
		}
		
	}
	
	@RequestMapping(value = "/paisos", method = RequestMethod.GET)
	@ResponseBody
	private List<PaisosDto> getPaisos(
		HttpServletRequest request,
		Model model) {		
		return notificacioService.llistarPaisos();
	}
	
	@RequestMapping(value = "/provincies", method = RequestMethod.GET)
	@ResponseBody
	private List<ProvinciesDto> getProvincies(
		HttpServletRequest request,
		Model model) {		
		return notificacioService.llistarProvincies();
	}
	
	@RequestMapping(value = "/localitats/{provinciaId}", method = RequestMethod.GET)
	@ResponseBody
	private List<LocalitatsDto> getLocalitats(
		HttpServletRequest request,
		Model model,
		@PathVariable String provinciaId) {
		return notificacioService.llistarLocalitats(provinciaId);
	}

	private void ompliModelFormulari(
			ProcedimentDto procedimentActual,
			EntitatDto entitatActual,
			NotificacioCommandV2 notificacioCommand,
			BindingResult bindingResult,
			List<String> tipusDocumentEnumDto,
			Model model) {
		if (procedimentActual.getPagadorcie() != null) {
			model.addAttribute("formatsFulla", pagadorCieFormatFullaService.findFormatFullaByPagadorCie(procedimentActual.getPagadorcie().getId()));
			model.addAttribute("formatsSobre", pagadorCieFormatSobreService.findFormatSobreByPagadorCie(procedimentActual.getPagadorcie().getId()));
		}
		
		List<TipusDocumentDto>  tipusDocuments =  entitatService.findTipusDocumentByEntitat(entitatActual.getId());
		TipusDocumentEnumDto tipusDocumentDefault = entitatService.findTipusDocumentDefaultByEntitat(entitatActual.getId());

		if (tipusDocuments != null) {
			for (TipusDocumentDto tipusDocument: tipusDocuments) {
				tipusDocumentEnumDto.add(tipusDocument.getTipusDocEnum().name());
			}
			if (tipusDocumentDefault != null) {
				notificacioCommand.setTipusDocumentDefault(tipusDocumentDefault.name());
			}
		}
		
		model.addAttribute("isTitularAmbIncapacitat", aplicacioService.propertyGet("es.caib.notib.titular.incapacitat"));
		model.addAttribute("isMultiplesDestinataris", aplicacioService.propertyGet("es.caib.notib.destinatari.multiple"));
		model.addAttribute("ambEntregaDeh", entitatActual.isAmbEntregaDeh());
		model.addAttribute("ambEntregaCie", entitatActual.isAmbEntregaCie());
		model.addAttribute("tipusDocumentEnumDto", tipusDocumentEnumDto);
		model.addAttribute("grups", grupService.findByProcedimentGrups(procedimentActual.getId()));
		model.addAttribute("comunicacioTipus", 
				EnumHelper.getOptionsForEnum(
						NotificacioComunicacioTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto."));
		model.addAttribute("enviamentTipus", 
				EnumHelper.getOptionsForEnum(
						NotificaEnviamentTipusEnumDto.class,
						"notificacio.tipus.enviament.enum."));
		model.addAttribute("serveiTipus", 
				EnumHelper.getOptionsForEnum(
						ServeiTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto."));
		model.addAttribute("interessatTipus", 
				EnumHelper.getOptionsForEnum(
						InteressatTipusEnumDto.class,
						"es.caib.notib.core.api.dto.interessatTipusEnumDto."));
		model.addAttribute("entregaPostalTipus", 
				EnumHelper.getOptionsForEnum(
						NotificaDomiciliConcretTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto."));
		model.addAttribute("registreDocumentacioFisica", 
				EnumHelper.getOptionsForEnum(
						RegistreDocumentacioFisicaEnumDto.class,
						"es.caib.notib.core.api.dto.registreDocumentacioFisicaEnumDto."));
		model.addAttribute("idioma", 
				EnumHelper.getOptionsForEnum(
						IdiomaEnumDto.class,
						"es.caib.notib.core.api.dto.idiomaEnumDto."));
		model.addAttribute("enviosGuardats", notificacioCommand.getEnviaments());
		model.addAttribute("tipusDocument", notificacioCommand.getTipusDocument());
        model.addAttribute("errors", bindingResult.getAllErrors());
	
        try {
			Method concepte = NotificacioCommandV2.class.getMethod("getConcepte");
			int concepteSize = concepte.getAnnotation(Size.class).max();
			
			Method descripcio = NotificacioCommandV2.class.getMethod("getDescripcio");
			int descripcioSize = descripcio.getAnnotation(Size.class).max();
			model.addAttribute("concepteSize", concepteSize);
			model.addAttribute("descripcioSize", descripcioSize);
		} catch (Exception ex) {
			logger.error("No s'ha pogut recuperar la longitud del concepte: " + ex.getMessage());
		}
	}
	private boolean isAdministrador(HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
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
	
	private static final Logger logger = LoggerFactory.getLogger(NotificacioController.class);
}
