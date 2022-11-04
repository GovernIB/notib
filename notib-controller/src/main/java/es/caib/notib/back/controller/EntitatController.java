/**
 * 
 */
package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.command.ConfigCommand;
import es.caib.notib.back.command.EntitatCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.EntitatHelper;
import es.caib.notib.back.helper.MessageHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.CodiValorDescDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.TipusDocumentDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.dto.config.ConfigGroupDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.ConfigService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
	private PagadorCieService cieService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ConfigService configService;

	@RequestMapping(method = RequestMethod.GET)
	public String get( HttpServletRequest request, Model model) {
		return "entitatList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( HttpServletRequest request ) {

		if (RolHelper.isUsuariActualAdministrador(request)) {
			return DatatablesHelper.getDatatableResponse(request, entitatService.findAllPaginat(DatatablesHelper.getPaginacioDtoFromRequest(request)));
		}
		if (RolHelper.isUsuariActualAdministradorEntitat(request)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request);
			return DatatablesHelper.getDatatableResponse(request, Arrays.asList(entitat));
		}
		return null;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(HttpServletRequest request, Model model) {

		if (!RolHelper.isUsuariActualAdministrador(request)) {
			return "entitatList";
		}
		return get(request, null, model);
	}

	@RequestMapping(value = "/{entitatId}", method = RequestMethod.GET)
	public String get(HttpServletRequest request, @PathVariable Long entitatId, Model model) {

		EntitatDto entitat = null;
		if (entitatId != null) {
			entitat = entitatService.findById(entitatId);
		}
		var modificant = false;
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
		var cieList = entitat != null ? cieService.findAllIdentificadorText() : cieService.findAllIdentificadorText();
		model.addAttribute("cieList", cieList);
		return "entitatForm";
	}

	@RequestMapping(value = "/{entitatId}/configurar", method = RequestMethod.GET)
	public String configEntitat(HttpServletRequest request, @PathVariable Long entitatId, Model model) {

		List<ConfigGroupDto> configGroups = configService.findAll();
		model.addAttribute("config_groups", configGroups);
		EntitatDto entitat = entitatService.findById(entitatId);
		model.addAttribute("entitatNom", entitat.getNom());
		if (entitat == null || Strings.isNullOrEmpty(entitat.getCodi())) {
			return "configEntitat";
		}
		for (ConfigGroupDto cGroup: configGroups) {
			fillFormsModel(cGroup, model, entitat.getCodi());
		}
		return "configEntitat";
	}

	private void fillFormsModel(ConfigGroupDto cGroup, Model model, String entiatCodi){

		List<ConfigDto> confs = new ArrayList<>();
		for (ConfigDto config: cGroup.getConfigs()) {
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
		for (ConfigGroupDto child : cGroup.getInnerConfigs()){
			fillFormsModel(child, model, entiatCodi);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid EntitatCommand command, BindingResult bindingResult, Model model) throws NotFoundException, IOException {

		if (bindingResult.hasErrors()) {
			List<IdentificadorTextDto> operadorPostalList = operadorPostalService.findAllIdentificadorText();
			model.addAttribute("operadorPostalList", operadorPostalList);
			List<IdentificadorTextDto> cieList = cieService.findAllIdentificadorText();
			model.addAttribute("cieList", cieList);
			model.addAttribute("errors", bindingResult.getAllErrors());
			model.addAttribute("oficinaSelected", command.getOficina());
			model.addAttribute("tipusDocSelected", getTipusDocSelected(request, command.getTipusDocName()));
			return "entitatForm";
		}
		String redirect = "redirect:entitat";
		String msg = command.getId() != null ? "entitat.controller.modificada.ok" : "entitat.controller.creada.ok";
		if (command.getId() != null) {
			EntitatDto entitatDto = entitatService.update(command.asDto());
			EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
			if (entitatDto != null && entitatActual != null && entitatDto.equals(entitatActual)) {
				EntitatHelper.actualitzarEntitatActualEnSessio(request, aplicacioService, entitatService);
			}
			boolean isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
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
		for (int foo = 0; foo < tipusDocName.length; foo++) {
			TipusDocumentEnumDto tipus = TipusDocumentEnumDto.toEnum(tipusDocName[foo]);
			if (tipus == null) {
				continue;
			}
			CodiValorDescDto valor = new CodiValorDescDto();
			valor.setCodi(tipus.getText());
			valor.setValor(tipus.name());
			valor.setDesc(MessageHelper.getInstance().getMessage("tipus.document.enum." + tipus.name(),null, getLocale(request)));
			valors.add(valor);
		}
		return valors;
	}

	@RequestMapping(value = "/{entitatId}/enable", method = RequestMethod.GET)
	public String enable(HttpServletRequest request, @PathVariable Long entitatId) {

		entitatService.updateActiva(entitatId, true);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "entitat.controller.activada.ok");
	}
	@RequestMapping(value = "/{entitatId}/disable", method = RequestMethod.GET)
	public String disable(HttpServletRequest request, @PathVariable Long entitatId) {

		entitatService.updateActiva(entitatId, false);
		return getAjaxControllerReturnValueSuccess(request,"redirect:../../entitat", "entitat.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{entitatId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long entitatId) {

		String msg = "entitat.controller.esborrada.ok";
		try {
			if (entitatService.delete(entitatId) != null) {
				return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", msg);
			}
			msg = "entitat.controller.esborrada.ko.notificacions.existents";
			return getAjaxControllerReturnValueError(request, "redirect:../../entitat", msg);
		} catch(Exception ex) {
			msg = "entitat.controller.esborrada.ko";
			return getAjaxControllerReturnValueError(request, "redirect:../../entitat", msg);
		}
	}
	
	@RequestMapping(value = "/getEntitatLogoCap", method = RequestMethod.GET)
	public String getEntitatLogoCap(HttpServletRequest request, HttpServletResponse response) throws IOException {

		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual == null) {
			writeFileToResponse("Logo_cap.png", IOUtils.toByteArray(getDefaultLogo()), response);
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
			return null;
		}
		try {
			writeFileToResponse("Logo_cap.png", IOUtils.toByteArray(getDefaultLogo()), response);
		} catch (Exception ex) {
			log.debug("Error al obtenir el logo genèric de la capçalera", ex);
		}
		return null;
	}
	
	@RequestMapping(value = "/getEntitatLogoPeu", method = RequestMethod.GET)
	public String getEntitatLogoPeu(HttpServletRequest request, HttpServletResponse response) throws IOException {

		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual == null) {
			writeFileToResponse("Logo_cap.png", IOUtils.toByteArray(getDefaultLogo()), response);
			return null;
		}
		if (entitatActual.getLogoPeuBytes() != null) {
			writeFileToResponse("Logo_peu.png", entitatActual.getLogoPeuBytes(), response);
			return null;
		}
		try {
			writeFileToResponse("Logo_peu.png", entitatService.getPeuLogo(), response);
			return null;
		} catch (Exception ex) {
			log.debug("Error al obtenir el logo del peu", ex);
		}
		try {
			writeFileToResponse("Logo_cap.png", IOUtils.toByteArray(getDefaultLogo()), response);
		} catch (Exception ex) {
			log.debug("Error al obtenir el logo genèric de la capçalera", ex);
		}
		return null;
	}
	
	@RequestMapping(value = "/{entitatId}/tipusDocument", method = RequestMethod.GET)
	@ResponseBody
	public CodiValorDescDto[] getTipusDocument(@PathVariable Long entitatId, HttpServletRequest request, HttpServletResponse response) throws IOException {

		CodiValorDescDto[] tipusDoc = null;
		List<TipusDocumentDto> tipusDocuments = entitatService.findTipusDocumentByEntitat(entitatId);
		if (tipusDocuments == null || tipusDocuments.isEmpty()) {
			return tipusDoc;
		}
		tipusDoc = new CodiValorDescDto[tipusDocuments.size()];
		for (int i = 0; i < tipusDocuments.size(); i++) {
			String msg = MessageHelper.getInstance().getMessage("tipus.document.enum." + tipusDocuments.get(i).getTipusDocEnum().name(),null,getLocale(request));
			tipusDoc[i] = new CodiValorDescDto(String.valueOf(i),tipusDocuments.get(i).getTipusDocEnum().name(), msg);
		}
		return tipusDoc;
	}
	
	@RequestMapping(value = "/oficines/{dir3codi}", method = RequestMethod.GET)
	@ResponseBody
	private List<OficinaDto> getOficines(HttpServletRequest request, Model model, @PathVariable String dir3codi) {
		return entitatService.findOficinesEntitat(dir3codi);
	}
	
	@RequestMapping(value = "/llibre/{dir3codi}", method = RequestMethod.GET)
	@ResponseBody
	private LlibreDto getLlibreEntitat(HttpServletRequest request, Model model, @PathVariable String dir3codi) {
		return entitatService.getLlibreEntitat(dir3codi);
	}

	@RequestMapping(value = "/localerequest", method = RequestMethod.GET)
	@ResponseBody
	private Locale getLocale(HttpServletRequest request) {

		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		return localeResolver != null ? localeResolver.resolveLocale(request) : request.getLocale();
	}

	@RequestMapping(value = "/organigrama/{entitatCodi}", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, OrganismeDto> getOrganigrama(@PathVariable String entitatCodi, HttpServletRequest request,HttpServletResponse response) throws IOException {
		return entitatService.findOrganigramaByEntitat(entitatCodi);
	}

	private InputStream getDefaultLogo() {
		return getClass().getResourceAsStream("/static/img/govern-logo.png");
	}
}
