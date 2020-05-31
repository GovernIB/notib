package es.caib.notib.war.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.EnumHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/massiu")
public class ReintentMassiuController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_SELECCIO = "ContingutMassiuController.session.seleccio";
	
	@Autowired
	private EnviamentService enviamentService;
	@Autowired
	private NotificacioService notificacioService;
	
	@RequestMapping(value = "/notificacions", method = RequestMethod.GET)
	public String getNotificacions(
			HttpServletRequest request,
			Model model) {
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
		return "contingutMassiuList";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		return DatatablesHelper.getDatatableResponse(
				request,
				notificacioService.findWithCallbackError(
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
					"redirect:/massiu/portafirmes",
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
}
