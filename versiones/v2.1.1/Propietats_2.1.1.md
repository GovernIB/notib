# Propietats afegides a la versió 2.1.1

| Propietat | Descripció | Valor per defecte |
|-----------|------------|--------------------|
| `es.caib.notib.plugin.cie.https` | Indica si el servei web de CIE va sobre https | `true` |
| `es.caib.notib.app.maxresults.selects` | Màxim nombre de resultats a mostrar a les llistes desplegables, per tal d'optimitzar les consultes | `20` |
| `es.caib.notib.app.interficie.react.defecte` | Indica si en accedir a l'aplicació s'ha de mostrar per defecte la interfície nova (React) enlloc de la clàssica (JSP) | `false` |

> Aquestes propietats es desen a BBDD (taula `NOT_CONFIG`, creades pels scripts `02_update_2.1.1_dml.sql`). Per tant, **no** s'han d'afegir al fitxer de propietats.

## Propietats del fitxer de propietats (`es.caib.notib.properties`)

| Propietat | Descripció | Valor per defecte |
|-----------|------------|--------------------|
| `es.caib.notib.auth.url` | URL base del servidor Keycloak/IdP (p.ex. `https://SERVIDOR_KEYCLOAK/auth/`). S'utilitza, només sota JBoss, com a fallback per construir l'URL de tancament de sessió SSO quan no hi ha `id_token` a la sessió. Ha de coincidir amb l'`auth-server-url` del subsystem keycloak de `standalone.xml`. | _(buit)_ |
| `es.caib.notib.auth.realm` | Realm de Keycloak/IdP (p.ex. `REALM_KEYCLOAK`). Mateix ús que l'anterior; ha de coincidir amb el `realm` del subsystem keycloak de `standalone.xml`. | _(buit)_ |

> Aquestes dues propietats **sí** s'han d'afegir al fitxer de propietats de l'aplicació. **No** s'han de confondre amb `es.caib.notib.front.auth.*`: aquestes darreres **no** s'han d'informar en desplegaments JBoss (si s'informen, la interfície React intenta autenticar-se per OIDC i entra en un bucle de redireccions).
