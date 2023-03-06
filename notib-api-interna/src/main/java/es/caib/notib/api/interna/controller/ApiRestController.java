package es.caib.notib.api.interna.controller;

import es.caib.notib.client.domini.AppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Controlador que exposa la documentació de la API REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller("ApiInternaController")
@RequestMapping("/api")
public class ApiRestController {

	@Autowired
	private ServletContext servletContext;

	@RequestMapping(value = {"/apidoc", "/rest" }, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		return "restDoc";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = {"/rest/appinfo"}, method = RequestMethod.GET)
	@ResponseBody
	public AppInfo getAppInfo(HttpServletRequest request) throws IOException {
		AppInfo appInfo = new AppInfo();
		appInfo.setNom("Notib");

		Manifest manifest = new Manifest(servletContext.getResourceAsStream("/" + JarFile.MANIFEST_NAME));
		Attributes manifestAtributs = manifest.getMainAttributes();
		Map<String, Object> manifestAtributsMap = new HashMap<String, Object>();
		for (Object key: new HashMap(manifestAtributs).keySet()) {
			manifestAtributsMap.put(key.toString(), manifestAtributs.get(key));
		}
		if (!manifestAtributsMap.isEmpty()) {
			Object version = manifestAtributsMap.get("Implementation-Version");
			Object data = manifestAtributsMap.get("Release-Date");
			appInfo.setVersio(version != null ? version.toString() : null);
			appInfo.setData(data != null ? data.toString() : null);
		}
		return appInfo;
	}

}