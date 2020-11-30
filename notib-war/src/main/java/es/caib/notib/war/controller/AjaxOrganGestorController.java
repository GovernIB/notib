package es.caib.notib.war.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.war.helper.EntitatHelper;
import es.caib.notib.war.helper.RolHelper;



/**
 * Controlador per a les consultes ajax dels usuaris normals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/organgestorajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxOrganGestorController extends BaseUserController {

	@Autowired
	private OrganGestorService organGestorService;

	@RequestMapping(value = "/organgestor", method = RequestMethod.GET)
	@ResponseBody
	public List<OrganGestorDto> get(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/organgestor/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<OrganGestorDto> get(HttpServletRequest request, @PathVariable String text, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		try {
			text = URLDecoder.decode(request.getRequestURI().split("/")[4], StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) { }
		
//		List<OrganGestorDto> organGestorsList;
//		organGestorsList = organGestorService.findByEntitat(
//				entitatActual.getId(),
//				text);
//		
//		if (text == null) {
//			return organGestorsList.subList(0, 5);
//		}
//
//		return organGestorsList;
		
		return null;
	}
		
	@RequestMapping(value = "/organgestor/item/{id}", method = RequestMethod.GET)
	@ResponseBody
	public OrganGestorDto getItem(HttpServletRequest request, @PathVariable Long id, Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			return organGestorService.findById(entitatActual.getId(), id);
		} catch (NotFoundException e) {
			return null;
		} 
	}
}
