package es.caib.notib.logic.intf.resourceservice;

import es.caib.notib.logic.intf.base.service.ReadonlyResourceService;
import es.caib.notib.logic.intf.model.ActiveMqDetailResource;
import es.caib.notib.logic.intf.model.ActiveMqResource;

/**
 * Definició del servei de consulta dels missatges d'una cua de l'ActiveMQ
 *
 * @author Límit Tecnologies
 */
public interface ActiveMqDetailResourceService extends ReadonlyResourceService<ActiveMqDetailResource, String> {
}
