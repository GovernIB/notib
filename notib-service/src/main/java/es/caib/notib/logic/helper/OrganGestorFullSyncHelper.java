package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Orquestra la sincronització completa amb DIR3/ROLSAC: òrgans, migració de permisos
 * d'òrgans obsolets, procediments, serveis i oficines SIR, en una sola execució, publicant
 * el progrés combinat via SSE.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrganGestorFullSyncHelper {

	private final OrganGestorSyncHelper organGestorSyncHelper;
	private final PermisosHelper permisosHelper;
	private final ProcSerSyncHelper procSerSyncHelper;
	private final OrganGestorService organGestorService;
	private final OrganGestorRepository organGestorRepository;
	private final SseEventService progressEventService;

	public void sincronitzarTot(EntitatResourceEntity entitat) {

		var entitatDto = new EntitatDto();
		entitatDto.setId(entitat.getId());
		entitatDto.setCodi(entitat.getCodi());
		entitatDto.setDir3Codi(entitat.getDir3Codi());

		publish(0, "Iniciant sincronització completa");
		var resultatOrgans = organGestorSyncHelper.sincronitzar(entitat, false, SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC);
		publish(40, "Òrgans actualitzats");

		publish(40, "Migrant permisos d'òrgans obsolets");
		migrarPermisos(resultatOrgans);
		publish(45, "Permisos migrats");

		publish(45, "Actualitzant procediments");
		try {
			procSerSyncHelper.actualitzaProcediments(entitatDto, (percent, message) -> publish(45 + percent * 25 / 100, message));
		} catch (Exception ex) {
			log.error("Error actualitzant procediments a la sincronització combinada", ex);
			publish(70, "Error actualitzant procediments: " + ex.getMessage());
		}

		publish(70, "Actualitzant serveis");
		try {
			procSerSyncHelper.actualitzaServeis(entitatDto, (percent, message) -> publish(70 + percent * 20 / 100, message));
		} catch (Exception ex) {
			log.error("Error actualitzant serveis a la sincronització combinada", ex);
			publish(90, "Error actualitzant serveis: " + ex.getMessage());
		}

		publish(90, "Sincronitzant oficines SIR");
		try {
			organGestorService.syncOficinesSIR(entitat.getId());
		} catch (Exception ex) {
			log.error("Error sincronitzant oficines SIR a la sincronització combinada", ex);
			publish(95, "Error sincronitzant oficines SIR: " + ex.getMessage());
		}

		publish(100, "Sincronització completada", SseEvent.SseEventStatus.DONE);
	}

	private void migrarPermisos(OrganGestorDir3Sync resultatOrgans) {

		// Substitucions: `nou` és l'extint, `vell` és el supervivent (invertit respecte del
		// nom del camp — veure el comentari de OrganGestorSyncHelper.persistirTransicions).
		var codisSubstituits = Arrays.stream(resultatOrgans.getSubstitucions())
			.map(s -> s.getNou().getCodi())
			.collect(Collectors.toList());
		var codisFusionats = Arrays.stream(resultatOrgans.getFusions())
			.flatMap(f -> Arrays.stream(f.getVells()))
			.map(OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem::getCodi)
			.collect(Collectors.toList());
		var codisDividits = Arrays.stream(resultatOrgans.getDivisions())
			.map(d -> d.getVell().getCodi())
			.collect(Collectors.toList());
		if (codisSubstituits.isEmpty() && codisFusionats.isEmpty() && codisDividits.isEmpty()) {
			return;
		}
		var organsSubstituits = organGestorRepository.findByCodiIn(codisSubstituits);
		var organsFusionats = organGestorRepository.findByCodiIn(codisFusionats);
		var organsDividits = organGestorRepository.findByCodiIn(codisDividits);
		List<es.caib.notib.plugin.unitat.NodeDir3> unitatsSintetiques = new ArrayList<>();
		Arrays.asList(codisSubstituits, codisFusionats, codisDividits).forEach(codis -> codis.forEach(codi -> {
			var node = new es.caib.notib.plugin.unitat.NodeDir3();
			node.setCodi(codi);
			unitatsSintetiques.add(node);
		}));
		var progres = new es.caib.notib.logic.intf.dto.ProgresActualitzacioDto();
		progres.setOnInfo(entry -> publish(42, entry.getText()));
		permisosHelper.actualitzarPermisosOrgansObsolets(unitatsSintetiques, organsDividits, organsFusionats, organsSubstituits, progres);
	}

	private void publish(int percent, String message) {
		publish(percent, message, SseEvent.SseEventStatus.RUNNING);
	}

	private void publish(int percent, String message, SseEvent.SseEventStatus status) {
		progressEventService.publishEvent(
			SseEventService.SseQueue.PROGRESS,
			new SseEvent(SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC, percent, status, message));
	}

}
