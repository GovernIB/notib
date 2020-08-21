/**
 * 
 */
package es.caib.notib.war.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.core.api.dto.ColumnesDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.ColumnesCommand;
import es.caib.notib.war.command.NotificacioEnviamentCommand;
import es.caib.notib.war.command.NotificacioEnviamentFiltreCommand;
import es.caib.notib.war.command.NotificacioFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.EntitatHelper;
import es.caib.notib.war.helper.MissatgesHelper;
import es.caib.notib.war.helper.RequestSessionHelper;
import es.caib.notib.war.helper.RolHelper;
/**
 * Controlador per el mantinement d'enviaments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/enviament")
public class EnviamentController extends BaseUserController {

	private static final String ENVIAMENTS_FILTRE = "enviaments_filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "EnviamentController.session.seleccio";
	private static final String ENVIAMENT_ID = "EnviamentController.session.enviament.id";
	
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private EnviamentService enviamentService;
	@Autowired
	private NotificacioService notificacioService;
	@Autowired
	private ProcedimentService procedimentService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		UsuariDto usuariAcutal = aplicacioService.getUsuariActual();
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		ColumnesDto columnes = null;
		
		model.addAttribute(
				getFiltreCommand(request));
		model.addAttribute(
				"seleccio",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO));
		if(entitatActual != null) {
			columnes = enviamentService.getColumnesUsuari(
					entitatActual.getId(), 
					usuariAcutal);
			
			if (columnes == null) {			
				enviamentService.columnesCreate(
						usuariAcutal,
						entitatActual.getId(),
						columnes);
			}
		}else {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"enviament.controller.entitat.cap.creada"));
		}
		NotificacioEnviamentFiltreCommand filtreEnviaments = getFiltreCommand(request);

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
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) throws ParseException {
		NotificacioEnviamentFiltreCommand filtreEnviaments = getFiltreCommand(request);
		PaginaDto<NotificacioEnviamentDtoV2> enviaments = new PaginaDto<NotificacioEnviamentDtoV2>();
		boolean isUsuari = RolHelper.isUsuariActualUsuari(request);
		boolean isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());
		
		List<ProcedimentDto> procedimentsDisponibles = new ArrayList<ProcedimentDto>();
		List<String> codisProcedimentsDisponibles = new ArrayList<String>();
		
		try {
			if(filtreEnviaments.getEstat() != null && filtreEnviaments.getEstat().toString().equals("")) {
				filtreEnviaments.setEstat(null);
			}
			
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			
			if (RolHelper.isUsuariActualUsuari(request)) {
				procedimentsDisponibles = procedimentService.findProcedimentsWithPermis(entitatActual.getId(), rolsUsuariActual, PermisEnum.CONSULTA);
				for(ProcedimentDto procediment: procedimentsDisponibles) {
					codisProcedimentsDisponibles.add(procediment.getCodi());
				}
			}
			
			enviaments = enviamentService.enviamentFindByEntityAndFiltre(
					entitatActual, 
					isUsuari, 
					isUsuariEntitat,
					codisProcedimentsDisponibles, 
					NotificacioEnviamentFiltreCommand.asDto(filtreEnviaments),
					DatatablesHelper.getPaginacioDtoFromRequest(request));

		}catch(SecurityException e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"enviament.controller.entitat.cap.assignada"));
		}
		
		return DatatablesHelper.getDatatableResponse(
				request, 
				enviaments,
				"id",
				SESSION_ATTRIBUTE_SELECCIO);
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
				if(!seleccio.contains(id)) {
					seleccio.add(id);	
				}
			}
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			NotificacioEnviamentFiltreCommand filtreCommand = getFiltreCommand(request);
			try {
				
				for(Long id: enviamentService.findIdsAmbFiltre(entitatActual.getId(),NotificacioEnviamentFiltreCommand.asDto(filtreCommand))) {
					if(!seleccio.contains(id)) {
						seleccio.add(id);	
					}
				}
			} catch (NotFoundException | ParseException e) {
				e.printStackTrace();
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
	
	@RequestMapping(value = "/export/{format}", method = RequestMethod.GET)
	public String export(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String format) throws IOException {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		NotificacioEnviamentFiltreCommand filtreCommand;
		NotificacioEnviamentFiltreCommand command = getFiltreCommand(request);
		if (seleccio == null || seleccio.isEmpty() || command == null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"enviament.controller.exportacio.seleccio.buida"));
			return "redirect:../../enviament";
		} else {
			filtreCommand = (NotificacioEnviamentFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
					request,
					ENVIAMENTS_FILTRE);
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			FitxerDto fitxer;
			try {
				fitxer = enviamentService.exportacio(
						entitatActual.getId(),
						seleccio,
						format,
						NotificacioEnviamentFiltreCommand.asDto(filtreCommand));
				writeFileToResponse(
						fitxer.getNom(),
						fitxer.getContingut(),
						response);
			} catch (NotFoundException | ParseException e) {
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	@RequestMapping(value = "/reintentar/notificacio", method = RequestMethod.GET)
	@ResponseBody
	public String reintentarNotificacio(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException, RegistreNotificaException {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		String resposta = "";
		if (seleccio == null || seleccio.isEmpty()) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"enviament.controller.notificacio.seleccio.buida"));
			resposta = "error";
		} else {
			List<Long> notificacioIds = new ArrayList<Long>();
			for(Long id: seleccio) {
				NotificacioEnviamentDtoV2 e = enviamentService.getOne(id);
				if(!notificacioIds.contains(e.getNotificacioId())) {
					notificacioIds.add(e.getNotificacioId());
				}
			}
			Integer notificacionsNoRegistrades = 0;
			for(Long notificacioId: notificacioIds) {
				NotificacioDtoV2 notificacio = notificacioService.findAmbId(
						notificacioId,
						isAdministrador(request));
				if(notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT)) {
					notificacioService.registrarNotificar(notificacioId);	
				}else {
					notificacionsNoRegistrades++;	
				}
			}
			if(notificacionsNoRegistrades.equals((Integer)notificacioIds.size()) && notificacionsNoRegistrades > 1) {
				MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"enviament.controller.reintent.notificacions.pendents.KO"));
			} else if(notificacionsNoRegistrades.equals((Integer)notificacioIds.size()) && notificacionsNoRegistrades == 1){
				MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"enviament.controller.reintent.notificacio.pendents.KO"));
			} else if(notificacioIds.size() > 1){
				MissatgesHelper.info(
						request, 
						getMessage(
								request, 
								"enviament.controller.reintent.notificacions.pendents.OK"));
			} else {
				MissatgesHelper.info(
						request, 
						getMessage(
								request, 
								"enviament.controller.reintent.notificacio.pendents.OK"));
			}
			resposta = "ok";
		}
		return resposta;
	}
	
	@RequestMapping(value = "/reactivar/consulta", method = RequestMethod.GET)
	@ResponseBody
	public String reactivarConsulta(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		String resposta = "";
		if (seleccio == null || seleccio.isEmpty()) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"enviament.controller.reactivar.seleccio.buida"));
			resposta = "error";
		} else {
			try {
				enviamentService.reactivaConsultes(seleccio);
				MissatgesHelper.info(
						request, 
						getMessage(
								request, 
								"enviament.controller.reactivar.consultes.OK"));
				resposta = "ok";
			} catch (Exception e) {
				MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"enviament.controller.reactivar.consultes.KO"));
			}
		}
		return resposta;
	}
	
	@RequestMapping(value = "/reactivar/sir", method = RequestMethod.GET)
	@ResponseBody
	public String reactivarSir(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		String resposta = "";
		if (seleccio == null || seleccio.isEmpty()) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"enviament.controller.reactivar.seleccio.buida"));
			resposta = "error";
		} else {
			try {
				enviamentService.reactivaSir(seleccio);
				MissatgesHelper.info(
						request, 
						getMessage(
								request, 
								"enviament.controller.reactivar.sir.OK"));
				resposta = "ok";
			} catch (Exception e) {
				MissatgesHelper.error(
						request, 
						getMessage(
								request, 
								"enviament.controller.reactivar.sir.KO"));
			}
		}
		return resposta;
	}
	
	@RequestMapping(value = "/visualitzar", method = RequestMethod.GET)
	public String visualitzar(
			HttpServletRequest request,
			Model model) {
		UsuariDto usuari = aplicacioService.getUsuariActual();
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		
		ColumnesDto columnes = enviamentService.getColumnesUsuari(
				entitat.getId(),
				usuari);
		
		if (columnes != null) {
			model.addAttribute(ColumnesCommand.asCommand(columnes));
		} else {
			model.addAttribute(new ColumnesCommand());
		}
		
		
		return "enviamentColumns";
	}
	
	@RequestMapping(value = "/visualitzar/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid ColumnesCommand columnesCommand,
			BindingResult bindingResult,
			Model model) throws IOException {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		
		if (bindingResult.hasErrors()) {
			return "procedimentAdminForm";
		}
	
		model.addAttribute(new NotificacioFiltreCommand());
		enviamentService.columnesUpdate(
					entitat.getId(),
					ColumnesCommand.asDto(columnesCommand));
		
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:enviament",
				"enviament.controller.modificat.ok");
	}
	
	private boolean isAdministrador(HttpServletRequest request) {
		return RolHelper.isUsuariActualAdministrador(request);
	}
	
	private NotificacioEnviamentFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		NotificacioEnviamentFiltreCommand filtreCommand = (NotificacioEnviamentFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(
				request,
				ENVIAMENTS_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new NotificacioEnviamentFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					ENVIAMENTS_FILTRE,
					filtreCommand);
		}
		
		/*Cookie cookie = WebUtils.getCookie(request, COOKIE_MEUS_EXPEDIENTS);
		filtreCommand.setMeusExpedients(cookie != null && "true".equals(cookie.getValue()));*/
		return filtreCommand;
	}
}
