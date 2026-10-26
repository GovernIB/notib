package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.AvisResource;
import es.caib.notib.logic.intf.model.sse.AvisosActiusEvent;
import es.caib.notib.persist.resourceentity.AvisResourceEntity;
import es.caib.notib.persist.resourcerepository.AvisResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvisEventHelper {

	private final AvisResourceRepository avisRepository;

	/**
	 * Retorna tots els avisos actius, sense filtrar per rol ni entitat.
	 * <p/>
	 * Es crida des de l'afterCommit de la transacció que ha modificat els avisos, on la transacció
	 * original ja s'ha confirmat però encara està associada al fil: cal una transacció nova.
	 */
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public List<AvisResource> findAvisosActius() {
		return consultarAvisosActius();
	}

	@Transactional(readOnly = true)
	public AvisosActiusEvent getAvisosActius(String rol, Long entitatId) {
		return AvisosActiusEvent.filtrar(consultarAvisosActius(), rol, entitatId);
	}

	private List<AvisResource> consultarAvisosActius() {

		return avisRepository.findActius(DateUtils.truncate(new Date(), Calendar.DATE)).stream()
			.map(this::toAvisSse)
			.collect(Collectors.toList());
	}

	private AvisResource toAvisSse(AvisResourceEntity avis) {

		var resource = AvisResource.builder()
			.assumpte(avis.getAssumpte())
			.missatge(avis.getMissatge())
			.avisNivell(avis.getAvisNivell())
			.dataInici(avis.getDataInici())
			.dataFinal(avis.getDataFinal())
			.actiu(Boolean.TRUE.equals(avis.getActiu()))
			.avisAdministrador(Boolean.TRUE.equals(avis.getAvisAdministrador()))
			.entitat(avis.getEntitat() != null ? ResourceReference.toResourceReference(avis.getEntitat().getId()) : null)
			.build();
		// El front fa servir l'id com a clau de cada avís de la llista
		resource.setId(avis.getId());
		return resource;
	}
}
