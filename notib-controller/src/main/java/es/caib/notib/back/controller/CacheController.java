/**
 * 
 */
package es.caib.notib.back.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.logic.intf.service.CacheService;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per a la gestió de cache
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/cache")
public class CacheController extends BaseController {
		
	@Autowired
	private CacheService cacheService;

	@RequestMapping(method = RequestMethod.GET)
	public String get( HttpServletRequest request ) {
		return "cacheList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable( HttpServletRequest request ) {
		return DatatablesHelper.getDatatableResponse(request, cacheService.getAllCaches());
	}
	
	@RequestMapping(value = "/{cacheValue}/buidar", method = RequestMethod.GET)
	public String buidar(HttpServletRequest request, @PathVariable String cacheValue) {

		cacheService.removeCache(cacheValue);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "cache.controller.esborrada.ok");
	}

	@RequestMapping(value = "/all/buidar", method = RequestMethod.GET)
	public String buidarTot(HttpServletRequest request) {
		cacheService.removeAllCaches();
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "cache.controller.esborrada.ok");
	}
}
