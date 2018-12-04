package es.caib.notib.war.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.service.EntitatService;
import es.caib.notib.core.api.service.GrupService;
import es.caib.notib.core.api.service.PagadorCieService;
import es.caib.notib.core.api.service.PagadorPostalService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.war.command.PagadorPostalCommand;
import es.caib.notib.war.command.PagadorPostalFiltreCommand;
import es.caib.notib.war.command.ProcedimentCommand;
import es.caib.notib.war.command.ProcedimentFiltreCommand;
import es.caib.notib.war.helper.DatatablesHelper;
import es.caib.notib.war.helper.RequestSessionHelper;
import es.caib.notib.war.helper.RolHelper;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;

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
