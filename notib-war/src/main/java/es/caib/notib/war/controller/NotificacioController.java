/**
 * 
 */
package es.caib.notib.war.controller;

import java.awt.Desktop;
import java.io.File;
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
import es.caib.notib.war.helper.RolHelper;

/**
 * Controlador per a la consulta i gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/notificacions")
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
		
		model.addAttribute( new NotificacioFiltreCommand() );
		if( RolHelper.isUsuariActualAdministrador(request) ) {
			model.addAttribute( "entitat", entitatService.findAll() );
		}
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
			notificacions = notificacioService.findFilteredByEntitatAndUsuari(
					filtre,
					DatatablesHelper.getPaginacioDtoFromRequest(request));
		} else if (RolHelper.isUsuariActualRepresentant(request)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual( request );
			notificacions = notificacioService.findByEntitat(
					entitat.getId(),
					filtre,
					DatatablesHelper.getPaginacioDtoFromRequest(request) );
		}
		filtre = null;
		return DatatablesHelper.getDatatableResponse(request, notificacions);
	}

	@RequestMapping(value = "/{notificacioId}", method = RequestMethod.GET)
	public String New(
			HttpServletRequest request,
			Model model,
			@PathVariable Long notificacioId) {
		
		model.addAttribute(notificacioService.findById(notificacioId));
		return "notificacioForm";
	}

	@RequestMapping(value = "/{notificacioId}/destinataris/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatableDestinataris(
			HttpServletRequest request,
			@PathVariable Long notificacioId ) {
		return DatatablesHelper.getDatatableResponse(
				request,
				notificacioService.findDestinatarisByNotificacioId(
						notificacioId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)
						)
				);
	}

	@RequestMapping(value = "/{notificacioId}/destinatari", method = RequestMethod.GET)
	@ResponseBody
	public List<NotificacioDestinatariDto> llistaDestinataris(
			HttpServletRequest request,
			Model model,
			@PathVariable Long notificacioId) {
		List<NotificacioDestinatariDto> destinataris = notificacioService.findDestinatarisByNotificacioId(
				notificacioId);
		return destinataris;
	}

	@RequestMapping(value = "/{notificacioId}/llistaevents", method = RequestMethod.GET)
	public String eventsNotificacio(
			HttpServletRequest request,
			Model model,
			@PathVariable Long notificacioId) {
		
		model.addAttribute("notificacioId", notificacioId);
		
		return "notificacioEvents";
		
	}

	@RequestMapping(value = "/{notificacioId}/events/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatableEvents(
			HttpServletRequest request,
			@PathVariable Long notificacioId ) {
		List<NotificacioEventDto> dto = notificacioService.findEventsByNotificacioId(notificacioId);
		for(NotificacioEventDto event: dto) {
			event.setLastModifiedDate( new Date(0) );
			event.setCreatedDate( new Date(0) );
			if(event.getDestinatari() == null) {
				event.setDestinatari(new NotificacioDestinatariDto());
				event.getDestinatari().setReferencia("-");
			}
			event.getDestinatari().setLastModifiedDate( new Date(0) );
			event.getDestinatari().setCreatedDate( new Date(0) );
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				dto);
	}

	@RequestMapping(value = "/{notificacioId}/destinatari/{destinatariId}/info", method = RequestMethod.GET)
	public String infoDestinatari(
			HttpServletRequest request,
			Model model,
			@PathVariable Long notificacioId,
			@PathVariable Long destinatariId ) {
		NotificacioDestinatariDto destinatari = notificacioService.findDestinatariById(destinatariId);
		model.addAttribute(destinatari);
		return "destinatariForm";
	}

	@RequestMapping(value = "/{notificacioId}/destinatari/{destinatariId}/llistaevents", method = RequestMethod.GET)
	public String llistaEvents(
			HttpServletRequest request,
			Model model,
			@PathVariable Long notificacioId,
			@PathVariable Long destinatariId) {
		model.addAttribute("notificacioId", notificacioId);
		model.addAttribute("destinatariId", destinatariId);
		return "destinatariEvents";
	}

	@RequestMapping(value = "/{notificacioId}/destinatari/{destinatariId}/events/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatableEventsDestinatari(
			HttpServletRequest request,
			@PathVariable Long notificacioId,
			@PathVariable Long destinatariId ) {
		List<NotificacioEventDto> dto = notificacioService.findEventsByDestinatariId(destinatariId);
		for (NotificacioEventDto event: dto) {
			event.setLastModifiedDate( new Date(0) );
			event.setCreatedDate( new Date(0) );
			if(event.getDestinatari() == null) {
				event.setDestinatari(new NotificacioDestinatariDto());
				event.getDestinatari().setReferencia("-");
			}
			event.getDestinatari().setLastModifiedDate( new Date(0) );
			event.getDestinatari().setCreatedDate( new Date(0) );
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				dto);
	}

//	@RequestMapping(
//			value = "/{notificacioId}/destinatari/{destinatariId}/events/datatable",
//			method = RequestMethod.GET)
//	@ResponseBody
//	public DatatablesResponse datatableDestinatariEvents(
//			HttpServletRequest request,
//			@PathVariable Long notificacioId,
//			@PathVariable Long destinatariId) {
//		
//		return DatatablesHelper.getDatatableResponse(
//				request,
//				notificacioService.findEventsByDestinatariId(
//						notificacioId,
//						destinatariId
//						)
//				);
//		
//	}

	@RequestMapping(value = "/descarregar/{notificacioId}", method = RequestMethod.GET)
	@ResponseBody
	public void descarregarArxiu(
			HttpServletResponse response,
			@PathVariable Long notificacioId) {
		
        try {
        	FitxerDto fitxer = notificacioService.findFitxer(notificacioId);
    		
        	writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
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
