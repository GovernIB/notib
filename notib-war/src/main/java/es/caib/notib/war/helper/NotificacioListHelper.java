/**
 * 
 */
package es.caib.notib.war.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.exception.NoPermisosException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.war.command.NotificacioFiltreCommand;
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
public class NotificacioListHelper {
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	MessageSource messageSource;

	public NotificacioFiltreCommand getFiltreCommand(
			HttpServletRequest request, String keyAttr) {
		NotificacioFiltreCommand notificacioFiltreCommand = (NotificacioFiltreCommand) request.getSession()
				.getAttribute(keyAttr);

		if (notificacioFiltreCommand == null) {
			notificacioFiltreCommand = new NotificacioFiltreCommand();
			if (getLast3months()) {
				Calendar cal = new GregorianCalendar();
				cal.add(Calendar.MONTH, -3);
				notificacioFiltreCommand.setDataInici(cal.getTime());
				notificacioFiltreCommand.setDataFi(new Date());
			}
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					keyAttr,
					notificacioFiltreCommand);
		}
		return notificacioFiltreCommand;
	}

	public void fillModel(EntitatDto entitatActual,
						  OrganGestorDto organGestorActual,
						  HttpServletRequest request, Model model) {
		ompleProcediments(entitatActual, organGestorActual, request, model);

		model.addAttribute("notificacioEstats",
				EnumHelper.getOptionsForEnum(NotificacioEstatEnumDto.class,
						"es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto."));
		model.addAttribute("tipusUsuari",
				EnumHelper.getOptionsForEnum(TipusUsuariEnumDto.class,
						"es.caib.notib.core.api.dto.TipusUsuariEnumDto."));
		model.addAttribute("notificacioEnviamentEstats",
				EnumHelper.getOptionsForEnum(NotificacioEnviamentEstatEnumDto.class,
						"es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto."));
		model.addAttribute("notificacioComunicacioTipus",
				EnumHelper.getOptionsForEnum(NotificacioComunicacioTipusEnumDto.class,
						"es.caib.notib.core.api.dto.notificacio.NotificacioComunicacioTipusEnumDto."));
		model.addAttribute("notificacioEnviamentTipus",
				EnumHelper.getOptionsForEnum(NotificaEnviamentTipusEnumDto.class,
						"es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto."));
		model.addAttribute("mostrarColumnaEntitat",
				aplicacioService.propertyGet("es.caib.notib.columna.entitat"));
		model.addAttribute("mostrarColumnaNumExpedient",
				aplicacioService.propertyGet("es.caib.notib.columna.num.expedient"));
	}

	public void ompleProcediments(
			EntitatDto entitatActual,
			OrganGestorDto organGestorActual,
			HttpServletRequest request,
			Model model) {

		List<CodiValorEstatDto> organsDisponibles = new ArrayList<>();

		Long entitatId = entitatActual.getId();
		String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
		RolEnumDto rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
		String organ = organGestorActual != null ? organGestorActual.getCodi() : null;

		if (RolHelper.isUsuariActualAdministrador(request)) {
			model.addAttribute("entitat", entitatService.findAll());
		}
//		// Eliminam l'òrgan gestor entitat  --> Per ara el mantenim, ja que hi ha notificacions realitzades a l'entitat
//		OrganGestorDto organEntitat = organGestorService.findByCodi(entitatActual.getId(), entitatActual.getDir3Codi());
//		organsGestorsDisponibles.remove(organEntitat);

		try {
			organsDisponibles = organGestorService.getOrgansGestorsDisponiblesConsulta(
					entitatId,
					usuari,
					rol,
					organ
			);
		}catch (Exception e) {
			if (ExceptionHelper.isExceptionOrCauseInstanceOf(e, NoPermisosException.class))
				MissatgesHelper.warning(request, messageSource.getMessage(
						"notificacio.controller.sense.permis.lectura",
						null,
						"???" + "notificacio.controller.sense.permis.lectura" + "???",
						new RequestContext(request).getLocale()));
		}
		model.addAttribute("organsGestorsPermisLectura", organsDisponibles);

	}
	private boolean getLast3months() {
		return PropertiesHelper.getProperties().getAsBoolean("es.caib.notib.filtre.remeses.last.3.month");
	}
}