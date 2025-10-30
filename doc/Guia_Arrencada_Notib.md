# Guia d'arrencada de Notib (Spring Boot i JBoss)

---

## 1) Arrencar amb Spring Boot (sense dev server del front)

- Perfils de Maven: `front`, `ide`, `oracle`

## 2) Arrencar amb Spring Boot + `npm run dev` (front en mode desenvolupament)

- Perfils de Maven: `ide`, `oracle`
- Per arrencar el frontal en local:
  1. Inicia el dev server com qualsevol projecte base React:
     - Des del directori del front: `notib-back/src/main/reactapp/notib-back`
     - Executa: `npm install` (la primera vegada) i després `npm run dev`
  2. Al backend, passa-li el perfil de SPRING (no de Maven) `devProxy`.

- Un cop fet això, podràs accedir al servidor de desenvolupament a través del backend, via proxy, a:
  - `http://localhost:8080/notibback/reactapp/`
  - El proxy està configurat a la classe `DevProxyController`.

- Important: Has d'entrar a través de l'URL del backend. No pots entrar directament al dev server del front, perquè no s'aplicarà l'autenticació per sessió de Notib i les peticions fallaran.

### 2.1) Configuració del front (`.env.local`)

Crea un fitxer `.env.local` a l'arrel de la carpeta del frontal: `notib-back/src/main/reactapp/notib-back` amb el contingut:

```
VITE_API_URL=http://localhost:8080/notibback/api2/
VITE_AUTH_PROVIDER_URL=https://authdev.limit.es
VITE_AUTH_PROVIDER_REALM=GOIB
VITE_AUTH_PROVIDER_CLIENTID=goib-default
DISABLE_OPEN_ON_START=true
```

### 2.2) Configuració del backend (`application.properties` a l'arrel)

Crea/ajusta el fitxer `application.properties` a l'arrel del projecte Spring Boot amb el contingut següent:

```
spring.datasource.url=jdbc:oracle:thin:@10.35.3.77:1521:xe
spring.datasource.username=notib
spring.datasource.password=notib

spring.mail.host=correu.limit.es
spring.mail.port=465
spring.mail.username=proves_limit@limit.es
spring.mail.password=R8lmu-98TRN
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.starttls.enable=false

spring.security.oauth2.client.provider.keycloak.issuer-uri=https://authdev.limit.es/realms/GOIB
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
spring.security.oauth2.client.registration.keycloak.client-id=goib-default
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code

spring.liquibase.enabled=false
spring.jpa.properties.hibernate.hbm2ddl.auto=none

es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl=https://authdev.limit.es
es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm=GOIB
es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id=goib-ws
es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret=KXbtEBU3kMpiekjvktZSmnpk5cGsnJNY

logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
#logging.level.org.hibernate.SQL=DEBUG
#logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

> Nota: Revisa credencials/URLs segons el teu entorn abans d'usar-les en producció.

---

## 3) Arrencar a JBoss

- Perfils de Maven: `jboss`, `front`
- `application.properties`: es reutilitza el mateix d'abans (apartat 2.2)

### 3.1) Fitxer `.env` (a l'arrel del projecte)

Crea un `.env` amb:

```
DB_URL=jdbc:oracle:thin:@10.35.3.77:1521:xe
DB_USERNAME=notib
DB_PASSWORD=notib

MAIL_HOST=correu.limit.es
MAIL_PORT=465
MAIL_USERNAME=proves_limit@limit.es
MAIL_PASSWORD=T0GjtFq8T2
MAIL_SSL=true
MAIL_TLS=true

AUTH_URL=https://authdev.limit.es
AUTH_REALM=GOIB
AUTH_CLIENTID=goib-default
AUTH_WS_CLIENTID=goib-ws
AUTH_CREDENTIAL_SECRET=KXbtEBU3kMpiekjvktZSmnpk5cGsnJNY

PLUGIN_USERINFO_KEYCLOAK_SERVER_URL=https://authdev.limit.es
PLUGIN_USERINFO_KEYCLOAK_REALM=GOIB
PLUGIN_USERINFO_KEYCLOAK_CLIENT_ID=goib-ws
PLUGIN_USERINFO_KEYCLOAK_PASSWD_SECRET=KXbtEBU3kMpiekjvktZSmnpk5cGsnJNY

DEBUG=true
```

### 3.2) `docker-compose.yml` (a l'arrel)

Utilitza el següent contingut (indentació YAML corregida):

```yaml
version: "3.8"

volumes:
  notib_files:
    driver: local

services:
  notib:
    image: notib:2.0.12
    ports:
      - "${APP_PORT:-8080}:8080"
      - "${DEBUG_PORT:-8787}:8787"
      - "${MGM_PORT:-9990}:9990"
    volumes:
      - notib_files:/home/jboss/apps/notib/files
    environment:
      - JAVA_OPTS=-Xms1303m -Xmx1303m -XX:MetaspaceSize=96M -XX:MaxMetaspaceSize=512m -Djava.net.preferIPv4Stack=true
      - JBOSS_APP_NAME=${APP_NAME:-notib}
      - JBOSS_DB_DRIVER=${DB_DRIVER:-oracle}
      - JBOSS_DB_URL=${DB_URL}
      - JBOSS_DB_USERNAME=${DB_USERNAME}
      - JBOSS_DB_PASSWORD=${DB_PASSWORD}
      - JBOSS_AUTH_URL=${AUTH_URL}
      - JBOSS_AUTH_REALM=${AUTH_REALM}
      - JBOSS_AUTH_CLIENTID=${AUTH_CLIENTID}
      - JBOSS_AUTH_WS_CLIENTID=${AUTH_WS_CLIENTID}
      - JBOSS_AUTH_CREDENTIAL_SECRET=${AUTH_CREDENTIAL_SECRET}
      - JBOSS_MAIL_HOST=${MAIL_HOST}
      - JBOSS_MAIL_PORT=${MAIL_PORT}
      - JBOSS_MAIL_USERNAME=${MAIL_USERNAME}
      - JBOSS_MAIL_PASSWORD=${MAIL_PASSWORD}
      - JBOSS_MAIL_SSL=${MAIL_SSL}
      - JBOSS_MAIL_TLS=${MAIL_TLS}
      - JBOSS_PROXY_HOST=${PROXY_HOST:-}
      - JBOSS_PROXY_PORT=${PROXY_PORT:-}
      - es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl=${PLUGIN_USERINFO_KEYCLOAK_SERVER_URL:-https://authdev.limit.es}
      - es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm=${PLUGIN_USERINFO_KEYCLOAK_REALM:-GOIB}
      - es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id=${PLUGIN_USERINFO_KEYCLOAK_CLIENT_ID:-goib-ws}
      - es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret=${PLUGIN_USERINFO_KEYCLOAK_PASSWD_SECRET:-}
      - es.caib.notib.front.api.url=${API_URL:-http://localhost:8080/notibback/api/}
      - DEBUG=${DEBUG:-}
      - DEBUG_PORT=${DEBUG_HOST:-0.0.0.0:}${DEBUG_PORT:-8787}
    restart: always
```

---

## 4) Resum ràpid

- Spring Boot sense front: perfils Maven `front,ide,oracle` (el front no es desplega automàticament).
- Spring Boot + dev server del front: perfils Maven `ide,oracle` i perfil Spring `devProxy`; accés via `http://localhost:8080/notibback/reactapp/`.
- `.env.local` al front amb URLs i dades d'auth de Keycloak.
- `application.properties` al backend amb BBDD, mail i Keycloak.
- Entorn JBoss: `.env` a l'arrel + `docker-compose.yml` proveït.

> Recorda: Entra sempre a través de l'URL del backend per garantir l'autenticació per sessió de Notib.
