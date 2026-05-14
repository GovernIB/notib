# Docker de Notib

Aquest directori conté els recursos que es copien dins la imatge Docker de l'EAR de Notib:

- `jboss_caib/standalone-openshift.xml`: configuració principal del perfil `docker-eap72-caib-openshift`.
- `jboss/`: configuració i script del perfil `docker-legacy`.
- `properties/`: `jboss.properties` i `jboss_system.properties`.
- `keystores/`: truststores i certificats necessaris per a l'entorn.

La imatge es defineix a `notib-ear/pom.xml` amb `io.fabric8:docker-maven-plugin`; no hi ha cap `Dockerfile` manual.

---

## 1) Perfils Docker disponibles

Perfil recomanat i actiu per defecte:

```text
docker-eap72-caib-openshift
```

- Imatge base: `280421/jboss-eap-caib-openshift-7.2`
- Configuració: `docker/jboss_caib/standalone-openshift.xml`
- Tags generats: `latest` i la versió Maven del projecte, per exemple `2.1.1`
- Nom de la imatge: `notib`

Perfil alternatiu:

```text
docker-legacy
```

- Imatge base: `280421/jboss-eap-caib-7.2`
- Configuració: `docker/jboss`
- Executa `jboss_config.sh` durant l'arrencada del servidor.

---

## 2) Crear la imatge Docker

Executa les comandes des de l'arrel del repositori.

Build de l'EAR:

```bash
mvn -pl notib-ear -am clean package -Pdocker-eap72-caib-openshift -DskipTests
```

Build de la imatge:

```bash
mvn -pl notib-ear docker:build -Pdocker-eap72-caib-openshift
```

La imatge resultant quedarà etiquetada com:

```text
notib:latest
notib:2.1.1
```

Si vols un altre nom d'imatge:

```bash
mvn -pl notib-ear docker:build -Pdocker-eap72-caib-openshift -Ddocker.image.name=registry.example.org/notib
```

Per generar la variant legacy:

```bash
mvn -pl notib-ear -am clean package -Pdocker-legacy -DskipTests
mvn -pl notib-ear docker:build -Pdocker-legacy
```

---

## 3) `.env` del projecte

Crea un fitxer `.env` a l'arrel del repositori. Aquest fitxer està ignorat per Git.

Exemple:

```dotenv
APP_NAME=notib
APP_PORT=8080
MGM_PORT=9990
DEBUG=
DEBUG_PORT=8787

DB_DRIVER=oracle
DB_JNDI_NAME=java:jboss/datasources/notibDS
DB_POOL_NAME=notibDS
DB_URL=jdbc:oracle:thin:@HOST:1521:SID
DB_USERNAME=notib
DB_PASSWORD=CHANGE_ME

MAIL_HOST=smtp.example.org
MAIL_PORT=465
MAIL_USERNAME=user@example.org
MAIL_PASSWORD=CHANGE_ME
MAIL_SSL=true
MAIL_TLS=false

AUTH_URL=https://auth.example.org
AUTH_REALM=GOIB
AUTH_SSL_REQUIRED=EXTERNAL
AUTH_CLIENTID=goib-default
AUTH_CLIENTID_REST=goib-ws
AUTH_CLIENTID_REST_EXTERNA=goib-ws
AUTH_SECRET=CHANGE_ME
AUTH_SECRET_EXTERNA=CHANGE_ME

TRUSTSTORE_PASSWORD=CHANGE_ME
JERSEY_CDI_LOOKUP_IN_BEAN=true
JGROUPS_JOIN_TIMEOUT=10000

FRONT_API_URL=http://localhost:8080/notibback/apinew
FRONT_AUTH_URL=https://auth.example.org
FRONT_AUTH_REALM=GOIB
FRONT_AUTH_CLIENTID=goib-default

PLUGIN_USERINFO_KEYCLOAK_SERVER_URL=https://auth.example.org
PLUGIN_USERINFO_KEYCLOAK_REALM=GOIB
PLUGIN_USERINFO_KEYCLOAK_CLIENT_ID=goib-ws
PLUGIN_USERINFO_KEYCLOAK_PASSWD_SECRET=CHANGE_ME

FILES_PATH=/home/jboss/apps/notib/files
```

Notes:

- Deixa `DEBUG=` buit si no vols obrir el port de depuració; posa `DEBUG=true` quan el necessitis.
- Per SMTP a port `465`, normalment `MAIL_SSL=true` i `MAIL_TLS=false`.
- Per SMTP a port `587`, normalment `MAIL_SSL=false` i `MAIL_TLS=true`.
- `AUTH_CLIENTID` correspon al client públic del backoffice.
- `AUTH_CLIENTID_REST` i `AUTH_SECRET` corresponen als clients bearer/basic de les APIs.
- `AUTH_CLIENTID_REST_EXTERNA` i `AUTH_SECRET_EXTERNA` permeten separar la configuració de `notib-api-externa.war`; si no cal separar-la, usa els mateixos valors que `AUTH_CLIENTID_REST` i `AUTH_SECRET`.
- `DB_JNDI_NAME`, `DB_POOL_NAME`, `AUTH_SSL_REQUIRED`, `JERSEY_CDI_LOOKUP_IN_BEAN` i `JGROUPS_JOIN_TIMEOUT` tenen valors per defecte al `standalone-openshift.xml`, però es documenten per fer explícita tota la configuració del contenidor.

---

## 4) `docker-compose.yml`

Crea `docker-compose.yml` a l'arrel del repositori. Aquest fitxer també està ignorat per Git.

```yaml
services:
  notib:
    image: notib:2.1.1
    container_name: notib
    ports:
      - "${APP_PORT:-8080}:8080"
      - "${MGM_PORT:-9990}:9990"
      - "${DEBUG_PORT:-8787}:8787"
    volumes:
      - notib_files:/home/jboss/apps/notib/files
    environment:
      JAVA_OPTS: "-Xms1303m -Xmx1303m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=512m -Djava.net.preferIPv4Stack=true"
      DEBUG: "${DEBUG:-}"
      DEBUG_PORT: "${DEBUG_HOST:-0.0.0.0:}${DEBUG_PORT:-8787}"

      JBOSS_APP_NAME: "${APP_NAME:-notib}"
      JBOSS_APP_PROPERTIES_NAME: "es.caib.notib.properties"
      JBOSS_APP_PROPERTIES_PATH: "/home/jboss/apps/notib/jboss.properties"
      JBOSS_APP_SYSTEM_PROPERTIES_NAME: "es.caib.notib.system.properties"
      JBOSS_APP_SYSTEM_PROPERTIES_PATH: "/home/jboss/apps/notib/jboss_system.properties"
      JERSEY_CDI_LOOKUP_IN_BEAN: "${JERSEY_CDI_LOOKUP_IN_BEAN:-true}"
      JGROUPS_JOIN_TIMEOUT: "${JGROUPS_JOIN_TIMEOUT:-10000}"

      JBOSS_DB_DRIVER: "${DB_DRIVER:-oracle}"
      JBOSS_DB_JNDI_NAME: "${DB_JNDI_NAME:-java:jboss/datasources/notibDS}"
      JBOSS_DB_POOL_NAME: "${DB_POOL_NAME:-notibDS}"
      JBOSS_DB_URL: "${DB_URL}"
      JBOSS_DB_USERNAME: "${DB_USERNAME}"
      JBOSS_DB_PASSWORD: "${DB_PASSWORD}"

      JBOSS_MAIL_HOST: "${MAIL_HOST}"
      JBOSS_MAIL_PORT: "${MAIL_PORT:-25}"
      JBOSS_MAIL_USERNAME: "${MAIL_USERNAME}"
      JBOSS_MAIL_PASSWORD: "${MAIL_PASSWORD}"
      JBOSS_MAIL_SSL: "${MAIL_SSL:-false}"
      JBOSS_MAIL_TLS: "${MAIL_TLS:-false}"

      JBOSS_AUTH_URL: "${AUTH_URL}"
      JBOSS_AUTH_REALM: "${AUTH_REALM}"
      JBOSS_AUTH_SSL_REQUIRED: "${AUTH_SSL_REQUIRED:-EXTERNAL}"
      JBOSS_AUTH_CLIENTID: "${AUTH_CLIENTID}"
      JBOSS_AUTH_CLIENTID_REST: "${AUTH_CLIENTID_REST}"
      JBOSS_AUTH_CLIENTID_REST_EXTERNA: "${AUTH_CLIENTID_REST_EXTERNA}"
      JBOSS_AUTH_SECRET: "${AUTH_SECRET}"
      JBOSS_AUTH_SECRET_EXTERNA: "${AUTH_SECRET_EXTERNA}"

      JBOSS_AUTH_WS_CLIENTID: "${AUTH_CLIENTID_REST}"
      JBOSS_AUTH_CREDENTIAL_SECRET: "${AUTH_SECRET}"

      JBOSS_TRUSTSTORE: "/home/jboss/keystores/truststore.jks"
      JBOSS_TRUSTSTORE_PASSWORD: "${TRUSTSTORE_PASSWORD}"

      es.caib.notib.front.api.url: "${FRONT_API_URL}"
      es.caib.notib.front.auth.url: "${FRONT_AUTH_URL}"
      es.caib.notib.front.auth.realm: "${FRONT_AUTH_REALM}"
      es.caib.notib.front.auth.clientid: "${FRONT_AUTH_CLIENTID}"

      es.caib.notib.fitxers: "${FILES_PATH:-/home/jboss/apps/notib/files}"
      es.caib.notib.plugin.gesdoc.filesystem.base.dir: "${FILES_PATH:-/home/jboss/apps/notib/files}"

      es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl: "${PLUGIN_USERINFO_KEYCLOAK_SERVER_URL}"
      es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm: "${PLUGIN_USERINFO_KEYCLOAK_REALM}"
      es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id: "${PLUGIN_USERINFO_KEYCLOAK_CLIENT_ID}"
      es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret: "${PLUGIN_USERINFO_KEYCLOAK_PASSWD_SECRET}"
    restart: unless-stopped

volumes:
  notib_files:
    driver: local
```

El bloc `JBOSS_AUTH_WS_CLIENTID` i `JBOSS_AUTH_CREDENTIAL_SECRET` es deixa per compatibilitat amb el perfil `docker-legacy`. El perfil recomanat `docker-eap72-caib-openshift` usa `JBOSS_AUTH_CLIENTID_REST` i `JBOSS_AUTH_SECRET`.

---

## 5) Arrencar i aturar

Arrencar:

```bash
docker compose up -d
```

Si utilitzes la CLI antiga, substitueix `docker compose` per `docker-compose`.

Veure logs:

```bash
docker compose logs -f notib
```

Aturar:

```bash
docker compose down
```

Aturar i eliminar el volum local de fitxers:

```bash
docker compose down -v
```

---

## 6) URLs habituals

Backoffice classic:

```text
http://localhost:8080/notibback/
```

Front React:

```text
http://localhost:8080/notibback/reactapp/
```

API interna:

```text
http://localhost:8080/notibapi/interna/
```

API externa:

```text
http://localhost:8080/notibapi/externa/
```

API React/HATEOAS:

```text
http://localhost:8080/notibback/apinew
```

---

## 7) Problemes freqüents

Si el front React no carrega configuració, revisa les variables:

- `es.caib.notib.front.api.url`
- `es.caib.notib.front.auth.url`
- `es.caib.notib.front.auth.realm`
- `es.caib.notib.front.auth.clientid`

Si l'aplicació no troba els fitxers de properties, revisa:

- `JBOSS_APP_PROPERTIES_NAME=es.caib.notib.properties`
- `JBOSS_APP_PROPERTIES_PATH=/home/jboss/apps/notib/jboss.properties`
- `JBOSS_APP_SYSTEM_PROPERTIES_NAME=es.caib.notib.system.properties`
- `JBOSS_APP_SYSTEM_PROPERTIES_PATH=/home/jboss/apps/notib/jboss_system.properties`

Si fallen connexions HTTPS cap a serveis externs, revisa:

- `JBOSS_TRUSTSTORE`
- `JBOSS_TRUSTSTORE_PASSWORD`
- el contingut de `docker/keystores/truststore.jks`

Si falla la base de dades, revisa `JBOSS_DB_URL`, `JBOSS_DB_USERNAME`, `JBOSS_DB_PASSWORD` i que la imatge base tengui el driver configurat per al valor de `JBOSS_DB_DRIVER`.
