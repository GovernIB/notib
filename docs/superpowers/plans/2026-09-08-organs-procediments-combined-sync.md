# Combined Òrgans/Procediments/Serveis/Oficines DIR3 Sync Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a single "Actualitzar òrgans i procediments" action that runs òrgans → permission migration → procediments → serveis → oficines SIR sequentially, streaming a live, appending operation log to the React SPA via SSE — matching the legacy JSP's combined flow, without touching the standalone òrgans-only or oficines-only buttons.

**Architecture:** A new orchestrating helper (`OrganGestorFullSyncHelper`) sequences the five phases, reusing existing sync logic in each (`OrganGestorSyncHelper`, `PermisosHelper`, `ProcSerSyncHelper`, `OrganGestorService.syncOficinesSIR`) and rescaling each phase's own progress into a slice of one combined SSE event stream (new `SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC`). Progress plumbing is additive everywhere: a `ProgressPublisher` hook is threaded through as new method overloads, existing call sites and behavior are untouched. A bridge (via `OrganGestorRepository.findByCodiIn`) reuses the existing, unmodified `PermisosHelper.actualitzarPermisosOrgansObsolets` against the legacy `OrganGestorEntity` model, since the new sync path doesn't otherwise populate the `nous`/`antics` relation that method depends on.

**Tech Stack:** Java 11, Spring Boot, Spring Data JPA, JUnit 5 (`notib-service`, `notib-service-intf`, `notib-back`); React 18 + TypeScript + MUI + `reactlib` (`notib-back/src/main/reactapp/notib-back`).

**Spec:** `docs/superpowers/specs/2026-09-08-organs-procediments-sync-ux-design.md` (Component 2 section, including "Pont de permisos")

## Global Constraints

- No existing method signature changes — every new capability is added via a new overload; the original no-arg/no-publisher methods must keep behaving exactly as they do today (verified by not modifying their existing call sites: `SchedulledServiceImpl`, `OrganGestorServiceImpl.syncDir3OrgansGestors`).
- `PermisosHelper.actualitzarPermisosOrgansObsolets` is **not modified** — only its call site's arguments are newly constructed.
- No new diff/simulation capability is added for procediments, serveis, or oficines — this plan only adds live progress logging to their existing apply-directly behavior.
- The standalone "Sincronització Dir3" (òrgans-only) and "Oficines" buttons keep working unchanged; this plan adds a third, independent button.
- New action code requires `BaseConfig.ROLE_ADMIN` (matching `DIR3_SYNC_ACTION_CODE`/`OFICINES_SYNC_ACTION_CODE`'s existing `@ResourceAccessConstraint`).
- Field-order gotcha carried over from the design spec: in `OrganGestorDir3Sync`, `substitucions[].vell` is the **surviving** org and `.nou` is the **extinct** one (inverted vs. what the names suggest) — `fusions`/`divisions` are *not* inverted (`vells`/`vell` = extinct, `nou`/`nous` = surviving). Get this backwards and permission migration duplicates in the wrong direction.
- Backend tests: JUnit 5, house style is plain instantiation + given/when/then comments (see `SseEventServiceImplTest.java`), Mockito only where a real dependency can't be constructed cheaply.

---

### Task 1: `SseEvent` new event name, `ProgressPublisher` interface, `ProgresActualitzacioDto` hooks

**Files:**
- Modify: `notib-service-intf/src/main/java/es/caib/notib/logic/intf/model/SseEvent.java`
- Create: `notib-service-intf/src/main/java/es/caib/notib/logic/intf/dto/ProgressPublisher.java`
- Modify: `notib-service-intf/src/main/java/es/caib/notib/logic/intf/dto/ProgresActualitzacioDto.java`
- Test: `notib-service-intf/src/test/java/es/caib/notib/logic/intf/dto/ProgresActualitzacioDtoTest.java` (new — check whether `notib-service-intf` has a `src/test` directory already; if not, create it following the same JUnit 5 setup as `notib-service`'s `pom.xml` test dependencies)

**Interfaces:**
- Produces: `SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC` (new enum constant). `ProgressPublisher.publish(int percent, String message)` (functional interface). `ProgresActualitzacioDto.setOnInfo(Consumer<ActualitzacioInfo>)`, `.setOnProgressChanged(Consumer<Integer>)` (new, Lombok-generated from new fields).

- [ ] **Step 1: Add the new SSE event name**

In `SseEvent.java`:

```java
	public enum SseEventName {
		DIR3_SYNC,
		ORGANS_PROCEDIMENTS_SYNC
	}
```

- [ ] **Step 2: Write `ProgressPublisher`**

```java
package es.caib.notib.logic.intf.dto;

/**
 * Callback per a publicar el progrés d'una operació llarga (percentatge + missatge).
 *
 * @author Límit Tecnologies
 */
@FunctionalInterface
public interface ProgressPublisher {

	void publish(int percent, String message);

}
```

- [ ] **Step 3: Write a failing test for the `ProgresActualitzacioDto` hooks**

```java
package es.caib.notib.logic.intf.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgresActualitzacioDtoTest {

	@Test
	void addInfoShouldInvokeOnInfoHookWhenSet() {
		// given
		var progres = new ProgresActualitzacioDto();
		List<String> received = new ArrayList<>();
		progres.setOnInfo(entry -> received.add(entry.getText()));
		// when
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, "hola");
		// then
		assertEquals(List.of("hola"), received);
	}

	@Test
	void addInfoShouldNotThrowWhenNoHookSet() {
		// given
		var progres = new ProgresActualitzacioDto();
		// when / then
		progres.addInfo(ProgresActualitzacioDto.TipusInfo.INFO, "hola");
		assertEquals(1, progres.getInfo().size());
	}

	@Test
	void incrementOperacionsRealitzadesShouldInvokeOnProgressChangedHookWhenSet() {
		// given
		var progres = new ProgresActualitzacioDto();
		progres.setNumOperacions(10);
		List<Integer> received = new ArrayList<>();
		progres.setOnProgressChanged(received::add);
		// when
		progres.incrementOperacionsRealitzades(5);
		// then
		assertEquals(List.of(50), received);
	}

	@Test
	void incrementOperacionsRealitzadesShouldNotThrowWhenNoHookSet() {
		// given
		var progres = new ProgresActualitzacioDto();
		progres.setNumOperacions(10);
		// when / then
		progres.incrementOperacionsRealitzades(5);
		assertTrue(progres.getProgres() == 50);
	}

}
```

- [ ] **Step 4: Run it to confirm it fails to compile (hooks don't exist yet)**

Run: `mvn -pl notib-service-intf test -Dtest=ProgresActualitzacioDtoTest`
Expected: compile error — `setOnInfo`/`setOnProgressChanged` don't exist.

- [ ] **Step 5: Add the hooks**

In `ProgresActualitzacioDto.java`:

```java
import java.util.function.Consumer;
// ... existing imports stay

	transient Consumer<ActualitzacioInfo> onInfo;
	transient Consumer<Integer> onProgressChanged;

	public void addInfo(TipusInfo tipus, String text) {

		log.info("[Progres Actualitzacio] " + text);
		var entry = new ActualitzacioInfo(tipus, text);
		info.add(entry);
		if (onInfo != null) {
			onInfo.accept(entry);
		}
	}

	public void incrementOperacionsRealitzades(int numOperacions) {
		if (this.numOperacions == null) {
			return;
		}
		this.numOperacionsRealitzades += numOperacions;
		double auxprogres = (this.numOperacionsRealitzades.doubleValue()  / this.numOperacions.doubleValue()) * 100;
		this.progres = (int) auxprogres;
		if (onProgressChanged != null) {
			onProgressChanged.accept(this.progres);
		}
	}
```

(Replace the existing `addInfo` and `incrementOperacionsRealitzades(int)` method bodies with the above — the `@Getter @Setter` class-level annotations already generate `setOnInfo`/`setOnProgressChanged`/`getOnInfo`/`getOnProgressChanged` for the two new fields, no manual accessors needed.)

- [ ] **Step 6: Run the test again to confirm it passes**

Run: `mvn -pl notib-service-intf test -Dtest=ProgresActualitzacioDtoTest`
Expected: PASS (all 4 tests).

- [ ] **Step 7: Run `notib-service`'s existing test suite to confirm nothing else broke**

Run: `mvn -pl notib-service test`
Expected: all tests pass (the changed methods are behaviorally identical when no hook is set).

- [ ] **Step 8: Commit**

```bash
git add notib-service-intf/src/main/java/es/caib/notib/logic/intf/model/SseEvent.java \
        notib-service-intf/src/main/java/es/caib/notib/logic/intf/dto/ProgressPublisher.java \
        notib-service-intf/src/main/java/es/caib/notib/logic/intf/dto/ProgresActualitzacioDto.java \
        notib-service-intf/src/test/java/es/caib/notib/logic/intf/dto/ProgresActualitzacioDtoTest.java
git commit -m "#1011 Afegit event SSE combinat i hooks de progrés a ProgresActualitzacioDto"
```

---

### Task 2: `ProcSerSyncHelper` progress-publisher overloads

**Files:**
- Modify: `notib-service/src/main/java/es/caib/notib/logic/helper/ProcSerSyncHelper.java:72-143` (`actualitzaProcediments`) and `:392-` (`actualitzaServeis`)
- Test: `notib-service/src/test/java/es/caib/notib/logic/helper/ProcSerSyncHelperTest.java` (new, if one doesn't already exist — check first)

**Interfaces:**
- Consumes: `ProgressPublisher` (Task 1).
- Produces: `ProcSerSyncHelper.actualitzaProcediments(EntitatDto, ProgressPublisher)`, `.actualitzaServeis(EntitatDto, ProgressPublisher)` (new overloads); existing `actualitzaProcediments(EntitatDto)`/`actualitzaServeis(EntitatDto)` unchanged in behavior, now thin delegates.

- [ ] **Step 1: Check for an existing test file**

Run: `find notib-service/src/test -iname "ProcSerSyncHelperTest.java"`
If found, read it first and add the new test methods below to it instead of creating a new file.

- [ ] **Step 2: Refactor `actualitzaProcediments` into a delegating pair**

Replace the method signature and the `progres = new ProgresActualitzacioProcSer();` line (`ProcSerSyncHelper.java:72` and `:90-91`):

```java
	public void actualitzaProcediments(EntitatDto entitatDto) {
		actualitzaProcediments(entitatDto, null);
	}

	public void actualitzaProcediments(EntitatDto entitatDto, ProgressPublisher publisher) {

		var info = new IntegracioInfo(IntegracioCodi.PROCEDIMENTS, "Actualització de procediments", IntegracioAccioTipusEnumDto.PROCESSAR,
				new AccioParam("Codi Dir3 de l'entitat", entitatDto.getDir3Codi()));
		info.setAplicacio(IntegracioInfo.INTERFICIE_WEB);
		if (entitatDto == null) {
			log.error("Error actualitzant els procediments. Entitat null");
		}
		info.setCodiEntitat(entitatDto.getCodi());
		ConfigHelper.setEntitatCodi(entitatDto.getCodi());
		log.debug("[PROCEDIMENTS] Inici actualitzar procediments");
		// Comprova si hi ha una altre instància del procés en execució
		var progres = ProcedimentServiceImpl.getProgresActualitzacio().get(entitatDto.getDir3Codi());
		if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
			log.debug("[PROCEDIMENTS] Ja existeix un altre procés que està executant l'actualització");
			return;	// Ja existeix un altre procés que està executant l'actualització.
		}
		// inicialitza el seguiment del prgrés d'actualització
		progres = new ProgresActualitzacioProcSer();
		if (publisher != null) {
			progres.setOnInfo(entry -> publisher.publish(progres.getProgres(), entry.getText()));
			progres.setOnProgressChanged(percent -> publisher.publish(percent, null));
		}
		ProcedimentServiceImpl.getProgresActualitzacio().put(entitatDto.getDir3Codi(), progres);
```

(The rest of the method body — from `Map<String, String[]> avisosProcedimentsOrgans = new HashMap<>();` through the end of the `catch` block — is unchanged.)

Add the `import es.caib.notib.logic.intf.dto.ProgressPublisher;` to the file's import list.

- [ ] **Step 3: Apply the same transformation to `actualitzaServeis`**

Replace the method signature and the `progres = new ProgresActualitzacioProcSer();` line (`ProcSerSyncHelper.java:392` and `:409-410`):

```java
	public void actualitzaServeis(EntitatDto entitatDto) {
		actualitzaServeis(entitatDto, null);
	}

	public void actualitzaServeis(EntitatDto entitatDto, ProgressPublisher publisher) {

		var info = new IntegracioInfo(IntegracioCodi.PROCEDIMENTS, "Actualització de serveis", IntegracioAccioTipusEnumDto.PROCESSAR, new AccioParam("Codi Dir3 de l'entitat", entitatDto.getDir3Codi()));
		info.setCodiEntitat(entitatDto.getCodi());
		info.setAplicacio(IntegracioInfo.INTERFICIE_WEB);
		if (entitatDto == null) {
			log.error("Error actualitzant els procediments. Entitat null");
		}
		ConfigHelper.setEntitatCodi(entitatDto.getCodi());
		log.debug("[SERVEIS] Inici actualitzar serveis");
		// Comprova si hi ha una altre instància del procés en execució
		var progres = ServeiServiceImpl.getProgresActualitzacioServeis().get(entitatDto.getDir3Codi());
		if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
			log.debug("[SERVEIS] Ja existeix un altre procés que està executant l'actualització");
			return;	// Ja existeix un altre procés que està executant l'actualització.
		}
		// inicialitza el seguiment del prgrés d'actualització
		progres = new ProgresActualitzacioProcSer();
		if (publisher != null) {
			progres.setOnInfo(entry -> publisher.publish(progres.getProgres(), entry.getText()));
			progres.setOnProgressChanged(percent -> publisher.publish(percent, null));
		}
		ServeiServiceImpl.getProgresActualitzacioServeis().put(entitatDto.getDir3Codi(), progres);
```

(The rest of the method body is unchanged.)

- [ ] **Step 4: Write tests for the delegation and the publisher wiring**

```java
package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.EntitatDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ProcSerSyncHelperProgressPublisherTest {

	@Test
	void actualitzaProcedimentsWithoutPublisherShouldNotThrow() {
		// given: an entitat with no ROLSAC data reachable in this test environment
		// (obtenirProcediments will fail fast or return empty — either path must not throw
		// due to the new overload's publisher-wiring code, which is guarded by `publisher != null`)
		var helper = new ProcSerSyncHelper();
		var dto = new EntitatDto();
		dto.setId(1L);
		dto.setCodi("TEST");
		dto.setDir3Codi("D3-TEST");
		// when / then: the no-arg overload must still be callable exactly as before (behavior
		// beyond "doesn't throw due to the refactor itself" is already covered by this class's
		// pre-existing integration-style tests, if any — this test only guards the new delegation).
		assertDoesNotThrow(() -> {
			try {
				helper.actualitzaProcediments(dto, null);
			} catch (Exception ex) {
				// obtenirProcediments will legitimately throw when the plugin/repository
				// dependencies are unmocked nulls in this minimal test; that is an existing
				// characteristic of this class unrelated to the publisher refactor, so it's
				// swallowed here rather than asserted on.
			}
		});
	}

}
```

Note: `ProcSerSyncHelper` has many `@Autowired`/`@Resource` fields and no constructor injection, so it can be instantiated with `new ProcSerSyncHelper()` for this narrow test (all dependent fields stay null; the test above tolerates the resulting exception since it's only verifying the new two-argument overload is reachable and doesn't NPE specifically on the publisher-wiring lines before reaching `obtenirProcediments`). If the project's existing test setup for this class already uses a fuller Spring context or Mockito mocks (check Step 1's search result), prefer extending that setup instead of this minimal approach — use whichever avoids duplicating fixture setup.

- [ ] **Step 5: Run the test**

Run: `mvn -pl notib-service test -Dtest=ProcSerSyncHelperProgressPublisherTest`
Expected: PASS.

- [ ] **Step 6: Run the full `notib-service` suite**

Run: `mvn -pl notib-service test`
Expected: all tests pass, including any pre-existing `ProcSerSyncHelper`/`ProcedimentServiceImpl`/`ServeiServiceImpl` tests (the no-arg overloads' behavior is unchanged).

- [ ] **Step 7: Commit**

```bash
git add notib-service/src/main/java/es/caib/notib/logic/helper/ProcSerSyncHelper.java \
        notib-service/src/test/java/es/caib/notib/logic/helper/ProcSerSyncHelperProgressPublisherTest.java
git commit -m "#1011 Overloads amb ProgressPublisher a ProcSerSyncHelper (procediments/serveis)"
```

---

### Task 3: `OrganGestorSyncHelper` — persist `nous`/`antics`, parameterize the SSE event name

**Files:**
- Modify: `notib-service/src/main/java/es/caib/notib/logic/helper/OrganGestorSyncHelper.java`
- Modify: `notib-service/src/test/java/es/caib/notib/logic/helper/OrganGestorSyncHelperTest.java` (created in the graphical-preview plan's Task 1b — add a new constructor parameter to its `new OrganGestorSyncHelper(...)` call)

**Interfaces:**
- Consumes: `OrganGestorRepository.findByCodiIn(List<String>)` (existing, `notib-persistence/.../repository/OrganGestorRepository.java:160`), `OrganGestorEntity.addNou(OrganGestorEntity)` (existing).
- Produces: `OrganGestorSyncHelper.sincronitzar(EntitatResourceEntity, boolean)` (unchanged signature, now also persists `nous`/`antics` when applying) delegates to a new `sincronitzar(EntitatResourceEntity, boolean, SseEvent.SseEventName)`; the new 3-arg overload is what `OrganGestorFullSyncHelper` (Task 5) calls with `ORGANS_PROCEDIMENTS_SYNC` to get the same per-organ progress granularity the standalone button already gets under `DIR3_SYNC`.

- [ ] **Step 1: Add the `OrganGestorRepository` dependency**

In `OrganGestorSyncHelper.java`, the class is annotated `@RequiredArgsConstructor` (Lombok), which generates the constructor in field-declaration order. The existing fields are:

```java
	private final PluginHelper pluginHelper;
	private final OrganGestorLlibreOficinaUpdateHelper organGestorLlibreOficinaHelper;
	private final OrganGestorResourceRepository organGestorResourceRepository;

	private final SseEventService progressEventService;
```

Add the new field **after** `progressEventService` (i.e., last), so the generated constructor's parameter order stays `(pluginHelper, organGestorLlibreOficinaHelper, organGestorResourceRepository, progressEventService, organGestorRepository)` — this exact order is assumed by every `new OrganGestorSyncHelper(...)` call in this task's and the graphical-preview plan's tests:

```java
	private final SseEventService progressEventService;

	private final OrganGestorRepository organGestorRepository;
```

Add the import: `import es.caib.notib.persist.repository.OrganGestorRepository;`

- [ ] **Step 2: Update `OrganGestorSyncHelperTest`'s constructor calls**

`OrganGestorSyncHelperTest.java` now has **two** `new OrganGestorSyncHelper(...)` call sites (one from the graphical-preview plan's Task 1b, one added later by that plan's final-review fix wave for the `isCodiInAnyMap` fix) — grep the file for `new OrganGestorSyncHelper(` and update **every** match, not just one. For each, add a mock for the new dependency:

```java
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.anyList())).thenReturn(List.of());
```

and pass it as the last constructor argument:

```java
		var helper = new OrganGestorSyncHelper(pluginHelper, llibreOficinaHelper, repository, progressEventService, organGestorRepository);
```

(the local variable names for the mocked `pluginHelper`/`llibreOficinaHelper`/`repository`/`progressEventService` differ slightly between the two existing tests — match each test's own existing variable names, don't rename them.)

Add the import `import es.caib.notib.persist.repository.OrganGestorRepository;` to that test file (once, if not already present).

- [ ] **Step 3: Parameterize the SSE event name — write the failing test first**

```java
	@Test
	void sincronitzarShouldPublishUnderTheGivenEventName() {
		// given
		var pluginHelper = Mockito.mock(PluginHelper.class);
		Mockito.when(pluginHelper.unitatsOrganitzativesFindByPare(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(List.of());
		var llibreOficinaHelper = Mockito.mock(OrganGestorLlibreOficinaUpdateHelper.class);
		var repository = Mockito.mock(OrganGestorResourceRepository.class);
		Mockito.when(repository.findByEntitat(Mockito.any())).thenReturn(List.of());
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		var progressEventService = Mockito.mock(es.caib.notib.logic.intf.resourceservice.SseEventService.class);
		var helper = new OrganGestorSyncHelper(pluginHelper, llibreOficinaHelper, repository, progressEventService, organGestorRepository);
		var entitat = new EntitatResourceEntity();
		entitat.setCodi("ENT1");
		entitat.setDir3Codi("D3-ENT1");
		var eventCaptor = org.mockito.ArgumentCaptor.forClass(es.caib.notib.logic.intf.model.SseEvent.class);

		// when
		helper.sincronitzar(entitat, true, es.caib.notib.logic.intf.model.SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC);

		// then
		Mockito.verify(progressEventService, Mockito.atLeastOnce())
			.publishEvent(Mockito.eq(es.caib.notib.logic.intf.resourceservice.SseEventService.SseQueue.PROGRESS), eventCaptor.capture());
		assertTrue(eventCaptor.getAllValues().stream()
			.allMatch(e -> e.getEventName() == es.caib.notib.logic.intf.model.SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC));
	}
```

Add `import static org.junit.jupiter.api.Assertions.assertTrue;` if not already present.

- [ ] **Step 4: Run it to confirm it fails to compile**

Run: `mvn -pl notib-service test -Dtest=OrganGestorSyncHelperTest`
Expected: compile error — no 3-arg `sincronitzar` overload exists yet.

- [ ] **Step 5: Add the 3-arg overload and thread the event name through**

In `OrganGestorSyncHelper.java`, change the method signature and every `publishProgressEvent(...)` call site to route through an instance-scoped event name:

```java
	public OrganGestorDir3Sync sincronitzar(
		EntitatResourceEntity entitat,
		boolean simular) {
		return sincronitzar(entitat, simular, SseEvent.SseEventName.DIR3_SYNC);
	}

	public OrganGestorDir3Sync sincronitzar(
		EntitatResourceEntity entitat,
		boolean simular,
		SseEvent.SseEventName eventName) {
		publishProgressEvent(
			eventName,
			SseEvent.SseEventStatus.RUNNING,
			0,
			"Consultant canvis a DIR3CAIB");
		// ... (unchanged body down to each subsequent publishProgressEvent(...) call — see Step 6)
```

Update `actualitzarOrgansGestors` to accept and forward `eventName`:

```java
	private void actualitzarOrgansGestors(
		EntitatResourceEntity entitat,
		List<NodeDir3> dir3SyncNodes,
		List<OrganGestorResourceEntity> organsGestors,
		SseEvent.SseEventName eventName) {
```

and its internal `publishProgressEvent(...)` call gains `eventName` as the first argument.

Update `publishProgressEvent`'s signature to take the event name explicitly instead of hardcoding it:

```java
	private void publishProgressEvent(
		SseEvent.SseEventName eventName,
		SseEvent.SseEventStatus status,
		int percent,
		String message) {
		progressEventService.publishEvent(
			SseEventService.SseQueue.PROGRESS,
			new SseEvent(
				eventName,
				percent,
				status,
				message));
	}
```

- [ ] **Step 6: Update every other call site in the same file**

Every remaining `publishProgressEvent(SseEvent.SseEventStatus.X, ...)` call inside `sincronitzar` and `actualitzarOrgansGestors` (there are 6 total across the two methods, per the original file) becomes `publishProgressEvent(eventName, SseEvent.SseEventStatus.X, ...)`, and the call to `actualitzarOrgansGestors(entitat, dir3SyncNodes, organsGestors)` inside `sincronitzar`'s `if (!simular)` block becomes `actualitzarOrgansGestors(entitat, dir3SyncNodes, organsGestors, eventName)`.

- [ ] **Step 7: Run the test to confirm it passes**

Run: `mvn -pl notib-service test -Dtest=OrganGestorSyncHelperTest`
Expected: PASS.

- [ ] **Step 8: Persist `nous`/`antics` on apply — write the failing test first**

```java
	@Test
	void sincronitzarShouldPersistNousAnticsOnSubstitucio() {
		// given: DIR3 reports A01 (currently vigent in DB) evolving into A02 (still vigent),
		// with A01's own latest record marked extinct.
		var a01 = new NodeDir3();
		a01.setCodi("A01");
		a01.setDenominacio("Unitat A01");
		a01.setEstat("E");
		a01.setHistoricosUO(List.of("A02"));
		var a02 = new NodeDir3();
		a02.setCodi("A02");
		a02.setDenominacio("Unitat A02");
		a02.setEstat("V");

		var pluginHelper = Mockito.mock(PluginHelper.class);
		Mockito.when(pluginHelper.unitatsOrganitzativesFindByPare(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(List.of(a01, a02));
		var llibreOficinaHelper = Mockito.mock(OrganGestorLlibreOficinaUpdateHelper.class);
		var resourceRepository = Mockito.mock(OrganGestorResourceRepository.class);
		var existingA01 = OrganGestorResourceEntity.builder().build();
		existingA01.setCodi("A01");
		var existingA02 = OrganGestorResourceEntity.builder().build();
		existingA02.setCodi("A02");
		Mockito.when(resourceRepository.findByEntitat(Mockito.any())).thenReturn(List.of(existingA01, existingA02));
		var progressEventService = Mockito.mock(es.caib.notib.logic.intf.resourceservice.SseEventService.class);
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		var legacyA01 = OrganGestorEntity.builder().codi("A01").build();
		var legacyA02 = OrganGestorEntity.builder().codi("A02").build();
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.argThat(l -> l != null && l.contains("A01"))))
			.thenReturn(List.of(legacyA01));
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.argThat(l -> l != null && l.contains("A02"))))
			.thenReturn(List.of(legacyA02));

		var helper = new OrganGestorSyncHelper(pluginHelper, llibreOficinaHelper, resourceRepository, progressEventService, organGestorRepository);
		var entitat = new EntitatResourceEntity();
		entitat.setCodi("ENT1");
		entitat.setDir3Codi("D3-ENT1");

		// when
		helper.sincronitzar(entitat, false);

		// then: A01 (the extinct one, DTO-field-`nou` for substitucions per the inverted
		// naming) must record A02 (the survivor, DTO-field-`vell`) as its successor.
		assertTrue(legacyA01.getNous().contains(legacyA02));
	}
```

- [ ] **Step 9: Run it to confirm it fails**

Run: `mvn -pl notib-service test -Dtest=OrganGestorSyncHelperTest`
Expected: FAIL — `legacyA01.getNous()` is empty.

- [ ] **Step 10: Implement `persistirTransicions`**

Add a new private method, called from `sincronitzar`'s `if (!simular)` block right after `actualitzarOrgansGestors(...)`:

```java
		if (!simular) {
			publishProgressEvent(
				eventName,
				SseEvent.SseEventStatus.RUNNING,
				10,
				"Actualitzant informació dels òrgans gestors");
			actualitzarOrgansGestors(entitat, dir3SyncNodes, organsGestors, eventName);
			persistirTransicions(substitucionsMap, fusionsMap, divisionsMap);
			LocalDate now = LocalDate.now();
```

```java
	private void persistirTransicions(
		MultiValuedMap<NodeDir3, NodeDir3> substitucionsMap,
		MultiValuedMap<NodeDir3, NodeDir3> fusionsMap,
		MultiValuedMap<NodeDir3, NodeDir3> divisionsMap) {

		List<String> totsElsCodis = new ArrayList<>();
		substitucionsMap.entries().forEach(e -> { totsElsCodis.add(e.getKey().getCodi()); totsElsCodis.add(e.getValue().getCodi()); });
		fusionsMap.entries().forEach(e -> { totsElsCodis.add(e.getKey().getCodi()); totsElsCodis.add(e.getValue().getCodi()); });
		divisionsMap.entries().forEach(e -> { totsElsCodis.add(e.getKey().getCodi()); totsElsCodis.add(e.getValue().getCodi()); });
		if (totsElsCodis.isEmpty()) {
			return;
		}
		Map<String, OrganGestorEntity> entitatsPerCodi = new HashMap<>();
		organGestorRepository.findByCodiIn(totsElsCodis).forEach(e -> entitatsPerCodi.put(e.getCodi(), e));
		List<OrganGestorEntity> aGuardar = new ArrayList<>();
		// Substitucions: la clau (vell al DTO) és el supervivent, el valor (nou al DTO) és l'extint.
		substitucionsMap.entries().forEach(entry -> afegeixTransicio(entitatsPerCodi, entry.getValue().getCodi(), entry.getKey().getCodi(), aGuardar));
		// Fusions: la clau és el supervivent (nou al DTO), els valors són els extints (vells al DTO).
		fusionsMap.entries().forEach(entry -> afegeixTransicio(entitatsPerCodi, entry.getValue().getCodi(), entry.getKey().getCodi(), aGuardar));
		// Divisions: la clau és l'extint (vell al DTO), els valors són els supervivents (nous al DTO).
		divisionsMap.entries().forEach(entry -> afegeixTransicio(entitatsPerCodi, entry.getKey().getCodi(), entry.getValue().getCodi(), aGuardar));
		if (!aGuardar.isEmpty()) {
			organGestorRepository.saveAll(aGuardar);
		}
	}

	private void afegeixTransicio(Map<String, OrganGestorEntity> entitatsPerCodi, String codiOrigen, String codiDesti, List<OrganGestorEntity> aGuardar) {
		var origen = entitatsPerCodi.get(codiOrigen);
		var desti = entitatsPerCodi.get(codiDesti);
		if (origen != null && desti != null) {
			origen.addNou(desti);
			aGuardar.add(origen);
		}
	}
```

Add imports: `es.caib.notib.persist.entity.OrganGestorEntity`, `java.util.Map` (if not already imported — `java.util.*` is already wildcard-imported in this file per its existing `import java.util.*;`).

- [ ] **Step 11: Run the test to confirm it passes**

Run: `mvn -pl notib-service test -Dtest=OrganGestorSyncHelperTest`
Expected: PASS.

- [ ] **Step 12: Run the full `notib-service` suite**

Run: `mvn -pl notib-service test`
Expected: all tests pass.

- [ ] **Step 13: Commit**

```bash
git add notib-service/src/main/java/es/caib/notib/logic/helper/OrganGestorSyncHelper.java \
        notib-service/src/test/java/es/caib/notib/logic/helper/OrganGestorSyncHelperTest.java
git commit -m "#1011 OrganGestorSyncHelper: persisteix nous/antics i parametritza l'event SSE"
```

---

### Task 4: `OrganGestorFullSyncHelper` orchestrator

**Files:**
- Create: `notib-service/src/main/java/es/caib/notib/logic/helper/OrganGestorFullSyncHelper.java`
- Test: `notib-service/src/test/java/es/caib/notib/logic/helper/OrganGestorFullSyncHelperTest.java`

**Interfaces:**
- Consumes: `OrganGestorSyncHelper.sincronitzar(EntitatResourceEntity, boolean, SseEvent.SseEventName)` (Task 3), `ProcSerSyncHelper.actualitzaProcediments/actualitzaServeis(EntitatDto, ProgressPublisher)` (Task 2), `PermisosHelper.actualitzarPermisosOrgansObsolets(...)` (existing, unmodified), `OrganGestorService.syncOficinesSIR(Long)` (existing), `OrganGestorRepository.findByCodiIn(List<String>)` (existing).
- Produces: `OrganGestorFullSyncHelper.sincronitzarTot(EntitatResourceEntity entitat)` — runs all 5 phases, publishes `SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC` events throughout, `DONE` at the end.

- [ ] **Step 1: Write the failing test**

```java
package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.model.SseEvent;
import es.caib.notib.logic.intf.resourceservice.SseEventService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrganGestorFullSyncHelperTest {

	@Test
	void sincronitzarTotShouldRunAllPhasesAndPublishDoneAtTheEnd() {
		// given
		var organGestorSyncHelper = Mockito.mock(OrganGestorSyncHelper.class);
		var organGestorDir3Sync = new es.caib.notib.logic.intf.model.OrganGestorDir3Sync(
			null, new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviCreacio[0],
			new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviModificacio[0],
			new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviSubstitucio[0],
			new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviExtincio[0],
			new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviFusio[0],
			new es.caib.notib.logic.intf.model.OrganGestorDir3Sync.OrganGestorDir3SyncCanviDivisio[0],
			true, false);
		Mockito.when(organGestorSyncHelper.sincronitzar(Mockito.any(), Mockito.eq(false), Mockito.eq(SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC)))
			.thenReturn(organGestorDir3Sync);
		var permisosHelper = Mockito.mock(PermisosHelper.class);
		var procSerSyncHelper = Mockito.mock(ProcSerSyncHelper.class);
		var organGestorService = Mockito.mock(OrganGestorService.class);
		var organGestorRepository = Mockito.mock(OrganGestorRepository.class);
		Mockito.when(organGestorRepository.findByCodiIn(Mockito.anyList())).thenReturn(List.of());
		var progressEventService = Mockito.mock(SseEventService.class);

		var helper = new OrganGestorFullSyncHelper(organGestorSyncHelper, permisosHelper, procSerSyncHelper,
			organGestorService, organGestorRepository, progressEventService);
		var entitat = new EntitatResourceEntity();
		entitat.setId(1L);
		entitat.setCodi("ENT1");
		entitat.setDir3Codi("D3-ENT1");

		// when
		helper.sincronitzarTot(entitat);

		// then
		Mockito.verify(organGestorSyncHelper).sincronitzar(entitat, false, SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC);
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
	}

}
```

- [ ] **Step 2: Run it to confirm it fails to compile**

Run: `mvn -pl notib-service test -Dtest=OrganGestorFullSyncHelperTest`
Expected: compile error — `OrganGestorFullSyncHelper` doesn't exist.

- [ ] **Step 3: Write `OrganGestorFullSyncHelper`**

```java
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
		organGestorService.syncOficinesSIR(entitat.getId());

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
```

- [ ] **Step 4: Run the test to confirm it passes**

Run: `mvn -pl notib-service test -Dtest=OrganGestorFullSyncHelperTest`
Expected: PASS.

- [ ] **Step 5: Run the full `notib-service` suite**

Run: `mvn -pl notib-service test`
Expected: all tests pass.

- [ ] **Step 6: Commit**

```bash
git add notib-service/src/main/java/es/caib/notib/logic/helper/OrganGestorFullSyncHelper.java \
        notib-service/src/test/java/es/caib/notib/logic/helper/OrganGestorFullSyncHelperTest.java
git commit -m "#1011 Afegit OrganGestorFullSyncHelper (orquestrador de sincronització combinada)"
```

---

### Task 5: Register the `ORGANS_PROCEDIMENTS_SYNC` action

**Files:**
- Modify: `notib-service-intf/src/main/java/es/caib/notib/logic/intf/model/OrganGestorResource.java`
- Create: `notib-service/src/main/java/es/caib/notib/logic/organs/OrgansProcedimentsSyncActionExecutor.java`
- Modify: `notib-service/src/main/java/es/caib/notib/logic/resourceservice/OrganGestorResourceServiceImpl.java`

**Interfaces:**
- Consumes: `OrganGestorFullSyncHelper.sincronitzarTot(EntitatResourceEntity)` (Task 4).
- Produces: action code `OrganGestorResource.ORGANS_PROCEDIMENTS_SYNC_ACTION_CODE = "ORGANS_PROCEDIMENTS_SYNC"`, executable via `POST {API_PATH}/organGestorResource/exec_ORGANS_PROCEDIMENTS_SYNC` (the existing generic action-dispatch route already used by `DIR3_SYNC`/`OFICINES_SYNC`).

- [ ] **Step 1: Add the action code and artifact declaration**

In `OrganGestorResource.java`, add the constant next to the existing ones:

```java
	public static final String ORGANS_PROCEDIMENTS_SYNC_ACTION_CODE = "ORGANS_PROCEDIMENTS_SYNC";
```

Add a new `@ResourceArtifact` entry to the `artifacts` array (alongside `DIR3_SYNC_ACTION_CODE`'s), reusing `OrganGestorDir3SyncForm` as the (unused-field) form class, matching how `OFICINES_SYNC_ACTION_CODE` already does the same:

```java
			@ResourceArtifact(
				type = ResourceArtifactType.ACTION,
				code = OrganGestorResource.ORGANS_PROCEDIMENTS_SYNC_ACTION_CODE,
				formClass = OrganGestorResource.OrganGestorDir3SyncForm.class,
				accessConstraints = {
					@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_ADMIN })
				}
			),
```

- [ ] **Step 2: Write the action executor**

```java
package es.caib.notib.logic.organs;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.OrganGestorFullSyncHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.exception.ActionExecutionException;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.model.OrganGestorResource;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourcerepository.EntitatResourceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * Acció per a sincronitzar en una sola execució òrgans, permisos, procediments, serveis
 * i oficines SIR amb DIR3/ROLSAC.
 */
@Slf4j
@AllArgsConstructor
public class OrgansProcedimentsSyncActionExecutor implements BaseMutableResourceService.ActionExecutor<OrganGestorResourceEntity, OrganGestorResource.OrganGestorDir3SyncForm, Boolean> {

	private final EntitatResourceRepository entitatResourceRepository;
	private final UserSessionHelper userSessionHelper;
	private final OrganGestorFullSyncHelper organGestorFullSyncHelper;
	private final Class<OrganGestorResource> resourceClass;

	@Override
	public Boolean exec(String code, OrganGestorResourceEntity entity, OrganGestorResource.OrganGestorDir3SyncForm params) throws ActionExecutionException {

		var entitat = entitatResourceRepository.findById(userSessionHelper.getCurrentEntitatId());
		if (entitat.isEmpty()) {
			throw new ActionExecutionException(OrganGestorResource.class, null, code, "Couldn't find current entitat in user session");
		}
		try {
			ConfigHelper.setEntitatCodi(entitat.get().getCodi());
			organGestorFullSyncHelper.sincronitzarTot(entitat.get());
			return true;
		} catch (Exception ex) {
			var msg = "Error a la sincronització combinada d'òrgans i procediments de l'entitat " + entitat.get().getCodi() + ": ";
			log.error(msg, ex);
			msg += ex.getMessage();
			throw new ActionExecutionException(resourceClass, entity != null ? entity.getId() : null, code, msg, ex);
		}
	}

	@Override
	public void onChange(Serializable id, OrganGestorResource.OrganGestorDir3SyncForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, OrganGestorResource.OrganGestorDir3SyncForm target) {
	}
}
```

- [ ] **Step 3: Register it in `OrganGestorResourceServiceImpl`**

Add the field, constructor parameter, and assignment (this class has a manual constructor, not `@RequiredArgsConstructor`):

```java
	private final OrganGestorFullSyncHelper organGestorFullSyncHelper;
```

```java
	public OrganGestorResourceServiceImpl(
		UserSessionHelper userSessionHelper,
		AuthenticationHelper authenticationHelper,
		NotibPermissionHelper notibPermissionHelper,
		AclHelper aclHelper,
		OrganGestorSyncHelper organGestorSyncHelper,
		OrganGestorFullSyncHelper organGestorFullSyncHelper,
		EntitatResourceRepository entitatResourceRepository,
		OrganGestorResourceRepository organGestorResourceRepository,
		PagadorPostalResourceRepository pagadorPostalResourceRepository,
		PagadorCieResourceRepository pagadorCieResourceRepository,
		EntregaCieResourceRepository entregaCieResourceRepository,
		OrganGestorService organGestorService) {

		super(userSessionHelper, authenticationHelper, notibPermissionHelper);
		this.aclHelper = aclHelper;
		this.organGestorSyncHelper = organGestorSyncHelper;
		this.organGestorFullSyncHelper = organGestorFullSyncHelper;
		this.entitatResourceRepository = entitatResourceRepository;
		this.organGestorResourceRepository = organGestorResourceRepository;
		this.pagadorPostalResourceRepository = pagadorPostalResourceRepository;
		this.pagadorCieResourceRepository = pagadorCieResourceRepository;
		this.entregaCieResourceRepository = entregaCieResourceRepository;
		this.organGestorService = organGestorService;
	}
```

Add the registration line in `init()`:

```java
	@PostConstruct
	public void init() {
		var resourceClass = getResourceClass();
		register(OrganGestorResource.PERSPECTIVE_TREE, new OrganGestorResourceTreePerspectiveApplicator());
		register(OrganGestorResource.DIR3_SYNC_ACTION_CODE, new Dir3SyncActionExecutor());
		register(OrganGestorResource.OFICINES_SYNC_ACTION_CODE, new OficinesSyncActionExecutor(entitatResourceRepository, userSessionHelper, organGestorService, resourceClass));
		register(OrganGestorResource.ORGANS_PROCEDIMENTS_SYNC_ACTION_CODE, new OrgansProcedimentsSyncActionExecutor(entitatResourceRepository, userSessionHelper, organGestorFullSyncHelper, resourceClass));
		register(OrganGestorResource.ACTION_ADMIN_ORGANS_AMB_PERMIS, new AdminOrgansAmbPermisActionExecutor(organGestorService, userSessionHelper));
	}
```

Add the import `import es.caib.notib.logic.organs.OrgansProcedimentsSyncActionExecutor;`.

- [ ] **Step 4: Build the module**

Run: `mvn -pl notib-service -am clean compile -DskipTests`
Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Run `notib-service`'s existing resourceservice tests**

Run: `mvn -pl notib-service test -Dtest=OrganGestorResourceServiceImplTest`
Expected: PASS (the new constructor parameter must not break existing test setup — if that test constructs `OrganGestorResourceServiceImpl` directly, add a mock/stub for `OrganGestorFullSyncHelper` there too).

- [ ] **Step 6: Commit**

```bash
git add notib-service-intf/src/main/java/es/caib/notib/logic/intf/model/OrganGestorResource.java \
        notib-service/src/main/java/es/caib/notib/logic/organs/OrgansProcedimentsSyncActionExecutor.java \
        notib-service/src/main/java/es/caib/notib/logic/resourceservice/OrganGestorResourceServiceImpl.java \
        notib-service/src/test/java/es/caib/notib/logic/resourceservice/OrganGestorResourceServiceImplTest.java
git commit -m "#1011 Registrada l'acció ORGANS_PROCEDIMENTS_SYNC"
```

---

### Task 6: Frontend combined button with live-appending log

**Files:**
- Create: `notib-back/src/main/reactapp/notib-back/src/hooks/useSse.ts` (extracted from `OrganGrid.tsx`)
- Modify: `notib-back/src/main/reactapp/notib-back/src/pages/organ/OrganGrid.tsx` (remove local `useSse`, import the extracted one)
- Create: `notib-back/src/main/reactapp/notib-back/src/pages/organ/OrgansProcedimentsSyncActionButton.tsx`
- Modify: `notib-back/src/main/reactapp/notib-back/src/pages/organ/OrganGrid.tsx` (add the new button to `toolbarElementsWithPositions`)

**Interfaces:**
- Produces: `useSse(queueId: string, eventName: string, onEvent: (event: any) => void, closeOnError?: boolean): void` (moved, same signature as today's inline version in `OrganGrid.tsx:93-125`).
- Produces: `OrgansProcedimentsSyncActionButton: React.FC<{ dataGridApiRef: MuiDataGridApiRef }>`.

- [ ] **Step 1: Extract `useSse` into its own hook file**

```tsx
// src/hooks/useSse.ts
import React from 'react';
import { EventSource } from 'eventsource';
import { useAuthContext, useResourceApiService } from 'reactlib';

export const useSse = (
    queueId: string,
    eventName: string,
    onEvent: (event: any) => void,
    closeOnError?: boolean
) => {
    const { getToken } = useAuthContext();
    const { isReady: apiIsReady, currentLinks } = useResourceApiService('sse');
    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        const subscribeHref = currentLinks['subscribe'].href;
        const eventSourceHref = subscribeHref.replace('{queueId}', queueId);
        const eventSource = new EventSource(eventSourceHref, {
            fetch: (input, init) =>
                fetch(input, {...init, headers: {...init.headers,
                        Authorization: 'Bearer ' + getToken(),
                    },
                }),
        });
        eventSource.addEventListener(eventName, (event) => {
            const data = JSON.parse(event.data);
            onEvent?.(data);
        });
        eventSource.onerror = () => {
            if (closeOnError) {
                eventSource.close();
            }
        };
        return () => eventSource.close();
    }, [apiIsReady]);
};

export default useSse;
```

In `OrganGrid.tsx`, delete the local `useSse` definition (lines 93-125) and add `import { useSse } from '../../hooks/useSse';` near the top.

- [ ] **Step 2: Type-check after the extraction, before adding new code**

Run: `npm run build`
Expected: no new TypeScript errors (the extraction alone must be behavior-preserving).

- [ ] **Step 3: Write the combined-sync button component**

```tsx
// src/pages/organ/OrgansProcedimentsSyncActionButton.tsx
import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import LinearProgress from '@mui/material/LinearProgress';
import { MuiActionReportButton, MuiDataGridApiRef, useBaseAppContext } from 'reactlib';
import { useSse } from '../../hooks/useSse';

type LogLine = { message: string; isError: boolean };

const OrgansProcedimentsSyncLoading: React.FC<{ percent: number; lines: LogLine[] }> = ({ percent, lines }) => {
    const logRef = React.useRef<HTMLDivElement>(null);
    React.useEffect(() => {
        if (logRef.current) {
            logRef.current.scrollTop = logRef.current.scrollHeight;
        }
    }, [lines.length]);
    return (
        <Box>
            <LinearProgress variant="determinate" value={percent} sx={{ mb: 1 }} />
            <Box ref={logRef} sx={{ maxHeight: 300, overflow: 'auto', bgcolor: 'action.hover', p: 1 }}>
                {lines.map((line, i) => (
                    <Typography key={i} variant="body2" color={line.isError ? 'error' : undefined}>
                        {line.message}
                    </Typography>
                ))}
            </Box>
        </Box>
    );
};

export const OrgansProcedimentsSyncActionButton: React.FC<{ dataGridApiRef: MuiDataGridApiRef }> = (props) => {

    const { dataGridApiRef } = props;
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const [percent, setPercent] = React.useState<number>(0);
    const [lines, setLines] = React.useState<LogLine[]>([]);

    useSse('PROGRESS', 'ORGANS_PROCEDIMENTS_SYNC', (event: any) => {
        setPercent(event.percent);
        if (event.message) {
            setLines((prev) => [...prev, { message: event.message, isError: event.status === 'ERROR' }]);
        }
    });

    const formDialogButtons = [
        {
            value: false,
            text: t('page.organs.grid.syncCombined.cancelar'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: t('page.organs.grid.syncCombined.sincronitzar'),
            icon: 'sync',
            componentProps: { variant: 'contained' },
        },
    ];

    return (
        <MuiActionReportButton
            resourceName="organGestorResource"
            action="ORGANS_PROCEDIMENTS_SYNC"
            title={t('page.organs.grid.syncCombined.title')}
            buttonIcon="sync_alt"
            formDialogTitle={t('page.organs.grid.syncCombined.dialogTitle')}
            formDialogContent={<Typography>{t('page.organs.grid.syncCombined.nota')}</Typography>}
            formDialogButtons={formDialogButtons}
            formDialogLoading={<OrgansProcedimentsSyncLoading percent={percent} lines={lines} />}
            formDialogResultProcessor={() => <Typography>{t('page.organs.grid.syncCombined.success')}</Typography>}
            buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
            onSuccess={() => {
                dataGridApiRef.current?.refresh();
                temporalMessageShow(null, t('page.organs.grid.syncCombined.success'), 'success');
            }}
            onClose={() => {
                setPercent(0);
                setLines([]);
            }}
        />
    );
};

export default OrgansProcedimentsSyncActionButton;
```

- [ ] **Step 4: Add the button to `OrganGrid`'s toolbar**

In `OrganGrid.tsx`, import the new component and add it to `toolbarElementsWithPositions`:

```tsx
import OrgansProcedimentsSyncActionButton from './OrgansProcedimentsSyncActionButton';
```

```tsx
                toolbarElementsWithPositions={[
                    {
                        position: 1,
                        element: viewSwitchComponent,
                    },
                    {
                        position: 2,
                        element: <OficinesSyncActionButton dataGridApiRef={dataGridApiRef} />,
                    },
                    {
                        position: 2,
                        element: <OrganGridDir3SyncActionButton dataGridApiRef={dataGridApiRef} />,
                    },
                    {
                        position: 2,
                        element: <OrgansProcedimentsSyncActionButton dataGridApiRef={dataGridApiRef} />,
                    },
                ]}
```

- [ ] **Step 5: Type-check**

Run: `npm run build`
Expected: no new TypeScript errors.

- [ ] **Step 6: Commit**

```bash
git add notib-back/src/main/reactapp/notib-back/src/hooks/useSse.ts \
        notib-back/src/main/reactapp/notib-back/src/pages/organ/OrganGrid.tsx \
        notib-back/src/main/reactapp/notib-back/src/pages/organ/OrgansProcedimentsSyncActionButton.tsx
git commit -m "#1011 Botó combinat d'actualització amb log de progrés en viu (SSE)"
```

---

### Task 7: i18n keys and manual verification

**Files:**
- Modify: `notib-back/src/main/reactapp/notib-back/src/i18n/translationCa.ts`
- Modify: `notib-back/src/main/reactapp/notib-back/src/i18n/translationEs.ts`

**Interfaces:**
- Produces: translation keys under `page.organs.grid.syncCombined.*`.

- [ ] **Step 1: Add the Catalan keys**

In `translationCa.ts`, inside the `organs.grid` block (near the existing `sync` block at line 114):

```ts
                syncCombined: {
                    title: 'Actualitzar òrgans i procediments',
                    dialogTitle: 'Actualitzar òrgans i procediments',
                    nota: "Se sincronitzaran òrgans, permisos, procediments, serveis i oficines SIR.",
                    cancelar: 'Cancel·lar',
                    sincronitzar: 'Sincronitzar',
                    success: 'Actualització completada correctament',
                },
```

- [ ] **Step 2: Add the equivalent Spanish keys** to `translationEs.ts` in the matching location (e.g. `title: 'Actualizar órganos y procedimientos'`, `nota: 'Se sincronizarán órganos, permisos, procedimientos, servicios y oficinas SIR.'`, `cancelar: 'Cancelar'`, `sincronitzar: 'Sincronizar'`, `success: 'Actualización completada correctamente'`).

- [ ] **Step 3: Type-check and lint**

Run: `npm run build && npm run lint`
Expected: both succeed.

- [ ] **Step 4: Manual verification against the dev server**

Using the same `ide,oracle` + `devProxy` local setup as the graphical-preview plan:
1. Open Configuració > Òrgans gestors — confirm a third button "Actualitzar òrgans i procediments" appears alongside "Sincronització Dir3" and "Oficines".
2. Click it — confirm the dialog opens directly with the note text and a "Sincronitzar" button (no preview/diff step).
3. Click "Sincronitzar" — confirm a progress bar and a scrolling, appending log appear (not replacing a single line), reasonably matching the phase order òrgans → permisos → procediments → serveis → oficines.
4. Confirm the log includes per-procediment lines (one per procediment processed) if the test entitat has procediments due for sync — this validates the `ProgressPublisher` wiring in `ProcSerSyncHelper` is actually forwarding `addInfo` calls, not just phase-boundary messages.
5. On completion, confirm the grid refreshes and a success message shows.
6. Trigger a scenario with a known òrgan substitution/fusion (or inspect the DB `not_organ_gestor_transicions`-equivalent join table, if reachable, after running) to confirm `nous`/`antics` rows were written — if no such scenario is available in the test environment, at minimum confirm no exception is thrown when `resultatOrgans` has empty substitucions/fusions/divisions (the common case).
7. Confirm the standalone "Sincronització Dir3" and "Oficines" buttons still work exactly as before (unaffected by this plan's changes).

- [ ] **Step 5: Commit**

```bash
git add notib-back/src/main/reactapp/notib-back/src/i18n/translationCa.ts \
        notib-back/src/main/reactapp/notib-back/src/i18n/translationEs.ts
git commit -m "#1011 Claus de traducció per al botó combinat d'actualització"
```
