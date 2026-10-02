package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.helper.UsuariHelper;
import es.caib.notib.logic.intf.base.permission.ExtendedPermission;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.repository.NotificacioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Comprovacions de permís de NotificacioServiceImpl.marcarComProcessada.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificacioServiceImplMarcarProcessadaTest {

	private static final Long NOTIFICACIO_ID = 1L;
	private static final Long ENTITAT_ID = 5L;

	@Mock private PermisosService permisosService;
	@Mock private NotibPermissionHelper notibPermissionHelper;
	@Mock private EntityComprovarHelper entityComprovarHelper;
	@Mock private MetricsHelper metricsHelper;
	@Mock private NotificacioTableHelper notificacioTableHelper;
	@Mock private UsuariHelper usuariHelper;
	@Mock private NotificacioRepository notificacioRepository;
	@Mock private AuditHelper auditHelper;

	@InjectMocks
	private NotificacioServiceImpl service;

	private NotificacioEntity notificacio;

	@BeforeEach
	void setUp() {
		var entitat = mock(EntitatEntity.class);
		when(entitat.getId()).thenReturn(ENTITAT_ID);
		when(entitat.getCodi()).thenReturn("ENT");
		notificacio = mock(NotificacioEntity.class);
		when(notificacio.getId()).thenReturn(NOTIFICACIO_ID);
		when(notificacio.getEntitat()).thenReturn(entitat);
		when(notificacio.getEstat()).thenReturn(NotificacioEstatEnumDto.FINALITZADA);
		when(notificacio.getUsuariCodi()).thenReturn("creador");
		when(entityComprovarHelper.comprovarNotificacio(null, NOTIFICACIO_ID)).thenReturn(notificacio);
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("usuari1", "N/A", List.of()));
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldCheckPermissionOfCurrentUser_notCreator() {
		when(permisosService.hasNotificacioPermis(NOTIFICACIO_ID, ENTITAT_ID, "usuari1", PermisEnum.PROCESSAR)).thenReturn(true);

		assertDoesNotThrow(() -> service.marcarComProcessada(NOTIFICACIO_ID, "motiu", false));

		verify(permisosService).hasNotificacioPermis(NOTIFICACIO_ID, ENTITAT_ID, "usuari1", PermisEnum.PROCESSAR);
		verify(permisosService, never()).hasNotificacioPermis(anyLong(), anyLong(), eq("creador"), any());
		verify(notificacioRepository).saveAndFlush(notificacio);
	}

	@Test
	void shouldReject_whenCurrentUserHasNoPermission() {
		when(permisosService.hasNotificacioPermis(anyLong(), anyLong(), anyString(), any())).thenReturn(false);

		assertThrows(Exception.class, () -> service.marcarComProcessada(NOTIFICACIO_ID, "motiu", false));

		verify(notificacioRepository, never()).saveAndFlush(any());
	}

	@Test
	void shouldReject_whenNoAuthenticatedUser() {
		SecurityContextHolder.clearContext();

		assertThrows(Exception.class, () -> service.marcarComProcessada(NOTIFICACIO_ID, "motiu", false));

		verify(notificacioRepository, never()).saveAndFlush(any());
	}

	@Test
	void shouldReject_whenAdministradorWithoutAdminPermissionOnNotificacioEntitat() {
		when(notibPermissionHelper.entitatPermissionAllowed(ENTITAT_ID, ExtendedPermission.PERM2)).thenReturn(false);

		assertThrows(Exception.class, () -> service.marcarComProcessada(NOTIFICACIO_ID, "motiu", true));

		verify(notificacioRepository, never()).saveAndFlush(any());
	}

	@Test
	void shouldAllow_whenAdministradorWithAdminPermissionOnNotificacioEntitat() {
		when(notibPermissionHelper.entitatPermissionAllowed(ENTITAT_ID, ExtendedPermission.PERM2)).thenReturn(true);

		assertDoesNotThrow(() -> service.marcarComProcessada(NOTIFICACIO_ID, "motiu", true));

		verify(permisosService, never()).hasNotificacioPermis(any(), any(), any(), any());
		verify(notificacioRepository).saveAndFlush(notificacio);
	}

	@Test
	void shouldReject_whenNotFinalitzada() {
		when(notificacio.getEstat()).thenReturn(NotificacioEstatEnumDto.ENVIADA);
		when(notibPermissionHelper.entitatPermissionAllowed(ENTITAT_ID, ExtendedPermission.PERM2)).thenReturn(true);

		assertThrows(Exception.class, () -> service.marcarComProcessada(NOTIFICACIO_ID, "motiu", true));

		verify(notificacioRepository, never()).saveAndFlush(any());
	}

}
