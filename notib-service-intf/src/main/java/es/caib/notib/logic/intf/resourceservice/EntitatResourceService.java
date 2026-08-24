package es.caib.notib.logic.intf.resourceservice;

import es.caib.notib.logic.intf.base.service.MutableResourceService;
import es.caib.notib.logic.intf.model.EntitatResource;

/**
 * Definició del servei de gestió d'entitats.
 *
 * @author Límit Tecnologies
 */
public interface EntitatResourceService extends MutableResourceService<EntitatResource, Long> {

	boolean validarCodiNoRepetit(Long id, String codi);

	boolean validarCodiDir3NoRepetit(Long id, String codi);
}
