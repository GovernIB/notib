/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.logic.intf.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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


	@GetMapping
	public String get( HttpServletRequest request ) {
		return "cacheList";
	}

	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesResponse datatable(HttpServletRequest request ) {
		return DatatablesHelper.getDatatableResponse(request, cacheService.getAllCaches());
	}
	
	@GetMapping(value = "/{cacheValue}/buidar")
	public String buidar(HttpServletRequest request, @PathVariable String cacheValue) {

		cacheService.removeCache(cacheValue);
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "cache.controller.esborrada.ok");
	}

	@GetMapping(value = "/all/buidar")
	public String buidarTot(HttpServletRequest request) {

		cacheService.removeAllCaches();
		return getAjaxControllerReturnValueSuccess(request, "redirect:../../entitat", "cache.controller.esborrada.ok");
	}
}
