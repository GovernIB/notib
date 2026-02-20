package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Helper per a cridar lògica preexistent des dels *ResourceServiceImpls.
 *
 * @author Límit Tecnologies
 */
@Component
@RequiredArgsConstructor
public class LegacyHelper {

	private final UserSessionHelper userSessionHelper;
	private final PluginHelper pluginHelper;

	/**
	 * Crea un document adjunt per una notificació.
	 *
	 * @param fileReference
	 *            referència a l'arxiu del document adjunt.
	 * @return l'id del document creat a la gestió documental.
	 */
	public String notificacioAdjuntCreate(FileReference fileReference) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		ConfigHelper.setEntitatCodi(currentEntitat.getCodi());
		return pluginHelper.gestioDocumentalCreate(
			PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
			fileReference.getContent());
	}

}
