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
import es.caib.notib.core.api.service.PagadorPostalService;
import es.caib.notib.core.api.service.ProcedimentService;

/**
 * Controlador per el mantinemnt de permisos de procediments
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
@Controller
@RequestMapping("/procediment")
public class ProcedimentPermisController extends BaseUserController{
	
	private final static String PROCEDIMENTS_FILTRE = "procediments_filtre";
	
	@Autowired
	ProcedimentService procedimentService;
	@Autowired
	EntitatService entitatService;
	@Autowired
	PagadorPostalService pagadorPostalService;
	@Autowired
	PagadorCieService pagadorCieService;
	@Autowired
	GrupService grupsService;
	
	@RequestMapping(value = "/{procedimentId}/permis", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		/*model.addAttribute(new ProcedimentFiltreCommand());
		ProcedimentFiltreCommand procedimentFiltreCommand = getFiltreCommand(request);
		model.addAttribute("procedimentFiltreCommand", procedimentFiltreCommand);*/
		return "procedimentAdminPermis";
	}
	
}
