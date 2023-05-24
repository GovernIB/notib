/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.command.UsuariCommand;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.SessioHelper;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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

	@RequestMapping(value = "/refresh", method = RequestMethod.HEAD)
	public void refresh(HttpServletRequest request, HttpServletResponse response) {
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		SecurityContextHolder.clearContext();
		// Només per Jboss
		if (session != null)
			// Esborrar la sessió
			session.invalidate();
		// Es itera sobre totes les cookies
		if (request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				// Es sobre escriu el valor de cada cookie a NULL
				Cookie ck = new Cookie(c.getName(), null);
				ck.setPath(request.getContextPath());
				response.addCookie(ck);
			}
		}
		return "redirect:/";
	}

	@RequestMapping(value = "/configuracio", method = RequestMethod.GET)
	public String getConfiguracio(HttpServletRequest request, Model model) {

		UsuariDto usuari = aplicacioService.getUsuariActual();
		model.addAttribute(UsuariCommand.asCommand(usuari));
		model.addAttribute("idiomaEnumOptions", EnumHelper.getOptionsForEnum(Idioma.class,"usuari.form.camp.idioma.enum."));
		return "usuariForm";
	}

	@RequestMapping(value = "/configuracio", method = RequestMethod.POST)
	public String save(HttpServletRequest request, @Valid UsuariCommand command, BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			UsuariDto usuari = aplicacioService.getUsuariActual();
			UsuariCommand uc = UsuariCommand.asCommand(usuari);
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
		UsuariDto usuari = aplicacioService.updateUsuariActual(UsuariCommand.asDto(command));
		SessioHelper.setUsuariActual(request, usuari);
		return getModalControllerReturnValueSuccess(request,"redirect:/","usuari.controller.modificat.ok");
	}

	@RequestMapping(value = "/configuracio/idioma", method = RequestMethod.GET)
	@ResponseBody
	public String getIdioma() {
		return new Locale(SessioHelper.getIdioma(aplicacioService), Locale.getDefault().getCountry()).toLanguageTag();
	}

}
