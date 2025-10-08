/**
 * 
 */
package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.command.ConfigCommand;
import es.caib.notib.back.command.EntitatCommand;
import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.dto.config.ConfigGroupDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/entitat")
public class EntitatController extends BaseController {
		
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private OperadorPostalService operadorPostalService;
	@Autowired
	private PagadorCieService pagadorCieService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private SessionScopedContext sessionScopedContext;

	private static final String CONFIG_ENTITAT = "configEntitat";
	private static final String REDIRECT = "redirect:../../entitat";


	@GetMapping
	public String get( HttpServletRequest request, Model model) {
		return "entitatList";
	}

	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request ) {

		if (RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual())) {
			return DatatablesHelper.getDatatableResponse(request, entitatService.findAllPaginat(DatatablesHelper.getPaginacioDtoFromRequest(request)));
		}
		if (RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual())) {
			EntitatDto entitat = sessionScopedContext.getEntitatActual();
			return DatatablesHelper.getDatatableResponse(request, Arrays.asList(entitat));
		}
		return null;
	}

	@GetMapping(value = "/new")
	public String getNew(HttpServletRequest request, Model model) {
		return !RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual()) ? "entitatList" : get(request, null, model);
	}

	@GetMapping(value = "/{entitatId}")
	public String get(HttpServletRequest request, @PathVariable Long entitatId, Model model) {

		EntitatDto entitat = null;
		if (entitatId != null) {
			entitat = entitatService.findById(entitatId);
		}
		boolean modificant = false;
		EntitatCommand command = null;
		if (entitat != null) {
			command = EntitatCommand.asCommand( entitat );
			model.addAttribute("tipusDocumentDefault", command.getTipusDocDefault());
			model.addAttribute("oficinaSelected", command.getOficina());
			model.addAttribute( command );
			modificant = true;
		}
		model.addAttribute(command != null ? command : new EntitatCommand());
		model.addAttribute("modificant", modificant);
		model.addAttribute("TipusDocumentEnumDto", TipusDocumentEnumDto.class);
		var operadorPostalList = entitat != null ? operadorPostalService.findPagadorsByEntitat(entitat) : operadorPostalService.findAllIdentificadorText();
		model.addAttribute("operadorPostalList", operadorPostalList);
		model.addAttribute("entitatNova", entitat == null || entitat.getId() == null);
		var cieList = entitat != null ? pagadorCieService.findPagadorsByEntitat(entitat) : pagadorCieService.findAllIdentificadorText();
		model.addAttribute("cieList", cieList);
		return "entitatForm";
	}

	@GetMapping(value = "/{entitatId}/configurar")
	public String configEntitat(HttpServletRequest request, @PathVariable Long entitatId, Model model) {

		var configGroups = configService.findAll();
		model.addAttribute("config_groups", configGroups);
		var entitat = entitatService.findById(entitatId);
		model.addAttribute("entitatNom", entitat.getNom());
		if (entitat == null || Strings.isNullOrEmpty(entitat.getCodi())) {
			return CONFIG_ENTITAT;
		}
		for (var cGroup: configGroups) {
			fillFormsModel(cGroup, model, entitat.getCodi());
		}
		return CONFIG_ENTITAT;
	}

	@GetMapping(value = "/{entitatId}/reset/actualitzacio/organs")
	public String resetActualitzacioOrgans(HttpServletRequest request, @PathVariable Long entitatId, Model model) {

		entitatService.resetActualitzacioOrgans(entitatId);
		return CONFIG_ENTITAT;
	}

	private void fillFormsModel(ConfigGroupDto cGroup, Model model, String entiatCodi){

		List<ConfigDto> confs = new ArrayList<>();
		for (var config: cGroup.getConfigs()) {
			if (Strings.isNullOrEmpty(config.getEntitatCodi()) || !config.getEntitatCodi().equals(entiatCodi)) {
				continue;
			}
			model.addAttribute("config_" + config.getKey().replace('.', '_'), ConfigCommand.builder().key(config.getKey()).value(config.getValue()).build());
			confs.add(config);
		}
		cGroup.setConfigs(confs);
		if (cGroup.getInnerConfigs() == null || cGroup.getInnerConfigs().isEmpty()){
			return;
		}
		for (var child : cGroup.getInnerConfigs()){
			fillFormsModel(child, model, entiatCodi);
		}
	}

	@PostMapping
	public String save(HttpServletRequest request, @Valid EntitatCommand command, BindingResult bindingResult, Model model) throws NotFoundException, IOException {

		if (bindingResult.hasErrors()) {
			var operadorPostalList = operadorPostalService.findAllIdentificadorText();
			model.addAttribute("operadorPostalList", operadorPostalList);
			var cieList = pagadorCieService.findAllIdentificadorText();
			model.addAttribute("cieList", cieList);
			model.addAttribute("errors", bindingResult.getAllErrors());
			model.addAttribute("oficinaSelected", command.getOficina());
			model.addAttribute("tipusDocSelected", getTipusDocSelected(request, command.getTipusDocName()));
			return "entitatForm";
		}
		var redirect = "redirect:entitat";
		var msg = command.getId() != null ? "entitat.controller.modificada.ok" : "entitat.controller.creada.ok";
		if (command.getId() != null) {
			var entitatDto = entitatService.update(command.asDto());
			var entitatActualId = sessionScopedContext.getEntitatActualId();
			if (entitatDto != null && entitatActualId != null && entitatDto.getId().equals(entitatActualId)) {
				sessionScopedContext.setEntitatActual(entitatDto);
			}
			var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
			return getModalControllerReturnValueSuccess(request,redirect + (isAdminEntitat ? "/" + command.getId() : ""), msg);
		}
		entitatService.create(command.asDto());
		return getModalControllerReturnValueSuccess(request, redirect, msg);
	}

	public List<CodiValorDescDto> getTipusDocSelected(HttpServletRequest request, String[] tipusDocName) {

		if (tipusDocName == null || tipusDocName.length == 0) {
			return null;
		}
		List<CodiValorDescDto> valors = new ArrayList<>();
		TipusDocumentEnumDto tipus;
		CodiValorDescDto valor;
		for (String s : tipusDocName) {
			tipus = TipusDocumentEnumDto.toEnum(s);
			if (tipus == null) {
				continue;
			}
			valor = new CodiValorDescDto();
			valor.setCodi(tipus.getText());
			valor.setValor(tipus.name());
			valor.setDesc(MessageHelper.getInstance().getMessage("tipus.document.enum." + tipus.name(), null, getLocale(request)));
			valors.add(valor);
		}
		return valors;
	}

	@GetMapping(value = "/{entitatId}/enable")
	public String enable(HttpServletRequest request, @PathVariable Long entitatId) {

		entitatService.updateActiva(entitatId, true);
		return getAjaxControllerReturnValueSuccess(request, REDIRECT, "entitat.controller.activada.ok");
	}

	@GetMapping(value = "/{entitatId}/disable")
	public String disable(HttpServletRequest request, @PathVariable Long entitatId) {

		entitatService.updateActiva(entitatId, false);
		return getAjaxControllerReturnValueSuccess(request,REDIRECT, "entitat.controller.desactivada.ok");
	}

	@GetMapping(value = "/{entitatId}/delete")
	public String delete(HttpServletRequest request, @PathVariable Long entitatId) {

		var msg = "entitat.controller.esborrada.ok";
		try {
			if (entitatService.delete(entitatId) != null) {
				return getAjaxControllerReturnValueSuccess(request, REDIRECT, msg);
			}
			msg = "entitat.controller.esborrada.ko.notificacions.existents";
			return getAjaxControllerReturnValueError(request, REDIRECT, msg);
		} catch(Exception ex) {
			msg = "entitat.controller.esborrada.ko";
			return getAjaxControllerReturnValueError(request, REDIRECT, msg);
		}
	}
	
	@GetMapping(value = "/getEntitatLogoCap")
	public String getEntitatLogoCap(HttpServletRequest request, HttpServletResponse response) throws IOException {

		var entitatActual = sessionScopedContext.getEntitatActual();
		if (entitatActual == null) {
			return null;
		}
		if (entitatActual.getLogoCapBytes() != null) {
			writeFileToResponse("Logo_cap.png", entitatActual.getLogoCapBytes(), response);
			return null;
		}
		try {
			writeFileToResponse("Logo_cap.png", entitatService.getCapLogo(), response);
		} catch (Exception ex) {
			log.debug("Error al obtenir el logo de la capçalera", ex);
		}
		return null;
	}
	
	@GetMapping(value = "/getEntitatLogoPeu")
	public String getEntitatLogoPeu(HttpServletRequest request, HttpServletResponse response) throws IOException {

		var entitatActual = sessionScopedContext.getEntitatActual();
		if (entitatActual == null) {
			return null;
		}
		if (entitatActual.getLogoPeuBytes() != null) {
			writeFileToResponse("Logo_peu.png", entitatActual.getLogoPeuBytes(), response);
			return null;
		}
		try {
			writeFileToResponse("Logo_peu.png", entitatService.getPeuLogo(), response);
		} catch (Exception ex) {
			log.debug("Error al obtenir el logo del peu", ex);
		}
		return null;
	}
	
	@GetMapping(value = "/{entitatId}/tipusDocument")
	@ResponseBody
	public CodiValorDescDto[] getTipusDocument(@PathVariable Long entitatId, HttpServletRequest request, HttpServletResponse response) {

		CodiValorDescDto[] tipusDoc = null;
		var tipusDocuments = entitatService.findTipusDocumentByEntitat(entitatId);
		if (tipusDocuments == null || tipusDocuments.isEmpty()) {
			return tipusDoc;
		}
		tipusDoc = new CodiValorDescDto[tipusDocuments.size()];
		String msg;
		for (var i = 0; i < tipusDocuments.size(); i++) {
			msg = MessageHelper.getInstance().getMessage("tipus.document.enum." + tipusDocuments.get(i).getTipusDocEnum().name(),null,getLocale(request));
			tipusDoc[i] = new CodiValorDescDto(String.valueOf(i),tipusDocuments.get(i).getTipusDocEnum().name(), msg);
		}
		return tipusDoc;
	}
	
	@GetMapping(value = "/oficines/{dir3codi}")
	@ResponseBody
	public List<OficinaDto> getOficines(HttpServletRequest request, Model model, @PathVariable String dir3codi) {
		return entitatService.findOficinesEntitat(dir3codi);
	}
	
	@GetMapping(value = "/llibre/{dir3codi}")
	@ResponseBody
	public LlibreDto getLlibreEntitat(HttpServletRequest request, Model model, @PathVariable String dir3codi) {
		return entitatService.getLlibreEntitat(dir3codi);
	}

	@GetMapping(value = "/localerequest")
	@ResponseBody
	public Locale getLocale(HttpServletRequest request) {

		var localeResolver = RequestContextUtils.getLocaleResolver(request);
		return localeResolver != null ? localeResolver.resolveLocale(request) : request.getLocale();
	}

	@GetMapping(value = "/organigrama/{entitatCodi}")
	@ResponseBody
	public Map<String, OrganismeDto> getOrganigrama(@PathVariable String entitatCodi, HttpServletRequest request, HttpServletResponse response) {
		return entitatService.findOrganigramaByEntitat(entitatCodi);
	}
}
