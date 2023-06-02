/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.SchedulledService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

	@RequestMapping(value = "/", method = RequestMethod.GET)
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

//	public void listResources() throws IOException {
//		for (File file: getResourceFolderFiles("")) {
//			listFileDir(file, 0);
//		}
//	}
//
//	private void listFileDir(File file, int nivell) {
//		String prefix = StringUtils.leftPad("|-", nivell + 2);
//		if (file.isDirectory()) {
//			log.info(prefix + " (D) " + file.getName());
//			for(File fill: file.listFiles()) {
//				listFileDir(fill, nivell + 1);
//			}
//		} else {
//			log.info(prefix + " (F) " + file.getName() + "(" + file.getPath() + ")");
//		}
//	}
//
//	private static File[] getResourceFolderFiles (String folder) throws IOException {
//		ClassLoader loader = Thread.currentThread().getContextClassLoader();
//		URL url = loader.getResource(folder);
//		String path = url.getPath();
//
//		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//		var resources = resolver.getResources("classpath*:**/*");
//		for (var resource: resources) {
//			var uri = resource.getURI().toString();
//			if (uri.toLowerCase().contains("webjar"))
//				log.info("Recurs: {} ({})", resource.getFilename(), uri);
//		}
//		return new File(path).listFiles();
//	}

}
