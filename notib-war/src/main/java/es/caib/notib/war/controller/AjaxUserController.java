/**
 * 
 */
package es.caib.notib.war.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.UsuariAplicacioService;
import es.caib.notib.war.helper.EnumHelper.HtmlOption;

/**
 * Controlador per a les consultes ajax dels usuaris normals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/userajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxUserController extends BaseUserController {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private UsuariAplicacioService usuariAplicacioService;
	
	@RequestMapping(value = "/usuariDades/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto getByCodiPluginDadesUsuari(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		try {
			return aplicacioService.findUsuariAmbCodi(codi);
		} catch (Exception ex) {
			logger.error("Error al consultar la informació de l'usuari " + codi, ex);
			return null;
		}
	}

	@RequestMapping(value = "/usuarisDades/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UsuariDto> getPluginDadesUsuari(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Set<UsuariDto> setUsuaris = new HashSet<UsuariDto>();
		try {
			List<UsuariDto> usuarisWeb = aplicacioService.findUsuariAmbText(text);
			setUsuaris.addAll(usuarisWeb);
			
			AplicacioDto aplicacio = usuariAplicacioService.findByEntitatAndText(entitatActual.getId(), text);
			if (aplicacio != null) {
				UsuariDto usuariAplciacio = new UsuariDto();
				usuariAplciacio.setCodi(aplicacio.getUsuariCodi());
				usuariAplciacio.setNom(aplicacio.getUsuariCodi());
				setUsuaris.add(usuariAplciacio);
			}
			return new ArrayList<UsuariDto>(setUsuaris);
		} catch (Exception ex) {
			logger.error("Error al consultar la informació dels usuaris amb el filtre \"" + text + "\"", ex);
			return new ArrayList<UsuariDto>();
		}
	}
	
	@RequestMapping(value = "/enum/{enumClass}", method = RequestMethod.GET)
	@ResponseBody
	public List<HtmlOption> enumValorsAmbText(
			HttpServletRequest request,
			@PathVariable String enumClass) throws ClassNotFoundException {
		Class<?> enumeracio = Class.forName("es.caib.notib.core.api.dto." + enumClass);
		StringBuilder textKeyPrefix = new StringBuilder();
		String[] textKeys = StringUtils.splitByCharacterTypeCamelCase(enumClass);
		for (String textKey: textKeys) {
			if (!"dto".equalsIgnoreCase(textKey)) {
				textKeyPrefix.append(textKey.toLowerCase());
				textKeyPrefix.append(".");
			}
		}
		List<HtmlOption> resposta = new ArrayList<HtmlOption>();
		if (enumeracio.isEnum()) {
			for (Object e: enumeracio.getEnumConstants()) {
				resposta.add(new HtmlOption( 
						((Enum<?>)e).name(),
						getMessage(
								request,
								textKeyPrefix.toString() + ((Enum<?>)e).name(),
								null)));
			}
		}
		return resposta;
	}

	private static final Logger logger = LoggerFactory.getLogger(AjaxUserController.class);
}
