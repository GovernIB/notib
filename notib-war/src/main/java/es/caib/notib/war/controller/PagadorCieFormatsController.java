package es.caib.notib.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.PagadorCieFiltreCommand;
import es.caib.notib.war.helper.RequestSessionHelper;

/**
 * Controlador per el mantinemnt de pagadors cie.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/pagadorCie")
public class PagadorCieFormatsController extends BaseUserController{
	
	private final static String PAGADOR_CIE_FILTRE = "pagadorcie_filtre";
	
	@Autowired
	ProcedimentService procedimentService;
	@Autowired
	EntitatService entitatService;
	@Autowired
	PagadorCieService pagadorCieService;
	@Autowired
	GrupService grupsService;
	
	@RequestMapping(value = "formats/fulla", method = RequestMethod.GET)
	public String getFormatsFulla(
			HttpServletRequest request,
			Model model) {
		return "pagadorCieFullaAdminList";
	}
	
	@RequestMapping(value = "formats/sobre", method = RequestMethod.GET)
	public String getFormatsSobre(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(new PagadorCieFiltreCommand());
		//PagadorCieFiltreCommand pagadorCieFiltreCommand = getFiltreCommand(request);
		//model.addAttribute("pagadorCieFiltreCommand", pagadorCieFiltreCommand);
		return "pagadorCieAdminList";
	}
	
}
