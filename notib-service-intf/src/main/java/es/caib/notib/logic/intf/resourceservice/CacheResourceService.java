package es.caib.notib.logic.intf.resourceservice;

import es.caib.notib.logic.intf.base.service.MutableResourceService;
import es.caib.notib.logic.intf.base.service.ReadonlyResourceService;
import es.caib.notib.logic.intf.model.CacheResource;

/**
 * Definició del servei de consulta de les caches de l'aplicació
 *
 * @author Límit Tecnologies
 */
public interface CacheResourceService extends MutableResourceService<CacheResource, String> {
}
