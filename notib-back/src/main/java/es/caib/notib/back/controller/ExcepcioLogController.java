/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador per a la consulta del log d'excepcions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/excepcio")
public class ExcepcioLogController extends BaseUserController {

	@Autowired
	private AplicacioService aplicacioService;

	@GetMapping
	public String get(HttpServletRequest request, Model model) {
		return "excepcioList";
	}

	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request) {
		return DatatablesHelper.getDatatableResponse(request, aplicacioService.excepcioFindAll());
	}

	@GetMapping(value = "/{index}")
	public String detall(HttpServletRequest request, @PathVariable Long index, Model model) {

		model.addAttribute("excepcio", aplicacioService.excepcioFindOne(index));
		return "excepcioDetall";
	}
}
