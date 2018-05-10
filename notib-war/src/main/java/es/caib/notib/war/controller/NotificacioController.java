/**
 * 
 */
package es.caib.notib.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.war.command.NotificacioFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.EntitatHelper;
import es.caib.notib.war.helper.EnumHelper;
import es.caib.notib.war.helper.MissatgesHelper;
import es.caib.notib.war.helper.PropertiesHelper;
import es.caib.notib.war.helper.RolHelper;

/**
 * Controlador per a la consulta i gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/notificacio")
public class NotificacioController extends BaseController {

	private final static String NOTIFICACIONS_FILTRE = "notificacions_filtre";

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private EntitatService entitatService;



	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new NotificacioFiltreCommand());
		if (RolHelper.isUsuariActualAdministrador(request)) {
			model.addAttribute(
					"entitat",
					entitatService.findAll());
		}
		model.addAttribute(
				"notificacioDestinatariEstats",
				EnumHelper.getOptionsForEnum(
						NotificacioEnviamentEstatEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto."));
		model.addAttribute("send", sendToNotificaOnAlta());
		return "notificacioList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(	
			HttpServletRequest request,
			NotificacioFiltreCommand command,
			Model model) {
		request.getSession().setAttribute(
				NOTIFICACIONS_FILTRE,
				NotificacioFiltreCommand.asDto(command));
		return "notificacioList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( HttpServletRequest request ) {
		NotificacioFiltreDto filtre = (NotificacioFiltreDto)
				request.getSession().getAttribute( NOTIFICACIONS_FILTRE );
		PaginaDto<NotificacioDto> notificacions = null;
		if (RolHelper.isUsuariActualAdministrador(request)) {
			notificacions = notificacioService.findAmbFiltrePaginat(
					filtre,
					DatatablesHelper.getPaginacioDtoFromRequest(request));
		} else if (RolHelper.isUsuariActualRepresentant(request)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual( request );
			notificacions = notificacioService.findAmbEntitatIFiltrePaginat(
					entitat.getId(),
					filtre,
					DatatablesHelper.getPaginacioDtoFromRequest(request) );
		}
		filtre = null;
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

	@RequestMapping(value = "/{notificacioId}/event", method = RequestMethod.GET)
	public String eventList(
			HttpServletRequest request,
			Model model,
			@PathVariable Long notificacioId) {
		model.addAttribute("notificacioId", notificacioId);
		model.addAttribute(
				"eventTipus",
				EnumHelper.getOptionsForEnum(
						NotificacioEventTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
		return "notificacioEvents";
	}
	@RequestMapping(value = "/{notificacioId}/event/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse eventDatatable(
			HttpServletRequest request,
			@PathVariable Long notificacioId) {
		return DatatablesHelper.getDatatableResponse(
				request,
				notificacioService.eventFindAmbNotificacio(notificacioId));
	}

	@RequestMapping(value = "/{notificacioId}/document", method = RequestMethod.GET)
	@ResponseBody
	public void documentDescarregar(
			HttpServletResponse response,
			@PathVariable Long notificacioId) throws IOException {
    	ArxiuDto arxiu = notificacioService.getDocumentArxiu(notificacioId);
    	writeFileToResponse(
    			arxiu.getNom(),
    			arxiu.getContingut(),
				response);
	}

	@RequestMapping(value = "/{notificacioId}/enviar", method = RequestMethod.GET)
	public String enviar(
			HttpServletRequest request,
			@PathVariable Long notificacioId,
			Model model) {
		boolean enviada = notificacioService.enviar(notificacioId);
		emplenarModelNotificacioInfo(
				notificacioId,
				"accions",
				model);
		if (enviada) {
			return getAjaxControllerReturnValueSuccess(
					request,
					"notificacioInfo",
					"notificacio.controller.enviament.ok");
		} else {
			return getAjaxControllerReturnValueError(
					request,
					"notificacioInfo",
					"notificacio.controller.enviament.error");
		}
	}
	
	@RequestMapping(value = "/{notificacioId}/refrescarEstat/{enviamentId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean consultarEstatLlista(
			HttpServletRequest request,
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId,
			Model model) {
		
		try {
			notificacioService.enviamentRefrescarEstat(enviamentId);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/refrescarEstat", method = RequestMethod.GET)
	public String consultarEstat(
			HttpServletRequest request,
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId,
			Model model) {
		model.addAttribute(
				"notificacioEstat",
				notificacioService.enviamentRefrescarEstat(enviamentId));
		emplenarModelEnviamentInfo(
				enviamentId,
				"accions",
				model);
		return "enviamentInfo";
	}

	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/comunicacioSeu", method = RequestMethod.GET)
	public String comunicacioSeu(
			HttpServletRequest request,
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId,
			Model model) {
		boolean totbe = notificacioService.enviamentComunicacioSeu(enviamentId);
		if (totbe) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"notificacio.controller.comunicacio.seu.ok"));
		} else {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"notificacio.controller.comunicacio.seu.error"));
		}
		emplenarModelEnviamentInfo(
				enviamentId,
				"accions",
				model);
		return "enviamentInfo";
	}

	@RequestMapping(value = "/{notificacioId}/enviament", method = RequestMethod.GET)
	@ResponseBody
	public List<NotificacioEnviamentDto> enviamentList(
			HttpServletRequest request,
			Model model,
			@PathVariable Long notificacioId) {
		List<NotificacioEnviamentDto> destinataris = notificacioService.enviamentFindAmbNotificacio(
				notificacioId);
		return destinataris;
	}

	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}", method = RequestMethod.GET)
	public String enviamentInfo(
			HttpServletRequest request,
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId,
			Model model) {
		emplenarModelEnviamentInfo(
				enviamentId,
				"dades",
				model);
		return "enviamentInfo";
	}

	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/event", method = RequestMethod.GET)
	public String enviamentEvents(
			HttpServletRequest request,
			Model model,
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId) {
		model.addAttribute("notificacioId", notificacioId);
		model.addAttribute("enviamentId", enviamentId);
		model.addAttribute(
				"eventTipus",
				EnumHelper.getOptionsForEnum(
						NotificacioEventTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
		return "enviamentEvents";
	}
	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/event/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse enviamentEventsDatatable(
			HttpServletRequest request,
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId) {
		return DatatablesHelper.getDatatableResponse(
				request,
				notificacioService.eventFindAmbNotificacioIEnviament(
						notificacioId,
						enviamentId));
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	    binder.registerCustomEditor(
	    		Boolean.class, 
	    		new CustomBooleanEditor("SI", "NO", false));
	}

	private void emplenarModelNotificacioInfo(
			Long notificacioId,
			String pipellaActiva,
			Model model) {
		model.addAttribute("pipellaActiva", pipellaActiva);
		model.addAttribute(
				"notificacio",
				notificacioService.findAmbId(notificacioId));
		model.addAttribute(
				"eventTipus",
				EnumHelper.getOptionsForEnum(
						NotificacioEventTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
	}

	private void emplenarModelEnviamentInfo(
			Long enviamentId,
			String pipellaActiva,
			Model model) {
		model.addAttribute("pipellaActiva", pipellaActiva);
		NotificacioEnviamentDto enviament = notificacioService.enviamentFindAmbId(
				enviamentId);
		model.addAttribute("enviament", enviament);
		model.addAttribute(
				"pluginSeuDisponible",
				aplicacioService.pluginSeuDisponible());
		model.addAttribute(
				"eventTipus",
				EnumHelper.getOptionsForEnum(
						NotificacioEventTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
	}
	
	public Boolean sendToNotificaOnAlta() {
		String sendToNotifica = PropertiesHelper.getProperties().getProperty(
				"es.caib.notib.notifica.send.alta");
		return !"false".equalsIgnoreCase(sendToNotifica);
	}

}
