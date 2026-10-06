package es.caib.notib.logic.service;

import es.caib.notib.logic.intf.dto.CodiValorDto;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.repository.NotificacioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Tests de PermisosServiceImpl.hasNotificacioPermis.
 */
class PermisosServiceImplHasNotificacioPermisTest {

	private static final Long NOT_ID = 1L;
	private static final Long ENTITAT_ID = 5L;
	private static final String USUARI = "usuari1";

	private PermisosServiceImpl service;
	private NotificacioRepository notificacioRepository;

	@BeforeEach
	void setUp() {
		service = spy(new PermisosServiceImpl());
		notificacioRepository = mock(NotificacioRepository.class);
		ReflectionTestUtils.setField(service, "notificacioRepository", notificacioRepository);
	}

	@Test
	void shouldGrant_whenUserHasPermissionOnOrgan() {
		permisos(List.of(), List.of("ORG1"));
		notificacio(NotificacioEstatEnumDto.FINALITZADA, "PROC1", "ORG1");

		assertTrue(service.hasNotificacioPermis(NOT_ID, ENTITAT_ID, USUARI, PermisEnum.PROCESSAR));
	}

	@Test
	void shouldGrant_whenUserHasPermissionOnProcediment() {
		permisos(List.of("PROC1"), List.of());
		notificacio(NotificacioEstatEnumDto.FINALITZADA, "PROC1", "ORG1");

		assertTrue(service.hasNotificacioPermis(NOT_ID, ENTITAT_ID, USUARI, PermisEnum.PROCESSAR));
	}

	@Test
	void shouldGrantByOrgan_whenNotificacioHasNoProcediment() {
		permisos(List.of(), List.of("ORG1"));
		notificacio(NotificacioEstatEnumDto.FINALITZADA, null, "ORG1");

		assertTrue(service.hasNotificacioPermis(NOT_ID, ENTITAT_ID, USUARI, PermisEnum.PROCESSAR));
	}

	@Test
	void shouldDeny_whenNoMatchingPermission() {
		permisos(List.of("PROC2"), List.of("ORG2"));
		notificacio(NotificacioEstatEnumDto.FINALITZADA, "PROC1", "ORG1");

		assertFalse(service.hasNotificacioPermis(NOT_ID, ENTITAT_ID, USUARI, PermisEnum.PROCESSAR));
	}

	@Test
	void shouldDenyProcessar_whenNotFinalitzada() {
		permisos(List.of("PROC1"), List.of("ORG1"));
		notificacio(NotificacioEstatEnumDto.ENVIADA, "PROC1", "ORG1");

		assertFalse(service.hasNotificacioPermis(NOT_ID, ENTITAT_ID, USUARI, PermisEnum.PROCESSAR));
	}

	private void permisos(List<String> procediments, List<String> organs) {
		doReturn(procediments.stream().map(c -> CodiValorOrganGestorComuDto.builder().codi(c).build()).collect(java.util.stream.Collectors.toList()))
				.when(service).getProcSersAmbPermis(ENTITAT_ID, USUARI, PermisEnum.PROCESSAR);
		doReturn(organs.stream().map(c -> CodiValorDto.builder().codi(c).build()).collect(java.util.stream.Collectors.toList()))
				.when(service).getOrgansAmbPermis(ENTITAT_ID, USUARI, PermisEnum.PROCESSAR);
	}

	private void notificacio(NotificacioEstatEnumDto estat, String procedimentCodi, String organCodi) {
		var not = mock(NotificacioEntity.class);
		when(not.getEstat()).thenReturn(estat);
		if (procedimentCodi != null) {
			var procediment = mock(ProcSerEntity.class);
			when(procediment.getCodi()).thenReturn(procedimentCodi);
			when(not.getProcediment()).thenReturn(procediment);
		}
		var organ = mock(OrganGestorEntity.class);
		when(organ.getCodi()).thenReturn(organCodi);
		when(not.getOrganGestor()).thenReturn(organ);
		when(notificacioRepository.findById(NOT_ID)).thenReturn(Optional.of(not));
	}

}
