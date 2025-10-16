
-- Changeset db/changelog/changes/2.0.11/1001.yaml::1634114082437-1::limit
UPDATE NOT_CONFIG_GROUP SET description = 'Plugin de registre' WHERE code = 'REGISTRE';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin de Notifica' WHERE code = 'NOTIFICA';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin de Directori comú d''unitats organitzatives (DIR3)' WHERE code = 'DIR3';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin d''usuaris' WHERE code = 'USUARIS';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin de notificacions mòbils (API CARPETA)' WHERE code = 'CARPETA';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin d''inventari de procediments i serveis (ROLSAC)' WHERE code = 'GESCONADM';

UPDATE NOT_CONFIG_GROUP SET description = 'Plugin de centre d''impressió i ensobrat (CIE)' WHERE code = 'CIE';

-- Changeset db/changelog/changes/2.0.11/992.yaml::1634114082437-1::limit
CREATE TABLE NOT_ACCIO_MASSIVA (ID NUMBER(19, 0) NOT NULL, tipus VARCHAR2(50 CHAR), data_inici TIMESTAMP(6), data_fi TIMESTAMP(6), createdby_codi VARCHAR2(64 CHAR), LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), LASTMODIFIEDDATE TIMESTAMP(6), createddate TIMESTAMP(6), entitat_id NUMBER(38, 0), error NUMBER(1), num_errors INTEGER, error_descripcio VARCHAR2(1024 CHAR), excepcio_stacktrace VARCHAR2(2048 CHAR), CONSTRAINT NOT_ACC_MASS_PK PRIMARY KEY (ID));

CREATE TABLE NOT_ACCIO_MASSIVA_ELEMENT (ID NUMBER(19, 0) NOT NULL, ACCIO_MASSIVA_ID NUMBER(19, 0), seleccio_tipus VARCHAR2(20 CHAR), element_id NUMBER(19, 0), data_execucio TIMESTAMP(6), error_descripcio VARCHAR2(1024 CHAR), excepcio_stacktrace VARCHAR2(2048 CHAR), CONSTRAINT NOT_ACC_MASS_ELEM_PK PRIMARY KEY (ID));

ALTER TABLE NOT_ACCIO_MASSIVA_ELEMENT ADD CONSTRAINT FK_ACCIOMASSIVA_ELEMENT FOREIGN KEY (ACCIO_MASSIVA_ID) REFERENCES NOT_ACCIO_MASSIVA (ID);

-- Changeset db/changelog/changes/2.0.11/comanda.yaml::1634114082437-1::limit
INSERT INTO NOT_CONFIG_GROUP (CODE, PARENT_CODE, POSITION, DESCRIPTION) VALUES ('COMANDA', 'PLUGINS', 12, 'Plugin de Comanda');

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.plugin.comanda.url', 'URL del plugin de Comanda', '', 'COMANDA', 1, 'TEXT', 0, '0');

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.log.tipus.COMANDA', 'false', 'Mostrar logs del plugin de Comanda', 'LOGS', 23, 0, 'BOOL', 0);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.plugin.comanda.entorn.codi', '', 'Codi entorn enviat a Comanda', 'COMANDA', 0, 'TEXT', 0, '0');

-- Changeset db/changelog/changes/2.0.11/estadistiques_basiques.yml::modify-columns-not-explot-fet::user
ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_PENDENT NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_REG_ERR NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_REGISTR NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_SIR_ACC NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_SIR_REB NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_NOT_ERR NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_NOT_ENV NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_NOT_NOT NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_NOT_REB NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_NOT_EXP NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_CIE_ERR NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_CIE_ENV NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_CIE_NOT NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_CIE_REB NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_CIE_FAL NUMBER(38, 0);

ALTER TABLE NOT_EXPLOT_FET MODIFY TOT_PROCESS NUMBER(38, 0);

-- Changeset db/changelog/changes/2.0.11/estadistiques_basiques.yml::add-calculated-columns::user
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD CREATED_DIA VARCHAR2(255) DEFAULT TRUNC(CREATEDDATE);

ALTER TABLE NOT_NOTIFICACIO_TABLE ADD ENVIADA_DIA VARCHAR2(255) DEFAULT TRUNC(ENVIADA_DATE);

ALTER TABLE NOT_NOTIFICACIO_ENV_TABLE ADD REGISTRE_DIA VARCHAR2(255) DEFAULT TRUNC(REGISTRE_DATA);

ALTER TABLE NOT_NOTIFICACIO_ENV ADD ESTAT_DIA VARCHAR2(255) DEFAULT TRUNC(NOTIFICA_ESTAT_DATAACT);

ALTER TABLE NOT_NOTIFICACIO_ENV ADD ESTAT_NOM VARCHAR2(255) DEFAULT CASE WHEN NOTIFICA_ESTAT = 0 THEN 'PENDENT' WHEN NOTIFICA_ESTAT = 10 THEN 'EXPIRADA' WHEN NOTIFICA_ESTAT IN (13, 14) THEN 'NOTIFICADA' WHEN NOTIFICA_ESTAT = 20 THEN 'REBUTJADA' WHEN NOTIFICA_ESTAT = 24 THEN 'PEND_ENV' WHEN NOTIFICA_ESTAT IN (2, 3, 9, 11, 12, 21) THEN 'NOT_ERR' ELSE 'ALTRES' END;

ALTER TABLE NOT_NOTIFICACIO_ENV ADD SIR_DIA VARCHAR2(255) DEFAULT TRUNC(SIR_REC_DATA);

ALTER TABLE NOT_NOTIFICACIO_ENV ADD SIR_ESTAT VARCHAR2(255) DEFAULT CASE WHEN ESTAT_REGISTRE = 9 THEN 'REBUTJADA' WHEN ESTAT_REGISTRE = 5 THEN 'NOTIFICADA' ELSE 'ALTRES' END;

-- Changeset db/changelog/changes/2.0.11/estadistiques_basiques.yml::create-indexes::user
CREATE INDEX NOT_CREATED_DIA_I ON NOT_NOTIFICACIO_TABLE(CREATED_DIA);

CREATE INDEX NOT_ENVIADA_DIA_I ON NOT_NOTIFICACIO_TABLE(ENVIADA_DIA);

CREATE INDEX NOT_REGISTRE_DIA_I ON NOT_NOTIFICACIO_ENV_TABLE(REGISTRE_DIA);

CREATE INDEX NOT_ESTAT_DIA_I ON NOT_NOTIFICACIO_ENV(ESTAT_DIA);

CREATE INDEX NOT_ESTAT_I ON NOT_NOTIFICACIO_ENV(ESTAT_NOM);

CREATE INDEX NOT_SIR_DIA_I ON NOT_NOTIFICACIO_ENV(SIR_DIA);

CREATE INDEX NOT_SIR_ESTAT_I ON NOT_NOTIFICACIO_ENV(SIR_ESTAT);

-- Changeset db/changelog/changes/2.0.11/estadistiques_basiques.yml::create-views::user
CREATE VIEW NOT_CREADES_ENVIADES_VIEW AS WITH notificacions AS (
    SELECT
        nt.entitat_id,
        n.procediment_id,
        o.codi AS organ_codi,
        nt.usuari_codi,
        nt.env_tipus,
        n.origen,
        nt.CREATED_DIA,
        nt.ENVIADA_DIA
    FROM NOT_NOTIFICACIO_TABLE nt
             JOIN NOT_NOTIFICACIO n ON nt.id = n.id
             JOIN NOT_ORGAN_GESTOR o ON n.organ_gestor = o.id
    WHERE nt.CREATED_DIA IS NOT NULL OR nt.ENVIADA_DIA IS NOT NULL
)
                                         SELECT entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, 'CREADA' AS tipus, CREATED_DIA AS dia, COUNT(*) AS total
                                         FROM notificacions
                                         WHERE CREATED_DIA IS NOT NULL
                                         GROUP BY entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, CREATED_DIA
                                         UNION ALL
                                         SELECT entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, 'ENVIADA' AS tipus, ENVIADA_DIA AS dia, COUNT(*) AS total
                                         FROM notificacions
                                         WHERE ENVIADA_DIA IS NOT NULL
                                         GROUP BY entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, ENVIADA_DIA;

CREATE VIEW NOT_REGISTRADES_VIEW AS WITH notificacions AS (
    SELECT
        et.entitat_id,
        n.procediment_id,
        o.codi as organ_codi,
        et.usuari_codi,
        et.tipus_enviament as env_tipus,
        n.origen,
        et.REGISTRE_DIA
    FROM NOT_NOTIFICACIO_ENV_TABLE et
             JOIN NOT_NOTIFICACIO n ON et.notificacio_id = n.id
             JOIN NOT_ORGAN_GESTOR o ON n.organ_gestor = o.id
    WHERE et.REGISTRE_DIA IS NOT NULL
)
                                    SELECT entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, 'REGISTRADA' AS tipus, REGISTRE_DIA AS dia, COUNT(*) AS total
                                    FROM notificacions
                                    WHERE REGISTRE_DIA IS NOT NULL
                                    GROUP BY entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, REGISTRE_DIA;

CREATE VIEW NOT_ESTAT_VIEW AS WITH notificacions AS (
    SELECT
        n.entitat_id,
        n.procediment_id,
        o.codi as organ_codi,
        n.usuari_codi,
        n.env_tipus,
        n.origen,
        e.ESTAT_NOM as estat,
        nt.error_last_event as error,
        e.ESTAT_DIA
    FROM NOT_NOTIFICACIO_ENV e
             JOIN NOT_NOTIFICACIO n ON e.notificacio_id = n.id
             JOIN NOT_NOTIFICACIO_TABLE nt ON nt.id = n.id
             JOIN NOT_ORGAN_GESTOR o ON n.organ_gestor = o.id
    WHERE et.REGISTRE_DIA IS NOT NULL
)
                              SELECT entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, CASE WHEN error = 1 AND estat = 'PENDENT' THEN 'REG_ERR' WHEN error = 1 AND estat = 'PEND_ENV' THEN 'ENV_ERR' ELSE estat END AS tipus, ESTAT_DIA AS dia, COUNT(*) AS total
                              FROM notificacions
                              WHERE ESTAT_DIA IS NOT NULL
                              GROUP BY entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, estat, error, ESTAT_DIA;

CREATE VIEW NOT_SIR_VIEW AS WITH notificacions AS (
    SELECT
        n.entitat_id,
        n.procediment_id,
        o.codi as organ_codi,
        n.usuari_codi,
        n.env_tipus,
        n.origen,
        e.SIR_ESTAT as estat,
        e.SIR_DIA
    FROM NOT_NOTIFICACIO_ENV e
             JOIN NOT_NOTIFICACIO_ENV_TABLE et on et.id = e.id
             JOIN NOT_NOTIFICACIO n ON e.notificacio_id = n.id
             JOIN NOT_ORGAN_GESTOR o ON n.organ_gestor = o.id
    WHERE e.SIR_DIA IS NOT NULL AND e.registre_estat_final = 1
)
                            SELECT entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, estat AS tipus, SIR_DIA AS dia, COUNT(*) AS total
                            FROM notificacions
                            WHERE SIR_DIA IS NOT NULL
                            GROUP BY entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, estat, SIR_DIA;

CREATE VIEW NOT_STATS_VIEW AS SELECT entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, tipus, dia, total FROM NOT_CREADES_ENVIADES_VIEW
                              UNION ALL
                              SELECT entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, tipus, dia, total FROM NOT_REGISTRADES_VIEW
                              UNION ALL
                              SELECT entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, tipus, dia, total FROM NOT_ESTAT_VIEW
                              UNION ALL
                              SELECT entitat_id, procediment_id, organ_codi, usuari_codi, env_tipus, origen, tipus, dia, total FROM NOT_SIR_VIEW;

ALTER TABLE not_explot_fet DISABLE CONSTRAINT FK_NOT_EXPLOT_FET_TEMPS;
TRUNCATE TABLE NOT_EXPLOT_FET;
TRUNCATE TABLE NOT_EXPLOT_ENV_INFO;
TRUNCATE TABLE not_explot_temps;
ALTER TABLE not_explot_fet ENABLE CONSTRAINT FK_NOT_EXPLOT_FET_TEMPS;