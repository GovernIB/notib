create table not_acl_sid (
                             id bigint generated by default as identity(start with 100) not null primary key,
                             principal boolean not null,
                             sid varchar_ignorecase(100) not null,
                             constraint unique_uk_1 unique(sid,principal) );

create table not_acl_class (
                               id bigint generated by default as identity(start with 100) not null primary key,
                               class varchar_ignorecase(100) not null,
                               constraint unique_uk_2 unique(class) );

create table not_acl_object_identity (
                                         id bigint generated by default as identity(start with 100) not null primary key,
                                         object_id_class bigint not null,
                                         object_id_identity bigint not null,
                                         parent_object bigint,
                                         owner_sid bigint not null,
                                         entries_inheriting boolean not null,
                                         constraint unique_uk_3 unique(object_id_class,object_id_identity),
                                         constraint foreign_fk_1 foreign key(parent_object)references not_acl_object_identity(id),
                                         constraint foreign_fk_2 foreign key(object_id_class)references not_acl_class(id),
                                         constraint foreign_fk_3 foreign key(owner_sid)references not_acl_sid(id) );

create table not_acl_entry (
                               id bigint generated by default as identity(start with 100) not null primary key,
                               acl_object_identity bigint not null,ace_order int not null,sid bigint not null,
                               mask integer not null,granting boolean not null,audit_success boolean not null,
                               audit_failure boolean not null,
                               constraint unique_uk_4 unique(acl_object_identity,ace_order),
                               constraint foreign_fk_4 foreign key(acl_object_identity) references not_acl_object_identity(id),
                               constraint foreign_fk_5 foreign key(sid) references not_acl_sid(id) );

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