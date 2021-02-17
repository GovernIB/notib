/**
 * 
 */
package es.caib.notib.war.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import es.caib.notib.war.helper.FlushAuthCacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.core.api.dto.IdiomaEnumDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.war.command.UsuariCommand;
import es.caib.notib.war.helper.EnumHelper;
import es.caib.notib.war.helper.SessioHelper;

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


	@RequestMapping(value = "/configuracio", method = RequestMethod.GET)
	public String getConfiguracio(
			HttpServletRequest request,
			Model model) {
		UsuariDto usuari = aplicacioService.getUsuariActual();
		model.addAttribute(UsuariCommand.asCommand(usuari));
		model.addAttribute(
				"idiomaEnumOptions",
				EnumHelper.getOptionsForEnum(
						IdiomaEnumDto.class,
						"usuari.form.camp.idioma.enum."));
		return "usuariForm";
	}
	@RequestMapping(value = "/configuracio", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid UsuariCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "usuariForm";
		}
		UsuariDto usuari = aplicacioService.updateUsuariActual(UsuariCommand.asDto(command));
		SessioHelper.setUsuariActual(request, usuari);
		
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/",
					"usuari.controller.modificat.ok");
	}
	@RequestMapping(value = "/configuracio/idioma", method = RequestMethod.GET)
	@ResponseBody
	public String getIdioma() {
		return new Locale(SessioHelper.getIdioma(aplicacioService), Locale.getDefault().getCountry()).toLanguageTag();
	}

	@RequestMapping(value = "/{codi}/refrescarRols", method = RequestMethod.GET)
	public String refrescarRols(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		try {
			FlushAuthCacheHelper.flushAuthenticationCache(codi);
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(request, "redirect:/", "usuari.controller.refresh.roles.error");
		}
		return getAjaxControllerReturnValueSuccess(request, "redirect:/", "usuari.controller.refresh.roles.ok");
	}
	
}
