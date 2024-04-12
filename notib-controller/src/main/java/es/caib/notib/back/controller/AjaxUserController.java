/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.helper.EnumHelper.HtmlOption;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.UsuariAplicacioService;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Controlador per a les consultes ajax dels usuaris normals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/userajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxUserController extends BaseUserController {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private UsuariAplicacioService usuariAplicacioService;


	@GetMapping(value = "/usuariDades/{codi}")
	@ResponseBody
	public UsuariDto getByCodiPluginDadesUsuari(HttpServletRequest request, @PathVariable String codi, Model model) {

		try {
			return aplicacioService.findUsuariAmbCodi(codi);
		} catch (Exception ex) {
			log.error("Error al consultar la informació de l'usuari " + codi, ex);
			return null;
		}
	}

	@GetMapping(value = "/usuarisDades/{text}")
	@ResponseBody
	public List<UsuariDto> getPluginDadesUsuari(HttpServletRequest request, @PathVariable String text, Model model) {

		var entitatActual = getEntitatActualComprovantPermisos(request);
		Set<UsuariDto> setUsuaris = new HashSet<>();
		try {
			var encoded = new String(text.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
			var usuarisWeb = aplicacioService.findUsuariAmbText(encoded);
			setUsuaris.addAll(usuarisWeb);
			var aplicacio = usuariAplicacioService.findByEntitatAndText(entitatActual.getId(), encoded);
			if (aplicacio == null) {
				return new ArrayList<>(setUsuaris);
			}

			UsuariDto usuariAplciacio;
			for (var app : aplicacio) {
				usuariAplciacio = new UsuariDto();
				usuariAplciacio.setCodi(app.getUsuariCodi());
				usuariAplciacio.setNom(app.getUsuariCodi());
				setUsuaris.add(usuariAplciacio);
			}
			return new ArrayList<>(setUsuaris);
		} catch (Exception ex) {
			log.error("Error al consultar la informació dels usuaris amb el filtre \"" + text + "\"", ex);
			return new ArrayList<>();
		}
	}
	
	@GetMapping(value = "/enum/{enumClass}")
	@ResponseBody
	public List<HtmlOption> enumValorsAmbText(HttpServletRequest request, @PathVariable String enumClass) throws ClassNotFoundException {

		var enumeracio = Class.forName("es.caib.notib.logic.intf.dto." + enumClass);
		var textKeyPrefix = new StringBuilder();
		var textKeys = StringUtils.splitByCharacterTypeCamelCase(enumClass);
		for (var textKey: textKeys) {
			if (!"dto".equalsIgnoreCase(textKey)) {
				textKeyPrefix.append(textKey.toLowerCase());
				textKeyPrefix.append(".");
			}
		}
		if (!enumeracio.isEnum()) {
			return new ArrayList<>();
		}
		List<HtmlOption> resposta = new ArrayList<>();
		for (var e: enumeracio.getEnumConstants()) {
				resposta.add(new HtmlOption(((Enum<?>)e).name(), getMessage(request, textKeyPrefix.toString() + ((Enum<?>)e).name(), null)));
		}
		return resposta;
	}

}
