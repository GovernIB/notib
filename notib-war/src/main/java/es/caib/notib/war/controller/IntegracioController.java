/**
 * 
 */
package es.caib.notib.war.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.core.api.dto.IntegracioAccioDto;
import es.caib.notib.core.api.dto.IntegracioDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.service.AplicacioService;
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

	@Autowired
	private AplicacioService aplicacioService;

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
		FIRMASERV
	}

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		return getAmbCodi(request, "USUARIS", model);
	}
	@RequestMapping(value = "/{codi}", method = RequestMethod.GET)
	public String getAmbCodi(
			HttpServletRequest request,
			@PathVariable @NonNull String codi,
			Model model) {
		List<IntegracioDto> integracions = aplicacioService.integracioFindAll();
		for (IntegracioDto integracio: integracions) {
			for (IntegracioEnumDto integracioEnum: IntegracioEnumDto.values()) {
				if (integracio.getCodi() == integracioEnum.name()) {
					integracio.setNom(
							EnumHelper.getOneOptionForEnum(
									IntegracioEnumDto.class,
									"integracio.list.pipella." + integracio.getCodi()).getText());
				}
			}
		}
		model.addAttribute(
				"integracions",
				integracions);

		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE,
				codi);

		model.addAttribute(
				"codiActual",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE));
		log.info(String.format("[INTEGRACIONS] - Carregant dades de %s", codi));
		try {
			model.addAttribute("data",
					(new ObjectMapper()).writeValueAsString(aplicacioService.integracioFindDarreresAccionsByCodi(codi))
			);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "integracioList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		String codi = (String)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		List<IntegracioAccioDto> accions = null;
		if (codi != null) {
			accions = aplicacioService.integracioFindDarreresAccionsByCodi(codi);
		} else {
			accions = new ArrayList<IntegracioAccioDto>();
		}
		
		DatatablesResponse dtr = null;
		PaginacioParamsDto paginacio = DatatablesHelper.getPaginacioDtoFromRequest(request);
		if (paginacio.getPaginaTamany() < accions.size()) {
			int inici = paginacio.getPaginaNum() * paginacio.getPaginaTamany();
			PaginaDto<IntegracioAccioDto> dto = new PaginaDto<IntegracioAccioDto>();
			dto.setNumero(paginacio.getPaginaNum());
			dto.setTamany(paginacio.getPaginaTamany());
			dto.setTotal((int)Math.ceil(accions.size() / paginacio.getPaginaTamany()));
			dto.setElementsTotal(accions.size());
			dto.setAnteriors(false);
			dto.setPrimera(true);
			dto.setPosteriors(false);
			dto.setDarrera(true);
			accions = accions.subList(inici, inici + paginacio.getPaginaTamany());
			dto.setContingut(accions);
			dtr = DatatablesHelper.getDatatableResponse(request, dto);
		} else {
			dtr = DatatablesHelper.getDatatableResponse(
				request,
				accions);
		}
		return dtr;
	}

	@RequestMapping(value = "/{codi}/{index}", method = RequestMethod.GET)
	public String detall(
			HttpServletRequest request,
			@PathVariable String codi,
			@PathVariable int index,
			Model model) {
		List<IntegracioAccioDto> accions = aplicacioService.integracioFindDarreresAccionsByCodi(codi);
		if (index < accions.size()) {
			model.addAttribute(
					"integracio",
					accions.get(index));
		}
		model.addAttribute("codiActual", codi);
		return "integracioDetall";
	}

}
