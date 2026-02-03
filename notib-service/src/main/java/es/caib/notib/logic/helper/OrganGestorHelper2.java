package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.*;

/**
 * Helper per a convertir entities a dto
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrganGestorHelper2 {

	private final PluginHelper pluginHelper;

	public void sincronitzar(
		EntitatResourceEntity entitat,
		boolean simular) {
		Date dataActualitzacio = entitat.getDataActualitzacio() != null ? Date.from(
			entitat.getDataActualitzacio().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
		Date dataSincronitzacio = entitat.getDataSincronitzacio() != null ? Date.from(
			entitat.getDataSincronitzacio().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
		List<NodeDir3> unitatsDir3 = pluginHelper.unitatsOrganitzativesFindByPare(
			entitat.getCodi(),
			entitat.getDir3Codi(),
			dataActualitzacio,
			dataSincronitzacio);
	}

	/*private List<OrganGestorEntity> calcularExtingides(String entitatCodi, List<String> codis, boolean firstSincronization) {
		for (NodeDir3 u : unitatsWs) {
			codis.add(u.getCodi());
		}
		List<OrganGestorEntity> extingides = new ArrayList<>();
		var organsAExtingir = firstSincronization ? organGestorRepository.findCodiActiusByEntitat(entitatCodi) : organGestorRepository.findCodiInactiusByEntitat(entitatCodi);
		if (organsAExtingir.isEmpty()) {
			return extingides;
		}
		for (var codi: codis) {
			organsAExtingir.remove(codi);
		}
		if (organsAExtingir.isEmpty())
			return extingides;

		return getExtingits(entitatCodi, organsAExtingir);
	}*/
}
