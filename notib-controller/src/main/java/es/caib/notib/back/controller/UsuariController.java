/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.command.UsuariCodiCommand;
import es.caib.notib.back.command.UsuariCommand;
import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.client.domini.NumElementsPaginaDefecte;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.UsuariService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Locale;

import static es.caib.notib.back.controller.UsuariController.ResultatEstatEnum.ERROR;
import static es.caib.notib.back.controller.UsuariController.ResultatEstatEnum.OK;

/**
 * Controlador per al manteniment de regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/usuari")
public class UsuariController extends BaseController {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private SessionScopedContext sessionScopedContext;
    @Autowired
    private UsuariService usuariService;

	private static final String REDIRECT = "redirect:/";

	@RequestMapping(value = "/refresh", method = RequestMethod.HEAD)
	public void refresh(HttpServletRequest request, HttpServletResponse response) {
		// EMPTY METHOD
	}

	@GetMapping(value = "/configuracio")
	public String getConfiguracio(HttpServletRequest request, Model model) {

		var usuari = aplicacioService.getUsuariActual();
		model.addAttribute(UsuariCommand.asCommand(usuari));
		model.addAttribute("idiomaEnumOptions", EnumHelper.getOptionsForEnum(Idioma.class,"usuari.form.camp.idioma.enum."));
		model.addAttribute("numElementsPaginaDefecte", EnumHelper.getOptionsForEnum(NumElementsPaginaDefecte.class,"usuari.form.camp.elements.pagina.perdefecte.enum."));
		return "usuariForm";
	}

	@GetMapping(value = "/num/elements/pagina/defecte")
	@ResponseBody
	public Integer getNumElementsPaginaDefecte(HttpServletRequest request, Model model) {
		return aplicacioService.getNumElementsPaginaDefecte();
	}

	@PostMapping(value = "/configuracio")
	public String save(HttpServletRequest request, @Valid UsuariCommand command, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			var usuari = aplicacioService.getUsuariActual();
			var uc = UsuariCommand.asCommand(usuari);
			uc.setEmailAlt(command.getEmailAlt());
			command.setNom(uc.getNom());
			command.setEmail(uc.getEmail());
			command.setCodi(uc.getCodi());
			command.setRols(uc.getRols());
			command.setNif(uc.getNif());
			command.setNumElementsPaginaDefecte(uc.getNumElementsPaginaDefecte());
			model.addAttribute(command);
			model.addAttribute("idiomaEnumOptions", EnumHelper.getOptionsForEnum(Idioma.class,"usuari.form.camp.idioma.enum."));
			return "usuariForm";
		}
		var usuari = aplicacioService.updateUsuariActual(UsuariCommand.asDto(command));
		sessionScopedContext.setUsuariActual(usuari);
		return getModalControllerReturnValueSuccess(request, REDIRECT,"usuari.controller.modificat.ok");
	}

	@GetMapping(value = "/configuracio/idioma")
	@ResponseBody
	public String getIdioma() {
		return new Locale(sessionScopedContext.getIdiomaUsuari(), Locale.getDefault().getCountry()).toLanguageTag();
	}

	@GetMapping(value = "/sessio")
	@ResponseBody
	public String getInfoSessio(HttpServletRequest request) throws IOException {
		return RequestSessionHelper.getJsonSession(request);
	}

	@RequestMapping(value = "/username", method = RequestMethod.GET)
	public String getCanviCodi(HttpServletRequest request, Model model) {

		var usuariCodiCommand = new UsuariCodiCommand();
		model.addAttribute("usuariCodiCommand", usuariCodiCommand);
		return "usuariCodiForm";
	}

	@RequestMapping(value = "/username", method = RequestMethod.POST)
	public String setCanviCodi(HttpServletRequest request, HttpServletResponse response, @Valid UsuariCodiCommand command, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			return "usuariCodiForm";
		}
		try {
			usuariService.updateUsuariCodi(command.getCodiAntic(), command.getCodiNou());
			getModalControllerReturnValueSuccess(request, REDIRECT,"usuari.controller.codi.modificat.ok");
		} catch (Exception e) {
			getModalControllerReturnValueError(request, REDIRECT, "usuari.controller.codi.modificat.error", new Object[]{e.getMessage()});
			log.error("Error modificant el codi de l'usuari", e);
		}
		return "usuariCodiForm";
	}


	@RequestMapping(value = "/usernames/change", method = RequestMethod.GET)
	public String getCanviCodis(HttpServletRequest request, Model model) {
		return "usuarisCanviCodi";
	}

	@RequestMapping(value = "/usernames/{codiAntic}/validateTo/{codiNou}", method = RequestMethod.POST, produces = "application/json" )
	@ResponseBody
	public UsuariChangeValidation validaCanviCodis(HttpServletRequest request, @PathVariable("codiAntic") String codiAntic, @PathVariable("codiNou") String codiNou) {

		UsuariDto usuariAntic = usuariService.findByCodi(codiAntic);
		UsuariDto usuariNou = usuariService.findByCodi(codiNou);
		String codiActual = SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getName() : null;
		return UsuariChangeValidation.builder()
				.usuariActual(codiActual != null && codiActual.equals(codiAntic))
				.usuariAnticExists(usuariAntic != null).usuariNouExists(usuariNou != null).build();
	}

	@RequestMapping(value = "/usernames/{codiAntic}/changeTo/{codiNou}", method = RequestMethod.POST, produces = "application/json" )
	@ResponseBody
	public UsuariChangeResponse setCanviCodis(HttpServletRequest request, @PathVariable("codiAntic") String codiAntic, @PathVariable("codiNou") String codiNou) {

		Long t0 = System.currentTimeMillis();
		try {
			Long registresModificats = usuariService.updateUsuariCodi(codiAntic, codiNou);
			return UsuariChangeResponse.builder().estat(OK).registresModificats(registresModificats).duracio(System.currentTimeMillis() - t0).build();
		} catch (Exception e) {
			log.error("Error modificant el codi de l'usuari", e);
			var msg = getMessage(request, "usuari.controller.codi.modificat.error", null) + ": " + e.getMessage();
			return UsuariChangeResponse.builder().estat(ERROR).errorMessage(msg).duracio(System.currentTimeMillis() - t0).build();
		}
	}

	@Data
	@Builder
	public static class UsuariChangeValidation {

		private boolean usuariActual;
		private boolean usuariAnticExists;
		private boolean usuariNouExists;
	}

	@Data
	@Builder
	public static class UsuariChangeResponse {

		private ResultatEstatEnum estat;
		private String errorMessage;
		private Long registresModificats;
		private Long duracio;
	}

	public enum ResultatEstatEnum { OK, ERROR }

}
