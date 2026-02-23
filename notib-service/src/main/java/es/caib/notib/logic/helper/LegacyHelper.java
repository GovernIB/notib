package es.caib.notib.logic.helper;

import es.caib.notib.logic.helper.plugin.UnitatsOrganitzativesPluginHelper;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.model.Dir3Resource;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	private final UnitatsOrganitzativesPluginHelper unitatsOrganitzativesPluginHelper;

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

	public Optional<Dir3Resource> dir3FindOne(String codi) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		ConfigHelper.setEntitatCodi(currentEntitat.getCodi());
		NodeDir3 nodeDir3 = pluginHelper.unitatOrganitzativaFindByCodi(
			currentEntitat.getCodi(),
			codi,
			null,
			null);
		if (nodeDir3 != null) {
			return Optional.of(toDir3Resource(nodeDir3));
		} else {
			return Optional.empty();
		}
	}

	public Page<Dir3Resource> dir3FindMultiple(
		String codi,
		String denominacio,
		Long nivellAdministracio,
		Long comunitatAutonoma,
		Boolean ambOficines,
		Boolean esUnitatArrel,
		Long provincia,
		String municipi,
		Pageable pageable) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		ConfigHelper.setEntitatCodi(currentEntitat.getCodi());
		List<OrganGestorDto> nodesDir3 = pluginHelper.cercaUnitats(
			currentEntitat.getId(),
			codi,
			denominacio,
			nivellAdministracio,
			comunitatAutonoma,
			ambOficines,
			esUnitatArrel,
			provincia,
			municipi);
		if (!nodesDir3.isEmpty()) {
			return new PageImpl<>(
				nodesDir3.stream().map(this::toDir3Resource).collect(Collectors.toList()),
				pageable, nodesDir3.size());
		} else {
			return Page.empty();
		}
	}

	private Dir3Resource toDir3Resource(NodeDir3 nodeDir3) {
		Dir3Resource dir3Resource = new Dir3Resource();
		dir3Resource.setCodi(nodeDir3.getCodi());
		dir3Resource.setDenominacio(nodeDir3.getDenominacio());
		dir3Resource.setDenominacionCooficial(nodeDir3.getDenominacionCooficial());
		dir3Resource.setVersio(nodeDir3.getVersio());
		dir3Resource.setEstat(nodeDir3.getEstat());
		return dir3Resource;
	}

	private Dir3Resource toDir3Resource(OrganGestorDto organGestorDto) {
		Dir3Resource dir3Resource = new Dir3Resource();
		dir3Resource.setCodi(organGestorDto.getCodi());
		dir3Resource.setDenominacio(organGestorDto.getNom());
		dir3Resource.setDenominacionCooficial(organGestorDto.getNomEs());
		dir3Resource.setEstat(organGestorDto.getEstat().toString());
		return dir3Resource;
	}

}
