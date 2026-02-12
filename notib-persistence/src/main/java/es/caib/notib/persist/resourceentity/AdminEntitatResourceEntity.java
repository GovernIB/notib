package es.caib.notib.persist.resourceentity;

import es.caib.notib.persist.base.entity.ResourceEntity;

/**
 * Mètodes requerits per una entitat gestionada per un administrador d'entitats.
 *
 * @author Límit Tecnologies
 */
public interface AdminEntitatResourceEntity<R> extends ResourceEntity<R, Long> {

	EntitatResourceEntity getEntitat();
	void setEntitat(EntitatResourceEntity entitat);

}
