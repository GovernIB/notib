package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.NotificacioErrorCallbackFiltreCommand;
import es.caib.notib.war.command.NotificacioRegistreErrorFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.EnumHelper;
import es.caib.notib.war.helper.RequestSessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

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
	private EnviamentService enviamentService;
	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private ProcedimentService procedimentService;
	
	private final static String MASSIU_CALLBACK_FILTRE = "massiu_callback_filtre";
	private final static String MASSIU_REGISTRE_FILTRE = "massiu_registre_filtre";
	
	@RequestMapping(value = "/notificacions", method = RequestMethod.GET)
	public String getNotificacions(
			HttpServletRequest request,
			Model model) {
		Boolean mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
		if (mantenirPaginacio) {
			model.addAttribute("mantenirPaginacio", true);
		} else {
			model.addAttribute("mantenirPaginacio", false);
		}
		model.addAttribute(new NotificacioErrorCallbackFiltreCommand());
		model.addAttribute("procediments", procedimentService.findAll());
		model.addAttribute("notificacioEstats", 
				EnumHelper.getOptionsForEnum(NotificacioEstatEnumDto.class,
						"es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto."));
		model.addAttribute("notificacioEnviamentEstats",
				EnumHelper.getOptionsForEnum(NotificacioEnviamentEstatEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto."));
		return "contingutMassiuList";
	}
	
	@RequestMapping(value = "/notificacions", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request, 
			NotificacioErrorCallbackFiltreCommand notificacioErrorCallbackFiltreCommand,
			Model model) {
		request.getSession().setAttribute(MASSIU_CALLBACK_FILTRE, NotificacioErrorCallbackFiltreCommand.asDto(notificacioErrorCallbackFiltreCommand));
		model.addAttribute("procediments", procedimentService.findAll());
		model.addAttribute("notificacioEstats", 
				EnumHelper.getOptionsForEnum(NotificacioEstatEnumDto.class,
						"es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto."));
		model.addAttribute("notificacioEnviamentEstats",
				EnumHelper.getOptionsForEnum(NotificacioEnviamentEstatEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto."));
		return "contingutMassiuList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		NotificacioErrorCallbackFiltreDto filtre = (NotificacioErrorCallbackFiltreDto) request.getSession().getAttribute(MASSIU_CALLBACK_FILTRE);
		return DatatablesHelper.getDatatableResponse(
				request,
				notificacioService.findWithCallbackError(
						filtre,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				 "id",
				 SESSION_ATTRIBUTE_SELECCIO);
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/notificacions/reintentar", method = RequestMethod.GET)
	public String reintentar(
			HttpServletRequest request,
			Model model) {
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);

		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/notificacions",
					"accio.massiva.seleccio.buida");
		}
		for (Long notificacioId : seleccio) {
			List<NotificacioEventDto> events = notificacioService.eventFindAmbNotificacio(null, notificacioId);
			
			if (events != null && events.size() > 0) {
				NotificacioEventDto lastEvent = events.get(events.size() - 1);
				
				if(lastEvent.isError() && 
							(lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.CALLBACK_CLIENT) ||
							lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT) ||
							lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO) ||
							lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT) || 
							lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR) || 
							lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR) || 
							lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE) || 
							lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT))) {
					enviamentService.reintentarCallback(lastEvent.getId());
				}
			}
		}
		RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO);
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../massiu/notificacions",
				"accio.massiva.creat.ok");
	}
	
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselect(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.remove(id);
			}
		} else {
			seleccio.clear();
		}
		return seleccio.size();
	}
	
	@RequestMapping(value = "/detallErrorCallback/{notificacioId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request, 
			Model model,
			@PathVariable Long notificacioId) {
		
		NotificacioEventDto lastEvent = notificacioService.findUltimEventCallbackByNotificacio(notificacioId);
		model.addAttribute("event", lastEvent);
		return "errorCallbackDetall";
	}
	
	
	// REENVIAMENT A REGISTRE
	
	@RequestMapping(value = "/registre/notificacionsError", method = RequestMethod.GET)
	public String getNotificacionsRegistreError(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new NotificacioRegistreErrorFiltreCommand());
		model.addAttribute("procediments", procedimentService.findAll());
		return "registreMassiuList";
	}
	
	@RequestMapping(value = "/registre/notificacionsError", method = RequestMethod.POST)
	public String NotificacionsRegistreError(
			HttpServletRequest request, 
			NotificacioRegistreErrorFiltreCommand notificacioRegistreErrorFiltreCommand,
			Model model) {
		request.getSession().setAttribute(MASSIU_REGISTRE_FILTRE, NotificacioRegistreErrorFiltreCommand.asDto(notificacioRegistreErrorFiltreCommand));
		model.addAttribute("procediments", procedimentService.findAll());
		return "registreMassiuList";
	}
	
	@RequestMapping(value = "/registre/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse registreDatatable(
			HttpServletRequest request) {
		NotificacioRegistreErrorFiltreDto filtre = (NotificacioRegistreErrorFiltreDto) request.getSession().getAttribute(MASSIU_REGISTRE_FILTRE);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				notificacioService.findNotificacionsAmbErrorRegistre(
						entitatActual.getId(),
						filtre,
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				 "id",
				 SESSION_ATTRIBUTE_SELECCIO);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/registre/notificacionsError/reintentar", method = RequestMethod.GET)
	public String registreReintentar(
			HttpServletRequest request,
			Model model) {
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_REGISTRE_SELECCIO);

		if (seleccio == null || seleccio.isEmpty()) {
			return getModalControllerReturnValueError(
					request,
					"redirect:/massiu/registre/notificacionsError",
					"accio.massiva.seleccio.buida");
		}
		List<String> notificacionsError = new ArrayList<String>();
		for (Long notificacioId : seleccio) {
			try {
				notificacioService.reactivarRegistre(notificacioId);
			} catch (Exception e) {
				notificacionsError.add("[" + notificacioId + "]: " + e.getMessage());
			}
		}
		RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_REGISTRE_SELECCIO);
		
		if (!notificacionsError.isEmpty()) {
			if (notificacionsError.size() == seleccio.size()) {
				getModalControllerReturnValueError(
						request,
						"redirect:/massiu/registre/notificacionsError",
						"accio.massiva.creat.ko");
			} else {
				String desc = "";
				for (String err: notificacionsError)
					desc = desc + err + " \n"; 
				return getModalControllerReturnValueErrorWithDescription(
						request,
						"redirect:/massiu/registre/notificacionsError",
						"accio.massiva.creat.part",
						desc);
			}
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/massiu/registre/notificacionsError",
				"accio.massiva.creat.ok");
	}
	
	@RequestMapping(value = "/registre/detallError/{notificacioId}", method = RequestMethod.GET)
	public String inforErrorReg(
			HttpServletRequest request, 
			Model model,
			@PathVariable Long notificacioId) {
		
		NotificacioEventDto lastEvent = notificacioService.findUltimEventRegistreByNotificacio(notificacioId);
		model.addAttribute("event", lastEvent);
		return "errorCallbackDetall";
	}
	
	@RequestMapping(value = "/registre/select", method = RequestMethod.GET)
	@ResponseBody
	public int registreSelect(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_REGISTRE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_REGISTRE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			NotificacioRegistreErrorFiltreDto filtre = (NotificacioRegistreErrorFiltreDto) request.getSession().getAttribute(MASSIU_REGISTRE_FILTRE);
			try {
				for (Long id: notificacioService.findNotificacionsIdAmbErrorRegistre(entitatActual.getId(), filtre)) {
					if(!seleccio.contains(id)) {
						seleccio.add(id);	
					}
				}
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/registre/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int registreDeselect(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_REGISTRE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_REGISTRE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.remove(id);
			}
		} else {
			seleccio.clear();
		}
		return seleccio.size();
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
}
