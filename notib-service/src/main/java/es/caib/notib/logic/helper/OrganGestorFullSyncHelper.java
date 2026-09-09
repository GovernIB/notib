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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 3600)
	public void sincronitzarTot(EntitatResourceEntity entitat) {

		var entitatDto = new EntitatDto();
		entitatDto.setId(entitat.getId());
		entitatDto.setCodi(entitat.getCodi());
		entitatDto.setNom(entitat.getNom());
		entitatDto.setDir3Codi(entitat.getDir3Codi());

		publish(0, "Iniciant sincronització completa");
		// Fases 1 i 2 (òrgans i permisos) són prerequisit de les següents: si fallen no es pot
		// continuar, i el flux SSE s'ha de tancar amb un event ERROR terminal perquè el frontend
		// no quedi esperant indefinidament.
		OrganGestorDir3Sync resultatOrgans;
		try {
			// terminal=false: la finalització d'aquesta fase no ha de publicar DONE, que tancaria
			// el flux SSE i descartaria els events de les fases següents.
			resultatOrgans = organGestorSyncHelper.sincronitzar(entitat, false, SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC, false);
		} catch (Exception ex) {
			log.error("Error sincronitzant òrgans a la sincronització combinada", ex);
			publish(40, "Error sincronitzant òrgans: " + ex.getMessage(), SseEvent.SseEventStatus.ERROR);
			return;
		}
		publish(40, "Òrgans actualitzats");

		publish(40, "Migrant permisos d'òrgans obsolets");
		try {
			migrarPermisos(resultatOrgans, entitat);
		} catch (Exception ex) {
			log.error("Error migrant permisos d'òrgans obsolets a la sincronització combinada", ex);
			publish(45, "Error migrant permisos d'òrgans obsolets: " + ex.getMessage(), SseEvent.SseEventStatus.ERROR);
			return;
		}
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

	private void migrarPermisos(OrganGestorDir3Sync resultatOrgans, EntitatResourceEntity entitat) {

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
		// `codi` no és únic entre entitats a not_organ_gestor, així que cada resultat de
		// findByCodiIn s'ha de filtrar per l'entitat que s'està sincronitzant (igual que fa
		// OrganGestorSyncHelper.persistirTransicions). Sense aquest filtre una col·lisió de codis
		// entre entitats duplicaria permisos ACL a l'òrgan d'una altra entitat.
		var organsSubstituits = organsAmbSuccessor(filtrarPerEntitat(organGestorRepository.findByCodiIn(codisSubstituits), entitat));
		var organsFusionats = organsAmbSuccessor(filtrarPerEntitat(organGestorRepository.findByCodiIn(codisFusionats), entitat));
		var organsDividits = filtrarPerEntitat(organGestorRepository.findByCodiIn(codisDividits), entitat);
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

	/**
	 * Descarta els òrgans que no pertanyen a l'entitat que s'està sincronitzant.
	 */
	private List<OrganGestorEntity> filtrarPerEntitat(List<OrganGestorEntity> organs, EntitatResourceEntity entitat) {
		return organs.stream()
			.filter(organ -> {
				var pertany = OrganGestorSyncHelper.pertanyAEntitat(organ, entitat);
				if (!pertany) {
					log.warn(
						"Ignorant òrgan gestor amb codi {} a la migració de permisos perquè pertany a una entitat diferent de la que s'està sincronitzant (entitat esperada: {})",
						organ != null ? organ.getCodi() : null,
						entitat.getCodi());
				}
				return pertany;
			})
			.collect(Collectors.toList());
	}

	/**
	 * Descarta els òrgans sense successor registrat. {@code PermisosHelper.actualitzarPermisosOrgansObsolets}
	 * fa {@code organOrigen.getNous().get(0)} sense comprovar que la col·lecció no estigui buida
	 * per als òrgans fusionats i substituïts, i {@code persistirTransicions} només escriu
	 * {@code nous}/{@code antics} quan els dos extrems de la transició pertanyen a l'entitat
	 * sincronitzada — així que un òrgan pot arribar aquí sense successor.
	 */
	private List<OrganGestorEntity> organsAmbSuccessor(List<OrganGestorEntity> organs) {
		return organs.stream()
			.filter(organ -> {
				var teSuccessor = organ.getNous() != null && !organ.getNous().isEmpty();
				if (!teSuccessor) {
					log.warn(
						"Ignorant òrgan gestor amb codi {} a la migració de permisos perquè no té cap òrgan successor registrat",
						organ.getCodi());
				}
				return teSuccessor;
			})
			.collect(Collectors.toList());
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
