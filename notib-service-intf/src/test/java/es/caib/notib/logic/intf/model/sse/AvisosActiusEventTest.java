package es.caib.notib.logic.intf.model.sse;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.AvisResource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test unitari del filtrat d'avisos actius per rol i entitat.
 */
class AvisosActiusEventTest {

	private final AvisResource propi = avis(1L, 10L, false);
	private final AvisResource propiAdmin = avis(2L, 10L, true);
	private final AvisResource altraEntitat = avis(3L, 20L, false);
	private final AvisResource global = avis(4L, null, false);
	private final List<AvisResource> tots = List.of(propi, propiAdmin, altraEntitat, global);

	@Test
	void superusuariVeuTotsElsAvisos() {
		assertEquals(List.of(1L, 2L, 3L, 4L), ids(AvisosActiusEvent.filtrar(tots, BaseConfig.ROLE_SUPER, null)));
	}

	@Test
	void administradorVeuElsDeLaSevaEntitatIGlobals() {
		assertEquals(List.of(1L, 2L, 4L), ids(AvisosActiusEvent.filtrar(tots, BaseConfig.ROLE_ADMIN, 10L)));
	}

	@Test
	void usuariNoVeuElsAvisosDAdministrador() {
		assertEquals(List.of(1L, 4L), ids(AvisosActiusEvent.filtrar(tots, BaseConfig.ROLE_USER, 10L)));
	}

	@Test
	void sensEntitatNomesEsVeuenElsGlobals() {
		assertEquals(List.of(4L), ids(AvisosActiusEvent.filtrar(tots, BaseConfig.ROLE_USER, null)));
	}

	private static AvisResource avis(Long id, Long entitatId, boolean admin) {
		var avis = AvisResource.builder()
			.avisAdministrador(admin)
			.entitat(entitatId != null ? ResourceReference.toResourceReference(entitatId) : null)
			.build();
		avis.setId(id);
		return avis;
	}

	private static List<Long> ids(AvisosActiusEvent event) {
		return event.getAvisos().stream().map(AvisResource::getId).collect(Collectors.toList());
	}

}
