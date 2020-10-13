/**
 * 
 */
package es.caib.notib.war.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.caib.notib.core.api.dto.CodiValorDescDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.LlibreDto;
import es.caib.notib.core.api.dto.OficinaDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.war.command.EntitatCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.war.helper.EntitatHelper;
import es.caib.notib.war.helper.MessageHelper;
import es.caib.notib.war.helper.RolHelper;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/entitat")
public class EntitatController extends BaseController {
		
	@Autowired
	private EntitatService entitatService;

	@RequestMapping(method = RequestMethod.GET)
	public String get( 
			HttpServletRequest request,
			Model model) {
		return "entitatList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( HttpServletRequest request ) {
		if (RolHelper.isUsuariActualAdministrador(request)) {
			return DatatablesHelper.getDatatableResponse(
					request,
					entitatService.findAllPaginat(
							DatatablesHelper.getPaginacioDtoFromRequest(request)));
		} else if (RolHelper.isUsuariActualAdministradorEntitat(request)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request);
			return DatatablesHelper.getDatatableResponse(
					request,
					Arrays.asList(entitat));
		}
		return null;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request,
						 Model model) {
		if (!RolHelper.isUsuariActualAdministrador(request)) {
			return "entitatList";
		}
		return get(request, null, model);
	}
	@RequestMapping(value = "/{entitatId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) {
		EntitatDto entitat = null;
		if (entitatId != null) {
			entitat = entitatService.findById(entitatId);
		}
		if (entitat != null) {
		
			EntitatCommand command = EntitatCommand.asCommand( entitat );
			model.addAttribute("tipusDocumentDefault", command.getTipusDocDefault());
			model.addAttribute("oficinaSelected", command.getOficina());
			model.addAttribute( command );
		} else {
			model.addAttribute(new EntitatCommand());
		}
		model.addAttribute("TipusDocumentEnumDto", TipusDocumentEnumDto.class);
		return "entitatForm";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid EntitatCommand command,
			BindingResult bindingResult,
			Model model) throws NotFoundException, IOException {
		if (bindingResult.hasErrors()) {
			model.addAttribute("errors", bindingResult.getAllErrors());
			return "entitatForm";
		}
		if (command.getId() != null) {
			entitatService.update(EntitatCommand.asDto(command));
			boolean isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:entitat" + (isAdminEntitat ? "/" + command.getId() : ""),
					"entitat.controller.modificada.ok");
		} else {
			entitatService.create(EntitatCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:entitat",
					"entitat.controller.creada.ok");
		}
	}

	@RequestMapping(value = "/{entitatId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		entitatService.updateActiva(entitatId, true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"entitat.controller.activada.ok");
	}
	@RequestMapping(value = "/{entitatId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		entitatService.updateActiva(entitatId, false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"entitat.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{entitatId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		entitatService.delete(entitatId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"entitat.controller.esborrada.ok");
	}
	
	@RequestMapping(value = "/getEntitatLogoCap", method = RequestMethod.GET)
	public String getEntitatLogoCap(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		
		if (entitatActual != null) {
			if (entitatActual.getLogoCapBytes() != null) {
				writeFileToResponse(
						"Logo_cap.png",
						entitatActual.getLogoCapBytes(),
						response);
			} else {
				try {
					writeFileToResponse(
							"Logo_cap.png", 
							entitatService.getCapLogo(), 
							response);
				} catch (Exception ex) {
					logger.debug("Error al obtenir el logo de la capçalera", ex);
				}
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/getEntitatLogoPeu", method = RequestMethod.GET)
	public String getEntitatLogoPeu(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		
		if (entitatActual != null) {
			if (entitatActual.getLogoPeuBytes() != null) {
				writeFileToResponse(
						"Logo_peu.png",
						entitatActual.getLogoPeuBytes(),
						response);
			} else {
				try {
					writeFileToResponse(
							"Logo_peu.png", 
							entitatService.getPeuLogo(), 
							response);
				} catch (Exception ex) {
					logger.debug("Error al obtenir el logo del peu", ex);
				}
			}
		}
		return null;
	}
	
	@RequestMapping(value = "/{entitatId}/tipusDocument", method = RequestMethod.GET)
	@ResponseBody
	public CodiValorDescDto[] getTipusDocument(
			@PathVariable Long entitatId,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		CodiValorDescDto[] tipusDoc = null;
		List<TipusDocumentDto> tipusDocuments = entitatService.findTipusDocumentByEntitat(entitatId);
		if (tipusDocuments != null && !tipusDocuments.isEmpty()) {
			tipusDoc = new CodiValorDescDto[tipusDocuments.size()];
			for (int i = 0; i < tipusDocuments.size(); i++) {
				tipusDoc[i] = new CodiValorDescDto(String.valueOf(i),tipusDocuments.get(i).getTipusDocEnum().name(),MessageHelper.getInstance().getMessage("tipus.document.enum." + tipusDocuments.get(i).getTipusDocEnum().name(),null,getLocale(request)));
			}
		}
		return tipusDoc;
	}
	
	@RequestMapping(value = "/oficines/{dir3codi}", method = RequestMethod.GET)
	@ResponseBody
	private List<OficinaDto> getOficines(
		HttpServletRequest request,
		Model model,
		@PathVariable String dir3codi) {
		return entitatService.findOficinesEntitat(dir3codi);
	}
	
	@RequestMapping(value = "/llibre/{dir3codi}", method = RequestMethod.GET)
	@ResponseBody
	private LlibreDto getLlibreEntitat(
		HttpServletRequest request,
		Model model,
		@PathVariable String dir3codi) {
		return entitatService.getLlibreEntitat(dir3codi);
	}
	
	@RequestMapping(value = "/localerequest", method = RequestMethod.GET)
	@ResponseBody
	private Locale getLocale(
		HttpServletRequest request) {
		LocaleResolver localeResolver = RequestContextUtils
				.getLocaleResolver(request);
		Locale locale;
		if (localeResolver != null) {
			locale = localeResolver.resolveLocale(request);
		} else {
			locale = request.getLocale();
		}
		return locale;
	}

	private static final Logger logger = LoggerFactory.getLogger(EntitatController.class);
}
