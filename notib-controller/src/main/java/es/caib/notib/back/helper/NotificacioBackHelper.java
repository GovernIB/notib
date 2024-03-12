/**
 * 
 */
package es.caib.notib.back.helper;

import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.exception.NoPermisosException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.back.command.NotificacioFiltreCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioBackHelper {
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private SessionScopedContext sessionScopedContext;
	@Autowired
	MessageSource messageSource;


	public NotificacioFiltreCommand getFiltreCommand(HttpServletRequest request, String keyAttr) {

		NotificacioFiltreCommand notificacioFiltreCommand = (NotificacioFiltreCommand) request.getSession().getAttribute(keyAttr);
		if (notificacioFiltreCommand != null) {
//			notificacioFiltreCommand.setDefaultFiltreData();
			return notificacioFiltreCommand;
		}
		notificacioFiltreCommand = new NotificacioFiltreCommand();
		notificacioFiltreCommand.setDefaultFiltreData();
		if (getLast3months()) {
			notificacioFiltreCommand.setDefaultFiltreData();
		}
		RequestSessionHelper.actualitzarObjecteSessio(request, keyAttr, notificacioFiltreCommand);
		return notificacioFiltreCommand;
	}

	public void fillModel(EntitatDto entitatActual, OrganGestorDto organGestorActual, HttpServletRequest request, Model model) {

		ompleProcediments(entitatActual, organGestorActual, request, model);
		model.addAttribute("notificacioEstats", EnumHelper.getOptionsForEnum(NotificacioEstatEnumDto.class, "es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto."));
		model.addAttribute("tipusUsuari", EnumHelper.getOptionsForEnum(TipusUsuariEnumDto.class, "es.caib.notib.logic.intf.dto.TipusUsuariEnumDto."));
		model.addAttribute("notificacioEnviamentEstats", EnumHelper.getOptionsForEnum(EnviamentEstat.class, "es.caib.notib.client.domini.EnviamentEstat."));
		model.addAttribute("notificacioComunicacioTipus", EnumHelper.getOptionsForEnum(NotificacioComunicacioTipusEnumDto.class, "es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto."));
		model.addAttribute("notificacioEnviamentTipus", EnumHelper.getOptionsForEnum(EnviamentTipus.class, "es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto."));
		model.addAttribute("mostrarColumnaEntitat", aplicacioService.propertyGetByEntitat("es.caib.notib.columna.entitat"));
		model.addAttribute("mostrarColumnaNumExpedient", aplicacioService.propertyGetByEntitat("es.caib.notib.columna.num.expedient"));
	}

	public void ompleProcediments(EntitatDto entitatActual, OrganGestorDto organGestorActual, HttpServletRequest request, Model model) {

		List<CodiValorEstatDto> organsDisponibles = new ArrayList<>();
		var entitatId = entitatActual.getId();
		var usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		var rol = RolEnumDto.valueOf(sessionScopedContext.getRolActual());
		var organ = organGestorActual != null ? organGestorActual.getCodi() : null;
		if (RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual())) {
			model.addAttribute("entitat", entitatService.findAll());
		}
		try {
			organsDisponibles = organGestorService.getOrgansGestorsDisponiblesConsulta(entitatId, usuari, rol, organ);
		} catch (Exception e) {
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(e, NoPermisosException.class)) {
				var msg = "notificacio.controller.sense.permis.lectura";
				var defMsg = "???" + "notificacio.controller.sense.permis.lectura" + "???";
				MissatgesHelper.warning(request, messageSource.getMessage(msg, null, defMsg, new RequestContext(request).getLocale()));
			}
		}
		model.addAttribute("organsGestorsPermisLectura", organsDisponibles);

	}
	private boolean getLast3months() {
		return PropertiesHelper.getProperties().getAsBoolean("es.caib.notib.filtre.remeses.last.3.month");
	}
}
