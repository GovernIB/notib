/**
 * 
 */
package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.command.IntegracioFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioDetall;
import es.caib.notib.logic.intf.dto.IntegracioDiagnostic;
import es.caib.notib.logic.intf.dto.IntegracioDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.service.MonitorIntegracioService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per a la consulta d'accions de les integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/integracio")
public class IntegracioController extends BaseUserController {

	@Autowired
	private MonitorIntegracioService monitorIntegracioService;

	private static final String SESSION_ATTRIBUTE_FILTRE = "IntegracioController.session.filtre";
	private static final String INTEGRACIO_FILTRE = "integracio_filtre";

	@GetMapping
	public String get(HttpServletRequest request, Model model) {
		return getAmbCodi(request, "USUARIS", model);
	}

	@PostMapping
	public String post(HttpServletRequest request, IntegracioFiltreCommand command, Model model) {
		var codi = (String)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		if (Strings.isNullOrEmpty(codi)) {
			codi = "USUARIS";
		}
		return post(request, codi, command, model);
	}

	@PostMapping(value="/{codi}")
	public String post(HttpServletRequest request, @PathVariable @NonNull String codi, IntegracioFiltreCommand command, Model model) {
		if ("netejar".equals(request.getParameter("accio"))) {
			command = new IntegracioFiltreCommand();
		}
		RequestSessionHelper.actualitzarObjecteSessio(request, INTEGRACIO_FILTRE, command);
		return getAmbCodi(request, codi, model);
	}

	@GetMapping(value = "/{codi}")
	public String getAmbCodi(HttpServletRequest request, @PathVariable @NonNull String codi, Model model) {

		List<IntegracioDto> integracions = new ArrayList<>();
		// Consulta el número d'errors per codi d'integracio
		try {
			var errors = monitorIntegracioService.countErrors();
			IntegracioCodi.stream().forEach(integracioCodi -> integracions.add(IntegracioDto.builder()
					.codi(integracioCodi)
					.nom(EnumHelper.getOneOptionForEnum(IntegracioCodi.class, "integracio.list.pipella." + integracioCodi).getText())
					.numErrors(errors.get(integracioCodi))
					.build()));
		} catch (Exception ex) {
			var msg = "Error contant el nombre d'integracions amb error";
			log.error(msg, ex);
			MissatgesHelper.warning(request, msg);
		}
		var command = IntegracioFiltreCommand.getFiltreCommand(request, INTEGRACIO_FILTRE);
		model.addAttribute("integracioFiltreCommand", command);
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, codi);
		model.addAttribute("codiActual", codi);
		model.addAttribute("integracions", integracions);
		RequestSessionHelper.actualitzarObjecteSessio(request, INTEGRACIO_FILTRE, command);
		model.addAttribute("integracioEstats", EnumHelper.getOptionsForEnum(IntegracioAccioEstatEnumDto.class, "es.caib.notib.logic.intf.dto.IntegracioAccioEstatEnumDto."));
		model.addAttribute("integracioTipus", EnumHelper.getOptionsForEnum(IntegracioAccioTipusEnumDto.class, "es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto."));
		model.addAttribute("codiActual", RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE));
		log.info(String.format("[INTEGRACIONS] - Carregant dades de %s", codi));
		return "integracioList";
	}

	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {

		var paginacio = DatatablesHelper.getPaginacioDtoFromRequest(request);
		var codi = (String)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		var filtre = IntegracioFiltreCommand.getFiltreCommand(request, INTEGRACIO_FILTRE);
		var accions = monitorIntegracioService.integracioFindDarreresAccionsByCodi(IntegracioCodi.valueOf(codi), paginacio, filtre != null ? filtre.asDto() : null);
		return DatatablesHelper.getDatatableResponse(request, accions);
	}

	@GetMapping(value = "/{codi}/detall/{id}")
	@ResponseBody
	public IntegracioDetall detall(HttpServletRequest request, @PathVariable String codi, @PathVariable Long id, Model model) {
		return monitorIntegracioService.detallIntegracio(id);
	}


	// Eliminar informació de integracions de la BBDD
	@GetMapping(value = "/netejar")
	public String netejar(HttpServletRequest request, Model model) {

		var redirect = "redirect:../integracio";
		getEntitatActualComprovantPermisos(request);
		try {
			monitorIntegracioService.netejarMonitor();
			return getAjaxControllerReturnValueSuccess(request, redirect, "integracio.netejar.ok");
		} catch (Exception ex) {
			return getAjaxControllerReturnValueError(request, redirect, "integracio.netejar.error");
		}
	}

	@GetMapping(value = "/diagnostic")
	public String diagnostic(HttpServletRequest request, Model model) {

		List<IntegracioDto> integracions = new ArrayList<>();
		IntegracioCodi.stream().filter(integracioCodi -> !IntegracioCodi.CALLBACK.equals(integracioCodi))
				.forEach(integracioCodi -> integracions.add(IntegracioDto.builder()
				.codi(integracioCodi)
				.nom(EnumHelper.getOneOptionForEnum(IntegracioCodi.class, "integracio.list.pipella." + integracioCodi).getText())
				.build()));
		model.addAttribute("integracions", integracions);
		return "integracioDiagnostic";
	}

	@ResponseBody
	@GetMapping(value = "/diagnostic/{codiIntegracio}")
	public IntegracioDiagnostic diagnosticAjax(HttpServletRequest request, @PathVariable String codiIntegracio, Model model) {

		return monitorIntegracioService.diagnostic(codiIntegracio);
	}
}
