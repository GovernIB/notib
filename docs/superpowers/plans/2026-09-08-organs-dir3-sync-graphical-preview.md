# Òrgans DIR3 Sync Graphical Preview Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the numeric-counts preview in the "Sincronització Dir3" modal (òrgans) with a graphical box/connector rendering matching the legacy JSP, auto-fetched on dialog open, with Sincronitzar / Descarregar òrgans JSON / Descarrega PDF buttons.

**Architecture:** Port `horizontal-tree.css`'s pure-CSS connector technique into the React app as a small reusable `Dir3SyncBranch` component set, driven entirely by the existing `OrganGestorDir3Sync` DTO (no new backend diff logic). Add one small dedicated REST controller for the JSON download, reusing the existing `OrganGestorService.getJsonOrgansGestorDir3` service method unchanged. The dialog's existing `dialogAutoSubmit` mechanism (`onReady` triggers the query automatically) is reused as-is for the auto-fetch-on-open requirement — no changes to the shared `ActionReportButton`/`FormDialog` components.

**Tech Stack:** React 18 + TypeScript, MUI, `reactlib` (local component library under `lib/`), Spring Boot REST (`notib-service`, `notib-backend`), JUnit 5.

**Spec:** `docs/superpowers/specs/2026-09-08-organs-procediments-sync-ux-design.md` (Component 1 section)

## Global Constraints

- Reuse `OrganGestorDir3Sync` (`notib-service-intf/.../model/OrganGestorDir3Sync.java`) as-is — no new fields, no backend diff-logic changes.
- No Bootstrap dependency in the React app — port `horizontal-tree.css`'s Bootstrap-derived color classes (`bg-danger`/`bg-success`/`bg-warning`) as plain CSS using the same hex values already present in the legacy stylesheet (`border-green: #d6e9c6`, `border-red: #ebccd1`, `border-yellow: #faebcc`).
- No frontend automated test infrastructure exists in this project (no `.test.tsx`/`.test.ts` files) — verification is manual (dev server) plus `npm run build` (type-check) and `npm run lint`. Do not introduce a new test framework.
- Backend tests: JUnit 5, no Mockito needed for pure-logic classes (see `SseEventServiceImplTest.java` for the house style — plain instantiation, `@BeforeEach`, given/when/then comments).
- New action codes/REST endpoints require `BaseConfig.ROLE_ADMIN` (matching `DIR3_SYNC_ACTION_CODE`'s existing `@ResourceAccessConstraint`).

---

### Task 1: Port `horizontal-tree.css` and build the `Dir3SyncBranch` component

**Files:**
- Create: `notib-back/src/main/reactapp/notib-back/src/pages/organ/dir3SyncTree.css`
- Create: `notib-back/src/main/reactapp/notib-back/src/pages/organ/Dir3SyncBranch.tsx`

**Interfaces:**
- Produces: `Dir3SyncBranch` component — `type Dir3SyncNode = { codi?: string; nom: string }`, `type Dir3SyncNodeColor = 'red' | 'green' | 'yellow'`, props `{ orientation: 'left' | 'right'; root: Dir3SyncNode | null; rootColor: Dir3SyncNodeColor; rootPlaceholder?: 'create'; leaves: { node: Dir3SyncNode | null; color: Dir3SyncNodeColor; placeholder?: 'remove' }[] }`.

- [ ] **Step 1: Write the CSS file**

```css
/* dir3SyncTree.css — ported from notib-back/src/main/webapp/css/horizontal-tree.css,
   #wrapper id replaced by .dir3-wrapper class (multiple instances render per page),
   Bootstrap bg-*/border-* classes replaced by local dir3- equivalents (same hex values). */

.dir3-horizontal-left .dir3-wrapper {
  position: relative;
}
.dir3-horizontal-left .dir3-branch {
  position: relative;
  margin-left: 380px;
}
.dir3-horizontal-left .dir3-branch:before {
  content: "";
  width: 50px;
  border-top: 2px solid #eee9dc;
  position: absolute;
  left: -100px;
  top: 50%;
  margin-top: 1px;
}
.dir3-horizontal-left .dir3-entry {
  position: relative;
  min-height: 44px;
}
.dir3-horizontal-left .dir3-entry:before {
  content: "";
  height: 100%;
  border-left: 2px solid #eee9dc;
  position: absolute;
  left: -50px;
}
.dir3-horizontal-left .dir3-entry:after {
  content: "";
  width: 50px;
  border-top: 2px solid #eee9dc;
  position: absolute;
  left: -50px;
  top: 50%;
  margin-top: 1px;
}
.dir3-horizontal-left .dir3-entry:first-child:before {
  width: 10px;
  height: 50%;
  top: 50%;
  margin-top: 2px;
  border-radius: 10px 0 0 0;
}
.dir3-horizontal-left .dir3-entry:first-child:after {
  height: 10px;
  border-radius: 10px 0 0 0;
}
.dir3-horizontal-left .dir3-entry:last-child:before {
  width: 10px;
  height: 50%;
  border-radius: 0 0 0 10px;
}
.dir3-horizontal-left .dir3-entry:last-child:after {
  height: 10px;
  border-top: none;
  border-bottom: 2px solid #eee9dc;
  border-radius: 0 0 0 10px;
  margin-top: -9px;
}
.dir3-horizontal-left .dir3-entry.dir3-sole:before {
  display: none;
}
.dir3-horizontal-left .dir3-entry.dir3-sole:after {
  width: 50px;
  height: 0;
  margin-top: 1px;
  border-radius: 0;
}
.dir3-horizontal-right .dir3-wrapper {
  position: relative;
}
.dir3-horizontal-right .dir3-branch {
  position: relative;
  margin-right: 400px;
}
.dir3-horizontal-right .dir3-branch:before {
  content: "";
  width: 52px;
  border-top: 2px solid #eee9dc;
  position: absolute;
  right: -100px;
  top: 50%;
  margin-top: 1px;
}
.dir3-horizontal-right .dir3-entry {
  position: relative;
  min-height: 44px;
}
.dir3-horizontal-right .dir3-entry:before {
  content: "";
  height: 100%;
  border-right: 2px solid #eee9dc;
  position: absolute;
  right: -50px;
}
.dir3-horizontal-right .dir3-entry:after {
  content: "";
  width: 50px;
  border-top: 2px solid #eee9dc;
  position: absolute;
  right: -50px;
  top: 50%;
  margin-top: 1px;
}
.dir3-horizontal-right .dir3-entry:first-child:before {
  width: 10px;
  height: 50%;
  top: 50%;
  margin-top: 2px;
  border-radius: 0 10px 0 0;
}
.dir3-horizontal-right .dir3-entry:first-child:after {
  height: 10px;
  border-radius: 0 10px 0 0;
}
.dir3-horizontal-right .dir3-entry:last-child:before {
  width: 10px;
  height: 50%;
  border-radius: 0 0 10px 0;
}
.dir3-horizontal-right .dir3-entry:last-child:after {
  height: 10px;
  border-top: none;
  border-bottom: 2px solid #eee9dc;
  border-radius: 0 0 10px 0;
  margin-top: -9px;
}
.dir3-horizontal-right .dir3-entry.dir3-sole:before {
  display: none;
}
.dir3-horizontal-right .dir3-entry.dir3-sole:after {
  width: 50px;
  height: 0;
  margin-top: 1px;
  border-radius: 0;
}
.dir3-label {
  display: inline-block;
  min-width: 150px;
  max-width: 330px;
  padding: 5px 10px;
  line-height: 20px;
  text-align: center;
  border: 2px solid;
  border-radius: 5px;
  position: absolute;
  top: 50%;
  margin-top: -15px;
  color: #000;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.dir3-horizontal-left .dir3-label.dir3-root { left: 0; }
.dir3-horizontal-right .dir3-label.dir3-root { right: 0; }
.dir3-bg-green { background: #dff0d8; border-color: #d6e9c6 !important; }
.dir3-bg-red { background: #f2dede; border-color: #ebccd1 !important; }
.dir3-bg-yellow { background: #fcf8e3; border-color: #faebcc !important; }
.dir3-label.dir3-placeholder {
  height: 30px;
  width: 30px;
  min-width: 30px;
  max-width: 30px;
  border-radius: 15px;
  background: #fff;
}
.dir3-section {
  margin-bottom: 24px;
}
.dir3-section-title {
  font-weight: bold;
  border-bottom: 1px solid #ccc;
  padding-bottom: 4px;
  margin-bottom: 12px;
}
```

- [ ] **Step 2: Write the `Dir3SyncBranch` component**

```tsx
import React from 'react';
import './dir3SyncTree.css';

export type Dir3SyncNode = { codi?: string; nom: string };
export type Dir3SyncNodeColor = 'red' | 'green' | 'yellow';

export type Dir3SyncBranchProps = {
    orientation: 'left' | 'right';
    root: Dir3SyncNode | null;
    rootColor: Dir3SyncNodeColor;
    leaves: { node: Dir3SyncNode | null; color: Dir3SyncNodeColor }[];
};

const colorClass = (color: Dir3SyncNodeColor) =>
    color === 'red' ? 'dir3-bg-red' : color === 'yellow' ? 'dir3-bg-yellow' : 'dir3-bg-green';

const nodeText = (node: Dir3SyncNode | null) => (node == null ? '' : `${node.codi ? node.codi + ' - ' : ''}${node.nom}`);

export const Dir3SyncBranch: React.FC<Dir3SyncBranchProps> = (props) => {
    const { orientation, root, rootColor, leaves } = props;
    const sole = leaves.length === 1;
    return (
        <div className={`dir3-horizontal-${orientation}`}>
            <div className="dir3-wrapper">
                {root == null ? (
                    <span className="dir3-label dir3-root dir3-placeholder" />
                ) : (
                    <span className={`dir3-label dir3-root ${colorClass(rootColor)}`} title={nodeText(root)}>
                        {nodeText(root)}
                    </span>
                )}
                <div className="dir3-branch">
                    {leaves.map((leaf, i) => (
                        <div key={leaf.node?.codi ?? i} className={`dir3-entry ${sole ? 'dir3-sole' : ''}`}>
                            {leaf.node == null ? (
                                <span className="dir3-label dir3-placeholder" />
                            ) : (
                                <span className={`dir3-label ${colorClass(leaf.color)}`} title={nodeText(leaf.node)}>
                                    {nodeText(leaf.node)}
                                </span>
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default Dir3SyncBranch;
```

- [ ] **Step 3: Verify it type-checks**

Run: `npm run build` (from `notib-back/src/main/reactapp/notib-back`)
Expected: no TypeScript errors related to `Dir3SyncBranch.tsx` (other pre-existing errors, if any, are out of scope).

- [ ] **Step 4: Commit**

```bash
git add notib-back/src/main/reactapp/notib-back/src/pages/organ/dir3SyncTree.css \
        notib-back/src/main/reactapp/notib-back/src/pages/organ/Dir3SyncBranch.tsx
git commit -m "#1011 Afegit component gràfic de branques per a la previsualització DIR3"
```

---

### Task 1b: Fix pre-existing bug — `divisions[].nous` is always empty

**Files:**
- Modify: `notib-service/src/main/java/es/caib/notib/logic/helper/OrganGestorSyncHelper.java:138-142`
- Test: `notib-service/src/test/java/es/caib/notib/logic/helper/OrganGestorSyncHelperTest.java` (new)

**Interfaces:**
- Consumes: nothing new.
- Produces: `divisions[].nous` in `OrganGestorDir3Sync` now correctly populated (was always `[]`).

Tracing `sincronitzar()`'s diff-building code: `divisions` is built from `divisionsMap.keySet()`, but each entry's `nous` value is read from `fusionsMap.get(key)` instead of `divisionsMap.get(key)` — two unrelated maps built from disjoint branches of the same loop (a key that ends up in `divisionsMap` never also appears in `fusionsMap`), so `divisions[].nous` is always an empty array today. This has gone unnoticed because nothing currently renders it (the existing UI only shows counts). Task 2 in this plan renders it, so it must be fixed first.

- [ ] **Step 1: Write a failing test reproducing the bug**

```java
package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.model.OrganGestorDir3Sync;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourcerepository.OrganGestorResourceRepository;
import es.caib.notib.plugin.unitat.NodeDir3;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrganGestorSyncHelperTest {

	@Test
	void divisionsShouldIncludeTheSuccessorNodes() {
		// given: DIR3 reports codi A01 (currently vigent in DB) as extinct in its latest
		// version, with two historicosUO successors A02 and A03 both vigent — a division.
		var a01Extint = new NodeDir3();
		a01Extint.setCodi("A01");
		a01Extint.setDenominacio("Unitat A01");
		a01Extint.setEstat("E");
		a01Extint.setHistoricosUO(List.of("A02", "A03"));

		var a02 = new NodeDir3();
		a02.setCodi("A02");
		a02.setDenominacio("Unitat A02");
		a02.setEstat("V");

		var a03 = new NodeDir3();
		a03.setCodi("A03");
		a03.setDenominacio("Unitat A03");
		a03.setEstat("V");

		var pluginHelper = Mockito.mock(PluginHelper.class);
		var llibreOficinaHelper = Mockito.mock(OrganGestorLlibreOficinaUpdateHelper.class);
		var repository = Mockito.mock(OrganGestorResourceRepository.class);
		var progressEventService = Mockito.mock(es.caib.notib.logic.intf.resourceservice.SseEventService.class);
		Mockito.when(pluginHelper.unitatsOrganitzativesFindByPare(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(List.of(a01Extint, a02, a03));
		var existingA01 = es.caib.notib.persist.resourceentity.OrganGestorResourceEntity.builder()
			.build();
		existingA01.setCodi("A01");
		Mockito.when(repository.findByEntitat(Mockito.any())).thenReturn(List.of(existingA01));

		var helper = new OrganGestorSyncHelper(pluginHelper, llibreOficinaHelper, repository, progressEventService);
		var entitat = new EntitatResourceEntity();
		entitat.setCodi("ENT1");
		entitat.setDir3Codi("D3-ENT1");

		// when
		OrganGestorDir3Sync result = helper.sincronitzar(entitat, true);

		// then
		assertEquals(1, result.getDivisions().length);
		assertEquals(2, result.getDivisions()[0].getNous().length);
	}

}
```

- [ ] **Step 2: Run it to confirm it fails**

Run: `mvn -pl notib-service test -Dtest=OrganGestorSyncHelperTest`
Expected: FAIL — `result.getDivisions()[0].getNous().length` is `0`, not `2`.

- [ ] **Step 3: Fix the bug**

In `OrganGestorSyncHelper.java`, line 141 (inside the `divisions` array construction):

```java
// before:
			toArbreItems(fusionsMap.get(key)))).
// after:
			toArbreItems(divisionsMap.get(key)))).
```

- [ ] **Step 4: Run the test again to confirm it passes**

Run: `mvn -pl notib-service test -Dtest=OrganGestorSyncHelperTest`
Expected: PASS.

- [ ] **Step 5: Run the full module's existing tests to confirm nothing else broke**

Run: `mvn -pl notib-service test`
Expected: all tests pass (this is a one-line fix confined to the divisions branch; no other code path reads `fusionsMap` at that call site).

- [ ] **Step 6: Commit**

```bash
git add notib-service/src/main/java/es/caib/notib/logic/helper/OrganGestorSyncHelper.java \
        notib-service/src/test/java/es/caib/notib/logic/helper/OrganGestorSyncHelperTest.java
git commit -m "#1011 Corregit bug: divisions[].nous sempre buit a OrganGestorSyncHelper"
```

---

### Task 2: Render the six diff sections using `Dir3SyncBranch`

**Files:**
- Modify: `notib-back/src/main/reactapp/notib-back/src/pages/organ/OrganGrid.tsx:164-217` (`OrganGridDir3SyncActionResults`)

**Interfaces:**
- Consumes: `Dir3SyncBranch` from Task 1 (`import Dir3SyncBranch, {Dir3SyncNode} from './Dir3SyncBranch'`).
- Consumes: `OrganGestorDir3Sync` result shape already used today — `result.creacions[].nou`, `result.modificacions[].vell/.nou`, `result.substitucions[].vell/.nou`, `result.extincions[].vell`, `result.fusions[].vells[]/.nou`, `result.divisions[].vell/.nous[]`, each item having `codi`, `nom`, `nomCooficial`, `estat` (matching `OrganGestorDir3SyncArbreItem`).

- [ ] **Step 1: Replace `OrganGridDir3SyncActionResults`**

```tsx
import Dir3SyncBranch, { Dir3SyncNode } from './Dir3SyncBranch';

const toNode = (item: any): Dir3SyncNode => ({
    codi: item.codi,
    nom: item.nomCooficial || item.nom,
});

const Dir3SyncSection: React.FC<{ title: string; children: React.ReactNode }> = ({ title, children }) => (
    <Box className="dir3-section">
        <Typography className="dir3-section-title">{title}</Typography>
        {children}
    </Box>
);

const OrganGridDir3SyncActionResults: React.FC<{ result: any }> = (props) => {

    const { result } = props;
    const { t } = useTranslation();
    if (result.senseCanvis) {
        return <Typography>{t('page.organs.grid.sync.dialogButton.senseCanvis')}</Typography>;
    }
    return (
        <Grid container>
            <Grid size={12}>
                {result.divisions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.divisions')}>
                        {result.divisions.map((d: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="left"
                                root={toNode(d.vell)}
                                rootColor="red"
                                leaves={d.nous.map((n: any) => ({ node: toNode(n), color: 'green' as const }))}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
                {result.fusions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.fusions')}>
                        {result.fusions.map((f: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="right"
                                root={toNode(f.nou)}
                                rootColor="green"
                                leaves={f.vells.map((n: any) => ({ node: toNode(n), color: 'red' as const }))}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
                {result.substitucions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.substitucions')}>
                        {/* NOTE: for substitucions (and fusions/divisions), OrganGestorSyncHelper's DTO
                            has vell/nou meaning the OPPOSITE of what the names suggest: `vell` is the
                            SURVIVING (vigent) org, `nou` is the one going EXTINCT — see
                            OrganGestorSyncHelper.java's substitucionsMap construction (key=vigent
                            successor, value=extinct code). Do not "fix" this to look like modificacions
                            (where vell=old/nou=new correctly) — it's a different field, confirmed by
                            tracing getDir3SyncNodesExistentsDarreraVersioExtincio's estat checks. */}
                        {result.substitucions.map((s: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="right"
                                root={toNode(s.vell)}
                                rootColor="green"
                                leaves={[{ node: toNode(s.nou), color: 'red' as const }]}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
                {result.modificacions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.modificacions')}>
                        {result.modificacions.map((m: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="left"
                                root={toNode(m.vell)}
                                rootColor="green"
                                leaves={[{ node: toNode(m.nou), color: 'yellow' as const }]}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
                {result.creacions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.creacions')}>
                        {result.creacions.map((c: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="left"
                                root={null}
                                rootColor="green"
                                leaves={[{ node: toNode(c.nou), color: 'green' as const }]}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
                {result.extincions?.length > 0 && (
                    <Dir3SyncSection title={t('page.organs.grid.sync.dialogButton.extincions')}>
                        {result.extincions.map((e: any, i: number) => (
                            <Dir3SyncBranch
                                key={i}
                                orientation="left"
                                root={toNode(e.vell)}
                                rootColor="red"
                                leaves={[{ node: null, color: 'red' as const }]}
                            />
                        ))}
                    </Dir3SyncSection>
                )}
            </Grid>
        </Grid>
    );
};
```

- [ ] **Step 2: Type-check**

Run: `npm run build`
Expected: no new TypeScript errors.

- [ ] **Step 3: Commit**

```bash
git add notib-back/src/main/reactapp/notib-back/src/pages/organ/OrganGrid.tsx
git commit -m "#1011 Previsualització gràfica de canvis DIR3 (branques per categoria)"
```

---

### Task 3: `REPORT_DESCARREGAR_DIR3_JSON` report artifact for the DIR3 JSON download

> **Superseded design, ruled during execution:** the original version of this
> task specified a bespoke `@RestController` in `notib-back`, directly
> injecting `AuthenticationHelper`/`UserSessionHelper` (both defined in
> `notib-service`). That does not compile under `notib-back`'s default
> `jboss` Maven profile — `notib-back/pom.xml` only declares a dependency on
> `notib-service` inside the `ide` profile block (lines ~203-226); under
> `jboss` (the profile CI/deployment actually uses), `notib-service` is not
> on `notib-back`'s classpath at all. This was caught by the Task 3
> implementer reporting BLOCKED on a real compile error, not guessed.
> The codebase already has an established, jboss-safe mechanism for exactly
> this ("generate a file, let the user download it"): the `REPORT` artifact
> type (`ResourceArtifactType.REPORT`), implemented via
> `BaseReadonlyResourceService.ReportGenerator<E, P, R>` and registered the
> same way actions are — entirely inside `notib-service`, where
> `AuthenticationHelper`/`UserSessionHelper` are always available. See
> `JusitficantEnviamentMassiuReportGenerator.java` for a working example of
> the pattern this task follows. The REPORT dispatch is already exposed
> through the same generic REST framework `DIR3_SYNC`/`OFICINES_SYNC` use
> (`ActionReportButton.tsx`'s `generateReport`/`apiArtifactReport`), so **no
> new REST controller is needed at all** — this replaces the plan's original
> Task 3 in full.

**Files:**
- Modify: `notib-service-intf/src/main/java/es/caib/notib/logic/intf/model/OrganGestorResource.java` (new artifact code + `@ResourceArtifact` entry)
- Create: `notib-service/src/main/java/es/caib/notib/logic/organs/OrganGestorDir3SyncJsonReportGenerator.java`
- Modify: `notib-service/src/main/java/es/caib/notib/logic/resourceservice/OrganGestorResourceServiceImpl.java` (register the new report generator)

**Interfaces:**
- Consumes: `OrganGestorService.getJsonOrgansGestorDir3(Long entitatId)` (existing, unchanged, `notib-service-intf/.../service/OrganGestorService.java:88`), `UserSessionHelper.getCurrentEntitatId()`, `AuthenticationHelper.isCurrentUserInRole(String)` (existing).
- Produces: `OrganGestorResource.REPORT_DESCARREGAR_DIR3_JSON` artifact code, dispatched via the existing generic REPORT mechanism (`apiArtifactReport`/`generate_REPORT_DESCARREGAR_DIR3_JSON` link) — no bespoke endpoint.

- [ ] **Step 1: Add the artifact code and declaration**

In `OrganGestorResource.java`, add the constant next to `DIR3_SYNC_ACTION_CODE`/`OFICINES_SYNC_ACTION_CODE`:

```java
	public static final String REPORT_DESCARREGAR_DIR3_JSON = "REPORT_DESCARREGAR_DIR3_JSON";
```

Add a new `@ResourceArtifact` entry to the `artifacts` array (alongside the existing ACTION entries) — no `formClass` (no params needed), no `requiresId` (acts on the current session's entitat, not a specific row):

```java
			@ResourceArtifact(
				type = ResourceArtifactType.REPORT,
				code = OrganGestorResource.REPORT_DESCARREGAR_DIR3_JSON,
				accessConstraints = {
					@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_ADMIN })
				}
			),
```

- [ ] **Step 2: Write the report generator**

```java
package es.caib.notib.logic.organs;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Genera el JSON amb la darrera consulta DIR3 dels òrgans gestors de l'entitat actual, per
 * a descàrrega.
 */
@Slf4j
@RequiredArgsConstructor
public class OrganGestorDir3SyncJsonReportGenerator implements BaseReadonlyResourceService.ReportGenerator<OrganGestorResourceEntity, Serializable, Serializable> {

	private final OrganGestorService organGestorService;
	private final UserSessionHelper userSessionHelper;
	private final AuthenticationHelper authenticationHelper;

	@Override
	public List<Serializable> generateData(String code, OrganGestorResourceEntity entity, Serializable params) throws ReportGenerationException {
		return List.of();
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		if (!authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)) {
			log.warn("[DIR3-JSON] Usuari sense permisos ha intentat descarregar el JSON DIR3");
			return DownloadableFile.builder().name("organsDir3JSON.json").content(new byte[]{}).contentType("application/json").build();
		}
		var entitatId = userSessionHelper.getCurrentEntitatId();
		var arxiu = organGestorService.getJsonOrgansGestorDir3(entitatId);
		return DownloadableFile.builder().name("organsDir3JSON.json").content(arxiu).contentType("application/json").build();
	}

	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
	}
}
```

- [ ] **Step 3: Register it in `OrganGestorResourceServiceImpl`**

Add the registration line in `init()`, alongside the existing `register(...)` calls:

```java
		register(OrganGestorResource.REPORT_DESCARREGAR_DIR3_JSON, new OrganGestorDir3SyncJsonReportGenerator(organGestorService, userSessionHelper, authenticationHelper));
```

Add the import `import es.caib.notib.logic.organs.OrganGestorDir3SyncJsonReportGenerator;`.

- [ ] **Step 4: Build the module to catch compile errors**

Run: `mvn -pl notib-service -am clean compile -DskipTests`
Expected: `BUILD SUCCESS`. This deliberately does NOT build `notib-back` (its default `jboss` profile is unrelated to this change, and this task touches no `notib-back` files at all — the report generator lives entirely in `notib-service`).

- [ ] **Step 5: Commit**

```bash
git add notib-service-intf/src/main/java/es/caib/notib/logic/intf/model/OrganGestorResource.java \
        notib-service/src/main/java/es/caib/notib/logic/organs/OrganGestorDir3SyncJsonReportGenerator.java \
        notib-service/src/main/java/es/caib/notib/logic/resourceservice/OrganGestorResourceServiceImpl.java
git commit -m "#1011 Informe de descàrrega del JSON DIR3 d'òrgans gestors"
```

---

### Task 4: Wire the three footer buttons (Sincronitzar / Descarregar JSON / Descarrega PDF)

**Files:**
- Modify: `notib-back/src/main/reactapp/notib-back/src/pages/organ/OrganGrid.tsx:256-324` (`OrganGridDir3SyncActionButton`)
- Modify: `notib-back/src/main/reactapp/notib-back/src/pages/organ/dir3SyncTree.css` (print styles, appended)

**Interfaces:**
- Consumes: `useAuthContext().getToken()` (already imported in `OrganGrid.tsx`), `useBaseAppContext().saveAs` (`lib/components/BaseAppContext.tsx:63`), `useResourceApiService('organGestorResource').currentLinks` — used only if the download link is exposed via HATEOAS; otherwise call the fixed URL directly (see Step 1 note).

- [ ] **Step 1: Add JSON-download and PDF-print handlers, embed them in the result content, simplify `formDialogButtons` to a single Sincronitzar/Cancel·lar pair**

> **Superseded, ruled during execution:** the JSON download no longer goes
> through a bespoke REST endpoint (see Task 3's updated design — it's now a
> `REPORT` artifact, `OrganGestorResource.REPORT_DESCARREGAR_DIR3_JSON`,
> dispatched through the same generic mechanism `DIR3_SYNC` already uses).
> The frontend call below uses `useResourceApiService`'s `artifactReport`
> function directly (the same one `ActionReportButton.tsx`'s internal
> `generateReport` calls — see `lib/components/mui/ActionReportButton.tsx:228-255`
> for the exact blob-handling pattern this mirrors), rather than a raw
> `fetch` to a URL that doesn't exist.

`MuiActionReportButton`'s `formDialogButtons` only supports form-submitting buttons (each click resolves the dialog's promise with a `value`). "Descarregar JSON" and "Descarrega PDF" are side-effect-only actions that must NOT close the dialog, so they're rendered as extra buttons inside the content returned by `formDialogResultProcessor`, alongside the graphical preview — not as `formDialogButtons` entries.

```tsx
const useDir3JsonDownload = () => {
    const { saveAs } = useBaseAppContext();
    const { artifactReport } = useResourceApiService('organGestorResource');
    return React.useCallback(() => {
        artifactReport(undefined, { code: 'REPORT_DESCARREGAR_DIR3_JSON', data: {}, fileType: 'JSON' })
            .then((result: any) => {
                const blob = result?.blob instanceof Blob ? result.blob : new Blob([JSON.stringify(result.blob, null, 2)], { type: 'application/json; charset=utf-8' });
                saveAs?.(blob, result.fileName ?? 'organsDir3JSON.json');
            });
    }, [artifactReport, saveAs]);
};

const Dir3SyncResultActions: React.FC<{ result: any }> = ({ result }) => {
    const { t } = useTranslation();
    const downloadJson = useDir3JsonDownload();
    const printPdf = () => window.print();
    return (
        <Box sx={{ display: 'flex', gap: 1, mt: 2 }} className="dir3-sync-no-print">
            <Button variant="outlined" onClick={downloadJson} startIcon={<Icon>download</Icon>}>
                {t('page.organs.grid.sync.dialogButton.descarregarJson')}
            </Button>
            <Button variant="outlined" onClick={printPdf} startIcon={<Icon>picture_as_pdf</Icon>}>
                {t('page.organs.grid.sync.dialogButton.descarregarPdf')}
            </Button>
        </Box>
    );
};
```

Before writing this, read `useResourceApiService`'s `artifactReport` function signature in `lib/components/ResourceApiProvider.tsx` (the same file `OrganGrid.tsx`'s existing `useSse` already imports `useResourceApiService` from) to confirm the exact parameter shape (id, request-args object) and adjust the call above to match exactly — do not guess the signature; `ActionReportButton.tsx:228-255`'s `generateReport` function is the authoritative reference for both the call shape and the response's `{blob, fileName}` shape.

Wrap the preview content and result actions together in `resultProcessor`:

```tsx
    const resultProcessor = (result: any) => {

        setSenseCanvis(result.senseCanvis);
        setSimular(false);
        return (
            <>
                <OrganGridDir3SyncActionResults result={result} />
                <Dir3SyncResultActions result={result} />
            </>
        );
    };
```

Simplify `formDialogButtons` to a fixed Cancel·lar/Sincronitzar pair (no more query/apply toggle text):

```tsx
    const formDialogButtons = [
        {
            value: false,
            text: t('page.organs.grid.sync.dialogButton.cancel'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: t('page.organs.grid.sync.dialogButton.sincronitzar'),
            icon: 'save',
            componentProps: { variant: 'contained', disabled: senseCanvis === true },
        },
    ];
```

Since `dialogAutoSubmit` already fires the query (`simular:true`) via the dialog's `onReady`, the visible "Sincronitzar" button always submits with whatever `simular` currently is in state — `true` only in the brief window before the first auto-fetch resolves, `false` afterwards (matching the existing `resultProcessor` behavior, now simplified to always end in `false` once a result is shown, since there is no more toggle back to `true`).

- [ ] **Step 2: Add print CSS so `window.print()` only prints the preview panel**

Append to `dir3SyncTree.css`:

```css
@media print {
    body * {
        visibility: hidden;
    }
    #dir3-sync-preview, #dir3-sync-preview * {
        visibility: visible;
    }
    #dir3-sync-preview {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
    }
    .dir3-sync-no-print {
        display: none !important;
    }
}
```

Wrap `OrganGridDir3SyncActionResults`'s returned `<Grid container>` root element with `id="dir3-sync-preview"` (add the `id` prop to that root `Grid`).

- [ ] **Step 3: Type-check**

Run: `npm run build`
Expected: no new TypeScript errors.

- [ ] **Step 4: Commit**

```bash
git add notib-back/src/main/reactapp/notib-back/src/pages/organ/OrganGrid.tsx \
        notib-back/src/main/reactapp/notib-back/src/pages/organ/dir3SyncTree.css
git commit -m "#1011 Botons Sincronitzar/Descarregar JSON/Descarrega PDF a la modal DIR3"
```

---

### Task 5: i18n keys and manual verification

**Files:**
- Modify: `notib-back/src/main/reactapp/notib-back/src/i18n/translationCa.ts` (around line 114-129, `page.organs.grid.sync.dialogButton`)
- Modify: `notib-back/src/main/reactapp/notib-back/src/i18n/translationEs.ts` (equivalent section)

**Interfaces:**
- Consumes: none new.
- Produces: translation keys `page.organs.grid.sync.dialogButton.sincronitzar`, `.descarregarJson`, `.descarregarPdf` (new); `.query`/`.apply` become unused and should be removed since the toggle no longer exists.

- [ ] **Step 1: Update `translationCa.ts`**

Replace the `dialogButton` block (currently at `translationCa.ts:117-129`):

```ts
                    dialogButton: {
                        cancel: 'Cancel·lar',
                        sincronitzar: 'Sincronitzar',
                        descarregarJson: 'Descarregar òrgans JSON',
                        descarregarPdf: 'Descarrega PDF',
                        creacions: "Nous",
                        modificacions: "Canvis en atributs",
                        substitucions: "Substitucions",
                        extincions: "Extingides",
                        fusions: "Fusions",
                        divisions: "Divisions",
                        senseCanvis: "Sense canvis",
                    },
```

- [ ] **Step 2: Update `translationEs.ts`** with the equivalent Spanish text (mirroring the existing pattern in that file — same keys, Spanish values, e.g. `sincronizar: 'Sincronizar'`, `descarregarJson: 'Descargar órganos JSON'`, `descarregarPdf: 'Descargar PDF'`, `creacions: 'Nuevos'`, `modificacions: 'Cambios en atributos'`, `substitucions: 'Sustituciones'`, `extincions: 'Extinguidas'`, `fusions: 'Fusiones'`, `divisions: 'Divisiones'`, `senseCanvis: 'Sin cambios'`).

- [ ] **Step 3: Type-check and lint**

Run: `npm run build && npm run lint`
Expected: both succeed with no new errors.

- [ ] **Step 4: Manual verification against the dev server**

Follow `CLAUDE.md`'s "Spring Boot + live React dev server" setup (`ide,oracle` Maven profiles + `devProxy` Spring profile, `npm run dev` in the React app dir, access via `http://localhost:8080/notibback/reactapp/`). Then:
1. Open Configuració > Òrgans gestors, click "Sincronització Dir3".
2. Confirm the graphical preview (or "Sense canvis") appears immediately, with no separate query click needed.
3. If there are pending DIR3 changes, confirm each populated category (Divisions/Fusions/Substitucions/Canvis en atributs/Nous/Extingides) renders as connected boxes with correct codi/nom and colors (green=kept/valid, red=going away, yellow=new attribute state).
4. Click "Descarregar òrgans JSON" — confirm a file downloads named `organsDir3JSON.json` with DIR3 JSON content, dispatched via the `REPORT_DESCARREGAR_DIR3_JSON` report artifact (Task 3) rather than a raw endpoint.
5. Click "Descarrega PDF" — confirm the browser print dialog opens showing only the preview panel (no app chrome, no footer buttons).
6. Click "Sincronitzar" — confirm the sync applies and the grid refreshes, matching today's existing apply behavior.
7. Confirm "Oficines" button (separate, `OficinesSyncActionButton`) still works unchanged.

- [ ] **Step 5: Commit**

```bash
git add notib-back/src/main/reactapp/notib-back/src/i18n/translationCa.ts \
        notib-back/src/main/reactapp/notib-back/src/i18n/translationEs.ts
git commit -m "#1011 Claus de traducció per a la nova modal de previsualització DIR3"
```
