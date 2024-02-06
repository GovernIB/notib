/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.command.UsuariCommand;
import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Locale;

/**
 * Controlador per al manteniment de regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/usuari")
public class UsuariController extends BaseController {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private SessionScopedContext sessionScopedContext;

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
		return "usuariForm";
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

}
