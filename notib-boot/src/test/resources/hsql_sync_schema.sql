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

CREATE TABLE NOT_OG_SINC_REL
(
    "ANTIC_OG" numeric(38, 0) NOT NULL,
    "NOU_OG"   numeric(38, 0) NOT NULL,
    CONSTRAINT NOT_UO_SINC_REL_MULT_UK UNIQUE (ANTIC_OG, NOU_OG)
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
INSERT INTO NOT_CONFIG (KEY, VALUE) VALUES ('es.caib.notib.organs.consulta.canvis', null);

INSERT INTO NOT_ENTITAT (id, codi, nom, tipus, dir3_codi, descripcio, activa, version, api_key,  tipus_doc_default, amb_entrega_deh, dir3_codi_reg, amb_entrega_cie, llibre_entitat, oficina_entitat) VALUES (1, 'ENTITAT_TESTS', 'ENTITAT_TESTS', 'GOVERN', 'EA0004518', 'Descripció', 1, 1, '', 1, 1, '', 0, 0, 0);

INSERT INTO NOT_ACL_CLASS (ID, CLASS) VALUES (4, 'es.caib.notib.core.entity.EntitatEntity');
INSERT INTO NOT_ACL_SID (ID, PRINCIPAL, SID) VALUES (5, 1, 'admin');
INSERT INTO NOT_ACL_SID (ID, PRINCIPAL, SID) VALUES (6, 0, 'ROLE');
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (6, 4, 1, NULL, 5, 1);

-- Permís usuari
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (7, 6, 0, 5, 32, 1, 0, 0);
-- Permís administrador entitat
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (8, 6, 1, 5, 128, 1, 0, 0);


INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (-1, 'EA0004518', '1', 'Òrgan ARREL', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (0, 'A000', '1', 'Òrgan 000', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (1, 'A001', '1', 'Òrgan 001', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (2, 'A002', '1', 'Òrgan 002', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (3, 'A003', '1', 'Òrgan 003', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (4, 'A004', '1', 'Òrgan 004', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (5, 'A005', '1', 'Òrgan 005', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (6, 'A006', '1', 'Òrgan 006', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (7, 'A007', '1', 'Òrgan 007', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (8, 'A008', '1', 'Òrgan 008', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (9, 'A009', '1', 'Òrgan 009', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (10, 'A010', '1', 'Òrgan 010', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (11, 'A011', '1', 'Òrgan 011', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (12, 'A012', '1', 'Òrgan 012', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (13, 'A013', '1', 'Òrgan 013', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (14, 'A014', '1', 'Òrgan 014', 13, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (15, 'A015', '1', 'Òrgan 015', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (16, 'A016', '1', 'Òrgan 016', 15, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (17, 'A017', '1', 'Òrgan 017', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (18, 'A018', '1', 'Òrgan 018', 0, 'V');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM, CODI_PARE, ESTAT) VALUES (19, 'A019', '1', 'Òrgan 019', 0, 'V');

-- -- -- -- -- --
-- Permisos de l'òrgan gestor
-- -- -- -- -- --
INSERT INTO NOT_ACL_CLASS (ID, CLASS) VALUES (9, 'es.caib.notib.core.entity.OrganGestorEntity');
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (11, 9, 1, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (12, 9, 2, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (13, 9, 3, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (14, 9, 4, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (15, 9, 5, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (16, 9, 6, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (17, 9, 7, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (18, 9, 8, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (19, 9, 9, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (20, 9, 10, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (21, 9, 11, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (22, 9, 12, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (23, 9, 13, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (24, 9, 14, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (25, 9, 15, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (26, 9, 16, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (27, 9, 17, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (28, 9, 18, NULL, 5, 1);
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (29, 9, 19, NULL, 5, 1);

INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (51, 27, 1, 5, 64, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (52, 27, 2, 6, 512, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (53, 28, 1, 6, 2048, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (54, 29, 1, 6, 16, 1, 0, 0);


INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (11, 11, 0, 5, 1, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (12, 12, 0, 5, 1, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (13, 12, 1, 5, 16, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (14, 12, 2, 6, 64, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (15, 13, 0, 6, 1, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (16, 13, 1, 6, 512, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (17, 14, 0, 5, 64, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (18, 14, 1, 5, 1024, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (19, 14, 2, 6, 2048, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (20, 15, 0, 5, 2048, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (21, 15, 1, 5, 4096, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (22, 15, 2, 6, 1, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (23, 15, 3, 6, 64, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (24, 16, 0, 6, 16, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (25, 16, 1, 6, 512, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (26, 17, 0, 5, 512, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (27, 17, 1, 6, 16, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (28, 18, 0, 5, 1, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (29, 18, 1, 5, 16, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (30, 18, 2, 5, 64, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (31, 18, 3, 6, 64, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (32, 18, 4, 6, 512, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (33, 19, 0, 5, 1024, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (34, 19, 1, 6, 2048, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (35, 19, 2, 6, 4096, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (36, 20, 0, 5, 1, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (37, 20, 1, 5, 16, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (38, 20, 2, 6, 512, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (39, 21, 0, 5, 16, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (40, 21, 1, 5, 512, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (41, 21, 2, 6, 64, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (42, 22, 0, 5, 64, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (43, 22, 1, 5, 512, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (44, 23, 0, 6, 1, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (45, 23, 1, 6, 1024, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (46, 24, 0, 6, 1, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (47, 25, 0, 5, 16, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (48, 25, 1, 5, 64, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (49, 26, 0, 5, 16, 1, 0, 0);




