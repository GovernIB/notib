# Propietats afegides a la versió 2.1.1

| Propietat | Descripció | Valor per defecte |
|-----------|------------|--------------------|
| `es.caib.notib.plugin.cie.https` | Indica si el servei web de CIE va sobre https | `true` |
| `es.caib.notib.app.maxresults.selects` | Màxim nombre de resultats a mostrar a les llistes desplegables, per tal d'optimitzar les consultes | `20` |
| `es.caib.notib.app.interficie.react.defecte` | Indica si en accedir a l'aplicació s'ha de mostrar per defecte la interfície nova (React) enlloc de la clàssica (JSP) | `false` |
| `es.caib.notib.app.llistat.remeses.estat.asincron` | Llistat de remeses de la interfície React: si és `true`, la columna estat de les remeses pendents d'actualitzar es calcula en segon pla després de retornar el llistat i s'envia al navegador via SSE (el llistat es mostra immediatament, amb el darrer estat conegut i un indicador de càlcul en curs). Si és `false`, es calcula abans de retornar el llistat (en una sola transacció per pàgina). | `true` |

> Aquestes propietats es desen a BBDD (taula `NOT_CONFIG`). Les tres primeres les creen els scripts `02_update_2.1.1_dml.sql` i `es.caib.notib.app.llistat.remeses.estat.asincron` la creen els scripts `06_update_2.1.1_dml.sql`. Per tant, **no** s'han d'afegir al fitxer de propietats.

## Propietats del fitxer de propietats (`es.caib.notib.properties`)

| Propietat | Descripció | Valor per defecte |
|-----------|------------|--------------------|
| `es.caib.notib.auth.url` | URL base del servidor Keycloak/IdP (p.ex. `https://SERVIDOR_KEYCLOAK/auth/`). S'utilitza, només sota JBoss, com a fallback per construir l'URL de tancament de sessió SSO quan no hi ha `id_token` a la sessió. Ha de coincidir amb l'`auth-server-url` del subsystem keycloak de `standalone.xml`. | _(buit)_ |
| `es.caib.notib.auth.realm` | Realm de Keycloak/IdP (p.ex. `REALM_KEYCLOAK`). Mateix ús que l'anterior; ha de coincidir amb el `realm` del subsystem keycloak de `standalone.xml`. | _(buit)_ |

> Aquestes dues propietats **sí** s'han d'afegir al fitxer de propietats de l'aplicació. **No** s'han de confondre amb `es.caib.notib.front.auth.*`: aquestes darreres **no** s'han d'informar en desplegaments JBoss (si s'informen, la interfície React intenta autenticar-se per OIDC i entra en un bucle de redireccions).

## Ordenació del llistat de remeses (Oracle)

Els índexs que permeten ordenar ràpidament el llistat de remeses per columnes de text (concepte, número d'expedient, usuari, números de registre i titular), creats pels scripts `07_update_2.1.1_ddl.sql`, són índexs `NLSSORT` amb `NLS_SORT=CATALAN`. Oracle només els pot fer servir si la sessió ordena amb aquest mateix `NLS_SORT`. El driver JDBC el pren de l'idioma de la JVM de l'aplicació, així que el servidor ha d'arrencar amb una JVM en català (`ca_ES`, p.ex. `-Duser.language=ca -Duser.country=ES`). Amb un altre idioma, el llistat funciona igual, però ordenar per aquestes columnes amb molt de volum és lent.
