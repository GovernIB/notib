# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

NOTIB is the notifications/communications platform of the Govern de les Illes Balears (CAIB), developed by Límit Tecnologies. Java 11, Spring Boot 2.7 parent, multi-module Maven build. It is deployed as an EAR on JBoss EAP but the backoffice can also run standalone as a Spring Boot app for development. Code, comments, commit messages and UI strings are in Catalan; commits are prefixed with the issue number (e.g. `#1011 ...`).

The active branch line (`notib2-wip-react`) is migrating the backoffice UI from JSP/Spring MVC to a React SPA.

## Build & run

```bash
# Full build (default profiles: jboss + front -> builds the React app too)
mvn clean install

# Skip the React build
mvn clean install -P '!front'          # or -Dfrontend.skip=true

# Single module with its dependencies
mvn -pl notib-service -am install

# Tests (mainly in notib-service, also notib-persistence, notib-plugin, notib-client)
mvn -pl notib-service test
mvn -pl notib-service test -Dtest=CaducitatHelperTest
mvn -pl notib-service test -Dtest=CaducitatHelperTest#methodName
```

Maven profiles (see `doc/Guia_Arrencada_Notib.md` for the full startup guide):

- `jboss` (default) – WAR for the EAR; services are reached through EJBs.
- `ide` – runs `notib-back` as Spring Boot (`es.caib.notib.NotibBackBootApp`), with `notib-service` embedded directly instead of EJBs.
- `front` (default) – `frontend-maven-plugin` installs Node and runs `npm run build-jboss` in the React app.
- `oracle` / `postgresql` (in `notib-persistence`) – DB driver selection.

Run modes:

- Spring Boot, bundled front: profiles `front,ide,oracle`.
- Spring Boot + Vite dev server: profiles `ide,oracle`, Spring profile `devProxy`, and `npm run dev` in the front dir. Always open the app through the backend at `http://localhost:8080/notibback/reactapp/` (proxied by `DevProxyController`); hitting the Vite server directly bypasses session auth and API calls fail.
- JBoss/Docker: profiles `jboss,front`, configured via `.env` + `docker-compose.yml` at the repo root.

Local config files (`application.properties`, `.env`, `docker-compose.yml` at the repo root, and `.env.local` in the front dir) are git-ignored; templates are in the startup guide.

### React front-end

Located at `notib-back/src/main/reactapp/notib-back` (Vite + React 19 + TypeScript + MUI 7).

```bash
npm install        # runs patch-package (patches/) on postinstall
npm run dev        # served under /notibback/reactapp
npm run build      # tsc -b && vite build
npm run lint       # eslint
```

## Architecture

Layered modules (all under package `es.caib.notib`):

| Module | Role |
|---|---|
| `notib-persistence` | JPA entities, Spring Data repositories, ACLs, Liquibase changelogs (`src/main/resources/db/changelog`). |
| `notib-service-intf` | Service interfaces, DTOs, `*Resource` models (`logic.intf.model`), resource-service interfaces, state machine interfaces. Shared by everything above. |
| `notib-service` | Business logic (`es.caib.notib.logic`): services, helpers, scheduled tasks, ActiveMQ/JMS, callbacks, Spring State Machine for enviaments (`logic.statemachine`), resource-service implementations. |
| `notib-plugin-intf` / `notib-plugin` | Integrations with external systems (Notific@, Regweb registry, Arxiu, DIR3, CIE/postal, firma, user data…), selected via configuration. |
| `notib-ejb` | `@Stateless` EJB wrappers delegating to the Spring services, used when deployed on JBoss. `resourceservice/*ResourceServiceEjb` wrap resource services. |
| `notib-backend` | Generic REST base layer (`es.caib.notib.back.base`): `BaseReadonlyResourceController` / `BaseMutableResourceController`, error handling. |
| `notib-back` | Backoffice WAR: legacy Spring MVC/JSP controllers (`back.controller`), new REST resource controllers (`back.resourcecontroller`), security, websocket, and the React app. |
| `notib-api-interna` / `notib-api-externa` | REST/SOAP APIs for integrating applications and for external callbacks/advisers (Notific@ adviser, etc.). |
| `notib-ear` | Packages back + APIs + EJB into the EAR (Docker profiles for OpenShift / legacy images). |
| `notib-client`, `notib-client-legacy`, `notib-client-intf` | Java client libraries for consumers of the NOTIB API. |

`notib-client-2`, `notib-controller`, `notib-boot`, `notib-test` are not in the root reactor.

### Resource API pattern (used by the React UI)

Adding a new screen typically touches one class per layer, all keyed by the same `XxxResource`:

1. `XxxResource` model in `notib-service-intf` (`logic.intf.model`) and `XxxResourceService` interface (`logic.intf.resourceservice`).
2. `XxxResourceServiceImpl` in `notib-service` (`logic.resourceservice`), built on the generic base services in `logic.base`.
3. `XxxResourceServiceEjb` in `notib-ejb/resourceservice` **and** a bean in `notib-back/.../config/EjbClientConfig` (JNDI `java:app/notib-ejb/<Name>Ejb`) for the JBoss deployment.
4. `XxxResourceController extends BaseMutableResourceController<XxxResource, Long>` (or `BaseReadonlyResourceController`) in `notib-back/.../resourcecontroller`, mapped under `BaseConfig.API_PATH` (`/apinew`). Exposes HAL/HATEOAS endpoints with filtering (spring-filter), artifacts (actions, reports, filters) and perspectives.

The dual wiring matters: under `ide` the Spring service beans are injected directly; under `jboss` (`@ConditionalOnWarDeployment`) they are EJB proxies. Forgetting step 3 works locally but breaks on JBoss.

### Front-end structure

- `lib/` – in-house base library (auth providers, `ResourceApiProvider` using `ketting` HAL client, `GridPage`/`FormPage`/`DetailPage`, `MuiDataGrid`, form fields, dialogs, i18n). Pages build on these instead of calling axios directly.
- `src/pages/<domain>/` – one folder per functional area (notificacio, enviament, organ, procediment, entitat…). Routing in `src/router.tsx`, URL constants in `src/appUrls.ts`, role-based access in `src/routeAccess.ts`.
- `src/i18n` – Catalan/Spanish translations.

## Other considerations
- When implementing changes in this project, robustness, clarity, maintainability, and code reuse take precedence over creativity.
- Never make assumptions or extrapolations; always verify. If verification is not possible, state the claim but indicate that it is an assumption and specify how the user should proceed for external verification.
- The components in the \notib\notib-back\src\main\reactapp\notib-back\lib folder should not be modified or warned before doing so, as this folder is overwritten from an external project, and any changes made could be lost.
- When creating a task in JIRA, always assign it to the most recent sprint. Sprints are listed as YYMMLOT2 (e.g., 2606LOT2 for June 2026). Other required fields are: affected version is current version (from pom), fix version is next versión, category "Gestió incidències" and request date today.
- Not make hot-swapping changes using jboss-cli. When doing so, JBoss rewrites the entire standalone.xml file based on its in-memory configuration, and the XML comments are lost.
- When working with Playwright, if I ask for a visual change, I want you to apply it by default within the Playwright browser, not in the code, unless I tell you otherwise.