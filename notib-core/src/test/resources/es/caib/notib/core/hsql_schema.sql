create table not_usuari (
  codi varchar(64) not null,
  nom varchar(100),
  llinatges varchar(100),
  nom_sencer varchar(200),
  email varchar(200),
  version bigint not null,
  rebre_emails numeric(1, 0),
  idioma varchar(2),
  constraint not_usuari_pk primary key (codi)
);

create table not_entitat (
  id bigint generated by default as identity(start with 100) not null primary key,
  codi varchar(64) not null,
  nom varchar(256) not null,
  tipus varchar(32) not null,
  dir3_codi varchar(9) not null,
  descripcio varchar(1024),
  activa numeric(1, 0) not null,
  version bigint not null,
  createdby_codi varchar(64),
  createddate timestamp(6),
  lastmodifiedby_codi varchar(64),
  lastmodifieddate timestamp(6),
  api_key varchar(64),
  logo_cap blob,
  logo_peu blob,
  color_fons varchar(32),
  color_lletra varchar(32),
  tipus_doc_default numeric(32, 0),
  amb_entrega_deh numeric(1, 0) default 0,
  dir3_codi_reg varchar(9),
  amb_entrega_cie numeric(1, 0) default 0,
  amb_entrega_id bigint default null,
  llibre_entitat numeric(1, 0),
  oficina_entitat numeric(1, 0) default 1,
  llibreNom varchar(255),
  oficina varchar(255),
  constraint not_entitat_codi_uk unique (codi),
  constraint not_usucre_entitat_fk foreign key (createdby_codi) references not_usuari (codi),
  constraint not_usumod_entitat_fk foreign key (lastmodifiedby_codi) references not_usuari (codi)
);
create index not_usucre_entitat_fk_i on not_entitat(createdby_codi);
create index not_usumod_entitat_fk_i on not_entitat(lastmodifiedby_codi);


CREATE TABLE NOT_ORGAN_GESTOR
(
    id bigint generated by default as identity(start with 100) not null primary key,
    "CODI"           varchar(64)       NOT NULL,
    "ENTITAT"        numeric(19, 0),
    "NOM"            varchar(1000),
    "LLIBRE"         varchar(255),
    "LLIBRE_NOM"     varchar(255),
    "OFICINA"        varchar(255),
    "OFICINA_NOM"    varchar(255),
    "ESTAT"          varchar(1) DEFAULT 'V',
    "ENTREGA_CIE_ID" numeric(19, 0) DEFAULT NULL,
    "CODI_PARE"      varchar (32),
    "SIR"            numeric(1, 0) DEFAULT 0,
    constraint NOT_ORGAN_GESTOR_UK unique (codi),
    constraint NOT_ORGAN_ENTITAT_FK foreign key (ENTITAT) references NOT_ENTITAT (id)
);

create table not_aplicacio (
  id bigint generated by default as identity(start with 100) not null primary key,
  usuari_codi varchar(64) not null,
  callback_url varchar(256),
  createdby_codi varchar(64),
  createddate timestamp(6),
  lastmodifiedby_codi varchar(64),
  lastmodifieddate timestamp(6),
  entitat_id numeric(19, 0) not null,
  activa numeric(1, 0) default 1 not null,
  constraint not_usucre_aplicacio_fk foreign key (createdby_codi) references not_usuari (codi),
  constraint not_usumod_aplicacio_fk foreign key (lastmodifiedby_codi) references not_usuari (codi),
  constraint not_aplicacio_entitat_fk foreign key (entitat_id) references not_entitat (id)
);

CREATE TABLE NOT_CONFIG
(
    KEY                  varchar(256)     NOT NULL,
    VALUE                varchar(2048),
    DESCRIPTION          varchar(2048),
    GROUP_CODE           varchar(128)     DEFAULT 'GENERAL',
    POSITION             numeric(3, 0)    DEFAULT 0 NOT NULL,
    JBOSS_PROPERTY       numeric(1, 0)    DEFAULT 0 NOT NULL,
    TYPE_CODE            varchar(128)     DEFAULT 'TEXT',
    LASTMODIFIEDBY_CODI  varchar(64),
    LASTMODIFIEDDATE     TIMESTAMP(6),
    ENTITAT_CODI         varchar(64),
    CONFIGURABLE         numeric(1, 0)    DEFAULT 0 NOT NULL
);

CREATE TABLE NOT_CONFIG_GROUP
(
    CODE                 varchar(128)     NOT NULL,
    PARENT_CODE          varchar(128)     DEFAULT NULL,
    POSITION             numeric(3, 0)    DEFAULT 0 NOT NULL,
    DESCRIPTION          varchar(512)     NOT NULL
);

CREATE TABLE NOT_CONFIG_TYPE
(
    CODE                 varchar(128)     NOT NULL,
    VALUE                varchar(2048)   DEFAULT NULL
);

INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('BOOL');
INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('TEXT');
INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('INT');
INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('FLOAT');
INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('CRON');
INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('CREDENTIALS');

INSERT INTO NOT_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (0, 'GENERAL', 'Configuracions generals' );
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.metriques.generar', 'false');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasques.actives', 'false');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.registre.enviaments.periode', '300000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.registre.enviaments.retard.inicial', '3000000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.notifica.enviaments.periode', '300000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.notifica.enviaments.retard.inicial', '3000000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.enviament.actualitzacio.estat.periode', '300000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.enviament.actualitzacio.estat.retard.inicial', '3000000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.enviament.actualitzacio.estat.registre.periode', '300000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.enviament.actualitzacio.estat.registre.retard.inicial', '3000000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.actualitzacio.procediments.cron', '0 53 16 * * *');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.actualitzacio.serveis.cron', '0 58 16 * * *');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.refrescar.notificacions.expirades.cron', '0 0 0 * * ?');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.callback.pendents.periode', '300000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.callback.pendents.retard.inicial', '3000000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.enviament.deh.actualitzacio.certificacio.periode', '300000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.enviament.deh.actualitzacio.certificacio.retard.inicial', '3000000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.enviament.cie.actualitzacio.certificacio.periode', '432000000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.tasca.enviament.cie.actualitzacio.certificacio.retard.inicial', '3000000');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.plugin.unitats.dir3.protocol', 'REST');
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.plugin.unitats.fitxer', '');

INSERT INTO NOT_ENTITAT (id, codi, nom, tipus, dir3_codi, descripcio, activa, version, api_key,  tipus_doc_default, amb_entrega_deh, dir3_codi_reg, amb_entrega_cie, llibre_entitat, oficina_entitat) VALUES (1, 'ENTITAT_TESTS', 'ENTITAT_TESTS', 'GOVERN', 'EA0004518', 'Descripció', 1, 1, '', 1, 1, '', 0, 0, 0);

INSERT INTO NOT_ACL_CLASS (ID, CLASS) VALUES (4, 'es.caib.notib.core.entity.EntitatEntity');
INSERT INTO NOT_ACL_SID (ID, PRINCIPAL, SID) VALUES (5, 1, 'admin');
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (6, 4, 1, NULL, 5, 1);

-- Permís usuari
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (7, 6, 0, 5, 32, 1, 0, 0);
-- Permís administrador entitat
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (8, 6, 1, 5, 128, 1, 0, 0);

INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM) VALUES (2, 'A00000000', '1', 'òrgan per defecte');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM) VALUES (3, 'A04013511', '1', 'òrgan base de dades');
-- CREACIÓ D'UN ÒRGAN GESTOR SENSE CAP PERMÍS
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM) VALUES (86, 'A00000002', 1, 'òrgan sense permisos');

-- -- -- -- -- --
-- Permisos de l'òrgan gestor
-- -- -- -- -- --
INSERT INTO NOT_ACL_CLASS (ID, CLASS) VALUES (9, 'es.caib.notib.core.entity.OrganGestorEntity');
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (10, 9, 2, NULL, 5, 1);

INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (11, 10, 0, 5, 32, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (12, 10, 1, 5, 64, 1, 0, 0);




