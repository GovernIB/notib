/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.helper.AjaxHelper;
import es.caib.notib.back.helper.ModalHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * Controlador amb utilitats per a l'aplicació EMISERV.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class IndexController {

	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String root(HttpServletRequest request) {
		var rolActual = RolHelper.getRolActual(request, aplicacioService);

		if (RolHelper.ROLE_SUPER.equals(rolActual)) {
			return "redirect:/integracio";
		}
		if (RolHelper.ROLE_APLICACIO.equals(rolActual)) {
			return "redirect:/api/rest";
		}

		return "redirect:/notificacio";
	}

//	@PostConstruct
//	public void propagateDbProperties() {
//		aplicacioService.propagateDbProperties();
//	}
}
