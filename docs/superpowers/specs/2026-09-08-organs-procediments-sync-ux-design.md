# Disseny: UX de sincronització DIR3 d'òrgans, procediments i serveis (React)

Data: 2026-09-08

## Context

A la configuració d'òrgans gestors (React SPA), l'acció de sincronitzar amb DIR3/ROLSAC
existeix parcialment: `OrganGrid.tsx` té un botó "Sincronització Dir3" que fa una
simulació prèvia (comptadors numèrics) i després aplica els canvis, i un botó
"Oficines" separat. Procediments i serveis (`ProcedimentGrid.tsx`, `ServeiGrid.tsx`) tenen
un botó de sincronització sense cap indicador de progrés.

Aquesta UX no reflecteix la de la interfície JSP legacy
(`OrganGestorController` + `synchronizationPrediction.jsp`), que:
- Mostra gràficament (caixes de colors connectades per línies, CSS
  `horizontal-tree.css`) els canvis pendents **d'òrgans** abans d'aplicar-los.
- En aplicar, sincronitza seqüencialment òrgans → migració de permisos d'òrgans
  obsolets → procediments → serveis → oficines SIR, mostrant un log en viu
  (scroll de línies) mitjançant polling AJAX cada 500 ms.
- Ofereix botons de "Descarregar òrgans JSON" i "Descarrega PDF" a la mateixa
  modal de previsualització.

Aquest document especifica com portar aquesta UX al React SPA, reutilitzant tant
com sigui possible la infraestructura ja existent (`OrganGestorDir3Sync`,
`OrganGestorSyncHelper`, `SseEvent`/`SseEventService`/`SseController`, `useSse`).

## Objectiu

1. El botó "Sincronització Dir3" (òrgans) ha de mostrar gràficament els canvis
   pendents (no comptadors), igual que el JSP, i oferir els mateixos 3 botons:
   Descarregar òrgans JSON, Descarrega PDF, Sincronitzar.
2. Un nou botó combinat "Actualitzar òrgans i procediments" ha d'orquestrar
   òrgans → permisos → procediments → serveis → oficines SIR en una sola
   execució, mostrant un log d'operacions en viu via SSE (afegint línies, no
   substituint-les), igual que el JSP.

## Fora d'abast

- No es construeix cap capacitat nova de diff/simulació per a procediments,
  serveis, permisos o oficines — només òrgans té previsualització prèvia
  (igual que avui: ni el JSP ni el React actual en tenen per a la resta).
- No es toquen els botons existents "Sincronització Dir3" (òrgans, ja
  existent, es millora la seva modal) ni "Oficines" (es manté intacte i en
  paral·lel al nou botó combinat).
- No es construeix generació de PDF al servidor — és impressió client-side.
- No es resol el risc de timeout d'una petició HTTP síncrona molt llarga
  (veure "Riscs").

## Component 1: Previsualització gràfica d'òrgans

### Backend

Cap canvi. `OrganGestorSyncHelper.sincronitzar(entitat, simular=true)`
(`OrganGestorSyncHelper.java:50-175`) ja retorna `OrganGestorDir3Sync` complet
amb `creacions`, `modificacions`, `substitucions`, `extincions`, `fusions`,
`divisions` — cadascun amb els `OrganGestorDir3SyncArbreItem` (codi, nom,
nomCooficial, estat) necessaris.

### Frontend

- **Auto-fetch en obrir la modal**: en lloc del flux actual de dos clics
  ("Consultar canvis" → "Aplicar"), la modal ha de disparar automàticament
  l'acció `DIR3_SYNC` amb `simular:true` en obrir-se, i mostrar directament el
  resultat gràfic. Cal revisar l'API de `MuiActionReportButton`/`reactlib`
  per determinar el mecanisme d'auto-execució en obrir (p. ex. un `useEffect`
  que dispara l'acció via l'`apiRef` del formulari, o una prop dedicada si
  n'hi ha). Si `reactlib` no ho suporta nativament, substituir
  `MuiActionReportButton` per un diàleg propi que faci la crida directament
  amb `useResourceApiService('organGestorResource')`.
- **Renderitzat gràfic**: portar `horizontal-tree.css`
  (`notib-back/src/main/webapp/css/horizontal-tree.css`) tal qual a
  `notib-back/src/main/reactapp/notib-back` (és CSS pur, sense dependències
  de jQuery/JSP). Crear un component `Dir3SyncCanviBranch` (o similar) que
  reprodueixi l'estructura `div.horizontal-left|right > #wrapper > span.label +
  div.branch > div.entry > span.label`, parametritzat per:
  - orientació (`left`/`right`)
  - node arrel (`codi`/`nom`, color: vermell=`bg-danger/border-red`,
    verd=`bg-success/border-green`, groc=`bg-warning/border-yellow`, o buit
    ="create-label"/"remove-label")
  - llista de nodes fulla (mateixa paleta de colors)
- **Sis seccions**, cadascuna renderitzada només si l'array corresponent no
  és buit (igual que els `<c:if>` del JSP), mapejades 1:1 als camps de
  `OrganGestorDir3Sync`:

  | Secció JSP | Camp DTO | Orientació | Arrel | Fulles |
  |---|---|---|---|---|
  | Divisions (splits) | `divisions[].vell` → `divisions[].nous[]` | left | vell (vermell) | nous (verd) |
  | Fusions (merges) | `fusions[].vells[]` → `fusions[].nou` | right | nou (verd) | vells (vermell) |
  | Substitucions | `substitucions[].vell` → `.nou` | right | nou (verd) | vell (vermell), sole |
  | Canvis en atributs | `modificacions[].vell` → `.nou` | left | vell (verd) | nou (groc), sole |
  | Nous | `creacions[].nou` | left | buit (create-label) | nou (verd), sole |
  | Extingides | `extincions[].vell` | left | vell (vermell) | buit (remove-label), sole |

  Si totes les llistes són buides, mostrar el missatge "sense canvis" actual
  (`page.organs.grid.sync.dialogButton.senseCanvis`).
- **Tres botons** al peu de la modal, substituint els actuals cancel·lar/query
  o cancel·lar/aplicar:
  - **Sincronitzar**: crida l'acció `DIR3_SYNC` amb `simular:false` (lògica
    d'aplicar ja existent, sense canvis).
  - **Descarregar òrgans JSON**: crida el nou endpoint (veure més avall) i
    desa el resultat com a fitxer amb el nom `organsDir3JSON.json` (mateix
    patró que `FileSaver`/`Blob` del JSP, amb `fetch` + `URL.createObjectURL`
    en comptes de jQuery).
  - **Descarrega PDF**: `window.print()` amb CSS `@media print` que amaga
    tota la pàgina excepte el contenidor de la previsualització (equivalent
    funcional del plugin `printThis`, sense dependències noves).

### Nou endpoint REST per a la descàrrega JSON

Un controlador REST petit i dedicat (seguint el patró de
`SseController.java`, no el framework genèric d'`export`/`fields/download`
de `BaseReadonlyResourceController`, que està pensat per a exports de graella
i camps de recurs, no per a un payload ad-hoc), p. ex.:

```java
@RestController("dir3OrgansJsonController")
@RequestMapping(BaseConfig.API_PATH + "/organGestorResource/dir3SyncJson")
public class OrganGestorDir3SyncJsonController {
    @GetMapping
    public ResponseEntity<byte[]> download() {
        var entitatId = userSessionHelper.getCurrentEntitatId();
        byte[] arxiu = organGestorService.getJsonOrgansGestorDir3(entitatId);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=organsDir3JSON.json")
            .body(arxiu);
    }
}
```

Reutilitza `OrganGestorService.getJsonOrgansGestorDir3(Long entitatId)`
(`OrganGestorServiceImpl.java:688`), que ja existeix i no canvia.

## Component 2: Sincronització combinada òrgans + procediments + serveis + oficines

### Nova acció backend

Nova acció `ORGANS_PROCEDIMENTS_SYNC` registrada a
`OrganGestorResourceServiceImpl.init()`, executada per un nou
`OrgansProcedimentsSyncActionExecutor`, que crida un nou helper
`OrganGestorFullSyncHelper.sincronitzarTot(EntitatResourceEntity entitat)`.
Aquest helper orquestra, seqüencialment:

1. `organGestorSyncHelper.sincronitzar(entitat, simular=false)` (òrgans, ja
   existent).
2. `permisosHelper.actualitzarPermisosOrgansObsolets(...)` (migració de
   permisos, ja existent a `OrganGestorServiceImpl.syncDir3OrgansGestors`).
3. `procSerSyncHelper.actualitzaProcediments(entitatDto, progressPublisher)`
   (nou overload, veure més avall).
4. `procSerSyncHelper.actualitzaServeis(entitatDto, progressPublisher)` (nou
   overload).
5. Sincronització d'oficines SIR (mateixa lògica que crida avui
   `OficinesSyncActionExecutor`/`organGestorService.syncOficines(...)`).

Cada fase publica progrés sota el mateix esdeveniment SSE nou
`SseEvent.SseEventName.ORGANS_PROCEDIMENTS_SYNC`, reescalant el seu propi
0–100% intern a un tram del total (proporcions similars a les del
controlador legacy: òrgans i procediments/serveis reben els trams més grans;
permisos i oficines són fases més curtes, amb missatges d'inici/fi només,
sense percentatge fi per operació).

### Plumbing de progrés per a procediments/serveis (canvi additiu)

`ProgresActualitzacioDto` (`notib-service-intf/.../dto/ProgresActualitzacioDto.java`)
ja crida `addInfo(...)` un cop per línia de log (incloent-hi una línia per
procediment processat, `ProcSerSyncHelper.java:262`) i
`incrementOperacionsRealitzades(...)` per calcular el seu propi percentatge.
Per exposar-ho via SSE sense tocar cap dels ~69 punts de crida existents:

- Afegir un camp `transient Consumer<ActualitzacioInfo> onInfo` (setter
  inclòs) a `ProgresActualitzacioDto`, invocat al final de `addInfo(...)`.
- Afegir un `transient Consumer<Integer> onProgressChanged`, invocat al final
  de `incrementOperacionsRealitzades(...)`.
- `ProcSerSyncHelper.actualitzaProcediments`/`actualitzaServeis` guanyen un
  **nou overload** que rep un `ProgressPublisher` (interfície funcional
  `(int percent, String message) -> void`); els mètodes sense aquest
  paràmetre (usats avui per `SchedulledServiceImpl` i pel flux JSP legacy via
  `OrganGestorServiceImpl.syncDir3OrgansGestors`) deleguen als nous amb un
  publisher no-op. Cap crida existent canvia de comportament.
- `OrganGestorFullSyncHelper` passa un `ProgressPublisher` que rescala
  `percent` al tram corresponent de la fase i publica un `SseEvent` amb
  `status=RUNNING`, `message=text`.

### Frontend

- Nou botó a `OrganGrid.tsx`, acció `ORGANS_PROCEDIMENTS_SYNC` sobre
  `organGestorResource`.
- Nou component de progrés (variant de `OrganGridDir3SyncLoading`) que manté
  una llista de missatges rebuts via `useSse('PROGRESS',
  'ORGANS_PROCEDIMENTS_SYNC', ...)` i els **afegeix** (no els substitueix) a
  un contenidor amb scroll, replicant el `#actualitzacioInfo` del JSP. Estil
  senzill (una sola mida/color de línia, ressaltant en vermell les línies amb
  `status=ERROR`) — no es replica la jerarquia tipogràfica completa de 7
  nivells del JSP (TITOL/SUBTITOL/INFO/SUBINFO/TEMPS/SEPARADOR/ERROR).
- No hi ha pas de previsualització per a aquest botó combinat (només òrgans
  en té, component 1); en obrir la modal es mostra directament el botó
  "Sincronitzar" amb una nota informativa que s'actualitzaran també
  procediments, serveis i oficines SIR.
- El botó "Oficines" existent (`OficinesSyncActionButton`) es manté sense
  canvis, en paral·lel.

## Riscs

- **Timeout de petició HTTP síncrona**: igual que el flux actual d'òrgans, la
  sincronització combinada s'executa com una única crida HTTP síncrona
  mentre el progrés s'observa per una connexió SSE separada. Per a entitats
  amb molts procediments, això pot trigar prou com per superar el timeout
  d'un proxy/gateway intermedi. No es resol en aquest disseny (implicaria
  infraestructura de tasques asíncrones, fora d'abast); es documenta com a
  limitació coneguda heretada del patró existent.
- **`SseEventService` és d'un sol listener per cua**: si dues pestanyes o
  usuaris disparen sincronitzacions simultànies, el darrer subscriptor
  "guanya" la cua `PROGRESS` i l'altre deixa de rebre events (el
  comportament ja existent avui amb `DIR3_SYNC`). No es corregeix aquí.

## Fitxers afectats (resum)

Backend:
- `notib-service-intf/.../model/SseEvent.java` — nou valor d'enum
  `ORGANS_PROCEDIMENTS_SYNC`.
- `notib-service-intf/.../dto/ProgresActualitzacioDto.java` — hooks
  `onInfo`/`onProgressChanged`.
- `notib-service/.../helper/ProcSerSyncHelper.java` — nous overloads amb
  `ProgressPublisher`.
- `notib-service/.../helper/OrganGestorFullSyncHelper.java` — **nou**,
  orquestració de les 5 fases.
- `notib-service/.../resourceservice/OrganGestorResourceServiceImpl.java` —
  registre de la nova acció.
- Nou `OrgansProcedimentsSyncActionExecutor`.
- Nou controlador REST per a la descàrrega JSON (`dir3SyncJson`).

Frontend (`notib-back/src/main/reactapp/notib-back`):
- `src/pages/organ/OrganGrid.tsx` — modal d'òrgans reescrita (auto-fetch,
  render gràfic, 3 botons) + nou botó combinat.
- Nou fitxer CSS (`horizontal-tree.css` portat).
- Nous components de branca/entrada/etiqueta per al render gràfic.
- Nou component de log en viu (append-only) per a la sincronització
  combinada.

## Testing

- Backend: tests unitaris nous per a `OrganGestorFullSyncHelper` (ordre de
  fases, reescalat de percentatge, que els overloads sense publisher no
  trenquin `SchedulledServiceImplTest`/similars existents).
- Frontend: verificar manualment amb el dev server (`npm run dev` +
  `devProxy`) contra un entorn amb canvis DIR3 reals o simulats: obrir la
  modal d'òrgans i confirmar que el gràfic apareix sense clic previ, provar
  els 3 botons, i disparar el botó combinat confirmant que el log
  s'acumula i no es reemplaça.
