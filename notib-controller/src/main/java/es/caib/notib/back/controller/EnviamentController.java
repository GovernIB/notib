/**
 *
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.command.ColumnesCommand;
import es.caib.notib.back.command.NotificacioEnviamentCommand;
import es.caib.notib.back.command.NotificacioEnviamentFiltreCommand;
import es.caib.notib.back.command.NotificacioFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.notenviament.ColumnesDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EnviamentService;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

		var entitatActual = getEntitatActualComprovantPermisos(request);
		var filtreCommand = getFiltreCommand(request);
		String organGestorCodi = null;
		if (RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual()) && entitatActual != null) {
			var organGestorActual = getOrganGestorActual(request);
			organGestorCodi = organGestorActual.getCodi();
		}
		return enviamentService.findIdsAmbFiltre(entitatActual.getId(), RolEnumDto.valueOf(sessionScopedContext.getRolActual()), getCodiUsuariActual(), organGestorCodi, NotificacioEnviamentFiltreCommand.asDto(filtreCommand));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {

		Boolean mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
		model.addAttribute("mantenirPaginacio", mantenirPaginacio != null ? mantenirPaginacio : false);
		var entitatActual = sessionScopedContext.getEntitatActual();
		ColumnesDto columnes = null;
		var filtreEnviaments = getFiltreCommand(request);
		model.addAttribute(filtreEnviaments);
		model.addAttribute("seleccio", RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO));
		if(entitatActual != null) {
			var codiUsuari = getCodiUsuariActual();
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
	public String post(HttpServletRequest request, @Valid NotificacioEnviamentFiltreCommand filtreCommand, BindingResult bindingResult, Model model,
					   @RequestParam(value = "accio", required = false) String accio) {

		RequestSessionHelper.actualitzarObjecteSessio(request, ENVIAMENTS_FILTRE, filtreCommand);
		var enviamentId = (Long)RequestSessionHelper.obtenirObjecteSessio(request, ENVIAMENT_ID);
		if (enviamentId == null || !enviamentId.equals(filtreCommand.getId())) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO);
			RequestSessionHelper.actualitzarObjecteSessio(request, ENVIAMENT_ID, filtreCommand.getId());
		}
		return "redirect:enviament";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request, Model model) throws ParseException {

		var filtreEnviaments = getFiltreCommand(request);
		var enviaments = new PaginaDto<NotEnviamentTableItemDto>();
		var isAdminOrgan= RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual());
		String organGestorCodi = null;
		try {
			if(filtreEnviaments.getEstat() != null && filtreEnviaments.getEstat().toString().equals("")) {
				filtreEnviaments.setEstat(null);
			}
			var entitatActual = getEntitatActualComprovantPermisos(request);
			if (isAdminOrgan) {
				var organGestorActual = getOrganGestorActual(request);
				organGestorCodi = organGestorActual.getCodi();
			}

			enviaments = enviamentService.enviamentFindByEntityAndFiltre(entitatActual.getId(), RolEnumDto.valueOf(sessionScopedContext.getRolActual()), organGestorCodi,
					getCodiUsuariActual(), NotificacioEnviamentFiltreCommand.asDto(filtreEnviaments), DatatablesHelper.getPaginacioDtoFromRequest(request));

		} catch(SecurityException e) {
			MissatgesHelper.error(request, getMessage(request, "enviament.controller.entitat.cap.assignada"));
		}
		return DatatablesHelper.getDatatableResponse(request, enviaments,"id", SESSION_ATTRIBUTE_SELECCIO);
	}

	@RequestMapping(value = "/visualitzar", method = RequestMethod.GET)
	public String visualitzar(HttpServletRequest request, Model model) {

		var entitat = sessionScopedContext.getEntitatActual();
		var columnes = enviamentService.getColumnesUsuari(entitat.getId(), getCodiUsuariActual());
		model.addAttribute(columnes != null ? ColumnesCommand.asCommand(columnes) : new ColumnesCommand());
		return "enviamentColumns";
	}

	@RequestMapping(value = "/visualitzar/save", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid ColumnesCommand columnesCommand, BindingResult bindingResult, Model model) throws IOException {

		var entitat = sessionScopedContext.getEntitatActual();
		if (bindingResult.hasErrors()) {
			return "procedimentAdminForm";
		}
		model.addAttribute(new NotificacioFiltreCommand());
		enviamentService.columnesUpdate(entitat.getId(), ColumnesCommand.asDto(columnesCommand));
		return getModalControllerReturnValueSuccess(request, "redirect:enviament", "enviament.controller.modificat.ok");
	}

	private NotificacioEnviamentFiltreCommand getFiltreCommand(HttpServletRequest request) {

		var filtreCommand = (NotificacioEnviamentFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request, ENVIAMENTS_FILTRE);
		if (filtreCommand != null) {
			setDefaultFiltreData(filtreCommand);
			return filtreCommand;
		}
		filtreCommand = new NotificacioEnviamentFiltreCommand();
		setDefaultFiltreData(filtreCommand);
		RequestSessionHelper.actualitzarObjecteSessio(request, ENVIAMENTS_FILTRE, filtreCommand);
		return filtreCommand;

		/*Cookie cookie = WebUtils.getCookie(request, COOKIE_MEUS_EXPEDIENTS);
		filtreCommand.setMeusExpedients(cookie != null && "true".equals(cookie.getValue()));*/
	}

	private  void setDefaultFiltreData(NotificacioEnviamentFiltreCommand command) {

		var df = new SimpleDateFormat("dd/MM/yyyy");
		var avui = new Date();
		if (command.getDataEnviamentFi() == null) {
			command.setDataEnviamentFi(df.format(avui));
		}
		if (command.getDataEnviamentInici() != null) {
			return;
		}
		var c = Calendar.getInstance();
		c.setTime(avui);
		c.add(Calendar.MONTH, -3);
		var inici = c.getTime();
		command.setDataEnviamentInici(df.format(inici));
	}
}
