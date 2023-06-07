/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.service.AplicacioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * Controlador amb utilitats per a l'aplicació NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@Slf4j
public class IndexController {

	@Autowired
	private AplicacioService aplicacioService;

	@GetMapping(value = "/")
	public String root(HttpServletRequest request) {

		var rolActual = RolHelper.getRolActual(request, aplicacioService);
		return RolHelper.ROLE_SUPER.equals(rolActual) ? "redirect:/integracio" :
				RolHelper.ROLE_APLICACIO.equals(rolActual) ? "redirect:/api/rest" : "redirect:/notificacio";
	}

	@PostConstruct
	public void propagateDbProperties() {
		aplicacioService.propagateDbProperties();
		aplicacioService.restartSchedulledTasks();
	}
}
