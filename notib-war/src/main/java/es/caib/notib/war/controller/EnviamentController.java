/**
 *
 */
package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.RolEnumDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.dto.notenviament.ColumnesDto;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.war.command.ColumnesCommand;
import es.caib.notib.war.command.NotificacioEnviamentCommand;
import es.caib.notib.war.command.NotificacioEnviamentFiltreCommand;
import es.caib.notib.war.command.NotificacioFiltreCommand;
import es.caib.notib.war.helper.*;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
/**
 * Controlador per el mantinement d'enviaments.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/enviament")
public class EnviamentController extends TableAccionsMassivesController {

	private static final String ENVIAMENTS_FILTRE = "enviaments_filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "EnviamentController.session.seleccio";
	private static final String ENVIAMENT_ID = "EnviamentController.session.enviament.id";

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private EnviamentService enviamentService;


	public EnviamentController() {
		super.sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO;
	}

	protected List<Long> getIdsElementsFiltrats(HttpServletRequest request) throws ParseException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		NotificacioEnviamentFiltreCommand filtreCommand = getFiltreCommand(request);

		return enviamentService.findIdsAmbFiltre(entitatActual.getId(), NotificacioEnviamentFiltreCommand.asDto(filtreCommand));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {

		Boolean mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
		model.addAttribute("mantenirPaginacio", mantenirPaginacio != null ? mantenirPaginacio : false);
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		ColumnesDto columnes = null;

		NotificacioEnviamentFiltreCommand filtreEnviaments = getFiltreCommand(request);
		model.addAttribute(filtreEnviaments);
		model.addAttribute("seleccio", RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO));
		if(entitatActual != null) {
			String codiUsuari = getCodiUsuariActual();
			columnes = enviamentService.getColumnesUsuari(entitatActual.getId(), codiUsuari);
			if (columnes == null) {
				enviamentService.columnesCreate(codiUsuari, entitatActual.getId(), columnes);
			}
		} else {
			MissatgesHelper.error(request, getMessage(request, "enviament.controller.entitat.cap.creada"));
		}
		model.addAttribute(new NotificacioEnviamentCommand());
		model.addAttribute("columnes", ColumnesCommand.asCommand(columnes));
		model.addAttribute("filtreEnviaments", filtreEnviaments);
		return "enviamentList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid NotificacioEnviamentFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {

		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				ENVIAMENTS_FILTRE,
				filtreCommand);

		Long enviamentId = (Long)RequestSessionHelper.obtenirObjecteSessio(
				request,
				ENVIAMENT_ID);
		if (enviamentId == null || !enviamentId.equals(filtreCommand.getId())) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					ENVIAMENT_ID,
					filtreCommand.getId());
		}

		return "redirect:enviament";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) throws ParseException {

		NotificacioEnviamentFiltreCommand filtreEnviaments = getFiltreCommand(request);
		PaginaDto<NotEnviamentTableItemDto> enviaments = new PaginaDto<>();
		boolean isAdminOrgan= RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
		String organGestorCodi = null;
		try {
			if(filtreEnviaments.getEstat() != null && filtreEnviaments.getEstat().toString().equals("")) {
				filtreEnviaments.setEstat(null);
			}
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			if (isAdminOrgan) {
				OrganGestorDto organGestorActual = getOrganGestorActual(request);
				organGestorCodi = organGestorActual.getCodi();
			}

			enviaments = enviamentService.enviamentFindByEntityAndFiltre(
					entitatActual.getId(),
					RolEnumDto.valueOf(RolHelper.getRolActual(request)),
					organGestorCodi,
					getCodiUsuariActual(),
					NotificacioEnviamentFiltreCommand.asDto(filtreEnviaments),
					DatatablesHelper.getPaginacioDtoFromRequest(request));

		}catch(SecurityException e) {
			MissatgesHelper.error(request, getMessage(request, "enviament.controller.entitat.cap.assignada"));
		}
		return DatatablesHelper.getDatatableResponse(request, enviaments,"id", SESSION_ATTRIBUTE_SELECCIO);
	}

	@RequestMapping(value = "/visualitzar", method = RequestMethod.GET)
	public String visualitzar(HttpServletRequest request, Model model) {

		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		ColumnesDto columnes = enviamentService.getColumnesUsuari(entitat.getId(), getCodiUsuariActual());
		model.addAttribute(columnes != null ? ColumnesCommand.asCommand(columnes) : new ColumnesCommand());
		return "enviamentColumns";
	}

	@RequestMapping(value = "/visualitzar/save", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid ColumnesCommand columnesCommand, BindingResult bindingResult, Model model) throws IOException {

		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (bindingResult.hasErrors()) {
			return "procedimentAdminForm";
		}
		model.addAttribute(new NotificacioFiltreCommand());
		enviamentService.columnesUpdate(entitat.getId(), ColumnesCommand.asDto(columnesCommand));
		return getModalControllerReturnValueSuccess(request, "redirect:enviament", "enviament.controller.modificat.ok");
	}

	private NotificacioEnviamentFiltreCommand getFiltreCommand(HttpServletRequest request) {

		NotificacioEnviamentFiltreCommand filtreCommand = (NotificacioEnviamentFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request, ENVIAMENTS_FILTRE);
		if (filtreCommand != null) {
			return filtreCommand;
		}
		filtreCommand = new NotificacioEnviamentFiltreCommand();
		RequestSessionHelper.actualitzarObjecteSessio(request, ENVIAMENTS_FILTRE, filtreCommand);
		return filtreCommand;

		/*Cookie cookie = WebUtils.getCookie(request, COOKIE_MEUS_EXPEDIENTS);
		filtreCommand.setMeusExpedients(cookie != null && "true".equals(cookie.getValue()));*/
	}
}
