drop table NOT_CONFIG;
drop table NOT_CONFIG_GROUP;
drop table NOT_CONFIG_TYPE;

CREATE TABLE NOT_CONFIG
(
    KEY                  VARCHAR2(256 CHAR)     NOT NULL,
    VALUE                VARCHAR2(2048 CHAR),
--     DEFAULT_VALUE        VARCHAR2(2048 CHAR),
    DESCRIPTION          VARCHAR2(2048 CHAR),
    GROUP_CODE           VARCHAR2(128 CHAR)     NOT NULL,
    POSITION             NUMBER(3)              DEFAULT 0 NOT NULL,
    JBOSS_PROPERTY       NUMBER(1)              DEFAULT 0 NOT NULL,
    TYPE_CODE            VARCHAR2(128 CHAR)     DEFAULT 'TEXT',
    LASTMODIFIEDBY_CODI  VARCHAR2(64),
    LASTMODIFIEDDATE     TIMESTAMP(6)
);

CREATE TABLE NOT_CONFIG_GROUP
(
    CODE                 VARCHAR2(128 CHAR)     NOT NULL,
    PARENT_CODE          VARCHAR2(128 CHAR)     DEFAULT NULL,
    POSITION             NUMBER(3)              DEFAULT 0 NOT NULL,
    DESCRIPTION          VARCHAR2(512 CHAR)     NOT NULL
);

CREATE TABLE NOT_CONFIG_TYPE
(
    CODE                 VARCHAR2(128 CHAR)     NOT NULL,
    VALUE                VARCHAR2(2048 CHAR)   DEFAULT NULL
);

ALTER TABLE NOT_CONFIG ADD (
    CONSTRAINT NOT_CONFIG_PK PRIMARY KEY (KEY));

ALTER TABLE NOT_CONFIG_GROUP ADD (
    CONSTRAINT NOT_CONFIG_GROUP_PK PRIMARY KEY (CODE));

ALTER TABLE NOT_CONFIG_TYPE ADD (
    CONSTRAINT NOT_CONFIG_TYPE_PK PRIMARY KEY (CODE));


ALTER TABLE NOT_CONFIG
    ADD CONSTRAINT NOT_CONFIG_GROUP_FK FOREIGN KEY (GROUP_CODE) REFERENCES NOT_CONFIG_GROUP(CODE);


INSERT ALL
    INTO NOT_CONFIG_TYPE (CODE) VALUES ('BOOL')
    INTO NOT_CONFIG_TYPE (CODE) VALUES ('TEXT')
    INTO NOT_CONFIG_TYPE (CODE) VALUES ('INT')
    INTO NOT_CONFIG_TYPE (CODE) VALUES ('FLOAT')
    INTO NOT_CONFIG_TYPE (CODE) VALUES ('CRON')
    INTO NOT_CONFIG_TYPE (CODE) VALUES ('CREDENTIALS')
    INTO NOT_CONFIG_TYPE (CODE, VALUE) VALUES ('TIPUS_COMUNICACIO', 'ASINCRON,SINCRON')
    INTO NOT_CONFIG_TYPE (CODE, VALUE) VALUES ('NOTIFICA_VERSION', '0,1,2')
    INTO NOT_CONFIG_TYPE (CODE, VALUE) VALUES ('API_PROTOCOL', 'REST,SOAP')
    INTO NOT_CONFIG_TYPE (CODE, VALUE) VALUES ('PRIORITAT_ENVIAMENT_MASSIU', 'ALTA,BAIXA')
    INTO NOT_CONFIG_TYPE (CODE, VALUE) VALUES ('REGISTRE_CLASS', 'es.caib.notib.plugin.registre.RegistrePluginRegweb3Impl,es.caib.notib.plugin.registre.RegistrePluginMockImpl')
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (0, 'GENERAL', 'Configuracions generals' )
    INTO NOT_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (1, 'EMAIL', 'Enviament de correus' )
    INTO NOT_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (2, 'SCHEDULLED', 'Configuració de les tasques periòdiques' )
    INTO NOT_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (3, 'PLUGINS', 'Plugins de l''aplicació' )
    INTO NOT_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (4, 'GES_DOC', 'Gestió documental (Sistema de fitxers)' )
    INTO NOT_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (5, 'FIRMA', 'Firma' )
    INTO NOT_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (5, 'LOGS', 'Logs del servidor' )
    INTO NOT_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (6, 'ASPECTE', 'Aspecte per defecte de l''aplicació' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (1, 'GENERAL', 'TAULA_REMESES', 'Configuració de la taula de remeses' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (2, 'GENERAL', 'FORM_REMESES', 'Configuració del formulari de remeses' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (2, 'GENERAL', 'DOCUMENTS', 'Gestió dels documents' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (3, 'GENERAL', 'DEFAULT_VALUES', 'Valors per defecte configurables' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (1, 'ASPECTE', 'ASPECTE_JUSTIFICANT', 'Valors configurables del justificant' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (1, 'PLUGINS', 'REGISTRE', 'Configuració del registre' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (2, 'PLUGINS', 'NOTIFICA', 'Configuració de notific@' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (3, 'PLUGINS', 'DIR3', 'Configuració del plugin de DIR3' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (4, 'PLUGINS', 'ARXIU', 'Configuració de l''arxiu' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (7, 'PLUGINS', 'USUARIS', 'Configuració del plugin d''usuaris' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (9, 'PLUGINS', 'GESCONADM', 'Plugin de gestió documental administratiu (ROLSAC)' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (1, 'SCHEDULLED', 'SCHEDULLED_REGISTRE', 'Tasca periòdica d''enviament al registre' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (2, 'SCHEDULLED', 'SCHEDULLED_NOTIFICA', 'Tasca periòdica d''enviament a notifica' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (3, 'SCHEDULLED', 'SCHEDULLED_UPDATE_STATUS_NOTIFICA',
                                                                             'Tasca periòdica d''actualització de l''estat dels enviaments amb l''estat de Notific@' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (3, 'SCHEDULLED', 'SCHEDULLED_UPDATE_STATUS_SIR',
                                                                             'Tasca periòdica d''actualització de l''estat dels enviaments amb l''estat de enviat_sir' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (5, 'SCHEDULLED', 'SCHEDULLED_PROCEDIMENTS', 'Tasca periòdica d''actualització dels procediments' )
    INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (6, 'SCHEDULLED', 'SCHEDULLED_CALLBACK', 'Tasca periòdica de processament callback pendents' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.emprar.sir', 'true', 'Activar SIR', 'BOOL', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (1, 'es.caib.notib.adviser.actiu', 'true', 'Activar ADVISER', 'BOOL', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (2, 'es.caib.notib.comunicacio.tipus.defecte', 'ASINCRON',
                                                                                       'TIPUS DE COMUNICACIÓ DE LA PLATAFORMA (SINCRON/ASINCRON)',
                                                                                       'TIPUS_COMUNICACIO', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (3, 'es.caib.notib.default.user.language', 'ca',
                                                                                       'Llenguatge per defecte de l''aplicació',
                                                                                       'TEXT', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (4, 'es.caib.notib.metriques.generar', 'true', 'Generar mètriques', 'BOOL', 'GENERAL' )

    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (7, 'es.caib.notib.plugin.registre.generar.justificant', 'false',
                                                                                       'Indica si s''ha de generar el justificant del registre de totes les notificacions. ' ||
                                                                                       'Si es false només es generen per a comunicacions a administracions (enviaments SIR)',
                                                                                       'BOOL', 'GENERAL' )
INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (9, 'es.caib.notib.notifica.dir3.entitat.permes', 'false',
                                                                                   'Tractar l''entitat com un organ gestor més',
                                                                                   'BOOL', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (9, 'es.caib.notib.plugin.codi.dir3.entitat', 'false',
                                                                                       'Si s''activa l''organisme emissor de les notificacions és la mateixa l''entitat',
                                                                                       'BOOL', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (10, 'es.caib.notib.document.consulta.id.csv.mida.min', '16',
                                                                                       'Nombre mínim de caràcters que pot tenir un codi CSV',
                                                                                       'INT', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (11, 'es.caib.notib.app.base.url', '',
                                                                                       'Especificar la URL base de l''aplicació',
                                                                                       'TEXT', 'GENERAL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (11, 'es.caib.notib.enviament.massiu.prioritat', 'BAIXA',
                                                                                       'Indica si l''enviament massiu s''ha de prioritzar per a que s''enviin el més aviat possible, ' ||
                                                                                       'o si han de tenir una prioritat inferior que la resta de notificacions.',
                                                                                       'PRIORITAT_ENVIAMENT_MASSIU', 'GENERAL' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (5, 'es.caib.notib.documents.metadades.from.arxiu', 'false',
                                                                                           'Indica si volem consultar les metadades dels documents directament de l''arxiu. ' ||
                                                                                           'Si no està marcat les metadades son consultades a ConCSV',
                                                                                           'BOOL', 'DOCUMENTS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (6, 'es.caib.notib.notificacio.document.size', '10485760',
                                                                                       'Mida máxima permesa per a un document d''una notificació  (bytes)',
                                                                                       'INT', 'DOCUMENTS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (6, 'es.caib.notib.notificacio.document.total.size', '15728640',
                                                                                       'Mida máxima del conjunt de documents d''una notificació (bytes)',
                                                                                       'INT', 'DOCUMENTS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (7, 'es.caib.notib.document.metadades.por.defecto', 'true',
                                                                                       'Indica si usar valors per defecte quan el document no té metadades ' ||
                                                                                       'o no s''envien metadades en la crida a l''API REST d''alta notificació',
                                                                                       'BOOL', 'DOCUMENTS' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.email.jndi', 1, 'JNDI email', 'EMAIL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.email.remitent', 'bgalmes@limit.es', 'Remitent dels correus electrònics', 'EMAIL' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.email.footer', ' Notib - Govern de les Illes Balears', 'Text del peu dels correus electrònics', 'EMAIL' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasques.actives', 'true',
                                                                                       'Activa les tasques d''actualitzar estat (SIR i Notific@) i la d''enviament de callbacks de forma conjunta',
                                                                                       'BOOL', 'SCHEDULLED' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.registre.enviaments.periode', 1,
                                                                                                'Iterval de temps entre les execucions de la tasca (ms)',
                                                                                                'INT',
                                                                                                'SCHEDULLED_REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (1, 'es.caib.notib.tasca.registre.enviaments.processar.max', '10',
                                                                                          'Nombre màxim d''enviaments que es processaran cada cop que s''executi la tasca',
                                                                                          'INT',
                                                                                          'SCHEDULLED_REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.registre.enviaments.reintents.maxim', '3',
                                                                                       'Nombre màxim de vegades que una mateixa notificació s''intentarà registrar abans d''obtenir una resposta satisfactòria',
                                                                                       'INT',
                                                                                       'SCHEDULLED_REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (2, 'es.caib.notib.tasca.registre.enviaments.retard.inicial', 1,
                                                                                                'Temps a esperar per a executar la tasca per primera vegada un cop arrancat el servidor (ms)',
                                                                                                'INT',
                                                                                                'SCHEDULLED_REGISTRE' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.notifica.enviaments.actiu', 'true',
                                                                                          'Indica si la tasca periòdica està activa (true/false)',
                                                                                          'BOOL',
                                                                                          'SCHEDULLED_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.notifica.enviaments.processar.max', '10',
                                                                                       'Nombre màxim d''enviaments que es processaran cada cop que s''executi la tasca',
                                                                                       'INT',
                                                                                       'SCHEDULLED_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.notifica.enviaments.reintents.maxim', '3',
                                                                                       'Nombre màxim de vegades que una mateixa notificació s''intentarà enviar a notifica abans d''obtenir una resposta satisfactòria',
                                                                                       'INT',
                                                                                       'SCHEDULLED_NOTIFICA' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.notifica.enviaments.periode', 1,
                                                                                                'Iterval de temps entre les execucions de la tasca (ms)',
                                                                                                'INT',
                                                                                                'SCHEDULLED_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.notifica.enviaments.retard.inicial', 1,
                                                                                                'Temps a esperar per a executar la tasca per primera vegada un cop arrancat el servidor (ms)',
                                                                                                'INT',
                                                                                                'SCHEDULLED_NOTIFICA' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.enviament.actualitzacio.estat.actiu', 'true',
                                                                                       'Indica si la tasca periòdica està activa (true/false)',
                                                                                       'BOOL',
                                                                                       'SCHEDULLED_UPDATE_STATUS_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.enviament.actualitzacio.estat.processar.max',
                                                                                       '10',
                                                                                       'Nombre màxim d''enviaments que es processaran cada cop que s''executi la tasca',
                                                                                       'INT',
                                                                                       'SCHEDULLED_UPDATE_STATUS_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.enviament.actualitzacio.estat.reintents.maxim', '3',
                                                                                       'Nombre màxim de vegades que una mateixa notificació s''intentarà enviar a notifica abans d''obtenir una resposta satisfactòria',
                                                                                       'INT',
                                                                                       'SCHEDULLED_UPDATE_STATUS_NOTIFICA' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.enviament.actualitzacio.estat.periode', 1,
                                                                                                'Iterval de temps entre les execucions de la tasca (ms)',
                                                                                                'INT',
                                                                                                'SCHEDULLED_UPDATE_STATUS_NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.enviament.actualitzacio.estat.retard.inicial', 1,
                                                                                                'Temps a esperar per a executar la tasca per primera vegada un cop arrancat el servidor (ms)',
                                                                                                'INT',
                                                                                                'SCHEDULLED_UPDATE_STATUS_NOTIFICA' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.enviament.actualitzacio.estat.registre.actiu', 'true',
                                                                                       'Indica si la tasca periòdica està activa (true/false)',
                                                                                       'BOOL',
                                                                                       'SCHEDULLED_UPDATE_STATUS_SIR' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.enviament.actualitzacio.estat.registre.processar.max',
                                                                                       '10',
                                                                                       'Nombre màxim d''enviaments que es processaran cada cop que s''executi la tasca',
                                                                                       'INT',
                                                                                       'SCHEDULLED_UPDATE_STATUS_SIR' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.enviament.actualitzacio.estat.registre.reintents.maxim', '3',
                                                                                       'Nombre màxim de vegades que una mateixa notificació s''intentarà enviar a notifica abans d''obtenir una resposta satisfactòria',
                                                                                       'INT',
                                                                                       'SCHEDULLED_UPDATE_STATUS_SIR' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.enviament.actualitzacio.estat.registre.periode', 1,
                                                                                                'Iterval de temps entre les execucions de la tasca (ms)',
                                                                                                'INT',
                                                                                                'SCHEDULLED_UPDATE_STATUS_SIR' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.enviament.actualitzacio.estat.registre.retard.inicial', 1,
                                                                                                'Temps a esperar per a executar la tasca per primera vegada un cop arrancat el servidor (ms)',
                                                                                                'INT',
                                                                                                'SCHEDULLED_UPDATE_STATUS_SIR' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.callback.pendents.actiu', 'true',
                                                                                       'Indica si la tasca periòdica està activa (true/false)',
                                                                                       'BOOL',
                                                                                       'SCHEDULLED_CALLBACK' )

    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.callback.pendents.processar.max',
                                                                                       '50',
                                                                                       'Nombre màxim d''enviaments que es processaran cada cop que s''executi la tasca',
                                                                                       'INT',
                                                                                       'SCHEDULLED_CALLBACK' )

    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.callback.pendents.notifica.events.intents.max',
                                                                                       '10',
                                                                                       'Nombre màxim de vegades que s''intentarà enviar un callback sense obtenir una resposta satisfactòria',
                                                                                       'INT',
                                                                                       'SCHEDULLED_CALLBACK' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.callback.pendents.periode', 1, '30000',
                                                                                                'Iterval de temps entre les execucions de la tasca (ms)',
                                                                                                'INT',
                                                                                                'SCHEDULLED_CALLBACK' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.tasca.callback.pendents.retard.inicial', 1,
                                                                                                'Temps a esperar per a executar la tasca per primera vegada un cop arrancat el servidor (ms)',
                                                                                                'INT',
                                                                                                'SCHEDULLED_CALLBACK' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.refrescar.notificacions.expirades.cron', 1, '0 0 0 * * ?',
                                                                                                       'Especificar l''expressió ''cron'' indicant l''interval de temps que vols que es refresquin les notificacions expirades',
                                                                                                       'CRON',
                                                                                                       'SCHEDULLED' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.actualitzacio.procediments.actiu', 'true',
                                                                                       'Indica si la tasca periòdica està activa (true/false)',
                                                                                       'BOOL',
                                                                                       'SCHEDULLED_PROCEDIMENTS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.actualitzacio.procediments.modificar', 'true',
                                                                                       'Indica si la tasca periòdica ha d''actualitzar els procediments',
                                                                                       'BOOL',
                                                                                       'SCHEDULLED_PROCEDIMENTS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.actualitzacio.procediments.eliminar.organs', 'false',
                                                                                       'Indica si la tasca periòdica ha d''eliminar els òrgans gestors que han quedat en desús.',
                                                                                       'BOOL',
                                                                                       'SCHEDULLED_PROCEDIMENTS' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.actualitzacio.procediments.cron', 1, '0 53 16 * * *',
                                                                                                       'Especificar l''expressió ''cron'' indicant la freqüencia en que s''han d''actualitzar els procediments',
                                                                                                       'CRON',
                                                                                                       'SCHEDULLED_PROCEDIMENTS' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.arxiu.class', 'es.caib.notib.plugin.arxiu.ArxiuPluginConcsvImpl', 'Classe Arxiu', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (1, 'es.caib.notib.plugin.arxiu.gestionar.expedients', 'true', 'Gestionar expedients', 'BOOL', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (2, 'es.caib.notib.plugin.arxiu.gestionar.documents', 'true', 'Gestionar documents', 'BOOL', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (3, 'es.caib.notib.plugin.arxiu.gestionar.carpetes', 'true', 'Gestionar carpetes', 'BOOL', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (4, 'es.caib.notib.plugin.arxiu.document.versionable', 'true', 'Document versionable', 'BOOL', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (5, 'es.caib.notib.plugin.arxiu.suporta.metadades', 'true', 'Suporta metadades', 'BOOL', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (6, 'es.caib.notib.plugin.arxiu.escriptori.classificacio', '000000', 'Escriptori classificació', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (7, 'es.caib.notib.plugin.arxiu.escriptori.serie.documental', 'S0001', 'Escriptori serie documental', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (8, 'es.caib.notib.plugin.arxiu.caib.aplicacio.codi', 'NOTIB', 'Codi aplicació', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (9, 'es.caib.notib.plugin.arxiu.caib.csv.definicio', 'CSV_DEF', 'CSV definició', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (10, 'es.caib.notib.plugin.arxiu.caib.timeout.connect', '20000', 'Timeout connect', 'INT', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (11, 'es.caib.notib.plugin.arxiu.caib.timeout.read', '20000', 'Timeout read', 'INT', 'ARXIU' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.arxiu.csv.base.url', 1,
                                                                                     'Url base CSV', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.arxiu.caib.base.url', 1,
                                                                                     'Url base ARXIU', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.arxiu.caib.usuari', 1, 'Usuari arxiu', 'CREDENTIALS', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.arxiu.caib.contrasenya', 1, 'Password arxiu', 'CREDENTIALS', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.arxiu.caib.conversio.imprimible.url.uuid', 1, 'Url on generar imprimible per UUID', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.arxiu.caib.conversio.imprimible.url.csv', 1, 'Url on generar imprimible per CSV', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.arxiu.caib.conversio.imprimible.usuari', 1, 'Usuari per a generar imprimible', 'CREDENTIALS', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.arxiu.caib.conversio.imprimible.contrasenya', 1, 'Password per a generar imprimible', 'CREDENTIALS', 'ARXIU' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.arxiu.verificacio.baseurl', 1, 'URL base per a la verificació de firmes', 'ARXIU' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.registre.class', 'es.caib.notib.plugin.registre.RegistrePluginRegweb3Impl',
                                                                            'Classe Registre', 'REGISTRE_CLASS', 'REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (1, 'es.caib.notib.plugin.registre.namespaceuri', 'urn:es:caib:regweb:ws:v1:services',
                                                                            'URI del namespace del registre', 'REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (2, 'es.caib.notib.plugin.registre.service.name', 'RegwebFacadeService',
                                                                            'Nom del servei del registre', 'REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (3, 'es.caib.notib.plugin.registre.port.name', 'RegwebFacade',
                                                                            'Nom del port del registre', 'REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (4, 'es.caib.notib.plugin.registre.segons.entre.peticions', '30',
                                                                                       'Especificar el nombre mínim de segons que hi pot haver entre peticions per obtenir justificant al registre',
                                                                                       'INT', 'REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (5, 'es.caib.notib.plugin.registre.documents.enviar', 'true',
                                                                                       'Indicar si s''han d''enviar els documents al registre',
                                                                                       'BOOL', 'REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (6, 'es.caib.notib.plugin.regweb.mock.sequencia', 1,
                                                                            'Ruta a un arxiu de text amb la sequencia per al mock del registre', 'REGISTRE' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (7, 'es.caib.notib.plugin.regweb.mock.justificant', 1,
                                                                            'Ruta a un arxiu pdf amb el justificant per al mock del registre', 'REGISTRE' )
SELECT 1 FROM DUAL;


INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.notifica.versio', '2',
                                                                                       'Versió de notifica a utilitzar: 0 (Mock), 1 o 2',
                                                                                       'NOTIFICA_VERSION', 'NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.notifica.apikey', '', 'Api key Notific@', 'NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.notifica.clau.xifrat.ids', 'P0rt4FI8',
                                                                            'Clau per al xifrat dels identificadors de les notificacions',
                                                                            'NOTIFICA' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.notifica.url',1,
                                                                                     'Url de notific@', 'NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.notifica.sede.url', 1,
                                                                                     'Url de SEDE de notific@', 'NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.notifica.username', 1,
                                                                                                'Nom de l''usuari per a accedir a Notific@',
                                                                                                'CREDENTIALS', 'NOTIFICA' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.notifica.password', 1,
                                                                                                'Password per a accedir a Notific@',
                                                                                                'CREDENTIALS', 'NOTIFICA' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.gesdoc.class',
                                                                            'es.caib.notib.plugin.gesdoc.GestioDocumentalPluginFilesystem',
                                                                            'Nom de la classe per a gestionar l''emmagatzament de documents',
                                                                            'GES_DOC' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.gesdoc.filesystem.base.dir',
                                                                            '',
                                                                            'Directori del sistema de fitxers on emmagatzemar els documents',
                                                                            'GES_DOC' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.dades.usuari.class',
                                                                            'es.caib.notib.plugin.usuari.DadesUsuariPluginJdbc',
                                                                            'Classe per a gestionar l''accés al plugin d''usuaris',
                                                                            'USUARIS' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (1, 'es.caib.notib.plugin.dades.usuari.jdbc.datasource.jndi.name', 1,
                                                                            'Datasource dels usuaris',
                                                                            'USUARIS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (2, 'es.caib.notib.plugin.dades.usuari.jdbc.query.codi',
                                                                            'select usu_codi, usu_nom, usu_nif, usu_codi||''@limit.es'' from sc_wl_usuari where usu_codi=:codi',
                                                                            'Consulta d''usuaris per codi',
                                                                            'USUARIS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (3, 'es.caib.notib.plugin.dades.usuari.jdbc.query.nif',
                                                                            'select usu_codi, usu_nom, usu_nif, usu_codi||''@limit.es'' from sc_wl_usuari where usu_nif=:nif',
                                                                            'Consulta d''usuaris per nif',
                                                                            'USUARIS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (4, 'es.caib.notib.plugin.dades.usuari.jdbc.query.rols',
                                                                            'select ugr_codgru from sc_wl_usugru where ugr_codusu=:codi',
                                                                            'Consulta d''usuaris per rols',
                                                                            'USUARIS' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (5, 'es.caib.notib.plugin.dades.usuari.jdbc.query.grup',
                                                                            'select usu_codi, usu_nom, usu_nif, ''josepg@limit.es'' from sc_wl_usuari u, sc_wl_usugru g where g.ugr_codgru=:grup and g.ugr_codusu=u.usu_codi',
                                                                            'Consulta d''usuaris per grup',
                                                                            'USUARIS' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.capsalera.logo', '',
                                                                            'Especificar la ruta de la imatge per a usar a la capsalera com a logo de l''aplicació per defecte',
                                                                            'ASPECTE' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (1, 'es.caib.notib.peu.logo', '',
                                                                            'Especificar la ruta de la imatge per a usar al peu com a logo de l''aplicació per defecte',
                                                                            'ASPECTE' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (2, 'es.caib.notib.capsalera.color.fons', '',
                                                                            'Especificar el color del fons de l''aplicació a usar per defecte',
                                                                            'ASPECTE' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (3, 'es.caib.notib.capsalera.color.lletra', '',
                                                                            'Especificar el color de la lletra de l''aplicació a usar per defecte',
                                                                            'ASPECTE' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.justificant.capsalera.direccio', '',
                                                                            'Especificar la direcció postal a mostrar al justificant',
                                                                            'ASPECTE_JUSTIFICANT' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (1, 'es.caib.notib.justificant.capsalera.nif', '',
                                                                            'Especificar el NIF a mostrar al justificant',
                                                                            'ASPECTE_JUSTIFICANT' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (2, 'es.caib.notib.justificant.capsalera.codi', '',
                                                                            'Especificar el codi a mostrar al justificant',
                                                                            'ASPECTE_JUSTIFICANT' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (3, 'es.caib.notib.justificant.capsalera.email', '',
                                                                            'Especificar el correu electrònic a mostrar al justificant',
                                                                            'ASPECTE_JUSTIFICANT' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (4, 'es.caib.notib.justificant.peu.titol', 'NOTIB \u2013 Justificant d\u0027enviament de notificaci\u00F3 electr\u00F2nica',
                                                                            'Especificar el text a mostrar al peu del justificant',
                                                                            'ASPECTE_JUSTIFICANT' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (5, 'es.caib.notib.justificant.peu.logo', '',
                                                                            'Especificar path del logo a mostrar al peu del justificant',
                                                                            'ASPECTE_JUSTIFICANT' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (6, 'es.caib.notib.justificant.capsalera.logo', '',
                                                                            'Especificar path del logo a mostrar a la capçalera del justificant',
                                                                            'ASPECTE_JUSTIFICANT' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.unitats.class', 'es.caib.notib.plugin.unitat.UnitatsOrganitzativesPluginDir3',
                                                                            'Especificar la classe per a accedir a les unitats organitzatives',
                                                                            'DIR3' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.unitats.fitxer', '',
                                                                            'Path del fitxer on vols guardar l''organigrama DIR3 per evitar consultes innecessaries. ' ||
                                                                            'Si no s''especifica sempre que no estigui a cache es consultarà a la API de DIR3.',
                                                                            'DIR3' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.unitats.dir3.protocol', '',
                                                                            'REST',
                                                                            'API_PROTOCOL',
                                                                            'DIR3' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.unitats.dir3.url', 1,
                                                                                     'Url per accedir als organismes',
                                                                                     'DIR3' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.unitats.dir3.username', 1,
                                                                                                'Nom de l''usuari per a accedir a DIR3',
                                                                                                'CREDENTIALS', 'DIR3' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.unitats.dir3.password', 1,
                                                                                                'Password per a accedir a DIR3',
                                                                                                'CREDENTIALS', 'DIR3' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.gesconadm.class', 'es.caib.notib.plugin.gesconadm.GestorContingutsAdministratiuPluginRolsac',
                                                                            'Especificar la classe per a accedir al contingut administratiu',
                                                                            'GESCONADM' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.gesconadm.basic.authentication', 'false',
                                                                                       'Indicar si authenticació és basic',
                                                                                       'BOOL', 'GESCONADM' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.gesconadm.base.url', 1,
                                                                                     'Url per accedir a gestió documental administratiu',
                                                                                     'GESCONADM' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.gesconadm.username', 1,
                                                                                                'Nom de l''usuari per a accedir a gestió documental administratiu',
                                                                                                'CREDENTIALS', 'GESCONADM' )
    INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.gesconadm.password', 1,
                                                                                                'Password per a accedir a gestió documental administratiu',
                                                                                                'CREDENTIALS', 'GESCONADM' )
SELECT 1 FROM DUAL;


INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.procediment.alta.auto.retard', '10',
                                                                                       'Valor per defecte del retard d''un procediment',
                                                                                       'INT', 'DEFAULT_VALUES' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (1, 'es.caib.notib.procediment.alta.auto.caducitat', '15',
                                                                                       'Valor per defecte de la caducitat d''un procediment',
                                                                                       'INT', 'DEFAULT_VALUES' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.columna.entitat', 'false',
                                                                                       'Mostrar columna entitat',
                                                                                       'BOOL', 'TAULA_REMESES' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (1, 'es.caib.notib.columna.num.expedient', 'true',
                                                                                       'Mostrar columna expedient',
                                                                                       'BOOL', 'TAULA_REMESES' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.destinatari.multiple', 'false',
                                                                                       'Permetre múltiples destinataris',
                                                                                       'BOOL', 'FORM_REMESES' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (1, 'es.caib.notib.titular.incapacitat', 'true',
                                                                                       'Permetre titular amb discapacitat',
                                                                                       'BOOL', 'FORM_REMESES' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.firmaservidor.class', 'es.caib.notib.plugin.firmaservidor.FirmaServidorPluginPortafib',
                                                                            'Especificar la classe per a gestionar la firma del servidor',
                                                                            'FIRMA' )

    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.firmaservidor.portafib.username', '',
                                                                            'Nom usuari firma portafib',
                                                                            'FIRMA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.firmaservidor.portafib.location', 'Palma',
                                                                            'Ubicació de la firma portafib',
                                                                            'FIRMA' )
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, GROUP_CODE) VALUES (0, 'es.caib.notib.plugin.firmaservidor.portafib.signerEmail', 'suport@caib.es',
                                                                            'Correu electrònic del firmant',
                                                                            'FIRMA' )
SELECT 1 FROM DUAL;

INSERT ALL
    INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.permisos.organ.logs', 'false',
                                                                            'Indica si s''han de generar els logs cada vegada que es consulten els permisos dels organs' ,
                                                                            'BOOL', 'LOGS' )
SELECT 1 FROM DUAL;
commit;

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_CONFIG TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_CONFIG_GROUP TO WWW_NOTIB;