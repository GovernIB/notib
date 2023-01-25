/**
 * 
 */
package es.caib.notib.war.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.IntegracioAccioDto;
import es.caib.notib.core.api.dto.IntegracioDetall;
import es.caib.notib.core.api.dto.IntegracioDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.service.MonitorIntegracioService;
import es.caib.notib.war.command.IntegracioFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.EnumHelper;
import es.caib.notib.war.helper.RequestSessionHelper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controlador per a la consulta d'accions de les integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/integracio")
public class IntegracioController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "IntegracioController.session.filtre";
	private static final String INTEGRACIO_FILTRE = "integracio_filtre";

	@Autowired
	private MonitorIntegracioService monitorIntegracioService;
	
	enum IntegracioEnumDto {
		USUARIS,
		REGISTRE,
		NOTIFICA,
		ARXIU,
		CALLBACK,
		GESDOC,
		UNITATS,
		GESCONADM,
		PROCEDIMENTS,
		FIRMASERV,
		VALIDASIG
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) {
		return getAmbCodi(request, "USUARIS", model);
	}

	@RequestMapping(value="/{codi}", method = RequestMethod.POST)
	public String post(HttpServletRequest request, @PathVariable @NonNull String codi, IntegracioFiltreCommand command, Model model) {

		RequestSessionHelper.actualitzarObjecteSessio(request, INTEGRACIO_FILTRE, command);
		return getAmbCodi(request, codi, model);
	}

	@RequestMapping(value = "/{codi}", method = RequestMethod.GET)
	public String getAmbCodi(HttpServletRequest request, @PathVariable @NonNull String codi, Model model) {

		List<IntegracioDto> integracions = monitorIntegracioService.integracioFindAll();
		
		// Consulta el número d'errors per codi d'integracio
		Map<String, Integer> errors = monitorIntegracioService.countErrors();
				
		for (IntegracioDto integracio: integracions) {
			for (IntegracioEnumDto integracioEnum: IntegracioEnumDto.values()) {
				if (integracio.getCodi().equals(integracioEnum.name())) {
					integracio.setNom(EnumHelper.getOneOptionForEnum(IntegracioEnumDto.class,"integracio.list.pipella." + integracio.getCodi()).getText());
				}
			}
			if (errors.containsKey(integracio.getCodi())) {
				integracio.setNumErrors(errors.get(integracio.getCodi()).intValue());
			}
		}
		IntegracioFiltreCommand command = IntegracioFiltreCommand.getFiltreCommand(request, INTEGRACIO_FILTRE);
		model.addAttribute("integracioFiltreCommand", command);
		RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, codi);
		model.addAttribute("codiActual", codi);
		model.addAttribute("integracions", integracions);
		RequestSessionHelper.actualitzarObjecteSessio(request, INTEGRACIO_FILTRE, command);
		model.addAttribute("codiActual", RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE));
		log.info(String.format("[INTEGRACIONS] - Carregant dades de %s", codi));
//		try {
//			PaginacioParamsDto paginacio = DatatablesHelper.getPaginacioDtoFromRequest(request);
//			model.addAttribute("data", (new ObjectMapper()).writeValueAsString(monitorIntegracioService.integracioFindDarreresAccionsByCodi(codi, paginacio, command.asDto())));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		return "integracioList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {

		PaginacioParamsDto paginacio = DatatablesHelper.getPaginacioDtoFromRequest(request);
		String codi = (String)RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
		IntegracioFiltreCommand filtre = IntegracioFiltreCommand.getFiltreCommand(request, INTEGRACIO_FILTRE);
		PaginaDto<IntegracioAccioDto> accions = monitorIntegracioService.integracioFindDarreresAccionsByCodi(codi, paginacio, filtre !=null ? filtre.asDto() : null);
		return DatatablesHelper.getDatatableResponse(request, accions);
	}

	@RequestMapping(value = "/{codi}/detall/{id}", method = RequestMethod.GET)
	@ResponseBody
	public IntegracioDetall detall(HttpServletRequest request, @PathVariable String codi, @PathVariable Long id, Model model) {

		return monitorIntegracioService.detallIntegracio(id);
	}

	@RequestMapping(value = "/netejar", method = RequestMethod.GET)
	public String natejar(HttpServletRequest request, Model model) {

		String redirect = "redirect:../integracio";
		EntitatDto entitat = getEntitatActualComprovantPermisos(request);
		try {
			monitorIntegracioService.netejarMonitor();
			return getAjaxControllerReturnValueSuccess(request, redirect, "integracio.netejar.ok");
		} catch (Exception ex) {
			return getAjaxControllerReturnValueError(request, redirect, "integracio.netejar.error");
		}
	}
}
