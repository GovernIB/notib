package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrganGestorFullSyncHelperTest {

	private OrganGestorDir3Sync buidaResultatOrgans() {
		return novaResultatOrgans(new OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio[0]);
	}

	private OrganGestorDir3Sync novaResultatOrgans(OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio[] substitucions) {
		return new OrganGestorDir3Sync(
			null,
			new OrganGestorDir3Sync.OrganGestorDir3SyncCanviCreacio[0],
			new OrganGestorDir3Sync.OrganGestorDir3SyncCanviModificacio[0],
			substitucions,
			new OrganGestorDir3Sync.OrganGestorDir3SyncCanviExtincio[0],
			new OrganGestorDir3Sync.OrganGestorDir3SyncCanviFusio[0],
			new OrganGestorDir3Sync.OrganGestorDir3SyncCanviDivisio[0],
			true,
			false);
	}

	private EntitatResourceEntity novaEntitat() {
		var entitat = new EntitatResourceEntity();
		entitat.setId(1L);
		entitat.setCodi("ENT1");
		entitat.setNom("Entitat 1");
		entitat.setDir3Codi("D3-ENT1");
		return entitat;
	}

	@Test
	void sincronitzarTotShouldRunAllPhasesAndPublishDoneAtTheEnd() throws Exception {
		// given
		var permisosHelper = Mockito.mock(PermisosHelper.class);
		var procSerSyncHelper = Mockito.mock(ProcSerSyncHelper.class);
		var organGestorService = Mockito.mock(OrganGestorService.class);
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.anyList())).thenReturn(List.of());
		var progressEventService = Mockito.mock(SseEventService.class);
		var organGestorSyncHelper = Mockito.mock(OrganGestorSyncHelper.class);
		// La fase d'òrgans s'ha d'invocar en mode NO terminal (4t argument false) perquè el seu
		// event final no tanqui el flux SSE de la sincronització combinada. El mock reprodueix el
		// comportament real d'OrganGestorSyncHelper.sincronitzar: publica un event final amb
		// estat DONE si se li demana en mode terminal i RUNNING si no, de manera que l'assert de
		// "només un event terminal" també cobreix la fase 1 i falla si se li passés terminal=true.
		Mockito.when(organGestorSyncHelper.sincronitzar(
				Mockito.any(),
				Mockito.eq(false),
				Mockito.eq(SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC),
				Mockito.anyBoolean()))
			.thenAnswer(invocation -> {
				boolean terminal = invocation.getArgument(3);
				progressEventService.publishEvent(
					SseEventService.SseQueue.PROGRESS,
					new SseEvent(
						SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC,
						100,
						terminal ? SseEvent.SseEventStatus.DONE : SseEvent.SseEventStatus.RUNNING,
						null));
				return buidaResultatOrgans();
			});

		var helper = new OrganGestorFullSyncHelper(organGestorSyncHelper, permisosHelper, procSerSyncHelper,
			organGestorService, organGestorRepository, progressEventService);
		var entitat = novaEntitat();

		// when
		helper.sincronitzarTot(entitat);

		// then
		Mockito.verify(organGestorSyncHelper).sincronitzar(entitat, false, SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC, false);
		Mockito.verify(procSerSyncHelper).actualitzaProcediments(Mockito.any(), Mockito.any());
		Mockito.verify(procSerSyncHelper).actualitzaServeis(Mockito.any(), Mockito.any());
		Mockito.verify(organGestorService).syncOficinesSIR(1L);
		var eventCaptor = ArgumentCaptor.forClass(SseEvent.class);
		Mockito.verify(progressEventService, Mockito.atLeastOnce())
			.publishEvent(Mockito.eq(SseEventService.SseQueue.PROGRESS), eventCaptor.capture());
		var events = eventCaptor.getAllValues();
		assertTrue(events.stream().allMatch(e -> e.getEventName() == SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC));
		assertEquals(SseEvent.SseEventStatus.DONE, events.get(events.size() - 1).getStatus());
		assertEquals(100, events.get(events.size() - 1).getPercent());
		// i, sobretot: cap event intermedi pot dur un estat terminal (DONE/ERROR), perquè el
		// transport SSE (SseController) tanca l'emitter en rebre'n un i descartaria tota la
		// resta del log. L'únic terminal ha de ser el darrer.
		var terminals = events.stream()
			.filter(e -> SseEvent.SseEventStatus.DONE.equals(e.getStatus()) || SseEvent.SseEventStatus.ERROR.equals(e.getStatus()))
			.count();
		assertEquals(1, terminals, "Només el darrer event del flux combinat pot ser terminal");
	}

	@Test
	void migrarPermisosShouldIgnoreOrgansFromADifferentEntitat() {
		// given: DIR3 reporta una substitució en què A01 (l'extint, camp `nou` del DTO per la
		// inversió de noms) és substituït per A02 (el supervivent, camp `vell`). findByCodiIn
		// retorna DUES files amb codi A01: una de l'entitat sincronitzada i una d'una altra
		// entitat (col·lisió de codis, ja que `codi` no és únic a not_organ_gestor).
		var substitucio = new OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio(
			new OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem("A02", "Unitat A02", "Unitat A02", null),
			new OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem("A01", "Unitat A01", "Unitat A01", null));
		var organGestorSyncHelper = Mockito.mock(OrganGestorSyncHelper.class);
		Mockito.when(organGestorSyncHelper.sincronitzar(
				Mockito.any(), Mockito.eq(false), Mockito.any(), Mockito.eq(false)))
			.thenReturn(novaResultatOrgans(new OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio[] { substitucio }));

		var entitatSincronitzada = Mockito.mock(EntitatEntity.class);
		Mockito.when(entitatSincronitzada.getCodi()).thenReturn("ENT1");
		var entitatAliena = Mockito.mock(EntitatEntity.class);
		Mockito.when(entitatAliena.getCodi()).thenReturn("ENT-ALIENA");
		var successorPropi = OrganGestorEntity.builder().codi("A02").entitat(entitatSincronitzada).build();
		var a01Propi = OrganGestorEntity.builder().codi("A01").entitat(entitatSincronitzada).build();
		a01Propi.addNou(successorPropi);
		var successorAlie = OrganGestorEntity.builder().codi("A02").entitat(entitatAliena).build();
		var a01Alie = OrganGestorEntity.builder().codi("A01").entitat(entitatAliena).build();
		a01Alie.addNou(successorAlie);

		var permisosHelper = Mockito.mock(PermisosHelper.class);
		var procSerSyncHelper = Mockito.mock(ProcSerSyncHelper.class);
		var organGestorService = Mockito.mock(OrganGestorService.class);
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.anyList())).thenReturn(List.of(a01Propi, a01Alie));
		var progressEventService = Mockito.mock(SseEventService.class);

		var helper = new OrganGestorFullSyncHelper(organGestorSyncHelper, permisosHelper, procSerSyncHelper,
			organGestorService, organGestorRepository, progressEventService);

		// when
		helper.sincronitzarTot(novaEntitat());

		// then: només l'òrgan de l'entitat sincronitzada arriba a PermisosHelper; l'homònim de
		// l'altra entitat s'ha de descartar per no duplicar-li permisos ACL.
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<OrganGestorEntity>> substituitsCaptor = ArgumentCaptor.forClass(List.class);
		Mockito.verify(permisosHelper).actualitzarPermisosOrgansObsolets(
			Mockito.anyList(), Mockito.anyList(), Mockito.anyList(), substituitsCaptor.capture(), Mockito.any());
		assertEquals(List.of(a01Propi), substituitsCaptor.getValue());
	}

	@Test
	void migrarPermisosShouldIgnoreOrgansWithoutSuccessor() {
		// given: la mateixa substitució, però l'òrgan extint de l'entitat sincronitzada no té cap
		// successor registrat (persistirTransicions no l'ha pogut escriure). PermisosHelper faria
		// getNous().get(0) i llançaria IndexOutOfBoundsException, així que s'ha de descartar.
		var substitucio = new OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio(
			new OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem("A02", "Unitat A02", "Unitat A02", null),
			new OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem("A01", "Unitat A01", "Unitat A01", null));
		var organGestorSyncHelper = Mockito.mock(OrganGestorSyncHelper.class);
		Mockito.when(organGestorSyncHelper.sincronitzar(
				Mockito.any(), Mockito.eq(false), Mockito.any(), Mockito.eq(false)))
			.thenReturn(novaResultatOrgans(new OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio[] { substitucio }));

		var entitatSincronitzada = Mockito.mock(EntitatEntity.class);
		Mockito.when(entitatSincronitzada.getCodi()).thenReturn("ENT1");
		var a01SenseSuccessor = OrganGestorEntity.builder().codi("A01").entitat(entitatSincronitzada).build();

		var permisosHelper = Mockito.mock(PermisosHelper.class);
		var procSerSyncHelper = Mockito.mock(ProcSerSyncHelper.class);
		var organGestorService = Mockito.mock(OrganGestorService.class);
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.anyList())).thenReturn(List.of(a01SenseSuccessor));
		var progressEventService = Mockito.mock(SseEventService.class);

		var helper = new OrganGestorFullSyncHelper(organGestorSyncHelper, permisosHelper, procSerSyncHelper,
			organGestorService, organGestorRepository, progressEventService);

		// when
		helper.sincronitzarTot(novaEntitat());

		// then
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<OrganGestorEntity>> substituitsCaptor = ArgumentCaptor.forClass(List.class);
		Mockito.verify(permisosHelper).actualitzarPermisosOrgansObsolets(
			Mockito.anyList(), Mockito.anyList(), Mockito.anyList(), substituitsCaptor.capture(), Mockito.any());
		assertTrue(substituitsCaptor.getValue().isEmpty());
	}

	@Test
	void sincronitzarTotShouldPublishTerminalErrorAndRethrowWhenOrgansPhaseFails() {
		// given: la fase d'òrgans llança una excepció.
		var organGestorSyncHelper = Mockito.mock(OrganGestorSyncHelper.class);
		var causa = new RuntimeException("DIR3 no disponible");
		Mockito.when(organGestorSyncHelper.sincronitzar(
				Mockito.any(), Mockito.eq(false), Mockito.any(), Mockito.eq(false)))
			.thenThrow(causa);
		var permisosHelper = Mockito.mock(PermisosHelper.class);
		var procSerSyncHelper = Mockito.mock(ProcSerSyncHelper.class);
		var organGestorService = Mockito.mock(OrganGestorService.class);
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		var progressEventService = Mockito.mock(SseEventService.class);

		var helper = new OrganGestorFullSyncHelper(organGestorSyncHelper, permisosHelper, procSerSyncHelper,
			organGestorService, organGestorRepository, progressEventService);

		// when: l'excepció s'ha de propagar fora de sincronitzarTot (no quedar-se engolida), perquè
		// la transacció REQUIRES_NEW faci rollback i OrgansProcedimentsSyncActionExecutor.exec() no
		// informi d'un èxit fals.
		var llançada = assertThrows(RuntimeException.class, () -> helper.sincronitzarTot(novaEntitat()));
		assertEquals(causa, llançada);

		// then: cap fase posterior s'executa...
		Mockito.verifyNoInteractions(permisosHelper);
		Mockito.verifyNoInteractions(procSerSyncHelper);
		Mockito.verifyNoInteractions(organGestorService);
		// ...però abans de rellançar, el flux SSE s'ha tancat amb un event ERROR terminal, no queda
		// penjat sense terminal.
		var eventCaptor = ArgumentCaptor.forClass(SseEvent.class);
		Mockito.verify(progressEventService, Mockito.atLeastOnce())
			.publishEvent(Mockito.eq(SseEventService.SseQueue.PROGRESS), eventCaptor.capture());
		var events = eventCaptor.getAllValues();
		var darrer = events.get(events.size() - 1);
		assertEquals(SseEvent.SseEventStatus.ERROR, darrer.getStatus());
		assertTrue(darrer.getMessage().contains("DIR3 no disponible"));
	}

	@Test
	void sincronitzarTotShouldPublishTerminalErrorAndRethrowWhenPermisosPhaseFails() {
		// given: la fase d'òrgans va bé però la migració de permisos falla.
		var substitucio = new OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio(
			new OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem("A02", "Unitat A02", "Unitat A02", null),
			new OrganGestorDir3Sync.OrganGestorDir3SyncArbreItem("A01", "Unitat A01", "Unitat A01", null));
		var organGestorSyncHelper = Mockito.mock(OrganGestorSyncHelper.class);
		Mockito.when(organGestorSyncHelper.sincronitzar(
				Mockito.any(), Mockito.eq(false), Mockito.any(), Mockito.eq(false)))
			.thenReturn(novaResultatOrgans(new OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio[] { substitucio }));
		var permisosHelper = Mockito.mock(PermisosHelper.class);
		var procSerSyncHelper = Mockito.mock(ProcSerSyncHelper.class);
		var organGestorService = Mockito.mock(OrganGestorService.class);
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		var causa = new RuntimeException("BD no disponible");
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.anyList()))
			.thenThrow(causa);
		var progressEventService = Mockito.mock(SseEventService.class);

		var helper = new OrganGestorFullSyncHelper(organGestorSyncHelper, permisosHelper, procSerSyncHelper,
			organGestorService, organGestorRepository, progressEventService);

		// when
		var llançada = assertThrows(RuntimeException.class, () -> helper.sincronitzarTot(novaEntitat()));
		assertEquals(causa, llançada);

		// then
		Mockito.verifyNoInteractions(procSerSyncHelper);
		Mockito.verifyNoInteractions(organGestorService);
		var eventCaptor = ArgumentCaptor.forClass(SseEvent.class);
		Mockito.verify(progressEventService, Mockito.atLeastOnce())
			.publishEvent(Mockito.eq(SseEventService.SseQueue.PROGRESS), eventCaptor.capture());
		var events = eventCaptor.getAllValues();
		var darrer = events.get(events.size() - 1);
		assertEquals(SseEvent.SseEventStatus.ERROR, darrer.getStatus());
		assertTrue(darrer.getMessage().contains("BD no disponible"));
	}

}
