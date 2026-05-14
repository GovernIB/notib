# Guia d'arrencada de Notib

Aquest document recull els fluxos habituals per arrencar Notib en local:

- Spring Boot amb el front empaquetat per Maven.
- Spring Boot amb el front React en mode desenvolupament.
- JBoss/EAP dins Docker.

Els exemples fan servir Oracle, Keycloak i SMTP externs. Substitueix sempre els valors d'host, usuari i secrets pels del teu entorn.

---

## 1) Requisits

- JDK 11.
- Maven 3.8 o superior.
- Accés a la base de dades configurada.
- Accés al servidor d'autenticació Keycloak.
- Docker o Docker Compose si s'arrenca l'EAR dins JBoss.

Per al front en mode desenvolupament també cal `npm`. El build Maven del perfil `front` instal·la Node automàticament amb `frontend-maven-plugin` dins `notib-back/target`.

---

## 2) Spring Boot amb front empaquetat

Aquest mode compila el front React i el deixa servit pel backend a `/notibback/reactapp/`.

Perfils Maven:

- `front`
- `ide`
- `oracle`

Compilació des de l'arrel del projecte:

```bash
mvn -pl notib-back -am package -Pfront,ide,oracle -DskipTests
```

Arrencada recomanada:

- Classe principal: `es.caib.notib.NotibBackBootApp`
- Perfil Maven: `front,ide,oracle`
- Perfil Spring: cap

Si tens els mòduls interns instal·lats al repositori Maven local, també pots arrencar només el mòdul del backoffice:

```bash
mvn -f notib-back/pom.xml spring-boot:run -Pfront,ide
```

URL principal:

```text
http://localhost:8080/notibback/reactapp/
```

---

## 3) Spring Boot amb front React en mode desenvolupament

Aquest mode deixa Vite servint el front, però l'entrada s'ha de fer sempre a través del backend. El backend fa de proxy mitjançant `DevProxyController`, actiu amb el perfil Spring `devProxy`.

Perfils Maven:

- `ide`
- `oracle`

Perfil Spring:

- `devProxy`

### 3.1) Arrencar el backend

Compilació des de l'arrel del projecte:

```bash
mvn -pl notib-back -am package -Pide,oracle -DskipTests
```

Arrencada recomanada:

- Classe principal: `es.caib.notib.NotibBackBootApp`
- Perfil Maven: `ide,oracle`
- Perfil Spring: `devProxy`

Si tens els mòduls interns instal·lats al repositori Maven local:

```bash
mvn -f notib-back/pom.xml spring-boot:run -Pide -Dspring-boot.run.profiles=devProxy
```

Si el dev server de Vite no escolta a `http://localhost:5173`, afegeix aquesta propietat al `application.properties` local:

```properties
es.caib.notib.development.proxyUrl=http://localhost:5173
```

### 3.2) Arrencar el front

Directori:

```text
notib-back/src/main/reactapp/notib-back
```

Primera vegada:

```bash
npm install
```

Arrencada:

```bash
npm run dev
```

### 3.3) `.env.local` del front

Crea `notib-back/src/main/reactapp/notib-back/.env.local`:

```dotenv
VITE_API_URL=http://localhost:8080/notibback/apinew
VITE_AUTH_URL=https://authdev.limit.es
VITE_AUTH_REALM=GOIB
VITE_AUTH_CLIENTID=goib-default
DISABLE_OPEN_ON_START=true
```

Important: entra sempre per l'URL del backend:

```text
http://localhost:8080/notibback/reactapp/
```

No entris directament a `http://localhost:5173`, perquè no s'aplicarà l'autenticació per sessió de Notib i les peticions al backend poden fallar.

---

## 4) `application.properties` local per Spring Boot

Per arrencar en local amb Spring Boot pots crear un `application.properties` a l'arrel del projecte. Aquest fitxer està ignorat per Git.

Exemple mínim:

```properties
spring.datasource.url=jdbc:oracle:thin:@HOST:1521:SID
spring.datasource.username=notib
spring.datasource.password=CHANGE_ME

spring.mail.host=smtp.example.org
spring.mail.port=465
spring.mail.username=user@example.org
spring.mail.password=CHANGE_ME
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.starttls.enable=false

spring.security.oauth2.client.provider.keycloak.issuer-uri=https://auth.example.org/realms/GOIB
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
spring.security.oauth2.client.registration.keycloak.client-id=goib-default
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code

spring.liquibase.enabled=false
spring.jpa.properties.hibernate.hbm2ddl.auto=none

es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl=https://auth.example.org
es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm=GOIB
es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id=goib-ws
es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret=CHANGE_ME
```

Per al front empaquetat, el backend exposa les variables Vite via `/notibback/sysenv?format=vite`. Si cal forçar-les des de properties:

```properties
es.caib.notib.front.api.url=http://localhost:8080/notibback/apinew
es.caib.notib.front.auth.url=https://auth.example.org
es.caib.notib.front.auth.realm=GOIB
es.caib.notib.front.auth.clientid=goib-default
```

---

## 5) JBoss/EAP amb Docker

La imatge Docker es genera des de `notib-ear/pom.xml` amb `io.fabric8:docker-maven-plugin`; no hi ha cap `Dockerfile` manual al repositori.

Documentació detallada:

```text
notib-ear/docker/README.md
```

Resum ràpid:

```bash
mvn -pl notib-ear -am clean package -Pdocker-eap72-caib-openshift -DskipTests
mvn -pl notib-ear docker:build -Pdocker-eap72-caib-openshift
docker compose up -d
```

Fitxers locals esperats a l'arrel del projecte:

- `.env`
- `docker-compose.yml`

Tots dos estan pensats com a configuració local d'entorn i no s'han de commitar amb secrets reals.

URLs habituals:

```text
http://localhost:8080/notibback/
http://localhost:8080/notibback/reactapp/
http://localhost:8080/notibapi/interna/
http://localhost:8080/notibapi/externa/
```

---

## 6) Resum de perfils

| Mode | Perfils Maven | Perfil Spring | Notes |
| --- | --- | --- | --- |
| Spring Boot amb front empaquetat | `front,ide,oracle` | cap | Maven compila el front i el serveix des del backend. |
| Spring Boot amb Vite | `ide,oracle` | `devProxy` | Entrar sempre per `/notibback/reactapp/`. |
| JBoss Docker | `docker-eap72-caib-openshift` | cap | Perfil Docker per defecte de `notib-ear`. |
| JBoss Docker legacy | `docker-legacy` | cap | Usa `docker/jboss` i `jboss_config.sh`. |
