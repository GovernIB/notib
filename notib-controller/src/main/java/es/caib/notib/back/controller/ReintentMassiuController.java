package es.caib.notib.back.controller;

import es.caib.notib.back.command.NotificacioErrorCallbackFiltreCommand;
import es.caib.notib.back.command.NotificacioRegistreErrorFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.intf.dto.NotificacioErrorCallbackFiltreDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreErrorFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.CallbackService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador per al manteniment de bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/massiu")
public class ReintentMassiuController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_SELECCIO = "ContingutMassiuController.session.seleccio";
	private static final String SESSION_ATTRIBUTE_REGISTRE_SELECCIO = "ContingutMassiuController.session.registre.seleccio";

	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private CallbackService callbackService;
	@Autowired
	private ProcedimentService procedimentService;
	
	private static final String MASSIU_CALLBACK_FILTRE = "massiu_callback_filtre";
	private static final String MASSIU_REGISTRE_FILTRE = "massiu_registre_filtre";
	private static final String PROCEDIMENTS = "procediments";

	
	@GetMapping(value = "/notificacions")
	public String getNotificacions(HttpServletRequest request, Model model) {

		var mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
		model.addAttribute("mantenirPaginacio", mantenirPaginacio);
		model.addAttribute(getFiltre(request));
		model.addAttribute(PROCEDIMENTS, procedimentService.findAll());
		model.addAttribute("notificacioEstats", EnumHelper.getOptionsForEnum(NotificacioEstatEnumDto.class, "es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto."));
		model.addAttribute("notificacioEnviamentEstats", EnumHelper.getOptionsForEnum(EnviamentEstat.class, "es.caib.notib.client.domini.EnviamentEstat."));
		return "contingutMassiuList";
	}
	
	@PostMapping(value = "/notificacions")
	public String post(HttpServletRequest request, NotificacioErrorCallbackFiltreCommand notificacioErrorCallbackFiltreCommand, Model model) {

		request.getSession().setAttribute(MASSIU_CALLBACK_FILTRE, NotificacioErrorCallbackFiltreCommand.asDto(notificacioErrorCallbackFiltreCommand));
		model.addAttribute(PROCEDIMENTS, procedimentService.findAll());
		model.addAttribute("notificacioEstats", EnumHelper.getOptionsForEnum(NotificacioEstatEnumDto.class, "es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto."));
		model.addAttribute("notificacioEnviamentEstats", EnumHelper.getOptionsForEnum(EnviamentEstat.class, "es.caib.notib.client.domini.EnviamentEstat."));
		return "contingutMassiuList";
	}
	
	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {

		var filtre = (NotificacioErrorCallbackFiltreDto) request.getSession().getAttribute(MASSIU_CALLBACK_FILTRE);
		var nots = notificacioService.findWithCallbackError(filtre, DatatablesHelper.getPaginacioDtoFromRequest(request));
		return DatatablesHelper.getDatatableResponse(request, nots, "id", SESSION_ATTRIBUTE_SELECCIO);
	}

	private NotificacioErrorCallbackFiltreCommand getFiltre(HttpServletRequest request) {

		var filtre = (NotificacioErrorCallbackFiltreDto) request.getSession().getAttribute(MASSIU_CALLBACK_FILTRE);
		return filtre != null ? NotificacioErrorCallbackFiltreCommand.asCommand(filtre) : new NotificacioErrorCallbackFiltreCommand();
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/notificacions/reintentar")
	public String reintentar(HttpServletRequest request, Model model) {

		var seleccio = (Set<Long>) RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(request, "redirect:/massiu/notificacions", "accio.massiva.seleccio.buida");
		}
		for (var notificacioId : seleccio) {
			callbackService.reintentarCallback(notificacioId);
		}
		RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO);
		return getModalControllerReturnValueSuccess(request, "redirect:../../massiu/notificacions", "accio.massiva.creat.ok");
	}
	
	@GetMapping(value = "/select")
	@ResponseBody
	public int select(HttpServletRequest request, @RequestParam(value="ids[]", required = false) Long[] ids) {

		@SuppressWarnings("unchecked")
		var seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<>();
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO, seleccio);
		}
		if (ids == null) {
			return seleccio.size();
		}
		Collections.addAll(seleccio, ids);
		return seleccio.size();
	}

	@GetMapping(value = "/deselect")
	@ResponseBody
	public int deselect(HttpServletRequest request, @RequestParam(value="ids[]", required = false) Long[] ids) {

		@SuppressWarnings("unchecked")
		var seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<>();
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO, seleccio);
		}
		if (ids == null) {
			seleccio.clear();
			return 0;
		}
		for (var id: ids) {
			seleccio.remove(id);
		}
		return seleccio.size();
	}
	
	@GetMapping(value = "/detallErrorCallback/{notificacioId}")
	public String info(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {
		
		var lastEvent = notificacioService.findUltimEventCallbackByNotificacio(notificacioId);
		model.addAttribute("event", lastEvent);
		return "errorCallbackDetall";
	}
	
	// REENVIAMENT A REGISTRE
	
	@GetMapping(value = "/registre/notificacionsError")
	public String getNotificacionsRegistreError(HttpServletRequest request, Model model) {

		model.addAttribute(new NotificacioRegistreErrorFiltreCommand());
		model.addAttribute(PROCEDIMENTS, procedimentService.findAll());
		return "registreMassiuList";
	}
	
	@PostMapping(value = "/registre/notificacionsError")
	public String notificacionsRegistreError(HttpServletRequest request, NotificacioRegistreErrorFiltreCommand notificacioRegistreErrorFiltreCommand, Model model) {

		request.getSession().setAttribute(MASSIU_REGISTRE_FILTRE, NotificacioRegistreErrorFiltreCommand.asDto(notificacioRegistreErrorFiltreCommand));
		model.addAttribute(PROCEDIMENTS, procedimentService.findAll());
		return "registreMassiuList";
	}
	
	@GetMapping(value = "/registre/datatable")
	@ResponseBody
	public DatatablesResponse registreDatatable(HttpServletRequest request) {

		var filtre = (NotificacioRegistreErrorFiltreDto) request.getSession().getAttribute(MASSIU_REGISTRE_FILTRE);
		var entitatActual = getEntitatActualComprovantPermisos(request);
		var nots = notificacioService.findNotificacionsAmbErrorRegistre(entitatActual.getId(), filtre, DatatablesHelper.getPaginacioDtoFromRequest(request));
		return DatatablesHelper.getDatatableResponse(request, nots, "id", SESSION_ATTRIBUTE_SELECCIO);
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(value = "/registre/notificacionsError/reintentar")
	public String registreReintentar(HttpServletRequest request, Model model) {

		var seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_REGISTRE_SELECCIO);
		var redirect = "redirect:/massiu/registre/notificacionsError";
		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(request, redirect, "accio.massiva.seleccio.buida");
		}
		List<String> notificacionsError = new ArrayList<>();
		for (var notificacioId : seleccio) {
			try {
				notificacioService.reactivarRegistre(notificacioId);
			} catch (Exception e) {
				notificacionsError.add("[" + notificacioId + "]: " + e.getMessage());
			}
		}
		RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_REGISTRE_SELECCIO);
		
		if (!notificacionsError.isEmpty()) {
			if (notificacionsError.size() == seleccio.size()) {
				return getModalControllerReturnValueError(request, redirect, "accio.massiva.creat.ko");
			}
			var desc = new StringBuilder();
			for (var err: notificacionsError) {
				desc.append(err).append(" \n");
			}
			return getModalControllerReturnValueErrorWithDescription(request, redirect, "accio.massiva.creat.part", desc.toString());
		}
		return getModalControllerReturnValueSuccess(request, redirect, "accio.massiva.creat.ok");
	}
	
	@GetMapping(value = "/registre/detallError/{notificacioId}")
	public String inforErrorReg(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {
		
		var lastEvent = notificacioService.findUltimEventRegistreByNotificacio(notificacioId);
		model.addAttribute("event", lastEvent);
		return "errorCallbackDetall";
	}
	
	@GetMapping(value = "/registre/select")
	@ResponseBody
	public int registreSelect(HttpServletRequest request, @RequestParam(value="ids[]", required = false) Long[] ids) {

		@SuppressWarnings("unchecked")
		var seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_REGISTRE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<>();
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_REGISTRE_SELECCIO, seleccio);
		}
		if (ids != null) {
			Collections.addAll(seleccio, ids);
			return seleccio.size();
		}
		var entitatActual = getEntitatActualComprovantPermisos(request);
		var filtre = (NotificacioRegistreErrorFiltreDto) request.getSession().getAttribute(MASSIU_REGISTRE_FILTRE);
		try {
			for (var id: notificacioService.findNotificacionsIdAmbErrorRegistre(entitatActual.getId(), filtre)) {
				if(!seleccio.contains(id)) {
					seleccio.add(id);
				}
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		return seleccio.size();
	}

	@GetMapping(value = "/registre/deselect")
	@ResponseBody
	public int registreDeselect(HttpServletRequest request, @RequestParam(value="ids[]", required = false) Long[] ids) {

		@SuppressWarnings("unchecked")
		var seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_REGISTRE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<>();
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_REGISTRE_SELECCIO, seleccio);
		}
		if (ids == null) {
			seleccio.clear();
			return 0;
		}
		for (var id: ids) {
			seleccio.remove(id);
		}
		return seleccio.size();
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {

		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
		binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor("SI", "NO", false));
	}
}
