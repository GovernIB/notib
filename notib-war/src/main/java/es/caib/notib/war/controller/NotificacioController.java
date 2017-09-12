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

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariDto;
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.war.command.NotificacioFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.EntitatHelper;
import es.caib.notib.war.helper.EnumHelper;
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
						NotificacioDestinatariEstatEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto."));
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
			notificacions = notificacioService.findByFiltrePaginat(
					filtre,
					DatatablesHelper.getPaginacioDtoFromRequest(request));
		} else if (RolHelper.isUsuariActualRepresentant(request)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual( request );
			notificacions = notificacioService.findByEntitatIFiltrePaginat(
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
		model.addAttribute(
				"notificacio",
				notificacioService.findById(notificacioId));
		return "notificacioInfo";
	}

	@RequestMapping(value = "/{notificacioId}/event", method = RequestMethod.GET)
	public String eventList(
			HttpServletRequest request,
			Model model,
			@PathVariable Long notificacioId) {
		model.addAttribute("notificacioId", notificacioId);
		return "notificacioEvents";
	}
	@RequestMapping(value = "/{notificacioId}/event/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse eventDatatable(
			HttpServletRequest request,
			@PathVariable Long notificacioId) {
		List<NotificacioEventDto> dto = notificacioService.eventFindByNotificacio(
				notificacioId);
		for (NotificacioEventDto event: dto) {
			if (event.getDestinatari() == null) {
				event.setDestinatari(new NotificacioDestinatariDto());
				event.getDestinatari().setReferencia("-");
			}
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				dto);
	}

	@RequestMapping(value = "/{notificacioId}/document", method = RequestMethod.GET)
	@ResponseBody
	public void documentDescarregar(
			HttpServletResponse response,
			@PathVariable Long notificacioId) throws IOException {
    	FitxerDto fitxer = notificacioService.findFitxer(notificacioId);
    	writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
	}

	@RequestMapping(value = "/{notificacioId}/enviament", method = RequestMethod.GET)
	@ResponseBody
	public List<NotificacioDestinatariDto> enviamentList(
			HttpServletRequest request,
			Model model,
			@PathVariable Long notificacioId) {
		List<NotificacioDestinatariDto> destinataris = notificacioService.destinatariFindByNotificacio(
				notificacioId);
		return destinataris;
	}

	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/info", method = RequestMethod.GET)
	public String enviamentInfo(
			HttpServletRequest request,
			Model model,
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId) {
		NotificacioDestinatariDto enviament = notificacioService.destinatariFindById(
				enviamentId);
		model.addAttribute("enviament", enviament);
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
		return "enviamentEvents";
	}
	@RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/event/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse enviamentEventsDatatable(
			HttpServletRequest request,
			@PathVariable Long notificacioId,
			@PathVariable Long enviamentId) {
		List<NotificacioEventDto> dto = notificacioService.eventFindByNotificacioIDestinatari(
				notificacioId,
				enviamentId);
		for (NotificacioEventDto event: dto) {
			if(event.getDestinatari() == null) {
				event.setDestinatari(new NotificacioDestinatariDto());
				event.getDestinatari().setReferencia("-");
			}
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				dto);
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

}
